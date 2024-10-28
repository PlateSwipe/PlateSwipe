package com.android.sample.model.filter

import com.android.sample.resources.C.Tag.MAX_BORN_SHOULD_NOT_BE_NEGATIVE
import com.android.sample.resources.C.Tag.MAX_SHOULD_NOT_BE_NEGATIVE
import com.android.sample.resources.C.Tag.MIN_BORN_SHOULD_NOT_BE_NEGATIVE
import com.android.sample.resources.C.Tag.MIN_SHOULD_NOT_BE_GREATER_THAN_MAX
import com.android.sample.resources.C.Tag.MIN_SHOULD_NOT_BE_NEGATIVE
import com.android.sample.resources.C.Tag.NEW_MAX_SHOULD_NOT_BE_NEGATIVE
import com.android.sample.resources.C.Tag.NEW_MIN_AND_NEW_MAX_SHOULD_BE_WITHIN_RANGE
import com.android.sample.resources.C.Tag.NEW_MIN_SHOULD_NOT_BE_NEGATIVE
import com.android.sample.resources.C.Tag.NEW_MIN_SHOULD_NOT_EXCEED_MAX
import com.android.sample.resources.C.Tag.UNINITIALIZED_BORN_VALUE
import kotlin.math.roundToInt

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
    require(min >= 0 || min == UNINITIALIZED_BORN_VALUE) { MIN_SHOULD_NOT_BE_NEGATIVE }
    require(max >= 0 || max == UNINITIALIZED_BORN_VALUE) { MAX_SHOULD_NOT_BE_NEGATIVE }
    require(minBorn >= 0 || minBorn == UNINITIALIZED_BORN_VALUE) { MIN_BORN_SHOULD_NOT_BE_NEGATIVE }
    require(maxBorn >= 0 || maxBorn == UNINITIALIZED_BORN_VALUE) { MAX_BORN_SHOULD_NOT_BE_NEGATIVE }
    require(min <= max) { MIN_SHOULD_NOT_BE_GREATER_THAN_MAX }
  }

  fun update(newMin: Float, newMax: Float) {
    require(0 <= newMin) { NEW_MIN_SHOULD_NOT_BE_NEGATIVE }
    require(0 <= newMax) { NEW_MAX_SHOULD_NOT_BE_NEGATIVE }
    require(newMin <= newMax) { NEW_MIN_SHOULD_NOT_EXCEED_MAX }
    if (minBorn == UNINITIALIZED_BORN_VALUE && maxBorn == UNINITIALIZED_BORN_VALUE) {
      minBorn = newMin.roundToInt().toFloat()
      maxBorn = newMax.roundToInt().toFloat()
    }
    require(newMin >= minBorn && newMax <= maxBorn) { NEW_MIN_AND_NEW_MAX_SHOULD_BE_WITHIN_RANGE }
    min = newMin.roundToInt().toFloat()
    max = newMax.roundToInt().toFloat()
  }

  companion object {
    val TIME_RANGE =
        FloatRange(
            UNINITIALIZED_BORN_VALUE,
            UNINITIALIZED_BORN_VALUE,
            UNINITIALIZED_BORN_VALUE,
            UNINITIALIZED_BORN_VALUE)
    val PRICE_RANGE =
        FloatRange(
            UNINITIALIZED_BORN_VALUE,
            UNINITIALIZED_BORN_VALUE,
            UNINITIALIZED_BORN_VALUE,
            UNINITIALIZED_BORN_VALUE)
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
