package com.android.sample.model.filter

import com.android.sample.resources.C.Tag.Filter.MAX_BORN_SHOULD_NOT_BE_NEGATIVE
import com.android.sample.resources.C.Tag.Filter.MAX_SHOULD_NOT_BE_NEGATIVE
import com.android.sample.resources.C.Tag.Filter.MIN_BORN_SHOULD_NOT_BE_NEGATIVE
import com.android.sample.resources.C.Tag.Filter.MIN_SHOULD_NOT_BE_GREATER_THAN_MAX
import com.android.sample.resources.C.Tag.Filter.MIN_SHOULD_NOT_BE_NEGATIVE
import com.android.sample.resources.C.Tag.Filter.UNINITIALIZED_BORN_VALUE
import com.android.sample.resources.C.Tag.FilterPage.TIME_RANGE_MAX
import com.android.sample.resources.C.Tag.FilterPage.TIME_RANGE_MIN

/**
 * Class to store the filter options.
 *
 * @param timeRange The time range of the filter.
 * @param difficulty The difficulty of the filter.
 * @param category The category of the filter. null if no category is selected.
 */
open class Filter(
    var timeRange: FloatRange =
        FloatRange(
            UNINITIALIZED_BORN_VALUE,
            UNINITIALIZED_BORN_VALUE,
            UNINITIALIZED_BORN_VALUE,
            UNINITIALIZED_BORN_VALUE),
    var difficulty: Difficulty = Difficulty.Undefined,
    var category: String? = null,
)

/**
 * Class to store the range of a float value.
 *
 * @param min The minimum value of the range.
 * @param max The maximum value of the range.
 * @param minBorn The minimum born value of the range.
 * @param maxBorn The maximum born value of the range.
 */
data class FloatRange(var min: Float, var max: Float, var minBorn: Float, var maxBorn: Float) {
  init {
    require(min >= 0 || min == UNINITIALIZED_BORN_VALUE) { MIN_SHOULD_NOT_BE_NEGATIVE }
    require(max >= 0 || max == UNINITIALIZED_BORN_VALUE) { MAX_SHOULD_NOT_BE_NEGATIVE }
    require(minBorn >= 0 || minBorn == UNINITIALIZED_BORN_VALUE) { MIN_BORN_SHOULD_NOT_BE_NEGATIVE }
    require(maxBorn >= 0 || maxBorn == UNINITIALIZED_BORN_VALUE) { MAX_BORN_SHOULD_NOT_BE_NEGATIVE }
    require(min <= max) { MIN_SHOULD_NOT_BE_GREATER_THAN_MAX }
  }

  /**
   * Updates the range with the new minimum and maximum values.
   *
   * @param newMin The new minimum value.
   * @param newMax The new maximum value.
   */
  fun update(newMin: Float, newMax: Float) {
    require(newMin <= newMax) { MIN_SHOULD_NOT_BE_GREATER_THAN_MAX }
    min = newMin
    max = newMax
  }

  /**
   * Checks if the range is limited.
   *
   * @return true if the range is limited, false otherwise.
   */
  fun isLimited() =
      // Check if there was a change in the range value to avoid unnecessary calls to the database
      // with the time filter
      !((min.toInt() != TIME_RANGE_MIN.toInt() || max.toInt() != TIME_RANGE_MAX.toInt()) &&
          (min != minBorn || max != maxBorn) &&
          (min != UNINITIALIZED_BORN_VALUE && max != UNINITIALIZED_BORN_VALUE))
}

/** Enum class to represent the difficulty of a recipe. */
enum class Difficulty {
  Easy,
  Medium,
  Hard,
  Undefined
}
