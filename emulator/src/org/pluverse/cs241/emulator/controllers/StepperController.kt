package org.pluverse.cs241.emulator.controllers;

import org.pluverse.cs241.emulator.cpumodel.CpuEmulator
import org.pluverse.cs241.emulator.cpumodel.EmulatorHasReturnedOSException
import org.pluverse.cs241.emulator.views.CliView
import kotlin.io.path.Path
import kotlin.io.path.readBytes


class StepperController {

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
                val input = readln().toString()[0]
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