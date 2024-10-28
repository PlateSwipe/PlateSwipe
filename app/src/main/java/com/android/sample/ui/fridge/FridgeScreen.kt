package com.android.sample.ui.fridge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

/**
 * Fridge Screen
 *
 * @param navigationActions
 * @param ingredientViewModel
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

  Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top) {
          Text(text = "Fridge Screen", style = MaterialTheme.typography.bodySmall)
          Button(onClick = { navigationActions.navigateTo(Screen.CAMERA_SCAN_CODE_BAR) }) {
            Text(text = "Scan Barcode")
          }
        }
  }
}
