package nodm.adapter

import nodm.Mapper

class NullableIntAdapter : AbstractNullableAdapter<Int>() {
    override fun unmarshal(notesValue: Any?, mapper: Mapper): Int? = when (notesValue) {
        is Int -> notesValue
        is Number -> notesValue.toInt()
        else -> notesValue.toString().substringAfterLast(".").toInt()
    }
}

class IntAdapter : AbstractNotNullAdapter<Int>(NullableIntAdapter(), 0)

class IntListAdapter : ListAdapter<Int>(IntAdapter())