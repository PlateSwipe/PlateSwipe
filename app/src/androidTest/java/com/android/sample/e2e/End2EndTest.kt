package com.android.sample.e2e

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
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
import com.android.sample.resources.C.TestTag.RecipeOverview.RECIPE_TITLE
import com.android.sample.resources.C.TestTag.SwipePage.DRAGGABLE_ITEM
import com.android.sample.resources.C.TestTag.SwipePage.FILTER
import com.android.sample.resources.C.TestTag.Utils.BACK_ARROW_ICON
import com.android.sample.ui.account.AccountScreen
import com.android.sample.ui.createRecipe.AddInstructionStepScreen
import com.android.sample.ui.createRecipe.CreateRecipeScreen
import com.android.sample.ui.createRecipe.PublishRecipeScreen
import com.android.sample.ui.createRecipe.RecipeIngredientsScreen
import com.android.sample.ui.createRecipe.RecipeInstructionsScreen
import com.android.sample.ui.filter.FilterPage
import com.android.sample.ui.fridge.FridgeScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.navigation.TopLevelDestinations
import com.android.sample.ui.recipe.SearchRecipeScreen
import com.android.sample.ui.recipeOverview.RecipeOverview
import com.android.sample.ui.swipePage.SwipePage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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

    /*`when`(mockRepository..thenAnswer { invocation ->
        val onSuccess = invocation.getArgument<(List<Recipe>) -> Unit>(1)
        onSuccess(mockedRecipesList)
        null
    }*/

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

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun cookingChickenCongee() {
    runTest {
      // using search screen (using filter if I link it to search screen)
      val mealName = "Chicken Congee"
      composeTestRule.setContent {
        val navController = rememberNavController()
        FakeNavHost(navController, userViewModel, createRecipeViewModel, recipesViewModel)
      }

      composeTestRule.onNodeWithTag("tabSearch").assertIsDisplayed()
      composeTestRule.onNodeWithTag("tabSearch").performClick()

      composeTestRule.onNodeWithTag("searchBar").performClick()
      composeTestRule.onNodeWithTag("searchBar").performTextInput("Chicken")

      // voir si toutes les recettes affichées commencent ou contiennent par chicken sont affichées

      composeTestRule.onNodeWithTag("").assertIsDisplayed()

      // afficher l'overview

      // liker et voir le like recettes dans le account

    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun likeRecipeTest() {
    runTest {
      // TODO : simuler la swipePage avec des recette que je cree car mockRepository => pas de
      // recettes de fireStore
      composeTestRule.setContent {
        val navController = rememberNavController()
        FakeNavHost(navController, userViewModel, createRecipeViewModel, recipesViewModel)
      }

      var currentRecipe = recipesViewModel.currentRecipe.value
      assertEquals(0, userViewModel.likedRecipes.value.size)

      // Filter testing
      /*composeTestRule.onNodeWithTag(FILTER).performClick()

      verify(navigationActions).navigateTo(Screen.FILTER)

      composeTestRule.onNodeWithTag("categoryCheckboxVegan", useUnmergedTree = true).performClick()

      assertEquals(0, userViewModel.likedRecipes.value.size)
      // Make sure the first recipe is displayed and of category Vegan
      composeTestRule.onNodeWithTag(DRAGGABLE_ITEM, useUnmergedTree = true).assertIsDisplayed()
      composeTestRule.onNodeWithTag(CATEGORY_CHIP, useUnmergedTree = true).assertIsDisplayed()
      composeTestRule.onNodeWithTag(CATEGORY_CHIP, useUnmergedTree = true)
          .assertTextContains("Vegan")
      */

      // Simulate a drag event (dislike)
      composeTestRule.onNodeWithTag(DRAGGABLE_ITEM).performTouchInput { swipeLeft(0f, -10000f) }

      advanceUntilIdle()

      composeTestRule.waitForIdle()
      assertNotEquals(currentRecipe, recipesViewModel.currentRecipe.value)

      currentRecipe = recipesViewModel.currentRecipe.value

      // Make sure the second recipe is displayed and of category Vegan also
      composeTestRule.onNodeWithTag(DRAGGABLE_ITEM, useUnmergedTree = true).assertIsDisplayed()
      // composeTestRule.onNodeWithTag(CATEGORY_CHIP, useUnmergedTree = true).assertIsDisplayed()
      // composeTestRule.onNodeWithTag(CATEGORY_CHIP, useUnmergedTree = true)
      // .assertTextContains("Vegan")

      // Simulate a drag event (like)
      composeTestRule.onNodeWithTag(DRAGGABLE_ITEM).performTouchInput { swipeRight(0f, 10000f) }

      advanceUntilIdle()
      composeTestRule.waitForIdle()

      // composeTestRule.onNodeWithTag("tabAccount", useUnmergedTree = true).performClick()

      // verify(navigationActions).navigateTo(TopLevelDestinations.ACCOUNT)

      advanceUntilIdle()
      composeTestRule.waitForIdle()

      // userViewModel likedRecipes testing
      assertEquals(1, userViewModel.likedRecipes.value.size)
      val likedRecipe = userViewModel.likedRecipes.value[0]
      composeTestRule
          .onNodeWithTag("recipeCard${likedRecipe.idMeal}", useUnmergedTree = true)
          .isDisplayed()

      composeTestRule
          .onNodeWithTag("recipeCard${likedRecipe.strMeal}", useUnmergedTree = true)
          .performClick()
      verify(navigationActions).navigateTo(Screen.OVERVIEW_RECIPE)
      composeTestRule
          .onNodeWithTag(RECIPE_TITLE, useUnmergedTree = true)
          .assertTextContains(likedRecipe.idMeal)

      composeTestRule
          .onNodeWithTag(BACK_ARROW_ICON, useUnmergedTree = true)
          .assertExists()
          .performClick()
      verify(navigationActions).goBack()

      // dislike recipe testing
      composeTestRule
          .onNodeWithTag("recipeFavoriteIcon${likedRecipe.idMeal}", useUnmergedTree = true)
          .performClick()

      assertEquals(0, userViewModel.likedRecipes.value.size)
      composeTestRule
          .onNodeWithTag("recipeCard${likedRecipe.idMeal}", useUnmergedTree = true)
          .assertDoesNotExist()
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
        composable(Screen.SWIPE) { SwipePage(navigationActions, recipesViewModel) }
        composable(Screen.OVERVIEW_RECIPE) { RecipeOverview(navigationActions, recipesViewModel) }
        composable(Screen.FILTER) { FilterPage(navigationActions, recipesViewModel) }
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
        composable(Screen.CREATE_RECIPE) { backStackEntry ->
          remember(backStackEntry) { navController.getBackStackEntry(Route.CREATE_RECIPE) }

          CreateRecipeScreen(
              navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
        }
        composable(Screen.CREATE_RECIPE_INGREDIENTS) {
          RecipeIngredientsScreen(
              navigationActions = navigationActions,
              createRecipeViewModel = createRecipeViewModel,
              currentStep = 1)
        }
        composable(Screen.CREATE_RECIPE_INSTRUCTIONS) {
          RecipeInstructionsScreen(navigationActions = navigationActions, currentStep = 2)
        }
        composable(Screen.CREATE_RECIPE_ADD_INSTRUCTION) {
          AddInstructionStepScreen(
              navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
        }
        composable(Screen.PUBLISH_CREATED_RECIPE) {
          PublishRecipeScreen(
              navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
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
}
