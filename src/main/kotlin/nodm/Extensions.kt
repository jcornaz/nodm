package nodm

import lotus.domino.Base
import lotus.domino.Database

fun Base.safeRecycle() = avoidExceptions { recycle() }

fun <T> avoidExceptions(operation: () -> T) =
        try {
            operation()
        } catch (e: Exception) {
            null
        }

fun String.orNull() = if (trim().isEmpty()) null else this

fun Database.get(unid: String) = avoidExceptions { getDocumentByUNID(unid) }
operator fun Database.get(unid: UniversalID) = get(unid.toString())

