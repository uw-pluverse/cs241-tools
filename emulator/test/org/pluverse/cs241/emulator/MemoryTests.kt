package org.pluverse.cs241.emulator
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.Assert.*

import org.pluverse.cs241.emulator.cpumodel.*

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
        val address = Memory.getAddress(0) // Assuming getAddress converts index to address correctly
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

class RamMemoryTest {

    @Test
    fun getDataReturnsCorrectMipsInstructionData() {
        val ramMemory = RamMemory(10) // Small size for testing
        val data = ramMemory.getData(0)
        assertEquals(0u, data.address.getAddressBits())
    }

    @Test
    fun getUsingAddressReturnsCorrectMipsInstructionData() {
        val ramMemory = RamMemory(10)
        val address = Memory.getAddress(0) // Assuming getAddress converts index to address correctly
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