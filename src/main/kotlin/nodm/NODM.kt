package nodm

import lotus.domino.Database
import nodm.impl.DefaultDatabaseManager
import nodm.impl.DefaultEntityManager
import nodm.impl.DefaultMapper
import nodm.impl.DefaultMapping
import nodm.utils.mutableLazy
import kotlin.reflect.KClass


object NODM {

    var databaseManager: DatabaseManager? = null

    var entityManager: EntityManager by mutableLazy { DefaultEntityManager() }

    var mappingFactory: MappingFactory = object : MappingFactory {
        override fun <T : Any> createMapping(klass: KClass<T>): Mapping<T> = DefaultMapping(klass)
    }

    val mapperFactory: MapperFactory = object : MapperFactory {
        override fun createMapper(databaseManager: DatabaseManager, entityManager: EntityManager, mappingFactory: MappingFactory): Mapper =
                DefaultMapper(databaseManager, entityManager, mappingFactory)
    }

    val isConnected: Boolean
        get() = databaseManager != null

    fun connect(database: Database) {
        databaseManager?.clear()
        databaseManager = DefaultDatabaseManager(database)
        DefaultDatabaseManager(database)
    }

    fun mapper(): Mapper = databaseManager.let { dbm ->
        if (dbm == null) throw IllegalStateException("No database manager")
        else mapperFactory.createMapper(dbm, entityManager, mappingFactory)
    }

    inline operator fun <reified T : Any> get(unid: UniversalID): T? = get(unid, T::class)
    inline operator fun <reified T : Any> get(unid: String): T? = get(unid, T::class)
    operator fun <T : Any> get(unid: UniversalID, klass: KClass<T>): T? = mapper().get(unid, klass)
    operator fun <T : Any> get(unid: String, klass: KClass<T>): T? = get(UniversalID(unid), klass)
    operator fun <T : Any> get(unid: UniversalID, klass: Class<T>): T? = get(unid, klass.kotlin)
    operator fun <T : Any> get(unid: String, klass: Class<T>): T? = get(UniversalID(unid), klass.kotlin)

    fun save(entity: Any, force: Boolean = false): Unit = mapper().save(entity, force)
}