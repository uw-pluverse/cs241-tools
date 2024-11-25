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
interface IEmulatorListener {

  /**
   * Functions to inject dependencies in the CPUModel to initialize states
   *
   *      Registers, Memory, Program Counter, Execution Stack, Check Returned OS
   */
  fun injectInitialState(
    registers: ReadonlyMemory,
    memory: ReadonlyMemory,
    pc: () -> Address,
    executionStack: ExecutionStack,
    checkReturnedOs: () -> Boolean,
  )

  /**
   * Updates the registers in the view. Specifies a specific register that changed too
   */
  fun notifyRegUpdate(index: Int, oldValue: Int)

  /**
   * Updates the RAM memory in the view. Specifies a specific memory index that changed too
   * and notes the old value.
   *
   */
  fun notifyMemUpdate(address: Address, oldValue: Int)

  /**
   * Updates the program counter in the view
   */
  fun notifyPcUpdate(pc: Address)

  fun notifyRunInstruction(
    instruction: MipsInstruction,
    executions: List<Execution>,
    error: InstructionExecutionFailureException?,
  )
}
