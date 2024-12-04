package com.android.sample.model.ingredient.network

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.image.ImageDownload
import com.android.sample.model.ingredient.DefaultIngredientRepository
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_NORMAL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_SMALL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL_URL
import com.android.sample.ui.utils.testIngredients
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class IngredientDownloadTest {

  @Mock private lateinit var defaultIngredientRepository: DefaultIngredientRepository
  @Mock private lateinit var imageDownload: ImageDownload
  private lateinit var ingredientViewModel: IngredientViewModel
  private val context: Context = ApplicationProvider.getApplicationContext()

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    ingredientViewModel = IngredientViewModel(defaultIngredientRepository, imageDownload)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `test download ingredients`() = runTest {
    val ingr =
        testIngredients[0].copy(
            images =
                mutableMapOf(
                    PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                    PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))
    // Mock downloadAndSaveImage to return "path" for any inputs
    `when`(imageDownload.downloadAndSaveImage(any(), any(), any(), any())).thenReturn("path")

    var ver = false
    // Validate that ingredient.images contains no null URLs
    assert(ingr.images.values.all { it != null })
    ingredientViewModel.downloadIngredient(
        ingr, context, Dispatchers.IO, onSuccess = { ver = true }, onFailure = { ver = false })
    advanceUntilIdle()
    verify(imageDownload, times(3)).downloadAndSaveImage(any(), any(), any(), any())

    assert(ver)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testDownloadIngredientFail() = runTest {
    val ingr =
        testIngredients[0].copy(
            images =
                mutableMapOf(
                    PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                    PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))
    val context: Context = ApplicationProvider.getApplicationContext()

    ingr.images.forEach { (format, url) ->
      `when`(imageDownload.downloadAndSaveImage(context, url, ingr.name + format, Dispatchers.IO))
          .thenThrow(RuntimeException("Error"))
    }

    ingredientViewModel.downloadIngredient(testIngredients[0], context, Dispatchers.IO, {}, {})
    advanceUntilIdle()
    verify(defaultIngredientRepository, times(0)).addDownload(testIngredients[0])
  }
}
