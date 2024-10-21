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
package org.pluverse.cs241.emulator.views

import org.pluverse.cs241.emulator.cpumodel.Address
import org.pluverse.cs241.emulator.cpumodel.CpuEmulator
import org.pluverse.cs241.emulator.cpumodel.ExecutionStack
import org.pluverse.cs241.emulator.cpumodel.IEmulatorListener
import org.pluverse.cs241.emulator.cpumodel.ReadonlyMemory

/**
 * This class is a simple view which requires the register, memory, and pc
 *
 */
abstract class BasicEmulatorView : IEmulatorListener {
  lateinit var registers: ReadonlyMemory
  lateinit var memory: ReadonlyMemory
  lateinit var pc: () -> Address
  lateinit var executionStack: ExecutionStack
  lateinit var checkReturnedOs: () -> Boolean

  override fun injectInitialState(
    registers: ReadonlyMemory,
    memory: ReadonlyMemory,
    pc: () -> Address,
    executionStack: ExecutionStack,
    checkReturnedOs: () -> Boolean,
  ) {
    this.registers = registers
    this.memory = memory
    this.pc = pc
    this.executionStack = executionStack
    this.checkReturnedOs = checkReturnedOs
  }

  abstract fun start(emulator: CpuEmulator)
}
