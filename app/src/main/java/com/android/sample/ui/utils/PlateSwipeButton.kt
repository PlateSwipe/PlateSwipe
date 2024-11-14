package com.android.sample.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.sample.resources.C
import com.android.sample.resources.C.Dimension.CreateRecipeListInstructionsScreen.ROUNDED_CORNER_SHAPE

/**
 * Composable function that displays a button for the PlateSwipe app. This button is used for any
 * big button on the UI screens of the PlateSwipe app.
 *
 * @param text The text to be displayed on the button.
 * @param modifier The modifier to be applied to the button.
 * @param onClick The callback to be invoked when the button is clicked.
 */
@Composable
fun PlateSwipeButton(
    text: String,
    modifier: Modifier,
    onClick: () -> Unit,
) {
  Button(
      onClick = { onClick() },
      modifier =
          modifier
              .width(C.Tag.PlateSwipeButton.BUTTON_WIDTH)
              .height(C.Tag.PlateSwipeButton.BUTTON_HEIGHT)
              .background(
                  color = MaterialTheme.colorScheme.primary,
                  shape = RoundedCornerShape(size = ROUNDED_CORNER_SHAPE.dp)),
      colors =
          ButtonDefaults.buttonColors(
              MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary),
      shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE.dp)) {
        Text(text = text, textAlign = TextAlign.Center)
      }
}