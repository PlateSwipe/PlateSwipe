package com.android.sample.createRecipe

import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.model.recipe.networkData.FirestoreRecipesRepository
import com.android.sample.resources.C.Tag.SCREEN_HEIGHT_THRESHOLD
import com.android.sample.resources.C.Tag.SCREEN_WIDTH_THRESHOLD
import com.android.sample.resources.C.TestTag.RecipeNameScreen.LOADING_COOK_TEST_TAG
import com.android.sample.ui.createRecipe.RecipeNameScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecipeNameScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var createRecipeViewModel: CreateRecipeViewModel
  private lateinit var repoImg: ImageRepositoryFirebase

  @Before
  fun setUp() = runTest {
    mockNavigationActions = mockk(relaxed = true)

    // Mock Firestore and Repository for use in ViewModel
    val firestore = mockk<FirebaseFirestore>(relaxed = true)
    val repository = FirestoreRecipesRepository(firestore)
    repoImg = mockk(relaxed = true)
    createRecipeViewModel = spyk(CreateRecipeViewModel(repository, repoImg)) // Spy to verify calls
  }

  /** New Test: Ensures the loading indicator is shown during initialization. */
  @Test
  fun testLoadingIndicatorShownWhileInitializing() = runTest {
    composeTestRule.mainClock.autoAdvance = false // Pause the test clock

    composeTestRule.setContent {
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = false)
    }

    // Assert that the CircularProgressIndicator (LoadingCook) is displayed
    composeTestRule.onNodeWithTag(LOADING_COOK_TEST_TAG).assertExists().assertIsDisplayed()

    // Advance the clock to simulate initialization completing
    composeTestRule.mainClock.advanceTimeBy(1000) // Simulate delay for initialization

    // Assert that the loading indicator is gone and the rest of the screen is displayed
    composeTestRule.onNodeWithTag(LOADING_COOK_TEST_TAG).assertDoesNotExist()
    composeTestRule.onNodeWithTag("recipeNameTextField").assertExists().assertIsDisplayed()
  }

  @Test
  fun testLoadingIndicatorShownWhileInitializingInEditMode() = runTest {
    composeTestRule.mainClock.autoAdvance = false // Pause the test clock

    composeTestRule.setContent {
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = true)
    }

    // Assert that the CircularProgressIndicator (LoadingCook) is displayed
    composeTestRule.onNodeWithTag(LOADING_COOK_TEST_TAG).assertExists().assertIsDisplayed()

    // Advance the clock to simulate initialization completing
    composeTestRule.mainClock.advanceTimeBy(1000) // Simulate delay for initialization

    // Assert that the loading indicator is gone and the rest of the screen is displayed
    composeTestRule.onNodeWithTag(LOADING_COOK_TEST_TAG).assertDoesNotExist()
    composeTestRule.onNodeWithTag("recipeNameTextField").assertExists().assertIsDisplayed()
  }

  /** Tests if all components of RecipeNameScreen are displayed in create mode. */
  @Test
  fun testRecipeNameScreenComponentsAreDisplayedInCreateMode() {
    composeTestRule.setContent {
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = false)
    }

    // Assert title, subtitle, and text field are displayed
    composeTestRule.onNodeWithTag("RecipeTitle").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("RecipeSubtitle").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("recipeNameTextField").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("NextStepButton").assertExists().assertIsDisplayed()

    // Verify create mode title and description
    composeTestRule.onNodeWithTag("RecipeTitle").assertTextEquals("Create your recipe")
    composeTestRule
        .onNodeWithTag("RecipeSubtitle")
        .assertTextEquals(
            "Create a recipe that others can discover and enjoy. Start by giving your dish a name!")
  }

  /** New Test: Ensures components are displayed correctly in edit mode. */
  @Test
  fun testRecipeNameScreenComponentsAreDisplayedInEditMode() {
    every { createRecipeViewModel.getRecipeName() } returns "Edited Recipe Name"

    composeTestRule.setContent {
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = true)
    }

    // Assert title, subtitle, and text field are displayed
    composeTestRule.onNodeWithTag("RecipeTitle").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("RecipeSubtitle").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("recipeNameTextField").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("NextStepButton").assertExists().assertIsDisplayed()

    // Verify edit mode title and description
    composeTestRule.onNodeWithTag("RecipeTitle").assertTextEquals("Edit your recipe")
    composeTestRule
        .onNodeWithTag("RecipeSubtitle")
        .assertTextEquals("Make changes to your recipe details below.")
  }

  /** Updated Test: Verifies `startNewRecipe` is called in create mode. */
  @Test
  fun testStartNewRecipeCalledInCreateMode() = runTest {
    composeTestRule.setContent {
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = false)
    }

    // Verify `startNewRecipe` is called
    verify { createRecipeViewModel.startNewRecipe() }
  }

  /** Updated Test: Ensures `getRecipeName` retrieves the name in edit mode. */
  @Test
  fun testRecipeNameFetchedInEditMode() = runTest {
    every { createRecipeViewModel.getRecipeName() } returns "Edited Recipe"

    composeTestRule.setContent {
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = true)
    }

    // Verify the text field contains the fetched recipe name
    composeTestRule.onNodeWithTag("recipeNameTextField").assertTextEquals("Edited Recipe")
  }

  /**
   * New Test: Ensure an error message is displayed when NextStepButton is clicked without entering
   * a name.*
   */
  @Test
  fun testErrorDisplayedWhenRecipeNameIsEmpty() {
    composeTestRule.setContent {
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = false)
    }
    composeTestRule.onNodeWithTag("NextStepButton").performClick()
    composeTestRule.onNodeWithTag("ErrorMessage").assertExists().assertIsDisplayed()
  }

  @Test
  fun testErrorDisplayedWhenRecipeNameIsEmptyInEditMode() {
    composeTestRule.setContent {
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = true)
    }
    composeTestRule.onNodeWithTag("NextStepButton").performClick()
    composeTestRule.onNodeWithTag("ErrorMessage").assertExists().assertIsDisplayed()
  }

  /** Test: Ensures the ChefImage is displayed when screen size is above the threshold. */
  @Test
  fun testChefImageDisplayedWhenScreenSizeAboveThreshold() {
    composeTestRule.setContent {
      LocalConfiguration.current.apply {
        screenWidthDp = SCREEN_WIDTH_THRESHOLD + 1
        screenHeightDp = SCREEN_HEIGHT_THRESHOLD + 1
      }
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = false)
    }
    composeTestRule.onNodeWithTag("ChefImage").assertExists().assertIsDisplayed()
  }

  /** Test: Ensures the ChefImage is not displayed when screen size is below the threshold. */
  @Test
  fun testChefImageNotDisplayedWhenScreenSizeBelowThreshold() {
    composeTestRule.setContent {
      LocalConfiguration.current.apply {
        screenWidthDp = SCREEN_WIDTH_THRESHOLD - 1
        screenHeightDp = SCREEN_HEIGHT_THRESHOLD - 1
      }
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = false)
    }
    composeTestRule.onNodeWithTag("ChefImage").assertDoesNotExist()
  }

  @Test
  fun testChefImageDisplayedWhenScreenSizeAboveThresholdInEditMode() {
    composeTestRule.setContent {
      LocalConfiguration.current.apply {
        screenWidthDp = SCREEN_WIDTH_THRESHOLD + 1
        screenHeightDp = SCREEN_HEIGHT_THRESHOLD + 1
      }
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = true)
    }
    composeTestRule.onNodeWithTag("ChefImage").assertExists().assertIsDisplayed()
  }

  @Test
  fun testChefImageNotDisplayedWhenScreenSizeBelowThresholdInEditMode() {
    composeTestRule.setContent {
      LocalConfiguration.current.apply {
        screenWidthDp = SCREEN_WIDTH_THRESHOLD - 1
        screenHeightDp = SCREEN_HEIGHT_THRESHOLD - 1
      }
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = true)
    }
    composeTestRule.onNodeWithTag("ChefImage").assertDoesNotExist()
  }

  @Test
  fun testNextStepButtonNavigatesToCategoryScreenInCreateMode() = runTest {
    composeTestRule.setContent {
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = false)
    }

    // Provide text input for the recipe name
    composeTestRule.onNodeWithTag("recipeNameTextField").performTextInput("New Recipe")

    // Click the "Next Step" button
    composeTestRule.onNodeWithTag("NextStepButton").assertExists().performClick()

    // Verify navigation to the category screen in create mode
    verify { mockNavigationActions.navigateTo(Screen.CREATE_CATEGORY_SCREEN) }
  }

  @Test
  fun testNextStepButtonNavigatesToCategoryScreenInEditMode() = runTest {
    every { createRecipeViewModel.getRecipeName() } returns "Edited Recipe"

    composeTestRule.setContent {
      RecipeNameScreen(
          navigationActions = mockNavigationActions,
          currentStep = 0,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = true)
    }

    // Ensure the text field contains the pre-filled recipe name
    composeTestRule.onNodeWithTag("recipeNameTextField").assertTextEquals("Edited Recipe")

    // Click the "Next Step" button
    composeTestRule.onNodeWithTag("NextStepButton").assertExists().performClick()

    // Verify navigation to the category screen in edit mode
    verify { mockNavigationActions.navigateTo(Screen.EDIT_CATEGORY_SCREEN) }
  }
}
