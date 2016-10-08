package nodm

import lotus.domino.Database
import lotus.domino.Session

interface DatabaseSolver {
    operator fun get(session: Session, server: String?, database: String?): Database?

    fun recycle()
}