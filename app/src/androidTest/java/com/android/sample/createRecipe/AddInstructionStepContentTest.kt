package com.android.sample.createRecipe

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.SemanticsMatcher.Companion.keyIsDefined
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.android.sample.resources.C.Tag.SAVE_BUTTON_TAG
import com.android.sample.ui.createRecipe.AddInstructionStepContent
import com.android.sample.ui.createRecipe.IconType
import org.junit.Before
import org.junit.Rule
import org.junit.Test

// General tests using default setup content
class AddInstructionStepContentGeneralTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    composeTestRule.setContent {
      AddInstructionStepContent(
          paddingValues = PaddingValues(16.dp), onSave = { _, _, _, _ -> } // Default empty onSave
          )
    }
  }

  @Test
  fun addInstructionStepContent_allFieldsDisplayed() {
    composeTestRule.onNodeWithTag("StepLabel").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TimeInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("CategoryInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("IconDropdown").assertIsDisplayed()
    composeTestRule.onNodeWithTag("InstructionInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag(SAVE_BUTTON_TAG).assertIsDisplayed()
  }

  @Test
  fun addInstructionStepContent_enteringDataUpdatesFields() {
    composeTestRule.onNodeWithTag("TimeInput").performTextInput("10")
    composeTestRule.onNodeWithTag("CategoryInput").performTextInput("Main Course")
    composeTestRule.onNodeWithTag("InstructionInput").performTextInput("Preheat oven to 180°C...")

    composeTestRule.onNodeWithText("10").assertIsDisplayed()
    composeTestRule.onNodeWithText("Main Course").assertIsDisplayed()
    composeTestRule.onNodeWithText("Preheat oven to 180°C...").assertIsDisplayed()
  }

  @Test
  fun addInstructionStepContent_dropdownSelectionUpdatesIcon() {
    composeTestRule.onNodeWithTag("IconDropdown").performClick()
    composeTestRule.waitForIdle()

    composeTestRule
        .onNode(hasText("Fire").and(hasAnyAncestor(keyIsDefined(SemanticsProperties.IsPopup))))
        .performClick()

    composeTestRule.onNodeWithContentDescription("Fire").assertIsDisplayed()
  }

  @Test
  fun addInstructionStepContent_saveButtonEnabledWhenFieldsFilled() {
    composeTestRule.onNodeWithTag("TimeInput").performTextInput("10")
    composeTestRule.onNodeWithTag("CategoryInput").performTextInput("Main Course")
    composeTestRule.onNodeWithTag("InstructionInput").performTextInput("Preheat oven to 180°C...")

    composeTestRule.onNodeWithTag("IconDropdown").performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNode(hasText("Fire").and(hasAnyAncestor(keyIsDefined(SemanticsProperties.IsPopup))))
        .performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(SAVE_BUTTON_TAG).assertIsEnabled().performClick()
  }

  @Test
  fun addInstructionStepContent_saveButtonShowsErrorIfInstructionEmpty() {
    composeTestRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("InstructionError").assertIsDisplayed()
  }
}

// Custom tests requiring specific content setup
class AddInstructionStepContentCustomTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun addInstructionStepContent_iconDefaultsToAxeIfNoneSelected() {
    var savedIcon: IconType? = null

    composeTestRule.setContent {
      AddInstructionStepContent(
          paddingValues = PaddingValues(16.dp), onSave = { _, _, _, icon -> savedIcon = icon })
    }

    composeTestRule.onNodeWithTag("InstructionInput").performTextInput("Preheat oven to 180°C...")
    composeTestRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()
    composeTestRule.waitForIdle()

    assert(savedIcon is IconType.Axe) { "Expected default icon to be Axe, but was $savedIcon" }
  }
}
