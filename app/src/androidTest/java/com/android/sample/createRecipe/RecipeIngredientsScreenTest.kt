package com.android.sample.createRecipe

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.sample.model.image.ImageDownload
import com.android.sample.model.ingredient.DefaultIngredientRepository
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.ui.createRecipe.RecipeIngredientsScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.utils.testIngredients
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class RecipeIngredientsScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Mock private lateinit var ingredientViewModel: IngredientViewModel

  @Mock private lateinit var aggregatorIngredientRepository: DefaultIngredientRepository
  @Mock private lateinit var mockNavigationActions: NavigationActions

  private val titleText = "No Ingredients"
  private val subtitleText = "List the ingredients needed for your recipe. Add as many as you need."
  private val buttonText = "Add Ingredients"
  private val mockIngredients = testIngredients

  @Before
  fun setup() {
    aggregatorIngredientRepository = mock(DefaultIngredientRepository::class.java)
    mockNavigationActions = mock(NavigationActions::class.java)
    `when`(mockNavigationActions.currentRoute()).thenReturn(Route.CREATE_RECIPE)
  }

  @Test
  fun testRecipeIngredientsScreenComponentsAreDisplayed() {
    ingredientViewModel = IngredientViewModel(aggregatorIngredientRepository, ImageDownload())

    composeTestRule.setContent {
      RecipeIngredientsScreen(
          navigationActions = mockNavigationActions,
          currentStep = 1,
          ingredientViewModel = ingredientViewModel)
    }

    composeTestRule.onNodeWithText(titleText).assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithText(subtitleText).assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithText(buttonText).assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("progressBar").assertExists().assertIsDisplayed()
  }

  @Test
  fun testAddIngredientsButtonNavigatesToNextScreen() {
    ingredientViewModel = IngredientViewModel(aggregatorIngredientRepository, ImageDownload())

    composeTestRule.setContent {
      RecipeIngredientsScreen(
          navigationActions = mockNavigationActions,
          currentStep = 1,
          ingredientViewModel = ingredientViewModel)
    }

    assertEquals(ingredientViewModel.ingredientList.value.size, 0)
    composeTestRule.onNodeWithText(buttonText).assertExists().performClick()
    assertEquals(ingredientViewModel.ingredientList.value.size, 0)
    verify(mockNavigationActions).navigateTo(Screen.CREATE_RECIPE_LIST_INGREDIENTS)
  }

  @Test
  fun testEmptyNotEmptyRecipe() {
    ingredientViewModel = IngredientViewModel(aggregatorIngredientRepository, ImageDownload())

    ingredientViewModel.addIngredient(mockIngredients[0])

    composeTestRule.setContent {
      RecipeIngredientsScreen(
          navigationActions = mockNavigationActions,
          currentStep = 1,
          ingredientViewModel = ingredientViewModel)
    }
    assertNotEquals(ingredientViewModel.ingredientList.value.size, 0)
    verify(mockNavigationActions).navigateTo(Screen.CREATE_RECIPE_LIST_INGREDIENTS)
  }
}
