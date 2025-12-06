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

class PrettyVisitor : Arm64AsmBaseVisitor<Void?>() {
  private var indent = 0

  private fun printWithIndent(s: String) {
    println("  ".repeat(indent) + s)
  }

  private fun withIndent(block: () -> Unit): Void? {
    indent++
    try {
      block()
    } finally {
      indent--
    }
    return null
  }

  // program : line* lastline? EOF
  override fun visitProgram(ctx: Arm64AsmParser.ProgramContext): Void? {
    printWithIndent("program")
    return withIndent {
      // visit lines with NEWLINE
      for (lineCtx in ctx.line()) {
        visit(lineCtx)
      }
      // deal with lastline if exists
      ctx.lastline()?.let { visit(it) }
    }
  }

  // line : labels? statement? NEWLINE
  override fun visitLine(ctx: Arm64AsmParser.LineContext): Void? {
    printLabelsAndStatement(ctx.labels(), ctx.statement())
    return null
  }

  // lastline
  //   : labels statement?
  //   | statement
  override fun visitLastline(ctx: Arm64AsmParser.LastlineContext): Void? {
    printLabelsAndStatement(ctx.labels(), ctx.statement())
    return null
  }

  /**
   * print labels and statement in a line or lastline
   */
  private fun printLabelsAndStatement(
    labelsCtx: Arm64AsmParser.LabelsContext?,
    stmtCtx: Arm64AsmParser.StatementContext?,
  ) {
    if (labelsCtx != null) {
      val defs = labelsCtx.labelDef()
      val sb = StringBuilder("labels:")
      for (d in defs) {
        sb.append(" ").append(d.LABEL_ID().text)
      }
      printWithIndent(sb.toString())
    }

    if (stmtCtx != null) {
      visit(stmtCtx)
    } else if (labelsCtx != null) {
      // there are labels but no statement
      withIndent {
        printWithIndent("(no instruction on this line)")
      }
    }
    // both labelsCtx and stmtCtx can be null (empty line)
  }

  // ---------- arith3 ----------

  override fun visitAdd3(ctx: Arm64AsmParser.Add3Context): Void? {
    printWithIndent("add")
    return withIndent {
      printWithIndent("rd = " + ctx.reg(0).text)
      printWithIndent("rn = " + ctx.reg(1).text)
      printWithIndent("rm = " + ctx.reg(2).text)
    }
  }

  override fun visitSub3(ctx: Arm64AsmParser.Sub3Context): Void? {
    printWithIndent("sub")
    return withIndent {
      printWithIndent("rd = " + ctx.reg(0).text)
      printWithIndent("rn = " + ctx.reg(1).text)
      printWithIndent("rm = " + ctx.reg(2).text)
    }
  }

  override fun visitMul3(ctx: Arm64AsmParser.Mul3Context): Void? {
    printWithIndent("mul")
    return withIndent {
      printWithIndent("rd = " + ctx.reg(0).text)
      printWithIndent("rn = " + ctx.reg(1).text)
      printWithIndent("rm = " + ctx.reg(2).text)
    }
  }

  override fun visitSmulh3(ctx: Arm64AsmParser.Smulh3Context): Void? {
    printWithIndent("smulh")
    return withIndent {
      printWithIndent("rd = " + ctx.reg(0).text)
      printWithIndent("rn = " + ctx.reg(1).text)
      printWithIndent("rm = " + ctx.reg(2).text)
    }
  }

  override fun visitUmulh3(ctx: Arm64AsmParser.Umulh3Context): Void? {
    printWithIndent("umulh")
    return withIndent {
      printWithIndent("rd = " + ctx.reg(0).text)
      printWithIndent("rn = " + ctx.reg(1).text)
      printWithIndent("rm = " + ctx.reg(2).text)
    }
  }

  override fun visitSdiv3(ctx: Arm64AsmParser.Sdiv3Context): Void? {
    printWithIndent("sdiv")
    return withIndent {
      printWithIndent("rd = " + ctx.reg(0).text)
      printWithIndent("rn = " + ctx.reg(1).text)
      printWithIndent("rm = " + ctx.reg(2).text)
    }
  }

