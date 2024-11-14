package com.android.sample.ui.fridge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.android.sample.R
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.ui.createRecipe.ChefImage
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.utils.PlateSwipeScaffold

/**
 * Fridge Screen
 *
 * @param navigationActions
 * @return Unit
 *
 * Function to display the Fridge Screen
 *
 * @see FridgeScreen
 * @see NavigationActions
 * @see IngredientViewModel
 */
@Composable
fun FridgeScreen(navigationActions: NavigationActions) {

  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = navigationActions.currentRoute(),
      content = { paddingValues -> FridgeContent(paddingValues) },
      showBackArrow = true)
}

@Composable
fun FridgeContent(paddingValues: PaddingValues) {
  Column(
      modifier = Modifier.fillMaxSize().padding(paddingValues),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(id = R.string.fridge_screen),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary)
        Text(
            text = stringResource(id = R.string.fridge_screen_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary)
        ChefImage()
      }
}
