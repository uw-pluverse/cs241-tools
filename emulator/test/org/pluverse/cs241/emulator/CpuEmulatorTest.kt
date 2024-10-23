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

import com.google.common.truth.Truth.assertThat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.pluverse.cs241.emulator.cpumodel.CpuEmulator
import org.pluverse.cs241.emulator.cpumodel.EmulatorHasReturnedOSException
import org.pluverse.cs241.emulator.cpumodel.MipsStdInput
import org.pluverse.cs241.emulator.views.CliView
import java.nio.file.Paths
import kotlin.io.path.readBytes

@RunWith(JUnit4::class)
class CpuEmulatorTest {

  val view = CliView()

  @Test
  fun testLisInstructions() {
    val mipsFile = "emulator/test/org/pluverse/cs241/emulator/testdata/lis.mips"
    val emulator = createAndRunEmulator(mipsFile)
    assertThat(emulator.getRegisterValue(1).doubleWord).isEqualTo(1)
  }

  @Test
  fun testStdinStdout() {
    val mipsFile = "emulator/test/org/pluverse/cs241/emulator/testdata/io.mips"
    val emulator = createAndRunEmulator(mipsFile)
    assertThat(emulator.getRegisterValue(3).doubleWord).isEqualTo(-1)
  }

  private fun createAndRunEmulator(mipsFile: String): CpuEmulator {
    val emulator = CpuEmulator.createTwoIntsEmulator(
      view = view,
      mipsProgram = Paths.get(mipsFile).readBytes(),
      stdin = MipsStdInput.EmptyMipsStdInput,
      register1 = 1,
      register2 = 2,
    )
    Assert.assertThrows(EmulatorHasReturnedOSException::class.java) {
      while (true) {
        emulator.runFetchExecuteLoop()
      }
    }
    return emulator
  }
}
