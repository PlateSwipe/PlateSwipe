package com.android.sample.model.recipe

import kotlinx.coroutines.flow.StateFlow

/** Interface for the recipe overview view model. */
interface RecipeOverviewViewModel {
  /** The current recipe to display */
  val currentRecipe: StateFlow<Recipe?>
}
