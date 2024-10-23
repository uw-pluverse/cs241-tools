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
package org.pluverse.cs241.emulator.cpumodel

import com.google.common.primitives.ImmutableIntArray
import java.nio.file.Path
import kotlin.io.path.readBytes

class MipsStdInput(file: Path) : AbstractMipsStdInput() {

  private var index = 0

  private val content = file.readBytes().let { byteArray ->
    val builder = ImmutableIntArray.builder()
    byteArray.forEach { byte ->
      builder.add(byte.toInt())
    }
    builder.build()
  }

  override fun read(): Int {
    if (index >= content.length()) {
      return EOF
    }
    return content[index++]
  }

  companion object {
    const val EOF = -1
  }

  object EmptyMipsStdInput : AbstractMipsStdInput() {
    override fun read(): Int {
      return EOF
    }
  }
}
