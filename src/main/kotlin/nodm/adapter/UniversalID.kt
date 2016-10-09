package nodm.adapter

import nodm.Mapper
import nodm.UniversalID

class UniversalIDAdapter(default: UniversalID? = UniversalID(0)) : AbstractAdapter<UniversalID>({ default }) {
    override fun unmarshal(notesValue: Any?, mapper: Mapper): UniversalID? = notesValue?.toString()?.let(::UniversalID)
}

class UniversalIDListAdapter : ListAdapter<UniversalID>(UniversalIDAdapter())