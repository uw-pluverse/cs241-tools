package org.pluverse.cs241.emulator.cpumodel


open class MissingFileArgException : Throwable("Missing a file argument")
open class BadMipsFileException(details: String = "") : Throwable("Bad mips file format: $details")
open class MissingByteException : BadMipsFileException("Partial double word read, must be 4 bytes (doubleword)")

/**
 * Exceptions related to running the emulator
 */
open class EmulatorHasReturnedOSException : Throwable("Program already returned to OS, finished successfully")
open class WordWithNoInstructionException : Throwable("Executing a non-instruction is not allowed")
open class ReverseNoExecutionExeception : Throwable("No more instructions to reverse")
open class NoInputToReadException : Throwable("No input to load into a word")
open class ArrayOutsideMemoryRangeException : Throwable("Array has too many elements; is outside memory range")

/**
 * Exceptions related to the Memory class
 *
*/
open class OutsideMemoryRangeException : Throwable("Data is outside memory range.")
open class OutsideAddressRangeException : Throwable("Address is outside memory range.")
open class InvalidAddressException : Throwable("Address must be a multiple of four and positive")

/**
 * Internal errors
 */
open class WrongMipsInstructionException: Throwable("Mismatch opcode or operand")
open class BadCodeException(type: String) : Throwable("Mismatch opcode or operand on $type")

