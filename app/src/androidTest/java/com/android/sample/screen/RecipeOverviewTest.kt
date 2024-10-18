package com.android.sample.screen

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import androidx.test.espresso.intent.Intents
import com.android.sample.model.recipe.RecipeRepository
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.recipeOverview.RecipeOverview
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.anyOrNull

class RecipeOverviewTest {
  private lateinit var navigationActions: NavigationActions
  private lateinit var repository: RecipeRepository
  private lateinit var recipesViewModel: RecipesViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    repository = mock(RecipeRepository::class.java)
    recipesViewModel = RecipesViewModel(repository)
    `when`(navigationActions.currentRoute()).thenReturn(Route.SEARCH)
    `when`(repository.random(eq(1), anyOrNull(), anyOrNull())).then {}
    Intents.init()
  }

  @After
  fun tearDown() {
    Intents.release()
  }

  @Test
  fun screenDisplayedCorrectlyTest() {
    composeTestRule.setContent { RecipeOverview(navigationActions, recipesViewModel) }
    // Checking if the main components of the screen are displayed
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("draggableItem").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
  }

  @Test
  fun recipeImageIsDisplayedTest() {
    composeTestRule.setContent { RecipeOverview(navigationActions, recipesViewModel) }
    // Checking if the image is displayed
    composeTestRule.onNodeWithTag("recipeImage").assertIsDisplayed()
  }

  @Test
  fun ratingIconAndTextIsDisplayedTest() {
    composeTestRule.setContent { RecipeOverview(navigationActions, recipesViewModel) }
    // Checking if the recipe description is displayed
    composeTestRule.onNodeWithTag("ratingIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ratingText").assertIsDisplayed()
  }

  @Test
  fun timeToCookDescriptionIsDisplayedTest() {
    composeTestRule.setContent { RecipeOverview(navigationActions, recipesViewModel) }
    // Checking if the times are displayed
    composeTestRule.onNodeWithTag("prepTimeText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cookTimeText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("totalTimeText").assertIsDisplayed()
  }

  @Test
  fun ingredientAndInstructionButtonClickTest() {
    composeTestRule.setContent { RecipeOverview(navigationActions, recipesViewModel) }
    // Checking is the Ingredients tab button is displayed and if it works
    composeTestRule.onNodeWithTag("ingredientsButton").assertHasClickAction()
    composeTestRule.onNodeWithTag("ingredientsButton").performClick()

    // Checking is the Instructions tab button is displayed and if it works
    composeTestRule.onNodeWithTag("instructionsButton").assertHasClickAction()
    composeTestRule.onNodeWithTag("instructionsButton").performClick()
  }

  @Test
  fun testDraggingEventRight() {
    composeTestRule.setContent { RecipeOverview(navigationActions, recipesViewModel) }

    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("draggableItem").assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("draggableItem").performTouchInput { swipeRight() }
    composeTestRule.waitForIdle()
  }
}
