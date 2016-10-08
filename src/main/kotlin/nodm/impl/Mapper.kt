package nodm.impl

import nodm.*
import nodm.utils.*
import java.lang.reflect.Field
import kotlin.reflect.jvm.javaField
import kotlin.reflect.memberProperties

data class Mapper(val databaseManager: DatabaseManager, val entityManager: EntityManager) {

    inline fun <reified T : Any> lotus.domino.Document.read(field: Field, instance: T): Unit {
        val fieldName = field.notesItem?.name?.orNull() ?: field.name
        val notesValues: List<Any?> = getItemValue(fieldName) ?: emptyList()

        val wasAccessible = field.isAccessible
        field.isAccessible = true

        field[instance] = if (Collection::class.java.isAssignableFrom(field.type)) {
            val list = field.type.firstTypeArgument.let { type -> notesValues.map { it.convertTo(type) } }

            if (field.type.isAssignableFrom(Set::class.java)) list.toSet()
            else list

        } else notesValues.firstOrNull().convertTo(field.type)

        field.isAccessible = wasAccessible
    }

    inline fun <reified T : Any> lotus.domino.Document.read(allProperties: Boolean): T {
        val constructor = T::class.constructors.firstOrNull { it.parameters.isEmpty() } ?: throw IllegalArgumentException("The class \"${T::class.qualifiedName}\" does not have a no-arg constructor")
        val instance = constructor.call()

        println(T::class.java)

        T::class.memberProperties
                .map { it.javaField }
                .filterNotNull()
                .filterNotesItems(allProperties)
                .forEach { read(it, instance) }

        return instance
    }

    inline operator fun <reified T : Any> get(unid: UniversalID): T? =
            (entityManager[unid, T::class] as? T) ?: let {
                val annotation = T::class.notesDocument

                val server = annotation?.server?.orNull()
                val database = annotation?.database?.orNull()

                val db = databaseManager[server, database] ?: throw nodm.exceptions.DatabaseNotFound(server.orEmpty(), database.orEmpty())

                db[unid]?.use {
                    println("start")
                    val result = it.read<T>(annotation?.allProperties ?: true)
                    entityManager[unid] = result
                    return@use result
                }
            }
}