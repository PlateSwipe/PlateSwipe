package com.android.sample.model.ingredient

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.image.ImageUploader
import com.android.sample.ui.utils.testIngredients
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.fail
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
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class AggregatorIngredientRepositoryTest {
  @Mock private lateinit var mockFirestoreIngredientRepository: FirestoreIngredientRepository
  @Mock private lateinit var mockImageRepository: ImageRepositoryFirebase

  @Mock
  private lateinit var mockOpenFoodFactsIngredientRepository: OpenFoodFactsIngredientRepository

  @Mock private lateinit var mockImageUploader: ImageUploader

  @Captor private lateinit var onSuccessVoidCapture: ArgumentCaptor<Function0<Unit>>
  @Captor private lateinit var onSuccessSingleCapture: ArgumentCaptor<Function1<Ingredient?, Unit>>
  @Captor
  private lateinit var onSuccessCollectionCapture: ArgumentCaptor<Function1<List<Ingredient>, Unit>>
  @Captor private lateinit var onFailureCapture: ArgumentCaptor<Function1<Exception, Unit>>

  private lateinit var aggregatorIngredientRepository: AggregatorIngredientRepository
  private val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
  private val dispatcher = Dispatchers.IO
  private val ingredient = testIngredients[0]

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    whenever(mockImageRepository.urlToBitmap(any())).thenReturn(bitmap)

    doNothing()
        .`when`(mockFirestoreIngredientRepository)
        .get(any(), capture(onSuccessSingleCapture), capture(onFailureCapture))

    doNothing()
        .`when`(mockFirestoreIngredientRepository)
        .search(any(), capture(onSuccessCollectionCapture), capture(onFailureCapture))

    doNothing()
        .`when`(mockOpenFoodFactsIngredientRepository)
        .get(any(), capture(onSuccessSingleCapture), capture(onFailureCapture))

    doNothing()
        .`when`(mockOpenFoodFactsIngredientRepository)
        .search(any(), capture(onSuccessCollectionCapture), capture(onFailureCapture))

    aggregatorIngredientRepository =
        AggregatorIngredientRepository(
            mockFirestoreIngredientRepository,
            mockOpenFoodFactsIngredientRepository,
            mockImageRepository,
            mockImageUploader)
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
  fun testGetFailWhenFireStoreGetFail() = runTest {
    var resultingException: Exception? = null

    `when`(mockFirestoreIngredientRepository.get(any(), any(), any())).thenAnswer { invocation ->
      val onFailure = invocation.arguments[2] as (Exception) -> Unit
      onFailure(Exception("Error"))
    }

    aggregatorIngredientRepository.get(
        barCode = 12345L,
        onSuccess = { returnedIngredient ->
          // Assert the ingredient from OpenFoodFacts is immediately returned
          fail("Should not reach onSuccess: $returnedIngredient")
        },
        onFailure = { exception -> resultingException = exception })

    assertNotNull(resultingException)
  }

  @Test
  fun testGetFailWhenOpenFoodFactsGetFail() = runTest {
    var resultingException: Exception? = null

    `when`(mockFirestoreIngredientRepository.get(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as (Ingredient?) -> Unit
      onSuccess(null)
    }
    `when`(mockOpenFoodFactsIngredientRepository.get(any(), any(), any())).thenAnswer { invocation
      ->
      val onFailure = invocation.arguments[2] as (Exception) -> Unit
      onFailure(Exception("Error"))
    }

    aggregatorIngredientRepository.get(
        barCode = 12345L,
        onSuccess = { returnedIngredient ->
          // Assert the ingredient from OpenFoodFacts is immediately returned
          fail("Should not reach onSuccess: $returnedIngredient")
        },
        onFailure = { exception -> resultingException = exception })

    assertNotNull(resultingException)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testGetFailWhenAddFirestoreFail() = runTest {
    var resultingException: Exception? = null
    `when`(mockFirestoreIngredientRepository.get(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as (Ingredient?) -> Unit
      onSuccess(null)
    }
    `when`(mockOpenFoodFactsIngredientRepository.get(any(), any(), any())).thenAnswer { invocation
      ->
      val onSuccess = invocation.arguments[1] as (Ingredient?) -> Unit
      onSuccess(ingredient)
    }

    // Mock uploadAndRetrieveUrlAsync for 3 images format
    ingredient.images.forEach { (format, url) ->
      `when`(
              mockImageUploader.uploadAndRetrieveUrlAsync(
                  ingredient, format, mockImageRepository, dispatcher))
          .thenReturn(format to url)
    }

    `when`(mockFirestoreIngredientRepository.add(eq(ingredient), any(), any())).thenAnswer {
        invocation ->
      val onFailure = invocation.arguments[2] as (Exception) -> Unit
      onFailure(Exception("Error"))
    }

    aggregatorIngredientRepository.get(
        barCode = 12345L,
        onSuccess = { returnedIngredient ->
          // Assert the ingredient from OpenFoodFacts is immediately returned
          assertEquals(ingredient, returnedIngredient)
        },
        onFailure = { exception ->
          println("exception: $exception")
          resultingException = exception
        })

    // Wait for background process
    advanceUntilIdle()
    println(resultingException)
    assertNull(resultingException)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun testGetFailWhenRetrieveUrlFail() = runTest {
    var resultingException: Exception? = null
    `when`(mockFirestoreIngredientRepository.get(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as (Ingredient?) -> Unit
      onSuccess(null)
    }
    `when`(mockOpenFoodFactsIngredientRepository.get(any(), any(), any())).thenAnswer { invocation
      ->
      val onSuccess = invocation.arguments[1] as (Ingredient?) -> Unit
      onSuccess(ingredient)
    }

    // Mock uploadAndRetrieveUrlAsync for 3 images format
    ingredient.images.forEach { (format, url) ->
      `when`(
              mockImageUploader.uploadAndRetrieveUrlAsync(
                  ingredient, format, mockImageRepository, dispatcher))
          .thenThrow((RuntimeException("upload failed")))
    }

    `when`(mockFirestoreIngredientRepository.add(eq(ingredient), any(), any())).thenAnswer {
        invocation ->
      val onFailure = invocation.arguments[2] as (Exception) -> Unit
      onFailure(Exception("Error"))
    }

    aggregatorIngredientRepository.get(
        barCode = 12345L,
        onSuccess = { returnedIngredient ->
          // Assert the ingredient from OpenFoodFacts is immediately returned
          assertEquals(ingredient, returnedIngredient)
        },
        onFailure = { exception -> resultingException = exception })

    // Wait for background process
    advanceUntilIdle()

    assertNull(resultingException)
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

    onFailureCapture.value.invoke(Exception())

    assertNotNull(resultingException)
    assertNull(resultingIngredient)
  }

  @Test
  fun testSearchReturnsCorrectIngredientsUsingOpenFoodFactsRepo() {
    var resultingIngredient: Ingredient? = null
    var resultingException: Exception? = null

    aggregatorIngredientRepository.search(
        ingredient.name,
        onSuccess = { resultingIngredient = it[0] },
        onFailure = { resultingException = it },
        count = 1)

    onSuccessCollectionCapture.value.invoke(listOf(ingredient))

    assertNull(resultingException)
    assertNotNull(resultingIngredient)
    assertEquals(ingredient, resultingIngredient)
  }
}
