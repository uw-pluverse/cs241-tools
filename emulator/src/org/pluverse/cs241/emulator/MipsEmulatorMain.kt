package org.pluverse.cs241.emulator

import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.readBytes

class MipsEmulatorMain {

    companion object {

        /*
        main: the entry point of this application
        @param args: array of arguments provided from cmdline
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val testMemory = Memory<MipsInstructionData>()

            testMemory.getData(Memory.Companion.Address(8u)).test()

//            println(Path(args[0]).absolute())
//            val data = Path(args[0]).readBytes()
//
//            var doubleWord: Int = (data[0].toInt() and 0xff shl 24) or (data[1].toInt() and 0xff shl 16) or
//                    (data[2].toInt() and 0xff shl 8)  or (data[3].toInt() and 0xff)
//
//            println(doubleWord)

        }
    }

}