package com.github.se.bootcamp.ui.overview

// ***************************************************************************** //
// ***                                                                       *** //
// *** THIS FILE WILL BE OVERWRITTEN DURING GRADING. IT SHOULD BE LOCATED IN *** //
// *** `app/src/androidTest/java/com/github/se/bootcamp/ui/overview/         *** //
// *** DO **NOT** IMPLEMENT YOUR OWN TESTS IN THIS FILE                      *** //
// ***                                                                       *** //
// ***************************************************************************** //

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.sample.model.recipe.PreparationTime
import com.android.sample.model.recipe.Recipe
import com.android.sample.ui.screens.RecipeList
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

// QUAND JE MERGE CE QU A FAIT ANDRE JE REMPLACE LE PARAMEDRE RECIPEsCREEN PAR USERVIEWMODEL
// ET JE FAIS UN MOCK POUR LE TEST DE USERVEWMODEL COMME J AI FAIT AVEC NAVIGATIONACTIONS
// MAIS JE CEER LA RECIPESLIST POUR LE TEST COME CE QUE J AI FAIT MAINTENANT
class RecipeScreenTest {
  private lateinit var navigationActions: NavigationActions
  private lateinit var recipesList: List<Recipe>

  @get:Rule val composeTestRule = createComposeRule()
  @Before
  fun setUp() {
    // Mock is a way to create a fake object that can be used in place of a real object
    navigationActions = mock(NavigationActions::class.java)
    recipesList =
        listOf(
            Recipe(
                0,
                "Meal1",
                "Meal1cat",
                "Meal1Area",
                "Meals 1 instructions",
                "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.marieclaire.fr%2Fcuisine%2F15-recettes-saines-et-equilibrees-pour-la-rentree%2C1400263.asp&psig=AOvVaw2EsPz3oLRsz1Nek_WY0sK7&ust=1729001857122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCe9f2HjokDFQAAAAAdAAAAABAE",
                listOf(1, 2, 3),
                listOf("peu", "beaucoup", "peu"),
                2.0,
                PreparationTime(1, 30),
                3),
            Recipe(
                1,
                "Meal2",
                "Meal2cat",
                "Meal2Area",
                "Meals 2 instructions",
                "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.marieclaire.fr%2Fcuisine%2F15-recettes-saines-et-equilibrees-pour-la-rentree%2C1400263.asp&psig=AOvVaw2EsPz3oLRsz1Nek_WY0sK7&ust=1729001857122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCe9f2HjokDFQAAAAAdAAAAABAE",
                listOf(2, 3, 4),
                listOf("un peu", "moyen", "beaucoup"),
                4.9,
                PreparationTime(0, 45),
                4),
            Recipe(
                2,
                "Meal3",
                "Meal3cat",
                "Meal3Area",
                "Meals 3 instructions",
                "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.marieclaire.fr%2Fcuisine%2F15-recettes-saines-et-equilibrees-pour-la-rentree%2C1400263.asp&psig=AOvVaw2EsPz3oLRsz1Nek_WY0sK7&ust=1729001857122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCe9f2HjokDFQAAAAAdAAAAABAE",
                listOf(1, 2),
                listOf("beaucoup", "peu"),
                1.2,
                PreparationTime(1, 15),
                2),
            Recipe(
                3,
                "Meal4",
                "Meal4cat",
                "Meal4Area",
                "Meals 4 instructions",
                "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.marieclaire.fr%2Fcuisine%2F15-recettes-saines-et-equilibrees-pour-la-rentree%2C1400263.asp&psig=AOvVaw2EsPz3oLRsz1Nek_WY0sK7&ust=1729001857122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCe9f2HjokDFQAAAAAdAAAAABAE",
                listOf(3, 5, 7),
                listOf("moyen", "beaucoup", "un peu"),
                3.0,
                PreparationTime(2, 0),
                5),
            Recipe(
                4,
                "Meal5",
                "Meal5cat",
                "Meal5Area",
                "Meals 5 instructions",
                "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.marieclaire.fr%2Fcuisine%2F15-recettes-saines-et-equilibrees-pour-la-rentree%2C1400263.asp&psig=AOvVaw2EsPz3oLRsz1Nek_WY0sK7&ust=1729001857122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCe9f2HjokDFQAAAAAdAAAAABAE",
                listOf(4, 6, 8),
                listOf("beaucoup", "peu", "un peu"),
                2.5,
                PreparationTime(0, 30),
                4))
    // Mock the current route to be the add todo screen
    `when`(navigationActions.currentRoute()).thenReturn(Screen.RECIPE)
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent { RecipeList(recipesList, navigationActions) }

    composeTestRule.onNodeWithTag("recipeList").assertIsDisplayed()

    composeTestRule.onNodeWithTag("SearchButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchTextText", useUnmergedTree = true).assertTextEquals("search")
    composeTestRule.onNodeWithTag("searchButtonIcon", useUnmergedTree = true).assertIsDisplayed()

    composeTestRule.onNodeWithTag("bottomNavigationMenu", useUnmergedTree = true).assertIsDisplayed()

    for (recipe in recipesList) {
      composeTestRule.onNodeWithTag("recipeCard${recipe.idMeal}").assertIsDisplayed()

      composeTestRule.onNodeWithTag("recipeImage${recipe.idMeal}").assertIsDisplayed()

      composeTestRule.onNodeWithTag("recipeTitle${recipe.idMeal}").assertIsDisplayed()
      composeTestRule.onNodeWithTag("recipeTitle${recipe.idMeal}").assertTextEquals(recipe.strMeal)

      composeTestRule.onNodeWithTag("recipeRatingIcon${recipe.idMeal}").assertIsDisplayed()
      composeTestRule.onNodeWithTag("recipeRating${recipe.idMeal}").assertTextEquals(recipe.rating.toString())

      composeTestRule.onNodeWithTag("recipeTime${recipe.idMeal}").assertIsDisplayed().assertTextEquals(recipe.preparationTime.toString())
      composeTestRule.onNodeWithTag("recipeTimeIcon${recipe.idMeal}").assertIsDisplayed()

      composeTestRule.onNodeWithTag("priceRating${recipe.idMeal}").assertIsDisplayed()
    }
  }
}
