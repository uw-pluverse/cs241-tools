package org.pluverse.cs241.assembler

import java.nio.ByteBuffer
import java.nio.ByteOrder

class CodeGenVisitor : Arm64AsmBaseVisitor<Unit>() {

  private val instructions = ArrayList<Arm64Instruction>()

  /** label -> byte address */
  private val labelTable = mutableMapOf<String, Int>()

  /** program counter in BYTES */
  private var pc = 0

  /** true during label-collection pass */
  private var pass1 = true

  val machineCode: ByteArray
    get() {
      val buffer = ByteBuffer
        .allocate(instructions.size * 4)
        .order(ByteOrder.LITTLE_ENDIAN)

      for (instr in instructions) {
        buffer.putInt(instr.encode())
      }
      return buffer.array()
    }


  override fun visitProgram(ctx: Arm64AsmParser.ProgramContext) {
  pass1 = true
  pc = 0
  for (line in ctx.line()) visit(line)
    ctx.lastline()?.let { visit(it) }

  pass1 = false
  pc = 0
  instructions.clear()
  for (line in ctx.line()) visit(line)
    ctx.lastline()?.let { visit(it) }
}

override fun visitLine(ctx: Arm64AsmParser.LineContext) {
    collectLabels(ctx.labels()) 
    ctx.statement()?.let { visit(it) }
  }

  override fun visitLastline(ctx: Arm64AsmParser.LastlineContext) {
    collectLabels(ctx.labels()) 
    ctx.statement()?.let { visit(it)}
  }

  private fun collectLabels(labelsCtx: Arm64AsmParser.LabelsContext?) { 
    if (!pass1 || labelsCtx == null) return 
    for (def in labelsCtx.labelDef()) { 
      val name = def.LABEL_ID().text 
      if (labelTable.containsKey(name)) { 
        error("Duplicate label: $name") 
        } 
        labelTable[name] = pc 
      } 
    }

  private fun resolveAddr(ctx: Arm64AsmParser.AddrContext): Int =
    when (ctx) {
      is Arm64AsmParser.AddrImmContext ->
        parseImmediate(ctx.imm().text).toInt()

      is Arm64AsmParser.AddrLabelContext -> {
        labelTable[ctx.LABEL_ID().text]
          ?: error("Undefined label: ${ctx.LABEL_ID().text}")
      }

      else -> error("Unknown addr")
    }

  private fun branchOffset(ctx: Arm64AsmParser.AddrContext): Int {
    val targetPC = resolveAddr(ctx)
    return (targetPC - pc) / 4
  }

  
  // ------------------------------------------------------------
  // Instructions
  // ------------------------------------------------------------

  override fun visitAdd3(ctx: Arm64AsmParser.Add3Context) {
    if (!pass1) {
      instructions.add(
        AddInstruction(
          parseReg(ctx.reg(0).text),
          parseReg(ctx.reg(1).text),
          parseReg(ctx.reg(2).text)
        )
      )
    }
    pc += 4
  }

  override fun visitSub3(ctx: Arm64AsmParser.Sub3Context) {
    if (!pass1) {
      instructions.add(
        SubInstruction(
          parseReg(ctx.reg(0).text),
          parseReg(ctx.reg(1).text),
          parseReg(ctx.reg(2).text)
        )
      )
    }
    pc += 4
  }

  override fun visitMul3(ctx: Arm64AsmParser.Mul3Context) {
    if (!pass1) {
      instructions.add(
        MulInstruction(
          parseReg(ctx.reg(0).text),
          parseReg(ctx.reg(1).text),
          parseReg(ctx.reg(2).text)
        )
      )
    }
    pc += 4
  }

  override fun visitSmulh3(ctx: Arm64AsmParser.Smulh3Context) {
    if (!pass1) {
      instructions.add(
        SmulhInstruction(
          parseReg(ctx.reg(0).text),
          parseReg(ctx.reg(1).text),
          parseReg(ctx.reg(2).text)
        )
      )
    }
    pc += 4
  }

