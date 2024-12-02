package com.android.sample.model.recipe

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.ui.utils.testRecipes
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EntityMapperTest {

  private val recipes = testRecipes[0]

  @Test
  fun testRecipeToEntity() {
    val recipeEntity = recipes.toEntity()
    assertEquals(recipes.uid, recipeEntity.uid)
    assertEquals(recipes.name, recipeEntity.name)
    assertEquals(recipes.category, recipeEntity.category)
    assertEquals(recipes.origin, recipeEntity.origin)
    assertEquals(
        "[{\"description\":\"1. Boil water\",\"time\":\"30 min\",\"iconType\":\"Cook\"},{\"description\":\"2. Add pasta\",\"time\":\"30 min\",\"iconType\":\"Fire\"},{\"description\":\"3. Cook for 10 minutes\",\"time\":\"30 min\",\"iconType\":\"Fire\"},{\"description\":\"4. Drain water\",\"time\":\"30 min\",\"iconType\":\"Fire\"},{\"description\":\"5. Add sauce\",\"time\":\"30 min\",\"iconType\":\"Fire\"}]",
        recipeEntity.instructions)
    assertEquals(recipes.strMealThumbUrl, recipeEntity.strMealThumbUrl)
    assertEquals(
        "[{\"first\":\"Beef\",\"second\":\"1 lb\"},{\"first\":\"Pasta\",\"second\":\"1 lb\"},{\"first\":\"Tomato Sauce\",\"second\":\"1 cup\"}]",
        recipeEntity.ingredientsAndMeasurements)
    assertEquals(recipes.time, recipeEntity.time)
    assertEquals(recipes.difficulty, recipeEntity.difficulty)
    assertEquals(recipes.price, recipeEntity.price)
    assertEquals(recipes.url, recipeEntity.url)
  }

  @Test
  fun testRecipeEntityToRecipe() {
    val recipeEntity = recipes.toEntity()
    val recipeFromEntity = recipeEntity.toRecipe()
    assertEquals(recipes.uid, recipeFromEntity.uid)
    assertEquals(recipes.name, recipeFromEntity.name)
    assertEquals(recipes.category, recipeFromEntity.category)
    assertEquals(recipes.origin, recipeFromEntity.origin)
    assertEquals(recipes.instructions, recipeFromEntity.instructions)
    assertEquals(recipes.strMealThumbUrl, recipeFromEntity.strMealThumbUrl)
    assertEquals(recipes.ingredientsAndMeasurements, recipeFromEntity.ingredientsAndMeasurements)
    assertEquals(recipes.time, recipeFromEntity.time)
    assertEquals(recipes.difficulty, recipeFromEntity.difficulty)
    assertEquals(recipes.price, recipeFromEntity.price)
    assertEquals(recipes.url, recipeFromEntity.url)
  }
}
