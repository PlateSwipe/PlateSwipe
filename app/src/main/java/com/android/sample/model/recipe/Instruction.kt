package com.android.sample.model.recipe

import com.android.sample.ui.createRecipe.IconType

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
    val icon: IconType?,
) {}
