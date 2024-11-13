package com.android.sample.utils

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.model.recipe.Recipe
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.utils.RecipeList
import com.android.sample.ui.utils.TopCornerLikeButton
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class RecipeListTest {
  private lateinit var mockNavigationActions: NavigationActions

  private val recipesList: List<Recipe> =
      listOf(
          Recipe(
              "0",
              "Meal1",
              "Meal1cat",
              "Meal1Area",
              "Meals 1 instructions",
              "https://img.jakpost.net/c/2016/09/29/2016_09_29_12990_1475116504._large.jpg",
              listOf(Pair("1", "peu"), Pair("2", "beaucoup"), Pair("3", "peu")),
          ),
          Recipe(
              "1",
              "Meal2",
              "Meal2cat",
              "Meal2Area",
              "Meals 2 instructions",
              "https://img.jakpost.net/c/2016/09/29/2016_09_29_12990_1475116504._large.jpg",
              listOf(Pair("1", "peu"), Pair("2", "beaucoup"), Pair("3", "peu")),
          ),
          Recipe(
              "2",
              "Meal3",
              "Meal3cat",
              "Meal3Area",
              "Meals 3 instructions",
              "https://img.jakpost.net/c/2016/09/29/2016_09_29_12990_1475116504._large.jpg",
              listOf(Pair("1", "peu"), Pair("2", "beaucoup"), Pair("3", "peu")),
          ),
          Recipe(
              "3",
              "Meal4",
              "Meal4cat",
              "Meal4Area",
              "Meals 4 instructions",
              "https://img.jakpost.net/c/2016/09/29/2016_09_29_12990_1475116504._large.jpg",
              listOf(Pair("1", "peu"), Pair("2", "beaucoup"), Pair("3", "peu")),
          ),
          Recipe(
              "4",
              "Meal5",
              "Meal5cat",
              "Meal5Area",
              "Meals 5 instructions",
              "https://img.jakpost.net/c/2016/09/29/2016_09_29_12990_1475116504._large.jpg",
              listOf(Pair("1", "peu"), Pair("2", "beaucoup"), Pair("3", "peu")),
          ))

  private val testRecipe =
      Recipe(
          idMeal = "12345",
          strMeal = "Test Recipe",
          strCategory = "Test Category",
          strArea = "Test Area",
          strInstructions = "Test Instructions",
          strMealThumbUrl = "https://example.com/image.jpg",
          ingredientsAndMeasurements = listOf(Pair("1", "Test Ingredient")),
      )

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    mockNavigationActions = mock(NavigationActions::class.java)

    `when`(mockNavigationActions.currentRoute()).thenReturn(Screen.ACCOUNT)
  }

  @Test
  fun testCorrectlyShowsList() {
    composeTestRule.setContent {
      RecipeList(
          modifier = Modifier.fillMaxSize(),
          list = recipesList,
          onRecipeSelected = {},
          topCornerButton = { recipe -> TopCornerLikeButton(recipe = recipe) })
    }

    for (recipe in recipesList) {
      composeTestRule
          .onNodeWithTag("recipeCard${recipe.idMeal}", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("recipeTitle${recipe.idMeal}", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("recipeImage${recipe.idMeal}", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("recipeFavoriteIcon${recipe.idMeal}", useUnmergedTree = true)
          .assertIsDisplayed()
    }
  }

  @Test
  fun testOnRecipeSelectedIsCalledOnSelection() {
    var selected = false

    val onRecipeSelected: (Recipe) -> Unit = { recipe ->
      assert(recipe == testRecipe)
      selected = true
    }

    composeTestRule.setContent {
      RecipeList(listOf(testRecipe), onRecipeSelected = onRecipeSelected)
    }

    composeTestRule.onNodeWithTag("recipeCard12345", useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()
    assert(selected)
  }
}
