package com.android.sample.model.recipe

import com.android.sample.resources.C.TestTag.Instruction.DEFAULT_ICON

/**
 * Data class representing an instruction in a recipe.
 *
 * @property description The description of the instruction.
 * @property time The time required to complete the instruction in minutes. Nullable.
 * @property iconType The icon representing the instruction, see IconType class to see the different
 *   icons.
 */
data class Instruction(
    val description: String,
    val time: String?,
    val iconType: String?,
) {
  private val icon: IconType = IconType(iconType ?: DEFAULT_ICON)

  /** Returns the icon representing the instruction. */
  fun getIcon(): IconType {
    return icon
  }
}
