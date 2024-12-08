package com.android.sample.model.ingredient.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.localData.IngredientDAO
import com.android.sample.model.ingredient.localData.RoomIngredientRepository
import com.android.sample.model.ingredient.toEntity
import com.android.sample.ui.utils.testIngredients
import junit.framework.TestCase.fail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class RoomIngredientRepositoryTest {

  @Mock private lateinit var mockIngredientDAO: IngredientDAO
  private lateinit var roomIngredientRepository: RoomIngredientRepository
  private val testDispatcher = StandardTestDispatcher()

  private lateinit var ingredient: Ingredient

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    roomIngredientRepository = RoomIngredientRepository(mockIngredientDAO, testDispatcher)
    ingredient = testIngredients[0]
  }

  @After
  fun tearDown() {
    // Clean up the dispatcher
    Dispatchers.resetMain()
  }

  @Test
  fun addCallIngredientDAO() =
      runTest(testDispatcher) {
        roomIngredientRepository.add(ingredient)
        testScheduler.advanceUntilIdle()
        verify(mockIngredientDAO).insert(ingredient.toEntity())
      }

  @Test
  fun updateCallIngredientDAO() =
      runTest(testDispatcher) {
        roomIngredientRepository.update(ingredient)
        testScheduler.advanceUntilIdle()
        verify(mockIngredientDAO).update(ingredient.toEntity())
      }

  @Test
  fun deleteCallIngredientDAO() =
      runTest(testDispatcher) {
        roomIngredientRepository.delete(ingredient)
        testScheduler.advanceUntilIdle()
        verify(mockIngredientDAO).delete(ingredient.toEntity())
      }

  @Test
  fun getAllCallIngredientDAO() =
      runTest(testDispatcher) {
        roomIngredientRepository.getAll({ assert(true) }, { fail("Fail") })
        `when`(mockIngredientDAO.getAll()).thenReturn(listOf(ingredient.toEntity()))
        testScheduler.advanceUntilIdle()
        verify(mockIngredientDAO).getAll()
      }

  @Test
  fun getCallIngredientDAO() =
      runTest(testDispatcher) {
        roomIngredientRepository.getByBarcode(
            ingredient.barCode!!, { assert(true) }, { fail("Fail") })
        `when`(mockIngredientDAO.get(ingredient.barCode!!)).thenReturn(ingredient.toEntity())
        testScheduler.advanceUntilIdle()
        verify(mockIngredientDAO).get(ingredient.barCode!!)
      }
}
