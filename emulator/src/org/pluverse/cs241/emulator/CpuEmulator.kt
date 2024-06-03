package org.pluverse.cs241.emulator

import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.readBytes
import org.pluverse.cs241.emulator.Memory.Companion as MemoryCompanion

/**
CpuEmulator (Class) stores the basic registers, pc pointer, and stores and retrieves from the memory.

Note, $30 or index 30 of the register is the stack pointer by convention.
 */

class CpuEmulator(fp: String) {

    // Define CONSTANTS
    private val file: Path = Path(fp).absolute();

    // Define VARIABLES
    val registers: Memory<RegisterData> = Registers()
    val memory : Memory<MipsInstructionData> = RamMemory()

    var pc = MemoryCompanion.Address(0u) // Start the PC at 0x0

    var hasReturnedOS = false // This tracks to ensure we haven't returned to OS yet

    /**
     * executionStack will be appended whenever a change occurs in the memory or register to save
     * for rewind
     *
     * Pair<Int, ...>
     *
     *     register: 0
     *
     *     memory: 1
     *
     *     PC: 2
     *
     * ... Pair<Companion.Address, Int>
     *
     *     Address: The address in which it was stored
     *
     *     Int: The value that was in their prior
     *
     * TODO...
     */
    val executionStack: MutableList<Pair<Int, Pair<MemoryCompanion.Address, Int>>> = mutableListOf()

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
        if (hasReturnedOS) throw EmulatorHasReturnedOS()

        val instruction = memory.getData(pc)
        pc += 1

        instruction().execute(::getReg, ::getMem, ::updateReg, ::updateMem, ::setPC) // Execute the instruction with the given functions
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

    private fun getMem(address: MemoryCompanion.Address): Int {
        return memory[address].doubleWord
    }

    private fun updateMem(address: MemoryCompanion.Address, value: Int) {
        memory[address].update(value)
    }

    private fun setPC(init: ((currentPC: MemoryCompanion.Address) -> MemoryCompanion.Address)) {
        val newAddress = init(pc)

        if (newAddress.address.toLong() == RETURN_OS) {
            hasReturnedOS = true
        }
    }


    /**
     * Read the MIPS file and load the instructions into the memory
     *
     */
    init {
        val mipsFileData = file.readBytes() // Read the data

        // Verify it has a valid number of bytes. Also, we want to ensure that
        // the number of bytes won't overflow the largest address 0xffffffff (unsigned)
        if (mipsFileData.size % 4 != 0 || mipsFileData.size > (UInt.MAX_VALUE / 4u).toInt())
            throw InvalidAddressException()

        // Insert the instruction into the memory
        for (i in mipsFileData.indices step 4) {
            val byteAddress = MemoryCompanion.Address(i.toUInt())

            // This sets the data to the instruction
            memory.getData(byteAddress).update(MemoryData.convertFourByteToInteger(
                mipsFileData[i], mipsFileData[i + 1], mipsFileData[i + 2], mipsFileData[i + 3]
            ))
        }
    }

    /**
     * Set the starting register values
     */
    init {
        // Modify $31 to be return address
        registers[31].update(RETURN_OS.toInt())

        // Modify $1, $2 ...
    }

    companion object {
        const val RETURN_OS = 0x8123456c // IDK subject to change
    }
}
