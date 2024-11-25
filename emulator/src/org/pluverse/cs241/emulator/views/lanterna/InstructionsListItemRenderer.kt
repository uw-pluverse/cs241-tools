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

import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.graphics.DefaultMutableThemeStyle
import com.googlecode.lanterna.gui2.CheckBoxList
import com.googlecode.lanterna.gui2.TextGUIGraphics
import org.pluverse.cs241.emulator.cpumodel.Address

const val HIGHLIGHT_CUSTOM_THEME = "HIGHLIGHT"

/**
 * This is a renderer for a row inside the CheckBoxList. It will highlight a specific row
 * with the theme of HIGHLIGHT_CUSTOM_THEME. I.e., highlight the PC Row.
 *
 */
class InstructionsListItemRenderer<T>(
  var highlight: Int = 0,
) : CheckBoxList.CheckBoxListItemRenderer<T>() {

  override fun getLabel(listBox: CheckBoxList<T>?, index: Int, item: T): String {
    listBox!!

    // Define constants for the item
    val itemChecked = listBox.isChecked(index)
    val isPc = index == highlight
    val selectedIndex = listBox.selectedIndex
    val rows = listBox.size.rows
    val cursorRow = listBox.cursorLocation.row

    // Define the text for the item
    val breakpoint = if (itemChecked) '\u23FA' else ' '
    val pcText = if (isPc) "PC \u2192 " else ""
    val maxDisplayedAddress = getLargestDisplayedIndex(selectedIndex, cursorRow, rows).toUInt() * 4u
    val maxAddressText = Address(maxDisplayedAddress).toHexStringSimple()
    val addressHex = Address(index.toUInt() * 4u).toHexStringSimple()
    val addressText = if (isPc) {
      addressHex.padEnd(
        maxAddressText.length,
      )
    } else {
      addressHex.padEnd(maxAddressText.length + 1)
    }
    val itemText = "${(item ?: "<error>")}"

    return breakpoint + pcText + addressText + itemText
  }

  override fun getHotSpotPositionOnLine(selectedIndex: Int): Int {
    return 0
  }

  override fun drawItem(
    graphics: TextGUIGraphics,
    listBox: CheckBoxList<T>,
    index: Int,
    item: T,
    selected: Boolean,
    focused: Boolean,
  ) {
    // Define constants for the item
    val itemChecked = listBox.isChecked(index)
    val isPc = index == highlight
    val selectedIndex = listBox.selectedIndex
    val rows = listBox.size.rows
    val cursorRow = listBox.renderer.getCursorLocation(listBox).row

    // Define the text for the item
    val breakpoint = if (itemChecked) '\u23FA' else ' '
    val pcText = if (isPc) "PC \u2192 " else ""
    val maxDisplayedAddress = getLargestDisplayedIndex(selectedIndex, cursorRow, rows).toUInt() * 4u
    val maxAddressText = Address(maxDisplayedAddress).toHexStringSimple()
    val addressHex = Address(index.toUInt() * 4u).toHexStringSimple()
    val addressText = if (isPc) {
      addressHex.padEnd(
        addressHex.length + 1,
      )
    } else {
      addressHex.padEnd(maxAddressText.length + 1)
    }
    val itemText = "${(item ?: "<error>")}"

    // Get the styles for the items
    val themeDefinition = listBox.theme.getDefinition(CheckBoxList::class.java)
    val itemStyle = DefaultMutableThemeStyle(
      if (selected) {
        themeDefinition.active
      } else if (focused) {
        themeDefinition.insensitive
      } else {
        themeDefinition.normal
      },
    )

    if (itemChecked) itemStyle.setBackground(TextColor.Factory.fromString("#992222"))

    val addressStyle = DefaultMutableThemeStyle(itemStyle)
      .setForeground(TextColor.ANSI.YELLOW_BRIGHT)
    val markerStyle = DefaultMutableThemeStyle(
      themeDefinition.normal,
    ).setForeground(TextColor.ANSI.RED_BRIGHT)
    val pcStyle = DefaultMutableThemeStyle(themeDefinition.getCustom(HIGHLIGHT_CUSTOM_THEME))
      .setBackground(itemStyle.background)

    // Draw the checkbox if set
    graphics.applyThemeStyle(if (itemChecked) markerStyle else itemStyle)
    graphics.setCharacter(0, 0, breakpoint)

    // Insert the actual text
    // Note: We want to space it evenly based on the max length of addressText
    graphics.applyThemeStyle(if (isPc) pcStyle else itemStyle)

    var insertCol = 2 // Insert two after debug breakpoints
    graphics.putString(insertCol, 0, pcText)

    insertCol += pcText.length
    graphics.applyThemeStyle(addressStyle)
    graphics.putString(insertCol, 0, addressText)

    insertCol += addressText.length
    graphics.applyThemeStyle(if (isPc) pcStyle else itemStyle)
    graphics.putString(insertCol, 0, itemText)
  }

  private fun getLargestDisplayedIndex(
    index: Int,
    cursorRow: Int,
    rows: Int,
  ): Int = rows - cursorRow + index - 1
}