  override fun visitUdiv3(ctx: Arm64AsmParser.Udiv3Context): Void? {
    printWithIndent("udiv")
    return withIndent {
      printWithIndent("rd = " + ctx.reg(0).text)
      printWithIndent("rn = " + ctx.reg(1).text)
      printWithIndent("rm = " + ctx.reg(2).text)
    }
  }

  // ---------- cmp2 ----------

  override fun visitCmpInstr(ctx: Arm64AsmParser.CmpInstrContext): Void? {
    printWithIndent("cmp")
    return withIndent {
      printWithIndent("rn = " + ctx.reg(0).text)
      printWithIndent("rm = " + ctx.reg(1).text)
    }
  }

  // ---------- mem: ldur/stur ----------

  override fun visitLdurMem(ctx: Arm64AsmParser.LdurMemContext): Void? {
    printWithIndent("ldur")
    return withIndent {
      printWithIndent("rt  = " + ctx.reg(0).text)
      printWithIndent("rn  = " + ctx.reg(1).text)
      printWithIndent("imm = " + ctx.imm().text)
    }
  }

  override fun visitSturMem(ctx: Arm64AsmParser.SturMemContext): Void? {
    printWithIndent("stur")
    return withIndent {
      printWithIndent("rt  = " + ctx.reg(0).text)
      printWithIndent("rn  = " + ctx.reg(1).text)
      printWithIndent("imm = " + ctx.imm().text)
    }
  }

  // ---------- ldr_pc: LDR reg, addr ----------

  override fun visitLdrPc(ctx: Arm64AsmParser.LdrPcContext): Void? {
    printWithIndent("ldr (pc-relative)")
    return withIndent {
      printWithIndent("rt   = " + ctx.reg().text)
      printWithIndent("addr = " + addrToString(ctx.addr()))
    }
  }

  // ---------- branches ----------

  override fun visitBImm(ctx: Arm64AsmParser.BImmContext): Void? {
    printWithIndent("b")
    return withIndent {
      printWithIndent("addr = " + addrToString(ctx.addr()))
    }
  }

  override fun visitBrReg(ctx: Arm64AsmParser.BrRegContext): Void? {
    printWithIndent("br")
    return withIndent {
      printWithIndent("rn = " + ctx.reg().text)
    }
  }

  override fun visitBlrReg(ctx: Arm64AsmParser.BlrRegContext): Void? {
    printWithIndent("blr")
    return withIndent {
      printWithIndent("rn = " + ctx.reg().text)
    }
  }

  override fun visitBCondDot(ctx: Arm64AsmParser.BCondDotContext): Void? {
    printWithIndent("b.cond") // b . cond addr
    return withIndent {
      printWithIndent("cond = " + ctx.cond().text)
      printWithIndent("addr = " + addrToString(ctx.addr()))
    }
  }

  override fun visitBCondPlain(ctx: Arm64AsmParser.BCondPlainContext): Void? {
    printWithIndent("b.cond") // b cond addr
    return withIndent {
      printWithIndent("cond = " + ctx.cond().text)
      printWithIndent("addr = " + addrToString(ctx.addr()))
    }
  }

  // ---------- directive .8byte ----------

  override fun visitDir8Byte(ctx: Arm64AsmParser.Dir8ByteContext): Void? {
    printWithIndent(".8byte")
    return withIndent {
      printWithIndent("addr = " + addrToString(ctx.addr()))
    }
  }

  // ---------- addr: imm | LABEL_ID ----------

  private fun addrToString(ctx: Arm64AsmParser.AddrContext): String {
    return when (ctx) {
      is Arm64AsmParser.AddrImmContext -> ctx.imm().text
      is Arm64AsmParser.AddrLabelContext -> ctx.LABEL_ID().text
      else -> ctx.text // should not reach here
    }
  }

  // ---------- default ----------

  override fun defaultResult(): Void? = null

  override fun aggregateResult(aggregate: Void?, nextResult: Void?): Void? = null
}
