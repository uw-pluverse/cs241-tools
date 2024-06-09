package org.pluverse.cs241.emulator.views

import org.pluverse.cs241.emulator.cpumodel.Registers
import org.pluverse.cs241.emulator.cpumodel.RamMemory
import org.pluverse.cs241.emulator.cpumodel.Memory.Companion.Address

/**
 * This class is a simple view which only needs the registers and PC
 */
class CliView : EmulatorView {

    lateinit var registers: Registers

    override fun injectInitialState(registers: Registers, memory: RamMemory, pc: Address) {
        this.registers = registers
    }

    override fun updateRegisters(registers: Registers, changedIndex: Int) {}

    override fun updateMemory(memory: RamMemory, changedIndex: Int) {}

    override fun updateProgramCounter(pc: Address) {}

    fun getCompletedOutput(): String {
        val ret: StringBuilder = StringBuilder("MIPS Program Completed\n")
        var rowCounter = 0 // Should print 4 registers per row

        for (i in 1..31) {
            ret.append("$${i.toString().padStart(2, '0')} = ${registers[i].getHex()}  ")
            rowCounter++

            if (rowCounter == 4) {
                ret.append("\n")
                rowCounter = 0
            }
        }

        return ret.toString()
    }
}