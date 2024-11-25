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
package org.pluverse.cs241.emulator.views

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.graphics.SimpleTheme
import com.googlecode.lanterna.gui2.BasicWindow
import com.googlecode.lanterna.gui2.Border
import com.googlecode.lanterna.gui2.BorderLayout
import com.googlecode.lanterna.gui2.Borders
import com.googlecode.lanterna.gui2.CheckBoxList
import com.googlecode.lanterna.gui2.Direction
import com.googlecode.lanterna.gui2.Interactable
import com.googlecode.lanterna.gui2.Label
import com.googlecode.lanterna.gui2.LinearLayout
import com.googlecode.lanterna.gui2.MultiWindowTextGUI
import com.googlecode.lanterna.gui2.Panel
import com.googlecode.lanterna.gui2.Window
import com.googlecode.lanterna.gui2.WindowBasedTextGUI
import com.googlecode.lanterna.gui2.WindowListenerAdapter
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import org.pluverse.cs241.emulator.cpumodel.Address
import org.pluverse.cs241.emulator.cpumodel.CpuEmulator
import org.pluverse.cs241.emulator.cpumodel.Execution
import org.pluverse.cs241.emulator.cpumodel.MemoryData
import org.pluverse.cs241.emulator.cpumodel.MipsInstruction
import org.pluverse.cs241.emulator.cpumodel.Registers
import org.pluverse.cs241.emulator.views.lanterna.CommandLine
import org.pluverse.cs241.emulator.views.lanterna.DataActionListBox
import org.pluverse.cs241.emulator.views.lanterna.HIGHLIGHT_CUSTOM_THEME
import org.pluverse.cs241.emulator.views.lanterna.InstructionsListItemRenderer
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.concurrent.atomic.AtomicBoolean

/**
 * This provides a GUI view for the emulator using Lanterna. It will display the registers, memory, and PC.
 * It will update the view whenever the registers, memory, or PC changes.
 *
 */
class GuiView : BasicEmulatorView() {

  lateinit var stepForward: () -> Unit
  lateinit var stepBackward: () -> Unit

  private val outputStream = ByteArrayOutputStream() // Used to redirect output

  /**
   * The main wrapper components for the GUI
   */
  val screen = DefaultTerminalFactory().createScreen()
  private val textGUI: WindowBasedTextGUI
  private val window: BasicWindow

  private var leftSideSize: Int
  private var rightSideSize: Int

  /**
   * The components of the GUI
   */
  private val mainPanel: Panel = Panel(BorderLayout()) // Main container wrapper

  private val instructionList: CheckBoxList<MemoryData> // Left side
  private val instructionListBorder: Border = Borders.singleLineReverseBevel("[1]-Instructions")
  private val instructionListRenderer = InstructionsListItemRenderer<MemoryData>()

  private val rightPanel: Panel = Panel(
    BorderLayout(),
  ) // Right side - holds rightTop and rightBottom

  private val cmdLine: CommandLine // Top Right
  private val cmdLineBorder: Border = Borders.singleLineReverseBevel("[2]-Command Line")

  private val rightBottomPanel: Panel = Panel(BorderLayout()) // Bottom right - holds Reg / Stack

  private val registerTable: DataActionListBox
  private val registerTableBorder: Border = Borders.singleLineReverseBevel("[3]-Registers")

  private val stackTable: DataActionListBox
  private val stackTableBorder: Border = Borders.singleLineReverseBevel("[4]-Stack")

  private val bottomPanel: Panel = Panel(
    LinearLayout(Direction.HORIZONTAL),
  ) // Bottom - holds command info

  /**
   * The themes for the GUI
   */
  private val mainTheme = object : SimpleTheme(
    FOREGROUND_COLOR,
    BACKGROUND_COLOR,
  ) {
    init {
      defaultDefinition.setSelected(FOREGROUND_COLOR, BACKGROUND_COLOR)
      defaultDefinition.setCustom(
        HIGHLIGHT_CUSTOM_THEME,
        TextColor.ANSI.GREEN_BRIGHT,
        TextColor.ANSI.BLACK,
      )
    }
  }
  private val focusPanelTheme = object : SimpleTheme(TextColor.ANSI.GREEN, TextColor.ANSI.BLACK) {
    init {
      defaultDefinition.setActive(TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK)
    }
  }

