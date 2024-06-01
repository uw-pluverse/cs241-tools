package org.pluverse.cs241.emulator

/*
This is a general data type for the Memory class.
Stores general info about the command such as the details of the object.

 */
interface MemoryData {
    var doubleWord: Int

    fun getBinary(): String // Get the binary printout of doubleWord
    fun getHex(): String // Get the hexadecimal printout of doubleWord
    fun getDetails(): String // Print additional info and the binary and hex
    fun update(doubleWord: Int) // Updates the double word

    companion object {
        // Static methods for conversions
        @JvmStatic
        fun convertFourByteToInteger(c1: Int, c2: Int, c3: Int, c4: Int): Int {
            return ((c1 shl 24) or (c2 shl 16) or (c3 shl 8) or (c4))
        }
    }
}


/*
This implements similar functions from MemoryData for Mips and Register Data

 */
abstract class EmulatorMemoryData(override var doubleWord: Int) : MemoryData {

    constructor() : this(0) // Default to 0

    // Returns a binary string, padded to 32 bits
    override fun getBinary(): String = Integer.toBinaryString(doubleWord).padStart(Int.SIZE_BITS, '0')

    // Returns a hex string of the doubleWord
    override fun getHex(): String = Integer.toHexString(doubleWord)

    override fun update(doubleWord: Int) {
        // Use the custom set methods to modify it
        // This is for better semantics

        this.doubleWord = doubleWord
    }
}


/*
Class to represent a register instruction. Just a straight implementation
 */

class RegisterData(private val regNumber: Int) : EmulatorMemoryData() {

    constructor() : this(0)

    // We want to only mutate doubleWord if it's a non-zero register
    override var doubleWord: Int
        get() = super.doubleWord
        set(value) {
            if (regNumber != 0) super.doubleWord = value
        }

    override fun getDetails(): String {
        return "<Register $${regNumber}> : ${getBinary()}"
    }

}


/*
General info for the mips instructions and allows for mutation of CpuEmulator

Note: CpuEmulator is the Controller, Memory is the Model, MipsInstructions are the View,
        MipsInstruction(s) can call the CpuEmulator to mutate the Memory(s).

 */
class MipsInstructionData(doubleWord: Int, private val address: Memory.Companion.Address) : EmulatorMemoryData(doubleWord) {

    override var doubleWord: Int
        get() = super.doubleWord
        set(value) {
            super.doubleWord = value
            instruction = getMipsInstruction(value, address)
        }

    var instruction: MipsInstruction = getMipsInstruction(doubleWord, address)

    constructor(address: Memory.Companion.Address) : this(0, address)

    override fun getDetails(): String {

        return ""
    }

    companion object {
        /*
        Method to filter a doubleWord and return the correct MipsInstructionData
         */
        @JvmStatic
        fun getMipsInstruction(doubleWord: Int, address: Memory.Companion.Address): MipsInstruction {
            val opcode: Int = doubleWord shr 26
            val operand: Int = doubleWord and (0b11111111111).toInt()

            // these are the register values
            val regS = (doubleWord shr 21) and (0b11111).toInt()
            val regT = (doubleWord shr 16) and (0b11111).toInt()
            val regD = (doubleWord shr 11) and (0b11111).toInt()
            val defaultRegisterOpCode = 0b000000;

            // Check edge cases first, mfhi, mflo, lis, jr, jalr
            if (opcode == MoveHighInstruction.OPCODE && regT == 0) {
                if (regS == 0 ) {
                    if (operand == MoveHighInstruction.OPERAND) return MoveHighInstruction(doubleWord)
                    else if (operand == MoveLowInstruction.OPERAND) return MoveLowInstruction(doubleWord)
                    else if (operand == LisInstruction.OPERAND) return LisInstruction(doubleWord, address)
                } else if (regD == 0) {
                    if (operand == JumpInstruction.OPERAND) return JumpInstruction(doubleWord)
                    else if (operand == JumpAndLinkInstruction.OPERAND) return JumpAndLinkInstruction(doubleWord)
                }
            }

            return when (opcode) {
                defaultRegisterOpCode -> when (operand) {
                    AddInstruction.OPERAND -> AddInstruction(doubleWord)
                    SubInstruction.OPERAND -> SubInstruction(doubleWord)
                    MultiplyInstruction.OPERAND -> MultiplyInstruction(doubleWord)
                    MultiplyUInstruction.OPERAND -> MultiplyUInstruction(doubleWord)
                    DivideInstruction.OPERAND -> DivideInstruction(doubleWord)
                    DivideUInstruction.OPERAND -> DivideUInstruction(doubleWord)
                    SetLessThanInstruction.OPERAND -> SetLessThanInstruction(doubleWord)
                    SetLessThanUInstruction.OPERAND -> SetLessThanUInstruction(doubleWord)
                    else -> WordInstruction(doubleWord)
                }
                BranchEqualInstruction.OPCODE -> BranchEqualInstruction(doubleWord)
                BranchNotEqualInstruction.OPCODE -> BranchNotEqualInstruction(doubleWord)
                else -> WordInstruction(opcode)
            }
        }
    }

}


