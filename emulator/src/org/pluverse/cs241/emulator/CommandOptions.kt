package org.pluverse.cs241.emulator

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters


class CommandOptions(array: Array<String>) {

  val commandMain = CommandMain()
  val commandTwoInts = CommandTwoInts()
  val commandArray = CommandArray()
  val commander = JCommander.newBuilder()
    .addObject(commandMain)
    .addCommand(COMMAND_TWOINTS, commandTwoInts)
    .addCommand(COMMAND_ARRAY, commandArray)
    .build()

  init {
    commander.parse(*array)
  }

  class CommandMain {


    @Parameter(names = ["--debug"], arity = 1, description = "Whether to launch the debugger")
    var debugging = false

    @Parameter(names = ["--help", "-h"], help = true, description = "Print help message")
    var help = false
  }

  @Parameters(commandDescription = "mips.twoints")
  class CommandTwoInts {

    @Parameter(names = ["--register1", "-r1"], description = "The value of register 1")
    var register1:Int = 0

    @Parameter(names = ["--register2", "-r2"], description = "The value of register 2")
    var register2:Int = 0

    @Parameter(description = "The mips binary program")
    var mipsFilePath: String? = null
  }

  @Parameters(commandDescription = "mips.array")
  class CommandArray {

    @Parameter(names = ["--elements"], required = true, description = "The array elements")
    var elements: Array<Int> = emptyArray()

    @Parameter(description = "The mips binary program")
    var mipsFilePath: String? = null
  }

  companion object {
    const val COMMAND_TWOINTS= "twoints"
    const val COMMAND_ARRAY = "array"
  }
}
