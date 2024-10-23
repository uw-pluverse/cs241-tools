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

import com.google.common.base.MoreObjects

/**
An abstract class that provides functionality for an instruction based on the given doubleWord

 */
abstract class MipsInstruction(val word32: Int, expectedOpcode: Int?, expectedOperand: Int?) {

  /**
   Important information about the MipsInstruction
   */

  // opcode: the first 6 bits - identifies the type
  val opcode: Int = (word32 shr 26) and MemoryData.SIX_BITS

  // operand: the last 11 bits - identifies the specific operation
  val operand: Int = word32 and MemoryData.ELEVEN_BITS

  // these are the register values. 5 digit values
  val registerS = (word32 shr 21) and MemoryData.FIVE_BITS
  val registerT = (word32 shr 16) and MemoryData.FIVE_BITS
  val registerDestination = (word32 shr 11) and MemoryData.FIVE_BITS

  // immediate value - we want it to be in two's complement - 16 bits
  val immediate = word32.toShort().toInt()

  abstract fun getSyntax(): String // Returns the mips syntax for the operation

  abstract fun execute(
    context: ExecutionContext,
  ) // This will run the instruction and update memory

  init {
    // Verify the opcode and operand is correct
    if ((expectedOpcode != null && opcode != expectedOpcode) ||
      (expectedOperand != null && operand != expectedOperand)
    ) {
      throw WrongMipsInstructionException()
    }
  }

  override fun toString(): String {
    return MoreObjects.toStringHelper(this).addValue(getSyntax()).toString()
  }

  class ExecutionContext(
    val getReg: (index: Int) -> Int,
    val getMem: (address: Address) -> Int,
    val updateReg: (index: Int, value: Int) -> Unit,
    val updateMem: (address: Address, value: Int) -> Unit,
    val setPC: (init: (currentPC: Address) -> Address) -> Unit,
    val stdin: AbstractMipsStdInput,
  )
}

/**
Three register instructions below

 */

abstract class ThreeRegisterInstruction(
  val identifier: String,
  doubleWord: Int,
  expectedOpcode: Int,
  expectedOperand: Int?,
) : MipsInstruction(doubleWord, expectedOpcode, expectedOperand) {

  override fun getSyntax(): String {
    return "$identifier $$registerDestination, $$registerS, $$registerT"
  }
}

class AddInstruction(doubleWord: Int) : ThreeRegisterInstruction(
  "add",
  doubleWord,
  OPCODE,
  OPERAND,
) {

  override fun execute(
    context: ExecutionContext,
  ) {
    // $d = $s + $t
    val computedValue = context.getReg(registerS) + context.getReg(registerT)
    context.updateReg(registerDestination, computedValue)
  }

  companion object {
    const val OPCODE: Int = 0
    const val OPERAND: Int = (0b00000100000)
  }
}

class SubInstruction(doubleWord: Int) : ThreeRegisterInstruction(
  "sub",
  doubleWord,
  OPCODE,
  OPERAND,
) {

  override fun execute(
    context: ExecutionContext,
  ) {
    // $d = $s - $t
    val computedValue = context.getReg(registerS) - context.getReg(registerT)
    context.updateReg(registerDestination, computedValue)
  }

  companion object {
    const val OPCODE: Int = 0
    const val OPERAND: Int = (0b00000100010)
  }
}

class SetLessThanInstruction(doubleWord: Int) : ThreeRegisterInstruction(
  "slt",
  doubleWord,
  OPCODE,
  OPERAND,
) {

  override fun execute(
    context: ExecutionContext,
  ) {
    // $d = 1 if $s < $t; 0 otherwise
    val valS: Int = context.getReg(registerS)
    val valT: Int = context.getReg(registerT)

    val isLessThan = 1 // Value if it's less than
    val isNotLessThan = 0

    if (valS < valT) {
      context.updateReg(
        registerDestination,
        isLessThan,
      )
    } else {
      context.updateReg(registerDestination, isNotLessThan)
    }
  }

  companion object {
    const val OPCODE: Int = 0
    const val OPERAND: Int = (0b00000101010)
  }
}

