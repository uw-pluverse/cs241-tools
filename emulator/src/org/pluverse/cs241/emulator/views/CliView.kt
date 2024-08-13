package org.pluverse.cs241.emulator.views

import org.pluverse.cs241.emulator.cpumodel.*

/**
 * This class is a simple view which only needs the registers and PC
 */
class CliView : BasicEmulatorView() {

    override fun notifyRegUpdate(index: Int, oldValue: Int) {}

    override fun notifyMemUpdate(address: Address, oldValue: Int) {}

    override fun notifyPcUpdate(pc: Address) {}

    override fun notifyRunInstruction(instruction: MipsInstruction, executions: List<Execution>) {}

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