package com.android.sample.model.fridge.localData

import com.android.sample.model.fridge.FridgeItem
import java.time.LocalDate

interface FridgeItemLocalRepository {

  fun add(fridgeItem: FridgeItem)

  fun delete(fridgeItem: FridgeItem)

  fun getAll(onSuccess: (List<FridgeItem>) -> Unit, onFailure: (Exception) -> Unit)

  fun updateFridgeItem(
      id: String,
      currentExpirationDate: LocalDate,
      newExpirationDate: LocalDate,
      newQuantity: Int
  )

  fun upsertFridgeItem(fridgeItem: FridgeItem)
}
