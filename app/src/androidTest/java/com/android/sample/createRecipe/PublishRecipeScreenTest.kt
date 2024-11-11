package com.android.sample.createRecipe

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.model.recipe.FirestoreRecipesRepository
import com.android.sample.ui.createRecipe.PublishRecipeScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PublishRecipeScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var createRecipeViewModel: CreateRecipeViewModel
  private val repository = mockk<FirestoreRecipesRepository>(relaxed = true)

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mockk(relaxed = true)
    createRecipeViewModel = spyk(CreateRecipeViewModel(repository))
    Intents.init()

    every { repository.getNewUid() } returns "valid-id"
  }

  @After
  fun tearDown() {
    Intents.release()
  }

  /** Verifies that all UI elements are displayed on the PublishRecipeScreen. */
  @Test
  fun publishRecipeScreen_allFieldsDisplayed() {
    composeTestRule.setContent {
      PublishRecipeScreen(
          navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    composeTestRule.onNodeWithTag("DoneText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ChefImage").assertIsDisplayed()
    composeTestRule.onNodeWithTag("PublishButton").assertIsDisplayed()
  }

  /**
   * Verifies that clicking the publish button invokes recipe publishing and navigates to the next
   * screen.
   */
  @Test
  fun publishRecipeScreen_publishButtonTriggersPublishAndNavigation() = runTest {
    composeTestRule.setContent {
      PublishRecipeScreen(
          navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    // Click the publish button
    composeTestRule.onNodeWithText("Publish Recipe").performClick()

    // Verify publishRecipe was called
    verify { createRecipeViewModel.publishRecipe() }

    // Verify navigation to the CREATE_RECIPE screen
    verify { navigationActions.navigateTo(Screen.SWIPE) }
  }

  /** Verifies that an error is thrown when trying to publish without a recipe ID. */
  @Test(expected = IllegalArgumentException::class)
  fun publishRecipeScreen_throwsErrorWhenIdIsBlank() = runTest {
    // Simulate blank ID
    every { repository.getNewUid() } returns ""
    createRecipeViewModel.publishRecipe()
  }

  /**
   * Verifies that an error is thrown when trying to add an ingredient with a blank name or
   * measurement.
   */
  @Test(expected = IllegalArgumentException::class)
  fun publishRecipeScreen_throwsErrorWhenIngredientOrMeasurementIsBlank() {
    createRecipeViewModel.addIngredient("", "1 cup")
  }

  @Test(expected = IllegalArgumentException::class)
  fun publishRecipeScreen_throwsErrorWhenMeasurementIsBlank() {
    createRecipeViewModel.addIngredient("Sugar", "")
  }
}
