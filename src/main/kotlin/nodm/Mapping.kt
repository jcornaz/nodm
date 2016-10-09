package nodm

import lotus.domino.Document


interface Mapping<out T> {
    fun unidOf(instance: @UnsafeVariance T): UniversalID?
    fun read(document: Document, instance: @UnsafeVariance T, mapper: Mapper)
    fun write(instance: @UnsafeVariance  T, document: Document, force: Boolean = false)
}