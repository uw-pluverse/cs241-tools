package org.pluverse.cs241.emulator

import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.readBytes

/*
CpuEmulator (Class) stores the basic registers, pc pointer, and stores and retrieves from the memory.

Note, $30 or index 30 of the register is the stack pointer by convention.
 */

class CpuEmulator(fp: String) {

    // Define CONSTANTS
    private val file: Path = Path(fp);

    // Define VARIABLES
    val register: Memory<RegisterData> = Registers()
    val memory : Memory<MipsInstructionData> = RamMemory()

    /*
    Read the files  and load the memory
     */
    init {
        val fileData = file.readBytes()
    }

}
