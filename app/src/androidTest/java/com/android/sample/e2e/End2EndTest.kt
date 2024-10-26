package com.android.sample.e2e

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.user.UserRepository
import com.android.sample.model.user.UserViewModel
import com.android.sample.ui.account.AccountScreen
import com.android.sample.ui.createRecipe.CreateRecipeScreen
import com.android.sample.ui.fridge.FridgeScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.navigation.TopLevelDestinations
import com.android.sample.ui.recipe.SearchRecipeScreen
import com.android.sample.ui.swipePage.SwipePage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class EndToEndTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var mockUserRepository: UserRepository
  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var mockCurrentUser: FirebaseUser

  private lateinit var userViewModel: UserViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    mockUserRepository = mock(UserRepository::class.java)
    mockFirebaseAuth = mock(FirebaseAuth::class.java)
    userViewModel = UserViewModel(mockUserRepository, mockFirebaseAuth)
  }

  @Test
  fun testNavigationThroughBottomNav() {

    `when`(navigationActions.currentRoute()).thenReturn(Screen.SWIPE)
    // Set the initial content to the MainScreen
    composeTestRule.setContent { SwipePage(navigationActions = navigationActions) }

    // Click on Fridge Icon
    composeTestRule.onNodeWithTag("Fridge").assertExists().performClick()
    verify(navigationActions).navigateTo(TopLevelDestinations.FRIDGE)

    // Click on Create Recipe Icon
    composeTestRule.onNodeWithTag("Add Recipe").assertExists().performClick()
    verify(navigationActions).navigateTo(TopLevelDestinations.ADD_RECIPE)

    // Click on Search Icon
    composeTestRule.onNodeWithTag("Search").assertExists().performClick()
    verify(navigationActions).navigateTo(TopLevelDestinations.SEARCH)

    // Click on Account Icon
    composeTestRule.onNodeWithTag("Account").assertExists().performClick()
    verify(navigationActions).navigateTo(TopLevelDestinations.ACCOUNT)

    // Click on Swipe Icon
    composeTestRule.onNodeWithTag("Swipe").assertExists().performClick()
    verify(navigationActions).navigateTo(TopLevelDestinations.SWIPE)
  }

  @Test
  fun bottomNavigationTest() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      FakeNavHost(navController, userViewModel)
    }

    composeTestRule.onNodeWithTag("Fridge").assertExists().performClick()
    composeTestRule.onNodeWithText("Fridge Screen").assertExists()

    composeTestRule.onNodeWithTag("Search").assertExists().performClick()
    composeTestRule.onNodeWithText("Search Recipe Screen").assertExists()

    composeTestRule.onNodeWithTag("Add Recipe").assertExists().performClick()
    composeTestRule.onNodeWithText("Create your recipe").assertExists()
  }
}

@Composable
fun FakeNavHost(navController: NavHostController, userViewModel: UserViewModel) {
  val navigationActions = NavigationActions(navController)
  NavHost(navController = navController, startDestination = Route.SWIPE) {
    navigation(
        startDestination = Screen.SWIPE,
        route = Route.SWIPE,
    ) {
      composable(Screen.SWIPE) { SwipePage(navigationActions = navigationActions) }
    }
    navigation(
        startDestination = Screen.FRIDGE,
        route = Route.FRIDGE,
    ) {
      composable(Screen.FRIDGE) { FridgeScreen(navigationActions) }
    }

    navigation(
        startDestination = Screen.SEARCH,
        route = Route.SEARCH,
    ) {
      composable(Screen.SEARCH) { SearchRecipeScreen(navigationActions) }
    }
    navigation(
        startDestination = Screen.CREATE_RECIPE,
        route = Route.CREATE_RECIPE,
    ) {
      composable(Screen.CREATE_RECIPE) { CreateRecipeScreen(navigationActions) }
    }
    navigation(
        startDestination = Screen.ACCOUNT,
        route = Route.ACCOUNT,
    ) {
      composable(Screen.ACCOUNT) { AccountScreen(navigationActions, userViewModel) }
    }
  }
}
