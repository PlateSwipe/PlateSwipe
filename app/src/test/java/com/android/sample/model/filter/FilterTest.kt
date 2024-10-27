package com.android.sample.model.filter

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Test

class FilterTest {
  @Test
  fun `test default Filter values`() {
    val filter = Filter(FloatRange(-1f, -1f, -1f, -1f), FloatRange(-1f, -1f, -1f, -1f))
    assertTrue(filter.timeRange.isLimited())
    assertTrue(filter.priceRange.isLimited())
    assertEquals(Difficulty.Undefined, filter.difficulty)
    assertEquals(null, filter.category)
  }

  @Test
  fun `test Filter with custom values`() {
    val customTimeRange = FloatRange(0f, 100f, 0f, 100f)
    val customPriceRange =
        FloatRange(
            10f,
            50f,
            10f,
            50f,
        )
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
    val range = FloatRange(0f, 10f, 0f, 10f)
    range.update(5f, 8f)

    assertEquals(5f, range.min)
    assertEquals(8f, range.max)
  }

  @Test
  fun `test Filter update time range`() {

    val customTimeRange = FloatRange(0f, 100f, 0f, 100f)
    val filter = Filter(FloatRange(-1f, -1f, -1f, -1f), FloatRange(-1f, -1f, -1f, -1f))

    filter.timeRange.update(0f, 100f)
    assertEquals(customTimeRange, filter.timeRange)
    assertTrue(filter.priceRange.isLimited())
    assertEquals(Difficulty.Undefined, filter.difficulty)
    assertEquals(null, filter.category)
  }

  @Test
  fun `test Filter update price range`() {

    val customPriceRange = FloatRange(0f, 200f, 0f, 200f)
    val filter = Filter(FloatRange(-1f, -1f, -1f, -1f), FloatRange(-1f, -1f, -1f, -1f))
    filter.priceRange.update(0f, 200f)

    assertTrue(filter.timeRange.isLimited())
    assertEquals(customPriceRange, filter.priceRange)
    assertEquals(Difficulty.Undefined, filter.difficulty)
    assertEquals(null, filter.category)
  }

  @Test
  fun `test Filter update both ranges`() {
    val customTimeRange = FloatRange(0f, 100f, 0f, 100f)
    val customPriceRange = FloatRange(0f, 200f, 0f, 200f)
    val filter = Filter(FloatRange(-1f, -1f, -1f, -1f), FloatRange(-1f, -1f, -1f, -1f))
    filter.timeRange.update(0f, 100f)
    filter.priceRange.update(0f, 200f)

    assertEquals(customTimeRange, filter.timeRange)
    assertEquals(customPriceRange, filter.priceRange)
    assertEquals(Difficulty.Undefined, filter.difficulty)
    assertEquals(null, filter.category)
  }

  @Test
  fun `test Filter double update both ranges`() {
    val customTimeRange = FloatRange(5f, 99f, 0f, 100f)
    val customPriceRange = FloatRange(5f, 199f, 0f, 200f)
    val filter = Filter(FloatRange(-1f, -1f, -1f, -1f), FloatRange(-1f, -1f, -1f, -1f))
    filter.timeRange.update(0f, 100f)
    filter.priceRange.update(0f, 200f)

    filter.timeRange.update(5f, 99f)
    filter.priceRange.update(5f, 199f)

    assertEquals(customTimeRange, filter.timeRange)
    assertEquals(customPriceRange, filter.priceRange)
    assertEquals(Difficulty.Undefined, filter.difficulty)
    assertEquals(null, filter.category)
  }

  @Test
  fun `test update with negative newMin`() {
    val range = FloatRange(0f, 10f, 0f, 10f)

    // Expect an IllegalArgumentException when passing a negative newMin
    assertThrows(IllegalArgumentException::class.java) { range.update(-1f, 5f) }
  }

  @Test
  fun `test update with negative newMax`() {
    val range = FloatRange(0f, 10f, 0f, 10f)

    // Expect an IllegalArgumentException when passing a negative newMax
    assertThrows(IllegalArgumentException::class.java) { range.update(1f, -5f) }
  }

  @Test
  fun `test update with newMin greater than newMax`() {
    val range = FloatRange(0f, 10f, 0f, 10f)

    // Expect an IllegalArgumentException when newMin is greater than newMax
    assertThrows(IllegalArgumentException::class.java) { range.update(6f, 5f) }
  }

  @Test
  fun `test update with newMin less than minBorn`() {
    val range = FloatRange(0f, 10f, 5f, 10f)

    // Expect an IllegalArgumentException when newMin is less than minBorn
    assertThrows(IllegalArgumentException::class.java) { range.update(4f, 8f) }
  }

  @Test
  fun `test update with newMax greater than maxBorn`() {
    val range = FloatRange(0f, 5f, 0f, 5f)

    // Expect an IllegalArgumentException when newMax is greater than maxBorn
    assertThrows(IllegalArgumentException::class.java) { range.update(1f, 6f) }
  }
}
