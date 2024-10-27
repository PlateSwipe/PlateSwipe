package com.android.sample.model.filter

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class FilterTest {
  @Test
  fun `test default Filter values`() {
    val filter = Filter(FloatRange(-1f, -1f), FloatRange(-1f, -1f))
    assertTrue(filter.timeRange.isUnbounded())
    assertTrue(filter.priceRange.isUnbounded())
    assertEquals(Difficulty.Undefined, filter.difficulty)
    assertEquals(null, filter.category)
  }

  @Test
  fun `test Filter with custom values`() {
    val customTimeRange = FloatRange(0f, 100f)
    val customPriceRange = FloatRange(10f, 50f)
    val filter =
        Filter(
            timeRange = customTimeRange,
            priceRange = customPriceRange,
            difficulty = Difficulty.Medium,
            category = "dessert")

    assertEquals(customTimeRange, filter.timeRange)
    assertEquals(customPriceRange, filter.priceRange)
    assertEquals(Difficulty.Medium, filter.difficulty)
    assertEquals("dessert", filter.category)
  }

  @Test
  fun `test FloatRange valid update`() {
    val range = FloatRange(0f, 10f)
    range.update(5f, 15f)

    assertEquals(5f, range.min)
    assertEquals(15f, range.max)
  }

  @Test
  fun `test Filter update time range`() {

    val customTimeRange = FloatRange(0f, 100f)
    val filter = Filter(FloatRange(-1f, -1f), FloatRange(-1f, -1f))

    filter.timeRange.update(0f, 100f)
    assertEquals(customTimeRange, filter.timeRange)
    assertTrue(filter.priceRange.isUnbounded())
    assertEquals(Difficulty.Undefined, filter.difficulty)
    assertEquals(null, filter.category)
  }

  @Test
  fun `test Filter update price range`() {

    val customPriceRange = FloatRange(0f, 200f)
    val filter = Filter(FloatRange(-1f, -1f), FloatRange(-1f, -1f))
    filter.priceRange.update(0f, 200f)

    assertTrue(filter.timeRange.isUnbounded())
    assertEquals(customPriceRange, filter.priceRange)
    assertEquals(Difficulty.Undefined, filter.difficulty)
    assertEquals(null, filter.category)
  }

  @Test
  fun `test Filter update both ranges`() {
    val customTimeRange = FloatRange(0f, 100f)
    val customPriceRange = FloatRange(0f, 200f)
    val filter = Filter(FloatRange(-1f, -1f), FloatRange(-1f, -1f))
    filter.timeRange.update(0f, 100f)
    filter.priceRange.update(0f, 200f)

    assertEquals(customTimeRange, filter.timeRange)
    assertEquals(customPriceRange, filter.priceRange)
    assertEquals(Difficulty.Undefined, filter.difficulty)
    assertEquals(null, filter.category)
  }
}
