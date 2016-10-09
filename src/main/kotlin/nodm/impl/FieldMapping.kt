package nodm.impl

import nodm.TypeAdapter
import java.lang.reflect.Field

data class FieldMapping(val name: String, val field: Field, val adapter: TypeAdapter<*>)