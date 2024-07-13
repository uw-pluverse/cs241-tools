package org.pluverse.cs241.emulator

import org.junit.Assert.assertThrows
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.pluverse.cs241.emulator.cpumodel.*
import java.lang.Math.pow
import kotlin.math.pow

/**
 * We want to test each Mips Instruction individually and verify execution.
 *
 *
 */
@RunWith(JUnit4::class)
class MipsInstructionTests {
    /**
     * Create some generic values to test the instructions. These are instructions with register values
     * and immediate values set to 0.
     */
    companion object {
        // How to set specific values for testing
        // ... (or regS shl 21) or (regT shl 16) or (regD shl 11)
        // Immediate is first 16 bits

        // R-format instructions
        const val ADD = (0b00000000000000000000000000100000)
        const val SUB = (0b00000000000000000000000000100010)
        const val MULT = (0b00000000000000000000000000011000)
        const val MULTU = (0b00000000000000000000000000011001)
        const val DIV = (0b00000000000000000000000000011010)
        const val DIVU = (0b00000000000000000000000000011011)
        const val MFHI = (0b00000000000000000000000000010000)
        const val MFLO = (0b00000000000000000000000000010010)
        const val LIS = (0b00000000000000000000000000010100)
        const val SLT = (0b00000000000000000000000000101010)
        const val SLTU = (0b00000000000000000000000000101011)
        const val JR = (0b00000000000000000000000000001000)
        const val JALR = (0b00000000000000000000000000001001)

        // I-format instructions
        const val LW = (0b10001100000000000000000000000000)
        const val SW = (0b10101100000000000000000000000000)
        const val BEQ = (0b00010000000000000000000000000000)
        const val BNE = (0b00010100000000000000000000000000)

        // J-format instructions
        const val WORD = 0b00000000000000000000000000000000
        const val OTHERWORD = 0b11111111111111111111111111111111

        /**
         * Takes a base instruction with all values expected to be already (from above companion)
         * and inserts required variables.
         *
         * Requires: Register to be 0-31, and imm to be 16 bits two's complement.
         *
         * [-2^15, 2^15 - 1] = [-31768, 31767]
         */
        @JvmStatic
        fun modifyInstr(instr: Int, regS: Int? = null, regT: Int? = null, regD: Int? = null, imm: Int? = null): Int {
            var ret = instr

            assert((regS == null || regS in 0..31) &&
                (regT == null || regT in 0..31) &&
                (regD == null || regD in 0..31) &&
                (imm == null || imm in -32768..32767)
            )

            if (regS != null) ret = ret or (regS shl 21)
            if (regT != null) ret = ret or (regT shl 16)
            if (regD != null) ret = ret or (regD shl 11)
            if (imm != null) ret = (ret and 0xffff0000.toInt()) or (imm and 0xffff)

            return ret
        }
    }

    @Test
    fun testAddInstructionReadsOnlyAddInstruction() {
        assertThrows(WrongMipsInstructionException::class.java) { AddInstruction(SUB) }
        assertThrows(WrongMipsInstructionException::class.java) { AddInstruction(MULT) }
        assertThrows(WrongMipsInstructionException::class.java) { AddInstruction(MULTU) }
        assertThrows(WrongMipsInstructionException::class.java) { AddInstruction(DIV) }
        assertThrows(WrongMipsInstructionException::class.java) { AddInstruction(DIVU) }
        assertThrows(WrongMipsInstructionException::class.java) { AddInstruction(MFHI) }
        assertThrows(WrongMipsInstructionException::class.java) { AddInstruction(MFLO) }
        assertThrows(WrongMipsInstructionException::class.java) { AddInstruction(LIS) }
        assertThrows(WrongMipsInstructionException::class.java) { AddInstruction(SLT) }
        assertThrows(WrongMipsInstructionException::class.java) { AddInstruction(SLTU) }
        assertThrows(WrongMipsInstructionException::class.java) { AddInstruction(JR) }
        assertThrows(WrongMipsInstructionException::class.java) { AddInstruction(JALR) }
        assertThrows(WrongMipsInstructionException::class.java) { AddInstruction(LW.toInt()) }
        assertThrows(WrongMipsInstructionException::class.java) { AddInstruction(SW.toInt()) }

        val modifiedAddOperandErrorInstr = modifyInstr(ADD, imm = 30)
        assertThrows(WrongMipsInstructionException::class.java) { AddInstruction(modifiedAddOperandErrorInstr) }

        val modifiedAddOpcodeErrorInstr = 0xf0000000.toInt() or ADD
        assertThrows(WrongMipsInstructionException::class.java) { AddInstruction(modifiedAddOpcodeErrorInstr) }

        run {
            AddInstruction(ADD)
        }
    }

