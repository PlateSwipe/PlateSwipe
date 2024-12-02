package com.android.sample.model.recipe.localData

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecipeDAO {

  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(recipe: RecipeEntity)

  @Update suspend fun update(recipe: RecipeEntity)

  @Delete suspend fun delete(recipe: RecipeEntity)

  @Query("SELECT * FROM recipe") suspend fun getAll(): List<RecipeEntity>

  @Query("DELETE FROM recipe") suspend fun deleteAll()
}
