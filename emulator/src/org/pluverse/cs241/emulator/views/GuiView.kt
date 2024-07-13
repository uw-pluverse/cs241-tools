package org.pluverse.cs241.emulator.views

import org.pluverse.cs241.emulator.cpumodel.Address
import org.pluverse.cs241.emulator.cpumodel.RamMemory
import org.pluverse.cs241.emulator.cpumodel.Registers

class GuiView : EmulatorView {

    override fun injectInitialState(registers: Registers, memory: RamMemory, pc: Address) {
        TODO("Not yet implemented")
    }

    override fun updateRegisters(registers: Registers, changedIndex: Int) {
        TODO("Not yet implemented")
    }

    override fun updateMemory(memory: RamMemory, changedIndex: Int) {
        TODO("Not yet implemented")
    }

    override fun updateProgramCounter(pc: Address) {
        TODO("Not yet implemented")
    }
}