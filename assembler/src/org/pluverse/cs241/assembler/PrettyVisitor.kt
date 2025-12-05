package org.pluverse.cs241.assembler

class PrettyVisitor : Arm64AsmBaseVisitor<Void?>() {
  private var indent = 0

  private fun p(s: String) {
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
    p("program")
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
    stmtCtx: Arm64AsmParser.StatementContext?
  ) {
    if (labelsCtx != null) {
      val defs = labelsCtx.labelDef()
      val sb = StringBuilder("labels:")
      for (d in defs) {
        sb.append(" ").append(d.LABEL_ID().text)
      }
      p(sb.toString())
    }

    if (stmtCtx != null) {
      visit(stmtCtx)
    } else if (labelsCtx != null) {
      // 只有 label 没有指令（纯 label 行）
      withIndent {
        p("(no instruction on this line)")
      }
    }
    // both labelsCtx and stmtCtx can be null (empty line)
  }

  // ---------- arith3 ----------

  override fun visitAdd3(ctx: Arm64AsmParser.Add3Context): Void? {
    p("add")
    return withIndent {
      p("rd = " + ctx.reg(0).text)
      p("rn = " + ctx.reg(1).text)
      p("rm = " + ctx.reg(2).text)
    }
  }

  override fun visitSub3(ctx: Arm64AsmParser.Sub3Context): Void? {
    p("sub")
    return withIndent {
      p("rd = " + ctx.reg(0).text)
      p("rn = " + ctx.reg(1).text)
      p("rm = " + ctx.reg(2).text)
    }
  }

  override fun visitMul3(ctx: Arm64AsmParser.Mul3Context): Void? {
    p("mul")
    return withIndent {
      p("rd = " + ctx.reg(0).text)
      p("rn = " + ctx.reg(1).text)
      p("rm = " + ctx.reg(2).text)
    }
  }

  override fun visitSmulh3(ctx: Arm64AsmParser.Smulh3Context): Void? {
    p("smulh")
    return withIndent {
      p("rd = " + ctx.reg(0).text)
      p("rn = " + ctx.reg(1).text)
      p("rm = " + ctx.reg(2).text)
    }
  }

  override fun visitUmulh3(ctx: Arm64AsmParser.Umulh3Context): Void? {
    p("umulh")
    return withIndent {
      p("rd = " + ctx.reg(0).text)
      p("rn = " + ctx.reg(1).text)
      p("rm = " + ctx.reg(2).text)
    }
  }

  override fun visitSdiv3(ctx: Arm64AsmParser.Sdiv3Context): Void? {
    p("sdiv")
    return withIndent {
      p("rd = " + ctx.reg(0).text)
      p("rn = " + ctx.reg(1).text)
      p("rm = " + ctx.reg(2).text)
    }
  }

  override fun visitUdiv3(ctx: Arm64AsmParser.Udiv3Context): Void? {
    p("udiv")
    return withIndent {
      p("rd = " + ctx.reg(0).text)
      p("rn = " + ctx.reg(1).text)
      p("rm = " + ctx.reg(2).text)
    }
  }

  // ---------- cmp2 ----------

  override fun visitCmpInstr(ctx: Arm64AsmParser.CmpInstrContext): Void? {
    p("cmp")
    return withIndent {
      p("rn = " + ctx.reg(0).text)
      p("rm = " + ctx.reg(1).text)
    }
  }

  // ---------- mem: ldur/stur ----------

  override fun visitLdurMem(ctx: Arm64AsmParser.LdurMemContext): Void? {
    p("ldur")
    return withIndent {
      p("rt  = " + ctx.reg(0).text)
      p("rn  = " + ctx.reg(1).text)
      p("imm = " + ctx.imm().text)
    }
  }

  override fun visitSturMem(ctx: Arm64AsmParser.SturMemContext): Void? {
    p("stur")
    return withIndent {
      p("rt  = " + ctx.reg(0).text)
      p("rn  = " + ctx.reg(1).text)
      p("imm = " + ctx.imm().text)
    }
  }

  // ---------- ldr_pc: LDR reg, addr ----------

  override fun visitLdrPc(ctx: Arm64AsmParser.LdrPcContext): Void? {
    p("ldr (pc-relative)")
    return withIndent {
      p("rt   = " + ctx.reg().text)
      p("addr = " + addrToString(ctx.addr()))
    }
  }

  // ---------- branches ----------

  override fun visitBImm(ctx: Arm64AsmParser.BImmContext): Void? {
    p("b")
    return withIndent {
      p("addr = " + addrToString(ctx.addr()))
    }
  }

  override fun visitBrReg(ctx: Arm64AsmParser.BrRegContext): Void? {
    p("br")
    return withIndent {
      p("rn = " + ctx.reg().text)
    }
  }

  override fun visitBlrReg(ctx: Arm64AsmParser.BlrRegContext): Void? {
    p("blr")
    return withIndent {
      p("rn = " + ctx.reg().text)
    }
  }

  override fun visitBCondDot(ctx: Arm64AsmParser.BCondDotContext): Void? {
    p("b.cond") // b . cond addr
    return withIndent {
      p("cond = " + ctx.cond().text)
      p("addr = " + addrToString(ctx.addr()))
    }
  }

  override fun visitBCondPlain(ctx: Arm64AsmParser.BCondPlainContext): Void? {
    p("b.cond") // b cond addr
    return withIndent {
      p("cond = " + ctx.cond().text)
      p("addr = " + addrToString(ctx.addr()))
    }
  }

  // ---------- directive .8byte ----------

  override fun visitDir8Byte(ctx: Arm64AsmParser.Dir8ByteContext): Void? {
    p(".8byte")
    return withIndent {
      p("addr = " + addrToString(ctx.addr()))
    }
  }

  // ---------- addr: imm | LABEL_ID ----------

  private fun addrToString(ctx: Arm64AsmParser.AddrContext): String {
    return when (ctx) {
      is Arm64AsmParser.AddrImmContext -> ctx.imm().text
      is Arm64AsmParser.AddrLabelContext -> ctx.LABEL_ID().text
      else -> ctx.text  // should not reach here
    }
  }

  // ---------- default ----------

  override fun defaultResult(): Void? = null

  override fun aggregateResult(aggregate: Void?, nextResult: Void?): Void? = null
}
