package com.android.sample.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipeRepository
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.swipePage.SwipePage
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

@RunWith(AndroidJUnit4::class)
class SwipePageTest : TestCase() {
  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockRepository: RecipeRepository
  private lateinit var recipesViewModel: RecipesViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @OptIn(ExperimentalCoroutinesApi::class)

  /** This method runs before the test execution. */
  @Before
  fun setUp() {
    mockNavigationActions = mock(NavigationActions::class.java)
    mockRepository = mock(RecipeRepository::class.java)

    val recipe1 =
        Recipe(
            "Recipe 1",
            "",
            "url1",
            "Instructions 1",
            "Category 1",
            "Area 1",
            listOf(Pair("Ingredient 1", "Ingredient 1")))
    val recipe2 =
        Recipe(
            "Recipe 2",
            "",
            "url2",
            "Instructions 2",
            "Category 2",
            "Area 2",
            listOf(Pair("Ingredient 2", "Ingredient 2")))
    val mockedRecipesList = listOf(recipe1, recipe2)

    // Setup the mock to trigger onSuccess
    `when`(mockRepository.random(anyInt(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(List<Recipe>) -> Unit>(1)
      onSuccess(mockedRecipesList)
      null
    }

    recipesViewModel = RecipesViewModel(mockRepository)
    `when`(mockNavigationActions.currentRoute()).thenReturn(Route.SWIPE)
    `when`(mockNavigationActions.navigateTo(Route.AUTH)).then {}
    composeTestRule.setContent {
      SwipePage(mockNavigationActions, recipesViewModel) // Set up the SignInScreen directly
    }
    Intents.init()
  }

  /** This method runs after the test execution. */
  @After
  fun tearDown() {
    Intents.release()
  }

  @Test
  fun testFetchRandomRecipes() {
    val recipe1 =
        Recipe(
            "Recipe 1",
            "",
            "url1",
            "Instructions 1",
            "Category 1",
            "Area 1",
            listOf(Pair("Ingredient 1", "Ingredient 1")))
    val recipe2 =
        Recipe(
            "Recipe 2",
            "",
            "url2",
            "Instructions 2",
            "Category 2",
            "Area 2",
            listOf(Pair("Ingredient 2", "Ingredient 2")))
    val mockedRecipesList = listOf(recipe1, recipe2)

    // Setup the mock to trigger onSuccess
    `when`(mockRepository.random(anyInt(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(List<Recipe>) -> Unit>(1)
      onSuccess(mockedRecipesList)
      null
    }

    // Init the recipesViewModel will call fetchRandomRecipes

    // Assert that _recipes contains the mocked recipes and _loading is false
    assertEquals(mockedRecipesList, recipesViewModel.recipes.value)
    assertFalse(recipesViewModel.loading.value)
  }

  /** This test checks if the BottomBar and the topBar of the swipe page are correctly displayed. */
  @Test
  fun swipePageCorrectlyDisplayed() {
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("topBar").assertIsDisplayed()
  }

  /**
   * This test checks if the RecipeDisplay composable displays the recipe information correctly :
   * the recipe image and the recipe description.
   */
  @Test
  fun recipeAndDescriptionAreCorrectlyDisplayed() {

    composeTestRule.onNodeWithTag("recipeName", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("recipeStar", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("recipeRate", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("recipeDescription", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("recipeDescription", useUnmergedTree = true).performClick()

    composeTestRule.onNodeWithTag("recipeImage", useUnmergedTree = true).assertIsDisplayed()
  }

  /** This test checks the Dislike swipe of the image. */
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testDraggingEventLeft() = runTest {
    val currentRecipe = recipesViewModel.currentRecipe.value
    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("draggableItem").assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("draggableItem").performTouchInput { swipeLeft() }

    // Runs all other coroutines on the scheduler until there is nothing left in the queue
    runCurrent()
    // need to wait for the animation to finish -> 3 seconds
    composeTestRule.waitUntil(3000L) {
      currentRecipe != null && recipesViewModel.currentRecipe.value != currentRecipe
    }
    composeTestRule.waitForIdle()
    assertNotEquals(currentRecipe, recipesViewModel.currentRecipe.value)
  }

  /** This test checks the Like swipe of the image. */
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testDraggingEventRight() = runTest {
    val currentRecipe = recipesViewModel.currentRecipe.value
    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("draggableItem").assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("draggableItem").performTouchInput { swipeRight() }

    // Runs all other coroutines on the scheduler until there is nothing left in the queue
    advanceUntilIdle()

    // need to wait for the animation to finish -> 3 seconds
    composeTestRule.waitUntil(3000L) {
      currentRecipe != null && recipesViewModel.currentRecipe.value != currentRecipe
    }

    composeTestRule.waitForIdle()

    assertNotEquals(currentRecipe, recipesViewModel.currentRecipe.value)
  }

  /** This test checks when the swipe is not enough. */
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testDraggingNotEnough() = runTest {
    val currentRecipe = recipesViewModel.currentRecipe.value
    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("draggableItem").assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("draggableItem").performTouchInput {
      swipeRight(0f, 1f)
      swipeLeft(0f, -1f)
    }
    // Runs all other coroutines on the scheduler until there is nothing left in the queue
    advanceUntilIdle()
    // need to wait for the animation to finish -> 3 seconds
    composeTestRule.waitUntil(3000L) { currentRecipe != null }
    composeTestRule.waitForIdle()

    assertEquals(currentRecipe, recipesViewModel.currentRecipe.value)
  }
}
