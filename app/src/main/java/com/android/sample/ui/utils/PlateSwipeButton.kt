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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
    width: Dp = C.Tag.PlateSwipeButton.BUTTON_WIDTH,
    height: Dp = C.Tag.PlateSwipeButton.BUTTON_HEIGHT,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
  Button(
      onClick = { onClick() },
      modifier =
          modifier
              .width(width)
              .height(height)
              .background(
                  color = backgroundColor, shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE.dp)),
      colors =
          ButtonDefaults.buttonColors(
              containerColor = backgroundColor, contentColor = contentColor),
      shape = RoundedCornerShape(ROUNDED_CORNER_SHAPE.dp)) {
        Text(text = text, textAlign = TextAlign.Center)
      }
}
