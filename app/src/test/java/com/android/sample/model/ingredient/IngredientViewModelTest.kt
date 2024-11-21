package com.android.sample.model.ingredient

import androidx.test.core.app.ApplicationProvider
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_NORMAL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_SMALL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL_URL
import com.android.sample.ui.utils.testIngredients
import com.google.firebase.Firebase
import com.google.firebase.initialize
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
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

  @Before
  fun setUp() {
    ingredientRepository = mock(IngredientRepository::class.java)
    ingredientViewModel = IngredientViewModel(ingredientRepository)

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
    assertEquals(ingredient, ingredientViewModel.ingredient.value)
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
    assertNull(ingredientViewModel.ingredient.value)
  }

  @Test
  fun fetchIngredient_withSameBarcode_doesNotCallRepository() {
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
    // Call fetchIngredient again with the same barcode
    ingredientViewModel.fetchIngredient(barCode)

    // Verify that the repository's get method was only called once
    verify(ingredientRepository, times(1)).get(eq(barCode), any(), any())
  }

  @Test
  fun addIngredient_updatesIngredientList() {
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

    // Verify that the ingredient list now contains the added ingredient
    assertTrue(ingredientViewModel.ingredientList.value.contains(ingredient))
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
    assertEquals(ingredientList, ingredientViewModel.searchingIngredientList.value)
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
    assertTrue(ingredientViewModel.ingredientList.value.contains(ingredient2))
    assertTrue(!ingredientViewModel.ingredientList.value.contains(ingredient1))
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
    assertEquals(ingredientList, ingredientViewModel.searchingIngredientList.value)

    // Clear search and verify that the searchingIngredientList is empty
    ingredientViewModel.clearSearch()
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
        ingredientViewModel.ingredientList.value.find { it.barCode == ingredient.barCode }
    assertNotNull(updatedIngredient)
    assertEquals(newQuantity, updatedIngredient?.quantity)
  }

  @Test
  fun testFactory() {
    val factory = IngredientViewModel.Factory
    val ingredientViewModel = factory.create(IngredientViewModel::class.java)
    assertNotNull(ingredientViewModel)
    assertTrue(ingredientViewModel is IngredientViewModel)
  }
}
