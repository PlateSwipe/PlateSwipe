package com.android.sample.utils

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import com.android.sample.resources.C.TestTag.IngredientSearchScreen.CANCEL_BUTTON
import com.android.sample.resources.C.TestTag.IngredientSearchScreen.CONFIRMATION_BUTTON
import com.android.sample.resources.C.TestTag.IngredientSearchScreen.CONFIRMATION_POPUP
import com.android.sample.ui.utils.ConfirmationPopUp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class ConfirmationPopUpTest {
  @get:Rule val composeTestRule = createComposeRule()

  private val onConfirm = mock<() -> Unit>()
  private val onDismiss = mock<() -> Unit>()

  @Before
  fun setUp() {
    composeTestRule.setContent {
      ConfirmationPopUp(
          onConfirm = onConfirm,
          onDismiss = onDismiss,
          titleText = "Confirm Action",
          confirmationText = "Are you sure you want to proceed?",
          confirmationButtonText = "Confirm")
    }
  }

  @Test
  fun testConfirmButton_onClick_triggersOnConfirm() {
    // Act
    composeTestRule.onNodeWithTag(CONFIRMATION_BUTTON).performClick()

    // Assert
    verify(onConfirm).invoke() // Check if onConfirm callback was triggered
  }

  @Test
  fun testDismissButton_onClick_triggersOnDismiss() {
    // Act
    composeTestRule.onNodeWithTag(CANCEL_BUTTON).performClick()

    // Assert
    verify(onDismiss).invoke() // Check if onDismiss callback was triggered
  }

  @Test
  fun testTextElements_areDisplayedCorrectly() {
    // Assert
    composeTestRule.onNodeWithText("Confirm Action").assertIsDisplayed() // Title is displayed
    composeTestRule
        .onNodeWithText("Are you sure you want to proceed?")
        .assertIsDisplayed() // Confirmation text is displayed
    composeTestRule
        .onNodeWithText("Confirm")
        .assertIsDisplayed() // Confirm button text is displayed
    composeTestRule.onNodeWithText("Cancel").assertIsDisplayed() // Dismiss button text is displayed
  }

  @Test
  fun testPopUpIsVisible() {
    // Assert
    composeTestRule.onNodeWithTag(CONFIRMATION_POPUP).assertIsDisplayed() // Pop-up is visible
  }

  @Test
  fun testOnDismissRequest_triggersOnDismiss() {
    // Act
    composeTestRule
        .onNodeWithTag(CONFIRMATION_POPUP)
        .performSemanticsAction(SemanticsActions.Dismiss)

    // Assert
    verify(onDismiss).invoke() // Check if onDismiss callback was triggered
  }
}
