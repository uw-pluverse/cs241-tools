package org.pluverse.cs241.emulator

import org.pluverse.cs241.emulator.controllers.*
import kotlin.io.path.readBytes

import kotlinx.cli.*

class MipsEmulatorMain {

    companion object {

        /**
         * main: the entry point of this application
         * @param args: array of arguments provided from cmdline
         */
        @JvmStatic
        fun main(args: Array<String>) {
            /**
             * Format for parser:
             *
             * ./program filename [[OPTIONAL]]: -twoints
             *
             */

            val parser = ArgParser("MipsEmulatorMain")
            val file by parser.argument(ArgType.String, description = "Input file")
            val twoints by parser
                .option(ArgType.Boolean, shortName = "twoints", description = "Two Ints CLI")
                .default(false)
            val array by parser
                .option(ArgType.Boolean, shortName = "array", description = "Array CLI")
                .default(false)
            val stdin by parser
                .option(ArgType.Boolean, shortName = "stdin", description = "Stdin CLI")
                .default(false)

            parser.parse(args)

            val argsInput = Array<String>(1) { file }

            if (twoints) TwoIntsController.main(argsInput)
            else if (array) ArrayController.main(argsInput)
            else if (stdin) StdinController.main(argsInput)
            else throw Error("Need to specify a controller")
        }
    }

}