package nodm.adapter

import nodm.Mapper

class DoubleAdapter(default: Double? = 0.0) : AbstractAdapter<Double>({ default }) {
    override fun unmarshal(notesValue: Any?, mapper: Mapper): Double? = when (notesValue) {
        is Double -> notesValue
        is Number -> notesValue.toDouble()
        else -> notesValue.toString().substringAfterLast(".").toDouble()
    }
}

class DoubleListAdapter : ListAdapter<Int>(IntAdapter())