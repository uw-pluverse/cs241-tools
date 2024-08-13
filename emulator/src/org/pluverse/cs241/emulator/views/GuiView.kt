package org.pluverse.cs241.emulator.views

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.graphics.SimpleTheme
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import org.pluverse.cs241.emulator.cpumodel.*
import org.pluverse.cs241.emulator.views.lanterna.*
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * This provides a GUI view for the emulator using Lanterna. It will display the registers, memory, and PC.
 * It will update the view whenever the registers, memory, or PC changes.
 *
 */
class GuiView : BasicEmulatorView() {

    lateinit var stepForward: () -> Unit
    lateinit var stepBackward: () -> Unit

    /**
     * The main wrapper components for the GUI
     */
    val screen: Screen
    private val textGUI: WindowBasedTextGUI
    private val window: BasicWindow

    var leftSideSize: Int
    var rightSideSize: Int

    /**
     * The components of the GUI
     */
    private val mainPanel: Panel = Panel(BorderLayout()) // Main container wrapper

    private val instructionList: CheckBoxList<MemoryData> // Left side
    private val instructionListBorder: Border = Borders.singleLineReverseBevel("[1]-Instructions")
    private val instructionListRenderer = InstructionsListItemRenderer<MemoryData>()

    private val rightPanel: Panel = Panel(BorderLayout()) // Right side - holds rightTop and rightBottom

    private val cmdLine: TextBox // Top Right
    private val cmdLineBorder: Border = Borders.singleLineReverseBevel("[2]-Command Line")

    private val rightBottomPanel: Panel = Panel(BorderLayout()) // Bottom right - holds Reg / Stack

    private val registerTable: DataActionListBox
    private val registerTableBorder: Border = Borders.singleLineReverseBevel("[3]-Registers")

    private val stackTable: DataActionListBox
    private val stackTableBorder: Border = Borders.singleLineReverseBevel("[4]-Stack")

    private val bottomPanel: Panel = Panel(LinearLayout(Direction.HORIZONTAL)) // Bottom - holds command info

    /**
     * The themes for the GUI
     */
    private val mainTheme = object : SimpleTheme(TextColor.RGB(192, 192, 192), TextColor.ANSI.BLACK) {
        init {
            defaultDefinition.setSelected(TextColor.RGB(192, 192, 192), TextColor.ANSI.BLUE)
            defaultDefinition.setCustom(HIGHLIGHT_CUSTOM_THEME, TextColor.ANSI.GREEN_BRIGHT, TextColor.ANSI.BLACK)
        }
    }
    private val focusPanelTheme = object : SimpleTheme(TextColor.ANSI.GREEN, TextColor.ANSI.BLACK) {
        init {
            defaultDefinition.setActive(TextColor.ANSI.WHITE_BRIGHT, TextColor.ANSI.BLACK)
        }
    }

    /**
     * The fields for the GUI
     */

    // The panels in the display and their toggled modes
    private enum class Display { INSTRUCTIONS, REGISTERS, MEMORY, INFO, SEARCH }
    private enum class DisplayMode { REGISTERS, MEMORY, PC }
    private val displayModes: MutableMap<Display, DisplayMode> = EnumMap(Display::class.java)
    private var curDisplay: Display = Display.INSTRUCTIONS

    /**
     * Setup/Initialize the wrapper components. The screen & window.
     */
    init {
        // Create the terminal screen
        val defaultTerminalFactory = DefaultTerminalFactory()
        screen = defaultTerminalFactory.createScreen()
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
        instructionList = object: CheckBoxList<MemoryData>() {
            override fun afterEnterFocus(
                direction: Interactable.FocusChangeDirection?,
                previouslyInFocus: Interactable?
            ) {
                instructionListBorder.theme = focusPanelTheme
            }

            override fun afterLeaveFocus(direction: Interactable.FocusChangeDirection?, nextInFocus: Interactable?) {
                instructionListBorder.theme = mainTheme
            }

            init {
                withBorder(instructionListBorder)
                theme = mainTheme
                setListItemRenderer(instructionListRenderer)
            }
        }

        cmdLine = object : TextBox(TerminalSize(rightSideSize, 5), TextBox.Style.MULTI_LINE) {
            override fun afterEnterFocus(
                direction: Interactable.FocusChangeDirection?,
                previouslyInFocus: Interactable?
            ) {
                cmdLineBorder.theme = focusPanelTheme
            }

            override fun afterLeaveFocus(direction: Interactable.FocusChangeDirection?, nextInFocus: Interactable?) {
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
                previouslyInFocus: Interactable?
            ) {
                registerTableBorder.theme = focusPanelTheme
            }

            override fun afterLeaveFocus(direction: Interactable.FocusChangeDirection?, nextInFocus: Interactable?) {
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
                previouslyInFocus: Interactable?
            ) {
                stackTableBorder.theme = focusPanelTheme
            }

            override fun afterLeaveFocus(direction: Interactable.FocusChangeDirection?, nextInFocus: Interactable?) {
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
                .setForegroundColor(TextColor.ANSI.RED)
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
                hasBeenHandled: AtomicBoolean?
            ) {

            }

            override fun onInput(basePane: Window?, keyStroke: KeyStroke?, deliverEvent: AtomicBoolean?) {
                // Set the scrolls of arrow up and down as page up and down. Also disables next component focus switch.
                val handleFocusInput: (KeyStroke) -> Unit = lambda@ { _ ->
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
                    'q', '1', '2', '3', '4', 'n', 'b' -> {
                        when (keyStroke.character) {
                            'q' -> window.close()
                            '1' -> window.focusedInteractable = instructionList
                            '2' -> window.focusedInteractable = cmdLine
                            '3' -> window.focusedInteractable = registerTable
                            '4' -> window.focusedInteractable = stackTable
                            'n' -> stepForward()
                            'b' -> stepBackward()
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
    fun start(stepForward: () -> Unit, stepBackward: () -> Unit) {
        this.stepForward = stepForward
        this.stepBackward = stepBackward

        displayDefault()
        screen.startScreen()
        textGUI.addWindowAndWait(window)
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
        val registerPreferredSize = TerminalSize(rightSideSize / 2 - 1, screen.terminalSize.rows - 5)
        val stackPreferredSize = TerminalSize(rightSideSize - registerPreferredSize.columns, screen.terminalSize.rows - 5)

        instructionList.preferredSize = leftPreferredSize
        registerTable.preferredSize = registerPreferredSize
        stackTable.preferredSize = stackPreferredSize
    }

    private fun updateStackPointer() {
        stackTable.selectedIndex = Address(registers[Registers.STACK_POINTER].doubleWord.toUInt()).getMemoryIndex()
        stackTable.customRenderer?.highlight = stackTable.selectedIndex
    }

    override fun notifyRegUpdate(index: Int, oldValue: Int) {
        if (index == Registers.STACK_POINTER) updateStackPointer()
    }

    override fun notifyMemUpdate(address: Address, oldValue: Int) {

    }

    override fun notifyPcUpdate(pc: Address) {
        instructionListRenderer.highlight = pc()
    }

    override fun notifyRunInstruction(instruction: MipsInstruction, executions: List<Execution>) {

    }
}

