package com.android.sample.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.sample.model.recipe.PreparationTime
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.user.UserViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.screens.RecipeList
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.Call
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class RecipeScreenTest {
  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockUserViewModel: UserViewModel
  private lateinit var mockCall: Call
  private lateinit var recipesList: List<Recipe>

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    // Mock is a way to create a fake object that can be used in place of a real object
    MockitoAnnotations.openMocks(this)
    mockCall = mock(Call::class.java)

    mockNavigationActions = mock(NavigationActions::class.java)
    mockUserViewModel = mock(UserViewModel::class.java)

    recipesList =
        listOf(
            Recipe(
                "0",
                "Meal1",
                "Meal1cat",
                "Meal1Area",
                "Meals 1 instructions",
                "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.marieclaire.fr%2Fcuisine%2F15-recettes-saines-et-equilibrees-pour-la-rentree%2C1400263.asp&psig=AOvVaw2EsPz3oLRsz1Nek_WY0sK7&ust=1729001857122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCe9f2HjokDFQAAAAAdAAAAABAE",
                listOf(Pair("1", "peu"), Pair("2", "beaucoup"), Pair("3", "peu")),
                2.0,
                PreparationTime(1, 30),
                3),
            Recipe(
                "1",
                "Meal2",
                "Meal2cat",
                "Meal2Area",
                "Meals 2 instructions",
                "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.marieclaire.fr%2Fcuisine%2F15-recettes-saines-et-equilibrees-pour-la-rentree%2C1400263.asp&psig=AOvVaw2EsPz3oLRsz1Nek_WY0sK7&ust=1729001857122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCe9f2HjokDFQAAAAAdAAAAABAE",
                listOf(Pair("1", "peu"), Pair("2", "beaucoup"), Pair("3", "peu")),
                4.9,
                PreparationTime(0, 45),
                4),
            Recipe(
                "2",
                "Meal3",
                "Meal3cat",
                "Meal3Area",
                "Meals 3 instructions",
                "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.marieclaire.fr%2Fcuisine%2F15-recettes-saines-et-equilibrees-pour-la-rentree%2C1400263.asp&psig=AOvVaw2EsPz3oLRsz1Nek_WY0sK7&ust=1729001857122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCe9f2HjokDFQAAAAAdAAAAABAE",
                listOf(Pair("1", "peu"), Pair("2", "beaucoup"), Pair("3", "peu")),
                1.2,
                PreparationTime(1, 15),
                2),
            Recipe(
                "3",
                "Meal4",
                "Meal4cat",
                "Meal4Area",
                "Meals 4 instructions",
                "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.marieclaire.fr%2Fcuisine%2F15-recettes-saines-et-equilibrees-pour-la-rentree%2C1400263.asp&psig=AOvVaw2EsPz3oLRsz1Nek_WY0sK7&ust=1729001857122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCe9f2HjokDFQAAAAAdAAAAABAE",
                listOf(Pair("1", "peu"), Pair("2", "beaucoup"), Pair("3", "peu")),
                3.0,
                PreparationTime(2, 0),
                5),
            Recipe(
                "4",
                "Meal5",
                "Meal5cat",
                "Meal5Area",
                "Meals 5 instructions",
                "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.marieclaire.fr%2Fcuisine%2F15-recettes-saines-et-equilibrees-pour-la-rentree%2C1400263.asp&psig=AOvVaw2EsPz3oLRsz1Nek_WY0sK7&ust=1729001857122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCe9f2HjokDFQAAAAAdAAAAABAE",
                listOf(Pair("1", "peu"), Pair("2", "beaucoup"), Pair("3", "peu")),
                2.5,
                PreparationTime(0, 30),
                4))

    val savedRecipes = MutableStateFlow(recipesList)

    `when`(mockNavigationActions.currentRoute()).thenReturn(Screen.RECIPE)
    doReturn(savedRecipes).`when`(mockUserViewModel).savedRecipes
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent { RecipeList(mockUserViewModel, mockNavigationActions) }

    composeTestRule.onNodeWithTag("recipeList").assertIsDisplayed()

    composeTestRule.onNodeWithTag("SearchButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchText").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("searchTextText", useUnmergedTree = true)
        .assertTextEquals("search")
    composeTestRule.onNodeWithTag("searchButtonIcon", useUnmergedTree = true).assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("bottomNavigationMenu", useUnmergedTree = true)
        .assertIsDisplayed()

    for (recipe in recipesList) {
      composeTestRule.onNodeWithTag("recipeCard${recipe.idMeal}").assertIsDisplayed()

      composeTestRule.onNodeWithTag("recipeImage${recipe.idMeal}").assertIsDisplayed()

      composeTestRule.onNodeWithTag("recipeTitle${recipe.idMeal}").assertIsDisplayed()
      composeTestRule.onNodeWithTag("recipeTitle${recipe.idMeal}").assertTextEquals(recipe.strMeal)

      composeTestRule.onNodeWithTag("recipeRatingIcon${recipe.idMeal}").assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("recipeRating${recipe.idMeal}")
          .assertTextEquals(recipe.rating.toString())

      composeTestRule
          .onNodeWithTag("recipeTime${recipe.idMeal}")
          .assertIsDisplayed()
          .assertTextEquals(recipe.preparationTime.toString())
      composeTestRule.onNodeWithTag("recipeTimeIcon${recipe.idMeal}").assertIsDisplayed()

      composeTestRule.onNodeWithTag("priceRating${recipe.idMeal}").assertIsDisplayed()
    }
  }
}
