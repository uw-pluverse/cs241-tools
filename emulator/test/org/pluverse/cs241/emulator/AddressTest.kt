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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.pluverse.cs241.emulator.cpumodel.Address
import org.pluverse.cs241.emulator.cpumodel.InvalidAddressException
import org.pluverse.cs241.emulator.cpumodel.OutsideAddressRangeException

@RunWith(JUnit4::class)
class AddressTest {

  @Test
  fun getAddressBitsReturnsCorrectValue() {
    val address = Address(16u)
    assertEquals(16, address.getAddressBits())
  }

  @Test
  fun getMemoryIndexReturnsCorrectValue() {
    val address = Address(16u)
    assertEquals(4, address.getMemoryIndex())
  }

  @Test
  fun plusWithIntReturnsCorrectAddress() {
    val address = Address(4u)
    val result = address + 1
    assertEquals(8, result.getAddressBits())
  }

  @Test
  fun plusWithAddressReturnsCorrectAddress() {
    val address1 = Address(4u)
    val address2 = Address(8u)
    val result = address1 + address2
    assertEquals(12, result.getAddressBits())
  }

  @Test
  fun minusWithIntReturnsCorrectAddress() {
    val address = Address(8u)
    val result = address - 1
    assertEquals(4, result.getAddressBits())
  }

  @Test
  fun minusWithAddressReturnsCorrectAddress() {
    val address1 = Address(12u)
    val address2 = Address(4u)
    val result = address1 - address2
    assertEquals(8, result.getAddressBits())
  }

  @Test
  fun shiftBytesCorrectlyShiftsAddress() {
    val address = Address(4u)
    val result = address.shiftBytes(8)
    assertEquals(12, result.getAddressBits())
  }

  @Test
  fun toHexStringFormatsCorrectly() {
    val address = Address(256u)
    assertEquals("0x00000100", address.toHexString())
  }

  @Test
  fun toBinaryStringFormatsCorrectly() {
    val address = Address(4u)
    assertEquals("00000000000000000000000000000100", address.toBinaryString())
  }

  @Test
  fun toBinaryStringFormatsCorrectlyForUIntMax() {
    val address = Address(UInt.MAX_VALUE - 3u)
    assertEquals("11111111111111111111111111111100", address.toBinaryString())
  }

  @Test
  fun toHexStringFormatsCorrectlyForUIntMax() {
    val address = Address(UInt.MAX_VALUE - 3u)
    assertEquals("0xfffffffc", address.toHexString())
  }

  @Test
  fun toBinaryStringFormatsCorrectlyForZero() {
    val address = Address(0u)
    assertEquals("00000000000000000000000000000000", address.toBinaryString())
  }

  @Test
  fun toHexStringFormatsCorrectlyForZero() {
    val address = Address(0u)
    assertEquals("0x00000000", address.toHexString())
  }

  @Test
  fun plusWithIntOverflowThrowsException() {
    val address = Address(UInt.MAX_VALUE - 3u)
    assertThrows(OutsideAddressRangeException::class.java) { address + 1 }
  }

  @Test
  fun minusWithIntUnderflowThrowsException() {
    val address = Address(0u)
    assertThrows(OutsideAddressRangeException::class.java) { address - 1 }
  }

  @Test
  fun shiftBytesWithNonMultipleOfFourThrowsException() {
    val address = Address(4u)
    assertThrows(InvalidAddressException::class.java) { address.shiftBytes(3) }
  }

  @Test
  fun constructorWithNonMultipleOfFourThrowsException() {
    assertThrows(InvalidAddressException::class.java) { Address(3u) }
  }

  @Test(expected = OutsideAddressRangeException::class)
  fun shiftBytesWithOverflowThrowsException() {
    val address = Address(UInt.MAX_VALUE - 3u)
    address.shiftBytes(16)
  }

  @Test
  fun plusWithOverflowBeyondUIntMaxThrowsException() {
    val address = Address(UInt.MAX_VALUE - 3u)
    assertThrows(OutsideAddressRangeException::class.java) { address + 1 }
  }

  @Test
  fun minusWithUnderflowBelowZeroThrowsException() {
    val address = Address(0u)
    assertThrows(OutsideAddressRangeException::class.java) { address - 1 }
  }

  @Test
  fun shiftBytesWithOverflowBeyondUIntMaxThrowsException() {
    val address = Address(UInt.MAX_VALUE - 15u)
    assertThrows(OutsideAddressRangeException::class.java) { address.shiftBytes(16) }
  }

  @Test
  fun plusWithNegativeIntCorrectlyDecreasesAddress() {
    val address = Address(16u)
    val result = address + (-1)
    assertEquals(12, result.getAddressBits())
  }

  @Test
  fun minusWithNegativeIntCorrectlyIncreasesAddress() {
    val address = Address(4u)
    val result = address - (-1)
    assertEquals(8, result.getAddressBits())
  }

  @Test
  fun plusWithSelfReturnsDoubledAddress() {
    val address = Address(4u)
    val result = address + address
    assertEquals(8, result.getAddressBits())
  }

  @Test
  fun minusWithSelfReturnsZero() {
    val address = Address(4u)
    val result = address - address
    assertEquals(0, result.getAddressBits())
  }

  @Test
  fun shiftBytesWithPositiveNumberCorrectlyIncreasesAddress() {
    val address = Address(4u)
    val result = address.shiftBytes(4)
    assertEquals(8, result.getAddressBits())
  }

  @Test
  fun shiftBytesWithNegativeNumberCorrectlyDecreasesAddress() {
    val address = Address(8u)
    val result = address.shiftBytes(-4)
    assertEquals(4, result.getAddressBits())
  }
}
