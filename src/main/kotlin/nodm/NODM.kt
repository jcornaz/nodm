package nodm

import lotus.domino.Database
import lotus.domino.Session
import kotlin.reflect.KClass

interface DatabaseSolver {
    operator fun get(session: Session, server: String?, database: String?): Database?

    fun recycle()
}

object NODM {

    var context: MappingContext? = null

    fun connect(database: Database) {
        disconnect(false)
        context = MappingContext(database.parent, DefaultDatabaseFinder(database))
    }

    fun disconnect(recycleDatabases: Boolean = true) {
        if (recycleDatabases) context?.databaseFinder?.recycle()
        context = null
    }

    operator fun <T : Any> get(unid: UniversalID, klass: KClass<T>): T? = context.let {
        if (it == null) throw IllegalStateException("Not connected")
        else it[unid, klass]
    }

    operator fun <T : Any> get(unid: String, klass: KClass<T>): T? = get(UniversalID(unid), klass)
}