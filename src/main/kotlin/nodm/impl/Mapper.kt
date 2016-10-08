package nodm.impl

import lotus.domino.Document
import nodm.*
import nodm.exceptions.DatabaseNotFound
import nodm.utils.*
import java.lang.reflect.Field
import kotlin.reflect.jvm.javaField
import kotlin.reflect.memberProperties

data class Mapper(val databaseManager: DatabaseManager, val entityManager: EntityManager) {

    fun Any.write(document: Document): Unit = TODO()

    var Any.unid: UniversalID?
        get() = javaClass.notesID?.force {
            if (String::class.java.isAssignableFrom(it.type)) UniversalID(it[this] as String)
            else it[this] as? UniversalID
        } ?: entityManager.unidOf(this)
        set(unid) {
            if (unid != null) {
                entityManager[unid] = this

                javaClass.notesID?.force {
                    it[this] = if (it.type.isAssignableFrom(String::class.java)) unid.toString() else unid
                }
            }
        }

    var Any.document: Document
        get() = databaseManager[javaClass.dbName].let { db -> unid?.let { db[it] } ?: db.createDocument() }
        set(document) {
            unid = document.unid
        }

    inline fun <reified T : Any> Document.read(field: Field, instance: T): Unit {
        val fieldName = field.notesItem?.name?.orNull() ?: field.name
        val notesValues: List<Any?> = getItemValue(fieldName) ?: emptyList()

        val value = if (Collection::class.java.isAssignableFrom(field.type)) {
            val list = field.type.firstTypeArgument.let { type -> notesValues.map { it.convertTo(type) } }

            if (field.type.isAssignableFrom(Set::class.java)) list.toSet()
            else list

        } else notesValues.firstOrNull().convertTo(field.type)

        field.force { it[instance] = value }
    }

    inline fun <reified T : Any> Document.read(allProperties: Boolean): T {
        val constructor = T::class.constructors.firstOrNull { it.parameters.isEmpty() }
                ?: throw IllegalArgumentException("The class \"${T::class.qualifiedName}\" does not have a no-arg constructor")

        val instance = constructor.call()

        T::class.memberProperties
                .map { it.javaField }
                .filterNotNull()
                .filterNotesItems(allProperties)
                .forEach { read(it, instance) }

        return instance
    }

    @Throws(DatabaseNotFound::class)
    inline operator fun <reified T : Any> get(unid: UniversalID): T? =
            (entityManager[unid, T::class] as? T) ?: let {
                val annotation = T::class.notesDocument

                val db = databaseManager[T::class.dbName]

                db[unid]?.use { doc ->
                    val result = doc.read<T>(annotation?.allProperties ?: true)
                    result.document = doc
                    return@use result
                }
            }

    @Throws(DatabaseNotFound::class)
    fun save(entity: Any, force: Boolean = false, makeResponse: Boolean = false) {

        val doc = entity.document
        entity.write(doc)

        doc.save(force, makeResponse)

        entity.document = doc
    }
}