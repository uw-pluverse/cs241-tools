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

/**
An abstract class that provides functionality for an instruction based on the given doubleWord

 */
abstract class MipsInstruction(val doubleWord: Int, expectedOpcode: Int?, expectedOperand: Int?) {

  /**
   Important information about the MipsInstruction
   */

  // opcode: the first 6 bits - identifies the type
  val opcode: Int = (doubleWord shr 26) and MemoryData.SIX_BITS

  // operand: the last 11 bits - identifies the specific operation
  val operand: Int = doubleWord and MemoryData.ELEVEN_BITS

  // these are the register values. 5 digit values
  val regS = (doubleWord shr 21) and MemoryData.FIVE_BITS
  val regT = (doubleWord shr 16) and MemoryData.FIVE_BITS
  val regD = (doubleWord shr 11) and MemoryData.FIVE_BITS

  // immediate value - we want it to be in two's complement - 16 bits
  val immediate = doubleWord.toShort().toInt()

  abstract fun getSyntax(): String // Returns the mips syntax for the operation
  abstract fun execute(
    getReg: (index: Int) -> Int,
    getMem: (address: Address) -> Int,
    updateReg: (index: Int, value: Int) -> Unit,
    updateMem: (address: Address, value: Int) -> Unit,
    setPC: (init: (currentPC: Address) -> Address) -> Unit,
  ) // This will run the instruction and update memory

  init {
    // Verify the opcode and operand is correct
    if ((expectedOpcode != null && opcode != expectedOpcode) ||
      (expectedOperand != null && operand != expectedOperand)
    ) {
      throw WrongMipsInstructionException()
    }
  }
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
    return "$identifier $$regD, $$regS, $$regT"
  }
}

