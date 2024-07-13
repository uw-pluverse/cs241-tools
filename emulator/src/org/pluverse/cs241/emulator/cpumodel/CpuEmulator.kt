package org.pluverse.cs241.emulator.cpumodel

import org.pluverse.cs241.emulator.views.EmulatorView

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

    var hasReturnedOS = false // This tracks to ensure we haven't returned to OS yet

    /**
     * Primary constructor to be called by other constructors:
     *
     *      Read the MIPS file and load the instructions into the memory
     *
     */
    private constructor(view: EmulatorView, mipsInputData: ByteArray) {
        this.mipsInputData = mipsInputData
        this.instructionsCount = mipsInputData.size / 4

        view.injectInitialState(registers, memory, pc)

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
     * executionStack will be appended whenever a change occurs in the memory, register, or PC to save
     * for rewind. This is a list of list of executions done in each execution in stack-like fashion.
     *
     * MutableList<...> below:
     *
     * MutableList<Triple<ExecutionType, MemoryCompanion.Address, Int>>
     *
     *     A list of all mutations during a single instruction. Last item in overall list
     *     is the last instruction's mutation.
     *
     * ... Triple<ExecutionType, Companion.Address, Int>
     *
     *     Execution Type: The type of execution done
     *
     *     Address: The address in which it was stored || Last PC address
     *
     *     Int: The value that was in their prior || null
     *
     */
    enum class ExecutionType {REGISTER, MEMORY, PC}
    private val executionStack: MutableList<MutableList<Triple<ExecutionType, Address, Int>>> = mutableListOf()

    /**
     * Functions for executionStack below:
     *
     * getNumExecutions() returns the number of executions made already
     *
     * recordExecution(...) adds the mutation to the current instructions' executions (the last item/array).
     *
     * reverseExecution(...) reverses the numOfExecutions x instructions to previous values
     */
    fun getNumExecutions(): Int = executionStack.size

    fun recordExecution(mutation: Triple<ExecutionType, Address, Int>) {
        executionStack.last().add(mutation)
    }

    fun reverseExecution(numOfExecutions: Int = 1) {
        // First check there are enough executions to reverse
        if (numOfExecutions > executionStack.size) throw ReverseNoExecutionExeception()

        // Now reverse the executions
        for (i in numOfExecutions downTo 1) {
            val lastExecutions = executionStack.removeLast() // Pop last item in the stack

            // Go through each item in reverse, and reverse its affect
            lastExecutions.reversed().forEach { execution ->
                val type: ExecutionType = execution.first
                val oldAddress = execution.second
                val oldValue = execution.third

                when (type) {
                    // We want to set the register to previous value
                    ExecutionType.REGISTER -> registers[oldAddress()].update(oldValue)

                    // Restore old memory value
                    ExecutionType.MEMORY -> memory[oldAddress].update(oldValue)

                    // Set the PC to previous PC
                    ExecutionType.PC -> pc = oldAddress
                }
            }
        }
    }

    /**
     * Function to determine if the program has finished
     */
    fun hasFinished(): Boolean {
        return hasReturnedOS
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

        val instruction = memory.getData(pc)
        pc += 1

        // We want to push the executionStack by one to record the instruction about to run
        executionStack.add(mutableListOf())

        // Execute the instruction with the given functions
        instruction().execute(::getReg, ::getMem, ::updateReg, ::updateMem, ::setPC)
    }

    /**
     * Operators for instructions to use. It will mutate the view as required T.B.C.
     *
     */

    private fun getReg(index: Int): Int {
        return registers[index].doubleWord
    }

    private fun updateReg(index: Int, value: Int) {
        registers[index].update(value)
    }

    private fun getMem(address: Address): Int {
        return memory[address].doubleWord
    }

    private fun updateMem(address: Address, value: Int) {
        memory[address].update(value)
    }

    /**
     * init: takes the current PC and returns the NEW PC based on the old one
     */
    private fun setPC(init: ((currentPC: Address) -> Address)) {
        pc = init(pc) // We return the new PC based on the function

        if (pc.address.toLong() == RETURN_OS) {
            hasReturnedOS = true
        }
    }

    companion object {
        const val RETURN_OS = 0x8123456c // IDK subject to change\

        const val MAX_ADDRESS: UInt = 0x01000000u // This is the max address and starting stack pointer
        const val MAX_ARRAY_SIZE: Int = 4194304 // This is MAX_ADDRESS / 4
    }
}
