package com.android.sample.model.filter

import kotlinx.coroutines.flow.StateFlow

interface FilterPageViewModel {
  // filter that is being applied -> to use in the screen
  val filter: StateFlow<Filter>
  // filter that is being edited before applying changes -> to define in the ViewModel
  val tmpFilter: StateFlow<Filter>
  val categories: StateFlow<List<String>>
  val timeRangeState: StateFlow<FloatRange>
  val priceRangeState: StateFlow<FloatRange>

  /** Fetches the list of categories from the repository. */
  fun getCategoryList()

  /**
   * Updates the difficulty filter.
   *
   * @param difficulty The difficulty to filter by.
   */
  fun updateDifficulty(difficulty: Difficulty)

  /**
   * Updates the price range filter.
   *
   * @param min The minimum price.
   * @param max The maximum price.
   */
  fun updatePriceRange(min: Float, max: Float)

  /**
   * Updates the time range filter.
   *
   * @param min The minimum time.
   * @param max The maximum time.
   */
  fun updateTimeRange(min: Float, max: Float)

  /**
   * Updates the category filter.
   *
   * @param category The category to filter by.
   */
  fun updateCategory(category: String?)

  /** Resets all filters to their default values. */
  fun resetFilters()

  /** Applies the changes made to the filters. */
  fun applyChanges()

  /** Initializes the filter. */
  fun initFilter()
}
