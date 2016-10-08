package nodm

import lotus.domino.Database

interface DatabaseManager {

    operator fun get(server: String?, database: String?): Database?

    fun clear()
}