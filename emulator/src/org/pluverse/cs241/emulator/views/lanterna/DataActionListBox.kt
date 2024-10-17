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

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.graphics.DefaultMutableThemeStyle
import com.googlecode.lanterna.gui2.AbstractListBox
import com.googlecode.lanterna.gui2.Interactable
import com.googlecode.lanterna.gui2.TextGUIGraphics
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.input.MouseAction
import com.googlecode.lanterna.input.MouseActionType
import org.pluverse.cs241.emulator.cpumodel.MemoryData

/**
 * This stores MemoryData and includes a runnable method for optional functionality.
 * Prefix Tag is optional and is prepended before the actual contents.
 * I.e., M[3] or <Register> etc.
 */
data class DataActionItem(
  private val data: MemoryData,
  var action: Runnable,
  var displayMode: DisplayMode = DisplayMode.HEX,
) {

  val prefixTag: String = data.getPrefixTag()

  companion object {
    enum class DisplayMode { SIGNED_DECIMAL, HEX }
  }

  fun toggleDisplayMode() {
    displayMode = if (displayMode == DisplayMode.HEX) {
      DisplayMode.SIGNED_DECIMAL
    } else {
      DisplayMode.HEX
    }
  }

  override fun toString(): String {
    return if (displayMode == DisplayMode.HEX) data.getHex() else data.doubleWord.toString()
  }
}

/**
 * A GUI component for a scrollable list of DataMemory items. i.e. Register or Ram Memory.
 */
open class DataActionListBox(
  preferredSize: TerminalSize? = null,
) : AbstractListBox<DataActionItem, DataActionListBox>(preferredSize) {

  var customRenderer: DataActionListItemRenderer? = null

  override fun createDefaultListItemRenderer(): ListItemRenderer<
    DataActionItem,
    DataActionListBox,
    > {
    return DataActionListItemRenderer()
  }

  fun setListItemRenderer(highlightText: String): DataActionListBox? {
    customRenderer = customRenderer ?: DataActionListItemRenderer(highlightText = highlightText)
    return setListItemRenderer(customRenderer)
  }

  fun addItem(item: MemoryData, action: Runnable): DataActionListBox {
    return this.addItem(DataActionItem(item, action))
  }

  override fun getCursorLocation(): TerminalPosition? {
    return null
  }

  override fun handleKeyStroke(keyStroke: KeyStroke): Interactable.Result {
    if (this.isKeyboardActivationStroke(keyStroke)) {
      this.runSelectedItem()
      return Interactable.Result.HANDLED
    } else if (keyStroke.keyType == KeyType.MouseEvent) {
      val mouseAction = keyStroke as MouseAction
      val actionType = mouseAction.actionType
      if (!this.isMouseMove(keyStroke) &&
        actionType != MouseActionType.CLICK_RELEASE &&
        actionType != MouseActionType.SCROLL_UP &&
        actionType != MouseActionType.SCROLL_DOWN
      ) {
        val existingIndex = this.selectedIndex
        val newIndex = this.getIndexByMouseAction(mouseAction)
        if (existingIndex == newIndex &&
          this.isFocused && actionType != MouseActionType.CLICK_DOWN
        ) {
          return Interactable.Result.HANDLED
        } else {
          val result = super.handleKeyStroke(keyStroke)
          this.runSelectedItem()
          return result
        }
      } else {
        return super.handleKeyStroke(keyStroke)
      }
    } else {
      val result = super.handleKeyStroke(keyStroke)
      return result
    }
  }

  private fun runSelectedItem() {
    this.selectedItem?.toggleDisplayMode()
  }
}

class DataActionListItemRenderer(var highlight: Int? = null, var highlightText: String = "") :
  AbstractListBox.ListItemRenderer<DataActionItem, DataActionListBox>() {

  override fun drawItem(
    graphics: TextGUIGraphics,
    listBox: DataActionListBox,
    index: Int,
    item: DataActionItem,
    selected: Boolean,
    focused: Boolean,
  ) {
    val isHighlight = highlight == index

    // Get the styles for the items
    val themeDefinition = listBox.theme.getDefinition(AbstractListBox::class.java)
    val itemStyle = DefaultMutableThemeStyle(
      if (selected && focused) {
        themeDefinition.selected
      } else {
        themeDefinition.normal
      },
    )
    val tagStyle = DefaultMutableThemeStyle(
      itemStyle,
    ).setForeground(TextColor.Factory.fromString("#E67E22"))
    val highlightStyle = DefaultMutableThemeStyle(
      itemStyle,
    ).setForeground(TextColor.ANSI.GREEN_BRIGHT)

    // Insert text into gap after prefixTag
    val maxIndex = listBox.size.rows -
      listBox.renderer.getCursorLocation(listBox).row + listBox.selectedIndex - 1
    val maxPrefixTag = listBox.getItemAt(
      maxIndex.coerceAtMost(listBox.itemCount - 1),
    ).prefixTag.length
    val tag = if (isHighlight) "${item.prefixTag} " else item.prefixTag.padEnd(maxPrefixTag + 1)

    // Fill the gap with spaces
    graphics.applyThemeStyle(itemStyle)
    graphics.fill(' ')

    // Add Contents
    var insertCol = 0

    if (isHighlight) {
      // If this row should be highlighted, then we insert the highlight text
      graphics.applyThemeStyle(highlightStyle)
      graphics.putString(insertCol, 0, highlightText)
      insertCol = highlightText.length
    }

    // Insert the prefix tag
    graphics.applyThemeStyle(tagStyle)
    graphics.putString(insertCol, 0, tag)

    // Insert text
    insertCol += tag.length
    graphics.applyThemeStyle(if (isHighlight) highlightStyle else itemStyle)
    graphics.putString(insertCol, 0, "$item")
  }
}
