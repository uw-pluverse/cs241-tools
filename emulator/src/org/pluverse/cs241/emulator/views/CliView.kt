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
import org.pluverse.cs241.emulator.cpumodel.Execution
import org.pluverse.cs241.emulator.cpumodel.MipsInstruction

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
