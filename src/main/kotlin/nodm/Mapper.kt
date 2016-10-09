package nodm

import kotlin.reflect.KClass

interface Mapper {
    fun <T : Any> get(unid: UniversalID, klass: KClass<T>): T?
    fun <T : Any> save(entity: T, force: Boolean = false)
}