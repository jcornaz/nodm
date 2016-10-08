package nodm.utils

import lotus.domino.Base
import lotus.domino.Database
import nodm.UniversalID
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> avoidExceptions(operation: () -> T) =
        try {
            operation()
        } catch (e: Exception) {
            null
        }

fun String.orNull() = if (trim().isEmpty()) null else this



fun <T> mutableLazy(initializer: () -> T) = object : ReadWriteProperty<Any, T> {
    var value: T? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): T = value ?: initializer()

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        this.value = value
    }
}