  /**
   * Setup/Initialize the wrapper components. The screen & window.
   */
  init {
    // Create the terminal screen
    screen.startScreen()

    // Create the MultiWindowTextGUI
    textGUI = MultiWindowTextGUI(screen)

    // Create the main window. Set it to full screen and set the theme
    window = BasicWindow("MIPS Stepper")
    window.setHints(listOf(Window.Hint.FULL_SCREEN, Window.Hint.NO_DECORATIONS))
    window.theme = mainTheme

    // Set up a default size for the left and right side
    leftSideSize = screen.terminalSize.columns / 3
    rightSideSize = screen.terminalSize.columns - leftSideSize
  }

  /**
   * Initialize the main components to display the GUI.
   */
  init {
    instructionList = object : CheckBoxList<MemoryData>() {
      override fun afterEnterFocus(
        direction: Interactable.FocusChangeDirection?,
        previouslyInFocus: Interactable?,
      ) {
        instructionListBorder.theme = focusPanelTheme
      }

      override fun afterLeaveFocus(
        direction: Interactable.FocusChangeDirection?,
        nextInFocus: Interactable?,
      ) {
        instructionListBorder.theme = mainTheme
      }

      init {
        withBorder(instructionListBorder)
        theme = mainTheme
        setListItemRenderer(instructionListRenderer)
      }
    }

    cmdLine = object : CommandLine(TerminalSize(rightSideSize, 5), outputStream) {
      override fun afterEnterFocus(
        direction: Interactable.FocusChangeDirection?,
        previouslyInFocus: Interactable?,
      ) {
        cmdLineBorder.theme = focusPanelTheme
      }

      override fun afterLeaveFocus(
        direction: Interactable.FocusChangeDirection?,
        nextInFocus: Interactable?,
      ) {
        cmdLineBorder.theme = mainTheme
      }

      init {
        withBorder(cmdLineBorder)
        theme = mainTheme
      }
    }

    registerTable = object : DataActionListBox() {
      override fun afterEnterFocus(
        direction: Interactable.FocusChangeDirection?,
        previouslyInFocus: Interactable?,
      ) {
        registerTableBorder.theme = focusPanelTheme
      }

      override fun afterLeaveFocus(
        direction: Interactable.FocusChangeDirection?,
        nextInFocus: Interactable?,
      ) {
        registerTableBorder.theme = mainTheme
      }

      init {
        withBorder(registerTableBorder)
        theme = mainTheme
      }
    }

    stackTable = object : DataActionListBox() {
      override fun afterEnterFocus(
        direction: Interactable.FocusChangeDirection?,
        previouslyInFocus: Interactable?,
      ) {
        stackTableBorder.theme = focusPanelTheme
      }

      override fun afterLeaveFocus(
        direction: Interactable.FocusChangeDirection?,
        nextInFocus: Interactable?,
      ) {
        stackTableBorder.theme = mainTheme
      }

      init {
        withBorder(stackTableBorder)
        theme = mainTheme
        setListItemRenderer(highlightText = "$${Registers.STACK_POINTER} \u2192 ")
      }
    }

    bottomPanel.addComponent(
      Label("Press 'q' to quit, 'r' to run, 'n' to step, 'b' to step back, 'tab' to switch")
        .setForegroundColor(TextColor.ANSI.RED),
    )

    setPreferredSizes(screen.terminalSize) // Set the sizes of all components
  }

  /**
   * Attach the components to each other
   */
  init {
    rightBottomPanel.addComponent(registerTableBorder, BorderLayout.Location.LEFT)
    rightBottomPanel.addComponent(stackTableBorder, BorderLayout.Location.CENTER)

    rightPanel.addComponent(cmdLineBorder, BorderLayout.Location.TOP)
    rightPanel.addComponent(rightBottomPanel, BorderLayout.Location.CENTER)

    mainPanel.addComponent(instructionListBorder, BorderLayout.Location.LEFT)
    mainPanel.addComponent(rightPanel, BorderLayout.Location.RIGHT)
    mainPanel.addComponent(bottomPanel, BorderLayout.Location.BOTTOM)

    window.component = mainPanel
    window.addWindowListener(object : WindowListenerAdapter() {
      override fun onUnhandledInput(
        basePane: Window?,
        keyStroke: KeyStroke?,
        hasBeenHandled: AtomicBoolean?,
      ) {
      }

      override fun onInput(basePane: Window?, keyStroke: KeyStroke?, deliverEvent: AtomicBoolean?) {
        // Set the scrolls of arrow up and down as page up and down. Also disables next component focus switch.
        val handleFocusInput: (KeyStroke) -> Unit = lambda@{ _ ->
          window.focusedInteractable?.handleInput(keyStroke)
          deliverEvent?.set(false) // Stop default behaviour
        }

        when (keyStroke?.keyType) {
          KeyType.ArrowUp -> handleFocusInput(KeyStroke(KeyType.PageUp))
          KeyType.ArrowDown -> handleFocusInput(KeyStroke(KeyType.PageDown))
          else -> {}
        }

        // Handle main commands
        when (keyStroke?.character) {
          'q', '1', '2', '3', '4', 'n', 'b', 'r' -> {
            when (keyStroke.character) {
              'q' -> window.close()
              '1' -> window.focusedInteractable = instructionList
              '2' -> window.focusedInteractable = cmdLine
              '3' -> window.focusedInteractable = registerTable
              '4' -> window.focusedInteractable = stackTable
              'n' -> stepForward()
              'b' -> stepBackward()
              'r' -> runUntilBreakpoint()
            }

            deliverEvent?.set(false)
          }
        }
      }

      override fun onResized(window: Window?, oldSize: TerminalSize?, newSize: TerminalSize?) {
        if (newSize != null) {
          setPreferredSizes(newSize)
        }
      }
    })
  }

