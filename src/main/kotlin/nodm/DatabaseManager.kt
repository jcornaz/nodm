package nodm

import lotus.domino.Database

interface DatabaseManager {

    operator fun get(name: String): Database?

    fun clear()
}