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
import org.pluverse.cs241.emulator.cpumodel.AddInstruction
import org.pluverse.cs241.emulator.cpumodel.Address
import org.pluverse.cs241.emulator.cpumodel.BranchEqualInstruction
import org.pluverse.cs241.emulator.cpumodel.BranchNotEqualInstruction
import org.pluverse.cs241.emulator.cpumodel.DivideInstruction
import org.pluverse.cs241.emulator.cpumodel.DivideUInstruction
import org.pluverse.cs241.emulator.cpumodel.InvalidAddressException
import org.pluverse.cs241.emulator.cpumodel.JumpAndLinkInstruction
import org.pluverse.cs241.emulator.cpumodel.JumpInstruction
import org.pluverse.cs241.emulator.cpumodel.LoadWordInstruction
import org.pluverse.cs241.emulator.cpumodel.MoveHighInstruction
import org.pluverse.cs241.emulator.cpumodel.MoveLowInstruction
import org.pluverse.cs241.emulator.cpumodel.MultiplyInstruction
import org.pluverse.cs241.emulator.cpumodel.MultiplyUInstruction
import org.pluverse.cs241.emulator.cpumodel.OutsideAddressRangeException
import org.pluverse.cs241.emulator.cpumodel.Registers
import org.pluverse.cs241.emulator.cpumodel.SetLessThanInstruction
import org.pluverse.cs241.emulator.cpumodel.SetLessThanUInstruction
import org.pluverse.cs241.emulator.cpumodel.StoreWordInstruction
import org.pluverse.cs241.emulator.cpumodel.SubInstruction

@RunWith(JUnit4::class)
class MipsInstructionExecuteTests {

    private val registers = Array(34) { 0 }

    /**
     * Memory is an array of 1024 integers. Each index is address / 4
     */
    private val memory = Array(1024) { 0 }
    private var pc: Address = Address()

    private fun getReg(register: Int): Int = if (register == 0) 0 else registers[register]
    private fun getMem(address: Address): Int = memory[address()]
    private fun updateReg(register: Int, value: Int) { registers[register] = value }
    private fun updateMem(address: Address, value: Int) { memory[address()] = value }
    private fun setPC(init: ((currentPC: Address) -> Address)) {
        pc = init(pc) // We return the new PC based on the function
    }

    /**
     * Add instructions should add register values from 2nd & 3rd registers and store in 1st register.
     */
    @Test
    fun addInstructionUpdatesRegisterCorrectly() {
        run {
            // Test default add 0 to 0 to 0
            val addInstruction = AddInstruction(MipsInstructionTests.ADD)
            addInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0, registers[0])
        }

