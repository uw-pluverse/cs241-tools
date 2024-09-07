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
package org.pluverse.cs241.emulator.controllers;

import org.pluverse.cs241.emulator.cpumodel.CpuEmulator
import org.pluverse.cs241.emulator.cpumodel.EmulatorHasReturnedOSException
import org.pluverse.cs241.emulator.views.CliView
import org.pluverse.cs241.emulator.views.GuiView
import kotlin.io.path.Path
import kotlin.io.path.readBytes


class StepperControllerTest {

    companion object {

        /**
         * @params args: contains the file input path
         *
         */
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.isEmpty()) throw Error("Need file input")

            // Get the two integer inputs
            print("Enter value for register 1: ")
            val int1 = readln().toIntOrNull() ?: 0

            print("Enter value for register 2: ")
            val int2 = readln().toIntOrNull() ?: 0

            val view = CliView()
            val emulator = CpuEmulator(view, Path(args[0]).readBytes(), int1, int2)

            println("Running MIPS program.")

            while(!emulator.hasReturnedOS) {
                print("Type n, q, or r: ")
                val input = readln()[0]

                when(input) {
                    'n' -> emulator.runFetchExecuteLoop()
                    'r' -> emulator.reverseExecution()
                    'q' -> break
                    else -> println("Invalid input")
                }

                println(view.getCompletedOutput())
            }

            println(view.getCompletedOutput())
        }
    }
}

class StepperController {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.isEmpty()) throw Error("Need file input")

            // Get the two integer inputs
            print("Enter value for register 1: ")
            val int1 = readln().toIntOrNull() ?: 0

            print("Enter value for register 2: ")
            val int2 = readln().toIntOrNull() ?: 0

            val view = GuiView()
            val emulator = CpuEmulator(view, Path(args[0]).readBytes(), int1, int2)

            val stepOver: () -> Unit = {
                if (!emulator.hasReturnedOS) emulator.runFetchExecuteLoop()
            }

            val stepBack: () -> Unit = {
                if (emulator.numReverseExecutions() > 0)
                    emulator.reverseExecution()
            }

            try {
                view.start(stepOver, stepBack)
            } catch (e: EmulatorHasReturnedOSException) {
                e.printStackTrace()
            } finally {
                view.screen.let {
                    try {
                        it.stopScreen()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}