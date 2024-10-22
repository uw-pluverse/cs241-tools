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

import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.readBytes
import org.pluverse.cs241.emulator.cpumodel.CpuEmulator
import org.pluverse.cs241.emulator.views.CliView
import org.pluverse.cs241.emulator.views.GuiView

class MipsEmulatorMain {

  companion object {

    /**
     * main: the entry point of this application
     * @param args: array of arguments provided from cmdline
     */
    @JvmStatic
    fun main(args: Array<String>) {
      val cmdOptions = CommandOptions(args)
      if (cmdOptions.commandMain.help) {
        cmdOptions.commander.usage()
        return
      }
      val view = if (cmdOptions.commandMain.debugging) {
        GuiView()
      } else {
        CliView()
      }
      val emulator = when (cmdOptions.commander.parsedCommand) {
        CommandOptions.COMMAND_TWOINTS -> {
          val twoints = cmdOptions.commandTwoInts
          CpuEmulator.createTwoIntsEmulator(
            view,
            mipsInputData = readMipsProgram(twoints.mipsFilePath),
            register1 = twoints.register1,
            register2 = twoints.register2
          )
        }

        CommandOptions.COMMAND_ARRAY -> {
          val arrayCommand = cmdOptions.commandArray
          CpuEmulator.createArrayEmulator(
            view,
            mipsInputData = readMipsProgram(arrayCommand.mipsFilePath),
            arrayCommand.elements
          )
        }
        else -> error(cmdOptions.commander.parsedCommand)
      }
      view.start(emulator)
    }

    fun readMipsProgram(stringPath: String? ):ByteArray {
      checkNotNull(stringPath) {"No mips program is specified."}
      val path = Paths.get(stringPath)
      check(Files.isRegularFile(path)) { "$path is not a regular file" }
      return path.readBytes()
    }
  }
}
