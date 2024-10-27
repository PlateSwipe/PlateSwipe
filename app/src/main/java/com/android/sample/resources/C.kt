package com.android.sample.resources

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

    // Used in ChefImage composable
    val CHEF_IMAGE_WIDTH = 250.dp
    val CHEF_IMAGE_HEIGHT = 300.dp
    val CHEF_IMAGE_CORNER_RADIUS = 16.dp

    // Initial step value for recipe creation flow
    const val INITIAL_RECIPE_STEP = 0

    // RecipeNameScreen Specific Constants
    val RECIPE_NAME_BASE_PADDING = 16.dp
    val RECIPE_NAME_FIELD_SPACING = 30.dp
    val RECIPE_NAME_BUTTON_WIDTH = 261.dp
    val RECIPE_NAME_BUTTON_HEIGHT = 46.dp
    val RECIPE_NAME_FIELD_HEIGHT = 60.dp
    val RECIPE_NAME_FONT_SPACING = 0.14.sp

    // RecipeStepScreen
    val BASE_PADDING = 16.dp

    val BUTTON_WIDTH = 200.dp
    val BUTTON_HEIGHT = 50.dp
  }
}
