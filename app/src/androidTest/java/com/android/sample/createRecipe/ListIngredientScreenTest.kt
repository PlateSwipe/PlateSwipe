package com.android.sample.createRecipe

import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.android.sample.model.image.ImageDownload
import com.android.sample.model.ingredient.DefaultIngredientRepository
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

  @Mock private lateinit var aggregatorIngredientRepository: DefaultIngredientRepository

  // Sample ingredients list for testing
  private val mockedIngredients = testIngredients.filter { it.quantity != null }

  @Before
  fun setup() {
    // Initialize mocked navigation actions and view models
    mockNavigationActions = mock(NavigationActions::class.java)

    `when`(mockNavigationActions.currentRoute()).thenReturn(Route.CREATE_RECIPE)
    aggregatorIngredientRepository = mock(DefaultIngredientRepository::class.java)

    `when`(aggregatorIngredientRepository.search(any(), any(), any(), any())).thenAnswer {
        invocation ->
      val onSuccess = invocation.getArgument<(List<Ingredient>) -> Unit>(1)
      onSuccess(mockedIngredients)
      null
    }
    ingredientViewModel = IngredientViewModel(aggregatorIngredientRepository, ImageDownload())
    createRecipeViewModel = CreateRecipeViewModel.Factory.create(CreateRecipeViewModel::class.java)
    createRecipeViewModel.recipeBuilder.setName("Test Recipe")

    for (ingredient in mockedIngredients) {
      ingredientViewModel.addIngredient(ingredient)
    }

    // Setting up the composable content
    composeTestRule.setContent {
      IngredientListScreen(
          navigationActions = mockNavigationActions,
          searchIngredientViewModel = ingredientViewModel,
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
      Log.e("ingredient", ingredient.name)
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

  @Test
  fun testNextStepButtonAddsIngredientsToRecipeNotPossible() {
    composeTestRule
        .onNodeWithTag("recipeNameTextField${mockedIngredients[0].name}", useUnmergedTree = true)
        .performTextClearance()
    // Click the Next Step button
    composeTestRule.onNodeWithTag(NEXT_STEP_BUTTON, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(NEXT_STEP_BUTTON, useUnmergedTree = true).performClick()

    // Verify each ingredient is added to the recipe
    composeTestRule.onNodeWithText("All ingredients must have a quantity").assertIsDisplayed()
  }

  @Test
  fun testIngredientRemoveFunctionality() {
    // Simulate clicking the remove icon for the first ingredient
    composeTestRule
        .onNodeWithTag("removeIngredientIcon${mockedIngredients[0].name}", useUnmergedTree = true)
        .performClick()

    // Verify that the ingredient was removed from the view model
    assertEquals(
        mockedIngredients.drop(1).map { ingredient -> Pair(ingredient, ingredient.quantity) },
        ingredientViewModel.ingredientList.value)
  }

  @Test
  fun testnextStepWithoutAnyIngredient() {
    // Simulate clicking the remove icon for the first ingredient
    ingredientViewModel.ingredientList.value.forEach {
      ingredientViewModel.removeIngredient(it.first)
    }
    composeTestRule
        .onNodeWithText("Next Step", useUnmergedTree = true)
        .assertIsDisplayed()
        .performClick()

    // Verify that the ingredient was removed from the view model
    composeTestRule.onNodeWithText("Please add at least one ingredient").assertIsDisplayed()
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
    assertEquals(updatedQuantity, ingredientViewModel.ingredientList.value[0].second)
  }

  @Test
  fun testIngredientQuantityUpdateInTextFieldAndNavigates() {
    val updatedQuantity = "3 cups" // New quantity to simulate user input

    ingredientViewModel.ingredientList.value.forEachIndexed { i, (ingredient, _) ->
      // Given the initial ingredient with a quantity
      val initialQuantity = ingredient.quantity

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

      assertEquals(updatedQuantity, ingredientViewModel.ingredientList.value[i].second)
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(NEXT_STEP_BUTTON, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(NEXT_STEP_BUTTON, useUnmergedTree = true).performClick()

    verify(mockNavigationActions).navigateTo(Screen.CREATE_RECIPE_ADD_INSTRUCTION)
  }
}
