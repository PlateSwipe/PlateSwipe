package com.android.sample.ui.createRecipe

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.android.sample.R
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

@Composable
fun RecipeIngredientsScreen(
    navigationActions: NavigationActions,
    createRecipeViewModel: CreateRecipeViewModel,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
  RecipeStepScreen(
      title = stringResource(id = R.string.no_ingredients),
      subtitle = stringResource(id = R.string.list_ingredients),
      buttonText = stringResource(id = R.string.add_ingredient),
      onButtonClick = {
        createRecipeViewModel.addIngredient("Banana", "3")
        navigationActions.navigateTo(Screen.CREATE_RECIPE_SEARCH_INGREDIENTS)
      },
      navigationActions = navigationActions,
      currentStep = currentStep,
      modifier = modifier)
}
