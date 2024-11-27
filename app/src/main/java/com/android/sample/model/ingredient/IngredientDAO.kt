package com.android.sample.model.ingredient

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * Data Access Object for the IngredientEntity class.
 * Provides methods for querying the Ingredient local database.
 */
@DAO
interface IngredientDAO {

    /**
     * Retrieves all ingredients from the database.
     *
     * @return A list of all `IngredientEntity` objects.
     */
    @Query("SELECT * FROM ingrediententity")
    fun getAll(): List<IngredientEntity>

    /**
     * Loads all ingredients with the specified UIDs.
     *
     * @param ingredientUIDs A list of ingredient UIDs to load.
     * @return A list of `IngredientEntity` objects with the specified UIDs.
     */
    @Query("SELECT * FROM ingrediententity WHERE uid IN (:ingredientUIDs)")
    fun loadAllByIds(ingredientUIDs: List<String>): List<IngredientEntity>

    /**
     * Inserts a new ingredient into the database.
     *
     * @param ingredient The `IngredientEntity` object to insert.
     */
    @Insert
    fun insert(ingredient: IngredientEntity)

    /**
     * Deletes an ingredient from the database.
     *
     * @param ingredient The `IngredientEntity` object to delete.
     */
    @Delete
    fun delete(ingredient: IngredientEntity)
}