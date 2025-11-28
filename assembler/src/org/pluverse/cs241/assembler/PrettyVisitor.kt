package org.pluverse.cs241.assembler

class PrettyVisitor : Arm64AsmBaseVisitor<Void?>() {
    private var indent = 0

    private fun p(s: String) {
        println("  ".repeat(indent) + s)
    }

    private fun withIndent(r: Runnable): Void? {
        indent++
        try {
            r.run()
        } finally {
            indent--
        }
        return null
    }

    // program : (statement? NEWLINE)* statement? EOF
    override fun visitProgram(ctx: Arm64AsmParser.ProgramContext): Void? {
        p("program")
        return withIndent {
            // visit each statement
            for (st in ctx.statement()) {
                visit(st)
            }
        }
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

    // ---------- mem ----------
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

    // ---------- ldr_pc ----------
    override fun visitLdrPc(ctx: Arm64AsmParser.LdrPcContext): Void? {
        p("ldr (pc-relative)")
        return withIndent {
            p("rt  = " + ctx.reg().text)
            p("imm = " + ctx.imm().text)
        }
    }

    // ---------- branches ----------
    override fun visitBImm(ctx: Arm64AsmParser.BImmContext): Void? {
        p("b")
        return withIndent { p("imm = " + ctx.imm().text) }
    }

    override fun visitBrReg(ctx: Arm64AsmParser.BrRegContext): Void? {
        p("br")
        return withIndent { p("rn = " + ctx.reg().text) }
    }

    override fun visitBlrReg(ctx: Arm64AsmParser.BlrRegContext): Void? {
        p("blr")
        return withIndent { p("rn = " + ctx.reg().text) }
    }

    override fun visitBCondDot(ctx: Arm64AsmParser.BCondDotContext): Void? {
        p("b.cond") // b . cond imm
        return withIndent {
            p("cond = " + ctx.cond().text)
            p("imm  = " + ctx.imm().text)
        }
    }

    override fun visitBCondPlain(ctx: Arm64AsmParser.BCondPlainContext): Void? {
        p("b.cond") // b cond imm
        return withIndent {
            p("cond = " + ctx.cond().text)
            p("imm  = " + ctx.imm().text)
        }
    }

    // ---------- directive ----------
    override fun visitDir8Byte(ctx: Arm64AsmParser.Dir8ByteContext): Void? {
        p(".8byte")
        return withIndent { p("value = " + ctx.imm().text) }
    }

    // defaults
    override fun defaultResult(): Void? {
        return null
    }

    override fun aggregateResult(aggregate: Void?, nextResult: Void?): Void? {
        return null
    }
}