class AddInstruction(doubleWord: Int) : ThreeRegisterInstruction(
  "add",
  doubleWord,
  OPCODE,
  OPERAND,
) {

  override fun execute(
    getReg: (index: Int) -> Int,
    getMem: (index: Address) -> Int,
    updateReg: (index: Int, value: Int) -> Unit,
    updateMem: (address: Address, value: Int) -> Unit,
    setPC: (init: (currentPC: Address) -> Address) -> Unit,
  ) {
    // $d = $s + $t
    val computedValue = getReg(regS) + getReg(regT)
    updateReg(regD, computedValue)
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
    getReg: (index: Int) -> Int,
    getMem: (index: Address) -> Int,
    updateReg: (index: Int, value: Int) -> Unit,
    updateMem: (address: Address, value: Int) -> Unit,
    setPC: (init: (currentPC: Address) -> Address) -> Unit,
  ) {
    // $d = $s - $t
    val computedValue = getReg(regS) - getReg(regT)
    updateReg(regD, computedValue)
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
    getReg: (index: Int) -> Int,
    getMem: (index: Address) -> Int,
    updateReg: (index: Int, value: Int) -> Unit,
    updateMem: (address: Address, value: Int) -> Unit,
    setPC: (init: (currentPC: Address) -> Address) -> Unit,
  ) {
    // $d = 1 if $s < $t; 0 otherwise
    val valS: Int = getReg(regS)
    val valT: Int = getReg(regT)

    val isLessThan = 1 // Value if it's less than
    val isNotLessThan = 0

    if (valS < valT) updateReg(regD, isLessThan) else updateReg(regD, isNotLessThan)
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
    getReg: (index: Int) -> Int,
    getMem: (index: Address) -> Int,
    updateReg: (index: Int, value: Int) -> Unit,
    updateMem: (address: Address, value: Int) -> Unit,
    setPC: (init: (currentPC: Address) -> Address) -> Unit,
  ) {
    // $d = 1 if $s < $t; 0 otherwise
    // WE WANT ONLY THE UNSIGNED INT
    val valS: UInt = getReg(regS).toUInt()
    val valT: UInt = getReg(regT).toUInt()

    val isLessThan = 1 // Value if it's less than
    val isNotLessThan = 0

    if (valS < valT) updateReg(regD, isLessThan) else updateReg(regD, isNotLessThan)
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
    return "$identifier $$regS, $$regT"
  }

  init {
    // Verify bit 12-22 are all 0
    if (regD != 0) throw BadCodeException(identifier)
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
    getReg: (index: Int) -> Int,
    getMem: (index: Address) -> Int,
    updateReg: (index: Int, value: Int) -> Unit,
    updateMem: (address: Address, value: Int) -> Unit,
    setPC: (init: (currentPC: Address) -> Address) -> Unit,
  ) {
    // hi:lo = $s * $t
    val valS: Int = getReg(regS)
    val valT: Int = getReg(regT)

    // HI = 32 largest bits, LO = 32 Smallest bits of product
    val product: Long = valS.toLong() * valT.toLong()

    // Long.toInt() takes the 32 smallest bits
    val hi: Int = (product shr 32).toInt() // Take 32 most significant
    val lo: Int = product.toInt() // Take 32 least significant

    updateReg(Registers.HI_INDEX, hi)
    updateReg(Registers.LO_INDEX, lo)
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
    getReg: (index: Int) -> Int,
    getMem: (index: Address) -> Int,
    updateReg: (index: Int, value: Int) -> Unit,
    updateMem: (address: Address, value: Int) -> Unit,
    setPC: (init: (currentPC: Address) -> Address) -> Unit,
  ) {
    // hi:lo = $s * $t
    // ** Cast them to Unsigned prior to multiplication **
    val valS: UInt = getReg(regS).toUInt()
    val valT: UInt = getReg(regT).toUInt()

    // HI = 32 largest bits, LO = 32 Smallest bits of product
    val product: Long = valS.toLong() * valT.toLong()

    // Long.toInt() takes the 32 smallest bits
    val hi: Int = (product shr 32).toInt() // Take 32 most significant
    val lo: Int = product.toInt() // Take 32 least significant

    updateReg(Registers.HI_INDEX, hi)
    updateReg(Registers.LO_INDEX, lo)
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
    getReg: (index: Int) -> Int,
    getMem: (index: Address) -> Int,
    updateReg: (index: Int, value: Int) -> Unit,
    updateMem: (address: Address, value: Int) -> Unit,
    setPC: (init: (currentPC: Address) -> Address) -> Unit,
  ) {
    val valS: Int = getReg(regS)
    val valT: Int = getReg(regT)

    val lo = valS / valT
    val hi = valS % valT

    updateReg(Registers.HI_INDEX, hi)
    updateReg(Registers.LO_INDEX, lo)
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
    getReg: (index: Int) -> Int,
    getMem: (index: Address) -> Int,
    updateReg: (index: Int, value: Int) -> Unit,
    updateMem: (address: Address, value: Int) -> Unit,
    setPC: (init: (currentPC: Address) -> Address) -> Unit,
  ) {
    val valS: UInt = getReg(regS).toUInt()
    val valT: UInt = getReg(regT).toUInt()

    // Need the UInt operations
    // Casting doesn't change the bits - only need bits
    val lo: Int = (valS / valT).toInt()
    val hi: Int = (valS % valT).toInt()

    updateReg(Registers.HI_INDEX, hi)
    updateReg(Registers.LO_INDEX, lo)
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
    return "$identifier $$regS, $$regT, $immediate"
  }
}

class BranchEqualInstruction(doubleWord: Int) : BranchInstruction("beq", doubleWord, OPCODE, null) {

