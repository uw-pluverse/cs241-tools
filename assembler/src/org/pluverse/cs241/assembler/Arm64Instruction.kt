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
package org.pluverse.cs241.assembler

import java.nio.ByteBuffer
import java.nio.ByteOrder

abstract class Arm64Instruction {
  abstract fun encode(): Int
}

abstract class AbstractEncodedInstruction : Arm64Instruction() {
  protected fun intToBytes(value: Int): ByteArray {
    return ByteBuffer.allocate(4)
      .order(ByteOrder.LITTLE_ENDIAN)
      .putInt(value)
      .array()
  }
}

/**
 * Base class for instructions with layout: Opcode(11) | Rm(5) | Flags(6) | Rn(5) | Rd(5).
 *
 * @param opcode The 11-bit opcode field (bits 31..21) identifying the instruction encoding.
 *               Use binary/hex literals to show the fixed opcode bits for each instruction class.
 * @param flags  The 6-bit flags/sub-opcode field (bits 15..10) that differentiate variants.
 * @param rm     The source register Rm (5 bits).
 * @param rn     The source register Rn (5 bits).
 * @param rd     The destination register Rd (5 bits).
 */
abstract class DataProcessingInstruction(
  val opcode: Int,
  val flags: Int,
  val rm: Int,
  val rn: Int,
  val rd: Int,
) : Arm64Instruction() {

  override fun encode(): Int {
    return (opcode shl 21) or (rm shl 16) or (flags shl 10) or (rn shl 5) or rd
  }
}

// Three Register Instructions: op rd, rn, rm
abstract class ThreeRegisterInstruction(
  opcode: Int,
  flags: Int,
  rd: Int,
  rn: Int,
  rm: Int,
) : DataProcessingInstruction(opcode, flags, rm, rn, rd)

class AddInstruction(rd: Int, rn: Int, rm: Int) :
  ThreeRegisterInstruction(opcode = 0b10001011001, flags = 0b011000, rd, rn, rm)

class SubInstruction(rd: Int, rn: Int, rm: Int) :
  ThreeRegisterInstruction(opcode = 0b11001011001, flags = 0b011000, rd, rn, rm)

class MulInstruction(rd: Int, rn: Int, rm: Int) :
  ThreeRegisterInstruction(opcode = 0b10011011000, flags = 0b011111, rd, rn, rm)

class SmulhInstruction(rd: Int, rn: Int, rm: Int) :
  ThreeRegisterInstruction(opcode = 0b10011011010, flags = 0b011111, rd, rn, rm)

class UmulhInstruction(rd: Int, rn: Int, rm: Int) :
  ThreeRegisterInstruction(opcode = 0b10011011110, flags = 0b011111, rd, rn, rm)

class SdivInstruction(rd: Int, rn: Int, rm: Int) :
  ThreeRegisterInstruction(opcode = 0b10011010110, flags = 0b000011, rd, rn, rm)

class UdivInstruction(rd: Int, rn: Int, rm: Int) :
  ThreeRegisterInstruction(opcode = 0b10011010110, flags = 0b000010, rd, rn, rm)

class CmpInstruction(rn: Int, rm: Int) :
  DataProcessingInstruction(opcode = 0b11101011001, flags = 0b011000, rm, rn, rd = 31)

class BrInstruction(rn: Int) :
  DataProcessingInstruction(opcode = 0b11010110000, flags = 0b000000, rm = 31, rn, rd = 0)

class BlrInstruction(rn: Int) :
  DataProcessingInstruction(opcode = 0b11010110001, flags = 0b000000, rm = 31, rn, rd = 0)



abstract class MemInstruction(
    val opcode: Int,
    val fixedBits: Int, 
    val imm: Int,
    val rn: Int,
    val rt: Int
) : Arm64Instruction() {

    override fun encode(): Int {
        // Opcode is usually bits 31..21
        // Imm is usually bits 20..12 (9 bits)
        // FixedBits (option) usually bits 11..10
        // Rn usually bits 9..5
        // Rt usually bits 4..0
        val imm9 = imm and 0x1FF
        return (opcode shl 21) or (imm9 shl 12) or (fixedBits shl 10) or (rn shl 5) or rt
    }
}

class LdurInstruction(rt: Int, rn: Int, imm: Int) :
    MemInstruction(opcode = 0b11111000010, fixedBits = 0b00, imm, rn, rt)

class SturInstruction(rt: Int, rn: Int, imm: Int) :
    MemInstruction(opcode = 0b11111000000, fixedBits = 0b00, imm, rn, rt)

/**
 * Layout: Opcode(8) | Imm(19) | Rt(5)
 */
class LdrPcInstruction(val rt: Int, val imm: Int) : Arm64Instruction() {
    override fun encode(): Int {
        val opcode = 0b01011000 shl 24
        val imm19 = imm and 0x7FFFF
        return opcode or (imm19 shl 5) or rt
    }
}

/**
 * Layout: Opcode(6) | Imm(26)
 */
class BInstruction(val imm: Int) : Arm64Instruction() {
    override fun encode(): Int {
        val opcode = 0b000101 shl 26
        val imm26 = imm and 0x3FFFFFF
        return opcode or imm26
    }
}

/**
 * Layout: Opcode(8) | Imm(19) | 0(1) | Cond(4)
 */
class BCondInstruction(val cond: Int, val imm: Int) : Arm64Instruction() {
    override fun encode(): Int {
        val opcode = 0b01010100 shl 24
        val imm19 = imm and 0x7FFFF
        return opcode or (imm19 shl 5) or cond
    }
}