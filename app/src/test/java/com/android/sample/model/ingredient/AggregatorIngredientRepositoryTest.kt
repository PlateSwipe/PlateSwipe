package com.android.sample.model.ingredient

import android.graphics.Bitmap
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_NORMAL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_SMALL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL_URL
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class AggregatorIngredientRepositoryTest {
  @Mock private lateinit var mockFirestoreIngredientRepository: FirestoreIngredientRepository
  @Mock private lateinit var mockImageRepository: ImageRepositoryFirebase

  @Mock
  private lateinit var mockOpenFoodFactsIngredientRepository: OpenFoodFactsIngredientRepository

  @Captor private lateinit var onSuccessSingleCapture: ArgumentCaptor<Function1<Ingredient?, Unit>>
  @Captor
  private lateinit var onSuccessCollectionCapture: ArgumentCaptor<Function1<List<Ingredient>, Unit>>
  @Captor private lateinit var onFailureCapture: ArgumentCaptor<Function1<Exception, Unit>>

  private lateinit var aggregatorIngredientRepository: AggregatorIngredientRepository
  private val ingredient =
      Ingredient(
          uid = "1",
          name = "Coca-Cola",
          barCode = 5449000214911L,
          brands = "Coca cola",
          quantity = "330 mL",
          categories =
              listOf(
                  "Beverages and beverages preparations",
                  "Beverages",
                  "Carbonated drinks",
                  "Sodas",
                  "Carbonated soft drinks without fruit juice",
                  "Colas",
                  "Carbonated soft drinks without fruit juice with sugar",
                  "Sweetened beverages"),
          images =
              mutableMapOf(
                  PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                  PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                  PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    doNothing()
        .`when`(mockFirestoreIngredientRepository)
        .get(any(), capture(onSuccessSingleCapture), capture(onFailureCapture))

    doNothing()
        .`when`(mockFirestoreIngredientRepository)
        .search(any(), capture(onSuccessCollectionCapture), capture(onFailureCapture), any())

    doNothing()
        .`when`(mockOpenFoodFactsIngredientRepository)
        .get(any(), capture(onSuccessSingleCapture), capture(onFailureCapture))

    doNothing()
        .`when`(mockOpenFoodFactsIngredientRepository)
        .search(any(), capture(onSuccessCollectionCapture), capture(onFailureCapture), any())

    aggregatorIngredientRepository =
        AggregatorIngredientRepository(
            mockFirestoreIngredientRepository,
            mockOpenFoodFactsIngredientRepository,
            mockImageRepository)
  }

  @Test
  fun testGetReturnsRightIngredientWhenFoundInFirestore() {
    var resultingIngredient: Ingredient? = null
    var resultingException: Exception? = null

    aggregatorIngredientRepository.get(
        ingredient.barCode!!,
        onSuccess = { resultingIngredient = it },
        onFailure = { resultingException = it })

    onSuccessSingleCapture.value.invoke(ingredient)

    assertNotNull(resultingIngredient)
    assertEquals(ingredient, resultingIngredient)

    assertNull(resultingException)
  }

  @Test
  fun testGetThrowsErrorOnFailureUsingFirestoreRepo() {
    var resultingIngredient: Ingredient? = null
    var resultingException: Exception? = null

    aggregatorIngredientRepository.get(
        ingredient.barCode!!,
        onSuccess = { resultingIngredient = it },
        onFailure = { resultingException = it })

    onFailureCapture.value.invoke(Exception("Error"))

    assertNull(resultingIngredient)

    assertNotNull(resultingException)
  }

  //  @Test
  //  fun testGetFindsFromOpenFoodFactsRepoWhenNotFoundInFirestore() {
  //    var resultingIngredient: Ingredient? = null
  //    var resultingException: Exception? = null
  //
  //    aggregatorIngredientRepository.get(
  //        ingredient.barCode!!,
  //        onSuccess = { resultingIngredient = it },
  //        onFailure = { resultingException = it })
  //
  //    onSuccessSingleCapture.value.invoke(null)
  //
  //    onSuccessSingleCapture.value.invoke(ingredient)
  //
  //    verify(mockFirestoreIngredientRepository)
  //        .add(any<Ingredient>(), onSuccess = any(), onFailure = any())
  //
  //    assertNull(resultingException)
  //    assertNotNull(resultingIngredient)
  //    assertEquals(ingredient, resultingIngredient)
  //  }

  @Test
  fun testGetCallsOnFailureUsingOpenFoodFactsRepo() {
    var resultingIngredient: Ingredient? = null
    var resultingException: Exception? = null

    aggregatorIngredientRepository.get(
        ingredient.barCode!!,
        onSuccess = { resultingIngredient = it },
        onFailure = { resultingException = it })

    onSuccessSingleCapture.value.invoke(null)

    onFailureCapture.value.invoke(Exception("Error"))

    assertNotNull(resultingException)
    assertNull(resultingIngredient)
  }

  @Test
  fun testSearchReturnsRightIngredientWhenNotFoundInFirestore() {
    var resultingIngredient: Ingredient? = null
    var resultingException: Exception? = null

    aggregatorIngredientRepository.search(
        ingredient.name,
        onSuccess = { resultingIngredient = it[0] },
        onFailure = { resultingException = it },
        count = 1)

    onSuccessCollectionCapture.value.invoke(listOf(ingredient))
    onSuccessSingleCapture.value.invoke(null)

    // check that it will add the missing ingredient to firestore
    verify(mockFirestoreIngredientRepository, never())
        .add(any<Ingredient>(), onSuccess = any(), onFailure = any())

    assertNotNull(resultingIngredient)
    assertEquals(ingredient, resultingIngredient)

    assertNull(resultingException)
  }

  @Test
  fun testSearchReturnsRightIngredientWhenFoundInFirestore() {
    var resultingIngredient: Ingredient? = null
    var resultingException: Exception? = null

    aggregatorIngredientRepository.search(
        ingredient.name,
        onSuccess = { resultingIngredient = it[0] },
        onFailure = { resultingException = it },
        count = 1)

    onSuccessCollectionCapture.value.invoke(listOf(ingredient))
    onSuccessSingleCapture.value.invoke(ingredient)

    verify(mockFirestoreIngredientRepository, never())
        .add(any<Ingredient>(), onSuccess = any(), onFailure = any())

    assertNotNull(resultingIngredient)
    assertEquals(ingredient, resultingIngredient)

    assertNull(resultingException)
  }

  @Test
  fun testSearchThrowsErrorOnFailureUsingFirestoreRepo() {
    var resultingIngredient: Ingredient? = null
    var resultingException: Exception? = null

    aggregatorIngredientRepository.search(
        ingredient.name,
        onSuccess = { resultingIngredient = it[0] },
        onFailure = { resultingException = it },
        count = 1)

    onFailureCapture.value.invoke(Exception("Error"))

    assertNull(resultingIngredient)

    assertNotNull(resultingException)
  }

  @Test
  fun testSearchCallsOnFailureUsingOpenFoodFactsRepo() {
    var resultingIngredient: Ingredient? = null
    var resultingException: Exception? = null

    aggregatorIngredientRepository.search(
        ingredient.name,
        onSuccess = { resultingIngredient = it[0] },
        onFailure = { resultingException = it },
        count = 1)

    onFailureCapture.value.invoke(Exception("Error"))

    assertNotNull(resultingException)
    assertNull(resultingIngredient)
  }

  @Test
  fun testUploadAndRetrieveUrlAsyncFailWithIncorrectFormat() {

    val imageFormat = "not_valid"
    val exception =
        assertThrows(AssertionError::class.java) {
          runBlocking {
            aggregatorIngredientRepository.uploadAndRetrieveUrlAsync(ingredient, imageFormat)
          }
        }
    assert(exception.message!!.contains("Image format $imageFormat not found in ingredient"))
  }

  @Test
  fun testUploadAndRetrieveUrlAsyncFailWithNullUrl() {

    val ingredient =
        Ingredient(
            uid = "1",
            name = "Coca-Cola",
            barCode = 5449000214911L,
            brands = "Coca cola",
            quantity = "330 mL",
            categories = listOf("Beverages and beverages preparations"),
            images = mutableMapOf(PRODUCT_FRONT_IMAGE_NORMAL_URL to ""))

    val imageFormat = "image_front_url"

    val exception =
        assertThrows(AssertionError::class.java) {
          runBlocking {
            aggregatorIngredientRepository.uploadAndRetrieveUrlAsync(ingredient, imageFormat)
          }
        }
    assert(exception.message!!.contains("Image URL for format $imageFormat is blank"))
  }

  @Test
  fun testUploadAndRetrieveUrlAsyncFailWithNullBarcode() {

    val ingredient =
        Ingredient(
            uid = "1",
            name = "Coca-Cola",
            brands = "Coca cola",
            quantity = "330 mL",
            categories = listOf("Beverages and beverages preparations"),
            images = mutableMapOf(PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal"))

    val imageFormat = "image_front_url"

    val exception =
        assertThrows(AssertionError::class.java) {
          runBlocking {
            aggregatorIngredientRepository.uploadAndRetrieveUrlAsync(ingredient, imageFormat)
          }
        }
    assert(exception.message!!.contains("Ingredient barcode is null"))
  }

  @Test
  fun testUploadAndRetrieveUrlAsyncFailWithUnsupportImageFormat() {
    val imageFormat = "image_big_url"
    val ingredient =
        Ingredient(
            uid = "1",
            barCode = 5449000214911L,
            name = "Coca-Cola",
            brands = "Coca cola",
            quantity = "330 mL",
            categories = listOf("Beverages and beverages preparations"),
            images = mutableMapOf(imageFormat to "https://display_normal"))

    val exception =
        assertThrows(AssertionError::class.java) {
          runBlocking {
            aggregatorIngredientRepository.uploadAndRetrieveUrlAsync(ingredient, imageFormat)
          }
        }
    assert(exception.message!!.contains("Image format : $imageFormat is not supported"))
  }

  @Test
  fun urlToName() {
    val url = "image_front_small_url"
    val name = aggregatorIngredientRepository.urlToName(url)
    assertEquals("display_small", name)

    val url2 = "image_front_url"
    val name2 = aggregatorIngredientRepository.urlToName(url2)
    assert(name2 == "display_normal")

    val url3 = "image_front_thumb_url"
    val name3 = aggregatorIngredientRepository.urlToName(url3)
    assert(name3 == "display_thumbnail")

    val url4 = "invalid_url"
    val exception =
        assertThrows(IllegalArgumentException::class.java) {
          aggregatorIngredientRepository.urlToName(url4)
        }
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

    val result = aggregatorIngredientRepository.uploadAndRetrieveUrlAsync(ingredient, imageFormat)

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

    val result = aggregatorIngredientRepository.uploadAndRetrieveUrlAsync(ingredient, imageFormat)
    assertNull(result)
  }

  @Test
  fun testUploadAndRetrieveUrlAsyncFailRetrieveUrlReturnNull() = runTest {
    val imageFormat = PRODUCT_FRONT_IMAGE_NORMAL_URL
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    whenever(mockImageRepository.urlToBitmap(any())).thenReturn(bitmap)
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

    val result = aggregatorIngredientRepository.uploadAndRetrieveUrlAsync(ingredient, imageFormat)
    assertNull(result)
  }
}
