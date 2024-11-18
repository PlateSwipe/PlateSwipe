package com.android.sample.model.recipe

import com.android.sample.resources.C.TestTag.Instruction.DEFAULT_ICON
import org.junit.Test

class InstructionTest {
  private val text = "text"
  private val time = "time"
  private val icon = "fire"

  @Test
  fun `instruction initializes correctly`() {
    val instruction = Instruction(description = text, time = time, iconType = icon)
    assert(instruction.description == text)
    assert(instruction.time == time)
    assert(instruction.iconType == icon)
  }

  @Test
  fun `instruction default icon is the DEFAULT_ICON`() {
    val instruction = Instruction(description = text, time = time, iconType = null)
    assert(instruction.getIcon().iconName == DEFAULT_ICON)
  }

  @Test
  fun `instruction icon is the iconType`() {
    val instruction = Instruction(description = text, time = time, iconType = icon)
    assert(instruction.getIcon().iconName == icon)
  }
}
