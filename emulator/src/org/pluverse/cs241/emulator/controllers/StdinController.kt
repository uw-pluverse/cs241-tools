package org.pluverse.cs241.emulator.controllers

import org.pluverse.cs241.emulator.cpumodel.CpuEmulator
import org.pluverse.cs241.emulator.cpumodel.EmulatorHasReturnedOSException
import org.pluverse.cs241.emulator.views.CliView
import kotlin.io.path.Path
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
            if (args.isEmpty()) throw Error("Need file input")

            val view = CliView()
            val emulator = CpuEmulator(view, Path(args[0]).readBytes(), 0, 0)

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