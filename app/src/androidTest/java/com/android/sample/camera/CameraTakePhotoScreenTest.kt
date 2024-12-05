package com.android.sample.camera

import android.Manifest
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.model.recipe.networkData.FirestoreRecipesRepository
import com.android.sample.ui.camera.CameraTakePhotoScreen
import com.android.sample.ui.camera.TakePhotoButton
import com.android.sample.ui.navigation.NavigationActions
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.mockk
import io.mockk.spyk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CameraTakePhotoScreenTest {

  private lateinit var context: Context
  private lateinit var navigationActions: NavigationActions
  private lateinit var createRecipeViewModel: CreateRecipeViewModel
  private lateinit var firestore: FirebaseFirestore
  private lateinit var repoImg: ImageRepositoryFirebase
  private lateinit var repoRecipe: FirestoreRecipesRepository

  private val preview = "camera_preview"
  private val buttonBox = "Take photo button box"
  private val button = "Take photo button"

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

  @Before
  fun setUp() {
    repoRecipe = mockk<FirestoreRecipesRepository>(relaxed = true)
    firestore = mockk<FirebaseFirestore>(relaxed = true)
    repoImg = mockk<ImageRepositoryFirebase>(relaxed = true)
    navigationActions = mockk<NavigationActions>(relaxed = true)
    createRecipeViewModel = spyk(CreateRecipeViewModel(repoRecipe, repoImg))
    context = mockk<Context>(relaxed = true)
  }

  @Test
  fun takePhotoButtonIsDisplay() {
    composeTestRule.setContent { TakePhotoButton {} }
    composeTestRule.onNodeWithTag(buttonBox).assertIsDisplayed()
    composeTestRule.onNodeWithTag(button).assertIsDisplayed()
  }

  @Test
  fun takePhotoButtonCallTakePhotoOnClick() {
    var isTakePhotoCalled = false
    composeTestRule.setContent { TakePhotoButton { isTakePhotoCalled = true } }
    composeTestRule.onNodeWithTag(button).performClick()
    assert(isTakePhotoCalled)
  }

  @Test
  fun takePhotoScreenDisplayEveryComponent() {
    composeTestRule.setContent { CameraTakePhotoScreen(navigationActions, createRecipeViewModel) }

    // Check if the camera preview is displayed
    composeTestRule.onNodeWithTag(preview).assertExists()
    composeTestRule.onNodeWithTag(preview).assertIsDisplayed()

    // Check if the button to take a photo is displayed
    composeTestRule.onNodeWithTag(buttonBox).assertIsDisplayed()
    composeTestRule.onNodeWithTag(button).assertIsDisplayed().performClick()
  }
}
