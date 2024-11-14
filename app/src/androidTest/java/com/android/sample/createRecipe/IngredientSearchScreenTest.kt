package com.android.sample.createRecipe

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.sample.model.ingredient.AggregatorIngredientRepository
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.resources.C.TestTag.IngredientSearchScreen.CANCEL_BUTTON
import com.android.sample.resources.C.TestTag.IngredientSearchScreen.CONFIRMATION_BUTTON
import com.android.sample.resources.C.TestTag.IngredientSearchScreen.CONFIRMATION_POPUP
import com.android.sample.resources.C.TestTag.IngredientSearchScreen.SCANNER_ICON
import com.android.sample.resources.C.TestTag.Utils.SEARCH_BAR
import com.android.sample.ui.createRecipe.IngredientSearchScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class IngredientSearchScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  // Mocking the ViewModel and NavigationActions
  @Mock private lateinit var ingredientViewModel: IngredientViewModel

  @Mock private lateinit var mockNavigationActions: NavigationActions

  @Mock private lateinit var aggregatorIngredientRepository: AggregatorIngredientRepository

  // Setting up some test data
  private val testIngredients =
      listOf(
          Ingredient("1", 1234L, "Ingredient 1", "Description", "2 cups", emptyList(), emptyList()),
          Ingredient("2", 5678L, "Ingredient 2", "Description", "1 tbsp", emptyList(), emptyList()))

  @Before
  fun setup() {
    // Mocking the ViewModel's flow

    mockNavigationActions = mock(NavigationActions::class.java)
    `when`(mockNavigationActions.currentRoute()).thenReturn(Route.CREATE_RECIPE)
    aggregatorIngredientRepository = mock(AggregatorIngredientRepository::class.java)

    `when`(aggregatorIngredientRepository.search(any(), any(), any(), any())).thenAnswer {
        invocation ->
      val onSuccess = invocation.getArgument<(List<Ingredient>) -> Unit>(1)
      onSuccess(testIngredients)
      null
    }
    ingredientViewModel = IngredientViewModel(aggregatorIngredientRepository)

    composeTestRule.setContent {
      IngredientSearchScreen(
          navigationActions = mockNavigationActions, ingredientViewModel = ingredientViewModel)
    }
  }

  @Test
  fun testSearchBarTriggersFetchIngredientByName() {
    // Interacting with the SearchBar to enter a query
    composeTestRule.onNodeWithTag(SEARCH_BAR, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(SEARCH_BAR, useUnmergedTree = true).performClick()
  }

  @Test
  fun testScannerIconNavigatesToCameraScreen() {
    // Click on the scanner icon
    composeTestRule.onNodeWithTag(SEARCH_BAR, useUnmergedTree = true).assertIsDisplayed()

    composeTestRule.onNodeWithTag(SCANNER_ICON, useUnmergedTree = true).performClick()

    // Verify that the navigation action was triggered
    verify(mockNavigationActions).navigateTo(Screen.CAMERA_SCAN_CODE_BAR)
  }

  @Test
  fun testSearchBarDisplaysIngredients() = runTest {
    composeTestRule.onNodeWithTag(SEARCH_BAR, useUnmergedTree = true).performTextInput("To")

    composeTestRule.waitUntil(5000) {
      ingredientViewModel.searchingIngredientList.value.isNotEmpty()
    }
    composeTestRule.waitForIdle()
    testIngredients.forEach { ingredient ->
      composeTestRule
          .onNodeWithTag("ingredientItem${ingredient.name}", useUnmergedTree = true)
          .assertIsDisplayed()
    }
  }

  @Test
  fun testSearchPopUpDisplayCorrectly() {
    composeTestRule.onNodeWithTag(SEARCH_BAR, useUnmergedTree = true).performTextInput("To")

    composeTestRule.waitUntil(5000) {
      ingredientViewModel.searchingIngredientList.value.isNotEmpty()
    }

    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag("ingredientItem${testIngredients[0].name}", useUnmergedTree = true)
        .performClick()

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(CONFIRMATION_POPUP, useUnmergedTree = true).assertIsDisplayed()

    composeTestRule.onNodeWithTag(CONFIRMATION_BUTTON, useUnmergedTree = true).performClick()
    verify(mockNavigationActions).navigateTo(Screen.CREATE_RECIPE_LIST_INGREDIENTS)
    assertEquals(ingredientViewModel.ingredientList.value, listOf(testIngredients[0]))
  }

  @Test
  fun testSearchPopUpCancelCorrectly() {
    composeTestRule.onNodeWithTag(SEARCH_BAR, useUnmergedTree = true).performTextInput("To")

    composeTestRule.waitUntil(5000) {
      ingredientViewModel.searchingIngredientList.value.isNotEmpty()
    }

    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag("ingredientItem${testIngredients[0].name}", useUnmergedTree = true)
        .performClick()

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(CONFIRMATION_POPUP, useUnmergedTree = true).assertIsDisplayed()

    composeTestRule.onNodeWithTag(CANCEL_BUTTON, useUnmergedTree = true).performClick()

    composeTestRule.waitForIdle()

    verify(mockNavigationActions, never()).navigateTo(Screen.CREATE_RECIPE_LIST_INGREDIENTS)
    assertNotEquals(ingredientViewModel.ingredientList.value, listOf(testIngredients[0]))
  }
}
