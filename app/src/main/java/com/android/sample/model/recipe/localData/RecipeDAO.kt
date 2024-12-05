package com.android.sample.model.recipe.localData

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecipeDAO {

  /**
   * Inserts a recipe into the database. If the recipe already exists, it replaces it.
   *
   * @param recipe The recipe to insert.
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(recipe: RecipeEntity)

  /**
   * Updates an existing recipe in the database.
   *
   * @param recipe The recipe to update.
   */
  @Update suspend fun update(recipe: RecipeEntity)

  /**
   * Deletes a recipe from the database.
   *
   * @param recipe The recipe to delete.
   */
  @Delete suspend fun delete(recipe: RecipeEntity)

  /**
   * Retrieves all recipes from the database.
   *
   * @return A list of all recipes.
   */
  @Query("SELECT * FROM recipe") suspend fun getAll(): List<RecipeEntity>

  /** Deletes all recipes from the database. */
  @Query("DELETE FROM recipe") suspend fun deleteAll()
}
