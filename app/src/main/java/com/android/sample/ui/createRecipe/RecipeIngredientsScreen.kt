package com.android.sample.ui.createRecipe

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.android.sample.R
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

@Composable
fun RecipeIngredientsScreen(
    navigationActions: NavigationActions,
    ingredientViewModel: IngredientViewModel,
    currentStep: Int,
    modifier: Modifier = Modifier,
) {
  val ingredientList by ingredientViewModel.ingredientList.collectAsState()

  if (ingredientList.isNotEmpty()) {
    navigationActions.navigateTo(Screen.CREATE_RECIPE_LIST_INGREDIENTS)
  } else {
    RecipeStepScreen(
        title = stringResource(id = R.string.no_ingredients),
        subtitle = stringResource(id = R.string.list_ingredients),
        buttonText = stringResource(id = R.string.add_ingredient),
        onButtonClick = { navigationActions.navigateTo(Screen.CREATE_RECIPE_LIST_INGREDIENTS) },
        navigationActions = navigationActions,
        currentStep = currentStep,
        modifier = modifier)
  }
}
