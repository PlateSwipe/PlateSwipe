package com.android.sample.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.rememberAsyncImagePainter
import com.android.sample.R
import com.android.sample.model.recipe.Recipe

/**
 * Composable function to display a recipe image.
 *
 * @param displayCard1 Boolean indicating whether to display the current recipe's image.
 * @param currentRecipe The current recipe whose image is to be displayed.
 * @param nextRecipe The next recipe whose image is to be displayed if displayCard1 is false.
 * @param isConnected Boolean indicating if the device is connected to the internet.
 * @param name The test tag name for the image.
 */
@Composable
fun RecipeImage(
    displayCard1: Boolean,
    currentRecipe: Recipe?,
    nextRecipe: Recipe?,
    isConnected: Boolean,
    name: String
) {

  Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.onPrimary)) {
    if (isConnected) {
      // Load image from URL
      Image(
          painter =
              rememberAsyncImagePainter(
                  model = if (displayCard1) currentRecipe?.url else nextRecipe?.url),
          contentDescription = stringResource(R.string.recipe_image),
          modifier = Modifier.fillMaxSize().testTag(name),
          contentScale = ContentScale.Crop)
    } else {
      // Load no-wifi icon from drawable
      Image(
          painter = painterResource(id = R.drawable.round_wifi_off_24),
          contentDescription = "no wifi image",
          modifier = Modifier.fillMaxSize().testTag(name),
          contentScale = ContentScale.Crop)
    }
  }
}
