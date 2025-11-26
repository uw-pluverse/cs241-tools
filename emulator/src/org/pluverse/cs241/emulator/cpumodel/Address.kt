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

import kotlin.math.abs

/**
 * Address is a datatype for a 32-bit address. One UInt = 1 byte in address. instance() returns the index in an array.
 * They are represented in multiple of fours and are used to access memory.
 * @param address: UInt
 */
data class Address(val address: UInt = 0u) {

  init {
    // Ensure the address is a multiple of 4
    if (address % 4u != 0u) {
      throw InvalidAddressException("Invalid address $address")
    }
  }

  /**
   * Gets the memory array index given this address (address / 4)
   */
  val wordIndex: Int = address.toInt() / 4

  /**
   * Increase number of words (4 bytes)
   *
   * Plus and minus operations we want to consider overflow of address,
   * therefore we use long operations to verify.
   */

  operator fun plus(numOf32BitWord: Int): Address {
    if (numOf32BitWord < 0) return this - abs(numOf32BitWord)

    // Check if the increase will put it fast 0xfffffffff
    val increase = numOf32BitWord.toLong() * 4
    if (increase + address.toLong() > UInt.MAX_VALUE.toLong()) throw OutsideAddressRangeException()

    return Address(address + (numOf32BitWord * 4).toUInt())
  }

  operator fun plus(other: Address): Address {
    return this + other.wordIndex // Add the index or # of 4 bytes
  }

  operator fun minus(numOfFourBytes: Int): Address {
    if (numOfFourBytes < 0) return this + abs(numOfFourBytes)

    // Check if the decrease will put the address below 0.
    val decrease = numOfFourBytes.toLong() * 4
    if (decrease > address.toLong()) throw OutsideAddressRangeException()

    return Address(address - (numOfFourBytes * 4).toUInt())
  }

  operator fun minus(other: Address): Address {
    return this - other.wordIndex // Add the index or # of 4 bytes
  }

  /**
   * Operation to input move raw bytes instead of words
   *
   */
  fun shiftBytes(numOfBytes: Int): Address {
    // Assert that we are moving it by a multiple of 4
    if (numOfBytes % 4 != 0) {
      throw InvalidAddressException("Invalid offset $numOfBytes")
    }

    return this + (numOfBytes / 4)
  }

  fun toHexString(): String {
    return "0x${Integer.toHexString(
      address.toInt(),
    ).padStart(Memory.DOUBLE_WORD_HEX_LENGTH, '0')}"
  }

  /**
   * Return non-padded hex string
   */
  fun toHexStringSimple(): String {
    return "0x${Integer.toHexString(address.toInt())}"
  }

  fun toBinaryString(): String {
    return address.toString(2).padStart(Int.SIZE_BITS, '0')
  }
}
