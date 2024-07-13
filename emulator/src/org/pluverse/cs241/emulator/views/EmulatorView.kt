package org.pluverse.cs241.emulator.views

import org.pluverse.cs241.emulator.cpumodel.Address
import org.pluverse.cs241.emulator.cpumodel.RamMemory
import org.pluverse.cs241.emulator.cpumodel.Registers

/**
 * Represents a view of the emulator. Provides methods to update the view in the CpuEmulator class.
 *
 * Provide functionality for the CpuModel to update the view.
 *
 *      Functions: updateRegisters, updateMemory, updateProgramCounter
 *
 * Provide functionality for the Controller to update the model.
 *
 *      Functions: loadProgram, runProgram, stepProgram, resetProgram, reverseProgram
 */
interface EmulatorView {

    /**
     * Functions to inject dependencies in the CPUModel to initialize states
     *
     *      Registers, Memory, and Program Counter
     */
    fun injectInitialState(registers: Registers, memory: RamMemory, pc: Address)


    /**
     * Updates the registers in the view. Specifies a specific register that changed too
     */
    fun updateRegisters(registers: Registers, changedIndex: Int)

    /**
     * Updates the RAM memory in the view. Specifies a specific memory index that changed too
     */
    fun updateMemory(memory: RamMemory, changedIndex: Int)

    /**
     * Updates the program counter in the view
     */
    fun updateProgramCounter(pc: Address)

}