package com.android.sample.ui.createRecipe

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.resources.C.Tag.INITIAL_RECIPE_STEP
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.utils.PlateSwipeScaffold

/**
 * CreateRecipeScreen is the main screen for creating a recipe. It contains the RecipeNameScreen
 * which is the first step of the recipe creation process.
 *
 * @param navigationActions actions to navigate between screens
 * @param createRecipeViewModel view model for the recipe creation process
 * @param isEditing true if the user is editing a recipe, false otherwise
 */
@Composable
fun CreateRecipeScreen(
    navigationActions: NavigationActions,
    createRecipeViewModel: CreateRecipeViewModel,
    isEditing: Boolean
) {
  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = if (isEditing) Route.ACCOUNT else Route.CREATE_RECIPE,
      showBackArrow = isEditing,
      content = { paddingValues ->
        RecipeNameScreen(
            currentStep = INITIAL_RECIPE_STEP,
            navigationActions = navigationActions,
            createRecipeViewModel = createRecipeViewModel,
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            isEditing = isEditing)
      })
}
