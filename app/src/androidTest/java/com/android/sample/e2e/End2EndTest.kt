package com.android.sample.e2e

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
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
import com.android.sample.model.fridge.localData.FridgeItemLocalRepository
import com.android.sample.model.image.ImageDownload
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.ingredient.DefaultIngredientRepository
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.IngredientRepository
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipesRepository
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.model.recipe.networkData.FirestoreRecipesRepository
import com.android.sample.model.user.User
import com.android.sample.model.user.UserRepository
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_NORMAL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_SMALL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL_URL
import com.android.sample.resources.C.Tag.SAVE_BUTTON_TAG
import com.android.sample.resources.C.TestTag.AccountScreen.CREATED_RECIPES_BUTTON_TEST_TAG
import com.android.sample.resources.C.TestTag.AccountScreen.USERNAME_TEST_TAG
import com.android.sample.resources.C.TestTag.Category.BUTTON_TEST_TAG
import com.android.sample.resources.C.TestTag.Category.CATEGORY_DROPDOWN
import com.android.sample.resources.C.TestTag.Category.DIFFICULTY_DROPDOWN
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.INSTRUCTION_LIST_ITEM
import com.android.sample.resources.C.TestTag.EditAccountScreen.DATE_OF_BIRTH_CHANGE_BUTTON_TAG
import com.android.sample.resources.C.TestTag.EditAccountScreen.DATE_OF_BIRTH_TEXT_FIELD_TAG
import com.android.sample.resources.C.TestTag.EditAccountScreen.DATE_PICKER_POP_UP_CONFIRM_TAG
import com.android.sample.resources.C.TestTag.EditAccountScreen.DATE_PICKER_POP_UP_TAG
import com.android.sample.resources.C.TestTag.EditAccountScreen.SAVE_CHANGES_BUTTON_TAG
import com.android.sample.resources.C.TestTag.EditAccountScreen.USERNAME_FIELD_TAG
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
import com.android.sample.resources.C.TestTag.Utils.EDIT_ACCOUNT_ICON
import com.android.sample.ui.account.AccountScreen
import com.android.sample.ui.account.EditAccountScreen
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
import com.android.sample.ui.fridge.EditFridgeItemScreen
import com.android.sample.ui.fridge.FridgeScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.navigation.TopLevelDestinations
import com.android.sample.ui.recipe.SearchRecipeScreen
import com.android.sample.ui.recipeOverview.RecipeOverview
import com.android.sample.ui.searchIngredient.PopUpInformation
import com.android.sample.ui.searchIngredient.SearchIngredientScreen
import com.android.sample.ui.swipePage.SwipePage
import com.android.sample.ui.theme.PlateSwipeTheme
import com.android.sample.ui.utils.testRecipes
import com.android.sample.ui.utils.testUsers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.mockk
import java.time.LocalDate
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
import org.mockito.kotlin.doNothing

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

  private var testUser = testUsers[0]

  private val mockedRecipesList = testRecipes

  private lateinit var navigationActions: NavigationActions
  private lateinit var mockUserRepository: UserRepository
  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var mockFirebaseUser: FirebaseUser
  private lateinit var aggregatorIngredientRepository: DefaultIngredientRepository

  private lateinit var mockRepository: RecipesRepository
  private lateinit var userViewModel: UserViewModel
  private lateinit var createRecipeViewModel: CreateRecipeViewModel
  private lateinit var mockImageRepo: ImageRepositoryFirebase
  private lateinit var recipesViewModel: RecipesViewModel
  private lateinit var ingredientViewModel: IngredientViewModel
  private lateinit var mockIngredientRepository: IngredientRepository
  private lateinit var mockFridgeItemRepository: FridgeItemLocalRepository

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    mockUserRepository = mock(UserRepository::class.java)
    mockFirebaseAuth = mock(FirebaseAuth::class.java)
    mockFirebaseUser = mock(FirebaseUser::class.java)
    mockImageRepo = mockk<ImageRepositoryFirebase>(relaxed = true)
    mockRepository = mock(RecipesRepository::class.java)
    mockIngredientRepository = mock(IngredientRepository::class.java)
    aggregatorIngredientRepository = mock(DefaultIngredientRepository::class.java)
    mockFridgeItemRepository = mock(FridgeItemLocalRepository::class.java)

    `when`(aggregatorIngredientRepository.search(any(), any(), any(), any())).thenAnswer {
        invocation ->
      val onSuccess = invocation.getArgument<(List<Ingredient>) -> Unit>(1)
      onSuccess(listOf(ingredient1))
      null
    }
    `when`(mockIngredientRepository.search(any(), any(), any(), any())).thenAnswer { invocation ->
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

    `when`(mockUserRepository.getUserById(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.getArgument<(User) -> Unit>(1)
      onSuccess(testUser)
      null
    }
    doNothing().`when`(mockUserRepository).updateUser(any(), any(), any())

    `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
    `when`(mockFirebaseUser.uid).thenReturn(testUser.uid)
    `when`(mockFirebaseUser.email).thenReturn("example@mail.ch")
    userViewModel =
        UserViewModel(
            mockUserRepository,
            mockFirebaseAuth,
            mockIngredientRepository,
            fridgeItemRepository = mockFridgeItemRepository)

    userViewModel.changeUserName(testUser.userName)
    userViewModel.changeDateOfBirth(testUser.dateOfBirth)
    userViewModel.changeProfilePictureUrl(testUser.profilePictureUrl)

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
    composeTestRule.onNodeWithText("Empty Fridge").assertExists()
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

    composeTestRule.onNodeWithTag(RECIPE_CARD_TEST_TAG, useUnmergedTree = true).assertIsDisplayed()

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

  @Test
  fun fridgeScreenTest() {
    composeTestRule.setContent {
      val navController = rememberNavController()
      FakeNavHost(navController, userViewModel, createRecipeViewModel, recipesViewModel)
    }

    composeTestRule.onNodeWithTag("tabFridge", useUnmergedTree = true).performClick()
    composeTestRule.onNodeWithText("Empty Fridge").assertIsDisplayed()

    composeTestRule.onNodeWithText("Add Ingredient", useUnmergedTree = true).performClick()
    composeTestRule.onNodeWithTag("searchBar").performTextInput("ingredient")

    composeTestRule.waitUntil(
        5000, condition = { userViewModel.searchingIngredientList.value.isNotEmpty() })
    composeTestRule.waitForIdle()

    // select the ingredient
    composeTestRule.onNodeWithTag("ingredientItem${ingredient1.name}").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag(CONFIRMATION_BUTTON).performClick()

    // return to ingredient search list
    composeTestRule.onNodeWithTag(BACK_ARROW_ICON, useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("searchBar").performTextInput("ingredient")

    composeTestRule.waitUntil(
        5000, condition = { userViewModel.searchingIngredientList.value.isNotEmpty() })
    composeTestRule.waitForIdle()
    // select the ingredient
    composeTestRule.onNodeWithTag("ingredientItem${ingredient1.name}").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag(CONFIRMATION_BUTTON).performClick()

    // add ingredient
    composeTestRule.onNodeWithText("+", useUnmergedTree = true).performClick()
    composeTestRule.onNodeWithText("+", useUnmergedTree = true).performClick()
    composeTestRule.onNodeWithText("-", useUnmergedTree = true).performClick()

    // confirm addition of ingredient
    composeTestRule.onNodeWithText("Add", useUnmergedTree = true).performClick()
    composeTestRule.onNodeWithText("Fridge").assertIsDisplayed()
    assertEquals(1, userViewModel.fridgeItems.value.size)

    // edit ingredient
    composeTestRule
        .onNodeWithContentDescription("Edit ${ingredient1.name} Quantity", useUnmergedTree = true)
        .performClick()
    composeTestRule.onNodeWithText("-", useUnmergedTree = true).performClick()
    composeTestRule.onNodeWithText("Save", useUnmergedTree = true).performClick()
    assertEquals(1, userViewModel.fridgeItems.value.size)

    // switch to another screen
    composeTestRule.onNodeWithTag("tabAccount", useUnmergedTree = true).performClick()

    // switch back to fridge screen
    composeTestRule.onNodeWithTag("tabFridge", useUnmergedTree = true).performClick()

    // delete ingredient
    val expirationDate = LocalDate.now()
    composeTestRule
        .onNodeWithContentDescription(
            "Remove ${ingredient1.name} that expired $expirationDate from fridge")
        .performClick()
    composeTestRule.onNodeWithText("Yes").performClick()
    composeTestRule.onNodeWithText("Empty Fridge").assertIsDisplayed()
    assertEquals(0, userViewModel.fridgeItems.value.size)
  }

  @Test
  fun editAccountTest() {
    composeTestRule.setContent {
      PlateSwipeTheme {
        val navController = rememberNavController()
        FakeNavHost(navController, userViewModel, createRecipeViewModel, recipesViewModel)
      }
    }

    composeTestRule.onNodeWithTag("tabAccount", useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(EDIT_ACCOUNT_ICON, useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(BACK_ARROW_ICON, useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag(USERNAME_TEST_TAG, useUnmergedTree = true)
        .assertTextEquals(testUser.userName)

    composeTestRule.onNodeWithTag(EDIT_ACCOUNT_ICON, useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag(USERNAME_FIELD_TAG, useUnmergedTree = true).performTextClearance()
    composeTestRule
        .onNodeWithTag(USERNAME_FIELD_TAG, useUnmergedTree = true)
        .performTextInput("Trump")
    composeTestRule.onNodeWithTag(SAVE_CHANGES_BUTTON_TAG, useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(USERNAME_TEST_TAG).assertTextEquals("Trump")

    composeTestRule.onNodeWithTag(EDIT_ACCOUNT_ICON, useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag(DATE_OF_BIRTH_TEXT_FIELD_TAG, useUnmergedTree = true)
        .assertTextContains(testUser.dateOfBirth)

    composeTestRule
        .onNodeWithTag(DATE_OF_BIRTH_CHANGE_BUTTON_TAG, useUnmergedTree = true)
        .performClick()
    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag(DATE_PICKER_POP_UP_TAG, useUnmergedTree = true)
        .performTouchInput { click(center) }

    composeTestRule
        .onNodeWithTag(DATE_PICKER_POP_UP_CONFIRM_TAG, useUnmergedTree = true)
        .performClick()

    composeTestRule.onNodeWithTag(SAVE_CHANGES_BUTTON_TAG, useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(EDIT_ACCOUNT_ICON, useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag(DATE_OF_BIRTH_TEXT_FIELD_TAG, useUnmergedTree = true)
        .assert(!hasText(testUser.dateOfBirth))
  }

  @Test
  fun editCreatedRecipeTest() {
    composeTestRule.setContent {
      PlateSwipeTheme {
        val navController = rememberNavController()
        FakeNavHost(navController, userViewModel, createRecipeViewModel, recipesViewModel)
      }
    }
    userViewModel.addRecipeToUserCreatedRecipes(mockedRecipesList[0])

    composeTestRule.onNodeWithTag("tabAccount", useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag(CREATED_RECIPES_BUTTON_TEST_TAG, useUnmergedTree = true)
        .performClick()
    composeTestRule.waitForIdle()

    composeTestRule
        .onNode(
            hasAnySibling(hasText(mockedRecipesList[0].name))
                .and(hasContentDescription("Edit Recipe")),
            useUnmergedTree = true)
        .performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("recipeNameTextField").performTextClearance()
    composeTestRule.onNodeWithTag("recipeNameTextField").performTextInput("Pasta")
    composeTestRule.onNodeWithTag("NextStepButton").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag(CATEGORY_DROPDOWN).performClick()
    composeTestRule.onNodeWithText("No category", useUnmergedTree = true).performScrollTo()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("No category").performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onAllNodesWithTag(DROPDOWN_TITLE, useUnmergedTree = true)
        .assertCountEquals(2)
        .assertAny(hasText("No category"))

    // choose a difficulty
    composeTestRule.onNodeWithTag(DIFFICULTY_DROPDOWN).performClick()
    composeTestRule.onNodeWithText("No difficulty", useUnmergedTree = true).performScrollTo()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("No difficulty").performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onAllNodesWithTag(DROPDOWN_TITLE, useUnmergedTree = true)
        .assertCountEquals(2)
        .assertAny(hasText("No difficulty"))

    // get to ingredients step page
    composeTestRule.onNodeWithTag(BUTTON_TEST_TAG).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Next Step").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag(MINUTE_PICKER).performClick()
    composeTestRule.onNodeWithTag(NEXT_BUTTON).performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("PublishButton", useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()
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
        composable(Screen.OVERVIEW_RECIPE) {
          RecipeOverview(navigationActions, recipesViewModel, userViewModel)
        }
        composable(Screen.FILTER) { FilterPage(navigationActions, recipesViewModel) }
      }
      navigation(
          startDestination = Screen.FRIDGE,
          route = Route.FRIDGE,
      ) {
        composable(Screen.FRIDGE) { FridgeScreen(navigationActions, userViewModel) }
        composable(Screen.FRIDGE_SEARCH_ITEM) {
          val fridgeIngredientSearchPopUpInformation =
              PopUpInformation(
                  title = stringResource(R.string.pop_up_title_fridge),
                  confirmationText = stringResource(R.string.pop_up_description_fridge),
                  confirmationButtonText = stringResource(R.string.pop_up_confirmation_fridge),
                  onConfirmation = {
                    userViewModel.clearIngredientList()
                    userViewModel.addIngredient(it)
                    navigationActions.navigateTo(Screen.FRIDGE_EDIT)
                  })

          SearchIngredientScreen(
              navigationActions = navigationActions,
              searchIngredientViewModel = userViewModel,
              popUpInformation = fridgeIngredientSearchPopUpInformation,
              onSearchFinished = { navigationActions.navigateTo(Screen.FRIDGE_SCAN_CODE_BAR) })
        }
        composable(Screen.FRIDGE_EDIT) {
          EditFridgeItemScreen(navigationActions, userViewModel, ingredientViewModel)
        }
        composable(Screen.FRIDGE_SCAN_CODE_BAR) {
          CameraScanCodeBarScreen(
              navigationActions = navigationActions,
              searchIngredientViewModel = userViewModel,
              navigateToNextPage = { navigationActions.navigateTo(Screen.FRIDGE_EDIT) })
        }
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
              navigationActions = navigationActions,
              createRecipeViewModel = createRecipeViewModel,
              isEditing = false)
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
          val createRecipePopUpInformation =
              PopUpInformation(
                  title = stringResource(R.string.pop_up_title),
                  confirmationText = stringResource(R.string.pop_up_description),
                  confirmationButtonText = stringResource(R.string.pop_up_confirmation),
                  onConfirmation = {
                    ingredientViewModel.addIngredient(it)
                    navigationActions.navigateTo(Screen.CREATE_RECIPE_LIST_INGREDIENTS)
                  })

          SearchIngredientScreen(
              navigationActions = navigationActions,
              searchIngredientViewModel = ingredientViewModel,
              popUpInformation = createRecipePopUpInformation,
              onSearchFinished = { navigationActions.navigateTo(Screen.CAMERA_SCAN_CODE_BAR) })
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
          AccountScreen(navigationActions, userViewModel, recipesViewModel, createRecipeViewModel)
        }
        composable(Screen.OVERVIEW_RECIPE_ACCOUNT) {
          RecipeOverview(navigationActions, userViewModel, userViewModel)
        }
        composable(Screen.EDIT_RECIPE) {
          CreateRecipeScreen(
              navigationActions = navigationActions,
              createRecipeViewModel = createRecipeViewModel,
              isEditing = true)
        }
        composable(Screen.EDIT_CATEGORY_SCREEN) {
          OptionalInformationScreen(navigationActions, createRecipeViewModel, isEditing = true)
        }
        composable(Screen.EDIT_RECIPE_ADD_INSTRUCTION) {
          AddInstructionStepScreen(
              navigationActions = navigationActions,
              createRecipeViewModel = createRecipeViewModel,
              true)
        }
        composable(Screen.EDIT_RECIPE_LIST_INSTRUCTIONS) {
          RecipeListInstructionsScreen(
              navigationActions = navigationActions,
              createRecipeViewModel = createRecipeViewModel,
              isEditing = true)
        }
        composable(Screen.EDIT_RECIPE_TIME_PICKER) {
          TimePickerScreen(navigationActions, createRecipeViewModel, isEditing = true)
        }
        composable(Screen.PUBLISH_EDITED_RECIPE) {
          PublishRecipeScreen(
              navigationActions = navigationActions,
              createRecipeViewModel = createRecipeViewModel,
              userViewModel = userViewModel,
              isEditing = true)
        }
        composable(Screen.EDIT_ACCOUNT) {
          EditAccountScreen(navigationActions, userViewModel, mockFirebaseAuth, mockImageRepo)
        }
      }
    }
  }
}
