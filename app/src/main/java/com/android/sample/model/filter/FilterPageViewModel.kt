package com.android.sample.model.filter

import kotlinx.coroutines.flow.StateFlow

interface FilterPageViewModel {
  val filter: StateFlow<Filter>
  val categories: StateFlow<List<String>>

  /** Fetches the list of categories from the repository. */
  fun getCategoryList()

  /**
   * Updates the difficulty filter.
   *
   * @param difficulty The difficulty to filter by.
   */
  fun updateDifficulty(difficulty: Difficulty) {
    filter.value.difficulty = difficulty
  }

  /**
   * Updates the price range filter.
   *
   * @param min The minimum price.
   * @param max The maximum price.
   */
  fun updatePriceRange(min: Float, max: Float) {
    filter.value.priceRange.update(min, max)
  }

  /**
   * Updates the time range filter.
   *
   * @param min The minimum time.
   * @param max The maximum time.
   */
  fun updateTimeRange(min: Float, max: Float) {
    filter.value.timeRange.update(min, max)
  }

  /**
   * Updates the category filter.
   *
   * @param category The category to filter by.
   */
  fun updateCategory(category: String?) {
    filter.value.category = category
  }
}
