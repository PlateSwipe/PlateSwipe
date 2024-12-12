package com.android.sample.model.fridge.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.fridge.FridgeItem
import com.android.sample.model.fridge.localData.FridgeItemDao
import com.android.sample.model.fridge.localData.RoomFridgeItemRepository
import com.android.sample.model.fridge.localData.toFridgeItemEntity
import java.time.LocalDate
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
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class RoomFridgeItemRepositoryTest {

  @Mock private lateinit var mockFridgeItemDAO: FridgeItemDao

  private lateinit var roomFridgeItemRepository: RoomFridgeItemRepository

  private val testDispatcher = StandardTestDispatcher()

  private lateinit var fridgeItem: FridgeItem

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    roomFridgeItemRepository = RoomFridgeItemRepository(mockFridgeItemDAO, testDispatcher)
    val date = LocalDate.of(2021, 10, 10)
    fridgeItem = FridgeItem("id", 5, date)
  }

  @After
  fun tearDown() {
    // Clean up the dispatcher
    Dispatchers.resetMain()
  }

  @Test
  fun addCallFridgeItemDAO() =
      runTest(testDispatcher) {
        roomFridgeItemRepository.add(fridgeItem)
        testScheduler.advanceUntilIdle()
        verify(mockFridgeItemDAO).insert(fridgeItem.toFridgeItemEntity())
      }

  @Test
  fun deleteCallFridgeItemDAO() =
      runTest(testDispatcher) {
        roomFridgeItemRepository.delete(fridgeItem)
        testScheduler.advanceUntilIdle()
        verify(mockFridgeItemDAO).delete(fridgeItem.toFridgeItemEntity())
      }

  @Test
  fun getAllCallFridgeItemDAOOnSuccess() =
      runTest(testDispatcher) {
        var onSuccess = false
        `when`(mockFridgeItemDAO.getAll()).thenReturn(listOf(fridgeItem.toFridgeItemEntity()))
        roomFridgeItemRepository.getAll({ onSuccess = true }, { throw Exception("Fail") })
        testScheduler.advanceUntilIdle()
        verify(mockFridgeItemDAO).getAll()
        assert(onSuccess)
      }

  @Test
  fun getAllCallFridgeItemDaoOnFailure() =
      runTest(testDispatcher) {
        var onFailure = false
        `when`(mockFridgeItemDAO.getAll()).thenThrow(RuntimeException("Fail"))
        roomFridgeItemRepository.getAll({ throw Exception() }, { onFailure = true })
        testScheduler.advanceUntilIdle()
        verify(mockFridgeItemDAO).getAll()
        assert(onFailure)
      }

  @Test
  fun testUpdateFridgeItem() =
      runTest(testDispatcher) {
        val id = fridgeItem.id
        val currentExpirationDate = LocalDate.of(2020, 10, 10)
        val newExpirationDate = fridgeItem.expirationDate
        val newQuantity = 5

        `when`(mockFridgeItemDAO.getByIdAndExpirationDate(id, newExpirationDate))
            .thenReturn(fridgeItem.toFridgeItemEntity())
        `when`(mockFridgeItemDAO.update(any())).thenAnswer { it.arguments[0] }
        `when`(mockFridgeItemDAO.delete(any())).thenAnswer { it.arguments[0] }
        roomFridgeItemRepository.updateFridgeItem(
            id, currentExpirationDate, newExpirationDate, newQuantity)
        testScheduler.advanceUntilIdle()
        verify(mockFridgeItemDAO).update(any())
        verify(mockFridgeItemDAO).delete(any())
      }

  @Test
  fun testUpdateFridgeItemI() =
      runTest(testDispatcher) {
        val id = fridgeItem.id
        val currentExpirationDate = LocalDate.of(2020, 10, 10)
        val newExpirationDate = fridgeItem.expirationDate
        val newQuantity = 5

        `when`(mockFridgeItemDAO.updateExpirationDate(any(), any(), any(), any())).thenAnswer {
          it.arguments[0]
        }
        roomFridgeItemRepository.updateFridgeItem(
            id, currentExpirationDate, newExpirationDate, newQuantity)
        testScheduler.advanceUntilIdle()
        verify(mockFridgeItemDAO).updateExpirationDate(any(), any(), any(), any())
      }

  @Test
  fun testUpsetFridge() =
      runTest(testDispatcher) {
        `when`(mockFridgeItemDAO.getByIdAndExpirationDate(any(), any()))
            .thenReturn(fridgeItem.toFridgeItemEntity())
        `when`(mockFridgeItemDAO.update(any())).thenAnswer { it.arguments[0] }
        roomFridgeItemRepository.upsertFridgeItem(fridgeItem)
        testScheduler.advanceUntilIdle()
        verify(mockFridgeItemDAO).update(any())
      }

  @Test
  fun testUpsetFridgeI() =
      runTest(testDispatcher) {
        `when`(mockFridgeItemDAO.getByIdAndExpirationDate(any(), any())).thenReturn(null)
        `when`(mockFridgeItemDAO.insert(any())).thenAnswer { it.arguments[0] }
        roomFridgeItemRepository.upsertFridgeItem(fridgeItem)
        testScheduler.advanceUntilIdle()
        verify(mockFridgeItemDAO).insert(any())
      }

  @Test
  fun testUpdateFridge() =
      runTest(testDispatcher) {
        `when`(mockFridgeItemDAO.getByIdAndExpirationDate(any(), any())).thenReturn(null)
        `when`(mockFridgeItemDAO.update(any())).thenAnswer { it.arguments[0] }
        roomFridgeItemRepository.updateFridgeItem(
            fridgeItem.id,
            fridgeItem.expirationDate,
            fridgeItem.expirationDate,
            fridgeItem.quantity)

        testScheduler.advanceUntilIdle()
        verify(mockFridgeItemDAO).update(any())
        verify(mockFridgeItemDAO, times(0)).delete(any())
      }
}
