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
package org.pluverse.cs241.emulator

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import org.pluverse.cs241.emulator.controllers.ArrayController
import org.pluverse.cs241.emulator.controllers.StdinController
import org.pluverse.cs241.emulator.controllers.StepperController
import org.pluverse.cs241.emulator.controllers.StepperControllerArray
import org.pluverse.cs241.emulator.controllers.TwoIntsController

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
      val array by parser
        .option(ArgType.Boolean, shortName = "array", description = "Array CLI")
        .default(false)
      val stdin by parser
        .option(ArgType.Boolean, shortName = "stdin", description = "Stdin CLI")
        .default(false)
      val debug by parser
        .option(ArgType.Boolean, shortName = "debug", description = "Debug mode")
        .default(false)

      parser.parse(args)

      val argsInput = Array(1) { file }

      if (array) {
        if (debug) {
          StepperControllerArray.main(
            argsInput,
          )
        } else {
          ArrayController.main(argsInput)
        }
      } else if (stdin) {
        StdinController.main(argsInput)
      } else {
        // Default to twoints
        if (debug) StepperController.main(argsInput) else TwoIntsController.main(argsInput)
      }
    }
  }
}
