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

class CodeGenVisitor : Arm64AsmBaseVisitor<Unit>() {

  private val instructions = ArrayList<Arm64Instruction>()

  val machineCode: ByteArray
    get() {
      val buffer = ByteBuffer.allocate(instructions.size * 4)
        .order(ByteOrder.LITTLE_ENDIAN)

      for (instr in instructions) {
        buffer.putInt(instr.encode())
      }
      return buffer.array()
    }

  // ---------- Visit Program Entry ----------
  override fun visitProgram(ctx: Arm64AsmParser.ProgramContext) {
    for (line in ctx.line()) {
      visit(line)
    }

    ctx.lastline()?.let {
      visit(it)
    }
  }

  // ---------- Visit Line and Lastline ----------
  override fun visitLine(ctx: Arm64AsmParser.LineContext) {
    if (ctx.statement() != null) {
      visit(ctx.statement())
    }
  }

  override fun visitLastline(ctx: Arm64AsmParser.LastlineContext) {
    if (ctx.statement() != null) {
      visit(ctx.statement())
    }
  }
  override fun visitAdd3(ctx: Arm64AsmParser.Add3Context) {
    val rd = parseReg(ctx.reg(0).text)
    val rn = parseReg(ctx.reg(1).text)
    val rm = parseReg(ctx.reg(2).text)
    instructions.add(AddInstruction(rd, rn, rm))
  }

  override fun visitSub3(ctx: Arm64AsmParser.Sub3Context) {
    val rd = parseReg(ctx.reg(0).text)
    val rn = parseReg(ctx.reg(1).text)
    val rm = parseReg(ctx.reg(2).text)
    instructions.add(SubInstruction(rd, rn, rm))
  }

  override fun visitMul3(ctx: Arm64AsmParser.Mul3Context) {
    val rd = parseReg(ctx.reg(0).text)
    val rn = parseReg(ctx.reg(1).text)
    val rm = parseReg(ctx.reg(2).text)
    instructions.add(MulInstruction(rd, rn, rm))
  }

  override fun visitSmulh3(ctx: Arm64AsmParser.Smulh3Context) {
    val rd = parseReg(ctx.reg(0).text)
    val rn = parseReg(ctx.reg(1).text)
    val rm = parseReg(ctx.reg(2).text)
    instructions.add(SmulhInstruction(rd, rn, rm))
  }

  override fun visitUmulh3(ctx: Arm64AsmParser.Umulh3Context) {
    val rd = parseReg(ctx.reg(0).text)
    val rn = parseReg(ctx.reg(1).text)
    val rm = parseReg(ctx.reg(2).text)
    instructions.add(UmulhInstruction(rd, rn, rm))
  }

  override fun visitSdiv3(ctx: Arm64AsmParser.Sdiv3Context) {
    val rd = parseReg(ctx.reg(0).text)
    val rn = parseReg(ctx.reg(1).text)
    val rm = parseReg(ctx.reg(2).text)
    instructions.add(SdivInstruction(rd, rn, rm))
  }

  override fun visitUdiv3(ctx: Arm64AsmParser.Udiv3Context) {
    val rd = parseReg(ctx.reg(0).text)
    val rn = parseReg(ctx.reg(1).text)
    val rm = parseReg(ctx.reg(2).text)
    instructions.add(UdivInstruction(rd, rn, rm))
  }

  override fun visitCmpInstr(ctx: Arm64AsmParser.CmpInstrContext) {
    val rn = parseReg(ctx.reg(0).text)
    val rm = parseReg(ctx.reg(1).text)
    instructions.add(CmpInstruction(rn, rm))
  }

  override fun visitBrReg(ctx: Arm64AsmParser.BrRegContext) {
    val rn = parseReg(ctx.reg().text)
    instructions.add(BrInstruction(rn))
  }

  override fun visitBlrReg(ctx: Arm64AsmParser.BlrRegContext) {
    val rn = parseReg(ctx.reg().text)
    instructions.add(BlrInstruction(rn))
  }

  override fun visitLdurMem(ctx: Arm64AsmParser.LdurMemContext) {
    val rd = parseReg(ctx.reg(0).text)
    val rn = parseReg(ctx.reg(1).text)
    val imm = parseImmediate(ctx.imm().text).toInt()
    instructions.add(LdurInstruction(rd, rn, imm))
  }

  override fun visitSturMem(ctx: Arm64AsmParser.SturMemContext) {
    val rt = parseReg(ctx.reg(0).text)
    val rn = parseReg(ctx.reg(1).text)
    val imm = parseImmediate(ctx.imm().text).toInt()
    instructions.add(SturInstruction(rt, rn, imm))
  }

  override fun visitLdrPc(ctx: Arm64AsmParser.LdrPcContext) {
    val rt = parseReg(ctx.reg().text)
    // Note: ctx.addr() is used here based on PrettyVisitor structure
    val imm = parseImmediate(ctx.addr().text).toInt()
    instructions.add(LdrPcInstruction(rt, imm))
  }

  override fun visitBImm(ctx: Arm64AsmParser.BImmContext) {
    // Branch offset is in instructions (words), so divide by 4
    val imm = (parseImmediate(ctx.addr().text).toInt()) / 4
    instructions.add(BInstruction(imm))
  }

  override fun visitBCondDot(ctx: Arm64AsmParser.BCondDotContext) {
    val cond = parseCond(ctx.cond().text)
    val imm = (parseImmediate(ctx.addr().text).toInt()) / 4
    instructions.add(BCondInstruction(cond, imm))
  }

  private fun parseCond(cond: String): Int {
    return when (cond) {
      "eq" -> 0b0000
      "ne" -> 0b0001
      "cs", "hs" -> 0b0010
      "cc", "lo" -> 0b0011
      "mi" -> 0b0100
      "pl" -> 0b0101
      "vs" -> 0b0110
      "vc" -> 0b0111
      "hi" -> 0b1000
      "ls" -> 0b1001
      "ge" -> 0b1010
      "lt" -> 0b1011
      "gt" -> 0b1100
      "le" -> 0b1101
      "al" -> 0b1110
      else -> throw IllegalArgumentException("Unknown condition: $cond")
    }
  }

  companion object {


    internal fun parseReg(regName: String): Int {
      if (regName == "xzr") return 31
      if (regName == "sp") return 31
      // Remove 'x' prefix and parse as integer
      return regName.substring(1).toInt()
    }

    internal fun parseImmediate(imm: String): Long {
      if (imm.startsWith("0x") || imm.startsWith("0X")) {
        return imm.substring(2).toLong(16)
      }
      return imm.toLong()
    }
  }
}
