package com.android.sample.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.android.sample.R
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.resources.C
import com.android.sample.resources.C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_IMAGE_BORDER_RADIUS
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL_URL
import com.android.sample.resources.C.TestTag.SwipePage.RECIPE_IMAGE_1

/**
 * ImageBox composable that displays an image of an ingredient.
 *
 * @param ingredient the ingredient to display the image of.
 */
@Composable
fun IngredientImageBox(ingredient: Ingredient) {
  Box(
      modifier =
          Modifier.width(C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_IMAGE_WIDTH.dp)
              .height(C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_IMAGE_HEIGHT.dp)
              .border(
                  width =
                      C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_IMAGE_BORDER_WIDTH.dp,
                  color = Color.Black,
                  shape = RoundedCornerShape(INGREDIENT_DISPLAY_IMAGE_BORDER_RADIUS.dp))
              .background(MaterialTheme.colorScheme.background),
  ) {
    Image(
        painter =
            rememberAsyncImagePainter(model = ingredient.images[PRODUCT_FRONT_IMAGE_THUMBNAIL_URL]),
        contentDescription = stringResource(R.string.recipe_image),
        modifier =
            Modifier.fillMaxSize()
                .testTag(RECIPE_IMAGE_1)
                .clip(RoundedCornerShape(INGREDIENT_DISPLAY_IMAGE_BORDER_RADIUS.dp)),
        contentScale = ContentScale.Fit,
    )
  }
}
