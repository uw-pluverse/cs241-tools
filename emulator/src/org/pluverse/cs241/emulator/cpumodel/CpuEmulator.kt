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

import org.pluverse.cs241.emulator.views.EmulatorView
import org.pluverse.cs241.emulator.cpumodel.Execution.ExecutionType

/**
CpuEmulator (Class) stores the basic registers, pc pointer, and stores and retrieves from the memory.

Note, $30 or index 30 of the register is the stack pointer by convention.
 */

class CpuEmulator {

    // Define CONSTANTS
    private val mipsInputData: ByteArray // The data from input
    private val instructionsCount: Int // The number of instructions during init

    // Define VARIABLES
    private val registers: Registers = Registers()
    private val memory : RamMemory = RamMemory(MAX_ARRAY_SIZE)

    private var pc = Address(0u) // Start the PC at 0x0

    private val executionStack = MutableExecutionStack() // This is used to track the execution of the program for step reversal
    private val view: EmulatorView // Used to notify view.

    val hasReturnedOS: Boolean get() = pc.address.toLong() == RETURN_OS // This tracks to ensure we haven't returned to OS yet

    /**
     * Primary constructor to be called by other constructors:
     *
     *      Read the MIPS file and load the instructions into the memory
     *
     */
    private constructor(view: EmulatorView, mipsInputData: ByteArray) {
        this.mipsInputData = mipsInputData
        this.instructionsCount = mipsInputData.size / 4

        this.view = view
        this.view.injectInitialState(registers, memory, pc, executionStack)

        // Verify it has a valid number of bytes. Also, we want to ensure that
        // the number of bytes won't overflow the largest address 0xffffffff (unsigned)
        if (mipsInputData.size % 4 != 0 || mipsInputData.size > (UInt.MAX_VALUE / 4u).toInt())
            throw InvalidAddressException()

        // Insert the instruction into the memory
        for (i in mipsInputData.indices step 4) {
            val byteAddress = Address(i.toUInt())

            // This sets the data to the instruction
            memory.getData(byteAddress).update(
                MemoryData.convertFourByteToInteger(
                    mipsInputData[i], mipsInputData[i + 1], mipsInputData[i + 2], mipsInputData[i + 3]
                )
            )
        }
    }

    /**
     * This constructor is used for initializing the emulator with two ints
     *
     */
    constructor(view: EmulatorView, mipsInputData: ByteArray, input1: Int, input2: Int) : this(view, mipsInputData) {

        registers[1].update(input1)
        registers[2].update(input2)
    }

    /**
     * Initialize the object with an array of ints
     *
     */
    constructor(view: EmulatorView, mipsInputData: ByteArray, inputArray: Array<Int>) : this(view, mipsInputData) {
        // Want to load it in after the instructions.
        // Add a buffer of 2 instructions from the last instruction

        var insertAddress = Address((instructionsCount.toUInt() + 8u) * 4u)

        // Set Register 1 to the array start address
        // Set Register 2 to the array length
        registers[1].update(insertAddress.address.toInt())
        registers[2].update(inputArray.size)

        for (i in inputArray) {
            if (insertAddress.address > MAX_ADDRESS) throw ArrayOutsideMemoryRangeException()

            memory[insertAddress].update(i)
            insertAddress += 1 // Increment it. Note it won't overflow because
        }
    }

    /**
     * Set the starting register values
     */
    init {
        // Modify $31 to be return address
        registers[Registers.JUMP_REGISTER].update(RETURN_OS.toInt())

        // Set $30 - stack pointer - to be max_address
        registers[Registers.STACK_POINTER].update(MAX_ADDRESS.toInt())
    }

    /**
     * Functions for executionStack below:
     *
     * reverseExecution(...) reverses the numOfExecutions x instructions to previous values
     */
    fun numReverseExecutions(): Int = executionStack.getSize()

    fun reverseExecution(numOfExecutions: Int = 1) {
        // First check there are enough executions to reverse
        if (numOfExecutions > numReverseExecutions()) throw ReverseNoExecutionExeception()

        // Now reverse the executions
        for (i in numOfExecutions downTo 1) {
            val lastExecutions = executionStack.pop()!! // Pop last item in the stack

            // Go through each item in reverse, and reverse its affect
            lastExecutions.reversed().forEach { execution ->
                val type: ExecutionType = execution.type
                val oldAddress = execution.address
                val oldValue = execution.value

                when (type) {
                    // We want to set the register to previous value
                    ExecutionType.REGISTER -> {
                        registers[oldAddress].update(oldValue)
                        view.notifyRegUpdate(oldAddress(), oldValue)
                    }

                    // Restore old memory value
                    ExecutionType.MEMORY -> {
                        memory[oldAddress].update(oldValue)
                        view.notifyMemUpdate(oldAddress, oldValue)
                    }

                    // Set the PC to previous PC
                    ExecutionType.PC -> {
                        pc = oldAddress
                        view.notifyPcUpdate(pc)
                    }
                }
            }
        }
    }

    /**
     * Runs a single loop of the fetch execute cycle (Steps 3-5)
     *
     * 1: PC = 0x00
     *
     * 2: while True do
     *
     * 3: IR = MEM \[PC]
     *
     * 4: PC += 4
     *
     * 5: Decode and execute instruction in IR
     *
     * 6: end while
     */
    fun runFetchExecuteLoop() {
        if (hasReturnedOS) throw EmulatorHasReturnedOSException()

        executionStack.newInstruction() // Start a new instruction

        val data = memory.getData(pc)
        setPC { pc -> pc + 1 } // We want to increment the PC by 1 byte

        val instruction = data.instruction

        // Execute the instruction with the given functions
        instruction.execute(::getReg, ::getMem, ::updateReg, ::updateMem, ::setPC)

        // Notify the view we have run an instruction
        view.notifyRunInstruction(instruction, executionStack.last()!!)
    }

    /**
     * Operators for instructions to use. It will mutate the view as required T.B.C.
     *
     */

    private fun getReg(index: Int): Int {
        return registers[index].doubleWord
    }

    private fun updateReg(index: Int, value: Int) {
        assert(index in 0..33) { "Invalid register index" }

        // Record "prior" register value
        executionStack.recordExecution(ExecutionType.REGISTER, Address(index.toUInt() * 4u), registers[index].doubleWord)
        val oldValue = registers[index].doubleWord

        registers[index].update(value)

        // Notify the view of the register update
        view.notifyRegUpdate(index, oldValue)
    }

    private fun getMem(address: Address): Int {
        return memory[address].doubleWord
    }

    private fun updateMem(address: Address, value: Int) {
        // Record "prior" memory value
        executionStack.recordExecution(ExecutionType.MEMORY, address, memory[address].doubleWord)
        val oldValue = memory[address].doubleWord

        memory[address].update(value)

        // Notify the view of the memory update
        view.notifyMemUpdate(address, oldValue)
    }

    /**
     * init: takes the current PC and returns the NEW PC based on the old one
     */
    private fun setPC(init: ((currentPC: Address) -> Address)) {
        // Record the old PC
        executionStack.recordExecution(ExecutionType.PC, pc)

        pc = init(pc) // We return the new PC based on the function

        // Notify the view of the PC update
        view.notifyPcUpdate(pc)
    }

    companion object {
        const val RETURN_OS = 0x8123456c // IDK subject to change\

        const val MAX_ADDRESS: UInt = 0x01000000u // This is the max address and starting stack pointer
        const val MAX_ARRAY_SIZE: Int = 4194304 + 1 // This is MAX_ADDRESS / 4
    }
}
