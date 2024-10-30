package com.android.sample.model.recipe

import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_AREA
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_CATEGORY
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_DIFFICULTY
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_INGREDIENTS
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_INSTRUCTIONS
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_MEASUREMENTS
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_NAME
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_PICTURE_ID
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_PRICE
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_TIME
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

  fun `convert Recipe to Firestore-compatible map`() {
    // Arrange
    val idMeal = "1"
    val strMeal = "Spicy Arrabiata Penne"
    val strCategory = "Vegetarian"
    val strArea = "Italian"
    val strInstructions = "Instructions here..."
    val strMealThumbUrl = "https://www.recipetineats.com/penne-all-arrabbiata-spicy-tomato-pasta/"
    val ingredientsAndMeasurements = listOf(Pair("Penne", "1 pound"), Pair("Olive oil", "1/4 cup"))
    val time = "30 minutes"
    val difficulty = "Medium"
    val price = "$10"

    val recipe =
        Recipe(
            idMeal = idMeal,
            strMeal = strMeal,
            strCategory = strCategory,
            strArea = strArea,
            strInstructions = strInstructions,
            strMealThumbUrl = strMealThumbUrl,
            ingredientsAndMeasurements = ingredientsAndMeasurements,
            time = time,
            difficulty = difficulty,
            price = price)

    // Act
    val firestoreMap = recipe.toFirestoreMap()

    // Assert
    assertThat(firestoreMap[FIRESTORE_RECIPE_NAME], `is`(strMeal))
    assertThat(firestoreMap[FIRESTORE_RECIPE_CATEGORY], `is`(strCategory))
    assertThat(firestoreMap[FIRESTORE_RECIPE_AREA], `is`(strArea))
    assertThat(firestoreMap[FIRESTORE_RECIPE_PICTURE_ID], `is`(strMealThumbUrl))
    assertThat(firestoreMap[FIRESTORE_RECIPE_INSTRUCTIONS], `is`(strInstructions))
    assertThat(
        firestoreMap[FIRESTORE_RECIPE_INGREDIENTS],
        `is`(ingredientsAndMeasurements.map { it.first }))
    assertThat(
        firestoreMap[FIRESTORE_RECIPE_MEASUREMENTS],
        `is`(ingredientsAndMeasurements.map { it.second }))
    assertThat(firestoreMap[FIRESTORE_RECIPE_TIME], `is`(time))
    assertThat(firestoreMap[FIRESTORE_RECIPE_DIFFICULTY], `is`(difficulty))
    assertThat(firestoreMap[FIRESTORE_RECIPE_PRICE], `is`(price))
  }
}
