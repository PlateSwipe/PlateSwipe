package com.android.sample.model.ingredient.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.ingredient.localData.Converters
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConvertersTest {

  private lateinit var converters: Converters
  private lateinit var category: List<String>
  private lateinit var images: Map<String, String>

  @Before
  fun setup() {
    converters = Converters()
    category = listOf("category1", "category2")
    images = mapOf("image1" to "url1", "image2" to "url2")
  }

  @Test
  fun fromStringList() {
    val result = converters.fromStringList(category)
    assertEquals("[\"category1\",\"category2\"]", result)
  }

  @Test
  fun toStringList() {
    val result = converters.toStringList("[\"category1\",\"category2\"]")
    assertEquals(category, result)
  }

  @Test
  fun listDoubleWay() {
    val result = converters.toStringList(converters.fromStringList(category))
    assertEquals(category, result)
  }

  @Test
  fun fromMap() {
    val result = converters.fromMap(images)
    assertEquals("{\"image1\":\"url1\",\"image2\":\"url2\"}", result)
  }

  @Test
  fun toMap() {
    val result = converters.toMap("{\"image1\":\"url1\",\"image2\":\"url2\"}")
    assertEquals(images, result)
  }

  @Test
  fun mapDoubleWay() {
    val result = converters.toMap(converters.fromMap(images))
    assertEquals(images, result)
  }
}
