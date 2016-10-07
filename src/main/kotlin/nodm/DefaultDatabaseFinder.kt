package nodm

import com.google.common.collect.HashBasedTable
import lotus.domino.Database
import lotus.domino.Session

class DefaultDatabaseFinder(val defaultDatabase: Database) : DatabaseSolver {

    val databases = HashBasedTable.create<String, String, Database>().apply {
        put(defaultDatabase.server, defaultDatabase.fileName, defaultDatabase)
    }!!

    override fun get(session: Session, server: String?, database: String?): Database? = synchronized(this) {
        if (server == null || database == null) defaultDatabase
        else databases[server, database]
                ?: session.getDatabase(server, database, false)
                ?.apply { databases.put(server, database, this) }
    }

    override fun recycle() = synchronized(this) {
        databases.values().forEach { it.safeRecycle() }
        databases.clear()
    }
}