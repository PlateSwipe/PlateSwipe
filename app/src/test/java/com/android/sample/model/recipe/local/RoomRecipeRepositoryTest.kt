package com.android.sample.model.recipe.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.localData.RecipeDAO
import com.android.sample.model.recipe.localData.RoomRecipeRepository
import com.android.sample.model.recipe.toEntity
import com.android.sample.ui.utils.testRecipes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class RoomRecipeRepositoryTest {

  @Mock private lateinit var mockRecipeDAO: RecipeDAO
  private lateinit var roomRecipeRepository: RoomRecipeRepository
  private val testDispatcher = StandardTestDispatcher()

  private lateinit var recipe: Recipe

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    roomRecipeRepository = RoomRecipeRepository(mockRecipeDAO, testDispatcher)
    recipe = testRecipes[0]
  }

  @After
  fun tearDown() {
    // Clean up the dispatcher
    Dispatchers.resetMain()
  }

  @Test
  fun addCallRecipeDAO() =
      runTest(testDispatcher) {
        roomRecipeRepository.add(recipe)
        testScheduler.advanceUntilIdle()
        verify(mockRecipeDAO).insert(recipe.toEntity())
      }

  @Test
  fun updateCallRecipeDAO() =
      runTest(testDispatcher) {
        roomRecipeRepository.update(recipe)
        testScheduler.advanceUntilIdle()
        verify(mockRecipeDAO).update(recipe.toEntity())
      }

  @Test
  fun deleteCallRecipeDAO() =
      runTest(testDispatcher) {
        roomRecipeRepository.delete(recipe)
        testScheduler.advanceUntilIdle()
        verify(mockRecipeDAO).delete(recipe.toEntity())
      }

  @Test
  fun deleteAllCallRecipeDAO() =
      runTest(testDispatcher) {
        var onSuccess = false
        roomRecipeRepository.deleteAll({ onSuccess = true }, { fail("Fail") })
        testScheduler.advanceUntilIdle()
        verify(mockRecipeDAO).deleteAll()
        assert(onSuccess)
      }

  @Test
  fun getAllCallRecipeDAO() =
      runTest(testDispatcher) {
        var onSuccess = false
        roomRecipeRepository.getAll({ onSuccess = true }, { fail("Fail") })
        `when`(mockRecipeDAO.getAll()).thenReturn(testRecipes.map { it.toEntity() })
        testScheduler.advanceUntilIdle()
        verify(mockRecipeDAO).getAll()
        assert(onSuccess)
      }
}
