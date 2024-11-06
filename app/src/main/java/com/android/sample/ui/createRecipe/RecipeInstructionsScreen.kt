package com.android.sample.ui.createRecipe

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.android.sample.R
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

@Composable
fun RecipeInstructionsScreen(
    navigationActions: NavigationActions,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
  RecipeStepScreen(
      title = stringResource(id = R.string.add_instructions),
      subtitle = stringResource(id = R.string.describe_instructions),
      buttonText = stringResource(id = R.string.add_step),
      onButtonClick = { navigationActions.navigateTo(Screen.CREATE_RECIPE_ADD_INSTRUCTION) },
      navigationActions = navigationActions,
      currentStep = currentStep,
      modifier = modifier)
}
