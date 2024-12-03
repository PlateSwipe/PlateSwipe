package com.android.sample.account

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.Tag.RECIPE_FAVORITE_ICON_CONTENT_DESCRIPTION
import com.android.sample.resources.C.TestTag.AccountScreen.CREATED_RECIPES_BUTTON_TEST_TAG
import com.android.sample.resources.C.TestTag.AccountScreen.LIKED_RECIPES_BUTTON_TEST_TAG
import com.android.sample.resources.C.TestTag.AccountScreen.PROFILE_PICTURE_TEST_TAG
import com.android.sample.resources.C.TestTag.AccountScreen.USERNAME_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.CANCEL_BUTTON
import com.android.sample.resources.C.TestTag.RecipeList.CONFIRMATION_BUTTON
import com.android.sample.resources.C.TestTag.RecipeList.CONFIRMATION_POP_UP
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_CARD_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_DELETE_ICON_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_FAVORITE_ICON_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_LIST_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_TITLE_TEST_TAG
import com.android.sample.ui.account.AccountScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.SampleAppTheme
import com.android.sample.ui.utils.testRecipes
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
  private val dummyRecipes: List<Recipe> = testRecipes

  @Before
  fun setUp() {
    mockNavigationActions = mock(NavigationActions::class.java)

    userViewModel =
        UserViewModel.provideFactory(ApplicationProvider.getApplicationContext())
            .create(UserViewModel::class.java)

    userViewModel.changeUserName(userName)

    userViewModel.addRecipeToUserLikedRecipes(dummyRecipes[0])
    userViewModel.addRecipeToUserCreatedRecipes(dummyRecipes[1])

    `when`(mockNavigationActions.currentRoute()).thenReturn(Screen.ACCOUNT)
  }

  @Test
  fun testAccountDisplaysWithoutProfilePicture() {
    composeTestRule.setContent {
      SampleAppTheme { AccountScreen(mockNavigationActions, userViewModel) }
    }

    composeTestRule.onNodeWithTag(PROFILE_PICTURE_TEST_TAG).assertIsDisplayed()
    composeTestRule.onNodeWithTag(USERNAME_TEST_TAG).assertIsDisplayed().assertTextEquals(userName)
    composeTestRule.onNodeWithTag(RECIPE_LIST_TEST_TAG).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(RECIPE_TITLE_TEST_TAG, useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextEquals(dummyRecipes[0].name)
  }

  @Test
  fun testAccountDisplaysWithProfilePicture() {
    userViewModel.changeProfilePictureUrl(
        "app/src/androidTest/res/drawable/scoobygourmand_normal.jpg")
    composeTestRule.setContent {
      SampleAppTheme { AccountScreen(mockNavigationActions, userViewModel) }
    }

    composeTestRule.onNodeWithTag(PROFILE_PICTURE_TEST_TAG).assertIsDisplayed()
    composeTestRule.onNodeWithTag(USERNAME_TEST_TAG).assertIsDisplayed().assertTextEquals(userName)
    composeTestRule.onNodeWithTag(RECIPE_LIST_TEST_TAG).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(RECIPE_TITLE_TEST_TAG, useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextEquals(dummyRecipes[0].name)
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
        .assertTextEquals(dummyRecipes[1].name)

    composeTestRule.onNodeWithTag(LIKED_RECIPES_BUTTON_TEST_TAG).performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag(RECIPE_TITLE_TEST_TAG, useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextEquals(dummyRecipes[0].name)
  }

  @Test
  fun testSelectALikedRecipe() {
    composeTestRule.setContent {
      SampleAppTheme { AccountScreen(mockNavigationActions, userViewModel) }
    }

    composeTestRule
        .onNodeWithText(dummyRecipes[0].name, useUnmergedTree = true)
        .assertIsDisplayed()
        .performClick()
    verify(mockNavigationActions).navigateTo(Screen.OVERVIEW_RECIPE_ACCOUNT)
  }

  @Test
  fun testRemoveLikedRecipePopUpDisplayed() {
    composeTestRule.setContent {
      SampleAppTheme { AccountScreen(mockNavigationActions, userViewModel) }
    }
    composeTestRule
        .onNode(
            hasAnySibling(hasText(dummyRecipes[0].name))
                .and(hasTestTag(RECIPE_FAVORITE_ICON_TEST_TAG)),
            useUnmergedTree = true)
        .assertIsDisplayed()
        .performClick()
    composeTestRule.onNodeWithTag(CONFIRMATION_POP_UP).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CONFIRMATION_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CONFIRMATION_BUTTON).assertHasClickAction()
    composeTestRule.onNodeWithTag(CANCEL_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CANCEL_BUTTON).assertHasClickAction()
  }

  @Test
  fun testDeleteCreatedRecipePopUpDisplayed() {
    composeTestRule.setContent {
      SampleAppTheme { AccountScreen(mockNavigationActions, userViewModel) }
    }

    composeTestRule.onNodeWithTag(CREATED_RECIPES_BUTTON_TEST_TAG).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CREATED_RECIPES_BUTTON_TEST_TAG).assertHasClickAction()
    composeTestRule.onNodeWithTag(CREATED_RECIPES_BUTTON_TEST_TAG).performClick()

    composeTestRule.onNodeWithTag(RECIPE_CARD_TEST_TAG).assertIsDisplayed()
    composeTestRule
        .onNode(
            hasAnySibling(hasText(dummyRecipes[1].name))
                .and(hasTestTag(RECIPE_DELETE_ICON_TEST_TAG)),
            useUnmergedTree = true)
        .assertIsDisplayed()
        .performClick()

    composeTestRule.onNodeWithTag(CONFIRMATION_POP_UP).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CONFIRMATION_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CONFIRMATION_BUTTON).assertHasClickAction()
    composeTestRule.onNodeWithTag(CANCEL_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CANCEL_BUTTON).assertHasClickAction()
  }

  @Test
  fun testRemoveALikedRecipe() {
    composeTestRule.setContent {
      SampleAppTheme { AccountScreen(mockNavigationActions, userViewModel) }
    }
    composeTestRule
        .onNode(
            hasAnySibling(hasText(dummyRecipes[0].name))
                .and(hasTestTag(RECIPE_FAVORITE_ICON_TEST_TAG)),
            useUnmergedTree = true)
        .assertIsDisplayed()
        .performClick()
    composeTestRule.onNodeWithTag(CONFIRMATION_POP_UP).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CONFIRMATION_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CONFIRMATION_BUTTON).performClick()
    assert(!userViewModel.likedRecipes.value.contains(dummyRecipes[0]))
    assert(userViewModel.likedRecipes.value.isEmpty())
    composeTestRule.onNodeWithText(dummyRecipes[0].name).assertIsNotDisplayed()
  }

  @Test
  fun testNotRemoveALikedRecipe() {
    composeTestRule.setContent {
      SampleAppTheme { AccountScreen(mockNavigationActions, userViewModel) }
    }
    composeTestRule
        .onNode(
            hasAnySibling(hasText(dummyRecipes[0].name))
                .and(hasContentDescription(RECIPE_FAVORITE_ICON_CONTENT_DESCRIPTION)),
            useUnmergedTree = true)
        .assertIsDisplayed()
        .performClick()
    composeTestRule.onNodeWithTag(CONFIRMATION_POP_UP).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CANCEL_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CANCEL_BUTTON).performClick()
    assert(userViewModel.likedRecipes.value.isNotEmpty())
    assert(userViewModel.likedRecipes.value.contains(dummyRecipes[0]))
    composeTestRule.onNodeWithText(dummyRecipes[0].name).assertIsDisplayed()
  }

  @Test
  fun testNotDeleteACreatedRecipe() {
    composeTestRule.setContent {
      SampleAppTheme { AccountScreen(mockNavigationActions, userViewModel) }
    }

    composeTestRule.onNodeWithTag(CREATED_RECIPES_BUTTON_TEST_TAG).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CREATED_RECIPES_BUTTON_TEST_TAG).assertHasClickAction()
    composeTestRule.onNodeWithTag(CREATED_RECIPES_BUTTON_TEST_TAG).performClick()

    composeTestRule
        .onNode(
            hasAnySibling(hasText(dummyRecipes[1].name))
                .and(hasTestTag(RECIPE_DELETE_ICON_TEST_TAG)),
            useUnmergedTree = true)
        .assertIsDisplayed()
        .performClick()
    composeTestRule.onNodeWithTag(CONFIRMATION_POP_UP).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CANCEL_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CANCEL_BUTTON).performClick()

    assert(userViewModel.createdRecipes.value.contains(dummyRecipes[1]))
    assert(userViewModel.createdRecipes.value.isNotEmpty())
    composeTestRule.onNodeWithText(dummyRecipes[1].name).assertIsDisplayed()
  }
}
