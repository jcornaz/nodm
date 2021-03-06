package nodm.impl

import nodm.*
import nodm.exceptions.DatabaseNotFound
import nodm.exceptions.MissingNoArgumentConstructorException
import nodm.utils.get
import nodm.utils.use
import java.util.*
import kotlin.reflect.KClass

data class DefaultMapper(
        val databaseManager: DatabaseManager,
        val entityManager: EntityManager,
        val mappingFactory: MappingFactory
) : Mapper {

    private val mappings = HashMap<KClass<*>, Mapping<*>>()

    @Suppress("UNCHECKED_CAST")
    @Throws(DatabaseNotFound::class)
    override fun <T : Any> get(unid: UniversalID, klass: KClass<T>): T? =
            (entityManager[unid, klass] as? T) ?: let {
                val db = databaseManager[klass.dbName] ?: throw DatabaseNotFound(klass.dbName)

                db[unid]?.use { doc ->

                    val entity = klass.constructors.firstOrNull { it.parameters.isEmpty() }?.call() ?: throw MissingNoArgumentConstructorException(klass)
                    val mapping = mappings.getOrPut(klass) { mappingFactory.createMapping(klass) }
                    mapping.read(doc, entity, this)

                    entity
                }
            }


    @Throws(DatabaseNotFound::class)
    inline operator fun <reified T : Any> get(unid: UniversalID): T? = get(unid, T::class)

    @Throws(DatabaseNotFound::class)
    override fun <T : Any> save(entity: T, force: Boolean) {

        val klass = entity.javaClass.kotlin
        val mapping = mappings.getOrPut(klass) { mappingFactory.createMapping(klass) }
        val unid = mapping.unidOf(entity) ?: entityManager.unidOf(entity)
        val database = databaseManager[klass.dbName] ?: throw DatabaseNotFound(klass.dbName)
        val document = unid?.let { database[it] } ?: database.createDocument()

        mapping.write(entity, document, force)
    }
}