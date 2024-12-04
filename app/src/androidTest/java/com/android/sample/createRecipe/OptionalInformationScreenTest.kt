package com.android.sample.createRecipe

import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.model.recipe.FirestoreRecipesRepository
import com.android.sample.model.recipe.Recipe
import com.android.sample.resources.C.TestTag.Category.BUTTON_TEST_TAG
import com.android.sample.resources.C.TestTag.Category.CATEGORY_DROPDOWN
import com.android.sample.resources.C.TestTag.Category.DIFFICULTY_DROPDOWN
import com.android.sample.resources.C.TestTag.PlateSwipeDropdown.DROPDOWN_TITLE
import com.android.sample.ui.createRecipe.OptionalInformationScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OptionalInformationScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var createRecipeViewModel: CreateRecipeViewModel
  private lateinit var repoImg: ImageRepositoryFirebase

  @Before
  fun setUp() = runTest {
    mockNavigationActions = mockk(relaxed = true)

    val firestore = mockk<FirebaseFirestore>(relaxed = true)
    val repository = FirestoreRecipesRepository(firestore)
    repoImg = mockk(relaxed = true)
    createRecipeViewModel = spyk(CreateRecipeViewModel(repository, repoImg))
  }

  @Test
  fun testCategoryScreenComponentsAreDisplayed() {
    composeTestRule.setContent {
      OptionalInformationScreen(
          navigationActions = mockNavigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    composeTestRule.onNodeWithText("Select a Category").assertExists().assertIsDisplayed()
    composeTestRule
        .onNodeWithText(
            "Giving this information is optional, but it can make it easier for others to find your recipe.")
        .assertExists()
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag(CATEGORY_DROPDOWN).assertIsDisplayed()
    composeTestRule.onNodeWithTag("NextStepButton").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag(BUTTON_TEST_TAG).assertExists().assertIsDisplayed()
  }

  @Test
  fun testSelectingCategoryUpdatesButtonText() {
    composeTestRule.setContent {
      OptionalInformationScreen(
          navigationActions = mockNavigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    val selectedCategory = "Vegan"

    // Open dropdown menu
    composeTestRule.onNodeWithTag(CATEGORY_DROPDOWN).performClick()

    // Scroll to the desired category if necessary
    composeTestRule.onNodeWithText(selectedCategory, useUnmergedTree = true).performScrollTo()

    // Wait for the UI to update
    composeTestRule.waitForIdle()

    // Select the category
    composeTestRule.onNodeWithText(selectedCategory).performClick()

    composeTestRule.waitForIdle()

    // Verify dropdown button text updates to the selected category
    composeTestRule
        .onAllNodesWithTag(DROPDOWN_TITLE, useUnmergedTree = true)
        .assertCountEquals(2)
        .assertAny(hasText(selectedCategory))
  }

  @Test
  fun testSelectingDifficultyUpdatesButtonText() {
    composeTestRule.setContent {
      OptionalInformationScreen(
        navigationActions = mockNavigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    val selectedDifficulty = Recipe.getDifficulties()[1]

    // Open dropdown menu
    composeTestRule.onNodeWithTag(DIFFICULTY_DROPDOWN).performClick()

    composeTestRule.onNodeWithText(selectedDifficulty, useUnmergedTree = true).performScrollTo()

    // Wait for the UI to update
    composeTestRule.waitForIdle()

    // Select the category
    composeTestRule.onNodeWithText(selectedDifficulty).performClick()

    composeTestRule.waitForIdle()

    // Verify dropdown button text updates to the selected category
    composeTestRule
      .onAllNodesWithTag(DROPDOWN_TITLE, useUnmergedTree = true)
      .assertCountEquals(2)
      .assertAny(hasText(selectedDifficulty))
  }

  @Test
  fun testNextStepButtonNavigatesToNextScreen() {
    composeTestRule.setContent {
      OptionalInformationScreen(
          navigationActions = mockNavigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    // Click the "Next Step" button
    composeTestRule.onNodeWithTag("NextStepButton").assertExists().performClick()

    // Verify navigation to the next screen
    verify { mockNavigationActions.navigateTo(Screen.CREATE_RECIPE_INGREDIENTS) }
  }
}
