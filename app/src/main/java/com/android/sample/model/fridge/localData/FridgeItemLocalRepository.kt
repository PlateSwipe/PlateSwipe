package com.android.sample.model.fridge.localData

import com.android.sample.model.fridge.FridgeItem

interface FridgeItemLocalRepository {

  fun add(fridgeItem: FridgeItem)

  fun delete(fridgeItem: FridgeItem)

  fun getAll(onSuccess: (List<FridgeItem>) -> Unit, onFailure: (Exception) -> Unit)
}
