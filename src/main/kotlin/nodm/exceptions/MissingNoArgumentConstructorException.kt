package nodm.exceptions

import kotlin.reflect.KClass

class MissingNoArgumentConstructorException(klass: KClass<*>) :
        Exception("Missing no-args constructor in class ${klass.qualifiedName}") {
}