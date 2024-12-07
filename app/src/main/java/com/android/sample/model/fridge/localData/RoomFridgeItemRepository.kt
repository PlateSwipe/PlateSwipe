package com.android.sample.model.fridge.localData

import com.android.sample.model.fridge.FridgeItem
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
}
