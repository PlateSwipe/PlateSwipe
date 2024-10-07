package com.android.sample.model.ingredient

import okhttp3.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OpenFoodFactsIngredientRepositoryTest() {

  private lateinit var client: OkHttpClient

  private lateinit var repository: OpenFoodFactsIngredientRepository

  @Before
  fun setUp() {
    client = OkHttpClient()
    repository = OpenFoodFactsIngredientRepository(client)
  }

  @Test
  fun testGetReturnsValue() {
    val barCode = 4008400290126
    var ingredient: Ingredient? = null
    repository.get(barCode, { ingredient = it }, { throw it })
    Thread.sleep(1000)
    assertEquals("Kinder Pingui 30", ingredient?.name)
  }

  @Test
  fun testSearchReturnsValue() {
    val name = "a"
    val searchLength = 5

    var ingredients: List<Ingredient>
    repository.search(
        name,
        {
          ingredients = it
          assertEquals(searchLength, ingredients.count())
        },
        { throw it },
        count = searchLength)
  }

  @Test
  fun testSearchReturnsSpecificProduct() {
    val name = "Crispy De Poulet MBudget - Migros"

    repository.search(name, { assert(it.first().name.contains(name)) }, { throw it }, count = 1)
  }
}
