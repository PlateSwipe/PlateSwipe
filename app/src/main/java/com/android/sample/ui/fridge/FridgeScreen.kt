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
import androidx.compose.ui.res.stringResource
import com.android.sample.R
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

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

  Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top) {
          Text(
              text = stringResource(R.string.fridge_screen),
              style = MaterialTheme.typography.bodySmall)
          Button(onClick = { navigationActions.navigateTo(Screen.CAMERA_SCAN_CODE_BAR) }) {
            Text(text = stringResource(R.string.scan_barcode))
          }
          Button(onClick = { navigationActions.navigateTo(Screen.CAMERA_TAKE_PHOTO) }) {
            Text(text = stringResource(R.string.take_photo))
          }
          Button(onClick = { navigationActions.navigateTo(Screen.CAMERA_TAKE_PHOTO) }) {
            Text(text = stringResource(R.string.take_photo))
          }

          Button(onClick = { navigationActions.navigateTo(Screen.CAMERA_IMPORT_PHOTO) }) {
            Text(text = stringResource(R.string.import_photo))
          }
        }
  }
}
