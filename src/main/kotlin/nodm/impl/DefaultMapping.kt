package nodm.impl

import lotus.domino.Document
import nodm.*
import nodm.adapter.IntAdapter
import nodm.adapter.StringAdapter
import nodm.exceptions.MissingNoArgumentConstructorException
import nodm.exceptions.UnsupportedTypeException
import nodm.utils.force
import nodm.utils.unid
import java.lang.reflect.Field
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.jvm.javaField
import kotlin.reflect.memberProperties

class DefaultMapping<T : Any>(klass: KClass<T>) : Mapping<T> {

    val fields: Collection<FieldMapping> = klass.memberProperties
            .map { it.javaField }
            .filterNotNull()
            .filterNotesItems(klass.notesDocument?.allProperties ?: true)
            .map { field ->
                val name = field.notesItem?.name ?: field.name

                val adapterClass = field.getAnnotation(NotesTypeAdapter::class.java)?.value ?: when {
                    field.type.isAssignableFrom(Int::class.java) -> IntAdapter::class
                    field.type.isAssignableFrom(String::class.java) -> StringAdapter::class
                    else -> throw UnsupportedTypeException(field.type)
                }

                val adapter = adapterClass.constructors.firstOrNull { it.parameters.isEmpty() }?.call()
                        ?: throw MissingNoArgumentConstructorException(adapterClass)

                FieldMapping(name, field, adapter)
            }

    val idField: Field? = klass.notesID?.apply {
        if (!type.isAssignableFrom(String::class.java) && !type.isAssignableFrom(UniversalID::class.java))
            throw IllegalArgumentException("${NotesUniversalID::class.qualifiedName} must be a String or a ${UniversalID::class.qualifiedName}")
    }

    override fun unidOf(instance: T): UniversalID? = idField?.force { it[instance] }.let {
        when (it) {
            is UniversalID -> it
            is String -> UniversalID(it)
            else -> null
        }
    }

    override fun read(document: Document, instance: T, mapper: Mapper) {

        for ((name, field, adapter) in fields) {
            val notesValues: List<Any?> = document.getItemValue(name) ?: emptyList()

            field.force { it[instance] = adapter.unmarshal(notesValues, mapper) }
        }

        updateInstanceID(document, instance)
    }

    override fun write(instance: T, document: Document, force: Boolean, createConflict: Boolean) {

        for ((name, field, adapter) in fields) {
            field
                    .force { it[instance] }
                    .let { adapter.marshal(it) }
                    .let { document.replaceItemValue(name, Vector(it.filterNotNull())) }
        }

        document.save(force, createConflict)
        updateInstanceID(document, instance)
    }

    fun updateInstanceID(document: Document, instance: T) {
        idField?.force { it[instance] = if (it.type.isAssignableFrom(String::class.java)) document.universalID else document.unid }
    }
}