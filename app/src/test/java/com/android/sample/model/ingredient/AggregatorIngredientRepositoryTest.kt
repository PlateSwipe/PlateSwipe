package com.android.sample.model.ingredient

import com.android.sample.ui.utils.testIngredients
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class AggregatorIngredientRepositoryTest {
  @Mock private lateinit var mockFirestoreIngredientRepository: FirestoreIngredientRepository

  @Mock
  private lateinit var mockOpenFoodFactsIngredientRepository: OpenFoodFactsIngredientRepository

  @Captor private lateinit var onSuccessSingleCapture: ArgumentCaptor<Function1<Ingredient?, Unit>>
  @Captor
  private lateinit var onSuccessCollectionCapture: ArgumentCaptor<Function1<List<Ingredient>, Unit>>
  @Captor private lateinit var onFailureCapture: ArgumentCaptor<Function1<Exception, Unit>>

  private lateinit var aggregatorIngredientRepository: AggregatorIngredientRepository

  private val ingredient = testIngredients[0]

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
            mockFirestoreIngredientRepository, mockOpenFoodFactsIngredientRepository)
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
  fun testGetFindsFromOpenFoodFactsRepoWhenNotFoundInFirestore() {
    var resultingIngredient: Ingredient? = null
    var resultingException: Exception? = null

    aggregatorIngredientRepository.get(
        ingredient.barCode!!,
        onSuccess = { resultingIngredient = it },
        onFailure = { resultingException = it })

    onSuccessSingleCapture.value.invoke(null)

    onSuccessSingleCapture.value.invoke(ingredient)

    verify(mockFirestoreIngredientRepository)
        .add(any<Ingredient>(), onSuccess = any(), onFailure = any())

    assertNull(resultingException)
    assertNotNull(resultingIngredient)
    assertEquals(ingredient, resultingIngredient)
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
}
