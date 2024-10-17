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
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.pluverse.cs241.emulator.cpumodel.CpuEmulator
import org.pluverse.cs241.emulator.cpumodel.EmulatorHasReturnedOSException
import org.pluverse.cs241.emulator.views.CliView
import java.nio.file.Paths
import kotlin.io.path.readBytes

@RunWith(JUnit4::class)
class CpuEmulatorTest {

  val view = CliView()

  @Test
  fun testLisInstructions() {
    val emulator = CpuEmulator(
      view,
      Paths.get("emulator/test/org/pluverse/cs241/emulator/testdata/lis.mips").readBytes(),
      input1 = 1,
      input2 = 2,
    )
    Assert.assertThrows(EmulatorHasReturnedOSException::class.java) {
      while (true) {
        emulator.runFetchExecuteLoop()
      }
    }
    Truth.assertThat(emulator.getRegisterValue(1).doubleWord).isEqualTo(1)
  }
}