        run {
            // Separate registers
            val addInstruction = AddInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.ADD, 3, 2, 1))
            registers[1] = 5
            registers[2] = 10
            registers[3] = 15
            addInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )

            assertEquals(25, registers[1])
            assertEquals(10, registers[2])
            assertEquals(15, registers[3])
        }

        run {
            // Add register to itself
            val addInstruction = AddInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.ADD, 3, 3, 3))
            registers[3] = 5
            addInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )

            assertEquals(10, registers[3])
        }

        run {
            // Add itself with another and save
            val addInstruction = AddInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.ADD, 3, 2, 3))
            registers[2] = 10
            registers[3] = 5
            addInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )

            assertEquals(15, registers[3])
        }
    }

    @Test
    fun subInstructionUpdatesRegisterCorrectly() {
        run {
            // Test default sub 0 from 0 to 0
            val subInstruction = SubInstruction(MipsInstructionTests.SUB)
            subInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0, registers[0])
        }

        run {
            // Separate registers
            val subInstruction = SubInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SUB, 3, 2, 1))
            registers[1] = 5
            registers[2] = 10
            registers[3] = 15
            subInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )

            assertEquals(5, registers[1])
            assertEquals(10, registers[2])
            assertEquals(15, registers[3])
        }

        run {
            // Subtract register from itself
            val subInstruction = SubInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SUB, 3, 3, 3))
            registers[3] = 5
            subInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )

            assertEquals(0, registers[3])
        }

        run {
            // Subtract itself from another and save
            val subInstruction = SubInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SUB, 3, 2, 3))
            registers[2] = 10
            registers[3] = 5
            subInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )

            assertEquals(-5, registers[3])
        }
    }

    @Test
    fun multiplyInstructionUpdatesRegisterCorrectly() {
        run {
            // Normal
            val multiplyInstruction = MultiplyInstruction(MipsInstructionTests.MULT)
            multiplyInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0, registers[Registers.LO_INDEX])
            assertEquals(0, registers[Registers.HI_INDEX])
        }

        run {
            // Test multiply 2 positives
            val multiplyInstruction = MultiplyInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.MULT, 2, 1))
            registers[2] = 25
            registers[1] = 5
            multiplyInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(125, registers[Registers.LO_INDEX])
            assertEquals(0, registers[Registers.HI_INDEX])
        }

        run {
            // Test multiply neg and pos
            val multiplyInstruction = MultiplyInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.MULT, 2, 1))
            registers[2] = 25
            registers[1] = -5
            multiplyInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(-125, registers[Registers.LO_INDEX])
            assertEquals(-1, registers[Registers.HI_INDEX])
        }

        run {
            // mult int max to -1
            val multiplyInstruction = MultiplyInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.MULT, 2, 1))
            registers[2] = Int.MAX_VALUE
            registers[1] = -1
            multiplyInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(-Int.MAX_VALUE, registers[Registers.LO_INDEX])
            assertEquals(-1, registers[Registers.HI_INDEX])
        }

        run {
            // mult int max to 1
            val multiplyInstruction = MultiplyInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.MULT, 2, 1))
            registers[2] = Int.MAX_VALUE
            registers[1] = 1
            multiplyInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(Int.MAX_VALUE, registers[Registers.LO_INDEX])
            assertEquals(0, registers[Registers.HI_INDEX])
        }

        run {
            // mult int min to -1
            val multiplyInstruction = MultiplyInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.MULT, 2, 1))
            registers[2] = Int.MIN_VALUE
            registers[1] = -1
            multiplyInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(Int.MIN_VALUE, registers[Registers.LO_INDEX])
            assertEquals(0, registers[Registers.HI_INDEX])
        }

        run {
            // Test multiplication of int max * 2. should have unsigned variation in LO
            val multiplyInstruction = MultiplyInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.MULT, 2, 1))
            registers[2] = Int.MAX_VALUE
            registers[1] = 2
            multiplyInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(-2, registers[Registers.LO_INDEX])
            assertEquals(0, registers[Registers.HI_INDEX])
        }

        run {
            // Test overflow
            val multiplyInstruction = MultiplyInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.MULT, 2, 1))
            registers[2] = Int.MAX_VALUE
            registers[1] = 4
            multiplyInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(-4, registers[Registers.LO_INDEX])
            assertEquals(1, registers[Registers.HI_INDEX])
        }
    }

    @Test
    fun divideInstructionUpdatesRegisterCorrectly() {
        // Note: Divisor goes into LO and remainder into HI

        run {
            // Test divide 2 positives
            val divideInstruction = DivideInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.DIV, 2, 1))
            registers[2] = 125
            registers[1] = 5
            divideInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(25, registers[Registers.LO_INDEX])
            assertEquals(0, registers[Registers.HI_INDEX])
        }

        run {
            // Test divide neg and pos
            val divideInstruction = DivideInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.DIV, 2, 1))
            registers[2] = 125
            registers[1] = -5
            divideInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(-25, registers[Registers.LO_INDEX])
            assertEquals(0, registers[Registers.HI_INDEX])
        }

        run {
            // div int max to -1
            val divideInstruction = DivideInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.DIV, 2, 1))
            registers[2] = Int.MAX_VALUE
            registers[1] = -1
            divideInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(-Int.MAX_VALUE, registers[Registers.LO_INDEX])
            assertEquals(0, registers[Registers.HI_INDEX])
        }

        run {
            // div int max to 1
            val divideInstruction = DivideInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.DIV, 2, 1))
            registers[2] = Int.MAX_VALUE
            registers[1] = 1
            divideInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(Int.MAX_VALUE, registers[Registers.LO_INDEX])
            assertEquals(0, registers[Registers.HI_INDEX])
        }
    }

    @Test
    fun multiplyUInstructionUpdatesRegisterCorrectly() {
        run {
            // Normal
            val multiplyInstruction = MultiplyUInstruction(MipsInstructionTests.MULTU)
            multiplyInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(registers[32], 0)
            assertEquals(registers[33], 0)
        }

        run {
            // Test multiply 2 positives
            val multiplyInstruction =
                MultiplyUInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.MULTU, 2, 1))
            registers[2] = 25
            registers[1] = 5
            multiplyInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(125, registers[Registers.LO_INDEX])
            assertEquals(0, registers[Registers.HI_INDEX])
        }

        run {
            // Test multiply neg and pos
            val multiplyInstruction =
                MultiplyUInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.MULTU, 2, 1))
            registers[2] = 25
            registers[1] = -5
            multiplyInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(-125, registers[Registers.LO_INDEX])
            assertEquals(24, registers[Registers.HI_INDEX])
        }

        run {
            // mult two negatives
            val multiplyInstruction =
                MultiplyUInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.MULTU, 2, 1))
            registers[2] = 0x80000000.toInt()
            registers[1] = 0x80000001.toInt()
            multiplyInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(registers[Registers.LO_INDEX], -2147483648)
            assertEquals(registers[Registers.HI_INDEX], 1073741824)
        }

        run {
            // multiply into HI
            val multiplyInstruction =
                MultiplyUInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.MULTU, 2, 1))
            registers[2] = 0x80000000.toInt()
            registers[1] = 2
            multiplyInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(registers[Registers.LO_INDEX], 0)
            assertEquals(registers[Registers.HI_INDEX], 1)
        }
    }

    @Test
    fun divideUInstructionUpdatesRegisterCorrectly() {
        // Note: Divisor goes into LO and remainder into HI

        run {
            // Test divide 2 positives
            val divideInstruction = DivideUInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.DIVU, 2, 1))
            registers[2] = 125
            registers[1] = 5
            divideInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(registers[Registers.LO_INDEX], 25)
            assertEquals(registers[Registers.HI_INDEX], 0)
        }

        run {
            // Test divide neg and pos
            val divideInstruction = DivideUInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.DIVU, 2, 1))
            registers[2] = 125
            registers[1] = -5
            divideInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(registers[Registers.LO_INDEX], 0)
            assertEquals(registers[Registers.HI_INDEX], 125)
        }

        run {
            // Neg becomes unsigned
            val divideInstruction = DivideUInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.DIVU, 2, 1))
            registers[2] = -5
            registers[1] = 125
            divideInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(registers[Registers.LO_INDEX], 34359738)
            assertEquals(registers[Registers.HI_INDEX], 41)
        }

        run {
            // div big negative
            val divideInstruction = DivideUInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.DIVU, 2, 1))
            registers[2] = -4
            registers[1] = 0x7ffffffe
            divideInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(registers[Registers.LO_INDEX], 2)
            assertEquals(registers[Registers.HI_INDEX], 0)
        }

        run {
            // div int min to 1
            val divideInstruction = DivideUInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.DIVU, 2, 1))
            registers[2] = Int.MIN_VALUE
            registers[1] = 1
            divideInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(registers[Registers.LO_INDEX], Int.MIN_VALUE)
            assertEquals(registers[Registers.HI_INDEX], 0)
        }

        run {
            // Divide two negative numbers converted to unsigned
            val divideInstruction = DivideUInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.DIVU, 2, 1))
            registers[2] = -5
            registers[1] = -125
            divideInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(registers[Registers.LO_INDEX], 1)
            assertEquals(registers[Registers.HI_INDEX], 120)
        }
    }

    @Test
    fun setLessThanInstructionUpdatesRegisterCorrectly() {
        run {
            // Test default slt 0 < 0
            val sltInstruction = SetLessThanInstruction(MipsInstructionTests.SLT)
            sltInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0, registers[0])
        }

        run {
            // Test slt 5 < 10
            val sltInstruction =
                SetLessThanInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SLT, 1, 2, 3))
            registers[1] = 5
            registers[2] = 10
            registers[3] = 0
            sltInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(1, registers[3])
        }

        run {
            // Test slt 10 < 5
            val sltInstruction =
                SetLessThanInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SLT, 1, 2, 3))
            registers[1] = 10
            registers[2] = 5
            registers[3] = 0
            sltInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0, registers[3])
        }

        run {
            // Test slt 5 < 5
            val sltInstruction =
                SetLessThanInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SLT, 1, 2, 3))
            registers[1] = 5
            registers[2] = 5
            registers[3] = 0
            sltInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0, registers[3])
        }

        // Now tests with a mix of negative numbers
        run {
            // Test slt -5 < 10
            val sltInstruction =
                SetLessThanInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SLT, 1, 2, 3))
            registers[1] = -5
            registers[2] = 10
            registers[3] = 0
            sltInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(1, registers[3])
        }

        run {
            // Test slt 10 < -5
            val sltInstruction =
                SetLessThanInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SLT, 1, 2, 3))
            registers[1] = 10
            registers[2] = -5
            registers[3] = 0
            sltInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0, registers[3])
        }

        run {
            // Test slt -5 < -5
            val sltInstruction =
                SetLessThanInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SLT, 1, 2, 3))
            registers[1] = -5
            registers[2] = -5
            registers[3] = 0
            sltInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0, registers[3])
        }

        run {
            // Test slt -5 < -10
            val sltInstruction =
                SetLessThanInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SLT, 1, 2, 3))
            registers[1] = -5
            registers[2] = -10
            registers[3] = 0
            sltInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0, registers[3])
        }

        run {
            // Test slt -10 < -5
            val sltInstruction =
                SetLessThanInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SLT, 1, 2, 3))
            registers[1] = -10
            registers[2] = -5
            registers[3] = 0
            sltInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(1, registers[3])
        }
    }

    @Test
    fun setLessThanUInstructionUpdatesRegisterCorrectly() {
        run {
            // Test default sltu 0 < 0
            val sltuInstruction = SetLessThanUInstruction(MipsInstructionTests.SLTU)
            sltuInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0, registers[0])
        }

        run {
            // Test sltu 5 < 10
            val sltuInstruction =
                SetLessThanUInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SLTU, 1, 2, 3))
            registers[1] = 5
            registers[2] = 10
            registers[3] = 0
            sltuInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(1, registers[3])
        }

        run {
            // Test sltu 10 < 5
            val sltuInstruction =
                SetLessThanUInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SLTU, 1, 2, 3))
            registers[1] = 10
            registers[2] = 5
            registers[3] = 0
            sltuInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0, registers[3])
        }

        run {
            // Test sltu 5 < 5
            val sltuInstruction =
                SetLessThanUInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SLTU, 1, 2, 3))
            registers[1] = 5
            registers[2] = 5
            registers[3] = 0
            sltuInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0, registers[3])
        }

        // Now tests with a mix of negative numbers
        run {
            // Test sltu -5 < 10
            val sltuInstruction =
                SetLessThanUInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SLTU, 1, 2, 3))
            registers[1] = -5
            registers[2] = 10
            registers[3] = 0
            sltuInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0, registers[3])
        }

        run {
            // Test sltu 10 < -5
            val sltuInstruction =
                SetLessThanUInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SLTU, 1, 2, 3))
            registers[1] = 10
            registers[2] = -5
            registers[3] = 0
            sltuInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(1, registers[3])
        }
    }

    @Test
    fun jumpInstructionUpdatesRegisterCorrectly() {
        run {
            // Test default jump
            val jumpInstruction = JumpInstruction(MipsInstructionTests.JR)
            jumpInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0u, pc.address)
        }

        run {
            // Test jump to 12
            val jumpInstruction = JumpInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.JR, 1))
            pc = Address()
            registers[1] = 12
            jumpInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(12u, pc.address)
        }

        // Test jump 0
        run {
            val jumpInstruction = JumpInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.JR, 1))
            pc = Address(12u)
            registers[1] = 0
            pc = Address()
            jumpInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0u, pc.address)
        }

        // Test jump 4 starting at address 12u
        run {
            val jumpInstruction = JumpInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.JR, 1))
            pc = Address(12u)
            registers[1] = 4
            pc = Address()
            jumpInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(4u, pc.address)
        }

        // Test throws error going into negative address or address not divisible by four
        run {
            val jumpInstruction = JumpInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.JR, 1))
            pc = Address(12u)
            registers[1] = -1
            pc = Address()
            assertThrows(InvalidAddressException::class.java) {
                jumpInstruction.execute(
                    ::getReg,
                    ::getMem,
                    ::updateReg,
                    ::updateMem,
                    ::setPC,
                )
            }
        }

        run {
            val jumpInstruction = JumpInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.JR, 1))
            pc = Address(12u)
            registers[1] = 3
            pc = Address()
            assertThrows(InvalidAddressException::class.java) {
                jumpInstruction.execute(
                    ::getReg,
                    ::getMem,
                    ::updateReg,
                    ::updateMem,
                    ::setPC,
                )
            }
        }
    }

    @Test
    fun jumpAndLinkInstructionUpdatesRegisterCorrectly() {
        run {
            // Test default jump
            val jumpInstruction = JumpAndLinkInstruction(MipsInstructionTests.JALR)
            jumpInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0u, pc.address)
            assertEquals(0, registers[31])
        }

        run {
            // Test jump to 12
            val jumpInstruction = JumpAndLinkInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.JALR, 1))
            pc = Address(24u)
            registers[1] = 12
            jumpInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(12u, pc.address)
            assertEquals(24, registers[31])
        }

        // Test jump 0
        run {
            val jumpInstruction = JumpAndLinkInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.JALR, 1))
            pc = Address(12u)
            registers[1] = 0
            jumpInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0u, pc.address)
            assertEquals(12, registers[31])
        }

        // Test jump 4 starting at address 12u
        run {
            val jumpInstruction = JumpAndLinkInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.JALR, 1))
            pc = Address(12u)
            registers[1] = 4
            jumpInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(4u, pc.address)
            assertEquals(12, registers[31])
        }

        // Test throws error going into negative address or address not divisible by four
        run {
            val jumpInstruction = JumpAndLinkInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.JALR, 1))
            pc = Address(12u)
            registers[1] = -1
            pc = Address()
            assertThrows(InvalidAddressException::class.java) {
                jumpInstruction.execute(
                    ::getReg,
                    ::getMem,
                    ::updateReg,
                    ::updateMem,
                    ::setPC,
                )
            }
        }

        run {
            val jumpInstruction = JumpAndLinkInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.JALR, 1))
            pc = Address(12u)
            registers[1] = 3
            pc = Address()
            assertThrows(InvalidAddressException::class.java) {
                jumpInstruction.execute(
                    ::getReg,
                    ::getMem,
                    ::updateReg,
                    ::updateMem,
                    ::setPC,
                )
            }
        }
    }

    @Test
    fun branchOnEqualInstructionUpdatesRegisterCorrectly() {
        run {
            // Test default beq
            val beqInstruction = BranchEqualInstruction(MipsInstructionTests.BEQ)
            beqInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0u, pc.address)
        }

        run {
            // Test beq 5 == 5
            val beqInstruction =
                BranchEqualInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.BEQ, 1, 2, imm = 3))
            pc = Address()
            registers[1] = 5
            registers[2] = 5
            beqInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(12u, pc.address)
        }

        run {
            // Test beq 5 != 10
            val beqInstruction =
                BranchEqualInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.BEQ, 1, 2, imm = 3))
            registers[1] = 5
            registers[2] = 10
            pc = Address(12u)
            beqInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(12u, pc.address)
        }

        run {
            // Test beq -5 == -5
            val beqInstruction =
                BranchEqualInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.BEQ, 1, 2, imm = 5))
            registers[1] = -5
            registers[2] = -5
            pc = Address(4u)
            beqInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(24u, pc.address)
        }

        run {
            // Test beq -5 != -10
            val beqInstruction =
                BranchEqualInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.BEQ, 1, 2, imm = 16))
            registers[1] = -5
            registers[2] = -10
            pc = Address()
            beqInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0u, pc.address)
        }

        // Test going backwards
        run {
            // Test beq 5 == 5
            val beqInstruction =
                BranchEqualInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.BEQ, 1, 2, imm = -3))
            pc = Address(12u)
            registers[1] = 5
            registers[2] = 5
            beqInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0u, pc.address)
        }

        // Test going backwards v2
        run {
            // Test beq 5 == 5
            val beqInstruction =
                BranchEqualInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.BEQ, 1, 2, imm = -1))
            pc = Address(48u)
            registers[1] = 5
            registers[2] = 5
            beqInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(44u, pc.address)
        }

        // Test throwing invalid address exception when going into negatives
        run {
            // Test beq 5 == 5
            val beqInstruction =
                BranchEqualInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.BEQ, 1, 2, imm = -5))
            pc = Address(12u)
            registers[1] = 5
            registers[2] = 5
            assertThrows(OutsideAddressRangeException::class.java) {
                beqInstruction.execute(
                    ::getReg,
                    ::getMem,
                    ::updateReg,
                    ::updateMem,
                    ::setPC,
                )
            }
        }
    }

    @Test
    fun branchOnNotEqualInstructionUpdatesRegisterCorrectly() {
        run {
            // Test default bne
            val bneInstruction = BranchNotEqualInstruction(MipsInstructionTests.BNE)
            bneInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0u, pc.address)
        }

        run {
            // Test bne 5 != 10
            val bneInstruction =
                BranchNotEqualInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.BNE, 1, 2, imm = 3))
            pc = Address()
            registers[1] = 5
            registers[2] = 10
            bneInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(12u, pc.address)
        }

        run {
            // Test bne 5 == 5
            val bneInstruction =
                BranchNotEqualInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.BNE, 1, 2, imm = 3))
            registers[1] = 5
            registers[2] = 5
            pc = Address(12u)
            bneInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(12u, pc.address)
        }

        run {
            // Test bne -5 != -10
            val bneInstruction =
                BranchNotEqualInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.BNE, 1, 2, imm = 15))
            registers[1] = -5
            registers[2] = -10
            pc = Address(4u)
            bneInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(64u, pc.address)
        }

        // Test going backwards
        run {
            // Test bne 5 != 10
            val bneInstruction =
                BranchNotEqualInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.BNE, 1, 2, imm = -3))
            pc = Address(12u)
            registers[1] = 5
            registers[2] = 10
            bneInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0u, pc.address)
        }

        // Test going backwards v2
        run {
            // Test bne 5 != 10
            val bneInstruction =
                BranchNotEqualInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.BNE, 1, 2, imm = -1))
            pc = Address(48u)
            registers[1] = 5
            registers[2] = 10
            bneInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(44u, pc.address)
        }

        // Test throwing invalid address exception when going into negatives
        run {
            // Test bne 5 != 10
            val bneInstruction =
                BranchNotEqualInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.BNE, 1, 2, imm = -5))
            pc = Address(12u)
            registers[1] = 5
            registers[2] = 10
            assertThrows(OutsideAddressRangeException::class.java) {
                bneInstruction.execute(
                    ::getReg,
                    ::getMem,
                    ::updateReg,
                    ::updateMem,
                    ::setPC,
                )
            }
        }
    }

    @Test
    fun loadWordInstructionUpdatesRegisterCorrectly() {
        run {
            // Test default lw
            val lwInstruction = LoadWordInstruction(MipsInstructionTests.LW.toInt())
            lwInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0, registers[0])
        }

        run {
            // Test bad $s and 6 to make a divisible by 4 address
            val lwInstruction = LoadWordInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.LW.toInt(), 1, 2, imm = 6))
            registers[2] = 5
            registers[1] = 10
            memory[4] = 16
            lwInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(16, registers[2])
        }

        // Test two divisible by four numbers
        run {
            val lwInstruction = LoadWordInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.LW.toInt(), 1, 2, imm = 4))
            registers[2] = 5
            registers[1] = 4
            memory[2] = 23
            lwInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(23, registers[2])
        }

        // Test pos imm with neg reg 1
        run {
            val lwInstruction = LoadWordInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.LW.toInt(), 1, 2, imm = 4))
            registers[2] = 5
            registers[1] = -4
            memory[0] = 23
            lwInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(23, registers[2])
        }

        // Test neg imm with pos reg 1
        run {
            val lwInstruction = LoadWordInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.LW.toInt(), 1, 2, imm = -4))
            registers[2] = 5
            registers[1] = 4
            memory[0] = Int.MIN_VALUE
            lwInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(Int.MIN_VALUE, registers[2])
        }

        // Test invalid address exceptions cuz $s
        run {
            val lwInstruction = LoadWordInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.LW.toInt(), 1, 2, imm = 4))
            registers[2] = 5
            registers[1] = 3
            assertThrows(InvalidAddressException::class.java) {
                lwInstruction.execute(
                    ::getReg,
                    ::getMem,
                    ::updateReg,
                    ::updateMem,
                    ::setPC,
                )
            }
        }

        // Test invalid address exceptions cuz imm
        run {
            val lwInstruction = LoadWordInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.LW.toInt(), 1, 2, imm = 3))
            registers[2] = 5
            registers[1] = 4
            assertThrows(InvalidAddressException::class.java) {
                lwInstruction.execute(
                    ::getReg,
                    ::getMem,
                    ::updateReg,
                    ::updateMem,
                    ::setPC,
                )
            }
        }

        // Test invalid address exceptions cuz neg $s => neg address
        run {
            val lwInstruction = LoadWordInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.LW.toInt(), 1, 2, imm = 4))
            registers[2] = 5
            registers[1] = -16
            assertThrows(OutsideAddressRangeException::class.java) {
                lwInstruction.execute(
                    ::getReg,
                    ::getMem,
                    ::updateReg,
                    ::updateMem,
                    ::setPC,
                )
            }
        }

        // Test invalid address exceptions cuz neg imm => neg address
        run {
            val lwInstruction = LoadWordInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.LW.toInt(), 1, 2, imm = -16))
            registers[2] = 5
            registers[1] = 4
            assertThrows(OutsideAddressRangeException::class.java) {
                lwInstruction.execute(
                    ::getReg,
                    ::getMem,
                    ::updateReg,
                    ::updateMem,
                    ::setPC,
                )
            }
        }
    }

    @Test
    fun storeWordInstructionUpdatesRegisterCorrectly() {
        run {
            // Test default sw
            val swInstruction = StoreWordInstruction(MipsInstructionTests.SW.toInt())
            swInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0, memory[0])
        }

        run {
            // Test bad $s and 6 to make a divisible by 4 address
            val swInstruction =
                StoreWordInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SW.toInt(), 1, 2, imm = 6))
            registers[2] = 5
            registers[1] = 10
            swInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(5, memory[4])
        }

        // Test two divisible by four numbers
        run {
            val swInstruction =
                StoreWordInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SW.toInt(), 1, 2, imm = 4))
            registers[2] = 5
            registers[1] = 4
            swInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(5, memory[2])
        }

        // Test pos imm with neg reg 1
        run {
            val swInstruction =
                StoreWordInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SW.toInt(), 1, 2, imm = 4))
            registers[2] = 5
            registers[1] = -4
            swInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(5, memory[0])
        }

        // Test neg imm with pos reg 1
        run {
            val swInstruction =
                StoreWordInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SW.toInt(), 1, 2, imm = -4))
            registers[2] = 5
            registers[1] = 4
            swInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(5, memory[0])
        }

        // Test invalid address exceptions cuz $s
        run {
            val swInstruction =
                StoreWordInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SW.toInt(), 1, 2, imm = 3))
            registers[2] = 5
            registers[1] = 3
            assertThrows(InvalidAddressException::class.java) {
                swInstruction.execute(
                    ::getReg,
                    ::getMem,
                    ::updateReg,
                    ::updateMem,
                    ::setPC,
                )
            }
        }

        // Test invalid address exceptions cuz imm
        run {
            val swInstruction =
                StoreWordInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SW.toInt(), 1, 2, imm = 3))
            registers[2] = 5
            registers[1] = 4
            assertThrows(InvalidAddressException::class.java) {
                swInstruction.execute(
                    ::getReg,
                    ::getMem,
                    ::updateReg,
                    ::updateMem,
                    ::setPC,
                )
            }
        }

        // Test invalid address exceptions cuz neg $s => neg address
        run {
            val swInstruction =
                StoreWordInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SW.toInt(), 1, 2, imm = 4))
            registers[2] = 5
            registers[1] = -16
            assertThrows(OutsideAddressRangeException::class.java) {
                swInstruction.execute(
                    ::getReg,
                    ::getMem,
                    ::updateReg,
                    ::updateMem,
                    ::setPC,
                )
            }
        }

        // Test invalid address exceptions cuz neg imm => neg address
        run {
            val swInstruction =
                StoreWordInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.SW.toInt(), 1, 2, imm = -16))
            registers[2] = 5
            registers[1] = 4
            assertThrows(OutsideAddressRangeException::class.java) {
                swInstruction.execute(
                    ::getReg,
                    ::getMem,
                    ::updateReg,
                    ::updateMem,
                    ::setPC,
                )
            }
        }
    }

    @Test
    fun moveHighInstructionUpdatesRegisterCorrectly() {
        run {
            // Test default mfhi
            val mfhiInstruction = MoveHighInstruction(MipsInstructionTests.MFHI)
            mfhiInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0, registers[0])
        }

        run {
            // Test mfhi 5
            val mfhiInstruction = MoveHighInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.MFHI, regD = 5))
            registers[5] = 10
            registers[Registers.HI_INDEX] = 5
            mfhiInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(5, registers[5])
        }

        run {
            // Test mfhi 5 with negative number
            val mfhiInstruction = MoveHighInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.MFHI, regD = 5))
            registers[5] = 10
            registers[Registers.HI_INDEX] = -5
            mfhiInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(-5, registers[5])
        }

        run {
            // Test mfhi 5 with zero
            val mfhiInstruction = MoveHighInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.MFHI, regD = 5))
            registers[5] = 10
            registers[Registers.HI_INDEX] = 0
            mfhiInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0, registers[5])
        }
    }

    @Test
    fun moveLowInstructionUpdatesRegisterCorrectly() {
        run {
            // Test default mflo
            val mfloInstruction = MoveLowInstruction(MipsInstructionTests.MFLO)
            mfloInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0, registers[0])
        }

        run {
            // Test mflo 5
            val mfloInstruction = MoveLowInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.MFLO, regD = 5))
            registers[5] = 10
            registers[Registers.LO_INDEX] = 5
            mfloInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(5, registers[5])
        }

        run {
            // Test mflo 5 with negative number
            val mfloInstruction = MoveLowInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.MFLO, regD = 5))
            registers[5] = 10
            registers[Registers.LO_INDEX] = -5
            mfloInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(-5, registers[5])
        }

        run {
            // Test mflo 5 with zero
            val mfloInstruction = MoveLowInstruction(MipsInstructionTests.modifyInstr(MipsInstructionTests.MFLO, regD = 5))
            registers[5] = 10
            registers[Registers.LO_INDEX] = 0
            mfloInstruction.execute(
                ::getReg,
                ::getMem,
                ::updateReg,
                ::updateMem,
                ::setPC,
            )
            assertEquals(0, registers[5])
        }
    }
}
