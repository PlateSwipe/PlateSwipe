package com.android.sample.model.filter

import com.android.sample.resources.C.Tag.MIN_SHOULD_NOT_BE_GREATER_THAN_MAX
import com.android.sample.resources.C.Tag.NEW_MAX_SHOULD_NOT_BE_NEGATIVE
import com.android.sample.resources.C.Tag.NEW_MIN_AND_NEW_MAX_SHOULD_BE_WITHIN_RANGE
import com.android.sample.resources.C.Tag.NEW_MIN_SHOULD_NOT_BE_NEGATIVE
import com.android.sample.resources.C.Tag.NEW_MIN_SHOULD_NOT_EXCEED_MAX

/** Filter class to store filter types. */
open class Filter(
    var timeRange: FloatRange = FloatRange.TIME_RANGE,
    var priceRange: FloatRange = FloatRange.PRICE_RANGE,
    var difficulty: Difficulty = Difficulty.Undefined,
    var category: String? = null,
)

/** Class to store a range of float values. */
data class FloatRange(var min: Float, var max: Float, var minBorn: Float, var maxBorn: Float) {
  init {
    require(min <= max) { MIN_SHOULD_NOT_BE_GREATER_THAN_MAX }
  }

  fun update(newMin: Float, newMax: Float) {
    require(0 <= newMin) { NEW_MIN_SHOULD_NOT_BE_NEGATIVE }
    require(0 <= newMax) { NEW_MAX_SHOULD_NOT_BE_NEGATIVE }
    require(newMin <= newMax) { NEW_MIN_SHOULD_NOT_EXCEED_MAX }
    if (minBorn == -1f && maxBorn == -1f) {
      minBorn = newMin.toInt().toFloat()
      maxBorn = newMax.toInt().toFloat()
    }
    require(newMin >= minBorn && newMax <= maxBorn) { NEW_MIN_AND_NEW_MAX_SHOULD_BE_WITHIN_RANGE }
    min = newMin.toInt().toFloat()
    max = newMax.toInt().toFloat()
  }

  companion object {
    val TIME_RANGE = FloatRange(-1f, -1f, -1f, -1f)
    val PRICE_RANGE = FloatRange(-1f, -1f, -1f, -1f)
  }

  fun isLimited() = min == minBorn && max == maxBorn
}

/** Enum class to store the difficulty of a recipe. */
enum class Difficulty {
  Easy,
  Medium,
  Hard,
  Undefined
}