    @Test
    fun testAddInstructionRegisterUpdates() {
        run {
            val addInstruction = AddInstruction(ADD)
            assertEquals(0, addInstruction.regS)
            assertEquals(0, addInstruction.regT)
            assertEquals(0, addInstruction.regD)
            assertEquals(0b00000100000, addInstruction.operand)
            assertEquals(0b000000, addInstruction.opcode)
        }
        run {
            val addInstruction = AddInstruction(modifyInstr(ADD, 1, 2, 3))
            assertEquals(1, addInstruction.regS)
            assertEquals(2, addInstruction.regT)
            assertEquals(3, addInstruction.regD)
        }
    }

    @Test
    fun testSubInstructionReadsOnlySubInstruction() {
        assertThrows(WrongMipsInstructionException::class.java) { SubInstruction(ADD) }
        assertThrows(WrongMipsInstructionException::class.java) { SubInstruction(MULT) }
        assertThrows(WrongMipsInstructionException::class.java) { SubInstruction(MULTU) }
        assertThrows(WrongMipsInstructionException::class.java) { SubInstruction(DIV) }
        assertThrows(WrongMipsInstructionException::class.java) { SubInstruction(DIVU) }
        assertThrows(WrongMipsInstructionException::class.java) { SubInstruction(MFHI) }
        assertThrows(WrongMipsInstructionException::class.java) { SubInstruction(MFLO) }
        assertThrows(WrongMipsInstructionException::class.java) { SubInstruction(LIS) }
        assertThrows(WrongMipsInstructionException::class.java) { SubInstruction(SLT) }
        assertThrows(WrongMipsInstructionException::class.java) { SubInstruction(SLTU) }
        assertThrows(WrongMipsInstructionException::class.java) { SubInstruction(JR) }
        assertThrows(WrongMipsInstructionException::class.java) { SubInstruction(JALR) }
        assertThrows(WrongMipsInstructionException::class.java) { SubInstruction(LW.toInt()) }
        assertThrows(WrongMipsInstructionException::class.java) { SubInstruction(SW.toInt()) }

        val modifiedSubOperandErrorInstr = modifyInstr(SUB, imm = 31)
        assertThrows(WrongMipsInstructionException::class.java) { SubInstruction(modifiedSubOperandErrorInstr) }

        val modifiedSubOpcodeErrorInstr = 0xf0000000.toInt() or SUB
        assertThrows(WrongMipsInstructionException::class.java) { SubInstruction(modifiedSubOpcodeErrorInstr) }

        run {
            SubInstruction(SUB)
        }
    }

    @Test
    fun testSubInstructionRegisterUpdates() {
        run {
            val subInstruction = SubInstruction(SUB)
            assertEquals(0, subInstruction.regS)
            assertEquals(0, subInstruction.regT)
            assertEquals(0, subInstruction.regD)
            assertEquals(0b00000100010, subInstruction.operand)
            assertEquals(0b000000, subInstruction.opcode)
        }
        run {
            val subInstruction = SubInstruction(modifyInstr(SUB, 1, 2, 3))
            assertEquals(1, subInstruction.regS)
            assertEquals(2, subInstruction.regT)
            assertEquals(3, subInstruction.regD)
        }
    }

    @Test
    fun testMultiplyInstructionReadsOnlyMultiplyInstruction() {
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyInstruction(ADD) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyInstruction(SUB) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyInstruction(MULTU) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyInstruction(DIV) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyInstruction(DIVU) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyInstruction(MFHI) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyInstruction(MFLO) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyInstruction(LIS) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyInstruction(SLT) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyInstruction(SLTU) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyInstruction(JR) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyInstruction(JALR) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyInstruction(LW.toInt()) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyInstruction(SW.toInt()) }

