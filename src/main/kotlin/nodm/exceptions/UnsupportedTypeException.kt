package nodm.exceptions

class UnsupportedTypeException(type: Class<*>) : Exception("${type.canonicalName} is not supported")