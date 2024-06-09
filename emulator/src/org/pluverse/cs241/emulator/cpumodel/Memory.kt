package org.pluverse.cs241.emulator.cpumodel

import kotlin.math.abs

typealias MemIndex = Int

/**
    Memory: an abstract class that holds the data and has simple operations to retrieve and set.

    Address (Class) consumes an address and returns an array index
    - We use the Address class for better semantics in converting address (multiple of 4) to an array index.
        i.e. address() => returns an array index
 */
abstract class Memory<T : MemoryData>(protected val maxSize: Int = DEFAULT_MAX_MEM_SIZE) {

    protected abstract val data: Array<T> // Initialize it to a size of max size

    /**
     * getData returns the data at cpu address
     */
    fun getData(index: Int): T {
        if (index > data.size) throw OutsideMemoryRangeException()

        return data[index]
    }

    fun getData(address: Address): T {
        return getData(address())
    }

    /**
     * [ ] accessors for getting the data. Same as getData(...)
     */
    operator fun get(address: Address): T {
        return getData(address)
    }

    operator fun get(index: Int): T {
        return getData(index)
    }

    /**
    Default values for the class
    */
    companion object {
        // Assign the static variables
        const val DEFAULT_MAX_MEM_SIZE = 20000
        const val DOUBLE_WORD_HEX_LENGTH = 8

        // Correlated classes

        /**
        Address holds info on the address. instance() returns the index
         */
        data class Address(val address: UInt = 0u) {
            private val index: UInt = address / 4u

            /**
             * Return the address as an int
             */
            fun getAddressBits(): Int = address.toInt()

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
                if (increase + address.toLong() > UInt.MAX_VALUE.toLong())
                    throw OutsideAddressRangeException()

                return Address(address + (numOfFourBytes * 4).toUInt())
            }

            operator fun plus(other: Address): Address {
                return this + other() // Add the index or # of 4 bytes
            }

            operator fun minus(numOfFourBytes: Int): Address {
                if (numOfFourBytes < 0) return this + abs(numOfFourBytes)

                // Check if the decrease will put the address below 0
                val decrease = numOfFourBytes.toLong() * 4
                if (decrease > address.toLong())
                    throw OutsideAddressRangeException()

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
                return this + (numOfBytes / 4)
            }


            init {
                // Ensure the address is a multiple of 4
                if (address % 4u != 0u) throw InvalidAddressException()
            }
        }

        @JvmStatic
        fun getAddress(index: Int): Address {
            if (index < 0 || index.toUInt() > UInt.MAX_VALUE / 4u) throw InvalidAddressException()

            return Address(index.toUInt() * 4u)
        }
    }
}

/**
 * This is the Register memory used by the CPU. Note there are 34
 * Index 33 & 34 are HI and LO respectively
 * Note: we are protected from accessing it HI:LO directly because 5 bits are only [0, 31]
 */
class Registers : Memory<RegisterData>(34) {
    override val data: Array<RegisterData> = Array<RegisterData>(maxSize) { index -> RegisterData(index) }

    companion object {
        const val HI_INDEX = 32
        const val LO_INDEX = 33

        const val STACK_POINTER = 30 // Stack pointer
        const val JUMP_REGISTER = 31 // By convention to use this register for return also for specific semantics
    }

}

class RamMemory : Memory<MipsInstructionData> {

    constructor() : super()
    constructor(maxSize: Int) : super(maxSize)

    // Create an array of size maxSize of Mips Instructions
    override val data: Array<MipsInstructionData> = Array<MipsInstructionData>(maxSize)
    { index ->
        MipsInstructionData(address = getAddress(index))
    }

}



