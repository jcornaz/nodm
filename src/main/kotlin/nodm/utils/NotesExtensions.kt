package nodm.utils

import lotus.domino.Base
import lotus.domino.Database
import lotus.domino.Document
import nodm.UniversalID


fun Base.safeRecycle() = avoidExceptions { recycle() }
fun <T : Base, R> T.use(operation: (T) -> R): R =
        try {
            operation(this)
        } finally {
            safeRecycle()
        }

operator fun Database.get(unid: String) = avoidExceptions { getDocumentByUNID(unid) }
operator fun Database.get(unid: UniversalID) = get(unid.toString())

val Document.unid: UniversalID
    get() = UniversalID(universalID)