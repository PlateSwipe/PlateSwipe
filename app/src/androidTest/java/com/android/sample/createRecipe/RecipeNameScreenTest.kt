package com.android.sample.ui.createRecipe

import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.model.recipe.FirestoreRecipesRepository
import com.android.sample.resources.C.Tag.SCREEN_HEIGHT_THRESHOLD
import com.android.sample.resources.C.Tag.SCREEN_WIDTH_THRESHOLD
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecipeNameScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var createRecipeViewModel: CreateRecipeViewModel

  @Before
  fun setUp() = runTest {
    mockNavigationActions = mockk(relaxed = true)

    // Mock Firestore and Repository for use in ViewModel
    val firestore = mockk<FirebaseFirestore>(relaxed = true)
    val repository = FirestoreRecipesRepository(firestore)
    createRecipeViewModel = spyk(CreateRecipeViewModel(repository)) // Spy to verify calls

    // Set the content for testing

  }

  @After
  fun tearDown() {
    // Add any necessary cleanup here
  }

  /** Tests if all components of RecipeNameScreen are displayed. */
  @Test
  fun testRecipeNameScreenComponentsAreDisplayed() {
    composeTestRule.setContent {
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel)
    }
    composeTestRule.onNodeWithTag("RecipeTitle").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("RecipeSubtitle").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("recipeNameTextField").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("NextStepButton").assertExists().assertIsDisplayed()
  }

  /** Tests that an error message is shown if the recipe name is empty when clicking "Next Step". */
  @Test
  fun testErrorDisplayedWhenRecipeNameIsEmpty() = runTest {
    composeTestRule.setContent {
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel)
    }
    composeTestRule.onNodeWithText("Next Step").performClick()
    composeTestRule.onNodeWithText("Please enter a recipe name").assertExists().assertIsDisplayed()
  }

  /**
   * Tests that no error message is shown if the recipe name is entered and "Next Step" is clicked.
   */
  @Test
  fun testNoErrorDisplayedWhenRecipeNameIsEntered() = runTest {
    composeTestRule.setContent {
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel)
    }
    composeTestRule.onNodeWithTag("recipeNameTextField").performTextInput("Chocolate Cake")
    composeTestRule.onNodeWithText("Next Step").performClick()
    composeTestRule.onNodeWithText("Please enter a recipe name").assertDoesNotExist()
  }

  /**
   * Tests that the "Next Step" button calls updateRecipeName and navigates to ingredients screen.
   */
  @Test
  fun testNextStepButtonCallsUpdateAndNavigatesWhenRecipeNameIsEntered() = runTest {
    composeTestRule.setContent {
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel)
    }
    // Set up expectation for updateRecipeName
    every { createRecipeViewModel.updateRecipeName(any()) } just runs

    // Provide text input for the recipe name
    composeTestRule.onNodeWithTag("recipeNameTextField").performTextInput("Chocolate Cake")

    // Click on the "Next Step" button
    composeTestRule.onNodeWithText("Next Step").performClick()
    composeTestRule.awaitIdle() // Ensure the UI has settled

    // Verify that updateRecipeName is called with "Chocolate Cake"
    verify { createRecipeViewModel.updateRecipeName("Chocolate Cake") }

    // Verify navigation to the ingredients screen
    verify { mockNavigationActions.navigateTo(Screen.CREATE_RECIPE_INGREDIENTS) }
  }

  /**
   * Tests that the hint text is shown when recipe name is empty and hidden when text is entered.
   */
  @Test
  fun testHintTextVisibilityBasedOnRecipeName() = runTest {
    composeTestRule.setContent {
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel)
    }
    // Retrieve the hint text directly from resources if possible
    val hintText = "Choose a catchy title that reflects your dish"

    // Assert that the hint text is displayed initially (when recipeName is empty)
    composeTestRule.onNodeWithText(hintText).assertExists().assertIsDisplayed()

    // Enter text to make the label disappear
    composeTestRule.onNodeWithTag("recipeNameTextField").performTextInput("Chocolate Cake")

    // Assert that the hint text no longer exists once text is entered
    composeTestRule.onNodeWithText(hintText).assertDoesNotExist()
  }

  @Test
  fun testChefImageDisplayedWhenScreenDimensionsAreLarge() = runTest {
    composeTestRule.setContent {
      LocalConfiguration.current.apply {
        screenWidthDp = SCREEN_WIDTH_THRESHOLD
        screenHeightDp = SCREEN_HEIGHT_THRESHOLD
      }
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel)
    }

    // Check if ChefImage is displayed based on large dimensions
    composeTestRule.onNodeWithTag("ChefImage").assertExists().assertIsDisplayed()
  }

  @Test
  fun testChefImageNotDisplayedWhenScreenDimensionsAreSmall() = runTest {
    composeTestRule.setContent {
      LocalConfiguration.current.apply {
        screenWidthDp = SCREEN_WIDTH_THRESHOLD - 1
        screenHeightDp = SCREEN_HEIGHT_THRESHOLD - 1
      }
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel)
    }

    // Check if ChefImage is not displayed based on small dimensions
    composeTestRule.onNodeWithTag("ChefImage").assertDoesNotExist()
  }
}
