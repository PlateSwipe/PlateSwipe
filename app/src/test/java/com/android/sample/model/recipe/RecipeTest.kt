package com.android.sample.model.recipe

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test

class RecipeTest {

  @Test
  fun `create Recipe with valid data`() {
    // Arrange
    val idMeal = "1"
    val strMeal = "Spicy Arrabiata Penne"
    val strCategory = "Vegetarian"
    val strArea = "Italian"
    val strInstructions = "Instructions here..."
    val strMealThumbUrl = "https://www.recipetineats.com/penne-all-arrabbiata-spicy-tomato-pasta/"
    val ingredientsAndMeasurements = listOf(Pair("Penne", "1 pound"), Pair("Olive oil", "1/4 cup"))

    // Act
    val recipe =
        Recipe(
            idMeal = idMeal,
            strMeal = strMeal,
            strCategory = strCategory,
            strArea = strArea,
            strInstructions = strInstructions,
            strMealThumbUrl = strMealThumbUrl,
            ingredientsAndMeasurements = ingredientsAndMeasurements)

    // Assert
    assertThat(recipe.idMeal, `is`(idMeal))
    assertThat(recipe.strMeal, `is`(strMeal))
    assertThat(recipe.strCategory, `is`(strCategory))
    assertThat(recipe.strArea, `is`(strArea))
    assertThat(recipe.strInstructions, `is`(strInstructions))
    assertThat(recipe.strMealThumbUrl, `is`(strMealThumbUrl))
    assertThat(recipe.ingredientsAndMeasurements, `is`(ingredientsAndMeasurements))
  }

  @Test
  fun `create Recipe with nullable properties`() {
    // Arrange
    val idMeal = "2"
    val strMeal = "Chicken Curry"
    val strInstructions = "Instructions here..."
    val strMealThumbUrl = "https://www.foodfashionparty.com/2023/08/05/everyday-chicken-curry/"
    val ingredientsAndMeasurements =
        listOf(Pair("Chicken", "1 pound"), Pair("Curry powder", "2 tbsp"))

    // Act
    val recipe =
        Recipe(
            idMeal = idMeal,
            strMeal = strMeal,
            strCategory = null, // Nullable
            strArea = null, // Nullable
            strInstructions = strInstructions,
            strMealThumbUrl = strMealThumbUrl,
            ingredientsAndMeasurements = ingredientsAndMeasurements)

    // Assert
    assertThat(recipe.idMeal, `is`(idMeal))
    assertThat(recipe.strMeal, `is`(strMeal))
    assertThat(recipe.strCategory, `is`(nullValue())) // Use nullValue() matcher
    assertThat(recipe.strArea, `is`(nullValue())) // Use nullValue() matcher
    assertThat(recipe.strInstructions, `is`(strInstructions))
    assertThat(recipe.strMealThumbUrl, `is`(strMealThumbUrl))
    assertThat(recipe.ingredientsAndMeasurements, `is`(ingredientsAndMeasurements))
  }

  @Test
  fun `create Recipe with empty ingredientsAndMeasurements list`() {
    // Arrange
    val idMeal = "3"
    val strMeal = "Empty Ingredients Test"
    val strInstructions = "Instructions here..."
    val strMealThumbUrl = "https://example.com/empty-ingredients-test"

    // Act & Assert
    val exception =
        assertThrows(IllegalArgumentException::class.java) {
          Recipe(
              idMeal = idMeal,
              strMeal = strMeal,
              strCategory = null,
              strArea = null,
              strInstructions = strInstructions,
              strMealThumbUrl = strMealThumbUrl,
              ingredientsAndMeasurements = emptyList())
        }
    assertThat(exception.message, `is`("Ingredients and measurements must not be empty"))
  }
}
