package nodm.impl

import com.google.common.collect.HashBasedTable
import lotus.domino.Database
import lotus.domino.Session
import nodm.DatabaseManager
import nodm.utils.safeRecycle

class DefaultDatabaseManager(
        val defaultDatabase: Database? = null,
        val findDatabase: (String, String) -> Database?
) : DatabaseManager {

    constructor(session: Session, defaultDatabase: Database? = null) : this(defaultDatabase, { server, db -> session.getDatabase(server, db, false) })
    constructor(defaultDatabase: Database) : this(defaultDatabase.parent, defaultDatabase)

    val databases = HashBasedTable.create<String, String, Database>().apply {
        defaultDatabase?.let { put(defaultDatabase.server, defaultDatabase.fileName, it) }
    }!!

    override fun get(server: String?, database: String?): Database? = synchronized(this) {
        if (server == null || database == null) defaultDatabase
        else databases[server, database]
                ?: findDatabase(server, database)?.apply { databases.put(server, database, this) }
                ?: defaultDatabase
    }

    override fun clear() = synchronized(this) {
        databases.values().forEach { it.safeRecycle() }
        databases.clear()
    }
}