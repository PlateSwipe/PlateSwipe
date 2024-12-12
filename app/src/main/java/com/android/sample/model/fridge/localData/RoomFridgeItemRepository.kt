package com.android.sample.model.fridge.localData

import android.util.Log
import com.android.sample.model.fridge.FridgeItem
import java.time.LocalDate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class RoomFridgeItemRepository(
    private val fridgeItemDao: FridgeItemDao,
    private val dispatcher: CoroutineDispatcher
) : FridgeItemLocalRepository {

  override fun add(fridgeItem: FridgeItem) {
    CoroutineScope(dispatcher).launch { fridgeItemDao.insert(fridgeItem.toFridgeItemEntity()) }
  }

  override fun delete(fridgeItem: FridgeItem) {
    CoroutineScope(dispatcher).launch { fridgeItemDao.delete(fridgeItem.toFridgeItemEntity()) }
  }

  override fun getAll(onSuccess: (List<FridgeItem>) -> Unit, onFailure: (Exception) -> Unit) {
    CoroutineScope(dispatcher).launch {
      try {
        val fridgeItems = fridgeItemDao.getAll().map { it.toFridgeItem() }
        onSuccess(fridgeItems)
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }

  override fun updateFridgeItem(
      id: String,
      currentExpirationDate: LocalDate,
      newExpirationDate: LocalDate,
      newQuantity: Int
  ) {
    CoroutineScope(dispatcher).launch {
      val existingItem = fridgeItemDao.getByIdAndExpirationDate(id, newExpirationDate)
      if (existingItem != null) {
        val updatedItem = existingItem.copy(quantity = newQuantity + existingItem.quantity)
        fridgeItemDao.update(updatedItem)
        fridgeItemDao.delete(FridgeItemEntity(id, newQuantity, currentExpirationDate))
        Log.d(
            "FridgeItemRepository",
            "Updated fridge item with id: $id, new expiration date: $newExpirationDate, new quantity: $newQuantity")
      } else {
        fridgeItemDao.updateExpirationDate(
            id, currentExpirationDate, newExpirationDate, newQuantity)
        Log.d(
            "FridgeItemRepository",
            "Fridge item with id: $id and expiration date: $currentExpirationDate not found")
      }
    }
  }

  override fun upsertFridgeItem(fridgeItem: FridgeItem) {
    CoroutineScope(dispatcher).launch {
      val existingItem =
          fridgeItemDao.getByIdAndExpirationDate(fridgeItem.id, fridgeItem.expirationDate)
      if (existingItem != null) {
        // Combine quantities
        val updatedItem = existingItem.copy(quantity = existingItem.quantity + fridgeItem.quantity)
        fridgeItemDao.update(updatedItem)
        Log.d(
            "FridgeItemRepository",
            "Updated fridge item with id: ${fridgeItem.id}, new quantity: ${updatedItem.quantity}")
      } else {
        fridgeItemDao.insert(fridgeItem.toFridgeItemEntity())
        Log.d("FridgeItemRepository", "Inserted new fridge item with id: ${fridgeItem.id}")
      }
    }
  }
}
