package com.android.sample.camera

import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.takePhoto.TakePhotoViewModel
import com.android.sample.ui.camera.DisplayImageScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestScreenTest {

  private lateinit var takePhotoViewModel: TakePhotoViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    takePhotoViewModel = TakePhotoViewModel()
    takePhotoViewModel.setRotation(0)
    takePhotoViewModel.setBitmap(bitmap)
  }

  @Test
  fun displayImageTest() {

    composeTestRule.setContent { DisplayImageScreen(takePhotoViewModel) }
    composeTestRule.onNodeWithTag("display_image").assertIsDisplayed()
  }
}