class SetLessThanUInstruction(doubleWord: Int) : ThreeRegisterInstruction(
  "sltu",
  doubleWord,
  OPCODE,
  OPERAND,
) {

  override fun execute(
    context: ExecutionContext,
  ) {
    // $d = 1 if $s < $t; 0 otherwise
    // WE WANT ONLY THE UNSIGNED INT
    val valS: UInt = context.getReg(registerS).toUInt()
    val valT: UInt = context.getReg(registerT).toUInt()

    val isLessThan = 1 // Value if it's less than
    val isNotLessThan = 0

    if (valS < valT) {
      context.updateReg(
        registerDestination,
        isLessThan,
      )
    } else {
      context.updateReg(registerDestination, isNotLessThan)
    }
  }

  companion object {
    const val OPCODE: Int = 0
    const val OPERAND: Int = (0b00000101011)
  }
}

/**
End three register instructions

Two register instructions below
 */

abstract class TwoRegisterInstruction(
  val identifier: String,
  doubleWord: Int,
  expectedOpcode: Int,
  expectedOperand: Int?,
) : MipsInstruction(doubleWord, expectedOpcode, expectedOperand) {

  override fun getSyntax(): String {
    return "$identifier $$registerS, $$registerT"
  }

  init {
    // Verify bit 12-22 are all 0
    if (registerDestination != 0) throw BadCodeException(identifier)
  }
}

class MultiplyInstruction(doubleWord: Int) : TwoRegisterInstruction(
  "mult",
  doubleWord,
  OPCODE,
  OPERAND,
) {

  /**
   * hi:lo = $s * $t
   *
   * We want to multiply it as a long, then take the 64 bits and split them
   */
  override fun execute(
    context: ExecutionContext,
  ) {
    // hi:lo = $s * $t
    val valS: Int = context.getReg(registerS)
    val valT: Int = context.getReg(registerT)

    // HI = 32 largest bits, LO = 32 Smallest bits of product
    val product: Long = valS.toLong() * valT.toLong()

    // Long.toInt() takes the 32 smallest bits
    val hi: Int = (product shr 32).toInt() // Take 32 most significant
    val lo: Int = product.toInt() // Take 32 least significant

    context.updateReg(Registers.HI_INDEX, hi)
    context.updateReg(Registers.LO_INDEX, lo)
  }

  companion object {
    const val OPCODE: Int = 0
    const val OPERAND: Int = (0b00000011000)
  }
}

class MultiplyUInstruction(doubleWord: Int) : TwoRegisterInstruction(
  "multu",
  doubleWord,
  OPCODE,
  OPERAND,
) {

  /**
   * hi:lo = $s * $t
   *
   * We want to multiply it as unsigned -> long, then take the 64 bits and split them
   */
  override fun execute(
    context: ExecutionContext,
  ) {
    // hi:lo = $s * $t
    // ** Cast them to Unsigned prior to multiplication **
    val valS: UInt = context.getReg(registerS).toUInt()
    val valT: UInt = context.getReg(registerT).toUInt()

    // HI = 32 largest bits, LO = 32 Smallest bits of product
    val product: Long = valS.toLong() * valT.toLong()

    // Long.toInt() takes the 32 smallest bits
    val hi: Int = (product shr 32).toInt() // Take 32 most significant
    val lo: Int = product.toInt() // Take 32 least significant

    context.updateReg(Registers.HI_INDEX, hi)
    context.updateReg(Registers.LO_INDEX, lo)
  }

  companion object {
    const val OPCODE: Int = 0
    const val OPERAND: Int = (0b00000011001)
  }
}

class DivideInstruction(doubleWord: Int) : TwoRegisterInstruction(
  "div",
  doubleWord,
  OPCODE,
  OPERAND,
) {

  /**
   * lo = $s / $t; hi = $s % $t
   *
   * Store int division in lo and remainder in hi
   */
  override fun execute(
    context: ExecutionContext,
  ) {
    val valS: Int = context.getReg(registerS)
    val valT: Int = context.getReg(registerT)

    val lo = valS / valT
    val hi = valS % valT

    context.updateReg(Registers.HI_INDEX, hi)
    context.updateReg(Registers.LO_INDEX, lo)
  }

  companion object {
    const val OPCODE: Int = 0
    const val OPERAND: Int = (0b00000011010)
  }
}

class DivideUInstruction(doubleWord: Int) : TwoRegisterInstruction(
  "divu",
  doubleWord,
  OPCODE,
  OPERAND,
) {

  override fun execute(
    context: ExecutionContext,
  ) {
    val valS: UInt = context.getReg(registerS).toUInt()
    val valT: UInt = context.getReg(registerT).toUInt()

    // Need the UInt operations
    // Casting doesn't change the bits - only need bits
    val lo: Int = (valS / valT).toInt()
    val hi: Int = (valS % valT).toInt()

    context.updateReg(Registers.HI_INDEX, hi)
    context.updateReg(Registers.LO_INDEX, lo)
  }

  companion object {
    const val OPCODE: Int = 0
    const val OPERAND: Int = (0b00000011011)
  }
}

