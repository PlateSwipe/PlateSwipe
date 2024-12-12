package com.android.sample.model.fridge.localData

import com.android.sample.model.fridge.FridgeItem
import java.time.LocalDate

interface FridgeItemLocalRepository {

  /**
   * Adds a fridge item to the repository.
   *
   * @param fridgeItem The fridge item to be added.
   */
  fun add(fridgeItem: FridgeItem)
  /**
   * Deletes a fridge item from the repository.
   *
   * @param fridgeItem The fridge item to be deleted.
   */
  fun delete(fridgeItem: FridgeItem)
  /**
   * Retrieves all fridge items from the repository.
   *
   * @param onSuccess Callback function to be invoked with the list of retrieved fridge items.
   * @param onFailure Callback function to be invoked with an exception if an error occurs.
   */
  fun getAll(onSuccess: (List<FridgeItem>) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Updates a fridge item in the repository.
   *
   * @param id The ID of the fridge item to be updated.
   * @param currentExpirationDate The current expiration date of the fridge item.
   * @param newExpirationDate The new expiration date of the fridge item.
   * @param newQuantity The new quantity of the fridge item.
   */
  fun updateFridgeItem(
      id: String,
      currentExpirationDate: LocalDate,
      newExpirationDate: LocalDate,
      newQuantity: Int
  )
  /**
   * Inserts or updates a fridge item in the repository.
   *
   * @param fridgeItem The fridge item to be upserted.
   */
  fun upsertFridgeItem(fridgeItem: FridgeItem)
}
