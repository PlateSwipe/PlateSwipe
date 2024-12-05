package com.android.sample.model.recipe.localData

import androidx.room.TypeConverter
import com.android.sample.model.recipe.Instruction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/** A class containing type converters for Room recipe database. */
class Converters {
  private val gson = Gson()

  /**
   * Converts a list of instructions to a JSON string.
   *
   * @param value The list of instructions to convert.
   * @return The JSON string representation of the list of instructions.
   */
  @TypeConverter
  fun fromInstructionsList(value: List<Instruction>?): String {
    return gson.toJson(value)
  }

  /**
   * Converts a JSON string to a list of instructions.
   *
   * @param value The JSON string to convert.
   * @return The list of instructions.
   */
  @TypeConverter
  fun toInstructionsList(value: String): List<Instruction> {
    val type = object : TypeToken<List<Instruction>>() {}.type
    return gson.fromJson(value, type)
  }

  /**
   * Converts a list of pairs of ingredients and measurements to a JSON string.
   *
   * @param value The list of pairs to convert.
   * @return The JSON string representation of the list of pairs.
   */
  @TypeConverter
  fun fromIngredientsAndMeasurementsList(value: List<Pair<String, String>>?): String {
    return gson.toJson(value)
  }

  /**
   * Converts a JSON string to a list of pairs of ingredients and measurements.
   *
   * @param value The JSON string to convert.
   * @return The list of pairs of ingredients and measurements.
   */
  @TypeConverter
  fun toIngredientsAndMeasurementsList(value: String): List<Pair<String, String>> {
    val type = object : TypeToken<List<Pair<String, String>>>() {}.type
    return gson.fromJson(value, type)
  }
}
