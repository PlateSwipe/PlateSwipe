package com.android.sample.resources

// Like R, but C
object C {
  object Tag {
    const val greeting_robo = "second_screen_greeting"

    const val main_screen_container = "main_screen_container"
    const val second_screen_container = "second_screen_container"

    // SwipePage
    const val END_ANIMATION = 1500f

    const val LOADING = "Loading..."

    // RecipesViewModel
    const val MINIMUM_RECIPES_BEFORE_FETCH = 3
    const val NUMBER_RECIPES_TO_FETCH = 2
    const val MAXIMUM_RECIPES_TO_FETCH_MEAL_DB = 5
    const val MAX_NB_OF_INGREDIENTS_IN_A_RECIPE_MEAL_DB = 20
  }
}
