package com.android.sample.model.fridge.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.fridge.localData.Converters
import java.time.LocalDate
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConvertersTest {

  private lateinit var converters: Converters
  private lateinit var date: LocalDate

  @Before
  fun setup() {
    converters = Converters()
    date = LocalDate.of(2021, 10, 10)
  }

  @Test
  fun fromLocalDate() {
    val result = converters.fromLocalDate(date)
    assertEquals("10/10/2021", result)
  }

  @Test
  fun toLocalDate() {
    val result = converters.toLocalDate("10/10/2021")
    assertEquals(date, result)
  }
}
