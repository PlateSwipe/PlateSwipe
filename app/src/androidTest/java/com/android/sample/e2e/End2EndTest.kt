package com.android.sample.e2e

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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
import com.android.sample.R
import com.android.sample.model.image.ImageDownload
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.ingredient.DefaultIngredientRepository
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipesRepository
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.model.recipe.networkData.FirestoreRecipesRepository
import com.android.sample.model.user.UserRepository
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_NORMAL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_SMALL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL_URL
import com.android.sample.resources.C.Tag.SAVE_BUTTON_TAG
import com.android.sample.resources.C.TestTag.Category.BUTTON_TEST_TAG
import com.android.sample.resources.C.TestTag.Category.CATEGORY_DROPDOWN
import com.android.sample.resources.C.TestTag.Category.DIFFICULTY_DROPDOWN
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.INSTRUCTION_LIST_ITEM
import com.android.sample.resources.C.TestTag.IngredientListScreen.ADD_INGREDIENT_ICON
import com.android.sample.resources.C.TestTag.IngredientListScreen.NEXT_STEP_BUTTON
import com.android.sample.resources.C.TestTag.IngredientSearchScreen.CANCEL_BUTTON
import com.android.sample.resources.C.TestTag.IngredientSearchScreen.CONFIRMATION_BUTTON
import com.android.sample.resources.C.TestTag.IngredientSearchScreen.SCANNER_ICON
import com.android.sample.resources.C.TestTag.PlateSwipeDropdown.DROPDOWN_TITLE
import com.android.sample.resources.C.TestTag.RecipeAddImageScreen.CAMERA_BUTTON
import com.android.sample.resources.C.TestTag.RecipeAddImageScreen.DISPLAY_IMAGE_DEFAULT
import com.android.sample.resources.C.TestTag.RecipeAddImageScreen.GALLERY_BUTTON
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_CARD_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_FAVORITE_ICON_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_TITLE_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeOverview.RECIPE_TITLE
import com.android.sample.resources.C.TestTag.SwipePage.DRAGGABLE_ITEM
import com.android.sample.resources.C.TestTag.TimePicker.HOUR_PICKER
import com.android.sample.resources.C.TestTag.TimePicker.MINUTE_PICKER
import com.android.sample.resources.C.TestTag.TimePicker.NEXT_BUTTON
import com.android.sample.resources.C.TestTag.TimePicker.TIME_PICKER_DESCRIPTION
import com.android.sample.resources.C.TestTag.TimePicker.TIME_PICKER_TITLE
import com.android.sample.resources.C.TestTag.Utils.BACK_ARROW_ICON
import com.android.sample.ui.account.AccountScreen
import com.android.sample.ui.camera.CameraScanCodeBarScreen
import com.android.sample.ui.camera.CameraTakePhotoScreen
import com.android.sample.ui.createRecipe.AddInstructionStepScreen
import com.android.sample.ui.createRecipe.CreateRecipeScreen
import com.android.sample.ui.createRecipe.IngredientListScreen
import com.android.sample.ui.createRecipe.OptionalInformationScreen
import com.android.sample.ui.createRecipe.PublishRecipeScreen
import com.android.sample.ui.createRecipe.RecipeAddImageScreen
import com.android.sample.ui.createRecipe.RecipeIngredientsScreen
import com.android.sample.ui.createRecipe.RecipeInstructionsScreen
import com.android.sample.ui.createRecipe.RecipeListInstructionsScreen
import com.android.sample.ui.createRecipe.TimePickerScreen
import com.android.sample.ui.filter.FilterPage
import com.android.sample.ui.fridge.FridgeScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.navigation.TopLevelDestinations
import com.android.sample.ui.recipe.SearchRecipeScreen
import com.android.sample.ui.recipeOverview.RecipeOverview
import com.android.sample.ui.searchIngredient.SearchIngredientScreen
import com.android.sample.ui.swipePage.SwipePage
import com.android.sample.ui.utils.testRecipes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.mockk
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

  private val ingredient1 =
      Ingredient(
          name = "ingredient1",
          quantity = "50mg",
          categories = emptyList(),
          images =
              mutableMapOf(
                  PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                  PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                  PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))

  private val mockedRecipesList = testRecipes

  private lateinit var navigationActions: NavigationActions
  private lateinit var mockUserRepository: UserRepository
  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var aggregatorIngredientRepository: DefaultIngredientRepository

  private lateinit var mockRepository: RecipesRepository
  private lateinit var userViewModel: UserViewModel
  private lateinit var createRecipeViewModel: CreateRecipeViewModel
  private lateinit var mockImageRepo: ImageRepositoryFirebase
  private lateinit var recipesViewModel: RecipesViewModel
  private lateinit var ingredientViewModel: IngredientViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    mockUserRepository = mock(UserRepository::class.java)
    mockFirebaseAuth = mock(FirebaseAuth::class.java)
    mockImageRepo = mockk<ImageRepositoryFirebase>(relaxed = true)
    mockRepository = mock(RecipesRepository::class.java)
    aggregatorIngredientRepository = mock(DefaultIngredientRepository::class.java)

    `when`(aggregatorIngredientRepository.search(any(), any(), any(), any())).thenAnswer {
        invocation ->
      val onSuccess = invocation.getArgument<(List<Ingredient>) -> Unit>(1)
      onSuccess(listOf(ingredient1))
      null
    }

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
    createRecipeViewModel = CreateRecipeViewModel(repository, mockImageRepo)
    recipesViewModel = RecipesViewModel(mockRepository, ImageDownload())

    ingredientViewModel = IngredientViewModel(aggregatorIngredientRepository, ImageDownload())

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
      SwipePage(
          navigationActions = navigationActions,
          recipesViewModel = recipesViewModel,
          userViewModel = userViewModel)
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

  /** Test the filter feature */

  /*@Test
  fun testFilter() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      FakeNavHost(navController, userViewModel, createRecipeViewModel, recipesViewModel)
    }

    composeTestRule.onNodeWithTag(DRAGGABLE_ITEM, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("tabFilter").performClick()

    composeTestRule.onNodeWithTag(CATEGORY_CHIP).assertIsDisplayed().performClick()

    composeTestRule.onNodeWithTag(CATEGORY_NAME).assertIsDisplayed().performClick()

    composeTestRule.onNodeWithTag(FILTER).performClick()
    composeTestRule.waitForIdle()

    // check if the filter is applied
    assertEquals(1, recipesViewModel.recipes.value.size)
  }*/

  /** Test the like recipe feature */
  @Test
  fun likeRecipeTest() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      FakeNavHost(navController, userViewModel, createRecipeViewModel, recipesViewModel)
    }

    // 0 liked recipe
    var currentRecipe = recipesViewModel.currentRecipe.value
    assertEquals(0, userViewModel.likedRecipes.value.size)

    composeTestRule.onNodeWithTag(DRAGGABLE_ITEM, useUnmergedTree = true).assertIsDisplayed()

    // dislike recipe 1

    composeTestRule.onNodeWithTag(DRAGGABLE_ITEM).performTouchInput { swipeLeft(0f, -10000f) }

    composeTestRule.waitForIdle()
    composeTestRule.waitUntil(5000) { recipesViewModel.currentRecipe.value != currentRecipe }

    // still 0 liked recipe
    assertEquals(0, userViewModel.likedRecipes.value.size)
    assertNotEquals(currentRecipe, recipesViewModel.currentRecipe.value)

    // new current recipe update
    currentRecipe = recipesViewModel.currentRecipe.value

    // Make sure the second recipe is displayed and of category Vegan also
    composeTestRule.onNodeWithTag(DRAGGABLE_ITEM, useUnmergedTree = true).assertIsDisplayed()

    // Like recipe 2
    composeTestRule.onNodeWithTag(DRAGGABLE_ITEM).performTouchInput { swipeRight(0f, 10000f) }

    composeTestRule.waitUntil(5000) { recipesViewModel.currentRecipe.value != currentRecipe }

    composeTestRule.waitForIdle()

    // 1 liked recipe
    assertEquals(1, userViewModel.likedRecipes.value.size)
    val likedRecipe = userViewModel.likedRecipes.value[0]

    composeTestRule.onNodeWithTag("tabAccount", useUnmergedTree = true).performClick()

    composeTestRule.onNodeWithTag(RECIPE_CARD_TEST_TAG, useUnmergedTree = true).isDisplayed()

    composeTestRule
        .onNodeWithTag(RECIPE_FAVORITE_ICON_TEST_TAG, useUnmergedTree = true)
        .performClick()

    composeTestRule
        .onAllNodesWithTag(RECIPE_TITLE_TEST_TAG, useUnmergedTree = true)
        .assertAny(hasText(likedRecipe.name))

    composeTestRule.onNodeWithTag(RECIPE_CARD_TEST_TAG, useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag(RECIPE_TITLE, useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextEquals(likedRecipe.name)

    composeTestRule
        .onNodeWithTag(BACK_ARROW_ICON, useUnmergedTree = true)
        .assertExists()
        .performClick()

    // dislike recipe testing
    composeTestRule
        .onNodeWithTag(RECIPE_FAVORITE_ICON_TEST_TAG, useUnmergedTree = true)
        .assertExists()
        .performClick()

    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag(CONFIRMATION_BUTTON, useUnmergedTree = true)
        .assertExists()
        .performClick()

    assertEquals(0, userViewModel.likedRecipes.value.size)
    composeTestRule.onNodeWithTag(RECIPE_CARD_TEST_TAG, useUnmergedTree = true).assertDoesNotExist()
  }

  @Test
  fun testCreateRecipe() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      FakeNavHost(navController, userViewModel, createRecipeViewModel, recipesViewModel)
    }

    composeTestRule.onNodeWithTag("tabAdd Recipe").assertExists().performClick()
    composeTestRule.onNodeWithTag("RecipeTitle").assertExists()

    // recipe name -------------------------------------------------------

    // change the recipe name
    composeTestRule.onNodeWithTag("recipeNameTextField").performTextInput("recipeName")
    composeTestRule.onNodeWithTag("NextStepButton").performClick()
    composeTestRule.waitForIdle()

    // optional information -------------------------------------------------------

    val selectedCategory = "Beef"

    // Choose a category
    composeTestRule.onNodeWithTag(CATEGORY_DROPDOWN).performClick()
    composeTestRule.onNodeWithText(selectedCategory, useUnmergedTree = true).performScrollTo()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText(selectedCategory).performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onAllNodesWithTag(DROPDOWN_TITLE, useUnmergedTree = true)
        .assertCountEquals(2)
        .assertAny(hasText(selectedCategory))

    val selectedDifficulty = Recipe.getDifficulties()[0]

    // choose a difficulty
    composeTestRule.onNodeWithTag(DIFFICULTY_DROPDOWN).performClick()
    composeTestRule.onNodeWithText(selectedDifficulty, useUnmergedTree = true).performScrollTo()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText(selectedDifficulty).performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onAllNodesWithTag(DROPDOWN_TITLE, useUnmergedTree = true)
        .assertCountEquals(2)
        .assertAny(hasText(selectedDifficulty))

    // get to ingredients step page
    composeTestRule.onNodeWithTag(BUTTON_TEST_TAG).performClick()
    composeTestRule.waitForIdle()

    // ingredients -------------------------------------------------------

    // get to ingredients step page
    composeTestRule.onNodeWithTag("NextStepButton").performClick()
    composeTestRule.waitForIdle()

    // change the recipe ingredients list
    composeTestRule
        .onNodeWithTag(ADD_INGREDIENT_ICON, useUnmergedTree = true)
        .assertIsDisplayed()
        .performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag(SCANNER_ICON).assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchBar").assertIsDisplayed().performTextInput("ingredient")

    composeTestRule.waitUntil(
        5000, condition = { ingredientViewModel.searchingIngredientList.value.isNotEmpty() })
    composeTestRule.waitForIdle()

    // select the ingredient
    composeTestRule
        .onNodeWithTag("ingredientItem${ingredient1.name}")
        .assertIsDisplayed()
        .performClick()
    composeTestRule.waitForIdle()

    // click to cancel and click again
    composeTestRule.onNodeWithTag(CANCEL_BUTTON).assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("ingredientItem${ingredient1.name}")
        .assertIsDisplayed()
        .performClick()
    composeTestRule.waitForIdle()

    // confirm the ingredient
    composeTestRule.onNodeWithTag(CONFIRMATION_BUTTON).assertIsDisplayed().performClick()
    composeTestRule.waitForIdle()

    // check the quantity
    composeTestRule
        .onNodeWithTag("recipeNameTextField${ingredient1.name}")
        .assertTextEquals(ingredient1.quantity!!)

    // go to next step
    composeTestRule.onNodeWithTag(NEXT_STEP_BUTTON).performClick()
    composeTestRule.waitForIdle()

    // instructions -------------------------------------------------------

    // change input fields
    composeTestRule
        .onNodeWithTag("InstructionInput")
        .assertIsDisplayed()
        .performTextInput("general instructions")
    composeTestRule.onNodeWithTag("TimeInput").assertIsDisplayed().performTextInput("10")
    composeTestRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(INSTRUCTION_LIST_ITEM).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(CreateRecipeListInstructionsScreen.NEXT_STEP_BUTTON)
        .performClick()
    composeTestRule.waitForIdle()

    // Time Picker -------------------------------------------------------

    // Verify the time picker screen is displayed
    composeTestRule
        .onNodeWithTag(TIME_PICKER_TITLE)
        .assertExists()
        .assertTextEquals("Select Total Time")
    composeTestRule
        .onNodeWithTag(TIME_PICKER_DESCRIPTION)
        .assertExists()
        .assertTextEquals(
            "Selecting the total time is optional, but it helps others understand how long your recipe will take.")

    // Simulate selecting 2 hours and 30 minutes
    composeTestRule.onNodeWithTag(HOUR_PICKER).performClick() // Simulate selecting 2 hours
    composeTestRule.onNodeWithTag(MINUTE_PICKER).performClick() // Simulate selecting 30 minutes

    // Click Next Step button
    composeTestRule.onNodeWithTag(NEXT_BUTTON).performClick()
    composeTestRule.waitForIdle()

    // image -------------------------------------------------------

    composeTestRule.onNodeWithTag(DISPLAY_IMAGE_DEFAULT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(GALLERY_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CAMERA_BUTTON).assertIsDisplayed()

    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    createRecipeViewModel.setBitmap(bitmap, 0)

    composeTestRule.onNodeWithTag("NextStepButton").performClick()
    composeTestRule.waitForIdle()

    // publish -------------------------------------------------------

    composeTestRule.onNodeWithTag("ChefImage", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("DoneText", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("PublishButton", useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()

    // check if the recipe is published
    io.mockk.verify { mockImageRepo.uploadImage(any(), any(), any(), any(), any(), any()) }
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
        composable(Screen.SWIPE) { SwipePage(navigationActions, recipesViewModel, userViewModel) }
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
        composable(Screen.SEARCH) { SearchRecipeScreen(navigationActions) }
      }
      navigation(
          startDestination = Screen.CREATE_RECIPE,
          route = Route.CREATE_RECIPE,
      ) {
        composable(Screen.CREATE_RECIPE) {
          CreateRecipeScreen(
              navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
        }
        composable(Screen.CREATE_CATEGORY_SCREEN) {
          OptionalInformationScreen(
              navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
        }
        composable(Screen.CREATE_RECIPE_INGREDIENTS) {
          RecipeIngredientsScreen(
              navigationActions = navigationActions,
              currentStep = 1,
              ingredientViewModel = ingredientViewModel)
        }
        composable(Screen.CREATE_RECIPE_INSTRUCTIONS) {
          RecipeInstructionsScreen(navigationActions = navigationActions, currentStep = 2)
        }
        composable(Screen.CREATE_RECIPE_ADD_INSTRUCTION) {
          AddInstructionStepScreen(
              navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
        }

        composable(Screen.CREATE_RECIPE_LIST_INSTRUCTIONS) {
          RecipeListInstructionsScreen(
              navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
        }
        composable(Screen.CREATE_RECIPE_TIME_PICKER) {
          TimePickerScreen(navigationActions, createRecipeViewModel)
        }
        composable(Screen.CREATE_RECIPE_ADD_IMAGE) {
          RecipeAddImageScreen(navigationActions, createRecipeViewModel)
        }
        composable(Screen.CAMERA_TAKE_PHOTO) {
          CameraTakePhotoScreen(navigationActions, createRecipeViewModel)
        }
        composable(Screen.PUBLISH_CREATED_RECIPE) {
          PublishRecipeScreen(
              navigationActions = navigationActions,
              createRecipeViewModel = createRecipeViewModel,
              userViewModel = userViewModel)
        }

        composable(Screen.CREATE_RECIPE_SEARCH_INGREDIENTS) {
          SearchIngredientScreen(
              navigationActions = navigationActions,
              searchIngredientViewModel = ingredientViewModel,
              popUpTitle = stringResource(R.string.pop_up_title),
              popUpConfirmationText = stringResource(R.string.pop_up_description),
              popUpConfirmationButtonText = stringResource(R.string.pop_up_confirmation),
              onConfirmation = {
                ingredientViewModel.addIngredient(it)
                navigationActions.navigateTo(Screen.CREATE_RECIPE_LIST_INGREDIENTS)
              })
        }

        composable(Screen.CREATE_RECIPE_LIST_INGREDIENTS) {
          IngredientListScreen(
              navigationActions = navigationActions,
              ingredientViewModel = ingredientViewModel,
              createRecipeViewModel = createRecipeViewModel)
        }
        composable(Screen.CAMERA_SCAN_CODE_BAR) {
          CameraScanCodeBarScreen(
              navigationActions = navigationActions,
              searchIngredientViewModel = ingredientViewModel)
        }
      }
      navigation(
          startDestination = Screen.ACCOUNT,
          route = Route.ACCOUNT,
      ) {
        composable(Screen.ACCOUNT) {
          AccountScreen(navigationActions, userViewModel, recipesViewModel)
        }
        composable(Screen.OVERVIEW_RECIPE_ACCOUNT) {
          RecipeOverview(navigationActions, userViewModel)
        }
      }
    }
  }
}