  /**
   * Display and run the GUI
   */
  override fun start(emulator: CpuEmulator) {
    val originalOut = System.out
    System.setOut(PrintStream(outputStream))

    try {
      this.stepForward = { if (!emulator.hasReturnedOS) emulator.runFetchExecuteLoop() }
      this.stepBackward = {
        if (emulator.numReverseExecutions() > 0) {
          emulator.reverseExecution()
        }
      }

      displayDefault()
      screen.startScreen()
      textGUI.addWindowAndWait(window)
    } catch (e: Exception) {
      throw e
    } finally {
      System.setOut(originalOut)
      screen.startScreen()
    }
  }

  fun runUntilBreakpoint() {
    stepForward() // Want to take at least one step

    while (!checkReturnedOs() && !instructionList.isChecked(pc().getMemoryIndex())) {
      stepForward()
    }
  }

  /**
   * Sets all the components of the GUI to the default. Called in INIT and when a display REFRESH
   * is required.
   */
  private fun displayDefault() {
    registers.forEach { data -> registerTable.addItem(data) {} }
    memory.forEach { data ->
      instructionList.addItem(data)
      stackTable.addItem(data) {}
    }

    // Set the stack pointer text and move to the stack pointer
    updateStackPointer()
  }

  /**
   * Sets the preferred sizes for all components based on the new size.
   */
  private fun setPreferredSizes(newSize: TerminalSize) {
    leftSideSize = newSize.columns / 3
    rightSideSize = newSize.columns - leftSideSize

    instructionList.preferredSize = TerminalSize(leftSideSize, newSize.rows)
    cmdLine.preferredSize = TerminalSize(rightSideSize, 5)

    val leftPreferredSize = TerminalSize(leftSideSize, screen.terminalSize.rows)
    val registerPreferredSize =
      TerminalSize(rightSideSize / 2 - 1, screen.terminalSize.rows - 5)
    val stackPreferredSize =
      TerminalSize(rightSideSize - registerPreferredSize.columns, screen.terminalSize.rows - 5)

    instructionList.preferredSize = leftPreferredSize
    registerTable.preferredSize = registerPreferredSize
    stackTable.preferredSize = stackPreferredSize
  }

  /**
   * Updates the stack pointer in the stack table.
   */
  private fun updateStackPointer() {
    stackTable.selectedIndex = Address(
      registers[Registers.STACK_POINTER].doubleWord.toUInt(),
    ).getMemoryIndex()
    stackTable.customRenderer?.highlight = stackTable.selectedIndex
  }

  override fun notifyRegUpdate(index: Int, oldValue: Int) {
    if (index == Registers.STACK_POINTER) updateStackPointer()
  }

  override fun notifyMemUpdate(address: Address, oldValue: Int) {
  }

  override fun notifyPcUpdate(pc: Address) {
    instructionListRenderer.highlight = pc()
    instructionList.selectedIndex = pc.getMemoryIndex()
  }

  override fun notifyRunInstruction(instruction: MipsInstruction, executions: List<Execution>) {
    if (checkReturnedOs()) {
      cmdLine.printReturnOs()
    } else {
      cmdLine.printChanges(executions, memory, registers)
      cmdLine.printOutput()
    }
  }

  companion object {
    val BACKGROUND_COLOR = TextColor.ANSI.BLACK
    val FOREGROUND_COLOR = TextColor.ANSI.WHITE
  }
}
