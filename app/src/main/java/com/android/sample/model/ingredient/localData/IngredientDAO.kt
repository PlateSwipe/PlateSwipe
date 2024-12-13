package com.android.sample.model.ingredient.localData

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * Data Access Object for the IngredientEntity class. Provides methods for querying the Ingredient
 * local database.
 */
@Dao
interface IngredientDAO {

  /**
   * Inserts a new ingredient into the database.
   *
   * @param ingredient The `IngredientEntity` object to insert.
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(ingredient: IngredientEntity)

  /**
   * Updates an existing ingredient in the database.
   *
   * @param ingredient The `IngredientEntity` object to update.
   */
  @Update suspend fun update(ingredient: IngredientEntity)

  /**
   * Deletes an ingredient from the database.
   *
   * @param ingredient The `IngredientEntity` object to delete.
   */
  @Delete suspend fun delete(ingredient: IngredientEntity)

  /**
   * Retrieves all ingredients from the database.
   *
   * @return A list of all `IngredientEntity` objects.
   */
  @Query("SELECT * FROM ingredient") suspend fun getAll(): List<IngredientEntity>

  /** Delete all ingredients from the database. */
  @Query("DELETE FROM ingredient") suspend fun deleteAll()

  /**
   * Retrieves an ingredient by its barcode.
   *
   * @param barcode The barcode of the ingredient.
   * @return The `IngredientEntity` object with the given barcode.
   */
  @Query("SELECT * FROM ingredient WHERE barcode = :barcode")
  suspend fun get(barcode: Long): IngredientEntity?
}
