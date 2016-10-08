package nodm

import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.jvm.javaField
import kotlin.reflect.memberProperties

@Target(AnnotationTarget.CLASS)
annotation class NotesDocument(
        val dbName: String = "",
        val allProperties: Boolean = true
)

@Target(AnnotationTarget.FIELD)
annotation class NotesTransient

@Target(AnnotationTarget.FIELD)
annotation class NotesItem(val name: String = "")

@Target(AnnotationTarget.FIELD)
annotation class NotesUniversalID

val Field.isNotesID: Boolean
    get() = isAnnotationPresent(NotesUniversalID::class.java)

val Field.isNotesTransient: Boolean
    get() = isAnnotationPresent(NotesTransient::class.java)

val Field.isNotesItem: Boolean
    get() = !isNotesID && !isNotesTransient && isAnnotationPresent(NotesItem::class.java)

val Field.notesItem: NotesItem?
    get() = getAnnotation(NotesItem::class.java)

val KClass<*>.notesID: Field?
    get() = memberProperties.firstOrNull { it.javaField?.isNotesID ?: false }?.javaField

val Class<*>.notesID: Field?
    get() = kotlin.notesID

val KClass<*>.notesDocument: NotesDocument?
    get() = java.notesDocument

val Class<*>.notesDocument: NotesDocument?
    get() = getAnnotation(NotesDocument::class.java)

val KClass<*>.dbName: String
    get() = java.dbName

val Class<*>.dbName: String
    get() = getAnnotation(NotesDocument::class.java)?.dbName.orEmpty()

fun Collection<Field>.filterNotesItems(all: Boolean): Collection<Field> =
        if (all) filter { !it.isNotesTransient && !it.isNotesID } else filter { it.isNotesItem }