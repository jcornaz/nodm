package nodm.utils

import java.lang.reflect.Field
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> avoidExceptions(operation: () -> T) =
        try {
            operation()
        } catch (e: Exception) {
            null
        }

fun String?.orNull() = if (this?.trim()?.isEmpty() ?: true) null else this

fun <T> mutableLazy(initializer: () -> T) = object : ReadWriteProperty<Any, T> {
    var value: T? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): T = value ?: initializer()

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.value = value
    }
}

fun <T> Field.force(operation: (Field) -> T): T? {
    val wasAccessible = isAccessible
    isAccessible = true

    return try {
        operation(this)
    } finally {
        isAccessible = wasAccessible
    }
}