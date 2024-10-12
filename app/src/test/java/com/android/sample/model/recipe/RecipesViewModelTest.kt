package com.github.se.bootcamp.model.recipe

import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipeRepository
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

/** Unit tests for RecipesViewModel. */
class RecipesViewModelTest {
  private lateinit var recipeRepository: RecipeRepository
  private lateinit var recipesViewModel: RecipesViewModel

  // Dummy recipes for testing
  private val dummyRecipes: List<Recipe> =
      listOf(
          Recipe(
              idMeal = "1", // Updated to String
              strMeal = "Spicy Arrabiata Penne",
              strCategory = "Vegetarian",
              strArea = "Italian",
              strInstructions = "Instructions here...",
              strMealThumbUrl =
                  "https://www.recipetineats.com/penne-all-arrabbiata-spicy-tomato-pasta/",
              ingredientsAndMeasurements =
                  listOf(Pair("Penne", "1 pound"), Pair("Olive oil", "1/4 cup"))),
          Recipe(
              idMeal = "2", // Updated to String
              strMeal = "Chicken Curry",
              strCategory = "Non-Vegetarian",
              strArea = "Indian",
              strInstructions = "Instructions here...",
              strMealThumbUrl =
                  "https://www.foodfashionparty.com/2023/08/05/everyday-chicken-curry/",
              ingredientsAndMeasurements =
                  listOf(Pair("Chicken", "1 pound"), Pair("Curry powder", "2 tbsp")))
          // Add more dummy recipes as needed
          )

  @Before
  fun setUp() {
    // Mock the RecipeRepository
    recipeRepository = mock(RecipeRepository::class.java)
    // Initialize the RecipesViewModel with the mocked repository
    recipesViewModel = RecipesViewModel(recipeRepository)
  }

  @Test
  fun fetchRandomRecipesUpdatesState() {
    // Simulate the behavior of the repository
    `when`(recipeRepository.random(any(), any(), any())).thenAnswer { invocation ->
      val onSuccess = invocation.arguments[1] as (List<Recipe>) -> Unit
      onSuccess(dummyRecipes) // Return the dummy recipes
    }

    // Act
    recipesViewModel.fetchRandomRecipes(2)

    // Assert
    assertThat(
        recipesViewModel.recipes.value,
        `is`(dummyRecipes)) // Check if the ViewModel's recipes are updated
  }

  @Test
  fun fetchRandomRecipesCallsRepository() {
    // Arrange
    val numberOfRecipes = 2

    // Act
    recipesViewModel.fetchRandomRecipes(numberOfRecipes)

    // Assert
    verify(recipeRepository)
        .random(
            eq(numberOfRecipes),
            any(),
            any()) // Verify that the repository's random method is called
  }
}
