package com.android.sample.model.fridge.localData

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Converters {

  private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

  @TypeConverter
  fun fromLocalDate(date: LocalDate?): String? {
    return date?.format(formatter)
  }

  @TypeConverter
  fun toLocalDate(date: String?): LocalDate? {
    return date?.let { LocalDate.parse(it, formatter) }
  }
}
