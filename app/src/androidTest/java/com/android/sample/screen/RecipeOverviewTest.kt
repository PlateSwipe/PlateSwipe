package com.android.sample.screen

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import com.android.sample.model.filter.Difficulty
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipesRepository
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.resources.C.TestTag.RecipeOverview.ADD_SERVINGS
import com.android.sample.resources.C.TestTag.RecipeOverview.COOK_TIME_TEXT
import com.android.sample.resources.C.TestTag.RecipeOverview.DRAGGABLE_ITEM
import com.android.sample.resources.C.TestTag.RecipeOverview.INGREDIENTS_VIEW
import com.android.sample.resources.C.TestTag.RecipeOverview.INGREDIENT_CHECKBOX
import com.android.sample.resources.C.TestTag.RecipeOverview.INSTRUCTIONS_VIEW
import com.android.sample.resources.C.TestTag.RecipeOverview.NUMBER_SERVINGS
import com.android.sample.resources.C.TestTag.RecipeOverview.PREP_TIME_TEXT
import com.android.sample.resources.C.TestTag.RecipeOverview.RECIPE_IMAGE
import com.android.sample.resources.C.TestTag.RecipeOverview.RECIPE_RATE
import com.android.sample.resources.C.TestTag.RecipeOverview.RECIPE_STAR
import com.android.sample.resources.C.TestTag.RecipeOverview.RECIPE_TITLE
import com.android.sample.resources.C.TestTag.RecipeOverview.REMOVE_SERVINGS
import com.android.sample.resources.C.TestTag.RecipeOverview.SLIDING_BUTTON_INGREDIENTS
import com.android.sample.resources.C.TestTag.RecipeOverview.SLIDING_BUTTON_INSTRUCTIONS
import com.android.sample.resources.C.TestTag.RecipeOverview.TOTAL_TIME_TEXT
import com.android.sample.resources.C.TestTag.Utils.BOTTOM_BAR
import com.android.sample.resources.C.TestTag.Utils.TOP_BAR
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.recipeOverview.RecipeOverview
import com.android.sample.ui.utils.testRecipes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

class RecipeOverviewTest {
  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockRepository: RecipesRepository
  private lateinit var recipesViewModel: RecipesViewModel

  private val mockedRecipesList =
      listOf(
          testRecipes[0].copy(
              ingredientsAndMeasurements =
                  listOf(
                      Pair("Ingredient 1", "Ingredient 1x"),
                      Pair("Ingredient 2", "Ingredient 2x"),
                      Pair("Ingredient 3", "Ingredient 3x"))),
          testRecipes[1],
      )

