package nodm

import java.math.BigInteger

data class UniversalID private constructor(private val value: BigInteger) {

    constructor(string: String) : this(BigInteger(string, 16).apply {
        if (bitLength() > 128) throw IllegalArgumentException("Invalid Universal ID")
    })

    override fun toString() = String.format("%032X", value)
}