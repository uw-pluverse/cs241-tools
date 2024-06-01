package org.pluverse.cs241.emulator

/*
An abstract class that provides functionality for an instruction based on the given doubleWord

 */
abstract class MipsInstruction(val doubleWord: Int, expectedOpcode: Int?, expectedOperand: Int?) {

    /*
    Important information about the MipsInstruction
     */

    // opcode: the first 6 bits - identifies the type
    val opcode: Int = (doubleWord shr 26)

    // operand: the last 11 bits - identifies the specific operation
    val operand: Int = doubleWord and (0b11111111111).toInt()

    // these are the register values
    val regS = (doubleWord shr 21) and (0b11111).toInt()
    val regT = (doubleWord shr 16) and (0b11111).toInt()
    val regD = (doubleWord shr 11) and (0b11111).toInt()

    // immediate value
    val immediate = (doubleWord and 0xffff)

    abstract fun getSyntax(): String // Returns the mips syntax for the operation
    abstract fun run(
        getMem: (index: Memory.Companion.Address) -> Int,
        getReg: (index: Memory.Companion.Address) -> Int,
        updateReg: (index: Memory.Companion.Address, value: Int) -> Void,
        updateMem: (index: Memory.Companion.Address, value: Int) -> Void
    ) // This will run the instruction and update memory

    init {
        // Verify the opcode and operand is correct
        if ((expectedOpcode != null && opcode != expectedOpcode) || (expectedOperand != null && operand != expectedOperand))
            throw WrongMipsInstructionException()
    }
}

/*
Three register instructions below

 */


abstract class ThreeRegisterInstruction(
    val identifier: String,
    doubleWord: Int,
    expectedOpcode: Int,
    expectedOperand: Int?
) : MipsInstruction(doubleWord, expectedOpcode, expectedOperand) {

    override fun getSyntax(): String {
        return "${identifier} $${regD}, $${regS}, $${regT}"
    }
}

class AddInstruction(doubleWord: Int) : ThreeRegisterInstruction("add", doubleWord, OPCODE, OPERAND) {

    override fun run(
        getMem: (index: Memory.Companion.Address) -> Int,
        getReg: (index: Memory.Companion.Address) -> Int,
        updateReg: (index: Memory.Companion.Address, value: Int) -> Void,
        updateMem: (index: Memory.Companion.Address, value: Int) -> Void
    ) {

    }

    companion object {
        const val OPCODE: Int = 0
        const val OPERAND: Int = (0b00000100000)
    }
}

class SubInstruction(doubleWord: Int) : ThreeRegisterInstruction("sub", doubleWord, OPCODE, OPERAND) {

    override fun run(
        getMem: (index: Memory.Companion.Address) -> Int,
        getReg: (index: Memory.Companion.Address) -> Int,
        updateReg: (index: Memory.Companion.Address, value: Int) -> Void,
        updateMem: (index: Memory.Companion.Address, value: Int) -> Void
    ) {

    }

    companion object {
        const val OPCODE: Int = 0
        const val OPERAND: Int = (0b00000100010)
    }
}

class SetLessThanInstruction(doubleWord: Int) : ThreeRegisterInstruction("slt", doubleWord, OPCODE, OPERAND) {

    override fun run(
        getMem: (index: Memory.Companion.Address) -> Int,
        getReg: (index: Memory.Companion.Address) -> Int,
        updateReg: (index: Memory.Companion.Address, value: Int) -> Void,
        updateMem: (index: Memory.Companion.Address, value: Int) -> Void
    ) {

    }

    companion object {
        const val OPCODE: Int = 0
        const val OPERAND: Int = (0b00000101010)
    }
}

class SetLessThanUInstruction(doubleWord: Int) : ThreeRegisterInstruction("sltu", doubleWord, OPCODE, OPERAND) {

    override fun run(
        getMem: (index: Memory.Companion.Address) -> Int,
        getReg: (index: Memory.Companion.Address) -> Int,
        updateReg: (index: Memory.Companion.Address, value: Int) -> Void,
        updateMem: (index: Memory.Companion.Address, value: Int) -> Void
    ) {

    }

    companion object {
        const val OPCODE: Int = 0
        const val OPERAND: Int = (0b00000101011)
    }
}

/*
End three register instructions

Two register instructions below
 */

abstract class TwoRegisterInstruction(
    val identifier: String,
    doubleWord: Int,
    expectedOpcode: Int,
    expectedOperand: Int?
) : MipsInstruction(doubleWord, expectedOpcode, expectedOperand) {

    override fun getSyntax(): String {
        return "${identifier} $${regS}, $${regT}"
    }
}

class MultiplyInstruction(doubleWord: Int) : TwoRegisterInstruction("mult", doubleWord, OPCODE, OPERAND) {

