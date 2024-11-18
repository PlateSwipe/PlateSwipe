package com.android.sample.createRecipe

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.SemanticsMatcher.Companion.keyIsDefined
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.recipe.IconType
import com.android.sample.ui.createRecipe.IconDropdownMenu
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IconDropdownMenuTest {

  @get:Rule val composeTestRule = createComposeRule()

  private var selectedIcon: IconType? = null

  @Before
  fun setUp() {
    // Set up the composable for testing with the selectedIcon and an onIconSelected callback
    composeTestRule.setContent {
      IconDropdownMenu(selectedIcon = selectedIcon, onIconSelected = { selectedIcon = it })
    }
  }

  @Test
  fun iconDropdownMenu_initiallyShowsPlaceholderText() {
    // Assert that the placeholder text "Add Icon" is displayed initially
    composeTestRule.onNodeWithText("Add Icon", useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun iconDropdownMenu_expandsAndDisplaysIconsOnClick() = runTest {
    // Click on the dropdown trigger to expand the dropdown menu
    composeTestRule.onNodeWithTag("IconDropdownTrigger").performClick()
    composeTestRule.waitForIdle() // Ensure the dropdown is fully expanded

    // Verify that each icon option is displayed
    composeTestRule.onNodeWithText("Cook", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithText("Season", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithText("Mix", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithText("Prep", useUnmergedTree = true).assertIsDisplayed()

  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun iconDropdownMenu_collapsesWhenIconIsSelected() = runTest {
    // Click on the dropdown trigger to expand the dropdown menu
    composeTestRule.onNodeWithTag("IconDropdownTrigger").performClick()
    composeTestRule.waitForIdle()

    // Select an icon, e.g., "Season"
    composeTestRule.onNodeWithText("Season", useUnmergedTree = true).performClick()
    advanceUntilIdle() // Wait for UI updates after selection

    // Verify that the dropdown has collapsed and the "Season" option is no longer visible
    composeTestRule.onNodeWithText("Season", useUnmergedTree = true).assertDoesNotExist()
  }

  @Test
  fun dropdownMenu_expandsAndDisplaysOptions() = runTest {
    // Step 1: Expand the dropdown menu
    composeTestRule.onNodeWithTag("IconDropdownTrigger").performClick()
    composeTestRule.waitForIdle()

    // Step 2: Verify that each option is displayed within the popup
    composeTestRule
        .onNode(hasText("Heat").and(hasAnyAncestor(keyIsDefined(SemanticsProperties.IsPopup))))
        .assertIsDisplayed()
    composeTestRule
        .onNode(hasText("Season").and(hasAnyAncestor(keyIsDefined(SemanticsProperties.IsPopup))))
        .assertIsDisplayed()
    composeTestRule
        .onNode(hasText("Mix").and(hasAnyAncestor(keyIsDefined(SemanticsProperties.IsPopup))))
        .assertIsDisplayed()
    composeTestRule
        .onNode(hasText("Prep").and(hasAnyAncestor(keyIsDefined(SemanticsProperties.IsPopup))))
        .assertIsDisplayed()
    composeTestRule
      .onNode(hasText("Cook").and(hasAnyAncestor(keyIsDefined(SemanticsProperties.IsPopup))))
  }

  @Test
  fun dropdownMenu_closesOnBackPress() = runTest {
    // Step 1: Expand the dropdown menu
    composeTestRule.onNodeWithTag("IconDropdownTrigger").performClick()
    composeTestRule.waitForIdle()

    // Step 2: Press back to close the dropdown
    composeTestRule.onNodeWithTag("IconDropdownTrigger").assertExists()
    Espresso.pressBack() // Close the dropdown

    // Step 3: Assert dropdown is closed by verifying "Cook" is not displayed
    composeTestRule
        .onNode(hasText("Cook").and(hasAnyAncestor(keyIsDefined(SemanticsProperties.IsPopup))))
        .assertDoesNotExist()
  }
}
