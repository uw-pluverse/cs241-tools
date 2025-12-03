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
This is a general data type for the Memory class.
Stores general info about the register and/or mips instruction such as the details of the object.

 */
interface MemoryData {

  val word32: Int
  val address: Address

  operator fun invoke(): Any = word32 // Get the word32
  override fun toString(): String // Get the details of the object

  fun getBinary(): String // Get the binary printout of word32
  fun getHex(): String // Get the hexadecimal printout of word32
  fun getPrefixTag(): String // Used as an identifier for the MemoryData i.e. by address
  fun getDetails(): String // Print additional info and the binary and hex

  companion object {
    // Static methods for conversions
    @JvmStatic
    fun convertFourByteToInteger(c1: Int, c2: Int, c3: Int, c4: Int): Int {
      return (
        (c1 and BYTE_BITS shl 24)
          or (c2 and BYTE_BITS shl 16) or (c3 and BYTE_BITS shl 8) or (c4 and BYTE_BITS)
        )
    }

    @JvmStatic
    fun convertFourByteToInteger(b1: Byte, b2: Byte, b3: Byte, b4: Byte): Int {
      return convertFourByteToInteger(b1.toInt(), b2.toInt(), b3.toInt(), b4.toInt())
    }

    const val BYTE_BITS = 0xFF
    const val FIVE_BITS = 0x1F
    const val SIX_BITS = 0x3F
    const val ELEVEN_BITS = 0x7FF
    const val FIFTEEN_BITS = 0x7FFF
    const val SIXTEEN_BITS = 0xFFFF
  }
}

/**
This implements similar functions from MemoryData for Mips and Register Data

 */
abstract class EmulatorMemoryData(
  override var word32: Int,
  override val address: Address,
) : MemoryData {

  constructor(address: Address) : this(0, address)

  // Returns a binary string, padded to 32 bits
  override fun getBinary(): String = Integer.toBinaryString(
    word32,
  ).padStart(Int.SIZE_BITS, '0')

  // Returns a hex string of the word32
  override fun getHex(): String = getHex(word32)

  // Get the details
  override fun toString(): String = getDetails()

  fun update(word32: Int) {
    // Use the custom set methods to modify it
    // This is for better semantics

    this.word32 = word32
  }

  companion object {
    @JvmStatic
    fun getHex(word32: Int): String {
      return "0x${Integer.toHexString(
        word32,
      ).padStart(Memory.DOUBLE_WORD_HEX_LENGTH, '0')}"
    }
  }
}

/**
Class to represent a register instruction. Just a straight implementation
 */

class RegisterData(private val registerName: Int) : EmulatorMemoryData(
  Address(registerName.toUInt() * 4u),
) {

  override operator fun invoke(): Int = word32

  /**
   * We want to only mutate word32 if it's a non-zero register
   */
  override var word32: Int
    get() = super.word32
    set(value) {
      // Check if it's index 0
      if (registerName != 0) super.word32 = value
    }

  override fun getPrefixTag(): String {
    if (registerName == Registers.HI_INDEX) return "\$HI"
    if (registerName == Registers.LO_INDEX) return "\$LO"

    return "$$registerName"
  }

  override fun getDetails(): String {
    return "<Register $$registerName>${if (registerName < 10) " " else ""} : ${getBinary()}"
  }
}

/**
General info for the mips instructions and allows for mutation of CpuEmulator

Note: CpuEmulator is the Controller, Memory is the Model, Views TBD.
 MipsInstruction(s) run can mutate the Memory(s).

 */
class RamMemoryData(
  word32: Int,
  override val address: Address,
) : EmulatorMemoryData(word32, address) {

  override var word32: Int
    get() = super.word32
    set(value) {
      super.word32 = value
      instruction = getMipsInstruction(value)
    }

  var instruction: MipsInstruction = getMipsInstruction(word32)
    private set

  override operator fun invoke(): MipsInstruction = instruction

  constructor(address: Address) : this(0, address)

  override fun getPrefixTag(): String {
    return "M[${address.toHexStringSimple()}]"
  }

  override fun getDetails(): String {
    return instruction.getSyntax()
  }

  companion object {
    /**
     Method to filter a word32 and return the correct MipsInstructionData
     */
    @JvmStatic
    fun getMipsInstruction(word32: Int): MipsInstruction {
      val opcode: Int = (word32 shr 26) and MemoryData.SIX_BITS // First 6 digits
      val operand: Int = word32 and MemoryData.ELEVEN_BITS // Last 11 digits

      // these are the register values
      val regS = (word32 shr 21) and MemoryData.FIVE_BITS
      val regT = (word32 shr 16) and MemoryData.FIVE_BITS
      val regD = (word32 shr 11) and MemoryData.FIVE_BITS
      val defaultRegisterOpCode = 0b000000

      // Check edge cases first, mfhi, mflo, lis, jr, jalr
      if (opcode == MoveHighInstruction.OPCODE && regT == 0) {
        if (regS == 0) {
          if (operand == MoveHighInstruction.OPERAND) {
            return MoveHighInstruction(word32)
          } else if (operand == MoveLowInstruction.OPERAND) {
            return MoveLowInstruction(word32)
          } else if (operand == LisInstruction.OPERAND) return LisInstruction(word32)
        } else if (regD == 0) {
          if (operand == JumpInstruction.OPERAND) {
            return JumpInstruction(word32)
          } else if (operand == JumpAndLinkInstruction.OPERAND) {
            return JumpAndLinkInstruction(
              word32,
            )
          }
        }
      }

      return when (opcode) {
        defaultRegisterOpCode -> when (operand) {
          AddInstruction.OPERAND -> AddInstruction(word32)
          SubInstruction.OPERAND -> SubInstruction(word32)
          MultiplyInstruction.OPERAND -> MultiplyInstruction(word32)
          MultiplyUInstruction.OPERAND -> MultiplyUInstruction(word32)
          DivideInstruction.OPERAND -> DivideInstruction(word32)
          DivideUInstruction.OPERAND -> DivideUInstruction(word32)
          SetLessThanInstruction.OPERAND -> SetLessThanInstruction(word32)
          SetLessThanUInstruction.OPERAND -> SetLessThanUInstruction(word32)
          else -> WordInstruction(word32)
        }
        LoadWordInstruction.OPCODE -> LoadWordInstruction(word32)
        StoreWordInstruction.OPCODE -> StoreWordInstruction(word32)
        BranchEqualInstruction.OPCODE -> BranchEqualInstruction(word32)
        BranchNotEqualInstruction.OPCODE -> BranchNotEqualInstruction(word32)
        else -> WordInstruction(opcode)
      }
    }
  }
}
