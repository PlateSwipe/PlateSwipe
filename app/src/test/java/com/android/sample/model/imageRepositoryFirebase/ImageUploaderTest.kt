package com.android.sample.model.imageRepositoryFirebase

import android.graphics.Bitmap
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.image.ImageUploader
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_NORMAL_URL
import com.android.sample.ui.utils.testIngredients
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class ImageUploaderTest {

  @Mock private lateinit var mockImageRepository: ImageRepositoryFirebase

  private val imageUploader = ImageUploader()
  private val dispatcher = Dispatchers.IO
  private val ingredient = testIngredients[0]

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
  }

  @Test
  fun testUploadAndRetrieveUrlAsyncFailWithIncorrectFormat() {

    val imageFormat = "not_valid"
    val exception =
        assertThrows(AssertionError::class.java) {
          runBlocking {
            imageUploader.uploadAndRetrieveUrlAsync(
                ingredient, imageFormat, mockImageRepository, dispatcher)
          }
        }
    assert(exception.message!!.contains("Image format $imageFormat not found in ingredient"))
  }

  @Test
  fun testUploadAndRetrieveUrlAsyncFailWithNullUrl() {

    val ingredient =
        testIngredients[0].copy(images = mutableMapOf(PRODUCT_FRONT_IMAGE_NORMAL_URL to ""))

    val imageFormat = "image_front_url"

    val exception =
        assertThrows(AssertionError::class.java) {
          runBlocking {
            imageUploader.uploadAndRetrieveUrlAsync(
                ingredient, imageFormat, mockImageRepository, dispatcher)
          }
        }
    assert(exception.message!!.contains("Image URL for format $imageFormat is blank"))
  }

  @Test
  fun testUploadAndRetrieveUrlAsyncFailWithNullBarcode() {

    val ingredient =
        testIngredients[0].copy(
            barCode = null,
            images = mutableMapOf(PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal"))
    val imageFormat = "image_front_url"

    val exception =
        assertThrows(AssertionError::class.java) {
          runBlocking {
            imageUploader.uploadAndRetrieveUrlAsync(
                ingredient, imageFormat, mockImageRepository, dispatcher)
          }
        }
    assert(exception.message!!.contains("Ingredient barcode is null"))
  }

  @Test
  fun testUploadAndRetrieveUrlAsyncFailWithUnsupportImageFormat() {
    val imageFormat = "image_big_url"
    val ingredient =
        testIngredients[0].copy(images = mutableMapOf(imageFormat to "https://display_normal"))

    val exception =
        assertThrows(AssertionError::class.java) {
          runBlocking {
            imageUploader.uploadAndRetrieveUrlAsync(
                ingredient, imageFormat, mockImageRepository, dispatcher)
          }
        }
    assert(exception.message!!.contains("Image format : $imageFormat is not supported"))
  }

  @Test
  fun urlToName() {
    val url = "image_front_small_url"
    val name = imageUploader.urlToName(url)
    assertEquals("display_small", name)

    val url2 = "image_front_url"
    val name2 = imageUploader.urlToName(url2)
    assert(name2 == "display_normal")

    val url3 = "image_front_thumb_url"
    val name3 = imageUploader.urlToName(url3)
    assert(name3 == "display_thumbnail")

    val url4 = "invalid_url"
    val exception =
        assertThrows(IllegalArgumentException::class.java) { imageUploader.urlToName(url4) }
    assert(exception.message!!.contains("Unsupported image format: $url4"))
  }

  @Test
  fun testUploadAndRetrieveUrlAsyncSuccess() = runTest {
    val imageFormat = PRODUCT_FRONT_IMAGE_NORMAL_URL
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    whenever(mockImageRepository.urlToBitmap(any())).thenReturn(bitmap)
    // Mock uploadImage
    `when`(
            mockImageRepository.uploadImage(
                any(), any(), any(), any(), onSuccess = any(), onFailure = any()))
        .thenAnswer { invocation ->
          val onSuccessCallback = invocation.arguments[4] as () -> Unit
          onSuccessCallback()
        }

    `when`(
            mockImageRepository.getImageUrl(
                any(), any(), any(), onSuccess = any(), onFailure = any()))
        .thenAnswer { invocation ->
          val onSuccessCallback = invocation.arguments[3] as (Uri) -> Unit
          onSuccessCallback(Uri.EMPTY)
        }

    val result =
        imageUploader.uploadAndRetrieveUrlAsync(
            ingredient, imageFormat, mockImageRepository, dispatcher)

    assertNotNull(result)
    assertEquals(imageFormat, result?.first)
    assertEquals(Uri.EMPTY.toString(), result?.second)
  }

  @Test
  fun testUploadAndRetrieveUrlAsyncFailUploadReturnNull() = runTest {
    val imageFormat = PRODUCT_FRONT_IMAGE_NORMAL_URL
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    whenever(mockImageRepository.urlToBitmap(any())).thenReturn(bitmap)
    // Mock uploadImage
    `when`(
            mockImageRepository.uploadImage(
                any(), any(), any(), any(), onSuccess = any(), onFailure = any()))
        .thenAnswer { invocation ->
          val onSuccessCallback = invocation.arguments[4] as () -> Unit
          onSuccessCallback()
        }

    `when`(
            mockImageRepository.getImageUrl(
                any(), any(), any(), onSuccess = any(), onFailure = any()))
        .thenAnswer { invocation ->
          val onFailureCallBack = invocation.arguments[4] as (Exception) -> Unit
          onFailureCallBack(
              Exception("Image download from Firebase storage has failed or image does not exist"))
        }

    val result =
        imageUploader.uploadAndRetrieveUrlAsync(
            ingredient, imageFormat, mockImageRepository, dispatcher)
    assertNull(result)
  }

  @Test
  fun testUploadAndRetrieveUrlAsyncFailRetrieveUrlReturnNull() = runTest {
    val imageFormat = PRODUCT_FRONT_IMAGE_NORMAL_URL

    // Mock uploadImage
    `when`(
            mockImageRepository.uploadImage(
                any(), any(), any(), any(), onSuccess = any(), onFailure = any()))
        .thenAnswer { invocation ->
          val onFailureCallback = invocation.arguments[5] as (Exception) -> Unit
          onFailureCallback(Exception("Image upload to Firebase storage has failed"))
        }

    `when`(
            mockImageRepository.getImageUrl(
                any(), any(), any(), onSuccess = any(), onFailure = any()))
        .thenAnswer { invocation ->
          val onFailureCallBack = invocation.arguments[4] as (Exception) -> Unit
          onFailureCallBack(
              Exception("Image download from Firebase storage has failed or image does not exist"))
        }

    val result =
        imageUploader.uploadAndRetrieveUrlAsync(
            ingredient, imageFormat, mockImageRepository, dispatcher)
    assertNull(result)
  }
}
