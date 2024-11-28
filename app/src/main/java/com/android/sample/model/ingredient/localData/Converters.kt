package com.android.sample.model.ingredient.localData

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

  private val gson = Gson()

  /**
   * Converts a list of strings to a JSON string.
   *
   * @param value The list of strings to convert.
   * @return The JSON string representation of the list.
   */
  @TypeConverter
  fun fromStringList(value: List<String>): String {
    return gson.toJson(value)
  }

  /**
   * Converts a JSON string to a list of strings.
   *
   * @param value The JSON string to convert.
   * @return The list of strings represented by the JSON string.
   */
  @TypeConverter
  fun toStringList(value: String): List<String> {
    val listType = object : TypeToken<List<String>>() {}.type
    return gson.fromJson(value, listType)
  }

  /**
   * Converts a map of strings to a JSON string.
   *
   * @param value The map of strings to convert.
   * @return The JSON string representation of the map.
   */
  @TypeConverter
  fun fromMap(value: Map<String, String>): String {
    return gson.toJson(value)
  }

  /**
   * Converts a JSON string to a map of strings.
   *
   * @param value The JSON string to convert.
   * @return The map of strings represented by the JSON string.
   */
  @TypeConverter
  fun toMap(value: String): Map<String, String> {
    val mapType = object : TypeToken<Map<String, String>>() {}.type
    return gson.fromJson(value, mapType)
  }
}
