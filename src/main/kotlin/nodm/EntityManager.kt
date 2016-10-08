package nodm

import kotlin.reflect.KClass

interface EntityManager {
    operator fun get(universalID: UniversalID, klass: KClass<*>): Any?
    operator fun set(universalID: UniversalID, entity: Any)

    operator fun contains(entity: Any): Boolean

    fun unidOf(entity: Any): UniversalID?
}