package com.android.sample.camera

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.sample.model.takePhoto.TakePhotoViewModel
import com.android.sample.ui.camera.CameraTakePhotoScreen
import com.android.sample.ui.camera.TakePhotoButton
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class CameraTakePhotoScreenTest {

  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var takePhotoViewModel: TakePhotoViewModel

  private val preview = "camera_preview"
  private val buttonBox = "Take photo button box"
  private val button = "Take photo button"

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

  @Before
  fun setUp() {
    mockNavigationActions = mock(NavigationActions::class.java)
    takePhotoViewModel = TakePhotoViewModel()

    `when`(mockNavigationActions.currentRoute()).thenReturn(Screen.CAMERA_TAKE_PHOTO)
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
    composeTestRule.setContent { CameraTakePhotoScreen(mockNavigationActions, takePhotoViewModel) }

    // Check if the camera preview is displayed
    composeTestRule.onNodeWithTag(preview).assertExists()
    composeTestRule.onNodeWithTag(preview).assertIsDisplayed()

    // Check if the button to take a photo is displayed
    composeTestRule.onNodeWithTag(buttonBox).assertIsDisplayed()
    composeTestRule.onNodeWithTag(button).assertIsDisplayed()
  }
}
