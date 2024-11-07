package com.android.sample.model.takePhoto

import android.graphics.Bitmap
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Ingredient view model test */
@RunWith(AndroidJUnit4::class)
class TakePhotoViewModelTest {
  private lateinit var takePhotoViewModel: TakePhotoViewModel

  @Before
  fun setUp() {
    takePhotoViewModel = TakePhotoViewModel()
  }

  @Test
  fun setBitmap() {
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    // Check if the photo is set to null by default
    assert(takePhotoViewModel.photo.value == null)
    takePhotoViewModel.setBitmap(bitmap)
    assertEquals(bitmap, takePhotoViewModel.photo.value)
  }

  @Test
  fun setRotation() {
    // Check if the rotation is set to 0 by default
    assert(takePhotoViewModel.rotation.value == 0)
    takePhotoViewModel.setRotation(90)
    assertEquals(90, takePhotoViewModel.rotation.value)
  }

  @Test
  fun factory_createsTakePhotoViewModel() {
    val factory = TakePhotoViewModel.Factory
    val viewModel = factory.create(TakePhotoViewModel::class.java)
    assertTrue(takePhotoViewModel is TakePhotoViewModel)
    // Check if the viewModel can call setRotation
    viewModel.setRotation(90)
  }

  @Test
  fun setUri() {
    val uri = Uri.parse("content://media/external/images/media/139469")
    // Check if the uri is set to null by default
    assert(takePhotoViewModel.uri.value == null)
    takePhotoViewModel.setUri(uri)
    assertEquals(uri, takePhotoViewModel.uri.value)
  }
}
