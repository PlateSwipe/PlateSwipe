package com.android.sample.model.recipe

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class RecipeBuilderTest {

  private lateinit var builder: RecipeBuilder

  @Before
  fun setUp() {
    builder = RecipeBuilder()
  }

  @Test
  fun `test addIngredient adds ingredient and measurement`() {
    builder.addIngredient("Flour", "200g")
    builder.addIngredient("Sugar", "100g")

    val recipe =
        builder
            .apply {
              strMeal = "Cake"
              strInstructions = "Mix all ingredients"
            }
            .build()

    assertEquals(listOf("Flour" to "200g", "Sugar" to "100g"), recipe.ingredientsAndMeasurements)
  }

  @Test
  fun `test build with all required fields set`() {
    val recipe =
        builder
            .apply {
              strMeal = "Pasta"
              strInstructions = "Boil water, cook pasta"
              addIngredient("Pasta", "200g")
              addIngredient("Salt", "1 tsp")
            }
            .build()

    assertEquals("Pasta", recipe.strMeal)
    assertEquals("Boil water, cook pasta", recipe.strInstructions)
    assertEquals("200g", recipe.ingredientsAndMeasurements[0].second)
  }

  @Test
  fun `test build throws exception when strMeal is blank`() {
    builder.strInstructions = "Instructions for the recipe"
    builder.addIngredient("Ingredient", "Measurement")

    val exception = assertThrows(IllegalArgumentException::class.java) { builder.build() }
    assertEquals("Recipe name cannot be blank", exception.message)
  }

  @Test
  fun `test build throws exception when strInstructions is blank`() {
    builder.strMeal = "Recipe name"
    builder.addIngredient("Ingredient", "Measurement")

    val exception = assertThrows(IllegalArgumentException::class.java) { builder.build() }
    assertEquals("Recipe instructions cannot be blank", exception.message)
  }

  @Test
  fun `test build throws exception when no ingredients are added`() {
    builder.strMeal = "Recipe name"
    builder.strInstructions = "Recipe instructions"

    val exception = assertThrows(IllegalArgumentException::class.java) { builder.build() }
    assertEquals("Ingredients and measurements must not be empty", exception.message)
  }

  @Test
  fun `test optional fields are set correctly`() {
    val recipe =
        builder
            .apply {
              strMeal = "Salad"
              strInstructions = "Mix ingredients"
              addIngredient("Lettuce", "100g")
              strCategory = "Vegetarian"
              strArea = "French"
              strMealThumbUrl = "http://example.com/image.jpg"
              time = "15 mins"
              difficulty = "Easy"
              price = "5.00"
            }
            .build()

    assertEquals("Vegetarian", recipe.strCategory)
    assertEquals("French", recipe.strArea)
    assertEquals("http://example.com/image.jpg", recipe.strMealThumbUrl)
    assertEquals("15 mins", recipe.time)
    assertEquals("Easy", recipe.difficulty)
    assertEquals("5.00", recipe.price)
  }
}
