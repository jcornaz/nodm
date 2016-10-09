package nodm.impl

import lotus.domino.Document
import nodm.*
import nodm.adapter.DefaultAdapters
import nodm.exceptions.MissingNoArgumentConstructorException
import nodm.exceptions.UnsupportedTypeException
import nodm.utils.force
import nodm.utils.unid
import java.lang.reflect.Field
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.jvm.javaField
import kotlin.reflect.memberProperties

class DefaultMapping<out T : Any>(klass: KClass<T>) : Mapping<T> {

    val fields: Collection<FieldMapping> by lazy {
        klass.memberProperties
                .map { it.javaField }
                .filterNotNull()
                .filterNotesItems(klass.notesDocument?.allProperties ?: true)
                .map { field ->
                    val name = field.notesItem?.name ?: field.name

                    val adapterClass = field.getAnnotation(NotesTypeAdapter::class.java)?.value

                    val adapter = adapterClass?.let {
                        it.constructors.firstOrNull { it.parameters.isEmpty() }?.call()
                                ?: throw MissingNoArgumentConstructorException(adapterClass)
                    } ?: DefaultAdapters[field.type] ?: throw UnsupportedTypeException(field.type)

                    FieldMapping(name, field, adapter)
                }
    }

    val idField: Field? by lazy {
        klass.notesID?.apply {
            if (!type.isAssignableFrom(String::class.java) && !type.isAssignableFrom(UniversalID::class.java))
                throw IllegalArgumentException("${NotesUniversalID::class.qualifiedName} must be a String or a ${UniversalID::class.qualifiedName}")
        }
    }

    override fun unidOf(instance: @UnsafeVariance T): UniversalID? = idField?.force { it[instance] }.let {
        when (it) {
            is UniversalID -> it
            is String -> UniversalID(it)
            else -> null
        }
    }

    override fun read(document: Document, instance: @UnsafeVariance  T, mapper: Mapper) {

        for ((name, field, adapter) in fields) {
            val notesValues: List<Any?> = document.getItemValue(name) ?: emptyList()

            field.force { it[instance] = adapter.unmarshal(notesValues, mapper) }
        }

        updateInstanceID(document, instance)
    }

    override fun write(instance: @UnsafeVariance  T, document: Document, force: Boolean) {

        for ((name, field, adapter) in fields) {
            field
                    .force { it[instance] }
                    .let { adapter.marshal(it) }
                    .let { document.replaceItemValue(name, Vector(it.filterNotNull())) }
        }

        document.save(force)
        updateInstanceID(document, instance)
    }

    fun updateInstanceID(document: Document, instance: @UnsafeVariance T) {
        idField?.force { it[instance] = if (it.type.isAssignableFrom(String::class.java)) document.universalID else document.unid }
    }
}