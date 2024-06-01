package org.pluverse.cs241.emulator

/*
Exceptions related to bad the mips file reading
 */

open class MissingFileArgException : Throwable("Missing a file argument")
open class BadMipsFileException(details: String = "") : Throwable("Bad mips file format: $details")
open class MissingByteException : BadMipsFileException("Partial double word read, must be 4 bytes (doubleword)")

/*
Exceptions related to the Memory class

 */
open class OutsideMemoryRangeException : Throwable("Data is outside memory range.")
open class InvalidAddressException : Throwable("Address must be a multiple of four and positive")

/*
Internal errors
 */
open class WrongMipsInstructionException: Throwable("Mismatch opcode or operand")
open class BadCodeException(type: String) : Throwable("Mismatch opcode or operand on $type")