  /**
   * if ($s == $t) pc += i * 4
   *
   * Modify the pc address
   */
  override fun execute(
    getReg: (index: Int) -> Int,
    getMem: (index: Address) -> Int,
    updateReg: (index: Int, value: Int) -> Unit,
    updateMem: (address: Address, value: Int) -> Unit,
    setPC: (init: (currentPC: Address) -> Address) -> Unit,
  ) {
    val valS: Int = getReg(regS)
    val valT: Int = getReg(regT)

    setPC() { currentPC ->
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
    getReg: (index: Int) -> Int,
    getMem: (index: Address) -> Int,
    updateReg: (index: Int, value: Int) -> Unit,
    updateMem: (address: Address, value: Int) -> Unit,
    setPC: (init: (currentPC: Address) -> Address) -> Unit,
  ) {
    val valS: Int = getReg(regS)
    val valT: Int = getReg(regT)

    setPC { currentPC ->
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
    return "$identifier $$regT, $immediate($$regS)"
  }
}

class LoadWordInstruction(doubleWord: Int) : DataInstruction("lw", doubleWord, OPCODE, null) {

  /**
   * $t = MEM [$s + i]:4
   *
   * Read from memory into the $t. We will add the immediate to the valS to get the memory addr.
   */
  override fun execute(
    getReg: (index: Int) -> Int,
    getMem: (index: Address) -> Int,
    updateReg: (index: Int, value: Int) -> Unit,
    updateMem: (address: Address, value: Int) -> Unit,
    setPC: (init: (currentPC: Address) -> Address) -> Unit,
  ) {
    val valS = getReg(regS)

    // Covers edge cases - must be POS and % 4.
    val loadAddress = Address().shiftBytes(valS + immediate)

    // If it's the input address, read in the next address
    val data: Int = if (loadAddress.address.toLong() == STD_INPUT) {
      System.`in`.read()
    } else {
      getMem(
        loadAddress,
      )
    }

    updateReg(regT, data) // Mutate regT
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
    getReg: (index: Int) -> Int,
    getMem: (index: Address) -> Int,
    updateReg: (index: Int, value: Int) -> Unit,
    updateMem: (address: Address, value: Int) -> Unit,
    setPC: (init: (currentPC: Address) -> Address) -> Unit,
  ) {
    val valS: Int = getReg(regS)
    val valT = getReg(regT)

    // Covers edge cases - must be POS and % 4.
    val saveAddress = Address().shiftBytes(immediate + valS)

    if (saveAddress.address.toLong() == STD_OUTPUT) {
      // We want to print the least sig byte to stdout
      val leastSigByte: Byte = valT.toByte()
      print(leastSigByte.toInt().toChar()) // Cast it back to a char
    } else {
      // Update the value at memory index
      updateMem(saveAddress, valT)
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
    return "mfhi $$regD"
  }

  /**
   * Move the value from HI to register $d
   *
   * $d = hi
   */
  override fun execute(
    getReg: (index: Int) -> Int,
    getMem: (index: Address) -> Int,
    updateReg: (index: Int, value: Int) -> Unit,
    updateMem: (address: Address, value: Int) -> Unit,
    setPC: (init: (currentPC: Address) -> Address) -> Unit,
  ) {
    val hi = getReg(Registers.HI_INDEX)
    updateReg(regD, hi)
  }

  companion object {
    const val OPCODE: Int = 0
    const val OPERAND: Int = (0b00000010000)
  }

  init {
    // Verify that first 16 digits are 0
    if (opcode != 0 || regS != 0 || regT != 0) throw BadCodeException("Move High Instruction")
  }
}

class MoveLowInstruction(doubleWord: Int) : MipsInstruction(doubleWord, OPCODE, OPERAND) {
  override fun getSyntax(): String {
    return "mflo $$regD"
  }

  /**
   * Move the value from LO to register $d
   *
   * $d = hi
   */
  override fun execute(
    getReg: (index: Int) -> Int,
    getMem: (index: Address) -> Int,
    updateReg: (index: Int, value: Int) -> Unit,
    updateMem: (address: Address, value: Int) -> Unit,
    setPC: (init: (currentPC: Address) -> Address) -> Unit,
  ) {
    val lo = getReg(Registers.LO_INDEX)
    updateReg(regD, lo)
  }

  companion object {
    const val OPCODE: Int = 0
    const val OPERAND: Int = (0b00000010010)
  }

  init {
    // Verify that first 16 digits are 0
    if (opcode != 0 || regS != 0 || regT != 0) throw BadCodeException("Move Low Instruction")
  }
}

class LisInstruction(doubleWord: Int) : MipsInstruction(doubleWord, null, OPERAND) {

  override fun getSyntax(): String {
    return "lis $$regD"
  }

  /**
   * Save the value into $d register from the next PC address word
   *
   * $d = MEM \[pc]; pc = pc + 4
   *
   */
  override fun execute(
    getReg: (index: Int) -> Int,
    getMem: (index: Address) -> Int,
    updateReg: (index: Int, value: Int) -> Unit,
    updateMem: (address: Address, value: Int) -> Unit,
    setPC: (init: (currentPC: Address) -> Address) -> Unit,
  ) {
    setPC { currentPC ->
      // By the Fetch-Execute Cycle, current PC is the next instruction
      val nextWord: Int = getMem(currentPC)
      updateReg(regD, nextWord)

      currentPC + 1 // Increase it by 1 * number of 4 bytes = 1
    }
  }

  companion object {

    const val OPERAND: Int = (0b00000010100)
  }

  init {
    // Verify that first 16 digits are 0
    if (opcode != 0 || regS != 0 || regT != 0) throw BadCodeException("Lis Instruction")
  }
}

class JumpInstruction(doubleWord: Int) : MipsInstruction(doubleWord, OPCODE, OPERAND) {
  override fun getSyntax(): String {
    return "jr $$regS"
  }

  /**
   * Jumps to the address stored in $s
   *
   * pc = $s
   */
  override fun execute(
    getReg: (index: Int) -> Int,
    getMem: (index: Address) -> Int,
    updateReg: (index: Int, value: Int) -> Unit,
    updateMem: (address: Address, value: Int) -> Unit,
    setPC: (init: (currentPC: Address) -> Address) -> Unit,
  ) {
    val jumpToValue = getReg(regS)

    // The address class will verify it's divisible by four
    val jumpToAddress = Address(jumpToValue.toUInt())

    setPC { jumpToAddress }
  }

  companion object {
    const val OPCODE: Int = 0
    const val OPERAND: Int = (0b00000001000)
  }

  init {
    // Verify bit 12-22 are all 0
    if (regT != 0 || regD != 0) throw BadCodeException("Jump instruction")
  }
}

class JumpAndLinkInstruction(doubleWord: Int) : MipsInstruction(doubleWord, OPCODE, OPERAND) {
  override fun getSyntax(): String {
    return "jalr $$regS"
  }

  /**
   * Jump to the address in $s while storing the current PC into $31
   *
   * temp = $s; $31 = pc; pc = temp
   */
  override fun execute(
    getReg: (index: Int) -> Int,
    getMem: (index: Address) -> Int,
    updateReg: (index: Int, value: Int) -> Unit,
    updateMem: (address: Address, value: Int) -> Unit,
    setPC: (init: (currentPC: Address) -> Address) -> Unit,
  ) {
    val jumpToValue = getReg(regS)

    // The address class will verify it's divisible by four
    val jumpToAddress = Address(jumpToValue.toUInt())

    setPC { currentPC ->
      // Want to link the current PC to $31
      updateReg(Registers.JUMP_REGISTER, currentPC.getAddressBits())

      jumpToAddress // Return the jumpToAddress
    }
  }

  companion object {
    const val OPCODE: Int = 0
    const val OPERAND: Int = (0b00000001001)
  }

  init {
    // Verify bit 12-22 are all 0
    if (regT != 0 || regD != 0) throw BadCodeException("Jump instruction")
  }
}

class WordInstruction(doubleWord: Int) : MipsInstruction(doubleWord, null, null) {
  override fun getSyntax(): String {
    return ".word 0x${Integer.toHexString(doubleWord).padStart(8, '0')}"
  }

  /**
   * A word that isn't an instruction has no instruction and won't run.
   *
   * Throws an error
   */
  override fun execute(
    getReg: (index: Int) -> Int,
    getMem: (index: Address) -> Int,
    updateReg: (index: Int, value: Int) -> Unit,
    updateMem: (address: Address, value: Int) -> Unit,
    setPC: (init: (currentPC: Address) -> Address) -> Unit,
  ) {
    throw WordWithNoInstructionException()
  }
}
