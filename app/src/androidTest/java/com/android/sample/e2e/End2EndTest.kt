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
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.model.recipe.FirestoreRecipesRepository
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipesRepository
import com.android.sample.model.recipe.RecipesViewModel
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
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

@RunWith(AndroidJUnit4::class)
class EndToEndTest {
  private val recipe1 =
      Recipe(
          "Recipe 1",
          "",
          "url1",
          "Instructions 1",
          "Category 1",
          "Area 1",
          listOf(Pair("Ingredient 1", "Ingredient 1")))
  private val recipe2 =
      Recipe(
          "Recipe 2",
          "",
          "url2",
          "Instructions 2",
          "Category 2",
          "Area 2",
          listOf(Pair("Ingredient 2", "Ingredient 2")))
  private val mockedRecipesList = listOf(recipe1, recipe2)

  private lateinit var navigationActions: NavigationActions
  private lateinit var mockUserRepository: UserRepository
  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var mockRepository: RecipesRepository

  private lateinit var userViewModel: UserViewModel
  private lateinit var createRecipeViewModel: CreateRecipeViewModel
  private lateinit var recipesViewModel: RecipesViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    mockUserRepository = mock(UserRepository::class.java)
    mockFirebaseAuth = mock(FirebaseAuth::class.java)
    mockRepository = mock(RecipesRepository::class.java)

    // Setup the mock to trigger onSuccess
    `when`(mockRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(List<Recipe>) -> Unit>(1)
      onSuccess(mockedRecipesList)
      null
    }

    `when`(mockRepository.searchByCategory(any(), any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(List<Recipe>) -> Unit>(1)
      onSuccess(mockedRecipesList)
      null
    }

    userViewModel = UserViewModel(mockUserRepository, mockFirebaseAuth)

    val firestore = mockk<FirebaseFirestore>(relaxed = true)
    val repository = FirestoreRecipesRepository(firestore)
    createRecipeViewModel = CreateRecipeViewModel(repository)
    recipesViewModel = RecipesViewModel(mockRepository)
  }

  @Test
  fun testNavigationThroughBottomNav() {

    `when`(navigationActions.currentRoute()).thenReturn(Screen.SWIPE)
    // Set the initial content to the MainScreen
    composeTestRule.setContent {
      SwipePage(navigationActions = navigationActions, recipesViewModel = recipesViewModel)
    }

    // Click on Create Recipe Icon
    composeTestRule.onNodeWithTag("tabAdd Recipe").assertExists().performClick()
    verify(navigationActions).navigateTo(TopLevelDestinations.ADD_RECIPE)

    // Click on Search Icon
    composeTestRule.onNodeWithTag("tabSearch").assertExists().performClick()
    verify(navigationActions).navigateTo(TopLevelDestinations.SEARCH)

    // Click on Account Icon
    composeTestRule.onNodeWithTag("tabAccount").assertExists().performClick()
    verify(navigationActions).navigateTo(TopLevelDestinations.ACCOUNT)

    // Click on Swipe Icon
    composeTestRule.onNodeWithTag("tabSwipe").assertExists().performClick()
    verify(navigationActions).navigateTo(TopLevelDestinations.SWIPE)

    // Click on Fridge Icon
    composeTestRule.onNodeWithTag("tabFridge").assertExists().performClick()
    verify(navigationActions).navigateTo(TopLevelDestinations.FRIDGE)
  }

  @Test
  fun bottomNavigationTest() {

    composeTestRule.setContent {
      val navController = rememberNavController()
      FakeNavHost(navController, userViewModel, createRecipeViewModel, recipesViewModel)
    }

    composeTestRule.onNodeWithTag("tabSearch").assertExists().performClick()
    composeTestRule.onNodeWithText("Search Recipe Screen").assertExists()

    composeTestRule.onNodeWithTag("tabAdd Recipe").assertExists().performClick()
    composeTestRule.onNodeWithText("Create your recipe").assertExists()
    composeTestRule.onNodeWithTag("tabAdd Recipe").assertExists().performClick()
    composeTestRule.onNodeWithTag("RecipeTitle").assertExists()

    composeTestRule.onNodeWithTag("tabFridge").assertExists().performClick()
    composeTestRule.onNodeWithText("Fridge Screen").assertExists()
  }
}

@Composable
fun FakeNavHost(
    navController: NavHostController,
    userViewModel: UserViewModel,
    createRecipeViewModel: CreateRecipeViewModel,
    recipesViewModel: RecipesViewModel
) {
  val navigationActions = NavigationActions(navController)
  NavHost(navController = navController, startDestination = Route.SWIPE) {
    navigation(
        startDestination = Screen.SWIPE,
        route = Route.SWIPE,
    ) {
      composable(Screen.SWIPE) {
        SwipePage(navigationActions = navigationActions, recipesViewModel = recipesViewModel)
      }
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
      composable(Screen.SEARCH) { SearchRecipeScreen(navigationActions, emptyList()) }
    }
    navigation(
        startDestination = Screen.CREATE_RECIPE,
        route = Route.CREATE_RECIPE,
    ) {
      composable(Screen.CREATE_RECIPE) {
        CreateRecipeScreen(navigationActions, createRecipeViewModel)
      }
    }
    navigation(
        startDestination = Screen.ACCOUNT,
        route = Route.ACCOUNT,
    ) {
      composable(Screen.ACCOUNT) { AccountScreen(navigationActions, userViewModel) }
    }
  }
}
