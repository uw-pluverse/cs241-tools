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

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RegistersTest {

  @Test
  fun getDataReturnsCorrectRegisterData() {
    val registers = Registers()

    val data = registers.getData(0)
    assertEquals(0, data())
  }

  @Test
  fun getUsingAddressReturnsCorrectRegisterData() {
    val registers = Registers()
    val address = Memory.getAddress(
      0,
    ) // Assuming getAddress converts index to address correctly
    val data = registers[address]
    assertEquals(0, data())
  }

  @Test
  fun accessingHiIndexReturnsCorrectData() {
    val registers = Registers()
    val hiData = registers.getData(Registers.HI_INDEX)
    assertEquals(0, hiData())
  }

  @Test
  fun accessingLoIndexReturnsCorrectData() {
    val registers = Registers()
    val loData = registers.getData(Registers.LO_INDEX)
    assertEquals(0, loData())
  }

  @Test
  fun accessingBeyondMaxSizeThrowsException() {
    val registers = Registers()
    assertThrows(OutsideMemoryRangeException::class.java) { registers.getData(-1) }
  }
}

class RamMutableMemoryTest {

  @Test
  fun getDataReturnsCorrectMipsInstructionData() {
    val ramMemory = RamMemory(10) // Small size for testing
    val data = ramMemory.getData(0)
    assertEquals(0u, data.address.getAddressBits())
  }

  @Test
  fun getUsingAddressReturnsCorrectMipsInstructionData() {
    val ramMemory = RamMemory(10)
    val address = Memory.getAddress(
      0,
    ) // Assuming getAddress converts index to address correctly
    val data = ramMemory[address]
    assertEquals(0u, data.address.getAddressBits())
  }

  @Test
  fun accessingBeyondMaxSizeThrowsException() {
    val ramMemory = RamMemory(10)
    assertThrows(OutsideMemoryRangeException::class.java) { ramMemory.getData(10) }
  }

  @Test
  fun constructorWithInvalidMaxSizeThrowsException() {
    assertThrows(InvalidAddressException::class.java) { RamMemory(Int.MAX_VALUE) }
  }
}
