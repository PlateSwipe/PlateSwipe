package com.android.sample.utils

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.model.fridge.localData.FridgeItemLocalRepository
import com.android.sample.model.image.ImageDownload
import com.android.sample.model.ingredient.IngredientRepository
import com.android.sample.model.recipe.Instruction
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipesRepository
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.model.recipe.networkData.FirestoreRecipesRepository
import com.android.sample.model.user.UserRepository
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.TestTag.RecipeList.CANCEL_BUTTON
import com.android.sample.resources.C.TestTag.RecipeList.CONFIRMATION_BUTTON
import com.android.sample.resources.C.TestTag.RecipeList.CONFIRMATION_POP_UP
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_CARD_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_DOWNLOAD_ICON_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_FAVORITE_ICON_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_IMAGE_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_TITLE_TEST_TAG
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.utils.RecipeList
import com.android.sample.ui.utils.TopCornerDownloadAndLikeButton
import com.android.sample.ui.utils.TopCornerEditButton
import com.android.sample.ui.utils.testRecipes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class RecipeListTest {
  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockFirebaseFirestore: FirebaseFirestore
  private lateinit var mockUserRepository: UserRepository
  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var mockRecipeRepository: FirestoreRecipesRepository
  private lateinit var mockIngredientRepository: IngredientRepository
  private lateinit var mockFridgeItemLocalRepository: FridgeItemLocalRepository

  private lateinit var userViewModel: UserViewModel
  private lateinit var recipesViewModel: RecipesViewModel
  private lateinit var recipeRepository: RecipesRepository

  private val recipesList: List<Recipe> =
      listOf(
          Recipe(
              "0",
              "Meal1",
              "Meal1cat",
              "Meal1Area",
              listOf(Instruction("Meals 1 instructions")),
              "https://img.jakpost.net/c/2016/09/29/2016_09_29_12990_1475116504._large.jpg",
              listOf(Pair("1", "peu"), Pair("2", "beaucoup"), Pair("3", "peu")),
          ),
          Recipe(
              "1",
              "Meal2",
              "Meal2cat",
              "Meal2Area",
              listOf(
                  Instruction("Meals 2 instructions"),
              ),
              "https://img.jakpost.net/c/2016/09/29/2016_09_29_12990_1475116504._large.jpg",
              listOf(Pair("1", "peu"), Pair("2", "beaucoup"), Pair("3", "peu")),
          ),
          Recipe(
              "2",
              "Meal3",
              "Meal3cat",
              "Meal3Area",
              listOf(Instruction("Meals 3 instructions")),
              "https://img.jakpost.net/c/2016/09/29/2016_09_29_12990_1475116504._large.jpg",
              listOf(Pair("1", "peu"), Pair("2", "beaucoup"), Pair("3", "peu")),
          ),
          Recipe(
              "3",
              "Meal4",
              "Meal4cat",
              "Meal4Area",
              listOf(Instruction("Meals 4 instructions")),
              "https://img.jakpost.net/c/2016/09/29/2016_09_29_12990_1475116504._large.jpg",
              listOf(Pair("1", "peu"), Pair("2", "beaucoup"), Pair("3", "peu")),
          ),
          Recipe(
              "4",
              "Meal5",
              "Meal5cat",
              "Meal5Area",
              listOf(Instruction("Meals 5 instructions")),
              "https://img.jakpost.net/c/2016/09/29/2016_09_29_12990_1475116504._large.jpg",
              listOf(Pair("1", "peu"), Pair("2", "beaucoup"), Pair("3", "peu")),
          ))

  private val testRecipe =
      Recipe(
          uid = "12345",
          name = "Test Recipe",
          category = "Test Category",
          origin = "Test Area",
          instructions = listOf(Instruction("Test Instructions")),
          strMealThumbUrl = "https://example.com/image.jpg",
          ingredientsAndMeasurements = listOf(Pair("1", "Test Ingredient")),
      )

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    mockNavigationActions = mock(NavigationActions::class.java)
    mockFirebaseFirestore = mock(FirebaseFirestore::class.java)
    mockUserRepository = mock(UserRepository::class.java)
    mockFirebaseAuth = mock(FirebaseAuth::class.java)
    mockIngredientRepository = mock(IngredientRepository::class.java)
    mockRecipeRepository = FirestoreRecipesRepository(mockFirebaseFirestore)
    mockFridgeItemLocalRepository = mock(FridgeItemLocalRepository::class.java)
    recipeRepository = mock(RecipesRepository::class.java)

    userViewModel =
        UserViewModel(
            mockUserRepository,
            mockFirebaseAuth,
            mockIngredientRepository,
            mockRecipeRepository,
            fridgeItemRepository = mockFridgeItemLocalRepository)
    recipesViewModel = RecipesViewModel(recipeRepository, ImageDownload())

    `when`(mockNavigationActions.currentRoute()).thenReturn(Screen.ACCOUNT)
  }

  @Test
  fun testCorrectlyShowsList() {
    composeTestRule.setContent {
      RecipeList(
          modifier = Modifier.fillMaxSize(),
          list = recipesList,
          onRecipeSelected = {},
          topCornerButton = { recipe ->
            TopCornerDownloadAndLikeButton(
                recipe = recipe, userViewModel = userViewModel, recipesViewModel)
          })
    }

    composeTestRule
        .onAllNodesWithTag(RECIPE_CARD_TEST_TAG, useUnmergedTree = true)
        .assertCountEquals(recipesList.count())
    composeTestRule
        .onAllNodesWithTag(RECIPE_TITLE_TEST_TAG, useUnmergedTree = true)
        .assertCountEquals(recipesList.count())
    composeTestRule
        .onAllNodesWithTag(RECIPE_IMAGE_TEST_TAG, useUnmergedTree = true)
        .assertCountEquals(recipesList.count())
    composeTestRule
        .onAllNodesWithTag(RECIPE_FAVORITE_ICON_TEST_TAG, useUnmergedTree = true)
        .assertCountEquals(recipesList.count())
  }

  @Test
  fun testOnRecipeSelectedIsCalledOnSelection() {
    var selected = false

    val onRecipeSelected: (Recipe) -> Unit = { recipe ->
      assert(recipe == testRecipe)
      selected = true
    }

    composeTestRule.setContent {
      RecipeList(listOf(testRecipe), onRecipeSelected = onRecipeSelected)
    }

    composeTestRule.onNodeWithTag(RECIPE_CARD_TEST_TAG, useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()
    assert(selected)
  }

  @Test
  fun testTopCornerEditButton() {
    var clickedRecipe: Recipe? = null

    // Define the `onEditClicked` callback to capture the clicked recipe
    val onEditClicked: (Recipe) -> Unit = { recipe -> clickedRecipe = recipe }

    // Set up the test with a sample recipe
    composeTestRule.setContent {
      TopCornerEditButton(recipe = testRecipe, onEditClicked = onEditClicked)
    }

    // Verify the button is displayed
    composeTestRule
        .onNodeWithTag(RECIPE_FAVORITE_ICON_TEST_TAG, useUnmergedTree = true)
        .assertIsDisplayed()

    // Perform click action
    composeTestRule
        .onNodeWithTag(RECIPE_FAVORITE_ICON_TEST_TAG, useUnmergedTree = true)
        .performClick()

    // Verify the callback was triggered with the correct recipe
    composeTestRule.waitForIdle()
    assert(clickedRecipe == testRecipe)
  }

  @Test
  fun testTopCornerUnLikeButton() {
    recipesViewModel.setDownload(testRecipes)
    composeTestRule.setContent {
      TopCornerDownloadAndLikeButton(
          recipe = testRecipes[0], userViewModel = userViewModel, recipesViewModel)
    }
    composeTestRule.onNodeWithTag(RECIPE_DOWNLOAD_ICON_TEST_TAG).assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag(CONFIRMATION_POP_UP).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CANCEL_BUTTON).assertIsDisplayed().performClick()

    composeTestRule.onNodeWithTag(RECIPE_DOWNLOAD_ICON_TEST_TAG).assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag(CONFIRMATION_POP_UP).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CONFIRMATION_BUTTON).assertIsDisplayed().performClick()
  }

  @Test
  fun testTopCornerUnLikeButtonNotDownload() {
    composeTestRule.setContent {
      TopCornerDownloadAndLikeButton(
          recipe = testRecipes[0], userViewModel = userViewModel, recipesViewModel)
    }
    composeTestRule.onNodeWithTag(RECIPE_DOWNLOAD_ICON_TEST_TAG).assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag(CONFIRMATION_POP_UP).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CANCEL_BUTTON).assertIsDisplayed().performClick()

    composeTestRule.onNodeWithTag(RECIPE_DOWNLOAD_ICON_TEST_TAG).assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag(CONFIRMATION_POP_UP).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CONFIRMATION_BUTTON).assertIsDisplayed().performClick()
  }
}