    override fun run(
        getMem: (index: Memory.Companion.Address) -> Int,
        getReg: (index: Memory.Companion.Address) -> Int,
        updateReg: (index: Memory.Companion.Address, value: Int) -> Void,
        updateMem: (index: Memory.Companion.Address, value: Int) -> Void
    ) {

    }

    companion object {
        const val OPCODE: Int = 0
        const val OPERAND: Int = (0b00000011000)
    }
}

class MultiplyUInstruction(doubleWord: Int) : TwoRegisterInstruction("multu", doubleWord, OPCODE, OPERAND) {

    override fun run(
        getMem: (index: Memory.Companion.Address) -> Int,
        getReg: (index: Memory.Companion.Address) -> Int,
        updateReg: (index: Memory.Companion.Address, value: Int) -> Void,
        updateMem: (index: Memory.Companion.Address, value: Int) -> Void
    ) {

    }

    companion object {
        const val OPCODE: Int = 0
        const val OPERAND: Int = (0b00000011001)
    }
}

class DivideInstruction(doubleWord: Int) : TwoRegisterInstruction("div", doubleWord, OPCODE, OPERAND) {

    override fun run(
        getMem: (index: Memory.Companion.Address) -> Int,
        getReg: (index: Memory.Companion.Address) -> Int,
        updateReg: (index: Memory.Companion.Address, value: Int) -> Void,
        updateMem: (index: Memory.Companion.Address, value: Int) -> Void
    ) {

    }

    companion object {
        const val OPCODE: Int = 0
        const val OPERAND: Int = (0b00000011010)
    }
}

class DivideUInstruction(doubleWord: Int) : TwoRegisterInstruction("divu", doubleWord, OPCODE, OPERAND) {

    override fun run(
        getMem: (index: Memory.Companion.Address) -> Int,
        getReg: (index: Memory.Companion.Address) -> Int,
        updateReg: (index: Memory.Companion.Address, value: Int) -> Void,
        updateMem: (index: Memory.Companion.Address, value: Int) -> Void
    ) {

    }

    companion object {
        const val OPCODE: Int = 0
        const val OPERAND: Int = (0b00000011011)
    }
}

/*
End two register instructions

Branch instructions below
 */

abstract class BranchInstruction(
    val identifier: String,
    doubleWord: Int,
    expectedOpcode: Int,
    expectedOperand: Int?
) : MipsInstruction(doubleWord, expectedOpcode, expectedOperand) {

    override fun getSyntax(): String {
        return "${identifier} $${regS}, $${regT}, ${immediate}"
    }
}

class BranchEqualInstruction(doubleWord: Int) : BranchInstruction("beq", doubleWord, OPCODE, null) {

    override fun run(
        getMem: (index: Memory.Companion.Address) -> Int,
        getReg: (index: Memory.Companion.Address) -> Int,
        updateReg: (index: Memory.Companion.Address, value: Int) -> Void,
        updateMem: (index: Memory.Companion.Address, value: Int) -> Void
    ) {
        TODO("Not yet implemented")
    }

    companion object {
        const val OPCODE: Int = (0b000100)
        
    }
}

class BranchNotEqualInstruction(doubleWord: Int) : BranchInstruction("bne", doubleWord, OPCODE, null) {

    override fun run(
        getMem: (index: Memory.Companion.Address) -> Int,
        getReg: (index: Memory.Companion.Address) -> Int,
        updateReg: (index: Memory.Companion.Address, value: Int) -> Void,
        updateMem: (index: Memory.Companion.Address, value: Int) -> Void
    ) {
        TODO("Not yet implemented")
    }

    companion object {
        const val OPCODE: Int = (0b000101)
        
    }
}

/*
End Branch instructions

Load/Save Word below
 */

abstract class DataInstruction(
    val identifier: String,
    doubleWord: Int,
    expectedOpcode: Int,
    expectedOperand: Int?
) : MipsInstruction(doubleWord, expectedOpcode, expectedOperand) {

    override fun getSyntax(): String {
        return "${identifier} $${regT}, ${immediate}($${regS})"
    }

    init {
        // Verify that first 16 digits are 0
        if (opcode != 0 || regS != 0 || regT != 0) throw BadCodeException("Data instruction")
    }
}

class LoadInstruction(doubleWord: Int) : DataInstruction("lw", doubleWord, OPCODE, null) {
    override fun run(
        getMem: (index: Memory.Companion.Address) -> Int,
        getReg: (index: Memory.Companion.Address) -> Int,
        updateReg: (index: Memory.Companion.Address, value: Int) -> Void,
        updateMem: (index: Memory.Companion.Address, value: Int) -> Void
    ) {
        TODO("Not yet implemented")
    }

    companion object {
        const val OPCODE: Int = (0b100011)
    }
}

