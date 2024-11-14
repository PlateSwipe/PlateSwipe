package com.android.sample.createRecipe

import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.graphics.createBitmap
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.model.recipe.FirestoreRecipesRepository
import com.android.sample.ui.createRecipe.RecipeAddImageScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecipeAddImageScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var createRecipeViewModel: CreateRecipeViewModel
  private lateinit var firestore: FirebaseFirestore
  private lateinit var repoImg: ImageRepositoryFirebase
  private lateinit var repoRecipe: FirestoreRecipesRepository

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    repoRecipe = mockk<FirestoreRecipesRepository>(relaxed = true)
    firestore = mockk<FirebaseFirestore>(relaxed = true)
    repoImg = mockk<ImageRepositoryFirebase>(relaxed = true)
    navigationActions = mockk<NavigationActions>(relaxed = true)
    createRecipeViewModel = spyk(CreateRecipeViewModel(repoRecipe, repoImg))
  }

  @Test
  fun everythingIsDisplay() {
    composeTestRule.setContent {
      RecipeAddImageScreen(
          navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    composeTestRule.onNodeWithTag("main column").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("main box").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("col 2").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("title col").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithText("Add Image").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("box for image").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("display_image_default").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("row for buttons").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("camera button").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithText("Camera").assertExists().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("gallery button")
        .assertExists()
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule.onNodeWithText("Gallery").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("box for next button").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithText("Next").assertExists().assertIsDisplayed()
  }

  @Test
  fun clickCameraNavigateToTakePhoto() {
    composeTestRule.setContent {
      RecipeAddImageScreen(
          navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    composeTestRule.onNodeWithTag("camera button").performClick()
    verify { navigationActions.navigateTo(Screen.CAMERA_TAKE_PHOTO) }
  }

  @Test
  fun nextStepNotAvailable() {
    composeTestRule.setContent {
      RecipeAddImageScreen(
          navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    composeTestRule.onNodeWithText("Next").assertIsNotEnabled()
  }

  @Test
  fun nextStepAvailableAfterTakePhoto() {
    composeTestRule.setContent {
      RecipeAddImageScreen(
          navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    createRecipeViewModel.setBitmap(bitmap, 0)

    composeTestRule.onNodeWithTag("camera button").assertIsEnabled()
  }
}
