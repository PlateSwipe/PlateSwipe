package com.android.sample.account

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.Tag.RECIPE_FAVORITE_ICON_CONTENT_DESCRIPTION
import com.android.sample.resources.C.TestTag.AccountScreen.CREATED_RECIPES_BUTTON_TEST_TAG
import com.android.sample.resources.C.TestTag.AccountScreen.LIKED_RECIPES_BUTTON_TEST_TAG
import com.android.sample.resources.C.TestTag.AccountScreen.PROFILE_PICTURE_TEST_TAG
import com.android.sample.resources.C.TestTag.AccountScreen.USERNAME_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_LIST_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_TITLE_TEST_TAG
import com.android.sample.ui.account.AccountScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.SampleAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
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

    composeTestRule.onNodeWithTag(PROFILE_PICTURE_TEST_TAG).assertIsDisplayed()
    composeTestRule.onNodeWithTag(USERNAME_TEST_TAG).assertIsDisplayed().assertTextEquals(userName)
    composeTestRule.onNodeWithTag(RECIPE_LIST_TEST_TAG).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(RECIPE_TITLE_TEST_TAG, useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextEquals(dummyRecipes[0].strMeal)
  }

  @Test
  fun testChangingSelectedListChangesDisplay() {
    composeTestRule.setContent {
      SampleAppTheme { AccountScreen(mockNavigationActions, userViewModel) }
    }

    composeTestRule.onNodeWithTag(CREATED_RECIPES_BUTTON_TEST_TAG).performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag(RECIPE_TITLE_TEST_TAG, useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextEquals(dummyRecipes[1].strMeal)

    composeTestRule.onNodeWithTag(LIKED_RECIPES_BUTTON_TEST_TAG).performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag(RECIPE_TITLE_TEST_TAG, useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextEquals(dummyRecipes[0].strMeal)
  }

  @Test
  fun testSelectALikedRecipe() {
    composeTestRule.setContent {
      SampleAppTheme { AccountScreen(mockNavigationActions, userViewModel) }
    }

    composeTestRule
        .onNodeWithText("Spicy Arrabiata Penne", useUnmergedTree = true)
        .assertIsDisplayed()
        .performClick()
    verify(mockNavigationActions).navigateTo(Screen.OVERVIEW_RECIPE_ACCOUNT)
  }

  @Test
  fun testRemoveALikedRecipe() {
    composeTestRule.setContent {
      SampleAppTheme { AccountScreen(mockNavigationActions, userViewModel) }
    }

    composeTestRule
        .onNode(
            hasAnySibling(hasText("Spicy Arrabiata Penne"))
                .and(hasContentDescription(RECIPE_FAVORITE_ICON_CONTENT_DESCRIPTION)),
            useUnmergedTree = true)
        .assertIsDisplayed()
        .performClick()
    assert(userViewModel.likedRecipes.value.isEmpty())
    assert(dummyRecipes != userViewModel.likedRecipes.value)
    composeTestRule.onNodeWithText("Spicy Arrabiata Penne").assertIsNotDisplayed()
  }
}
