package nodm.utils

import nodm.exceptions.UnsupportedTypeException
import java.lang.reflect.ParameterizedType

@Suppress("UNCHECKED_CAST")
fun <T : Any> Any?.convertTo(type: Class<T>): T? = try {
    when {

        type.isAssignableFrom(String::class.java) -> this?.toString()  as? T
        type.isAssignableFrom(Char::class.java) -> this?.toString()?.firstOrNull() as? T
        type.isAssignableFrom(Short::class.java) -> this?.toString()?.substringBefore(".")?.toShort() as? T
        type.isAssignableFrom(Int::class.java) -> this?.toString()?.substringBefore(".")?.toInt() as? T
        type.isAssignableFrom(Long::class.java) -> this?.toString()?.substringBefore(".")?.toLong() as? T
        type.isAssignableFrom(Float::class.java) -> this?.toString()?.toFloat() as? T
        type.isAssignableFrom(Double::class.java) -> this?.toString()?.toDouble() as? T

        else -> throw UnsupportedTypeException(type)
    }
} catch (e: UnsupportedTypeException) {
    throw e
} catch (e: Exception) {
    null
}

val Class<*>.firstTypeArgument: Class<*>
    get() = (this as? ParameterizedType)?.actualTypeArguments?.firstOrNull()?.let { it as? Class<*> } ?: Any::class.java
