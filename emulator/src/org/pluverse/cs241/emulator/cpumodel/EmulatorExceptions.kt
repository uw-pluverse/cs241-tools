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
package org.pluverse.cs241.emulator.cpumodel

import java.io.PrintWriter
import java.io.StringWriter

open class MissingFileArgException : Exception("Missing a file argument")
open class BadMipsFileException(details: String = "") : Exception("Bad mips file format: $details")
open class MissingByteException : BadMipsFileException(
  "Partial double word read, must be 4 bytes (doubleword)",
)

/**
 * Exceptions related to running the emulator
 */
open class EmulatorHasReturnedOSException : Exception(
  "Program returned to OS, finished successfully",
)
open class WordWithNoInstructionException(
  val wordInstruction: WordInstruction,
) : Exception("Executing a non-instruction is not allowed $wordInstruction")
open class ReverseNoExecutionExeception : Exception("No more instructions to reverse")
open class NoInputToReadException : Exception("No input to load into a word")
open class ArrayOutsideMemoryRangeException : Exception(
  "Array has too many elements; is outside memory range",
)

/**
 * Exceptions related to the Memory class
 *
*/
open class OutsideMemoryRangeException : Exception("Data is outside memory range.")
open class OutsideAddressRangeException : Exception(
  "Address is outside memory range (< 0 or > 0x10000000).",
)
open class InvalidAddressException
(message: String) : Exception("Address must be a multiple of four: $message")

/**
 * Internal errors
 */
open class WrongMipsInstructionException : Exception("Mismatch opcode or operand")
open class BadCodeException(type: String) : Exception("Mismatch opcode or operand on $type")

class InstructionExecutionFailureException(
  message: String,
  val instruction: MipsInstruction,
  val pc: Address,
  cause: Exception,
) : Exception(
  """$message
    |PC=$pc
    |Instruction=$instruction
  """.trimMargin(),
  cause,
) {

  fun printStackTraceToString(): String {
    val stringWriter = StringWriter()

    printStackTrace(PrintWriter(stringWriter))
    return stringWriter.toString()
  }
}
