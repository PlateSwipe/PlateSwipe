package com.android.sample.camera

import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.takePhoto.TakePhotoViewModel
import com.android.sample.ui.camera.PhotoPicker
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PhotoPickerTest {

  private lateinit var takePhotoViewModel: TakePhotoViewModel

  companion object {
    const val PHOTO_PICKER_BUTTON = "Photo picker button"
    const val PHOTO_PICKER_TEXT = "Select Image"
    const val PHOTO_PICKER_IMAGE = "Photo picker image"
    const val PHOTO_PICKER_COLUMN = "Photo picker column"
  }

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    takePhotoViewModel = TakePhotoViewModel()
  }

  @Test
  fun photoPickerButtonTest() {
    composeTestRule.setContent { PhotoPicker(takePhotoViewModel) }
    composeTestRule.onNodeWithTag(PHOTO_PICKER_COLUMN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(PHOTO_PICKER_BUTTON).assertIsDisplayed().performClick()
  }

  @Test
  fun photoPickerTest() {
    val packageName = "com.android.sample.camera" // Replace with your app's package name
    val mockImageUri = Uri.parse("android.resource://$packageName/drawable/scoobygourmand_normal")

    takePhotoViewModel.setUri(mockImageUri)
    composeTestRule.setContent { PhotoPicker(takePhotoViewModel) }

    // Set the selectedImageUri programmatically

    composeTestRule.onNodeWithText(PHOTO_PICKER_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(PHOTO_PICKER_IMAGE).assertIsDisplayed()
  }
}
