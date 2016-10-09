package nodm.adapter

import nodm.Mapper
import nodm.TypeAdapter
import java.util.*

abstract class AbstractAdapter<T>(val default: () -> T?) : TypeAdapter<T?> {

    abstract fun unmarshal(notesValue: Any?, mapper: Mapper): T?

    override fun unmarshal(notesValue: List<Any?>, mapper: Mapper) = unmarshal(notesValue.firstOrNull(), mapper) ?: default()
    override fun marshal(value: T?) = listOf(value)
}

open class ListAdapter<T : Any>(val valueAdapter: TypeAdapter<T?>) : TypeAdapter<List<T>> {

    override fun unmarshal(notesValue: List<Any?>, mapper: Mapper): List<T> = notesValue.map { valueAdapter.unmarshal(listOf(it), mapper) }.filterNotNull()

    override fun marshal(value: List<T>) = value.flatMap { valueAdapter.marshal(it) }.filterNotNull().let { Vector(it) }
}