package org.pluverse.cs241.assembler;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.nio.file.Path;

public class Main {

    // ----------An Example: Pretty Visitor ----------
    static final class PrettyVisitor extends Arm64AsmBaseVisitor<Void> {
        private int indent = 0;

        private void p(String s) {
            System.out.println("  ".repeat(indent) + s);
        }
        private Void withIndent(Runnable r) {
            indent++;
            try { r.run(); } finally { indent--; }
            return null;
        }

        // program : (statement? NEWLINE)* statement? EOF
        @Override public Void visitProgram(Arm64AsmParser.ProgramContext ctx) {
            p("program");
            return withIndent(() -> {
                // visit each statement
                for (Arm64AsmParser.StatementContext st : ctx.statement()) {
                    visit(st);
                }
            });
        }

        // ---------- arith3 ----------
        @Override public Void visitAdd3(Arm64AsmParser.Add3Context ctx) {
            p("add");
            return withIndent(() -> {
                p("rd = " + ctx.reg(0).getText());
                p("rn = " + ctx.reg(1).getText());
                p("rm = " + ctx.reg(2).getText());
            });
        }
        @Override public Void visitSub3(Arm64AsmParser.Sub3Context ctx) {
            p("sub");
            return withIndent(() -> {
                p("rd = " + ctx.reg(0).getText());
                p("rn = " + ctx.reg(1).getText());
                p("rm = " + ctx.reg(2).getText());
            });
        }
        @Override public Void visitMul3(Arm64AsmParser.Mul3Context ctx) {
            p("mul");
            return withIndent(() -> {
                p("rd = " + ctx.reg(0).getText());
                p("rn = " + ctx.reg(1).getText());
                p("rm = " + ctx.reg(2).getText());
            });
        }
        @Override public Void visitSmulh3(Arm64AsmParser.Smulh3Context ctx) {
            p("smulh");
            return withIndent(() -> {
                p("rd = " + ctx.reg(0).getText());
                p("rn = " + ctx.reg(1).getText());
                p("rm = " + ctx.reg(2).getText());
            });
        }
        @Override public Void visitUmulh3(Arm64AsmParser.Umulh3Context ctx) {
            p("umulh");
            return withIndent(() -> {
                p("rd = " + ctx.reg(0).getText());
                p("rn = " + ctx.reg(1).getText());
                p("rm = " + ctx.reg(2).getText());
            });
        }
        @Override public Void visitSdiv3(Arm64AsmParser.Sdiv3Context ctx) {
            p("sdiv");
            return withIndent(() -> {
                p("rd = " + ctx.reg(0).getText());
                p("rn = " + ctx.reg(1).getText());
                p("rm = " + ctx.reg(2).getText());
            });
        }
        @Override public Void visitUdiv3(Arm64AsmParser.Udiv3Context ctx) {
            p("udiv");
            return withIndent(() -> {
                p("rd = " + ctx.reg(0).getText());
                p("rn = " + ctx.reg(1).getText());
                p("rm = " + ctx.reg(2).getText());
            });
        }

        // ---------- cmp2 ----------
        @Override public Void visitCmpInstr(Arm64AsmParser.CmpInstrContext ctx) {
            p("cmp");
            return withIndent(() -> {
                p("rn = " + ctx.reg(0).getText());
                p("rm = " + ctx.reg(1).getText());
            });
        }

        // ---------- mem ----------
        @Override public Void visitLdurMem(Arm64AsmParser.LdurMemContext ctx) {
            p("ldur");
            return withIndent(() -> {
                p("rt  = " + ctx.reg(0).getText());
                p("rn  = " + ctx.reg(1).getText());
                p("imm = " + ctx.imm().getText());
            });
        }
        @Override public Void visitSturMem(Arm64AsmParser.SturMemContext ctx) {
            p("stur");
            return withIndent(() -> {
                p("rt  = " + ctx.reg(0).getText());
                p("rn  = " + ctx.reg(1).getText());
                p("imm = " + ctx.imm().getText());
            });
        }

        // ---------- ldr_pc ----------
        @Override public Void visitLdrPc(Arm64AsmParser.LdrPcContext ctx) {
            p("ldr (pc-relative)");
            return withIndent(() -> {
                p("rt  = " + ctx.reg().getText());
                p("imm = " + ctx.imm().getText());
            });
        }

        // ---------- branches ----------
        @Override public Void visitBImm(Arm64AsmParser.BImmContext ctx) {
            p("b");
            return withIndent(() -> p("imm = " + ctx.imm().getText()));
        }
        @Override public Void visitBrReg(Arm64AsmParser.BrRegContext ctx) {
            p("br");
            return withIndent(() -> p("rn = " + ctx.reg().getText()));
        }
        @Override public Void visitBlrReg(Arm64AsmParser.BlrRegContext ctx) {
            p("blr");
            return withIndent(() -> p("rn = " + ctx.reg().getText()));
        }
        @Override public Void visitBCondDot(Arm64AsmParser.BCondDotContext ctx) {
            p("b.cond"); // b . cond imm
            return withIndent(() -> {
                p("cond = " + ctx.cond().getText());
                p("imm  = " + ctx.imm().getText());
            });
        }
        @Override public Void visitBCondPlain(Arm64AsmParser.BCondPlainContext ctx) {
            p("b.cond"); // b cond imm
            return withIndent(() -> {
                p("cond = " + ctx.cond().getText());
                p("imm  = " + ctx.imm().getText());
            });
        }

        // ---------- directive ----------
        @Override public Void visitDir8Byte(Arm64AsmParser.Dir8ByteContext ctx) {
            p(".8byte");
            return withIndent(() -> p("value = " + ctx.imm().getText()));
        }

        // defaults
        @Override protected Void defaultResult() { return null; }
        @Override protected Void aggregateResult(Void aggregate, Void nextResult) { return null; }
    }

    // ---------- I/O ----------
    private static CharStream openInput(String arg0) throws Exception {
        if (arg0 == null || "-".equals(arg0)) {
            return CharStreams.fromStream(System.in);
        } else {
            return CharStreams.fromPath(Path.of(arg0));
        }
    }

    // ---------- Main ----------
    public static void main(String[] args) throws Exception {
        String input = (args.length > 0) ? args[0] : "-";
        CharStream cs = openInput(input);

        Arm64AsmLexer lexer = new Arm64AsmLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Arm64AsmParser parser = new Arm64AsmParser(tokens);

        parser.removeErrorListeners();
        parser.addErrorListener(new DiagnosticErrorListener(true));
        parser.addErrorListener(ConsoleErrorListener.INSTANCE);

        Arm64AsmParser.ProgramContext tree = parser.program();

        // Visit the parse tree
        new PrettyVisitor().visit(tree);
    }
}
