package nodm.adapter

import nodm.Mapper

class IntAdapter(default: Int? = 0) : AbstractAdapter<Int>({ default }) {
    override fun unmarshal(notesValue: Any?, mapper: Mapper): Int? = when (notesValue) {
        is Int -> notesValue
        is Number -> notesValue.toInt()
        else -> notesValue?.toString()?.substringAfterLast(".")?.toInt()
    }
}

class IntListAdapter : ListAdapter<Int>(IntAdapter())

