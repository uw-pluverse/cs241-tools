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
package org.pluverse.cs241.assembler;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {

  // ----------An Example: Pretty Visitor ----------
  static final class PrettyVisitor extends Arm64AsmBaseVisitor<Void> {
    private int indent = 0;

    private void p(String s) {
      System.out.println("  ".repeat(indent) + s);
    }

    private Void withIndent(Runnable r) {
      indent++;
      try {
        r.run();
      } finally {
        indent--;
      }
      return null;
    }

    // program : (statement? NEWLINE)* statement? EOF
    @Override
    public Void visitProgram(Arm64AsmParser.ProgramContext ctx) {
      p("program");
      return withIndent(
          () -> {
            // visit each statement
            for (Arm64AsmParser.StatementContext st : ctx.statement()) {
              visit(st);
            }
          });
    }

    // ---------- arith3 ----------
    @Override
    public Void visitAdd3(Arm64AsmParser.Add3Context ctx) {
      p("add");
      return withIndent(
          () -> {
            p("rd = " + ctx.reg(0).getText());
            p("rn = " + ctx.reg(1).getText());
            p("rm = " + ctx.reg(2).getText());
          });
    }

    @Override
    public Void visitSub3(Arm64AsmParser.Sub3Context ctx) {
      p("sub");
      return withIndent(
          () -> {
            p("rd = " + ctx.reg(0).getText());
            p("rn = " + ctx.reg(1).getText());
            p("rm = " + ctx.reg(2).getText());
          });
    }

    @Override
    public Void visitMul3(Arm64AsmParser.Mul3Context ctx) {
      p("mul");
      return withIndent(
          () -> {
            p("rd = " + ctx.reg(0).getText());
            p("rn = " + ctx.reg(1).getText());
            p("rm = " + ctx.reg(2).getText());
          });
    }

    @Override
    public Void visitSmulh3(Arm64AsmParser.Smulh3Context ctx) {
      p("smulh");
      return withIndent(
          () -> {
            p("rd = " + ctx.reg(0).getText());
            p("rn = " + ctx.reg(1).getText());
            p("rm = " + ctx.reg(2).getText());
          });
    }

    @Override
    public Void visitUmulh3(Arm64AsmParser.Umulh3Context ctx) {
      p("umulh");
      return withIndent(
          () -> {
            p("rd = " + ctx.reg(0).getText());
            p("rn = " + ctx.reg(1).getText());
            p("rm = " + ctx.reg(2).getText());
          });
    }

    @Override
    public Void visitSdiv3(Arm64AsmParser.Sdiv3Context ctx) {
      p("sdiv");
      return withIndent(
          () -> {
            p("rd = " + ctx.reg(0).getText());
            p("rn = " + ctx.reg(1).getText());
            p("rm = " + ctx.reg(2).getText());
          });
    }

    @Override
    public Void visitUdiv3(Arm64AsmParser.Udiv3Context ctx) {
      p("udiv");
      return withIndent(
          () -> {
            p("rd = " + ctx.reg(0).getText());
            p("rn = " + ctx.reg(1).getText());
            p("rm = " + ctx.reg(2).getText());
          });
    }

    // ---------- cmp2 ----------
    @Override
    public Void visitCmpInstr(Arm64AsmParser.CmpInstrContext ctx) {
      p("cmp");
      return withIndent(
          () -> {
            p("rn = " + ctx.reg(0).getText());
            p("rm = " + ctx.reg(1).getText());
          });
    }

    // ---------- mem ----------
    @Override
    public Void visitLdurMem(Arm64AsmParser.LdurMemContext ctx) {
      p("ldur");
      return withIndent(
          () -> {
            p("rt  = " + ctx.reg(0).getText());
            p("rn  = " + ctx.reg(1).getText());
            p("imm = " + ctx.imm().getText());
          });
    }

    @Override
    public Void visitSturMem(Arm64AsmParser.SturMemContext ctx) {
      p("stur");
      return withIndent(
          () -> {
            p("rt  = " + ctx.reg(0).getText());
            p("rn  = " + ctx.reg(1).getText());
            p("imm = " + ctx.imm().getText());
          });
    }

    // ---------- ldr_pc ----------
    @Override
    public Void visitLdrPc(Arm64AsmParser.LdrPcContext ctx) {
      p("ldr (pc-relative)");
      return withIndent(
          () -> {
            p("rt  = " + ctx.reg().getText());
            p("imm = " + ctx.imm().getText());
          });
    }

    // ---------- branches ----------
    @Override
    public Void visitBImm(Arm64AsmParser.BImmContext ctx) {
      p("b");
      return withIndent(() -> p("imm = " + ctx.imm().getText()));
    }

    @Override
    public Void visitBrReg(Arm64AsmParser.BrRegContext ctx) {
      p("br");
      return withIndent(() -> p("rn = " + ctx.reg().getText()));
    }

    @Override
    public Void visitBlrReg(Arm64AsmParser.BlrRegContext ctx) {
      p("blr");
      return withIndent(() -> p("rn = " + ctx.reg().getText()));
    }

    @Override
    public Void visitBCondDot(Arm64AsmParser.BCondDotContext ctx) {
      p("b.cond"); // b . cond imm
      return withIndent(
          () -> {
            p("cond = " + ctx.cond().getText());
            p("imm  = " + ctx.imm().getText());
          });
    }

    @Override
    public Void visitBCondPlain(Arm64AsmParser.BCondPlainContext ctx) {
      p("b.cond"); // b cond imm
      return withIndent(
          () -> {
            p("cond = " + ctx.cond().getText());
            p("imm  = " + ctx.imm().getText());
          });
    }

    // ---------- directive ----------
    @Override
    public Void visitDir8Byte(Arm64AsmParser.Dir8ByteContext ctx) {
      p(".8byte");
      return withIndent(() -> p("value = " + ctx.imm().getText()));
    }

    // defaults
    @Override
    protected Void defaultResult() {
      return null;
    }

    @Override
    protected Void aggregateResult(Void aggregate, Void nextResult) {
      return null;
    }
  }

  // ---------- CodeGenVisitor ----------
  static final class CodeGenVisitor extends Arm64AsmBaseVisitor<Void> {
    // Buffer to store generated machine code bytes
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();

    // Get the generated byte array
    public byte[] getMachineCode() {
      return baos.toByteArray();
    }

    // Helper: write 32-bit integer in Little Endian
    private void emit32(int val) {
      baos.write(val & 0xFF);
      baos.write((val >> 8) & 0xFF);
      baos.write((val >> 16) & 0xFF);
      baos.write((val >> 24) & 0xFF);
    }

    // Helper: write 64-bit integer in Little Endian (.8byte)
    private void emit64(long val) {
      emit32((int) (val & 0xFFFFFFFFL));
      emit32((int) (val >>> 32));
    }

    // Helper: parse register, e.g., "x0" -> 0, "x10" -> 10
    private int parseReg(String regName) {
      if (regName.equals("xzr")) return 31;
      if (regName.equals("sp")) return 31;
      // Remove 'x' prefix and parse as integer
      return Integer.parseInt(regName.substring(1));
    }

    // Helper: parse immediate value
    private long parseImm(String imm) {
      if (imm.startsWith("0x") || imm.startsWith("0X")) {
        return Long.parseLong(imm.substring(2), 16);
      }
      return Long.parseLong(imm);
    }

    // ---------- Visit Program Entry ----------
    @Override
    public Void visitProgram(Arm64AsmParser.ProgramContext ctx) {
      // Visit all statements
      for (Arm64AsmParser.StatementContext st : ctx.statement()) {
        visit(st);
      }
      return null;
    }

    // ---------- Implement Instruction Encoding (Example: ADD) ----------
    @Override
    public Void visitAdd3(Arm64AsmParser.Add3Context ctx) {
      int rd = parseReg(ctx.reg(0).getText());
      int rn = parseReg(ctx.reg(1).getText());
      int rm = parseReg(ctx.reg(2).getText());

      int opcode = 0b10001011001 << 21;
      int flags = 0b011000 << 10;

      int machineCode = opcode | (rm << 16) | flags | (rn << 5) | rd;
      emit32(machineCode);
      return null;
    }

    @Override
    public Void visitSub3(Arm64AsmParser.Sub3Context ctx) {
      int rd = parseReg(ctx.reg(0).getText());
      int rn = parseReg(ctx.reg(1).getText());
      int rm = parseReg(ctx.reg(2).getText());

      int opcode = 0b11001011001 << 21;
      int flags = 0b011000 << 10;

      int machineCode = opcode | (rm << 16) | flags | (rn << 5) | rd;
      emit32(machineCode);
      return null;
    }

    @Override
    public Void visitMul3(Arm64AsmParser.Mul3Context ctx) {
      int rd = parseReg(ctx.reg(0).getText());
      int rn = parseReg(ctx.reg(1).getText());
      int rm = parseReg(ctx.reg(2).getText());

      int opcode = 0b10011011000 << 21;
      int flags = 0b011111 << 10;

      int machineCode = opcode | (rm << 16) | flags | (rn << 5) | rd;
      emit32(machineCode);
      return null;
    }

    @Override
    public Void visitSmulh3(Arm64AsmParser.Smulh3Context ctx) {
      int rd = parseReg(ctx.reg(0).getText());
      int rn = parseReg(ctx.reg(1).getText());
      int rm = parseReg(ctx.reg(2).getText());

      int opcode = 0b10011011010 << 21;
      int flags = 0b011111 << 10;

      int machineCode = opcode | (rm << 16) | flags | (rn << 5) | rd;
      emit32(machineCode);
      return null;
    }

    @Override
    public Void visitUmulh3(Arm64AsmParser.Umulh3Context ctx) {
      int rd = parseReg(ctx.reg(0).getText());
      int rn = parseReg(ctx.reg(1).getText());
      int rm = parseReg(ctx.reg(2).getText());

      int opcode = 0b10011011110 << 21;
      int flags = 0b011111 << 10;

      int machineCode = opcode | (rm << 16) | flags | (rn << 5) | rd;
      emit32(machineCode);
      return null;
    }

    @Override
    public Void visitSdiv3(Arm64AsmParser.Sdiv3Context ctx) {
      int rd = parseReg(ctx.reg(0).getText());
      int rn = parseReg(ctx.reg(1).getText());
      int rm = parseReg(ctx.reg(2).getText());

      int opcode = 0b10011010110 << 21;
      int flags = 0b000011 << 10;

      int machineCode = opcode | (rm << 16) | flags | (rn << 5) | rd;
      emit32(machineCode);
      return null;
    }

    @Override
    public Void visitUdiv3(Arm64AsmParser.Udiv3Context ctx) {
      int rd = parseReg(ctx.reg(0).getText());
      int rn = parseReg(ctx.reg(1).getText());
      int rm = parseReg(ctx.reg(2).getText());

      int opcode = 0b10011010110 << 21;
      int flags = 0b000010 << 10;

      int machineCode = opcode | (rm << 16) | flags | (rn << 5) | rd;
      emit32(machineCode);
      return null;
    }

    @Override
    public Void visitCmpInstr(Arm64AsmParser.CmpInstrContext ctx) {
      int rn = parseReg(ctx.reg(0).getText());
      int rm = parseReg(ctx.reg(1).getText());
      int rd = 31; // For cmp, the destination register (rd) is implicitly xzr (register 31)

      int opcode = 0b11101011001 << 21;
      int flags = 0b011000 << 10;

      // Assemble the machine code by combining opcode, registers, and flags
      int machineCode = opcode | (rm << 16) | flags | (rn << 5) | rd;
      emit32(machineCode);
      return null;
    }

    @Override
    public Void visitBrReg(Arm64AsmParser.BrRegContext ctx) {
      int rn = parseReg(ctx.reg().getText());
      int rm = 31;
      int rd = 0;

      int opcode = 0b11010110000 << 21;
      int flags = 0b000000 << 10;

      // Assemble the machine code by combining opcode, registers, and flags
      int machineCode = opcode | (rm << 16) | flags | (rn << 5) | rd;
      emit32(machineCode);
      return null;
    }

    @Override
    public Void visitBlrReg(Arm64AsmParser.BlrRegContext ctx) {
      int rn = parseReg(ctx.reg().getText());
      int rm = 31;
      int rd = 0;

      int opcode = 0b11010110001 << 21;
      int flags = 0b000000 << 10;

      // Assemble the machine code by combining opcode, registers, and flags
      int machineCode = opcode | (rm << 16) | flags | (rn << 5) | rd;
      emit32(machineCode);
      return null;
    }

    // ---------- Implement Directive (.8byte) ----------
    @Override
    public Void visitDir8Byte(Arm64AsmParser.Dir8ByteContext ctx) {
      long val = parseImm(ctx.imm().getText());
      emit64(val);
      return null;
    }
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
    // new PrettyVisitor().visit(tree);
    CodeGenVisitor generator = new CodeGenVisitor();
    generator.visit(tree);
    byte[] code = generator.getMachineCode();

    for (byte b : code) {
      String binaryString =
          String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
      System.out.print(binaryString + " ");
      // System.out.printf("%02X ", b);
    }
    System.out.println();
  }
}
