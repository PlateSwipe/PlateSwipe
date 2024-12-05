package com.android.sample.utils

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.resources.C.Dimension.Counter.ADD
import com.android.sample.resources.C.Dimension.Counter.MAX_VALUE
import com.android.sample.resources.C.Dimension.Counter.MIN_VALUE
import com.android.sample.resources.C.Dimension.Counter.NUMBER
import com.android.sample.resources.C.Dimension.Counter.REMOVE
import com.android.sample.ui.utils.Counter
import org.junit.Rule
import org.junit.Test

class CounterTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun counter_initialValueDisplayedCorrectly() {
    val initialCount = 5

    composeTestRule.setContent { Counter(count = initialCount, onCounterChange = {}) }

    // Verify initial value is displayed
    composeTestRule.onNodeWithTag(NUMBER).assertTextEquals(initialCount.toString())
  }

  @Test
  fun counter_incrementButton_increasesCount() {
    var count = 5

    composeTestRule.setContent { Counter(count = count, onCounterChange = { count = it }) }

    // Perform click on + button
    composeTestRule.onNodeWithTag(ADD).performClick()

    // Verify count is incremented
    composeTestRule.runOnIdle { assert(count == 6) }
    composeTestRule.onNodeWithTag(NUMBER).assertTextEquals("6")
  }

  @Test
  fun counter_decrementButton_decreasesCount() {
    var count = 5

    composeTestRule.setContent { Counter(count = count, onCounterChange = { count = it }) }

    // Perform click on - button
    composeTestRule.onNodeWithTag(REMOVE).performClick()
    composeTestRule.waitForIdle()

    // Verify count is decremented
    composeTestRule.runOnIdle { assert(count == 4) }
    composeTestRule.onNodeWithTag(NUMBER).assertTextEquals("4")
  }

  @Test
  fun counter_doesNotExceedMaxValue() {
    var count = MAX_VALUE

    composeTestRule.setContent { Counter(count = count, onCounterChange = { count = it }) }

    // Perform click on + button
    composeTestRule.onNodeWithTag(ADD).performClick()

    // Verify count does not exceed max value
    composeTestRule.runOnIdle { assert(count == MAX_VALUE) }
    composeTestRule.onNodeWithTag(NUMBER).assertTextEquals(MAX_VALUE.toString())
  }

  @Test
  fun counter_doesNotGoBelowMinValue() {
    var count = MIN_VALUE

    composeTestRule.setContent { Counter(count = count, onCounterChange = { count = it }) }

    // Perform click on - button
    composeTestRule.onNodeWithTag(REMOVE).performClick()

    // Verify count does not go below min value
    composeTestRule.runOnIdle { assert(count == MIN_VALUE) }
    composeTestRule.onNodeWithTag(NUMBER).assertTextEquals(MIN_VALUE.toString())
  }
}
