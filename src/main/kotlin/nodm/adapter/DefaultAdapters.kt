package nodm.adapter

import nodm.TypeAdapter
import nodm.UniversalID
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.defaultType
import kotlin.reflect.jvm.javaType

object DefaultAdapters {

    operator fun get(type: Type): TypeAdapter<*>? {
        val isNullable = type.javaClass.kotlin.defaultType.isMarkedNullable

        return when (type) {

            Int::class.defaultType.javaType -> IntAdapter(if (isNullable) null else 0)
            Double::class.defaultType.javaType -> DoubleAdapter(if (isNullable) null else 0.0)
            String::class.defaultType.javaType -> StringAdapter(if (isNullable) null else "")
            UniversalID::class.defaultType.javaType -> UniversalIDAdapter(if (isNullable) null else UniversalID(0))

            else -> if (type.javaClass.isAssignableFrom(List::class.java)) {
                (type.javaClass as? ParameterizedType)?.actualTypeArguments?.firstOrNull()?.let { get(it) }?.let { ListAdapter(it) }
            } else null
        }
    }
}