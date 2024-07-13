package org.pluverse.cs241.emulator.views

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal

class Test {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val screen = DefaultTerminalFactory().createScreen()
            screen.startScreen()

            val gui = MultiWindowTextGUI(screen)
            val window = BasicWindow("Hello World")

            window.component = Panel().apply {
                addComponent(Label("Welcome to Lanterna with Kotlin"))
                addComponent(Button("Exit") { window.close() })
            }

            gui.addWindowAndWait(window)
            screen.stopScreen()
        }
    }
}