  @get:Rule val composeTestRule = createComposeRule()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() = runTest {
    mockNavigationActions = mock(NavigationActions::class.java)
    mockRepository = mock(RecipesRepository::class.java)

    // Setup the mock to trigger onSuccess
    `when`(mockRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(List<Recipe>) -> Unit>(1)
      onSuccess(mockedRecipesList)
      null
    }
    `when`(mockRepository.searchByCategory(any(), any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(List<Recipe>) -> Unit>(1)
      onSuccess(mockedRecipesList)
      null
    }

    recipesViewModel = RecipesViewModel(mockRepository)
    advanceUntilIdle()

    `when`(mockNavigationActions.currentRoute()).thenReturn(Route.SWIPE)
    `when`(mockNavigationActions.navigateTo(Route.AUTH)).then {}

    // Init the filter
    recipesViewModel.updateTimeRange(0f, 100f)
    recipesViewModel.updateTimeRange(5f, 99f)
    recipesViewModel.updatePriceRange(0f, 100f)
    recipesViewModel.updatePriceRange(5f, 52f)
    recipesViewModel.updateDifficulty(Difficulty.Easy)
    recipesViewModel.updateCategory("Dessert")
  }

  @Test
  fun screenDisplayedCorrectlyTest() = runTest {
    composeTestRule.setContent {
      RecipeOverview(mockNavigationActions, recipesViewModel) // Set up the SignInScreen directly
    }
    // Checking if the main components of the screen are displayed
    composeTestRule.onNodeWithTag(TOP_BAR, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(BOTTOM_BAR, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(DRAGGABLE_ITEM, useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun screenDisplayedCorrectlyIfNullRecipe() = runTest {
    `when`(mockRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(List<Recipe>) -> Unit>(1)
      onSuccess(listOf())
      null
    }
    recipesViewModel = RecipesViewModel(mockRepository)

    composeTestRule.setContent {
      RecipeOverview(mockNavigationActions, recipesViewModel) // Set up the SignInScreen directly
    }

    composeTestRule
        .onNodeWithText("Unable to load recipe", useUnmergedTree = true)
        .performScrollTo()
        .assertIsDisplayed()
  }

  @Test
  fun testIngredientAndInstructionSwitch() {
    composeTestRule.setContent {
      RecipeOverview(mockNavigationActions, recipesViewModel) // Set up the SignInScreen directly
    }
    composeTestRule
        .onNodeWithTag(DRAGGABLE_ITEM, useUnmergedTree = true)
        .performScrollToNode(hasTestTag(INGREDIENTS_VIEW))
    composeTestRule.waitForIdle()
    // Initial state should show ingredients view
    composeTestRule.onNodeWithTag(INGREDIENTS_VIEW, useUnmergedTree = true).assertIsDisplayed()

    // Switch to instructions view and check if instructions are displayed
    composeTestRule
        .onNodeWithTag(SLIDING_BUTTON_INSTRUCTIONS, useUnmergedTree = true)
        .performClick()
    // TODO enable this
    // composeTestRule.onNodeWithTag(INSTRUCTIONS_VIEW, useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun testServingsCountButtons() {
    composeTestRule.setContent {
      RecipeOverview(mockNavigationActions, recipesViewModel) // Set up the SignInScreen directly
    }
    // Check initial servings count
    composeTestRule
        .onNodeWithTag(DRAGGABLE_ITEM, useUnmergedTree = true)
        .performScrollToNode(hasTestTag(NUMBER_SERVINGS))

    composeTestRule.onNodeWithTag(NUMBER_SERVINGS, useUnmergedTree = true).assertTextEquals("1")

    // Click the add button and verify if the count increases
    composeTestRule.onNodeWithTag(ADD_SERVINGS, useUnmergedTree = true).performClick()
    composeTestRule.onNodeWithTag(NUMBER_SERVINGS, useUnmergedTree = true).assertTextEquals("2")

    // Click the remove button and verify if the count decreases
    composeTestRule.onNodeWithTag(REMOVE_SERVINGS, useUnmergedTree = true).performClick()
    composeTestRule.onNodeWithTag(NUMBER_SERVINGS, useUnmergedTree = true).assertTextEquals("1")
  }

  @Test
  fun testIngredientsListCheckboxes() {
    composeTestRule.setContent {
      RecipeOverview(mockNavigationActions, recipesViewModel) // Set up the SignInScreen directly
    }

    composeTestRule
        .onNodeWithTag(DRAGGABLE_ITEM, useUnmergedTree = true)
        .performScrollToNode(hasTestTag(INGREDIENTS_VIEW))
    // Check if ingredient list is displayed with checkboxes
    composeTestRule.onNodeWithTag(INGREDIENTS_VIEW, useUnmergedTree = true).assertIsDisplayed()

    // Scroll to the third checkbox to ensure it's visible before performing actions
    composeTestRule
        .onAllNodesWithTag(INGREDIENT_CHECKBOX, useUnmergedTree = true)[2]
        .performScrollTo()

    composeTestRule.waitForIdle()

    composeTestRule.onAllNodesWithTag(INGREDIENT_CHECKBOX, useUnmergedTree = true).apply {
      assertCountEquals(3)
      get(0).assertIsOff()
      get(1).assertIsOff()
      get(2).assertIsOff()
    }

    // Check if each ingredient has a checkbox and can be checked
    composeTestRule.onAllNodesWithTag(INGREDIENT_CHECKBOX, useUnmergedTree = true).apply {
      assertCountEquals(3) // Assuming 3 ingredients for this test
      get(0).performClick()
      get(1).performClick()
      get(2).performClick()
    }
    composeTestRule.waitForIdle()

    composeTestRule.onAllNodesWithTag(INGREDIENT_CHECKBOX, useUnmergedTree = true).apply {
      assertCountEquals(3) // Assuming 3 ingredients for this test
      get(0).assertIsOn()
      get(1).assertIsOn()
      get(2).assertIsOn()
    }
  }

  @Test
  fun testPrepareCookTotalTimeDisplay() {
    composeTestRule.setContent {
      RecipeOverview(mockNavigationActions, recipesViewModel) // Set up the SignInScreen directly
    }
    // Check if each time property is displayed correctly
    composeTestRule
        .onNodeWithTag(DRAGGABLE_ITEM, useUnmergedTree = true)
        .performScrollToNode(hasTestTag(PREP_TIME_TEXT))
    composeTestRule.onNodeWithTag(PREP_TIME_TEXT, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(COOK_TIME_TEXT, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TOTAL_TIME_TEXT, useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun testRecipeImageDisplayed() {
    composeTestRule.setContent {
      RecipeOverview(mockNavigationActions, recipesViewModel) // Set up the SignInScreen directly
    }
    // Check if the recipe image is displayed
    composeTestRule.onNodeWithTag(RECIPE_IMAGE, useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun testRecipeTitleDisplayed() {
    composeTestRule.setContent {
      RecipeOverview(mockNavigationActions, recipesViewModel) // Set up the SignInScreen directly
    }
    // Check if the recipe title is displayed
    composeTestRule.onNodeWithTag(RECIPE_TITLE, useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun testRatingComponentsDisplayed() {
    composeTestRule.setContent {
      RecipeOverview(mockNavigationActions, recipesViewModel) // Set up the SignInScreen directly
    }
    // Check if the rating star and rate text are displayed
    composeTestRule.onNodeWithTag(RECIPE_STAR, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(RECIPE_RATE, useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun testIngredientQuantityIncreasedOnServingsChange() {
    composeTestRule.setContent {
      RecipeOverview(mockNavigationActions, recipesViewModel) // Set up the SignInScreen directly
    }
    // Initially check for ingredient quantity with 1 serving

    composeTestRule
        .onNodeWithTag(DRAGGABLE_ITEM, useUnmergedTree = true)
        .performScrollToNode(hasTestTag(SLIDING_BUTTON_INGREDIENTS))
    composeTestRule.waitForIdle()

    // Check if ingredient list is displayed with checkboxes
    composeTestRule
        .onNodeWithTag(SLIDING_BUTTON_INGREDIENTS, useUnmergedTree = true)
        .assertIsDisplayed()

    composeTestRule
        .onAllNodesWithTag(INGREDIENT_CHECKBOX, useUnmergedTree = true)
        .assertCountEquals(3)

    composeTestRule
        .onNodeWithTag(DRAGGABLE_ITEM, useUnmergedTree = true)
        .performScrollToNode(hasTestTag("ingredientIngredient 1"))
    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag("ingredientIngredient 1", useUnmergedTree = true)
        .assertTextContains("Ingredient 1: Ingredient 1x")

    // Increase servings and verify ingredient quantities are updated
    composeTestRule.onNodeWithTag(ADD_SERVINGS, useUnmergedTree = true).performClick()

    composeTestRule
        .onNodeWithTag("ingredientIngredient 1", useUnmergedTree = true)
        .assertTextContains(
            "Ingredient 1: Ingredient 2x") // Assuming the ingredient quantity doubles
  }

  @Test
  fun testInstructionsAreDisplayed() {
    composeTestRule.setContent {
      RecipeOverview(mockNavigationActions, recipesViewModel) // Set up the SignInScreen directly
    }
    composeTestRule
        .onNodeWithTag(DRAGGABLE_ITEM, useUnmergedTree = true)
        .performScrollToNode(hasTestTag(INGREDIENTS_VIEW))
    composeTestRule.waitForIdle()
    // Initial state should show ingredients view
    composeTestRule.onNodeWithTag(INGREDIENTS_VIEW, useUnmergedTree = true).assertIsDisplayed()

    // Switch to instructions view and check if instructions are displayed
    composeTestRule
        .onNodeWithTag(SLIDING_BUTTON_INSTRUCTIONS, useUnmergedTree = true)
        .performClick()
    composeTestRule.onNodeWithTag(INSTRUCTIONS_VIEW, useUnmergedTree = true).assertIsDisplayed()

    val displayedinstructionsLength = 1 // adapted to small phone
    val allInstructionsLength = testRecipes[0].instructions.size
    composeTestRule
        .onAllNodesWithTag("InstructionTitle", useUnmergedTree = true)
        .assertCountEquals(allInstructionsLength)
        .apply {
          for (i in 0 until displayedinstructionsLength) {
            get(i).assertIsDisplayed()
          }
        }

    composeTestRule
        .onAllNodesWithTag("InstructionIcon", useUnmergedTree = true)
        .assertCountEquals(allInstructionsLength)
        .apply {
          for (i in 0 until displayedinstructionsLength) {
            get(i).assertIsDisplayed()
          }
        }
    composeTestRule
        .onAllNodesWithTag("InstructionInfo", useUnmergedTree = true)
        .assertCountEquals(allInstructionsLength)
        .apply {
          for (i in 0 until displayedinstructionsLength) {
            get(i).assertIsDisplayed()
          }
        }
    composeTestRule
        .onAllNodesWithTag("InstructionTime", useUnmergedTree = true)
        .assertCountEquals(allInstructionsLength)
        .apply {
          for (i in 0 until displayedinstructionsLength) {
            get(i).assertIsDisplayed()
          }
        }
    composeTestRule
        .onAllNodesWithTag("InstructionValue", useUnmergedTree = true)
        .assertCountEquals(allInstructionsLength)
        .apply {
          for (i in 0 until displayedinstructionsLength) {
            get(i).assertIsDisplayed()
          }
        }
  }
}
