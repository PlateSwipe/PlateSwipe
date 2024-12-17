package com.android.sample.model.categoryIngredient

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalCategoryIngredientRepositoryTest {
  private val context: Context = ApplicationProvider.getApplicationContext()
  private lateinit var localCategoryIngredientRepository: LocalCategoryIngredientRepository

  @Before
  fun setUp() {
    localCategoryIngredientRepository = LocalCategoryIngredientRepository(context)
  }

  @Test
  fun testSearchIsSuccess() = runTest {
    val query = "beef"

    var foundFirst: String? = null
    var exception: Exception? = null

    localCategoryIngredientRepository.searchCategory(
        query,
        onSuccess = {
          assert(it.isNotEmpty())
          assertEquals(it[0], query)

          foundFirst = it[0]
        },
        onFailure = { exception = it },
        20)

    // we need to check these after otherwise we have
    // no guarantee that the callbacks have been called
    assertNull(exception)
    assertNotNull(foundFirst)
  }
}
