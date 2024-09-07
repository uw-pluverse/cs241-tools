/*
 * Copyright (C) 2018-2024 University of Waterloo.
 *
 * This file is part of Perses.
 *
 * Perses is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3, or (at your option) any later version.
 *
 * Perses is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Perses; see the file LICENSE.  If not see <http://www.gnu.org/licenses/>.
 */
package org.pluverse.cs241.emulator.cpumodel

typealias ReadonlyMemory = Memory<MemoryData>

/**
    Memory: an abstract class that holds the data and has simple operations to retrieve and set.

    Address (Class) consumes an address and returns an array index
    - We use the Address class for better semantics in converting address (multiple of 4) to an array index.
        i.e. address() => returns an array index
 */
abstract class Memory<out T : MemoryData>(protected val maxSize: Int = DEFAULT_MAX_MEM_SIZE) {

    protected abstract val data: List<T> // Initialize it to a size of max size

    /**
     * getData returns the data at cpu address
     */
    fun getData(index: Int): T {
        if (index > data.size || index < 0) throw OutsideMemoryRangeException()
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

    fun getSize(): Int {
        return data.size
    }

    fun forEach(run: (T) -> Unit) {
        for (element in data) run(element)
    }

    operator fun iterator(): Iterator<T> { return data.iterator() }


    /**
    Default values for the class
    */
    companion object {
        // Assign the static variables
        const val DEFAULT_MAX_MEM_SIZE = 20000
        const val DOUBLE_WORD_HEX_LENGTH = 8

        // Correlated classes

        /**
         * Convert an array index to it's equivalent address (index * 4).
         */
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
    override val data: MutableList<RegisterData> = MutableList<RegisterData>(maxSize) { index -> RegisterData(index) }

    companion object {
        const val HI_INDEX = 32
        const val LO_INDEX = 33

        const val STACK_POINTER = 30 // Stack pointer
        const val JUMP_REGISTER = 31 // By convention to use this register for return also for specific semantics
    }

}

/**
 * The RamMemory points an address to a double word. We will parse these double words into
 * readable Mips Instructions.
 */
class RamMemory : Memory<RamMemoryData> {

    constructor() : super()
    constructor(maxSize: Int) : super(maxSize)

    // Create an array of size maxSize of Mips Instructions
    override val data: MutableList<RamMemoryData> = MutableList<RamMemoryData>(maxSize)
    { index ->
        RamMemoryData(address = getAddress(index))
    }

}



