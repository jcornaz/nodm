package nodm.adapter

import nodm.Mapper

class NullableDoubleAdapter : AbstractNullableAdapter<Double>() {
    override fun unmarshal(notesValue: Any?, mapper: Mapper): Double? = when (notesValue) {
        is Double -> notesValue
        is Number -> notesValue.toDouble()
        else -> notesValue.toString().substringAfterLast(".").toDouble()
    }
}

class DoubleAdapter : AbstractNotNullAdapter<Int>(NullableIntAdapter(), 0)

class DoubleListAdapter : ListAdapter<Int>(IntAdapter())