package nodm.adapter

import nodm.Mapper


class StringAdapter(default: String? = "") : AbstractAdapter<String>({ default }) {
    override fun unmarshal(notesValue: Any?, mapper: Mapper): String? = notesValue?.toString()
}

class StringListAdapter : ListAdapter<Int>(IntAdapter())