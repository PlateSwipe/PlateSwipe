package com.android.sample.model.ingredient

import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.image.ImageDownload
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_NORMAL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_SMALL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL_URL
import com.android.sample.ui.utils.testIngredients
import com.android.sample.ui.utils.testIngredientsNullCategory
import com.google.firebase.Firebase
import com.google.firebase.initialize
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
/** Ingredient view model test */
class IngredientViewModelTest {
  private lateinit var ingredientViewModel: IngredientViewModel
  private lateinit var ingredientRepository: IngredientRepository
  private lateinit var imageDownload: ImageDownload

  @Before
  fun setUp() {
    ingredientRepository = mock(IngredientRepository::class.java)
    imageDownload = mock(ImageDownload::class.java)
    ingredientViewModel = IngredientViewModel(ingredientRepository, imageDownload)

    Firebase.initialize(ApplicationProvider.getApplicationContext())
  }

  @Test
  fun fetchIngredient_withNewBarcode_updatesIngredient() {
    val barCode = 123456L
    val ingredient = testIngredients[0].copy(barCode = barCode)

    `when`(ingredientRepository.get(eq(barCode), any(), any())).thenAnswer { invocation ->
      val onSuccess: (Ingredient?) -> Unit = invocation.getArgument(1)
      onSuccess(ingredient)
    }

    ingredientViewModel.fetchIngredient(barCode)
    verify(ingredientRepository).get(eq(barCode), any(), any())
    assertEquals(ingredient, ingredientViewModel.ingredient.value.first)
  }

  @Test
  fun fetchIngredient_withInvalidBarcode_setsIngredientToNull() {
    val barCode = 123456L

    `when`(ingredientRepository.get(eq(barCode), any(), any())).thenAnswer { invocation ->
      val onFailure: (Exception) -> Unit = invocation.getArgument(2)
      onFailure(Exception("Ingredient not found"))
    }

    ingredientViewModel.fetchIngredient(barCode)
    verify(ingredientRepository).get(eq(barCode), any(), any())
    assertNull(ingredientViewModel.ingredient.value.first)
  }

