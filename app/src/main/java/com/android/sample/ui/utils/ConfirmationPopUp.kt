package com.android.sample.ui.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.resources.C.Dimension.ConfirmationPopUp.TEXT_FONT_SIZE
import com.android.sample.resources.C.Dimension.ConfirmationPopUp.TEXT_LINE_HEIGHT
import com.android.sample.resources.C.Dimension.IngredientSearchScreen.POP_UP_ELEVATION
import com.android.sample.resources.C.Dimension.PADDING_16
import com.android.sample.resources.C.TestTag.IngredientSearchScreen.CANCEL_BUTTON
import com.android.sample.resources.C.TestTag.IngredientSearchScreen.CONFIRMATION_BUTTON
import com.android.sample.resources.C.TestTag.IngredientSearchScreen.CONFIRMATION_POPUP

/**
 * A composable that displays a confirmation pop-up.
 *
 * @param onConfirm the callback to invoke when the user confirms the action.
 * @param onDismiss the callback to invoke when the user dismisses the pop-up.
 */
@Composable
fun ConfirmationPopUp(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    titleText: String,
    confirmationText: String = "",
    confirmationButtonText: String = stringResource(R.string.pop_up_confirm_removal_liked_recipe),
    dismissButtonText: String = stringResource(R.string.pop_up_confirm_cancel_removal_liked_recipe)
) {
  AlertDialog(
      onDismissRequest = onDismiss,
      modifier = Modifier.fillMaxWidth().padding(PADDING_16.dp).testTag(CONFIRMATION_POPUP),
      shape = MaterialTheme.shapes.medium,
      tonalElevation = POP_UP_ELEVATION.dp,
      title = {
        Text(
            text = titleText,
            style = MaterialTheme.typography.titleMedium,
            lineHeight = TEXT_LINE_HEIGHT.sp,
            fontSize = TEXT_FONT_SIZE.sp,
            color = MaterialTheme.colorScheme.onPrimary)
      },
      text = {
        if (confirmationText.isNotEmpty()) {
          Text(
              text = confirmationText,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onPrimary)
        }
      },
      confirmButton = {
        TextButton(onClick = onConfirm, modifier = Modifier.testTag(CONFIRMATION_BUTTON)) {
          Text(
              text = confirmationButtonText,
              style = MaterialTheme.typography.titleSmall,
              color = MaterialTheme.colorScheme.onPrimary)
        }
      },
      dismissButton = {
        TextButton(onClick = onDismiss, modifier = Modifier.testTag(CANCEL_BUTTON)) {
          Text(
              dismissButtonText,
              style = MaterialTheme.typography.titleSmall,
              color = MaterialTheme.colorScheme.onPrimary)
        }
      },
      containerColor = MaterialTheme.colorScheme.secondary)
}
