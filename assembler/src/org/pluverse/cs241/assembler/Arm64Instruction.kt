package org.pluverse.cs241.assembler

import java.nio.ByteBuffer
import java.nio.ByteOrder

abstract class Arm64Instruction {
    abstract fun encode(): ByteArray
}

abstract class AbstractEncodedInstruction : Arm64Instruction() {
    protected fun intToBytes(value: Int): ByteArray {
        return ByteBuffer.allocate(4)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(value)
            .array()
    }
}

// Base class for instructions with format: Opcode(11) | Rm(5) | Flags(6) | Rn(5) | Rd(5)
abstract class DataProcessingInstruction(
    val opcode: Int,
    val flags: Int,
    val rm: Int,
    val rn: Int,
    val rd: Int
) : AbstractEncodedInstruction() {
    
    override fun encode(): ByteArray {
        val machineCode = (opcode shl 21) or (rm shl 16) or (flags shl 10) or (rn shl 5) or rd
        return intToBytes(machineCode)
    }
}

// Three Register Instructions: op rd, rn, rm
abstract class ThreeRegisterInstruction(
    opcode: Int,
    flags: Int,
    rd: Int,
    rn: Int,
    rm: Int
) : DataProcessingInstruction(opcode, flags, rm, rn, rd)

class AddInstruction(rd: Int, rn: Int, rm: Int) : 
    ThreeRegisterInstruction(0b10001011001, 0b011000, rd, rn, rm)

class SubInstruction(rd: Int, rn: Int, rm: Int) : 
    ThreeRegisterInstruction(0b11001011001, 0b011000, rd, rn, rm)

class MulInstruction(rd: Int, rn: Int, rm: Int) : 
    ThreeRegisterInstruction(0b10011011000, 0b011111, rd, rn, rm)

class SmulhInstruction(rd: Int, rn: Int, rm: Int) : 
    ThreeRegisterInstruction(0b10011011010, 0b011111, rd, rn, rm)

class UmulhInstruction(rd: Int, rn: Int, rm: Int) : 
    ThreeRegisterInstruction(0b10011011110, 0b011111, rd, rn, rm)

class SdivInstruction(rd: Int, rn: Int, rm: Int) : 
    ThreeRegisterInstruction(0b10011010110, 0b000011, rd, rn, rm)

class UdivInstruction(rd: Int, rn: Int, rm: Int) : 
    ThreeRegisterInstruction(0b10011010110, 0b000010, rd, rn, rm)

// Cmp Instruction: cmp rn, rm -> subs xzr, rn, rm
class CmpInstruction(rn: Int, rm: Int) : 
    DataProcessingInstruction(0b11101011001, 0b011000, rm, rn, 31)

// Branch Register Instructions
class BrInstruction(rn: Int) : 
    DataProcessingInstruction(0b11010110000, 0b000000, 31, rn, 0)

class BlrInstruction(rn: Int) : 
    DataProcessingInstruction(0b11010110001, 0b000000, 31, rn, 0)
