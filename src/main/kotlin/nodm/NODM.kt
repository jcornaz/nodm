package nodm

import lotus.domino.Database
import lotus.domino.Session
import kotlin.reflect.KClass

object NODM {

    var context: MappingContext? = null

    val isConnected: Boolean
        get() = context != null

    fun connect(database: Database) =
            connect(database.parent, DefaultDatabaseFinder(database))

    fun connect(session: Session, databaseFinder: DefaultDatabaseFinder) {
        disconnect(false)
        context = MappingContext(session, databaseFinder)
    }

    fun disconnect(recycleDatabases: Boolean = true) {
        if (recycleDatabases) context?.databaseFinder?.recycle()
        context = null
    }

    inline operator fun <reified T : Any> get(unid: UniversalID): T? = with(context) {
        if (this == null) throw IllegalStateException("Not connected")
        else get<T>(unid)
    }

    inline operator fun <reified T : Any> get(unid: String): T? = get(unid, T::class.java)
    inline operator fun <reified T : Any> get(unid: UniversalID, klass: Class<T>): T? = get(unid)
    inline operator fun <reified T : Any> get(unid: String, klass: Class<T>): T? = get(UniversalID(unid), klass)
    inline operator fun <reified T : Any> get(unid: UniversalID, klass: KClass<T>): T? = get(unid, klass.java)
    inline operator fun <reified T : Any> get(unid: String, klass: KClass<T>): T? = get(UniversalID(unid), klass.java)

}