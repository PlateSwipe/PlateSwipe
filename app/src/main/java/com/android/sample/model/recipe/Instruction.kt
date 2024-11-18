package com.android.sample.model.recipe

import com.android.sample.resources.C.TestTag.Instruction.DEFAULT_ICON

/**
 * Data class representing an instruction in a recipe.
 *
 * @property description The description of the instruction.
 * @property time The time required to complete the instruction in minutes. Nullable.
 * @property icon The icon representing the instruction.
 */
data class Instruction(
    val description: String,
    val time: String?,
    val iconType: String?,
) {
  private val icon: IconType = if (!iconType.isNullOrEmpty()) IconType(iconType) else IconType(DEFAULT_ICON)

  fun icon(): IconType {
    return icon
  }
}
