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

    // ImageRepositoryFirebase
    const val PROFILE_IMAGE_DIR = "images/profile/"
    const val RECIPE_IMAGE_DIR = "images/recipe/"
    const val INGREDIENTS_IMAGE_DIR = "images/ingredient/"
    const val DEFAULT_IMAGE_NAME = "default.jpg"
  }
}
