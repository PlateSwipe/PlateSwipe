package com.android.sample.createRecipe

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.fridge.localData.FridgeItemLocalRepository
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.ingredient.IngredientRepository
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.model.recipe.Instruction
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.networkData.FirestoreRecipesRepository
import com.android.sample.model.user.UserRepository
import com.android.sample.model.user.UserViewModel
import com.android.sample.ui.createRecipe.PublishRecipeScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.Call
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class PublishRecipeScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var createRecipeViewModel: CreateRecipeViewModel
  private lateinit var userViewModel: UserViewModel
  private lateinit var repoImg: ImageRepositoryFirebase
  private val repository = mockk<FirestoreRecipesRepository>(relaxed = true)

  private lateinit var mockUserRepository: UserRepository
  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var mockCurrentUser: FirebaseUser
  private lateinit var mockIngredientRepository: IngredientRepository
  private lateinit var mockFridgeItemRepository: FridgeItemLocalRepository

  private lateinit var mockCall: Call

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    mockCall = mock(Call::class.java)

    navigationActions = mockk(relaxed = true)
    repoImg = mockk(relaxed = true)
    createRecipeViewModel = spyk(CreateRecipeViewModel(repository, repoImg))

    // Mock dependencies for UserViewModel
    mockUserRepository = mock(UserRepository::class.java)
    mockFirebaseAuth = mock(FirebaseAuth::class.java)
    mockCurrentUser = mock(FirebaseUser::class.java)
    mockIngredientRepository = mock(IngredientRepository::class.java)
    mockFridgeItemRepository = mock(FridgeItemLocalRepository::class.java)

    `when`(mockFirebaseAuth.currentUser).thenReturn(mockCurrentUser)
    `when`(mockCurrentUser.uid).thenReturn("001")

    // Initialize UserViewModel with mocked dependencies
    userViewModel =
        UserViewModel(
            mockUserRepository,
            mockFirebaseAuth,
            mockIngredientRepository,
            fridgeItemRepository = mockFridgeItemRepository)

    every { repository.getNewUid() } returns "valid-id"
  }

  /** Verifies that all UI elements are displayed on the PublishRecipeScreen. */
  @Test
  fun publishRecipeScreen_allFieldsDisplayed() {
    composeTestRule.setContent {
      PublishRecipeScreen(
          navigationActions = navigationActions,
          createRecipeViewModel = createRecipeViewModel,
          userViewModel = userViewModel)
    }

    composeTestRule.onNodeWithTag("DoneText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ChefImage").assertIsDisplayed()
    composeTestRule.onNodeWithTag("PublishButton").assertIsDisplayed()
  }

  /**
   * Verifies that clicking the publish button invokes recipe publishing and navigates to the next
   * screen.
   */
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun publishRecipeScreen_publishButtonTriggersPublishAndNavigation() = runTest {
    // Set up required fields in the CreateRecipeViewModel
    createRecipeViewModel.updateRecipeName("Test Recipe")
    createRecipeViewModel.addRecipeInstruction(Instruction("Test instructions"))
    createRecipeViewModel.updateRecipeThumbnail("https://example.com/image.jpg")
    createRecipeViewModel.addIngredientAndMeasurement("Ingredient", "1 cup")

    composeTestRule.setContent {
      PublishRecipeScreen(
          navigationActions = navigationActions,
          createRecipeViewModel = createRecipeViewModel,
          userViewModel)
    }

    // Click the publish button
    composeTestRule.onNodeWithText("Publish Recipe").performClick()

    // Set up success callback
    val onSuccess = slot<(Recipe) -> Unit>()
    val onFailure = slot<(Exception) -> Unit>()

    // Verify that publishRecipe was called with onSuccess and onFailure
    verify { createRecipeViewModel.publishRecipe(false, capture(onSuccess), capture(onFailure)) }

    // Simulate success callback
    onSuccess.captured.invoke(createRecipeViewModel.recipeBuilder.build())
    advanceUntilIdle()

    // Verify navigation to the CREATE_RECIPE screen
    verify { navigationActions.navigateTo(Screen.SWIPE) }
  }

  /**
   * Verifies that an error is thrown when trying to add an ingredient with a blank name or
   * measurement.
   */
  @Test(expected = IllegalArgumentException::class)
  fun publishRecipeScreen_throwsErrorWhenIngredientOrMeasurementIsBlank() {
    createRecipeViewModel.addIngredientAndMeasurement("", "1 cup")
  }

  @Test(expected = IllegalArgumentException::class)
  fun publishRecipeScreen_throwsErrorWhenMeasurementIsBlank() {
    createRecipeViewModel.addIngredientAndMeasurement("Sugar", "")
  }

  @Test
  fun publishRecipeScreen_allFieldsDisplayed_InEditMode() {
    composeTestRule.setContent {
      PublishRecipeScreen(
          navigationActions = navigationActions,
          createRecipeViewModel = createRecipeViewModel,
          userViewModel = userViewModel,
          isEditing = true)
    }

    composeTestRule.onNodeWithTag("DoneText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ChefImage").assertIsDisplayed()
    composeTestRule.onNodeWithTag("PublishButton").assertIsDisplayed()
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun publishRecipeScreen_publishButtonTriggersPublishAndNavigation_InEditMode() = runTest {
    // Set up required fields in the CreateRecipeViewModel
    createRecipeViewModel.updateRecipeName("Test Recipe")
    createRecipeViewModel.addRecipeInstruction(Instruction("Test instructions"))
    createRecipeViewModel.updateRecipeThumbnail("https://example.com/image.jpg")
    createRecipeViewModel.addIngredientAndMeasurement("Ingredient", "1 cup")

    composeTestRule.setContent {
      PublishRecipeScreen(
          navigationActions = navigationActions,
          createRecipeViewModel = createRecipeViewModel,
          userViewModel,
          isEditing = true)
    }

    // Click the publish button
    composeTestRule.onNodeWithText("Publish Recipe").performClick()

    // Set up success callback
    val onSuccess = slot<(Recipe) -> Unit>()
    val onFailure = slot<(Exception) -> Unit>()

    // Verify that publishRecipe was called with onSuccess and onFailure
    verify { createRecipeViewModel.publishRecipe(true, capture(onSuccess), capture(onFailure)) }

    // Simulate success callback
    onSuccess.captured.invoke(createRecipeViewModel.recipeBuilder.build())
    advanceUntilIdle()

    // Verify navigation to the CREATE_RECIPE screen
    verify { navigationActions.navigateTo(Screen.SWIPE) }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun publishRecipeScreen_publishButtonUpdatesUserRecipe_InEditMode() = runTest {
    // Arrange: Set up the editing mode with existing recipe details
    val oldRecipeId = "old-recipe-id"
    val newRecipeName = "Updated Recipe Name"

    createRecipeViewModel.recipeBuilder.setId(oldRecipeId)
    createRecipeViewModel.updateRecipeName(newRecipeName)
    createRecipeViewModel.addRecipeInstruction(Instruction("Updated instructions"))
    createRecipeViewModel.updateRecipeThumbnail("https://example.com/updated-image.jpg")
    createRecipeViewModel.addIngredientAndMeasurement("Updated Ingredient", "2 cups")

    // Mock existing recipe in user's created recipes
    val oldRecipe =
        Recipe(
            uid = oldRecipeId,
            name = "Old Recipe Name",
            category = "Dessert",
            instructions = listOf(Instruction("Old instructions")),
            strMealThumbUrl = "https://example.com/old-image.jpg",
            ingredientsAndMeasurements = listOf("Old Ingredient" to "1 cup"))
    userViewModel.addRecipeToUserCreatedRecipes(oldRecipe)

    composeTestRule.setContent {
      PublishRecipeScreen(
          navigationActions = navigationActions,
          createRecipeViewModel = createRecipeViewModel,
          userViewModel = userViewModel,
          isEditing = true)
    }

    // Act: Click the publish button
    composeTestRule.onNodeWithText("Publish Recipe").performClick()

    // Capture success callback
    val onSuccess = slot<(Recipe) -> Unit>()
    val onFailure = slot<(Exception) -> Unit>()

    // Verify that publishRecipe was called in editing mode
    verify { createRecipeViewModel.publishRecipe(true, capture(onSuccess), capture(onFailure)) }

    // Simulate success callback with the updated recipe
    val updatedRecipe = createRecipeViewModel.recipeBuilder.build()
    onSuccess.captured.invoke(updatedRecipe)
    advanceUntilIdle()

    // Assert: Verify the old recipe was replaced with the updated recipe
    val createdRecipes = userViewModel.createdRecipes.value
    assertNotNull(createdRecipes)
    assertTrue(createdRecipes.contains(updatedRecipe))
    assertFalse(createdRecipes.contains(oldRecipe))
    assertEquals(1, createdRecipes.size)

    // Assert: Verify navigation to the next screen
    verify { navigationActions.navigateTo(Screen.SWIPE) }
  }
}
