package nodm.adapter

import nodm.Mapper


class NullableStringAdapter : AbstractNullableAdapter<String?>() {
    override fun unmarshal(notesValue: Any?, mapper: Mapper): String? = notesValue?.toString()
}

class StringAdapter : AbstractNotNullAdapter<String>(NullableStringAdapter(), "")

class StringListAdapter : ListAdapter<Int>(IntAdapter())