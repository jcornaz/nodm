package nodm

import kotlin.reflect.KClass

interface MappingFactory {
    fun <T : Any> createMapping(klass: KClass<T>): Mapping<T>
}