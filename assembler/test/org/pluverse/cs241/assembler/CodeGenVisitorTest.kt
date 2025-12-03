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

import com.google.common.truth.Truth.assertThat
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Test

class CodeGenVisitorTest {

  private fun compile(asm: String): ByteArray {
    val charStream = CharStreams.fromString(asm)
    val lexer = Arm64AsmLexer(charStream)
    val tokens = CommonTokenStream(lexer)
    val parser = Arm64AsmParser(tokens)
    parser.removeErrorListeners()

    val tree = parser.program()
    val visitor = CodeGenVisitor()
    visitor.visit(tree)
    return visitor.machineCode
  }

  private fun assertAssembly(asm: String, expectedHex: String) {
    val actualBytes = compile(asm)
    val expectedBytes = hexStringToByteArray(expectedHex)
    assertThat(actualBytes).isEqualTo(expectedBytes)
  }

  private fun hexStringToByteArray(s: String): ByteArray {
    val len = s.length
    val data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
      data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
      i += 2
    }
    return data
  }

  @Test
  fun testAdd() {
    assertAssembly("add x0, x1, x2", "2060228B")
  }

  @Test
  fun testSub() {
    assertAssembly("sub x0, x1, x2", "206022CB")
  }

  @Test
  fun testMul() {
    assertAssembly("mul x0, x1, x3", "207C039B")
  }

  @Test
  fun testSmulh() {
    assertAssembly("smulh x2, x4, x5", "827C459B")
  }

  @Test
  fun testUmulh() {
    assertAssembly("umulh x0, x1, x2", "207CC29B")
  }

  @Test
  fun testSdiv() {
    assertAssembly("sdiv x0, x1, x2", "200CC29A")
  }

  @Test
  fun testUdiv() {
    assertAssembly("udiv x0, x1, x2", "2008C29A")
  }

  @Test
  fun testCmp() {
    assertAssembly("cmp x1, x3", "3F6023EB")
  }

  @Test
  fun testBr() {
    assertAssembly("br x0", "00001FD6")
  }

  @Test
  fun testBlr() {
    assertAssembly("blr x0", "00003FD6")
  }
}
