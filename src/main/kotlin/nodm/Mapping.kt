package nodm

import lotus.domino.Document


interface Mapping<in T> {
    fun unidOf(instance: T): UniversalID?
    fun read(document: Document, instance: T, mapper: Mapper)
    fun write(instance: T, document: Document, force: Boolean = false, createConflict: Boolean = false)
}