/**
End two register instructions

Branch instructions below
 */

abstract class BranchInstruction(
  val identifier: String,
  doubleWord: Int,
  expectedOpcode: Int,
  expectedOperand: Int?,
) : MipsInstruction(doubleWord, expectedOpcode, expectedOperand) {

  override fun getSyntax(): String {
    return "$identifier $$registerS, $$registerT, $immediate"
  }
}

class BranchEqualInstruction(doubleWord: Int) : BranchInstruction("beq", doubleWord, OPCODE, null) {

  /**
   * if ($s == $t) pc += i * 4
   *
   * Modify the pc address
   */
  override fun execute(
    context: ExecutionContext,
  ) {
    val valS: Int = context.getReg(registerS)
    val valT: Int = context.getReg(registerT)

    context.setPC { currentPC ->
      if (valS == valT) (currentPC + immediate) else currentPC
    }
  }

  companion object {
    const val OPCODE: Int = (0b000100)
  }
}

class BranchNotEqualInstruction(doubleWord: Int) :
  BranchInstruction("bne", doubleWord, OPCODE, null) {

  /**
   * if ($s != $t) pc += i * 4
   *
   * Modify the pc address
   */
  override fun execute(
    context: ExecutionContext,
  ) {
    val valS: Int = context.getReg(registerS)
    val valT: Int = context.getReg(registerT)

    context.setPC { currentPC ->
      if (valS != valT) (currentPC + immediate) else currentPC
    }
  }

  companion object {
    const val OPCODE: Int = (0b000101)
  }
}

/**
End Branch instructions

Load/Save Word below
 */

abstract class DataInstruction(
  val identifier: String,
  doubleWord: Int,
  expectedOpcode: Int,
  expectedOperand: Int?,
) : MipsInstruction(doubleWord, expectedOpcode, expectedOperand) {

  override fun getSyntax(): String {
    return "$identifier $$registerT, $immediate($$registerS)"
  }
}

class LoadWordInstruction(doubleWord: Int) : DataInstruction("lw", doubleWord, OPCODE, null) {

  /**
   * $t = MEM [$s + i]:4
   *
   * Read from memory into the $t. We will add the immediate to the valS to get the memory addr.
   */
  override fun execute(
    context: ExecutionContext,
  ) {
    val baseAddress = context.getReg(registerS).toUInt().toLong()
    val finalAddress = baseAddress + immediate
    // Covers edge cases - must be POS and % 4.

    // If it's the input address, read in the next address
    val data: Int = if (finalAddress == STD_INPUT) {
      context.stdin.read()
    } else {
      val loadAddress = Address().shiftBytes(finalAddress.toInt())
      context.getMem(
        loadAddress,
      )
    }

    context.updateReg(registerT, data) // Mutate regT
  }

  companion object {
    const val OPCODE: Int = 0b100011

    // Address value that we read from input
    const val STD_INPUT: Long = 0xffff0004
  }
}

class StoreWordInstruction(doubleWord: Int) : DataInstruction("sw", doubleWord, OPCODE, null) {

  /**
   * MEM [$s + i]:4 = $t
   *
   * Load register t into memory address s + i. If address is STD_OUTPUT then print
   */
  override fun execute(
    context: ExecutionContext,
  ) {
    val baseAddress = context.getReg(registerS).toUInt().toLong()
    val finalAddress = baseAddress + immediate
    val valT = context.getReg(registerT)

    // Covers edge cases - must be POS and % 4.

    if (finalAddress == STD_OUTPUT) {
      // We want to print the least sig byte to stdout
      val leastSigByte: Byte = valT.toByte()
      print(leastSigByte.toInt().toChar()) // Cast it back to a char
    } else {
      val saveAddress = Address().shiftBytes(finalAddress.toInt())
      // Update the value at memory index
      context.updateMem(saveAddress, valT)
    }
  }

  companion object {
    const val OPCODE: Int = 0b101011

    // Address value that we print to output
    const val STD_OUTPUT: Long = 0xffff000c
  }
}

/**
End Load/Save word instructions

Rest of single register instructions below
 */

class MoveHighInstruction(doubleWord: Int) : MipsInstruction(doubleWord, OPCODE, OPERAND) {
  override fun getSyntax(): String {
    return "mfhi $$registerDestination"
  }

