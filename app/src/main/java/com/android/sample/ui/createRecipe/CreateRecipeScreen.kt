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

@Composable
fun CreateRecipeScreen(
    navigationActions: NavigationActions,
    createRecipeViewModel: CreateRecipeViewModel
) {
  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = Route.CREATE_RECIPE,
      showBackArrow = true,
      content = { paddingValues ->
        RecipeNameScreen(
            currentStep = INITIAL_RECIPE_STEP,
            navigationActions = navigationActions,
            createRecipeViewModel = createRecipeViewModel,
            modifier = Modifier.fillMaxSize().padding(paddingValues))
      })
}
