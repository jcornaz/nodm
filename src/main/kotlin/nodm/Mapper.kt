package nodm

import kotlin.reflect.KClass

interface Mapper {
    fun <T : Any> get(unid: UniversalID, klass: KClass<T>): T?
}