package com.android.sample.model.filter

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Test

class FilterTest {
  @Test
  fun `test default Filter values`() {
    val filter = Filter(FloatRange(-1f, -1f, -1f, -1f))
    assertTrue(filter.timeRange.isLimited())

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
        Filter(timeRange = customTimeRange, difficulty = Difficulty.Medium, category = "dessert")

    assertEquals(customTimeRange, filter.timeRange)
    assertEquals(Difficulty.Medium, filter.difficulty)
    assertEquals("dessert", filter.category)
  }

  @Test
  fun `test init with incorrect Min`() {
    // Expect an IllegalArgumentException
    assertThrows(IllegalArgumentException::class.java) { FloatRange(-50f, 0f, 0f, 0f) }
  }

  @Test
  fun `test init with incorrect Max`() {
    // Expect an IllegalArgumentException
    assertThrows(IllegalArgumentException::class.java) { FloatRange(0f, -50f, 0f, 0f) }
  }

  @Test
  fun `test init with incorrect minBorn`() {
    // Expect an IllegalArgumentException
    assertThrows(IllegalArgumentException::class.java) { FloatRange(0f, 0f, -50f, 0f) }
  }

  @Test
  fun `test init with incorrect maxBorn`() {
    // Expect an IllegalArgumentException
    assertThrows(IllegalArgumentException::class.java) { FloatRange(0f, 0f, 0f, -50f) }
  }

  @Test
  fun `test FloatRange valid update`() {
    val range = FloatRange(0f, 10f, 0f, 10f)
    range.update(5f, 8f)

    assertEquals(5f, range.min)
    assertEquals(8f, range.max)
  }

  @Test
  fun `test Filter update price range`() {

    val customPriceRange = FloatRange(0f, 200f, 0f, 200f)
    val filter =
        Filter(
            FloatRange(-1f, -1f, -1f, -1f),
        )

    assertTrue(filter.timeRange.isLimited())

    assertEquals(Difficulty.Undefined, filter.difficulty)
    assertEquals(null, filter.category)
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
}
