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
package org.pluverse.cs241.emulator.controllers

import org.pluverse.cs241.emulator.cpumodel.CpuEmulator
import org.pluverse.cs241.emulator.cpumodel.EmulatorHasReturnedOSException
import org.pluverse.cs241.emulator.views.CliView
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.readBytes

/**
 * This controller runs twoints on the CLI and produces the final results. This is the
 * Mips.TwoInts CLI remake.
 *
 * Has a main method to run the emulator, however, can be called from MipsEmulatorMain
 */

class TwoIntsController {

  companion object {

    /**
     * @params args: contains the file input path
     *
     */
    @JvmStatic
    fun main(args: Array<String>) {
      if (args.isEmpty() || !Path(args[0]).exists()) throw Error("Need file input")

      // Get the two integer inputs
      print("Enter value for register 1: ")
      val register1 = readln().toIntOrNull() ?: 0

      print("Enter value for register 2: ")
      val register2 = readln().toIntOrNull() ?: 0

      val view = CliView()
      val emulator = CpuEmulator.createTwoIntsEmulator(
        view,
        Path(args[0]).readBytes(),
        register1 = register1,
        register2 = register2,
      )

      System.err.println("Running MIPS program.")

      try {
        while (true) {
          emulator.runFetchExecuteLoop()
        }
      } catch (error: EmulatorHasReturnedOSException) {
        System.err.println(view.getCompletedOutput())
      } catch (error: Exception) {
        println(error.message)
      }
    }
  }
}
