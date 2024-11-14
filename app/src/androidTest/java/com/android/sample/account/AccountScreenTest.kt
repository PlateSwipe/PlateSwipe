package com.android.sample.account

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.user.UserViewModel
import com.android.sample.ui.account.AccountScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.SampleAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class AccountScreenTest {
  private lateinit var mockNavigationActions: NavigationActions

  private lateinit var userViewModel: UserViewModel

  @get:Rule val composeTestRule = createComposeRule()

  private val userName: String = "John Doe"
  private val dummyRecipes: List<Recipe> =
      listOf(
          Recipe(
              idMeal = "1",
              strMeal = "Spicy Arrabiata Penne",
              strCategory = "Vegetarian",
              strArea = "Italian",
              strInstructions = "Instructions here...",
              strMealThumbUrl =
                  "https://www.recipetineats.com/penne-all-arrabbiata-spicy-tomato-pasta/",
              ingredientsAndMeasurements =
                  listOf(Pair("Penne", "1 pound"), Pair("Olive oil", "1/4 cup"))),
          Recipe(
              idMeal = "2",
              strMeal = "Chicken Curry",
              strCategory = "Non-Vegetarian",
              strArea = "Indian",
              strInstructions = "Instructions here...",
              strMealThumbUrl =
                  "https://www.foodfashionparty.com/2023/08/05/everyday-chicken-curry/",
              ingredientsAndMeasurements =
                  listOf(Pair("Chicken", "1 pound"), Pair("Curry powder", "2 tbsp"))),
          Recipe(
              idMeal = "3",
              strMeal = "Burger with Fries",
              strCategory = "Fast Food",
              strArea = "American",
              strInstructions = "Instructions here...",
              strMealThumbUrl =
                  "https://www.recipetineats.com/penne-all-arrabbiata-spicy-tomato-pasta/",
              ingredientsAndMeasurements =
                  listOf(Pair("Penne", "1 pound"), Pair("Olive oil", "1/4 cup"))),
      )

  @Before
  fun setUp() {
    mockNavigationActions = mock(NavigationActions::class.java)

    userViewModel = UserViewModel.Factory.create(UserViewModel::class.java)

    userViewModel.changeUserName(userName)

    userViewModel.addRecipeToUserLikedRecipes(dummyRecipes[0])
    userViewModel.addRecipeToUserCreatedRecipes(dummyRecipes[1])

    `when`(mockNavigationActions.currentRoute()).thenReturn(Screen.ACCOUNT)
  }

  @Test
  fun testAccountDisplays() {
    composeTestRule.setContent {
      SampleAppTheme { AccountScreen(mockNavigationActions, userViewModel) }
    }

    composeTestRule.onNodeWithTag("profilePicture").assertIsDisplayed()
    composeTestRule.onNodeWithTag("userName").assertIsDisplayed().assertTextEquals(userName)
    composeTestRule.onNodeWithTag("recipeList").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("recipeTitle${dummyRecipes[0].idMeal}", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextEquals(dummyRecipes[0].strMeal)
  }

  @Test
  fun testChangingSelectedListChangesDisplay() {
    composeTestRule.setContent {
      SampleAppTheme { AccountScreen(mockNavigationActions, userViewModel) }
    }

    composeTestRule.onNodeWithTag("createdRecipesButton").performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("recipeTitle${dummyRecipes[1].idMeal}", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextEquals(dummyRecipes[1].strMeal)

    composeTestRule.onNodeWithTag("likedRecipesButton").performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("recipeTitle${dummyRecipes[0].idMeal}", useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextEquals(dummyRecipes[0].strMeal)
  }
}
