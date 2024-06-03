package org.pluverse.cs241.emulator


class MipsEmulatorMain {

    companion object {

        /*
        main: the entry point of this application
        @param args: array of arguments provided from cmdline
         */
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size == 0) throw Error("Need file input")
            val emulator = CpuEmulator(args[0])

            for (i in 0..10) {
                println(emulator.memory[i]().getSyntax())
            }
        }
    }

}