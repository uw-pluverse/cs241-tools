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

import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ConsoleErrorListener
import org.antlr.v4.runtime.DiagnosticErrorListener
import java.nio.file.Files
import java.nio.file.Path

class Main {
  companion object {
    // ---------- I/O ----------
    private fun openInput(arg0: String?): CharStream {
      return if (arg0 == null || "-" == arg0) {
        CharStreams.fromStream(System.`in`)
      } else {
        CharStreams.fromPath(Path.of(arg0))
      }
    }

    // ---------- Main ----------
    @JvmStatic
    fun main(args: Array<String>) {
      val input = if (args.isNotEmpty()) args[0] else "-"
      val cs = openInput(input)

      val lexer = Arm64AsmLexer(cs)
      val tokens = CommonTokenStream(lexer)
      val parser = Arm64AsmParser(tokens)

      parser.removeErrorListeners()
      parser.addErrorListener(DiagnosticErrorListener(true))
      parser.addErrorListener(ConsoleErrorListener.INSTANCE)

      val tree = parser.program()

      // Visit the parse tree
       PrettyVisitor().visit(tree)

      val generator = CodeGenVisitor()
      generator.visit(tree)
      val code = generator.machineCode

      // Write generated machine code to a binary file. Use second arg as output path if provided.
      val outputPath = if (args.size > 1) args[1] else "out.bin"
      val outPath = Path.of(outputPath)
      Files.write(outPath, code)
      println("Wrote ${code.size} bytes to $outputPath")
    }
  }
}
