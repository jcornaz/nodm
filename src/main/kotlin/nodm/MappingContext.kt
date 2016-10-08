package nodm

import lotus.domino.Document
import lotus.domino.Session
import nodm.exceptions.DatabaseNotFound
import java.lang.reflect.Field
import kotlin.reflect.jvm.javaField
import kotlin.reflect.memberProperties

data class MappingContext(val session: Session, val databaseFinder: DatabaseSolver) {

    inline fun <reified T : Any> Document.read(field: Field, instance: T): Unit {
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

    inline fun <reified T : Any> Document.read(allProperties: Boolean): T {
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

    inline operator fun <reified T : Any> get(unid: UniversalID): T? {
        val annotation = T::class.java.notesDocument

        val server = annotation?.server?.orNull()
        val database = annotation?.database?.orNull()

        val db = databaseFinder[session, server, database] ?: throw DatabaseNotFound(server.orEmpty(), database.orEmpty())

        return db[unid]?.read(annotation?.allProperties ?: true)
    }
}