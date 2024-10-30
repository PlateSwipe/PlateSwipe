package com.android.sample.model.ingredient

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

/** Ingredient view model test */
class IngredientViewModelTest {
  private lateinit var ingredientViewModel: IngredientViewModel
  private lateinit var ingredientRepository: IngredientRepository

  @Before
  fun setUp() {
    ingredientRepository = mock(IngredientRepository::class.java)
    ingredientViewModel = IngredientViewModel(ingredientRepository)
  }

  @Test
  fun fetchIngredient_withNewBarcode_updatesIngredient() {
    val barCode = 123456L
    val ingredient = Ingredient(barCode, "Test Ingredient", null, null)

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
    val ingredient = Ingredient(barCode, "Test Ingredient", null, null)

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
  fun factory_createsIngredientViewModel() {
    val factory = IngredientViewModel.Factory
    val viewModel = factory.create(IngredientViewModel::class.java)
    assertTrue(viewModel is IngredientViewModel)
    // Check if the viewModel can call fetchIngredient
    val barCode = 123456L
    viewModel.fetchIngredient(barCode)
  }
}
