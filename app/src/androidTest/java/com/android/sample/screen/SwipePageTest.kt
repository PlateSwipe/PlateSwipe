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
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class SwipePageTest : TestCase() {
  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockRepository: RecipeRepository
  private lateinit var recipesViewModel: RecipesViewModel

  @get:Rule val composeTestRule = createComposeRule()

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

    doAnswer { invocation ->
          val onSuccess =
              invocation.getArgument<(List<Recipe>) -> Unit>(1) // Get the onSuccess callback
          onSuccess(mockedRecipesList) // Call onSuccess with the mocked recipes list
          null // Return null as this method doesn't return anything
        }
        .whenever(mockRepository)
        .random(any(), any(), any())

    recipesViewModel = RecipesViewModel(mockRepository)
    `when`(mockNavigationActions.currentRoute()).thenReturn(Route.SWIPE)
    `when`(mockNavigationActions.navigateTo(Route.AUTH)).then {}
    `when`(mockRepository.random(anyOrNull(), anyOrNull(), anyOrNull())).then {}
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
  @Test
  fun testDraggingEventLeft() = runBlocking {
    val currentRecipe = recipesViewModel.currentRecipe.value
    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("draggableItem").assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("draggableItem").performTouchInput { swipeLeft() }
    composeTestRule.awaitIdle()

    assertNotEquals(currentRecipe, recipesViewModel.currentRecipe.value)
  }

  /** This test checks the Like swipe of the image. */
  @Test
  fun testDraggingEventRight() = runBlocking {
    val currentRecipe = recipesViewModel.currentRecipe.value
    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("draggableItem").assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("draggableItem").performTouchInput { swipeRight() }
    composeTestRule.awaitIdle()
    assertNotEquals(currentRecipe, recipesViewModel.currentRecipe.value)
  }

  /** This test checks when the swipe is not enough. */
  @Test
  fun testDraggingNotEnough() = runBlocking {
    val currentRecipe = recipesViewModel.currentRecipe.value
    // Make sure the screen is displayed
    composeTestRule.onNodeWithTag("draggableItem").assertIsDisplayed()

    // Simulate a drag event
    composeTestRule.onNodeWithTag("draggableItem").performTouchInput {
      swipeRight(0f, 1f)
      swipeLeft(0f, -1f)
    }
    composeTestRule.awaitIdle()
    assertEquals(currentRecipe, recipesViewModel.currentRecipe.value)
  }
}
