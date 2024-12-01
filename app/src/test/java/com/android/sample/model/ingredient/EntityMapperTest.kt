package com.android.sample.model.ingredient

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.ui.utils.testIngredients
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EntityMapperTest {

  private val ingredient = testIngredients[0]

  @Test
  fun testIngredientToEntity() {
    val ingredientEntity = ingredient.toEntity()
    val categories = "[\"Healthy\",\"Fruit\",\"Vegan\"]"
    val images =
        "{\"image_front_url\":\"https://display_normal\",\"image_front_thumb_url\":\"https://display_thumbnail\",\"image_front_small_url\":\"https://display_small\"}"
    assertEquals(ingredient.uid, ingredientEntity.uid)
    assertEquals(ingredient.barCode, ingredientEntity.barCode)
    assertEquals(ingredient.name, ingredientEntity.name)
    assertEquals(ingredient.brands, ingredientEntity.brands)
    assertEquals(ingredient.quantity, ingredientEntity.quantity)
    assertEquals(categories, ingredientEntity.categories)
    assertEquals(images, ingredientEntity.images)
  }

  @Test
  fun testIngredientEntityToIngredient() {
    val ingredientEntity = ingredient.toEntity()
    val ingredientFromEntity = ingredientEntity.toIngredient()
    assertEquals(ingredient.uid, ingredientFromEntity.uid)
    assertEquals(ingredient.barCode, ingredientFromEntity.barCode)
    assertEquals(ingredient.name, ingredientFromEntity.name)
    assertEquals(ingredient.brands, ingredientFromEntity.brands)
    assertEquals(ingredient.quantity, ingredientFromEntity.quantity)
    assertEquals(ingredient.categories, ingredientFromEntity.categories)
    assertEquals(ingredient.images, ingredientFromEntity.images)
  }

  @Test
  fun testDoubleWay() {
    val ingredientEntity = ingredient.toEntity()
    val ingredientFromEntity = ingredientEntity.toIngredient()
    assertEquals(ingredient.uid, ingredientFromEntity.uid)
    assertEquals(ingredient.barCode, ingredientFromEntity.barCode)
    assertEquals(ingredient.name, ingredientFromEntity.name)
    assertEquals(ingredient.brands, ingredientFromEntity.brands)
    assertEquals(ingredient.quantity, ingredientFromEntity.quantity)
    assertEquals(ingredient.categories, ingredientFromEntity.categories)
    assertEquals(ingredient.images, ingredientFromEntity.images)
  }
}
