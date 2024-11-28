package com.android.sample.ui.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.resources.C.Dimension.RecipeList.POP_UP_CLIP
import com.android.sample.resources.C.Dimension.RecipeList.POP_UP_DESCRIPTION_FONT_SIZE
import com.android.sample.resources.C.Dimension.RecipeList.POP_UP_ELEVATION
import com.android.sample.resources.C.Tag.PADDING
import com.android.sample.resources.C.TestTag.RecipeList.CANCEL_BUTTON
import com.android.sample.resources.C.TestTag.RecipeList.CONFIRMATION_BUTTON
import com.android.sample.resources.C.TestTag.RecipeList.CONFIRMATION_POP_UP

@Composable
fun PlateSwipeAlertBox(
    popUpMessage: String,
    confirmMessage: String,
    onConfirm: () -> Unit,
    dismissMessage: String,
    onDismiss: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
  AlertDialog(
      onDismissRequest = onDismiss,
      modifier =
          Modifier.fillMaxWidth()
              .padding(PADDING.dp)
              .shadow(elevation = POP_UP_ELEVATION.dp, clip = POP_UP_CLIP)
              .testTag(CONFIRMATION_POP_UP),
      title = {
        Text(
            text = popUpMessage,
            style = MaterialTheme.typography.titleSmall,
            fontSize = POP_UP_DESCRIPTION_FONT_SIZE.sp,
            color = textColor)
      },
      confirmButton = {
        TextButton(onClick = onConfirm, modifier = Modifier.testTag(CONFIRMATION_BUTTON)) {
          Text(
              text = confirmMessage, style = MaterialTheme.typography.titleSmall, color = textColor)
        }
      },
      dismissButton = {
        TextButton(onClick = onDismiss, modifier = Modifier.testTag(CANCEL_BUTTON)) {
          Text(
              text = dismissMessage, style = MaterialTheme.typography.titleSmall, color = textColor)
        }
      },
      containerColor = containerColor)
}
