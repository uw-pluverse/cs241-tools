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

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters
import java.nio.file.Path

class CommandOptions(array: Array<String>) {

  val commandMain = CommandMain()
  val commandTwoInts = CommandTwoInts()
  val commandArray = CommandArray()
  val commander = JCommander.newBuilder()
    .addObject(commandMain)
    .addCommand(COMMAND_TWOINTS, commandTwoInts)
    .addCommand(COMMAND_ARRAY, commandArray)
    .build()

  init {
    commander.parse(*array)
  }

  class CommandMain {

    @Parameter(names = ["--program"], description = "The mips binary program")
    var mipsFilePath: String? = null

    @Parameter(names = ["--stdin-file"], description = "The file for the stdandard input")
    var stdinFilePath: Path? = null

    @Parameter(names = ["--debug"], arity = 1, description = "Whether to launch the debugger")
    var debugging = false

    @Parameter(names = ["--help", "-h"], help = true, description = "Print help message")
    var help = false
  }

  @Parameters(commandDescription = "mips.twoints")
  class CommandTwoInts {

    @Parameter(names = ["--register1", "-r1"], description = "The value of register 1")
    var register1: Int = 0

    @Parameter(names = ["--register2", "-r2"], description = "The value of register 2")
    var register2: Int = 0
  }

  @Parameters(commandDescription = "mips.array")
  class CommandArray {

    @Parameter(names = ["--elements"], description = "The array elements")
    var elements: List<Int> = emptyList()
  }

  companion object {
    const val COMMAND_TWOINTS = "twoints"
    const val COMMAND_ARRAY = "array"
  }
}