        run {
            val multInstruction = MultiplyInstruction(MULT)
            assertEquals(0, multInstruction.regS)
            assertEquals(0, multInstruction.regT)
            assertEquals(0, multInstruction.regD)
            assertEquals(0b00000000000000000000000000011000, multInstruction.operand)
            assertEquals(0b000000, multInstruction.opcode)
        }
    }

    @Test
    fun testMultiplyInstructionRegisterUpdates() {
        run {
            val multInstruction = MultiplyInstruction(MULT)
            assertEquals(0, multInstruction.regS)
            assertEquals(0, multInstruction.regT)
            assertEquals(0, multInstruction.regD)
            assertEquals(0b00000000000000000000000000011000, multInstruction.operand)
            assertEquals(0b000000, multInstruction.opcode)
        }
        run {
            val multInstruction = MultiplyInstruction(modifyInstr(MULT, 1, 2))
            assertEquals(1, multInstruction.regS)
            assertEquals(2, multInstruction.regT)
        }
    }

    @Test
    fun testMultiplyUInstructionReadsOnlyMultiplyUInstruction() {
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyUInstruction(ADD) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyUInstruction(SUB) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyUInstruction(MULT) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyUInstruction(DIV) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyUInstruction(DIVU) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyUInstruction(MFHI) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyUInstruction(MFLO) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyUInstruction(LIS) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyUInstruction(SLT) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyUInstruction(SLTU) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyUInstruction(JR) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyUInstruction(JALR) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyUInstruction(LW.toInt()) }
        assertThrows(WrongMipsInstructionException::class.java) { MultiplyUInstruction(SW.toInt()) }

        run {
            val multuInstruction = MultiplyUInstruction(MULTU)
            assertEquals(0, multuInstruction.regS)
            assertEquals(0, multuInstruction.regT)
            assertEquals(0, multuInstruction.regD)
            assertEquals(0b00000000000000000000000000011001, multuInstruction.operand)
            assertEquals(0b000000, multuInstruction.opcode)
        }
    }

    @Test
    fun testMultiplyUInstructionRegisterUpdates() {
        run {
            val multuInstruction = MultiplyUInstruction(MULTU)
            assertEquals(0, multuInstruction.regS)
            assertEquals(0, multuInstruction.regT)
            assertEquals(0, multuInstruction.regD)
            assertEquals(0b00000000000000000000000000011001, multuInstruction.operand)
            assertEquals(0b000000, multuInstruction.opcode)
        }
        run {
            val multuInstruction = MultiplyUInstruction(modifyInstr(MULTU, 1, 2))
            assertEquals(1, multuInstruction.regS)
            assertEquals(2, multuInstruction.regT)
        }
    }

    @Test
    fun testDivideInstructionReadsOnlyDivideInstruction() {
        assertThrows(WrongMipsInstructionException::class.java) { DivideInstruction(ADD) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideInstruction(SUB) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideInstruction(MULT) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideInstruction(MULTU) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideInstruction(DIVU) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideInstruction(MFHI) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideInstruction(MFLO) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideInstruction(LIS) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideInstruction(SLT) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideInstruction(SLTU) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideInstruction(JR) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideInstruction(JALR) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideInstruction(LW.toInt()) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideInstruction(SW.toInt()) }

        run {
            val divInstruction = DivideInstruction(DIV)
            assertEquals(0, divInstruction.regS)
            assertEquals(0, divInstruction.regT)
            assertEquals(0, divInstruction.regD)
            assertEquals(0b00000000000000000000000000011010, divInstruction.operand)
            assertEquals(0b000000, divInstruction.opcode)
        }
    }

    @Test
    fun testDivideInstructionRegisterUpdates() {
        run {
            val divInstruction = DivideInstruction(DIV)
            assertEquals(0, divInstruction.regS)
            assertEquals(0, divInstruction.regT)
            assertEquals(0, divInstruction.regD)
            assertEquals(0b00000000000000000000000000011010, divInstruction.operand)
            assertEquals(0b000000, divInstruction.opcode)
        }
        run {
            val divInstruction = DivideInstruction(modifyInstr(DIV, 1, 2))
            assertEquals(1, divInstruction.regS)
            assertEquals(2, divInstruction.regT)
        }
    }
    
    @Test
    fun testDivideUInstructionReadsOnlyDivideUInstruction() {
        assertThrows(WrongMipsInstructionException::class.java) { DivideUInstruction(ADD) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideUInstruction(SUB) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideUInstruction(MULT) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideUInstruction(MULTU) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideUInstruction(DIV) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideUInstruction(MFHI) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideUInstruction(MFLO) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideUInstruction(LIS) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideUInstruction(SLT) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideUInstruction(SLTU) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideUInstruction(JR) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideUInstruction(JALR) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideUInstruction(LW.toInt()) }
        assertThrows(WrongMipsInstructionException::class.java) { DivideUInstruction(SW.toInt()) }

        run {
            val divuInstruction = DivideUInstruction(DIVU)
            assertEquals(0, divuInstruction.regS)
            assertEquals(0, divuInstruction.regT)
            assertEquals(0, divuInstruction.regD)
            assertEquals(0b00000000000000000000000000011011, divuInstruction.operand)
            assertEquals(0b000000, divuInstruction.opcode)
        }
    }

    @Test
    fun testDivideUInstructionRegisterUpdates() {
        run {
            val divuInstruction = DivideUInstruction(DIVU)
            assertEquals(0, divuInstruction.regS)
            assertEquals(0, divuInstruction.regT)
            assertEquals(0, divuInstruction.regD)
            assertEquals(0b00000000000000000000000000011011, divuInstruction.operand)
            assertEquals(0b000000, divuInstruction.opcode)
        }
        run {
            val divuInstruction = DivideUInstruction(modifyInstr(DIVU, 1, 2))
            assertEquals(1, divuInstruction.regS)
            assertEquals(2, divuInstruction.regT)
        }
    }
    
    @Test
    fun testMoveHighInstructionReadsOnlyMoveHighInstruction() {
        assertThrows(WrongMipsInstructionException::class.java) { MoveHighInstruction(ADD) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveHighInstruction(SUB) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveHighInstruction(MULT) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveHighInstruction(MULTU) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveHighInstruction(DIV) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveHighInstruction(DIVU) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveHighInstruction(MFLO) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveHighInstruction(LIS) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveHighInstruction(SLT) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveHighInstruction(SLTU) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveHighInstruction(JR) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveHighInstruction(JALR) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveHighInstruction(LW.toInt()) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveHighInstruction(SW.toInt()) }

        run {
            val mfhiInstruction = MoveHighInstruction(MFHI)
            assertEquals(0, mfhiInstruction.regS)
            assertEquals(0, mfhiInstruction.regT)
            assertEquals(0, mfhiInstruction.regD)
            assertEquals(0b00000000000000000000000000010000, mfhiInstruction.operand)
            assertEquals(0b000000, mfhiInstruction.opcode)
        }
    }

    @Test
    fun testMoveHighInstructionRegisterUpdates() {
        run {
            val mfhiInstruction = MoveHighInstruction(MFHI)
            assertEquals(0, mfhiInstruction.regS)
            assertEquals(0, mfhiInstruction.regT)
            assertEquals(0, mfhiInstruction.regD)
            assertEquals(0b00000000000000000000000000010000, mfhiInstruction.operand)
            assertEquals(0b000000, mfhiInstruction.opcode)
        }
        run {
            val mfhiInstruction = MoveHighInstruction(modifyInstr(MFHI, regD = 5))
            assertEquals(5, mfhiInstruction.regD)
        }
    }

    @Test
    fun testMoveLowInstructionReadsOnlyMoveLowInstruction() {
        assertThrows(WrongMipsInstructionException::class.java) { MoveLowInstruction(ADD) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveLowInstruction(SUB) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveLowInstruction(MULT) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveLowInstruction(MULTU) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveLowInstruction(DIV) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveLowInstruction(DIVU) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveLowInstruction(MFHI) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveLowInstruction(LIS) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveLowInstruction(SLT) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveLowInstruction(SLTU) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveLowInstruction(JR) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveLowInstruction(JALR) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveLowInstruction(LW.toInt()) }
        assertThrows(WrongMipsInstructionException::class.java) { MoveLowInstruction(SW.toInt()) }

        run {
            val mfloInstruction = MoveLowInstruction(MFLO)
            assertEquals(0, mfloInstruction.regS)
            assertEquals(0, mfloInstruction.regT)
            assertEquals(0, mfloInstruction.regD)
            assertEquals(0b00000000000000000000000000010010, mfloInstruction.operand)
            assertEquals(0b000000, mfloInstruction.opcode)
        }
    }

    @Test
    fun testMoveLowInstructionRegisterUpdates() {
        run {
            val mfloInstruction = MoveLowInstruction(MFLO)
            assertEquals(0, mfloInstruction.regS)
            assertEquals(0, mfloInstruction.regT)
            assertEquals(0, mfloInstruction.regD)
            assertEquals(0b00000000000000000000000000010010, mfloInstruction.operand)
            assertEquals(0b000000, mfloInstruction.opcode)
        }
        run {
            val mfloInstruction = MoveLowInstruction(modifyInstr(MFLO, regD = 5))
            assertEquals(5, mfloInstruction.regD)
        }
    }
    
    @Test
    fun testLisInstructionReadsOnlyLisInstruction() {
        assertThrows(WrongMipsInstructionException::class.java) { LisInstruction(ADD) }
        assertThrows(WrongMipsInstructionException::class.java) { LisInstruction(SUB) }
        assertThrows(WrongMipsInstructionException::class.java) { LisInstruction(MULT) }
        assertThrows(WrongMipsInstructionException::class.java) { LisInstruction(MULTU) }
        assertThrows(WrongMipsInstructionException::class.java) { LisInstruction(DIV) }
        assertThrows(WrongMipsInstructionException::class.java) { LisInstruction(DIVU) }
        assertThrows(WrongMipsInstructionException::class.java) { LisInstruction(MFHI) }
        assertThrows(WrongMipsInstructionException::class.java) { LisInstruction(MFLO) }
        assertThrows(WrongMipsInstructionException::class.java) { LisInstruction(SLT) }
        assertThrows(WrongMipsInstructionException::class.java) { LisInstruction(SLTU) }
        assertThrows(WrongMipsInstructionException::class.java) { LisInstruction(JR) }
        assertThrows(WrongMipsInstructionException::class.java) { LisInstruction(JALR) }
        assertThrows(WrongMipsInstructionException::class.java) { LisInstruction(LW.toInt()) }
        assertThrows(WrongMipsInstructionException::class.java) { LisInstruction(SW.toInt()) }

        run {
            val lisInstruction = LisInstruction(LIS)
            assertEquals(0, lisInstruction.regS)
            assertEquals(0, lisInstruction.regT)
            assertEquals(0, lisInstruction.regD)
            assertEquals(0b00000000000000000000000000010100, lisInstruction.operand)
            assertEquals(0b000000, lisInstruction.opcode)
        }
    }

    @Test
    fun testLisInstructionRegisterUpdates() {
        run {
            val lisInstruction = LisInstruction(LIS)
            assertEquals(0, lisInstruction.regS)
            assertEquals(0, lisInstruction.regT)
            assertEquals(0, lisInstruction.regD)
            assertEquals(0b00000000000000000000000000010100, lisInstruction.operand)
            assertEquals(0b000000, lisInstruction.opcode)
        }
        run {
            val lisInstruction = LisInstruction(modifyInstr(LIS, regD = 5))
            assertEquals(5, lisInstruction.regD)
        }
    }
    
    @Test
    fun testSetLessThanInstructionReadsOnlySetLessThanInstruction() {
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanInstruction(ADD) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanInstruction(SUB) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanInstruction(MULT) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanInstruction(MULTU) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanInstruction(DIV) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanInstruction(DIVU) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanInstruction(MFHI) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanInstruction(MFLO) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanInstruction(LIS) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanInstruction(SLTU) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanInstruction(JR) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanInstruction(JALR) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanInstruction(LW.toInt()) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanInstruction(SW.toInt()) }

        run {
            val sltInstruction = SetLessThanInstruction(SLT)
            assertEquals(0, sltInstruction.regS)
            assertEquals(0, sltInstruction.regT)
            assertEquals(0, sltInstruction.regD)
            assertEquals(0b00000000000000000000000000101010, sltInstruction.operand)
            assertEquals(0b000000, sltInstruction.opcode)
        }
    }

    @Test
    fun testSetLessThanInstructionRegisterUpdates() {
        run {
            val sltInstruction = SetLessThanInstruction(SLT)
            assertEquals(0, sltInstruction.regS)
            assertEquals(0, sltInstruction.regT)
            assertEquals(0, sltInstruction.regD)
            assertEquals(0b00000000000000000000000000101010, sltInstruction.operand)
            assertEquals(0b000000, sltInstruction.opcode)
        }
        run {
            val sltInstruction = SetLessThanInstruction(modifyInstr(SLT, 1, 2, 3))
            assertEquals(1, sltInstruction.regS)
            assertEquals(2, sltInstruction.regT)
            assertEquals(3, sltInstruction.regD)
        }
    }

    @Test
    fun testSetLessThanUInstructionReadsOnlySetLessThanUInstruction() {
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanUInstruction(ADD) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanUInstruction(SUB) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanUInstruction(MULT) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanUInstruction(MULTU) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanUInstruction(DIV) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanUInstruction(DIVU) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanUInstruction(MFHI) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanUInstruction(MFLO) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanUInstruction(LIS) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanUInstruction(SLT) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanUInstruction(JR) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanUInstruction(JALR) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanUInstruction(LW.toInt()) }
        assertThrows(WrongMipsInstructionException::class.java) { SetLessThanUInstruction(SW.toInt()) }

        run {
            val sltuInstruction = SetLessThanUInstruction(SLTU)
            assertEquals(0, sltuInstruction.regS)
            assertEquals(0, sltuInstruction.regT)
            assertEquals(0, sltuInstruction.regD)
            assertEquals(0b00000000000000000000000000101011, sltuInstruction.operand)
            assertEquals(0b000000, sltuInstruction.opcode)
        }
    }

    @Test
    fun testSetLessThanUInstructionRegisterUpdates() {
        run {
            val sltuInstruction = SetLessThanUInstruction(SLTU)
            assertEquals(0, sltuInstruction.regS)
            assertEquals(0, sltuInstruction.regT)
            assertEquals(0, sltuInstruction.regD)
            assertEquals(0b00000000000000000000000000101011, sltuInstruction.operand)
            assertEquals(0b000000, sltuInstruction.opcode)
        }
        run {
            val sltuInstruction = SetLessThanUInstruction(modifyInstr(SLTU, 1, 2, 3))
            assertEquals(1, sltuInstruction.regS)
            assertEquals(2, sltuInstruction.regT)
            assertEquals(3, sltuInstruction.regD)
        }
    }
    
    @Test
    fun testJumpInstructionReadsOnlyJumpInstruction() {
        assertThrows(WrongMipsInstructionException::class.java) { JumpInstruction(ADD) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpInstruction(SUB) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpInstruction(MULT) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpInstruction(MULTU) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpInstruction(DIV) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpInstruction(DIVU) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpInstruction(MFHI) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpInstruction(MFLO) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpInstruction(LIS) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpInstruction(SLT) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpInstruction(SLTU) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpInstruction(JALR) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpInstruction(LW.toInt()) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpInstruction(SW.toInt()) }

        run {
            val jrInstruction = JumpInstruction(JR)
            assertEquals(0, jrInstruction.regS)
            assertEquals(0, jrInstruction.regT)
            assertEquals(0, jrInstruction.regD)
            assertEquals(0b00000000000000000000000000001000, jrInstruction.operand)
            assertEquals(0b000000, jrInstruction.opcode)
        }
    }

    @Test
    fun testJumpInstructionRegisterUpdates() {
        run {
            val jrInstruction = JumpInstruction(JR)
            assertEquals(0, jrInstruction.regS)
            assertEquals(0, jrInstruction.regT)
            assertEquals(0, jrInstruction.regD)
            assertEquals(0b00000000000000000000000000001000, jrInstruction.operand)
            assertEquals(0b000000, jrInstruction.opcode)
        }
        run {
            val jrInstruction = JumpInstruction(modifyInstr(JR, 1))
            assertEquals(1, jrInstruction.regS)
        }
    }

    @Test
    fun testJumpAndLinkInstructionReadsOnlyJumpAndLinkInstruction() {
        assertThrows(WrongMipsInstructionException::class.java) { JumpAndLinkInstruction(ADD) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpAndLinkInstruction(SUB) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpAndLinkInstruction(MULT) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpAndLinkInstruction(MULTU) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpAndLinkInstruction(DIV) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpAndLinkInstruction(DIVU) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpAndLinkInstruction(MFHI) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpAndLinkInstruction(MFLO) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpAndLinkInstruction(LIS) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpAndLinkInstruction(SLT) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpAndLinkInstruction(SLTU) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpAndLinkInstruction(JR) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpAndLinkInstruction(LW.toInt()) }
        assertThrows(WrongMipsInstructionException::class.java) { JumpAndLinkInstruction(SW.toInt()) }

        run {
            val jalrInstruction = JumpAndLinkInstruction(JALR)
            assertEquals(0, jalrInstruction.regS)
            assertEquals(0, jalrInstruction.regT)
            assertEquals(0, jalrInstruction.regD)
            assertEquals(0b00000000000000000000000000001001, jalrInstruction.operand)
            assertEquals(0b000000, jalrInstruction.opcode)
        }
    }

    @Test
    fun testJumpAndLinkInstructionRegisterUpdates() {
        run {
            val jalrInstruction = JumpAndLinkInstruction(JALR)
            assertEquals(0, jalrInstruction.regS)
            assertEquals(0, jalrInstruction.regT)
            assertEquals(0, jalrInstruction.regD)
            assertEquals(0b00000000000000000000000000001001, jalrInstruction.operand)
            assertEquals(0b000000, jalrInstruction.opcode)
        }
        run {
            val jalrInstruction = JumpAndLinkInstruction(modifyInstr(JALR, 1))
            assertEquals(1, jalrInstruction.regS)
        }
    }
    
    @Test
    fun testLoadInstructionReadsOnlyLoadInstruction() {
        assertThrows(WrongMipsInstructionException::class.java) { LoadWordInstruction(ADD) }
        assertThrows(WrongMipsInstructionException::class.java) { LoadWordInstruction(SUB) }
        assertThrows(WrongMipsInstructionException::class.java) { LoadWordInstruction(MULT) }
        assertThrows(WrongMipsInstructionException::class.java) { LoadWordInstruction(MULTU) }
        assertThrows(WrongMipsInstructionException::class.java) { LoadWordInstruction(DIV) }
        assertThrows(WrongMipsInstructionException::class.java) { LoadWordInstruction(DIVU) }
        assertThrows(WrongMipsInstructionException::class.java) { LoadWordInstruction(MFHI) }
        assertThrows(WrongMipsInstructionException::class.java) { LoadWordInstruction(MFLO) }
        assertThrows(WrongMipsInstructionException::class.java) { LoadWordInstruction(LIS) }
        assertThrows(WrongMipsInstructionException::class.java) { LoadWordInstruction(SLT) }
        assertThrows(WrongMipsInstructionException::class.java) { LoadWordInstruction(SLTU) }
        assertThrows(WrongMipsInstructionException::class.java) { LoadWordInstruction(JR) }
        assertThrows(WrongMipsInstructionException::class.java) { LoadWordInstruction(JALR) }
        assertThrows(WrongMipsInstructionException::class.java) { LoadWordInstruction(SW.toInt()) }

        run {
            val lwInstruction = LoadWordInstruction(LW.toInt())
            assertEquals(0, lwInstruction.regS)
            assertEquals(0, lwInstruction.regT)
            assertEquals(0, lwInstruction.regD)
            assertEquals(0b100011, lwInstruction.opcode)
        }
    }

    @Test
    fun testLoadInstructionRegisterUpdates() {
        run {
            val lwInstruction = LoadWordInstruction(LW.toInt())
            assertEquals(0, lwInstruction.regS)
            assertEquals(0, lwInstruction.regT)
            assertEquals(0, lwInstruction.regD)
            assertEquals(0b100011, lwInstruction.opcode)
        }
        run {
            val lwInstruction = LoadWordInstruction(modifyInstr(LW.toInt(), imm = 2.0.pow(15.0).toInt() - 1))
            assertEquals(2.0.pow(15.0).toInt() - 1, lwInstruction.immediate)

            val lwInstruction2 = LoadWordInstruction(modifyInstr(LW.toInt(),31, imm = -pow(2.0, 15.0).toInt()))
            assertEquals(31, lwInstruction2.regS)
            assertEquals(-pow(2.0, 15.0).toInt(), lwInstruction2.immediate)

            val lwInstructionReg = LoadWordInstruction(modifyInstr(LW.toInt(), 1, 2))
            assertEquals(1, lwInstructionReg.regS)
            assertEquals(2, lwInstructionReg.regT)
        }
    }

    @Test
    fun testSaveInstructionReadsOnlySaveInstruction() {
        assertThrows(WrongMipsInstructionException::class.java) { StoreWordInstruction(ADD) }
        assertThrows(WrongMipsInstructionException::class.java) { StoreWordInstruction(SUB) }
        assertThrows(WrongMipsInstructionException::class.java) { StoreWordInstruction(MULT) }
        assertThrows(WrongMipsInstructionException::class.java) { StoreWordInstruction(MULTU) }
        assertThrows(WrongMipsInstructionException::class.java) { StoreWordInstruction(DIV) }
        assertThrows(WrongMipsInstructionException::class.java) { StoreWordInstruction(DIVU) }
        assertThrows(WrongMipsInstructionException::class.java) { StoreWordInstruction(MFHI) }
        assertThrows(WrongMipsInstructionException::class.java) { StoreWordInstruction(MFLO) }
        assertThrows(WrongMipsInstructionException::class.java) { StoreWordInstruction(LIS) }
        assertThrows(WrongMipsInstructionException::class.java) { StoreWordInstruction(SLT) }
        assertThrows(WrongMipsInstructionException::class.java) { StoreWordInstruction(SLTU) }
        assertThrows(WrongMipsInstructionException::class.java) { StoreWordInstruction(JR) }
        assertThrows(WrongMipsInstructionException::class.java) { StoreWordInstruction(JALR) }
        assertThrows(WrongMipsInstructionException::class.java) { StoreWordInstruction(LW.toInt()) }

        run {
            val swInstruction = StoreWordInstruction(SW.toInt())
            assertEquals(0, swInstruction.regS)
            assertEquals(0, swInstruction.regT)
            assertEquals(0, swInstruction.regD)
            assertEquals(0b101011, swInstruction.opcode)
        }
    }

    @Test
    fun testSaveInstructionRegisterUpdates() {
        run {
            val swInstruction = StoreWordInstruction(SW.toInt())
            assertEquals(0, swInstruction.regS)
            assertEquals(0, swInstruction.regT)
            assertEquals(0, swInstruction.regD)
            assertEquals(0b101011, swInstruction.opcode)
        }
        run {
            val swInstruction = StoreWordInstruction(modifyInstr(SW.toInt(), imm = 2.0.pow(15.0).toInt() - 1))
            assertEquals(2.0.pow(15.0).toInt() - 1, swInstruction.immediate)

            val swInstruction2 = StoreWordInstruction(modifyInstr(SW.toInt(),31, imm = -2.0.pow(15.0).toInt()))
            assertEquals(31, swInstruction2.regS)
            assertEquals(-2.0.pow(15.0).toInt(), swInstruction2.immediate)

            val swInstructionReg = StoreWordInstruction(modifyInstr(SW.toInt(), 1, 2))
            assertEquals(1, swInstructionReg.regS)
            assertEquals(2, swInstructionReg.regT)
        }
    }

    @Test
    fun testBranchEqualInstructionReadsOnlyBranchEqualInstruction() {
        assertThrows(WrongMipsInstructionException::class.java) { BranchEqualInstruction(ADD) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchEqualInstruction(SUB) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchEqualInstruction(MULT) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchEqualInstruction(MULTU) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchEqualInstruction(DIV) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchEqualInstruction(DIVU) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchEqualInstruction(MFHI) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchEqualInstruction(MFLO) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchEqualInstruction(LIS) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchEqualInstruction(SLT) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchEqualInstruction(SLTU) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchEqualInstruction(JR) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchEqualInstruction(JALR) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchEqualInstruction(LW.toInt()) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchEqualInstruction(SW.toInt()) }

        run {
            val beqInstruction = BranchEqualInstruction(BEQ)
            assertEquals(0, beqInstruction.regS)
            assertEquals(0, beqInstruction.regT)
            assertEquals(0, beqInstruction.regD)
            assertEquals(0b000100, beqInstruction.opcode)
        }
    }

    @Test
    fun testBranchEqualInstructionRegisterUpdates() {
        run {
            val beqInstruction = BranchEqualInstruction(BEQ)
            assertEquals(0, beqInstruction.regS)
            assertEquals(0, beqInstruction.regT)
            assertEquals(0, beqInstruction.regD)
            assertEquals(0b000100, beqInstruction.opcode)
        }
        run {
            val beqInstruction = BranchEqualInstruction(modifyInstr(BEQ, 1, 2, imm = 2.0.pow(15.0).toInt() - 1))
            assertEquals(1, beqInstruction.regS)
            assertEquals(2, beqInstruction.regT)
            assertEquals(2.0.pow(15.0).toInt() - 1, beqInstruction.immediate)

            val beqInstruction2 = BranchEqualInstruction(modifyInstr(BEQ, 1, 2, imm = -2.0.pow(15.0).toInt()))
            assertEquals(1, beqInstruction2.regS)
            assertEquals(2, beqInstruction2.regT)
            assertEquals(-2.0.pow(15.0).toInt(), beqInstruction2.immediate)
        }
    }

    @Test
    fun testBranchNotEqualInstructionReadsOnlyBranchNotEqualInstruction() {
        assertThrows(WrongMipsInstructionException::class.java) { BranchNotEqualInstruction(ADD) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchNotEqualInstruction(SUB) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchNotEqualInstruction(MULT) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchNotEqualInstruction(MULTU) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchNotEqualInstruction(DIV) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchNotEqualInstruction(DIVU) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchNotEqualInstruction(MFHI) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchNotEqualInstruction(MFLO) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchNotEqualInstruction(LIS) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchNotEqualInstruction(SLT) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchNotEqualInstruction(SLTU) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchNotEqualInstruction(JR) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchNotEqualInstruction(JALR) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchNotEqualInstruction(LW.toInt()) }
        assertThrows(WrongMipsInstructionException::class.java) { BranchNotEqualInstruction(SW.toInt()) }

        run {
            val bneInstruction = BranchNotEqualInstruction(BNE)
            assertEquals(0, bneInstruction.regS)
            assertEquals(0, bneInstruction.regT)
            assertEquals(0, bneInstruction.regD)
            assertEquals(0b000101, bneInstruction.opcode)
        }
    }

    @Test
    fun testBranchNotEqualInstructionRegisterUpdates() {
        run {
            val bneInstruction = BranchNotEqualInstruction(BNE)
            assertEquals(0, bneInstruction.regS)
            assertEquals(0, bneInstruction.regT)
            assertEquals(0, bneInstruction.regD)
            assertEquals(0b000101, bneInstruction.opcode)
        }
        run {
            val bneInstruction = BranchNotEqualInstruction(modifyInstr(BNE, 1, 2, imm = 2.0.pow(15.0).toInt() - 1))
            assertEquals(1, bneInstruction.regS)
            assertEquals(2, bneInstruction.regT)
            assertEquals(2.0.pow(15.0).toInt() - 1, bneInstruction.immediate)

            val bneInstruction2 = BranchNotEqualInstruction(modifyInstr(BNE, 1, 2, imm = -2.0.pow(15.0).toInt()))
            assertEquals(1, bneInstruction2.regS)
            assertEquals(2, bneInstruction2.regT)
            assertEquals(-2.0.pow(15.0).toInt(), bneInstruction2.immediate)
        }
    }


}

