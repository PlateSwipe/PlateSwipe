package com.android.sample.ui.createRecipe

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

@Composable
fun RecipeInstructionsScreen(
    navigationActions: NavigationActions,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
  RecipeStepScreen(
      title = "Add Instructions",
      subtitle = "Describe each step of how to prepare your dish.",
      buttonText = "Add Step",
      onButtonClick = { navigationActions.navigateTo(Screen.CREATE_RECIPE_ADD_INSTRUCTION) },
      navigationActions = navigationActions,
      currentStep = currentStep,
      modifier = modifier)
}
