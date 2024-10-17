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
 * This controller runs the program on the CLI and produces the final results, without setting registers.
 * mips.stdin CLI remake.
 *
 * Has a main method to run the emulator, however, can be called from MipsEmulatorMain
 */

class StdinController {

  companion object {

    /**
     * @params args: contains the file input path
     *
     */
    @JvmStatic
    fun main(args: Array<String>) {
      if (args.isEmpty() || !Path(args[0]).exists()) throw Error("Need file input")

      val view = CliView()
      val emulator = CpuEmulator(view, Path(args[0]).readBytes(), 0, 0)

      println("Running MIPS program.")

      try {
        while (true) {
          emulator.runFetchExecuteLoop()
        }
      } catch (error: EmulatorHasReturnedOSException) {
        println(view.getCompletedOutput())
      } catch (error: Exception) {
        println(error.message)
      }
    }
  }
}
