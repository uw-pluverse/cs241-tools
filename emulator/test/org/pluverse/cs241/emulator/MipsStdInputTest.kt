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
package org.pluverse.cs241.emulator

import com.google.common.truth.Truth
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.pluverse.cs241.emulator.cpumodel.MipsStdInput
import java.nio.file.Files
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively
import kotlin.io.path.writeText

@RunWith(JUnit4::class)
class MipsStdInputTest {

  private val tempDir = Files.createTempDirectory(this::class.qualifiedName)

  @OptIn(ExperimentalPathApi::class)
  @After
  fun teardown() {
    tempDir.deleteRecursively()
  }

  @Test
  fun test() {
    val content = "a\nb\n"
    val file = tempDir.resolve("a.txt").apply {
      writeText(content)
    }
    val input = MipsStdInput(file)
    var char = input.read()
    val actualContent = mutableListOf<Int>()
    while (char != MipsStdInput.EOF) {
      actualContent.add(char)
      char = input.read()
    }
    Truth.assertThat(actualContent).isEqualTo(
      content.asSequence().map { it.code }.toList(),
    )
    Truth.assertThat(
      actualContent.asSequence().map { it.toChar() }.joinToString(separator = ""),
    ).isEqualTo(content)
  }
}
