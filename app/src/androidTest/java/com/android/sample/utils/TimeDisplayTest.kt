package com.android.sample.utils

import android.util.Log
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.ui.utils.reformatTime
import org.junit.Rule
import org.junit.Test

class TimeDisplayTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testReformatTime_lessThanAnHour() {
    composeTestRule.setContent {
      val result = reformatTime(0f)
      assert(result == "0 min")
    }
  }

  @Test
  fun testReformatTime_moreThanAnHour() {
    composeTestRule.setContent {
      val result = reformatTime(60f)
      assert(result == "1 h 0 min")
    }
  }
}
