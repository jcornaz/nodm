package nodm.impl

import com.google.common.collect.HashBasedTable
import nodm.EntityManager
import nodm.UniversalID
import java.util.*
import kotlin.reflect.KClass

class DefaultEntityManager : EntityManager {

    private val entities = HashBasedTable.create<UniversalID, KClass<*>, Any>()!!
    private val unids = HashMap<Any, UniversalID>()

    override fun get(universalID: UniversalID, klass: KClass<*>): Any? =
            synchronized(this) { entities[universalID, klass] }

    override fun set(universalID: UniversalID, entity: Any): Unit = synchronized(this) {
        entities.put(universalID, entity.javaClass.kotlin, entity)
    }

    override fun contains(entity: Any): Boolean = synchronized(this) {
        entities.containsValue(entity)
    }

    override fun unidOf(entity: Any): UniversalID? = unids[entity]
}