  @Test
  fun fetchIngredient_withSameBarcode_doesNotCallRepository() {
    // Mock the repository to call onSuccess with the ingredient
    `when`(ingredientRepository.get(eq(testIngredients[0].barCode!!), any(), any())).thenAnswer {
        invocation ->
      val onSuccess: (Ingredient?) -> Unit = invocation.getArgument(1)
      onSuccess(testIngredients[0])
    }

    ingredientViewModel.fetchIngredient(testIngredients[0].barCode!!)
    // Call fetchIngredient again with the same barcode
    ingredientViewModel.fetchIngredient(testIngredients[0].barCode!!)

    // Verify that the repository's get method was only called once
    verify(ingredientRepository, times(1)).get(eq(testIngredients[0].barCode!!), any(), any())
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun addIngredient_updatesIngredientList() = runTest {
    ingredientViewModel.addIngredient(testIngredients[0])
    advanceUntilIdle()

    assertTrue(
        ingredientViewModel.ingredientList.value.contains(
            Pair(testIngredients[0], testIngredients[0].quantity)))
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun addIngredient_updatesIngredientListTwoTimes() = runTest {
    val ingredient =
        Ingredient(
            barCode = 123456L,
            name = "New Ingredient",
            brands = "Brand",
            quantity = "100g",
            categories = listOf("Category1"),
            images =
                mutableMapOf(
                    PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                    PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))

    ingredientViewModel.addIngredient(ingredient)
    ingredientViewModel.addIngredient(ingredient)

    advanceUntilIdle()

    assertTrue(ingredientViewModel.ingredientList.value.contains(Pair(ingredient, "200g")))
  }

  @Test
  fun fetchIngredientByName_withValidName_updatesSearchingIngredientList() {
    val name = "Test Ingredient"
    val ingredientList =
        listOf(
            Ingredient(
                barCode = 123456L,
                name = name,
                brands = "Brand1",
                quantity = "",
                categories = listOf(),
                images = mutableMapOf()),
            Ingredient(
                barCode = 789012L,
                name = "Another Ingredient",
                brands = "Brand2",
                quantity = "",
                categories = listOf(),
                images = mutableMapOf()))

    `when`(ingredientRepository.search(any(), any(), any(), any())).thenAnswer { invocation ->
      val onSuccess: (List<Ingredient>) -> Unit = invocation.getArgument(1)
      onSuccess(ingredientList)
    }

    ingredientViewModel.fetchIngredientByName(name)
    verify(ingredientRepository).search(any(), any(), any(), any())

    // Verify that the searchingIngredientList contains the expected ingredients
    assertEquals(ingredientList, ingredientViewModel.searchingIngredientList.value.map { it.first })
  }

  @Test
  fun fetchIngredientByName_withInvalidName_setsSearchingIngredientListToEmpty() {
    val name = "Invalid Ingredient"

    `when`(ingredientRepository.search(any(), any(), any(), any())).thenAnswer { invocation ->
      val onFailure: (Exception) -> Unit = invocation.getArgument(2)
      onFailure(Exception("Ingredient not found"))
    }

    ingredientViewModel.fetchIngredientByName(name)
    verify(ingredientRepository).search(any(), any(), any(), any())

    // Verify that searchingIngredientList is empty
    assertTrue(ingredientViewModel.searchingIngredientList.value.isEmpty())
  }

  @Test
  fun removeIngredient_updatesIngredientList() {
    val ingredient1 =
        Ingredient(
            barCode = 123456L,
            name = "Ingredient1",
            brands = "Brand1",
            quantity = "100g",
            categories = listOf("Category1"),
            images = mutableMapOf())
    val ingredient2 =
        Ingredient(
            barCode = 789012L,
            name = "Ingredient2",
            brands = "Brand2",
            quantity = "200g",
            categories = listOf("Category2"),
            images = mutableMapOf())

    // Add ingredients to the list
    ingredientViewModel.addIngredient(ingredient1)
    ingredientViewModel.addIngredient(ingredient2)

    // Remove one ingredient and verify it is no longer in the list
    ingredientViewModel.removeIngredient(ingredient1)
    assertTrue(
        ingredientViewModel.ingredientList.value.contains(Pair(ingredient2, ingredient2.quantity)))
    assertTrue(
        !ingredientViewModel.ingredientList.value.contains(Pair(ingredient1, ingredient1.quantity)))
  }

  @Test
  fun clearSearch_resetsSearchingIngredientListToEmpty() {
    val ingredientList =
        listOf(
            Ingredient(
                barCode = 123456L,
                name = "Ingredient1",
                brands = "Brand1",
                quantity = "",
                categories = listOf(),
                images = mutableMapOf()),
            Ingredient(
                barCode = 789012L,
                name = "Ingredient2",
                brands = "Brand2",
                quantity = "",
                categories = listOf(),
                images = mutableMapOf()))

    // Set initial search results
    `when`(ingredientRepository.search(any(), any(), any(), any())).thenAnswer { invocation ->
      val onSuccess: (List<Ingredient>) -> Unit = invocation.getArgument(1)
      onSuccess(ingredientList)
    }
    ingredientViewModel.fetchIngredientByName("Ingredient")
    assertEquals(ingredientList, ingredientViewModel.searchingIngredientList.value.map { it.first })

    // Clear search and verify that the searchingIngredientList is empty
    ingredientViewModel.clearSearchingIngredientList()
    assertTrue(ingredientViewModel.searchingIngredientList.value.isEmpty())
  }

  @Test
  fun updateQuantity_updatesIngredientQuantity() {
    // Create an initial ingredient
    val ingredient =
        Ingredient(
            barCode = 123456L,
            name = "Test Ingredient",
            brands = "Brand",
            quantity = "100g", // Initial quantity
            categories = listOf("Category1"),
            images =
                mutableMapOf(
                    PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                    PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))

    // Add the ingredient to the ingredient list
    ingredientViewModel.addIngredient(ingredient)

    // Update the quantity of the ingredient
    val newQuantity = "200g"
    ingredientViewModel.updateQuantity(ingredient, newQuantity)

    // Verify that the ingredient list contains the ingredient with the updated quantity
    val updatedIngredient =
        ingredientViewModel.ingredientList.value.find { it.first.barCode == ingredient.barCode }
    assertNotNull(updatedIngredient)
    assertEquals(newQuantity, updatedIngredient?.second)
  }

  @Test
  fun testFactory() {
    val factory =
        IngredientViewModel.provideFactory(context = ApplicationProvider.getApplicationContext())
    val ingredientViewModel = factory.create(IngredientViewModel::class.java)
    assertNotNull(ingredientViewModel)
  }

  @Test
  fun clearTest() {

    val barCode = 123456L
    val ingredient =
        Ingredient(
            barCode = barCode,
            name = "Test Ingredient",
            brands = null,
            quantity = "",
            categories = listOf(""),
            images =
                mutableMapOf(
                    PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                    PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))

    // Mock the repository to call onSuccess with the ingredient
    `when`(ingredientRepository.get(eq(barCode), any(), any())).thenAnswer { invocation ->
      val onSuccess: (Ingredient?) -> Unit = invocation.getArgument(1)
      onSuccess(ingredient)
    }

    ingredientViewModel.fetchIngredient(barCode)
    ingredientViewModel.clearSearchingIngredientList()
    assertTrue(ingredientViewModel.searchingIngredientList.value.isEmpty())
  }

  @Test
  fun clearIngredientListTest() {
    ingredientViewModel.addIngredient(testIngredients[0])
    ingredientViewModel.clearIngredientList()
    assertTrue(ingredientViewModel.ingredientList.value.isEmpty())
  }

  @Test
  fun addNullIngredientTest() {
    // Add the ingredient to the ingredient list
    ingredientViewModel.addIngredient(testIngredientsNullCategory[0])
    ingredientViewModel.addIngredient(testIngredientsNullCategory[0])

    // Verify that the ingredient list contains the ingredient with the updated quantity
    val updatedIngredient =
        ingredientViewModel.ingredientList.value.find {
          it.first.barCode == testIngredientsNullCategory[0].barCode
        }
    assertNotNull(updatedIngredient)
    assertEquals("", updatedIngredient?.second)
  }

  @Test
  fun addACorrectIngredientAndANullIngredientTest() {
    // Add the ingredient to the ingredient list
    ingredientViewModel.addIngredient(testIngredients[0])
    ingredientViewModel.addIngredient(testIngredientsNullCategory[0])

    // Verify that the ingredient list contains the ingredient with the updated quantity
    val updatedIngredient =
        ingredientViewModel.ingredientList.value.find {
          it.first.barCode == testIngredients[0].barCode
        }
    assertNotNull(updatedIngredient)
    assertEquals(testIngredients[0].quantity, updatedIngredient?.second)
  }

  @Test
  fun addANullIngredientAndACorrectIngredientTest() {
    // Add the ingredient to the ingredient list
    ingredientViewModel.addIngredient(testIngredientsNullCategory[0])
    ingredientViewModel.addIngredient(testIngredients[0])

    // Verify that the ingredient list contains the ingredient with the updated quantity
    val updatedIngredient =
        ingredientViewModel.ingredientList.value.find {
          it.first.barCode == testIngredients[0].barCode
        }
    assertNotNull(updatedIngredient)
    assertEquals(testIngredients[0].quantity, updatedIngredient?.second)
  }

  @Test
  fun addAFalseIngredientAndACorrectIngredientTest() {
    // Create an initial ingredient
    val ingredientCorrect =
        Ingredient(
            barCode = 123456L,
            name = "Test Ingredient",
            brands = "Brand",
            quantity = "100g",
            categories = listOf("Category1"),
            images =
                mutableMapOf(
                    PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                    PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))
    val ingredientFalse =
        Ingredient(
            barCode = 123456L,
            name = "Test Ingredient",
            brands = "Brand",
            quantity = "g",
            categories = listOf("Category1"),
            images =
                mutableMapOf(
                    PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                    PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))

    // Add the ingredient to the ingredient list
    ingredientViewModel.addIngredient(ingredientFalse)
    ingredientViewModel.addIngredient(ingredientCorrect)

    // Verify that the ingredient list contains the ingredient with the updated quantity
    val updatedIngredient =
        ingredientViewModel.ingredientList.value.find {
          it.first.barCode == ingredientCorrect.barCode
        }
    assertNotNull(updatedIngredient)
    assertEquals("100g", updatedIngredient?.second)
  }

  @Test
  fun addACorrectIngredientAndAFalseIngredientTest() {
    // Create an initial ingredient
    val ingredientCorrect =
        Ingredient(
            barCode = 123456L,
            name = "Test Ingredient",
            brands = "Brand",
            quantity = "100g",
            categories = listOf("Category1"),
            images =
                mutableMapOf(
                    PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                    PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))
    val ingredientFalse =
        Ingredient(
            barCode = 123456L,
            name = "Test Ingredient",
            brands = "g",
            categories = listOf("Category1"),
            images =
                mutableMapOf(
                    PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                    PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))

    // Add the ingredient to the ingredient list
    ingredientViewModel.addIngredient(ingredientCorrect)
    ingredientViewModel.addIngredient(ingredientFalse)

    // Verify that the ingredient list contains the ingredient with the updated quantity
    val updatedIngredient =
        ingredientViewModel.ingredientList.value.find {
          it.first.barCode == ingredientCorrect.barCode
        }
    assertNotNull(updatedIngredient)
    assertEquals("100g", updatedIngredient?.second)
  }

  @Test
  fun addNullRegexIngredientTest() {
    // Create an initial ingredient
    val ingredient =
        Ingredient(
            barCode = 123456L,
            name = "Test Ingredient",
            brands = "Brand",
            quantity = "s",
            categories = listOf("Category1"),
            images =
                mutableMapOf(
                    PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                    PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))

    // Add the ingredient to the ingredient list
    ingredientViewModel.addIngredient(ingredient)
    ingredientViewModel.addIngredient(ingredient)

    // Verify that the ingredient list contains the ingredient with the updated quantity
    val updatedIngredient =
        ingredientViewModel.ingredientList.value.find { it.first.barCode == ingredient.barCode }
    assertNotNull(updatedIngredient)
    assertEquals("s", updatedIngredient?.second)
  }

  @Test
  fun addIngredientWithPointRegexIngredientTest() {
    // Create an initial ingredient
    val ingredient =
        Ingredient(
            barCode = 123456L,
            name = "Test Ingredient",
            brands = "Brand",
            quantity = "1.5",
            categories = listOf("Category1"),
            images =
                mutableMapOf(
                    PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                    PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))

    // Add the ingredient to the ingredient list
    ingredientViewModel.addIngredient(ingredient)
    ingredientViewModel.addIngredient(ingredient)

    // Verify that the ingredient list contains the ingredient with the updated quantity
    val updatedIngredient =
        ingredientViewModel.ingredientList.value.find { it.first.barCode == ingredient.barCode }
    assertNotNull(updatedIngredient)
    assertEquals("3", updatedIngredient?.second)
  }

  @Test
  fun addTwoIngredientWithCommaRegexIngredientTest() {
    // Create an initial ingredient
    val ingredient =
        Ingredient(
            barCode = 123456L,
            name = "Test Ingredient",
            brands = "Brand",
            quantity = "1,5",
            categories = listOf("Category1"),
            images =
                mutableMapOf(
                    PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                    PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))

    // Add the ingredient to the ingredient list
    ingredientViewModel.addIngredient(ingredient)
    ingredientViewModel.addIngredient(ingredient)

    // Verify that the ingredient list contains the ingredient with the updated quantity
    val updatedIngredient =
        ingredientViewModel.ingredientList.value.find { it.first.barCode == ingredient.barCode }
    assertNotNull(updatedIngredient)
    assertEquals("3", updatedIngredient?.second)
  }

  @Test
  fun addOneIngredientWithCommaRegexIngredientTest() {
    // Create an initial ingredient
    val ingredient =
        Ingredient(
            barCode = 123456L,
            name = "Test Ingredient",
            brands = "Brand",
            quantity = "1,5",
            categories = listOf("Category1"),
            images =
                mutableMapOf(
                    PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                    PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))

    // Add the ingredient to the ingredient list
    ingredientViewModel.addIngredient(ingredient)

    // Verify that the ingredient list contains the ingredient with the updated quantity
    val updatedIngredient =
        ingredientViewModel.ingredientList.value.find { it.first.barCode == ingredient.barCode }
    assertNotNull(updatedIngredient)
    assertEquals("1.5", updatedIngredient?.second)
  }

  @Test
  fun addIngredientWithMultipleCommaRegexIngredientTest() {
    // Create an initial ingredient
    val ingredient =
        Ingredient(
            barCode = 123456L,
            name = "Test Ingredient",
            brands = "Brand",
            quantity = "1,5 ingredient, pasta",
            categories = listOf("Category1"),
            images =
                mutableMapOf(
                    PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                    PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))

    // Add the ingredient to the ingredient list
    ingredientViewModel.addIngredient(ingredient)

    // Verify that the ingredient list contains the ingredient with the updated quantity
    val updatedIngredient =
        ingredientViewModel.ingredientList.value.find { it.first.barCode == ingredient.barCode }
    assertNotNull(updatedIngredient)
    assertEquals("1.5 ingredient, pasta", updatedIngredient?.second)
  }

  @Test
  fun addTwoIngredientWithCommaAndNoRegexIngredientTest() {
    // Create an initial ingredient
    val ingredient1 =
        Ingredient(
            barCode = 123456L,
            name = "Test Ingredient",
            brands = "Brand",
            quantity = "1,5",
            categories = listOf("Category1"),
            images =
                mutableMapOf(
                    PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                    PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))

    val ingredient2 =
        Ingredient(
            barCode = 123456L,
            name = "Test Ingredient",
            brands = "Brand",
            quantity = "1",
            categories = listOf("Category1"),
            images =
                mutableMapOf(
                    PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                    PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))

    // Add the ingredient to the ingredient list
    ingredientViewModel.addIngredient(ingredient1)
    ingredientViewModel.addIngredient(ingredient2)

    // Verify that the ingredient list contains the ingredient with the updated quantity
    val updatedIngredient =
        ingredientViewModel.ingredientList.value.find { it.first.barCode == ingredient1.barCode }
    assertNotNull(updatedIngredient)
    assertEquals("2.5", updatedIngredient?.second)
  }

  @Test
  fun addIngredientWithPointAndNoRegexIngredientTest() {
    // Create an initial ingredient
    val ingredient1 =
        Ingredient(
            barCode = 123456L,
            name = "Test Ingredient",
            brands = "Brand",
            quantity = "1.5",
            categories = listOf("Category1"),
            images =
                mutableMapOf(
                    PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                    PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))

    val ingredient2 =
        Ingredient(
            barCode = 123456L,
            name = "Test Ingredient",
            brands = "Brand",
            quantity = "1",
            categories = listOf("Category1"),
            images =
                mutableMapOf(
                    PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                    PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))

    // Add the ingredient to the ingredient list
    ingredientViewModel.addIngredient(ingredient1)
    ingredientViewModel.addIngredient(ingredient2)

    // Verify that the ingredient list contains the ingredient with the updated quantity
    val updatedIngredient =
        ingredientViewModel.ingredientList.value.find { it.first.barCode == ingredient1.barCode }
    assertNotNull(updatedIngredient)
    assertEquals("2.5", updatedIngredient?.second)
  }

  @Test
  fun clearIngredientTest() {
    val barCode = 123456L
    val ingredient = testIngredients[0].copy(barCode = barCode)

    `when`(ingredientRepository.get(eq(barCode), any(), any())).thenAnswer { invocation ->
      val onSuccess: (Ingredient?) -> Unit = invocation.getArgument(1)
      onSuccess(ingredient)
    }

    ingredientViewModel.fetchIngredient(barCode)
    verify(ingredientRepository).get(eq(barCode), any(), any())
    assertEquals(ingredient, ingredientViewModel.ingredient.value.first)

    ingredientViewModel.clearIngredient()
    assertEquals(Pair(null, null), ingredientViewModel.ingredient.value)
  }

  @Test
  fun getAllDownloadedIngredientsSucces() {
    val downloadedIngredients = testIngredients
    `when`(ingredientRepository.getAllDownload(any(), any())).thenAnswer { invocation ->
      val onSuccess: (List<Ingredient>) -> Unit = invocation.getArgument(0)
      onSuccess(downloadedIngredients)
    }
    ingredientViewModel.getAllDownloadedIngredients()
    assertEquals(downloadedIngredients, ingredientViewModel.ingredientDownloadList.value)
  }

  @Test
  fun getAllDownloadedIngredientsFail() {
    `when`(ingredientRepository.getAllDownload(any(), any())).thenAnswer { invocation ->
      val onFailure: (Exception) -> Unit = invocation.getArgument(1)
      onFailure(Exception("Error"))
    }
    ingredientViewModel.getAllDownloadedIngredients()
    assertTrue(ingredientViewModel.ingredientDownloadList.value.isEmpty())
  }

  @Test
  fun deleteDownloadedIngredient() {
    val ingredient = testIngredients[0]
    ingredientViewModel.deleteDownloadedIngredient(ingredient)
    verify(ingredientRepository).deleteDownload(ingredient)
  }

  @Test
  fun getIngredientTestSuccess() {
    var ingredient = testIngredients[0]
    `when`(ingredientRepository.get(eq(testIngredients[2].barCode!!), any(), any())).thenAnswer {
        invocation ->
      val onSuccess: (Ingredient?) -> Unit = invocation.getArgument(1)
      onSuccess(testIngredients[2])
    }
    ingredientViewModel.getIngredient(
        testIngredients[2].barCode!!,
        onSuccess = { ingredient = it },
        onFailure = { fail(it.message) })
    assertEquals(testIngredients[2], ingredient)
  }

  @Test
  fun getIngredientTestIngrNull() {
    var exception = false
    `when`(ingredientRepository.get(eq(testIngredients[2].barCode!!), any(), any())).thenAnswer {
        invocation ->
      val onSuccess: (Ingredient?) -> Unit = invocation.getArgument(1)
      onSuccess(null)
    }
    ingredientViewModel.getIngredient(
        testIngredients[2].barCode!!, onSuccess = { fail() }, onFailure = { exception = true })
    assert(exception)
  }

  @Test
  fun getIngredientTestRepoFail() {
    var exception = false
    `when`(ingredientRepository.get(eq(testIngredients[2].barCode!!), any(), any())).thenAnswer {
        invocation ->
      val onFailure: (Exception) -> Unit = invocation.getArgument(2)
      onFailure(Exception("Error"))
    }
    ingredientViewModel.getIngredient(
        testIngredients[2].barCode!!, onSuccess = { fail() }, onFailure = { exception = true })
    assert(exception)
  }
}
