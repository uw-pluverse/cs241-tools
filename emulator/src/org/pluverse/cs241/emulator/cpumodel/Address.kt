package org.pluverse.cs241.emulator.cpumodel

import kotlin.math.abs

/**
 * Address is a datatype for a 32-bit address. One UInt = 1 byte in address. instance() returns the index in an array.
 * They are represented in multiple of fours and are used to access memory.
 * @param address: UInt
 */
 data class Address(val address: UInt = 0u) {
    /**
     * Gets the memory array index given this address (address / 4)
     */
    private val index: UInt = address / 4u

    /**
     * Return the address as an int
     */
    fun getAddressBits(): Int = address.toInt()

    fun getMemoryIndex(): Int = this()

    // Returns the array index i.e. address / 4
    operator fun invoke(): Int = index.toInt()

    /**
     * Increase number of words (4 bytes)
     *
     * Plus and minus operations we want to consider overflow of address,
     * therefore we use long operations to verify.
     */

    operator fun plus(numOfFourBytes: Int): Address {
        if (numOfFourBytes < 0) return this - abs(numOfFourBytes)

        // Check if the increase will put it fast 0xfffffffff
        val increase = numOfFourBytes.toLong() * 4
        if (increase + address.toLong() > UInt.MAX_VALUE.toLong()) throw OutsideAddressRangeException()

        return Address(address + (numOfFourBytes * 4).toUInt())
    }

    operator fun plus(other: Address): Address {
        return this + other() // Add the index or # of 4 bytes
    }

    operator fun minus(numOfFourBytes: Int): Address {
        if (numOfFourBytes < 0) return this + abs(numOfFourBytes)

        // Check if the decrease will put the address below 0.
        val decrease = numOfFourBytes.toLong() * 4
        if (decrease > address.toLong()) throw OutsideAddressRangeException()

        return Address(address - (numOfFourBytes * 4).toUInt())
    }

    operator fun minus(other: Address): Address {
        return this - other() // Add the index or # of 4 bytes
    }

    /**
     * Operation to input move raw bytes instead of words
     *
     */
    fun shiftBytes(numOfBytes: Int): Address {
        // Assert that we are moving it by a multiple of 4
        if (numOfBytes % 4 != 0) throw InvalidAddressException()

        return this + (numOfBytes / 4)
    }

    fun toHexString(): String {
        return "0x${Integer.toHexString(address.toInt()).padStart(Memory.DOUBLE_WORD_HEX_LENGTH, '0')}"
    }

    fun toBinaryString(): String {
        return address.toString(2).padStart(Int.SIZE_BITS, '0')
    }

    init {
        // Ensure the address is a multiple of 4
        if (address % 4u != 0u) throw InvalidAddressException()
    }
}