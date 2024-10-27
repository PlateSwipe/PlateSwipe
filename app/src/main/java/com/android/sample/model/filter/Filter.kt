package com.android.sample.model.filter

/** Filter class to store filter types. */
open class Filter(
    var timeRange: FloatRange = FloatRange.TIME_RANGE,
    var priceRange: FloatRange = FloatRange.PRICE_RANGE,
    var difficulty: Difficulty = Difficulty.Undefined,
    var category: String? = null,
)

/** Class to store a range of float values. */
data class FloatRange(var min: Float, var max: Float) {
  init {
    require(min <= max) { "min should not be greater than max" }
  }

  fun update(newMin: Float, newMax: Float) {
    require(0 <= newMin) { "newMin should not be negative" }
    require(0 <= newMax) { "newMax should not be negative" }
    require(newMin <= newMax) { "newMin should not exceed max" }
    min = newMin
    max = newMax
  }

  companion object {
    val TIME_RANGE = FloatRange(-1f, -1f)
    val PRICE_RANGE = FloatRange(-1f, -1f)
  }

  fun isUnbounded() = min.toInt() == -1 && max.toInt() == -1
}

/** Enum class to store the difficulty of a recipe. */
enum class Difficulty {
  Easy,
  Medium,
  Hard,
  Undefined
}
