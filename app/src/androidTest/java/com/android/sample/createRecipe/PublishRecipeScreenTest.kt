package com.android.sample.createRecipe

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
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

    `when`(mockFirebaseAuth.currentUser).thenReturn(mockCurrentUser)
    `when`(mockCurrentUser.uid).thenReturn("001")

    // Initialize UserViewModel with mocked dependencies
    userViewModel = UserViewModel(mockUserRepository, mockFirebaseAuth, mockIngredientRepository)

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
    verify { createRecipeViewModel.publishRecipe(capture(onSuccess), capture(onFailure)) }

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
}
