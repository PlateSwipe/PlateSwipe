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
    builder.addIngredientAndMeasurement("Flour", "200g")
    builder.addIngredientAndMeasurement("Sugar", "100g")

    val recipe =
        builder
            .apply {
              setName("Cake")
              setInstructions("Mix all ingredients")
            }
            .build()

    assertEquals(listOf("Flour" to "200g", "Sugar" to "100g"), recipe.ingredientsAndMeasurements)
  }

  @Test
  fun `test build with all required fields set`() {
    val recipe =
        builder
            .apply {
              setName("Pasta")
              setInstructions("Boil water, cook pasta")
              addIngredientAndMeasurement("Pasta", "200g")
              addIngredientAndMeasurement("Salt", "1 tsp")
            }
            .build()

    assertEquals("Pasta", recipe.strMeal)
    assertEquals("Boil water, cook pasta", recipe.strInstructions)
    assertEquals("200g", recipe.ingredientsAndMeasurements[0].second)
  }

  @Test
  fun `test build throws exception when strMeal is blank`() {
    builder.setInstructions("Instructions for the recipe")
    builder.addIngredientAndMeasurement("Ingredient", "Measurement")

    val exception = assertThrows(IllegalArgumentException::class.java) { builder.build() }
    assertEquals("Recipe name is required and cannot be blank.", exception.message)
  }

  @Test
  fun `test build throws exception when strInstructions is blank`() {
    builder.setName("Recipe name")
    builder.addIngredientAndMeasurement("Ingredient", "Measurement")

    val exception = assertThrows(IllegalArgumentException::class.java) { builder.build() }
    assertEquals("Recipe instructions are required and cannot be blank.", exception.message)
  }

  @Test
  fun `test build throws exception when no ingredients are added`() {
    builder.setName("Recipe name")
    builder.setInstructions("Recipe instructions")

    val exception = assertThrows(IllegalArgumentException::class.java) { builder.build() }
    assertEquals("At least one ingredient is required.", exception.message)
  }

  @Test
  fun `test optional fields are set correctly`() {
    val recipe =
        builder
            .apply {
              setName("Salad")
              setInstructions("Mix ingredients")
              addIngredientAndMeasurement("Lettuce", "100g")
              setCategory("Vegetarian")
              setArea("French")
              setPictureID("http://example.com/image.jpg")
              setTime("15 mins")
              setDifficulty("Easy")
              setPrice("5.00")
            }
            .build()

    assertEquals("Vegetarian", recipe.strCategory)
    assertEquals("French", recipe.strArea)
    assertEquals("http://example.com/image.jpg", recipe.strMealThumbUrl)
    assertEquals("15 mins", recipe.time)
    assertEquals("Easy", recipe.difficulty)
    assertEquals("5.00", recipe.price)
  }

  @Test
  fun `test clear function successfully clears`() {
    builder.setName("Salad")
    builder.setInstructions("Mix ingredients")
    builder.addIngredientAndMeasurement("Lettuce", "100g")
    builder.setCategory("Vegetarian")
    builder.setArea("French")
    builder.setPictureID("http://example.com/image.jpg")
    builder.setTime("15 mins")
    builder.setDifficulty("Easy")
    builder.setPrice("5.00")

    val recipe = builder.build()
    builder.clear()

    try {
      builder.build()
    } catch (e: IllegalArgumentException) {
      assertEquals("Recipe name is required and cannot be blank.", e.message)
    }
  }

  @Test
  fun `test all getters`() {
    builder.apply {
      setName("Salad")
      setInstructions("Mix ingredients")
      addIngredientAndMeasurement("Lettuce", "100g")
      setCategory("Vegetarian")
      setArea("French")
      setPictureID("http://example.com/image.jpg")
      setTime("15 mins")
      setDifficulty("Easy")
      setPrice("5.00")
      setId("1")
    }
    assertEquals("Salad", builder.getName())
    assertEquals("Mix ingredients", builder.getInstructions())
    assertEquals("Vegetarian", builder.getCategory())
    assertEquals("French", builder.getArea())
    assertEquals("http://example.com/image.jpg", builder.getPictureID())
    assertEquals("15 mins", builder.getTime())
    assertEquals("Easy", builder.getDifficulty())
    assertEquals("5.00", builder.getPrice())
    assertEquals(listOf("Lettuce" to "100g"), builder.getIngredientsAndMeasurements())
    assertEquals("1", builder.getId())
  }

  @Test
  fun `test update ingredient and measurement`() {
    builder.addIngredientAndMeasurement("Flour", "200g")
    builder.addIngredientAndMeasurement("Sugar", "100g")
    builder.updateIngredientAndMeasurement("Flour", "200g", "Flour", "300g")
    val listOfIngredients = builder.getIngredientsAndMeasurements().toList()
    assertEquals(listOf("Flour" to "300g", "Sugar" to "100g"), listOfIngredients)
  }

  @Test
  fun `test delete ingredient and measurement`() {
    builder.addIngredientAndMeasurement("Flour", "200g")
    builder.addIngredientAndMeasurement("Sugar", "100g")
    builder.deleteIngredientAndMeasurement("Flour", "200g")
    val listOfIngredients = builder.getIngredientsAndMeasurements()
    assertEquals(listOf("Sugar" to "100g"), listOfIngredients)
  }
}
