package com.android.sample.createRecipe

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.android.sample.model.ingredient.AggregatorIngredientRepository
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.resources.C.TestTag.IngredientListScreen.ADD_INGREDIENT_ICON
import com.android.sample.resources.C.TestTag.IngredientListScreen.NEXT_STEP_BUTTON
import com.android.sample.resources.C.TestTag.IngredientListScreen.RECIPE_NAME
import com.android.sample.ui.createRecipe.IngredientListScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.utils.testIngredients
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

class ListIngredientScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Mock private lateinit var ingredientViewModel: IngredientViewModel

  @Mock private lateinit var createRecipeViewModel: CreateRecipeViewModel

  @Mock private lateinit var mockNavigationActions: NavigationActions

  @Mock private lateinit var aggregatorIngredientRepository: AggregatorIngredientRepository

  // Sample ingredients list for testing
  private val mockedIngredients = testIngredients

  private val ingredientPairs: List<Pair<String, String?>> =
      mockedIngredients.map { it.name to it.quantity }

  @Before
  fun setup() {
    // Initialize mocked navigation actions and view models
    mockNavigationActions = mock(NavigationActions::class.java)

    `when`(mockNavigationActions.currentRoute()).thenReturn(Route.CREATE_RECIPE)
    aggregatorIngredientRepository = mock(AggregatorIngredientRepository::class.java)

    `when`(aggregatorIngredientRepository.search(any(), any(), any(), any())).thenAnswer {
        invocation ->
      val onSuccess = invocation.getArgument<(List<Ingredient>) -> Unit>(1)
      onSuccess(mockedIngredients)
      null
    }
    ingredientViewModel = IngredientViewModel(aggregatorIngredientRepository)
    createRecipeViewModel = CreateRecipeViewModel.Factory.create(CreateRecipeViewModel::class.java)
    createRecipeViewModel.recipeBuilder.setName("Test Recipe")

    for (ingredient in mockedIngredients) {
      ingredientViewModel.addIngredient(ingredient)
    }

    // Setting up the composable content
    composeTestRule.setContent {
      IngredientListScreen(
          navigationActions = mockNavigationActions,
          ingredientViewModel = ingredientViewModel,
          createRecipeViewModel = createRecipeViewModel)
    }
  }

  @Test
  fun testTextAreDisplayed() {
    composeTestRule.onNodeWithTag(RECIPE_NAME, useUnmergedTree = true).assertIsDisplayed()

    composeTestRule.onNodeWithText("Ingredients list", useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun testIngredientListDisplaysIngredients() {
    // Check that each ingredient is displayed
    mockedIngredients.forEach { ingredient ->
      composeTestRule.onNodeWithText(ingredient.name, useUnmergedTree = true).assertIsDisplayed()
    }
  }

  @Test
  fun testAddButtonNavigatesToSearchScreen() {
    // Click the add button to navigate to ingredient search screen
    composeTestRule.onNodeWithTag(ADD_INGREDIENT_ICON, useUnmergedTree = true).assertIsDisplayed()

    composeTestRule.onNodeWithTag(ADD_INGREDIENT_ICON, useUnmergedTree = true).performClick()

    // Verify navigation action to ingredient search screen
    verify(mockNavigationActions).navigateTo(Screen.CREATE_RECIPE_SEARCH_INGREDIENTS)
  }

  private fun compareIngredientPairs(
      list1: List<Pair<String, String?>>,
      list2: List<Pair<String, String?>>
  ): Boolean {
    // Check if both lists have the same size
    if (list1.size != list2.size) return false

    // Sort both lists to ensure the order doesn’t affect comparison
    val sortedList1 = list1.sortedBy { it.first.lowercase() }
    val sortedList2 = list2.sortedBy { it.first.lowercase() }

    // Compare each pair in both sorted lists
    return sortedList1 == sortedList2
  }

  @Test
  fun testNextStepButtonAddsIngredientsToRecipeAndNavigates() {
    // Click the Next Step button
    composeTestRule.onNodeWithTag(NEXT_STEP_BUTTON, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(NEXT_STEP_BUTTON, useUnmergedTree = true).performClick()

    // Verify each ingredient is added to the recipe
    assertTrue(
        compareIngredientPairs(
            ingredientPairs, createRecipeViewModel.recipeBuilder.getIngredientsAndMeasurements()))

    // Verify navigation to the instruction screen
    verify(mockNavigationActions).navigateTo(Screen.CREATE_RECIPE_ADD_INSTRUCTION)
  }

  @Test
  fun testIngredientRemoveFunctionality() {
    // Simulate clicking the remove icon for the first ingredient
    composeTestRule
        .onNodeWithTag("removeIngredientIcon${mockedIngredients[0].name}", useUnmergedTree = true)
        .performClick()

    // Verify that the ingredient was removed from the view model
    assertEquals(ingredientViewModel.ingredientList.value, listOf(mockedIngredients[1]))
  }

  @Test
  fun testIngredientQuantityUpdateInTextField() {
    // Given the initial ingredient with a quantity
    val ingredient = mockedIngredients[0]
    val initialQuantity = ingredient.quantity
    val updatedQuantity = "3 cups" // New quantity to simulate user input

    // Verify that the initial quantity is displayed in the text field
    composeTestRule
        .onNodeWithTag("recipeNameTextField${ingredient.name}", useUnmergedTree = true)
        .assertTextEquals(initialQuantity!!)

    composeTestRule
        .onNodeWithTag("recipeNameTextField${ingredient.name}", useUnmergedTree = true)
        .performTextClearance()
    // Simulate typing the updated quantity into the OutlinedTextField
    composeTestRule
        .onNodeWithTag("recipeNameTextField${ingredient.name}", useUnmergedTree = true)
        .performTextInput(updatedQuantity)

    // Verify that the updated quantity is now displayed in the text field
    composeTestRule
        .onNodeWithTag("recipeNameTextField${ingredient.name}", useUnmergedTree = true)
        .assertTextEquals(updatedQuantity)

    // Verify that the updateQuantity method was called with the correct parameters
    assertEquals(updatedQuantity, ingredientViewModel.ingredientList.value[0].quantity)
  }
}
