package org.pluverse.cs241.emulator.controllers

import org.pluverse.cs241.emulator.cpumodel.CpuEmulator
import org.pluverse.cs241.emulator.cpumodel.EmulatorHasReturnedOSException
import org.pluverse.cs241.emulator.views.CliView
import kotlin.io.path.Path
import kotlin.io.path.readBytes

/**
 * This controller runs the instructions on the CLI and produces the final results. This is the
 * mips.array CLI remake.
 *
 * Has a main method to run the emulator, however, can be called from MipsEmulatorMain
 */

class ArrayController {

    companion object {

        /**
         * @params args: contains the file input path
         *
         */
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.isEmpty()) throw Error("Need file input")

            // Get the length of the array
            print("Enter length of array: ")
            val length = readln().toInt()

            // Get the input values for the array
            val inputArray = Array<Int>(length) { index ->
                print("Enter array element $index: ")
                readln().toIntOrNull() ?: 0
            }

            val view = CliView()
            val emulator = CpuEmulator(view, Path(args[0]).readBytes(), inputArray)

            println("Running MIPS program.")

            try {
                while (true) {
                    emulator.runFetchExecuteLoop()
                }
            } catch(error: EmulatorHasReturnedOSException) {
                println(view.getCompletedOutput())
            }
        }
    }
}