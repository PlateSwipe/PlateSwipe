package com.android.sample.utils

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipesRepository
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.resources.C.Tag.SEARCH_BAR_PLACE_HOLDER
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.recipeOverview.SearchRecipeScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.anyOrNull

class searchRecipeScreenTest {
  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockRepository: RecipesRepository
  private lateinit var recipesViewModel: RecipesViewModel
  private lateinit var recipesList: List<Recipe>

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    mockNavigationActions = mock(NavigationActions::class.java)
    mockRepository = mock(RecipesRepository::class.java)
    recipesViewModel = RecipesViewModel(mockRepository)

    `when`(mockRepository.random(eq(1), anyOrNull(), anyOrNull())).then {}
    `when`(mockNavigationActions.currentRoute()).thenReturn(Screen.SEARCH)

    recipesList = listOf(
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
  }

  @Test
  fun displayTest() {
    composeTestRule.setContent { SearchRecipeScreen(mockNavigationActions, recipesViewModel) }
      composeTestRule.onNodeWithTag("SearchRecipeScreen").assertIsDisplayed()
      composeTestRule.onNodeWithTag("PlateSwipeLogo icon").assertIsDisplayed()
      composeTestRule.onNodeWithTag("PlateSwipeLogo text").assertIsDisplayed()
      composeTestRule.onNodeWithTag("filter button").assertIsDisplayed()
      composeTestRule.onNodeWithTag("search icon").assertIsDisplayed()
      composeTestRule.onNodeWithTag("searchBar").assertIsDisplayed()

      composeTestRule.onNodeWithTag("searchBar")
          .assertTextEquals(SEARCH_BAR_PLACE_HOLDER)
  }
}
