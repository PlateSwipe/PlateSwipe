package com.android.sample.ui.createRecipe

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.android.sample.R
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.searchIngredient.SearchIngredientScreen

/**
 * A composable that displays the ingredient search screen.
 *
 * @param navigationActions the navigation actions.
 * @param ingredientViewModel the view model for the ingredient.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun IngredientSearchScreen(
    navigationActions: NavigationActions,
    ingredientViewModel: IngredientViewModel,
) {
  SearchIngredientScreen(
      navigationActions = navigationActions,
      searchIngredientViewModel = ingredientViewModel,
      popUpTitle = stringResource(R.string.pop_up_title),
      popUpConfirmationText = stringResource(R.string.pop_up_description),
      popUpConfirmationButtonText = stringResource(R.string.pop_up_confirmation),
      onConfirmation = {
        ingredientViewModel.addIngredient(it)
        navigationActions.navigateTo(Screen.CREATE_RECIPE_LIST_INGREDIENTS)
      },
  )
}
