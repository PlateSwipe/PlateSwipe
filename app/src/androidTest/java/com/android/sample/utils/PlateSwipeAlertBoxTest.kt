package com.android.sample.utils

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.sample.resources.C.TestTag.RecipeList.CANCEL_BUTTON
import com.android.sample.resources.C.TestTag.RecipeList.CONFIRMATION_BUTTON
import com.android.sample.resources.C.TestTag.RecipeList.CONFIRMATION_POP_UP
import com.android.sample.ui.utils.PlateSwipeAlertBox
import org.junit.Rule
import org.junit.Test

class PlateSwipeAlertBoxTest {
  @get:Rule val composeTestRule = createComposeRule()
  private val text = "Alert Box"
  private val confirmMessage = "Confirm"
  private val dismissMessage = "Dismiss"

  @Test
  fun testPlateSwipeAlertBox() {
    composeTestRule.setContent {
      PlateSwipeAlertBox(
          popUpMessage = text,
          confirmMessage = confirmMessage,
          onConfirm = {},
          dismissMessage = dismissMessage,
          onDismiss = {},
      )
    }

    composeTestRule.onNodeWithTag(CONFIRMATION_POP_UP).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CONFIRMATION_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CANCEL_BUTTON).assertIsDisplayed()
  }
}
