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

import org.pluverse.cs241.emulator.cpumodel.AbstractMipsStdInput
import org.pluverse.cs241.emulator.cpumodel.CpuEmulator
import org.pluverse.cs241.emulator.cpumodel.MipsStdInput
import org.pluverse.cs241.emulator.views.CliView
import org.pluverse.cs241.emulator.views.GuiView
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.readBytes

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
      val mipsProgram = readMipsProgram(cmdOptions.commandMain.mipsFilePath)
      val stdin = readStdinFile(cmdOptions.commandMain.stdinFilePath)
      val emulator = when (cmdOptions.commander.parsedCommand) {
        CommandOptions.COMMAND_TWOINTS -> {
          val twoints = cmdOptions.commandTwoInts
          CpuEmulator.createTwoIntsEmulator(
            view,
            mipsProgram = mipsProgram,
            stdin,
            register1 = twoints.register1,
            register2 = twoints.register2,
          )
        }

        CommandOptions.COMMAND_ARRAY -> {
          val arrayCommand = cmdOptions.commandArray
          CpuEmulator.createArrayEmulator(
            view,
            mipsProgram = mipsProgram,
            stdin,
            arrayCommand.elements,
          )
        }

        else -> error(cmdOptions.commander.parsedCommand)
      }
      view.start(emulator)
    }

    fun readStdinFile(path: Path?): AbstractMipsStdInput {
      if (path == null) {
        return MipsStdInput.EmptyMipsStdInput
      }
      check(Files.isRegularFile(path)) {
        "$path is not a regular file."
      }
      return MipsStdInput(path)
    }

    fun readMipsProgram(stringPath: String?): ByteArray {
      checkNotNull(stringPath) { "No mips program is specified." }
      val path = Paths.get(stringPath)
      check(Files.isRegularFile(path)) { "$path is not a regular file" }
      return path.readBytes()
    }
  }
}