class SaveInstruction(doubleWord: Int) : DataInstruction("sw", doubleWord, OPCODE, null) {
    override fun run(
        getMem: (index: Memory.Companion.Address) -> Int,
        getReg: (index: Memory.Companion.Address) -> Int,
        updateReg: (index: Memory.Companion.Address, value: Int) -> Void,
        updateMem: (index: Memory.Companion.Address, value: Int) -> Void
    ) {
        TODO("Not yet implemented")
    }

    companion object {
        const val OPCODE: Int = (0b101011)
        
    }
}

/*
End Load/Save word instructions

Rest of single register instructions below
 */

class MoveHighInstruction(doubleWord: Int) : MipsInstruction(doubleWord, OPCODE, OPERAND) {
    override fun getSyntax(): String {
        return "mfhi $${regD}"
    }

    override fun run(
        getMem: (index: Memory.Companion.Address) -> Int,
        getReg: (index: Memory.Companion.Address) -> Int,
        updateReg: (index: Memory.Companion.Address, value: Int) -> Void,
        updateMem: (index: Memory.Companion.Address, value: Int) -> Void
    ) {
        TODO("Not yet implemented")
    }

    companion object {
        const val OPCODE: Int = 0
        const val OPERAND: Int = (0b00000010000)
    }
}

class MoveLowInstruction(doubleWord: Int) : MipsInstruction(doubleWord, OPCODE, OPERAND) {
    override fun getSyntax(): String {
        return "mflo $${regD}"
    }

    override fun run(
        getMem: (index: Memory.Companion.Address) -> Int,
        getReg: (index: Memory.Companion.Address) -> Int,
        updateReg: (index: Memory.Companion.Address, value: Int) -> Void,
        updateMem: (index: Memory.Companion.Address, value: Int) -> Void
    ) {
        TODO("Not yet implemented")
    }

    companion object {
        const val OPCODE: Int = 0
        const val OPERAND: Int = (0b00000010010)
    }
}

class LisInstruction(doubleWord: Int, val instrAddress: Memory.Companion.Address)
    : MipsInstruction(doubleWord, null, OPERAND) {

    override fun getSyntax(): String {
        return "lis $${regD}"
    }

    override fun run(
        getMem: (index: Memory.Companion.Address) -> Int,
        getReg: (index: Memory.Companion.Address) -> Int,
        updateReg: (index: Memory.Companion.Address, value: Int) -> Void,
        updateMem: (index: Memory.Companion.Address, value: Int) -> Void
    ) {
        TODO("Not yet implemented")
    }

    companion object {
        
        const val OPERAND: Int = (0b00000010100)
    }

    init {
        // Verify that first 16 digits are 0
        if (opcode != 0 || regS != 0 || regT != 0) throw BadCodeException("Lis Instruction")
    }
}

class JumpInstruction(doubleWord: Int) : MipsInstruction(doubleWord, OPCODE, OPERAND) {
    override fun getSyntax(): String {
        return "jr $${regS}"
    }

    override fun run(
        getMem: (index: Memory.Companion.Address) -> Int,
        getReg: (index: Memory.Companion.Address) -> Int,
        updateReg: (index: Memory.Companion.Address, value: Int) -> Void,
        updateMem: (index: Memory.Companion.Address, value: Int) -> Void
    ) {
        TODO("Not yet implemented")
    }

    companion object {
        const val OPCODE: Int = 0
        const val OPERAND: Int = (0b00000001000)
    }

    init {
        // Verify bit 12-22 are all 0
        if (regT != 0 || regD != 0) throw BadCodeException("Jump instruction")
    }
}

class JumpAndLinkInstruction(doubleWord: Int) : MipsInstruction(doubleWord, OPCODE, OPERAND) {
    override fun getSyntax(): String {
        return "jalr $${regS}"
    }

    override fun run(
        getMem: (index: Memory.Companion.Address) -> Int,
        getReg: (index: Memory.Companion.Address) -> Int,
        updateReg: (index: Memory.Companion.Address, value: Int) -> Void,
        updateMem: (index: Memory.Companion.Address, value: Int) -> Void
    ) {
        TODO("Not yet implemented")
    }

    companion object {
        const val OPCODE: Int = 0
        const val OPERAND: Int = (0b00000001001)
    }

    init {
        // Verify bit 12-22 are all 0
        if (regT != 0 || regD != 0) throw BadCodeException("Jump instruction")
    }
}

class WordInstruction(doubleWord: Int) : MipsInstruction(doubleWord, null, null) {
    override fun getSyntax(): String {
        return ".word 0x${Integer.toHexString(doubleWord).padStart(8, '0')}"
    }

    override fun run(
        getMem: (index: Memory.Companion.Address) -> Int,
        getReg: (index: Memory.Companion.Address) -> Int,
        updateReg: (index: Memory.Companion.Address, value: Int) -> Void,
        updateMem: (index: Memory.Companion.Address, value: Int) -> Void
    ) {
        TODO("Not yet implemented")
    }

}

