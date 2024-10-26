package com.android.sample.ui.createRecipe

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

@Composable
fun RecipeIngredientsScreen(
    navigationActions: NavigationActions,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
  RecipeStepScreen(
      title = "No Ingredients",
      subtitle = "List the ingredients needed for your recipe. Add as many as you need.",
      buttonText = "Add Ingredients",
      onButtonClick = { navigationActions.navigateTo(Screen.CREATE_RECIPE_INSTRUCTIONS) },
      navigationActions = navigationActions,
      currentStep = currentStep,
      modifier = modifier)
}
