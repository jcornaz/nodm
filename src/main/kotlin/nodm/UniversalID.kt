package nodm

import java.math.BigInteger

data class UniversalID(private val value: BigInteger) {

    constructor(stringValue: String) : this(BigInteger(stringValue, 16))
    constructor(longValue: Long) : this(BigInteger.valueOf(longValue))

    init {
        if (value.bitLength() > 128) throw IllegalArgumentException("Invalid Universal ID")
    }

    override fun toString() = String.format("%032X", value)
}