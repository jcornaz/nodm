package nodm

import java.lang.reflect.Field
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class NotesDocument(
        val server: String = "",
        val database: String = "",
        val allProperties: Boolean = true
)

@Target(AnnotationTarget.FIELD)
annotation class NotesTransientItem

@Target(AnnotationTarget.FIELD)
annotation class NotesItem(val name: String = "")

val Field.isNotNotesTransient: Boolean
    get() = annotations.none { it is NotesTransientItem }

val Field.isNotesItem: Boolean
    get() = annotations.any { it is NotesItem }

val Field.notesItem: NotesItem?
    get() = getAnnotation(NotesItem::class.java)

val KClass<*>.notesDocument: NotesDocument?
    get() = java.getAnnotation(NotesDocument::class.java)

fun Iterable<Field>.filterNotesItems(all: Boolean): Collection<Field> =
        if (all) filter { it.isNotNotesTransient } else filter { it.isNotesItem }