  override fun visitUmulh3(ctx: Arm64AsmParser.Umulh3Context) {
    if (!pass1) {
      instructions.add(
        UmulhInstruction(
          parseReg(ctx.reg(0).text),
          parseReg(ctx.reg(1).text),
          parseReg(ctx.reg(2).text)
        )
      )
    }
    pc += 4
  }

  override fun visitSdiv3(ctx: Arm64AsmParser.Sdiv3Context) {
    if (!pass1) {
      instructions.add(
        SdivInstruction(
          parseReg(ctx.reg(0).text),
          parseReg(ctx.reg(1).text),
          parseReg(ctx.reg(2).text)
        )
      )
    }
    pc += 4
  }

  override fun visitUdiv3(ctx: Arm64AsmParser.Udiv3Context) {
    if (!pass1) {
      instructions.add(
        UdivInstruction(
          parseReg(ctx.reg(0).text),
          parseReg(ctx.reg(1).text),
          parseReg(ctx.reg(2).text)
        )
      )
    }
    pc += 4
  }

  override fun visitCmpInstr(ctx: Arm64AsmParser.CmpInstrContext) {
    if (!pass1) {
      instructions.add(
        CmpInstruction(
          parseReg(ctx.reg(0).text),
          parseReg(ctx.reg(1).text)
        )
      )
    }
    pc += 4
  }

  override fun visitBrReg(ctx: Arm64AsmParser.BrRegContext) {
    if (!pass1) {
      instructions.add(BrInstruction(parseReg(ctx.reg().text)))
    }
    pc += 4
  }

  override fun visitBlrReg(ctx: Arm64AsmParser.BlrRegContext) {
    if (!pass1) {
      instructions.add(BlrInstruction(parseReg(ctx.reg().text)))
    }
    pc += 4
  }

  override fun visitLdurMem(ctx: Arm64AsmParser.LdurMemContext) {
    if (!pass1) {
      instructions.add(
        LdurInstruction(
          parseReg(ctx.reg(0).text),
          parseReg(ctx.reg(1).text),
          parseImmediate(ctx.imm().text).toInt()
        )
      )
    }
    pc += 4
  }

  override fun visitSturMem(ctx: Arm64AsmParser.SturMemContext) {
    if (!pass1) {
      instructions.add(
        SturInstruction(
          parseReg(ctx.reg(0).text),
          parseReg(ctx.reg(1).text),
          parseImmediate(ctx.imm().text).toInt()
        )
      )
    }
    pc += 4
  }

  override fun visitLdrPc(ctx: Arm64AsmParser.LdrPcContext) {
    if (!pass1) {
      instructions.add(
        LdrPcInstruction(
          parseReg(ctx.reg().text),
          parseImmediate(ctx.addr().text).toInt()
        )
      )
    }
    pc += 4
  }

  override fun visitBImm(ctx: Arm64AsmParser.BImmContext) {
    if (!pass1) {
      instructions.add(BInstruction(branchOffset(ctx.addr())))
    }
    pc += 4
  }

  override fun visitBCondDot(ctx: Arm64AsmParser.BCondDotContext) {
    if (!pass1) {
      instructions.add(
        BCondInstruction(
          parseCond(ctx.cond().text),
          branchOffset(ctx.addr())
        )
      )
    }
    pc += 4
  }

  // ------------------------------------------------------------
  // Utilities
  // ------------------------------------------------------------

  private fun parseCond(cond: String): Int =
    when (cond) {
      "eq" -> 0b0000
      "ne" -> 0b0001
      "hs", "cs" -> 0b0010
      "lo", "cc" -> 0b0011
      "hi" -> 0b1000
      "ls" -> 0b1001
      "ge" -> 0b1010
      "lt" -> 0b1011
      "gt" -> 0b1100
      "le" -> 0b1101
      else -> error("Unknown condition: $cond")
    }

  companion object {
    internal fun parseReg(regName: String): Int =
      when (regName) {
        "xzr", "sp" -> 31
        else -> regName.substring(1).toInt()
      }

    internal fun parseImmediate(imm: String): Long =
      if (imm.startsWith("0x") || imm.startsWith("0X"))
        imm.substring(2).toLong(16)
      else
        imm.toLong()
  }
}
