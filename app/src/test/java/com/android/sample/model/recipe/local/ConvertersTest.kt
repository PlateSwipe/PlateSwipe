package com.android.sample.model.recipe.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.recipe.Instruction
import com.android.sample.model.recipe.localData.Converters
import com.android.sample.ui.utils.recipeIngredientLists
import com.android.sample.ui.utils.recipeInstructions
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConvertersTest {

  private lateinit var converters: Converters
  private lateinit var instructions: List<Instruction>
  private lateinit var ingredientsAndMeasurements: List<Pair<String, String>>

  @Before
  fun setup() {
    converters = Converters()
    instructions = recipeInstructions[0]
    ingredientsAndMeasurements = recipeIngredientLists[0]
  }

  @Test
  fun fromInstructionsList() {
    val result = converters.fromInstructionsList(instructions)
    assertEquals(
        "[{\"description\":\"1. Boil water\",\"time\":\"30 min\",\"iconType\":\"Cook\"},{\"description\":\"2. Add pasta\",\"time\":\"30 min\",\"iconType\":\"Fire\"},{\"description\":\"3. Cook for 10 minutes\",\"time\":\"30 min\",\"iconType\":\"Fire\"},{\"description\":\"4. Drain water\",\"time\":\"30 min\",\"iconType\":\"Fire\"},{\"description\":\"5. Add sauce\",\"time\":\"30 min\",\"iconType\":\"Fire\"}]",
        result)
  }

  @Test
  fun toInstructionsList() {
    val result =
        converters.toInstructionsList(
            "[{\"description\":\"1. Boil water\",\"time\":\"30 min\",\"iconType\":\"Cook\"},{\"description\":\"2. Add pasta\",\"time\":\"30 min\",\"iconType\":\"Fire\"},{\"description\":\"3. Cook for 10 minutes\",\"time\":\"30 min\",\"iconType\":\"Fire\"},{\"description\":\"4. Drain water\",\"time\":\"30 min\",\"iconType\":\"Fire\"},{\"description\":\"5. Add sauce\",\"time\":\"30 min\",\"iconType\":\"Fire\"}]")
    assertEquals(instructions, result)
  }

  @Test
  fun instructionList() {
    assertEquals(
        instructions, converters.toInstructionsList(converters.fromInstructionsList(instructions)))
  }

  @Test
  fun fromIngredientsAndMeasurementsList() {
    val result = converters.fromIngredientsAndMeasurementsList(ingredientsAndMeasurements)
    assertEquals(
        "[{\"first\":\"Beef\",\"second\":\"1 lb\"},{\"first\":\"Pasta\",\"second\":\"1 lb\"},{\"first\":\"Tomato Sauce\",\"second\":\"1 cup\"}]",
        result)
  }

  @Test
  fun toIngredientsAndMeasurementsList() {
    val result =
        converters.toIngredientsAndMeasurementsList(
            "[{\"first\":\"Beef\",\"second\":\"1 lb\"},{\"first\":\"Pasta\",\"second\":\"1 lb\"},{\"first\":\"Tomato Sauce\",\"second\":\"1 cup\"}]")
    assertEquals(ingredientsAndMeasurements, result)
  }

  @Test
  fun ingredientsAndMeasurementsList() {
    assertEquals(
        ingredientsAndMeasurements,
        converters.toIngredientsAndMeasurementsList(
            converters.fromIngredientsAndMeasurementsList(ingredientsAndMeasurements)))
  }
}
