package nodm

import lotus.domino.Database
import nodm.impl.DefaultDatabaseManager
import nodm.impl.DefaultEntityManager
import nodm.impl.Mapper
import nodm.utils.mutableLazy
import kotlin.reflect.KClass


object NODM {

    var databaseManager: DatabaseManager? = null

    var entityManager: EntityManager by mutableLazy { DefaultEntityManager() }

    val isConnected: Boolean
        get() = databaseManager != null

    fun connect(database: Database) {
        databaseManager?.clear()
        databaseManager = DefaultDatabaseManager(database)
        DefaultDatabaseManager(database)
    }

    fun mapper(): Mapper = databaseManager.let { dbm ->
        if (dbm == null) throw IllegalStateException("No database manager")
        else Mapper(dbm, entityManager)
    }

    inline operator fun <reified T : Any> get(unid: UniversalID): T? = mapper().get<T>(unid)

    inline operator fun <reified T : Any> get(unid: String): T? = get(unid, T::class.java)
    inline operator fun <reified T : Any> get(unid: UniversalID, klass: Class<T>): T? = get(unid)
    inline operator fun <reified T : Any> get(unid: String, klass: Class<T>): T? = get(UniversalID(unid), klass)
    inline operator fun <reified T : Any> get(unid: UniversalID, klass: KClass<T>): T? = get(unid, klass.java)
    inline operator fun <reified T : Any> get(unid: String, klass: KClass<T>): T? = get(UniversalID(unid), klass.java)

    fun save(entity: Any): Unit = mapper().save(entity)
}