package nodm.impl

import lotus.domino.Database
import lotus.domino.Session
import nodm.DatabaseManager
import nodm.utils.avoidExceptions
import nodm.utils.safeRecycle
import java.util.*

class DefaultDatabaseManager(
        val findDatabase: (String) -> Database?,
        val releaseDatabase: (Database) -> Unit = { it.safeRecycle() }
) : DatabaseManager {

    constructor(session: Session, defaultDatabase: Database? = null) : this({ name ->
        avoidExceptions { session.getDatabase("", name, false) } ?: defaultDatabase
    })

    constructor(defaultDatabase: Database) : this(defaultDatabase.parent, defaultDatabase)

    val databases = HashMap<String, Database>()

    override fun get(name: String): Database? = synchronized(this) {
        databases[name] ?: findDatabase(name)?.apply { databases[name] = this }
    }

    override fun clear() = synchronized(this) {
        databases.values.forEach(releaseDatabase)
        databases.clear()
    }
}