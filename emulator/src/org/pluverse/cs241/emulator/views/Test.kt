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

    import com.googlecode.lanterna.SGR
    import com.googlecode.lanterna.TerminalSize
    import com.googlecode.lanterna.TextColor
    import com.googlecode.lanterna.graphics.*
    import com.googlecode.lanterna.graphics.SimpleTheme.Definition
    import com.googlecode.lanterna.gui2.*
    import com.googlecode.lanterna.gui2.dialogs.TextInputDialog
    import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder
    import com.googlecode.lanterna.gui2.menu.Menu
    import com.googlecode.lanterna.gui2.menu.MenuBar
    import com.googlecode.lanterna.gui2.menu.MenuItem
    import com.googlecode.lanterna.gui2.table.DefaultTableCellRenderer
    import com.googlecode.lanterna.gui2.table.Table
    import com.googlecode.lanterna.gui2.table.TableCellRenderer
    import com.googlecode.lanterna.input.KeyStroke
    import com.googlecode.lanterna.screen.Screen
    import com.googlecode.lanterna.terminal.DefaultTerminalFactory
    import java.io.IOException
    import java.util.concurrent.atomic.AtomicBoolean

    class Test {

        class CustomTableCellRenderer : TableCellRenderer<String> {
            private val selectedRowBackgroundColor = TextColor.ANSI.GREEN
            private val selectedRowForegroundColor = TextColor.ANSI.WHITE
            private val defaultBackgroundColor = TextColor.ANSI.BLACK
            private val defaultForegroundColor = TextColor.ANSI.WHITE

            override fun drawCell(
                table: Table<String>?,
                cell: String?,
                columnIndex: Int,
                rowIndex: Int,
                graphics: TextGUIGraphics
            ) {
                if (rowIndex == table?.selectedRow) {
                    graphics.setBackgroundColor(selectedRowBackgroundColor)
                    graphics.setForegroundColor(selectedRowForegroundColor)
                } else {
                    graphics.setBackgroundColor(defaultBackgroundColor)
                    graphics.setForegroundColor(defaultForegroundColor)
                }
                graphics.putString(0, 0, cell)
            }

            override fun getPreferredSize(p0: Table<String>?, p1: String?, p2: Int, p3: Int): TerminalSize {
                return (TerminalSize(10, 10))
            }
        }

        companion object {

            @JvmStatic
            fun main(args: Array<String>) {
                val defaultTerminalFactory = DefaultTerminalFactory()
                var screen: Screen? = null

                try {
                    screen = defaultTerminalFactory.createScreen()
                    screen.startScreen()

                    val textGUI: WindowBasedTextGUI = MultiWindowTextGUI(screen)

                    val window = BasicWindow("MIPS Stepper")
                    window.setHints(listOf(Window.Hint.FULL_SCREEN, Window.Hint.NO_DECORATIONS))
                    window.theme = SimpleTheme(TextColor.RGB(192, 192, 192), TextColor.ANSI.BLACK)

                    var leftSize = screen.terminalSize.columns / 3
                    var rightSize = screen.terminalSize.columns - leftSize

                    // Main panel with a BorderLayout
                    val mainPanel = Panel(BorderLayout())

                    // Left panel (e.g., status)
                    val leftPanel = Panel(LinearLayout(Direction.VERTICAL))
                    val leftPanelBorder = leftPanel.withBorder(Borders.singleLineReverseBevel("[1]-Instructions"))
                    val instructionList = CheckBoxList<String>()
//                    instructionList.setListItemRenderer() = Definition()
                    repeat(10) {
                        instructionList.addItem("LW $1, 0($2)")
                        instructionList.addItem("LW $3, 0($4)")
                    }

//                    val instructionList = Table<String>("")
//                    instructionList.setSelectAction {
//                        instructionList.setSelectedRow(0)
//                    }
//
//                    instructionList.theme = SimpleTheme(TextColor.ANSI.WHITE, TextColor.ANSI.BLACK)
//
//                    instructionList.tableCellRenderer = CustomTableCellRenderer()
//
//                    repeat(10) {
//                        instructionList.tableModel.addRow("LW $1, 0($2)")
//                        instructionList.tableModel.addRow("LW $3, 0($4)")
//                    }

                    leftPanel.addComponent(instructionList)

                    // Right panel with a split layout
                    val rightPanel = Panel(BorderLayout())

                    // Top right panel (e.g., branches)
                    val topRightPanel = Panel(LinearLayout(Direction.HORIZONTAL))

                    // Bottom right panel (e.g., commit log)
                    val bottomRightPanel = Panel(LinearLayout(Direction.VERTICAL))
                    bottomRightPanel.addComponent(Label("Commit Log").setForegroundColor(TextColor.ANSI.WHITE).setBackgroundColor(TextColor.ANSI.RED))
                    bottomRightPanel.addComponent(Label("Fix bug #123").setForegroundColor(TextColor.ANSI.WHITE).setBackgroundColor(TextColor.ANSI.RED))
                    bottomRightPanel.addComponent(Label("Add new feature").setForegroundColor(TextColor.ANSI.WHITE).setBackgroundColor(TextColor.ANSI.RED))

                    // Adding top and bottom right panels to right panel
                    rightPanel.addComponent(topRightPanel.withBorder(Borders.singleLine("[2]-INFO")), BorderLayout.Location.TOP)
                    rightPanel.addComponent(bottomRightPanel.withBorder(Borders.singleLine("[3]-REG/MEM")), BorderLayout.Location.CENTER)

                    val bottomPanel = Panel(LinearLayout(Direction.HORIZONTAL))
                    bottomPanel.addComponent(Label("Press 'q' to quit").setForegroundColor(TextColor.ANSI.WHITE).setBackgroundColor(TextColor.ANSI.BLUE))

                    val topPanel = Panel(BorderLayout())

                    // Adding left and right panels to main panel
                    topPanel.addComponent(leftPanelBorder, BorderLayout.Location.LEFT)
                    topPanel.addComponent(rightPanel.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.FILL, GridLayout.Alignment.FILL, true, true)))

                    mainPanel.addComponent(topPanel, BorderLayout.Location.CENTER)
                    mainPanel.addComponent(bottomPanel, BorderLayout.Location.BOTTOM)

                    val cmdLine = TextBox(TerminalSize(rightSize, 5), TextBox.Style.MULTI_LINE)
                    topRightPanel.addComponent(cmdLine)

                    // Setting the main panel to the window
                    window.component = mainPanel
                    window.addWindowListener(object : WindowListenerAdapter() {
                        override fun onUnhandledInput(
                            basePane: Window?,
                            keyStroke: KeyStroke?,
                            hasBeenHandled: AtomicBoolean?
                        ) {
                            when (keyStroke?.character) {
                                'q' -> window.close()
                                '1' -> {
                                    leftPanelBorder.theme = SimpleTheme(TextColor.ANSI.GREEN, TextColor.ANSI.BLACK)
                                    window.focusedInteractable = instructionList
                                }
                                '2' -> {
                                    window.focusedInteractable = cmdLine
                                }
//                                'j' -> {
//                                    // Move selection down
//                                    val currentRow = instructionList.selectedRow
//                                    if (currentRow < instructionList.tableModel.rowCount - 1) {
//                                        instructionList.setSelectedRow(currentRow + 1)
//                                    }
//                                }
//                                'k' -> {
//                                    // Move selection up
//                                    val currentRow = instructionList.selectedRow
//                                    if (currentRow > 0) {
//                                        instructionList.setSelectedRow(currentRow - 1)
//                                    }
//                                }
                            }

                            textGUI.updateScreen()
                        }

                        override fun onResized(window: Window?, oldSize: TerminalSize?, newSize: TerminalSize?) {
                            if (newSize != null) {
                                val newLeftSize = TerminalSize(newSize.columns / 3, newSize.rows)
                                leftPanel.preferredSize = newLeftSize
                                instructionList.preferredSize = newLeftSize
                            }

                        }
                    })

                    textGUI.addWindowAndWait(window)
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    screen?.let {
                        try {
                            it.stopScreen()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }