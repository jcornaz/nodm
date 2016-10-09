package nodm.adapter

import nodm.Mapper
import nodm.TypeAdapter
import java.util.*

abstract class AbstractNullableAdapter<T> : TypeAdapter<T?> {

    abstract fun unmarshal(notesValue: Any?, mapper: Mapper): T?

    override fun unmarshal(notesValue: List<Any?>, mapper: Mapper) = unmarshal(notesValue.firstOrNull(), mapper)
    override fun marshal(value: T?) = listOf(value)
}

abstract class AbstractNotNullAdapter<T : Any>(val nullableAdapter: TypeAdapter<T?>, val default: () -> T) : TypeAdapter<T> {

    constructor(nullableAdapter: TypeAdapter<T?>, default: T) : this(nullableAdapter, { default })

    override fun unmarshal(notesValue: List<Any?>, mapper: Mapper) = nullableAdapter.unmarshal(notesValue, mapper) ?: default()
    override fun marshal(value: T) = nullableAdapter.marshal(value)
}

open class ListAdapter<T>(val valueAdapter: TypeAdapter<T>) : TypeAdapter<List<T>> {

    override fun unmarshal(notesValue: List<Any?>, mapper: Mapper): List<T> = notesValue.map { valueAdapter.unmarshal(listOf(it), mapper) }

    override fun marshal(value: List<T>) = value.flatMap { valueAdapter.marshal(it) }.filterNotNull().let { Vector(it) }
}