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
package org.pluverse.cs241.emulator.views.lanterna

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.gui2.Interactable
import com.googlecode.lanterna.gui2.TextBox
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import org.pluverse.cs241.emulator.cpumodel.EmulatorMemoryData
import org.pluverse.cs241.emulator.cpumodel.Execution
import org.pluverse.cs241.emulator.cpumodel.ReadonlyMemory
import java.io.ByteArrayOutputStream

open class CommandLine(
    terminalSize: TerminalSize,
    private val outputStream: ByteArrayOutputStream,
) :
    TextBox(terminalSize, Style.MULTI_LINE) {

    private var lastOutput = ""

    private fun moveToEnd() {
        setCaretPosition(Int.MAX_VALUE, Int.MAX_VALUE)
    }

    override fun handleKeyStroke(keyStroke: KeyStroke?): Interactable.Result {
        // Doing some bypass stuff lol to make it "readonly" for the user
        // but still allow setting "scroll"
        when (keyStroke?.keyType) {
            KeyType.Character, KeyType.Backspace, KeyType.Delete, KeyType.Enter ->
                isReadOnly = true
            else -> {}
        }
        val ret = super.handleKeyStroke(keyStroke)
        isReadOnly = false
        return ret
    }

    fun printChanges(executions: List<Execution>, memory: ReadonlyMemory, registers: ReadonlyMemory) {
        if (executions.isEmpty()) return

        var formattedOutput = "Changes:\n"

        for (execution in executions) {
            val address = execution.address
            val value = execution.value
            formattedOutput += when (execution.type) {
                Execution.ExecutionType.REGISTER -> {
                    val oldHex = EmulatorMemoryData.getHex(value)
                    "$${address.getMemoryIndex()}: $oldHex -> ${registers.getData(address).getHex()}\n"
                }

                Execution.ExecutionType.MEMORY -> {
                    val oldHex = EmulatorMemoryData.getHex(value)
                    "M[${execution.address}]: $oldHex -> ${memory.get(address)}\n"
                }

                else -> ""
            }
        }

        if (lineCount == 1) {
            text = formattedOutput
        } else {
            addLine(formattedOutput)
        }
        moveToEnd()
    }

    /**
     * Will add STDOUT contents to the CommandLine
     * if the output has changed.
     */
    fun printOutput() {
        val output = outputStream.toString()
        if (lastOutput == output) return

        lastOutput = output
        val formattedOutput = "STDOUT (BELOW):\n$output"

        if (lineCount == 1) {
            text = formattedOutput
        } else {
            addLine(formattedOutput)
        }
        moveToEnd()
    }

    fun printReturnOs() {
        val output = "Returned to OS"
        if (lineCount == 1) {
            text = output
        } else {
            addLine(output)
        }
        moveToEnd()
    }
}
