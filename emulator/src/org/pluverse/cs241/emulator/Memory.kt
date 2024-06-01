package org.pluverse.cs241.emulator

import kotlin.collections.HashMap

typealias MemIndex = Int

/*
    Memory: an abstract class that holds the data and has simple operations to retrieve and set.

    Address (Class) consumes an address and returns an array index
    - We use the Address class for better semantics in converting address (multiple of 4) to an array index.
        i.e. address() => returns an array index
 */
abstract class Memory<T: MemoryData>(protected val maxSize: Int = DEFAULT_MAX_MEM_SIZE) {

    protected abstract val data: Array<T> // Initialize it to a size of max size
    protected val dataMapped: MutableMap<Address, T> = mutableMapOf()

    /*
    getData returns the data at cpu address
     */
    fun getData(address: Address): T {
        return data[address()]
    }

    /*
    Default values for the class
    */
    companion object {
        // Assign the static variables
        const val DEFAULT_MAX_MEM_SIZE = 20000

        // Correlated classes

        /*
        Address holds info on the address. instance() returns the index
         */
        data class Address(val address: UInt = 0u) {
            private val index: UInt = address / 4u

            operator fun invoke(): Int = index.toInt()

            operator fun plus(numOfFourBytes: Int): Address  {
                return Address(address + (numOfFourBytes * 4).toUInt())
            }

            init {
                if (index % 4u != 0u) throw InvalidAddressException()
            }
        }

        @JvmStatic
        fun getAddress(index: Int) : Address {
            if (index < 0 || index.toUInt() > UInt.MAX_VALUE / 4u) throw InvalidAddressException()

            return Address(index.toUInt() * 4u)
        }
    }
}

class Registers : Memory<RegisterData>(32) {
    override val data: Array<RegisterData> = Array<RegisterData>(maxSize) { RegisterData() }
}

class RamMemory() : Memory<MipsInstructionData>() {
    override val data: Array<MipsInstructionData>
        get() {
            // Create an array of size maxSize of Mips Instructions
            return Array<MipsInstructionData>(maxSize) { index ->
                MipsInstructionData(address = Companion.getAddress(index))
            }
        }

    fun setInstructions(instructions: ByteArray) {
        if (instructions.size % 4 != 0) throw InvalidAddressException()

//        for (i in )
    }
}



