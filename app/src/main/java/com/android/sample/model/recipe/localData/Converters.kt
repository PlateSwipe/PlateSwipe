package com.android.sample.model.recipe.localData

import androidx.room.TypeConverter
import com.android.sample.model.recipe.Instruction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
  private val gson = Gson()

  @TypeConverter
  fun fromInstructionsList(value: List<Instruction>?): String {
    return gson.toJson(value)
  }

  @TypeConverter
  fun toInstructionsList(value: String): List<Instruction> {
    val type = object : TypeToken<List<Instruction>>() {}.type
    return gson.fromJson(value, type)
  }

  @TypeConverter
  fun fromIngredientsAndMeasurementsList(value: List<Pair<String, String>>?): String {
    return gson.toJson(value)
  }

  @TypeConverter
  fun toIngredientsAndMeasurementsList(value: String): List<Pair<String, String>> {
    val type = object : TypeToken<List<Pair<String, String>>>() {}.type
    return gson.fromJson(value, type)
  }
}
