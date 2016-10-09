package nodm.impl

import kable.HashTable
import nodm.EntityManager
import nodm.UniversalID
import java.util.*
import kotlin.reflect.KClass

class DefaultEntityManager : EntityManager {

    private val entities = HashTable<UniversalID, KClass<*>, Any>()
    private val unids = HashMap<Any, UniversalID>()

    override fun get(universalID: UniversalID, klass: KClass<*>): Any? =
            synchronized(this) { entities[universalID, klass] }

    override fun set(universalID: UniversalID, entity: Any): Unit = synchronized(this) {
        unids[entity]?.let { entities.remove(it, entity.javaClass.kotlin) }
        entities.put(universalID, entity.javaClass.kotlin, entity)
        unids[entity] = universalID
    }

    override fun contains(entity: Any): Boolean = synchronized(this) {
        entity in entities
    }

    override fun unidOf(entity: Any): UniversalID? = unids[entity]
}