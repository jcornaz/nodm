package nodm

import lotus.domino.Document
import lotus.domino.Session
import nodm.exceptions.DatabaseNotFound
import kotlin.reflect.KClass

data class MappingContext(val session: Session, val databaseFinder: DatabaseSolver) {

    fun <T : Any> Document.unmarshalTo(klass: KClass<T>): T {
        val constructor = klass.constructors.firstOrNull { it.parameters.isEmpty() } ?: throw IllegalArgumentException("The class \"${klass.qualifiedName}\" does not have a no-arg constructor")
        return constructor.call()
    }

    operator fun <T : Any> get(unid: UniversalID, klass: KClass<T>): T? {
        val annotation = klass.annotations.firstOrNull { it is NotesDocument } as? NotesDocument

        val server = annotation?.server?.orNull()
        val database = annotation?.server?.orNull()

        val db = databaseFinder[session, server, database] ?: throw DatabaseNotFound(server.orEmpty(), database.orEmpty())

        return db[unid]?.unmarshalTo(klass)
    }
}