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
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.test.espresso.intent.Intents
import com.android.sample.model.filter.Difficulty
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipesRepository
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.recipeOverview.RecipeOverview
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
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

    private val recipe1 =
        Recipe(
            "Recipe 1",
            "Recipe 1",
            "url1",
            "Instructions 1",
            "Category 1",
            "Area 1",
            listOf(
                Pair("Ingredient 1", "Ingredient 1x"),
                Pair("Ingredient 2", "Ingredient 1x"),
                Pair("Ingredient 3", "Ingredient 2x")
            )
        )
    private val mockedRecipesList = listOf(recipe1)

    @get:Rule
    val composeTestRule = createComposeRule()

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

        composeTestRule.setContent {
            RecipeOverview(
                mockNavigationActions,
                recipesViewModel
            ) // Set up the SignInScreen directly
        }
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun screenDisplayedCorrectlyTest() = runTest {
        // Checking if the main components of the screen are displayed
        composeTestRule.onNodeWithTag("topBar", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule
            .onNodeWithTag("bottomNavigationMenu", useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("draggableItem", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun testIngredientAndInstructionSwitch() {
        composeTestRule
            .onNodeWithTag("draggableItem", useUnmergedTree = true)
            .performScrollToNode(hasTestTag("ingredientsView"))
        composeTestRule.waitForIdle()
        // Initial state should show ingredients view
        composeTestRule.onNodeWithTag("ingredientsView", useUnmergedTree = true).assertIsDisplayed()

        // Switch to instructions view and check if instructions are displayed
        composeTestRule.onNodeWithTag("instructionsButton", useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithTag("instructionsView", useUnmergedTree = true)
            .assertIsDisplayed()
    }

    @Test
    fun testServingsCountButtons() {
        // Check initial servings count
        composeTestRule
            .onNodeWithTag("draggableItem", useUnmergedTree = true)
            .performScrollToNode(hasTestTag("numberServings"))

        composeTestRule.onNodeWithTag("numberServings", useUnmergedTree = true)
            .assertTextEquals("1")

        // Click the add button and verify if the count increases
        composeTestRule.onNodeWithTag("addServings", useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithTag("numberServings", useUnmergedTree = true)
            .assertTextEquals("2")

        // Click the remove button and verify if the count decreases
        composeTestRule.onNodeWithTag("removeServings", useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithTag("numberServings", useUnmergedTree = true)
            .assertTextEquals("1")
    }

    @Test
    fun testIngredientsListCheckboxes() {
        composeTestRule
            .onNodeWithTag("draggableItem", useUnmergedTree = true)
            .performScrollToNode(hasTestTag("ingredientsView"))
        // Check if ingredient list is displayed with checkboxes
        composeTestRule.onNodeWithTag("ingredientsView", useUnmergedTree = true).assertIsDisplayed()

        // Scroll to the third checkbox to ensure it's visible before performing actions
        composeTestRule.onAllNodesWithTag(
            "checkboxIngredient",
            useUnmergedTree = true
        )[2].performScrollTo()

        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithTag("checkboxIngredient", useUnmergedTree = true).apply {
            assertCountEquals(3)
            get(0).assertIsOff()
            get(1).assertIsOff()
            get(2).assertIsOff()
        }

        // Check if each ingredient has a checkbox and can be checked
        composeTestRule.onAllNodesWithTag("checkboxIngredient", useUnmergedTree = true).apply {
            assertCountEquals(3) // Assuming 3 ingredients for this test
            get(0).performClick()
            get(1).performClick()
            get(2).performClick()
        }
        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithTag("checkboxIngredient", useUnmergedTree = true).apply {
            assertCountEquals(3) // Assuming 3 ingredients for this test
            get(0).assertIsOn()
            get(1).assertIsOn()
            get(2).assertIsOn()
        }
    }

    @Test
    fun testPrepareCookTotalTimeDisplay() {
        // Check if each time property is displayed correctly
        composeTestRule
            .onNodeWithTag("draggableItem", useUnmergedTree = true)
            .performScrollToNode(hasTestTag("prepTimeText"))
        composeTestRule.onNodeWithTag("prepTimeText", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("cookTimeText", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("totalTimeText", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun testRecipeImageDisplayed() {
        // Check if the recipe image is displayed
        composeTestRule.onNodeWithTag("recipeImage", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun testRecipeTitleDisplayed() {
        // Check if the recipe title is displayed
        composeTestRule.onNodeWithTag("recipeTitle", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun testRatingComponentsDisplayed() {
        // Check if the rating star and rate text are displayed
        composeTestRule.onNodeWithTag("recipeStar", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("recipeRate", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun testIngredientQuantityIncreasedOnServingsChange() {
        // Initially check for ingredient quantity with 1 serving

        composeTestRule
            .onNodeWithTag("draggableItem", useUnmergedTree = true)
            .performScrollToNode(hasTestTag("ingredientButton"))
        composeTestRule.waitForIdle()

        // Check if ingredient list is displayed with checkboxes
        composeTestRule.onNodeWithTag("ingredientButton", useUnmergedTree = true)
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithTag("checkboxIngredient", useUnmergedTree = true)
            .assertCountEquals(3)

        composeTestRule
            .onNodeWithTag("draggableItem", useUnmergedTree = true)
            .performScrollToNode(hasTestTag("ingredientIngredient 1"))
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag("ingredientIngredient 1", useUnmergedTree = true)
            .assertTextContains("Ingredient 1: Ingredient 1x")

        // Increase servings and verify ingredient quantities are updated
        composeTestRule.onNodeWithTag("addServings", useUnmergedTree = true).performClick()

        composeTestRule
            .onNodeWithTag("ingredientIngredient 1", useUnmergedTree = true)
            .assertTextContains(
                "Ingredient 1: Ingredient 2x"
            ) // Assuming the ingredient quantity doubles
    }
}