  /**
   * Move the value from HI to register $d
   *
   * $d = hi
   */
  override fun execute(
    context: ExecutionContext,
  ) {
    val hi = context.getReg(Registers.HI_INDEX)
    context.updateReg(registerDestination, hi)
  }

  companion object {
    const val OPCODE: Int = 0
    const val OPERAND: Int = (0b00000010000)
  }

  init {
    // Verify that first 16 digits are 0
    if (opcode != 0 || registerS != 0 || registerT != 0) {
      throw BadCodeException(
        "Move High Instruction",
      )
    }
  }
}

class MoveLowInstruction(doubleWord: Int) : MipsInstruction(doubleWord, OPCODE, OPERAND) {
  override fun getSyntax(): String {
    return "mflo $$registerDestination"
  }

  /**
   * Move the value from LO to register $d
   *
   * $d = hi
   */
  override fun execute(
    context: ExecutionContext,
  ) {
    val lo = context.getReg(Registers.LO_INDEX)
    context.updateReg(registerDestination, lo)
  }

  companion object {
    const val OPCODE: Int = 0
    const val OPERAND: Int = (0b00000010010)
  }

  init {
    // Verify that first 16 digits are 0
    if (opcode != 0 || registerS != 0 || registerT != 0) {
      throw BadCodeException(
        "Move Low Instruction",
      )
    }
  }
}

class LisInstruction(doubleWord: Int) : MipsInstruction(doubleWord, null, OPERAND) {

  override fun getSyntax(): String {
    return "lis $$registerDestination"
  }

  /**
   * Save the value into $d register from the next PC address word
   *
   * $d = MEM \[pc]; pc = pc + 4
   *
   */
  override fun execute(
    context: ExecutionContext,
  ) {
    context.setPC { currentPC ->
      // By the Fetch-Execute Cycle, current PC is the next instruction
      val nextWord: Int = context.getMem(currentPC)
      context.updateReg(registerDestination, nextWord)

      currentPC + 1 // Increase it by 1 * number of 4 bytes = 1
    }
  }

  companion object {

    const val OPERAND: Int = (0b00000010100)
  }

  init {
    // Verify that first 16 digits are 0
    if (opcode != 0 || registerS != 0 || registerT != 0) throw BadCodeException("Lis Instruction")
  }
}

class JumpInstruction(doubleWord: Int) : MipsInstruction(doubleWord, OPCODE, OPERAND) {
  override fun getSyntax(): String {
    return "jr $$registerS"
  }

  /**
   * Jumps to the address stored in $s
   *
   * pc = $s
   */
  override fun execute(
    context: ExecutionContext,
  ) {
    val jumpToValue = context.getReg(registerS)

    // The address class will verify it's divisible by four
    val jumpToAddress = Address(jumpToValue.toUInt())

    context.setPC { jumpToAddress }
  }

  companion object {
    const val OPCODE: Int = 0
    const val OPERAND: Int = (0b00000001000)
  }

  init {
    // Verify bit 12-22 are all 0
    if (registerT != 0 || registerDestination != 0) throw BadCodeException("Jump instruction")
  }
}

class JumpAndLinkInstruction(doubleWord: Int) : MipsInstruction(doubleWord, OPCODE, OPERAND) {
  override fun getSyntax(): String {
    return "jalr $$registerS"
  }

  /**
   * Jump to the address in $s while storing the current PC into $31
   *
   * temp = $s; $31 = pc; pc = temp
   */
  override fun execute(
    context: ExecutionContext,
  ) {
    val jumpToValue = context.getReg(registerS)

    // The address class will verify it's divisible by four
    val jumpToAddress = Address(jumpToValue.toUInt())

    context.setPC { currentPC ->
      // Want to link the current PC to $31
      context.updateReg(Registers.JUMP_REGISTER, currentPC.getAddressBits())

      jumpToAddress // Return the jumpToAddress
    }
  }

  companion object {
    const val OPCODE: Int = 0
    const val OPERAND: Int = (0b00000001001)
  }

  init {
    // Verify bit 12-22 are all 0
    if (registerT != 0 || registerDestination != 0) throw BadCodeException("Jump instruction")
  }
}

class WordInstruction(doubleWord: Int) : MipsInstruction(doubleWord, null, null) {
  override fun getSyntax(): String {
    return ".word 0x${Integer.toHexString(word32).padStart(8, '0')}"
  }

  /**
   * A word that isn't an instruction has no instruction and won't run.
   *
   * Throws an error
   */
  override fun execute(
    context: ExecutionContext,
  ) {
    throw WordWithNoInstructionException(this)
  }
}
