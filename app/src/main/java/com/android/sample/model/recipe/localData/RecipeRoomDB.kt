package com.android.sample.model.recipe.localData

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [RecipeEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class RecipeDatabase : RoomDatabase() {

  abstract fun recipeDao(): RecipeDAO

  companion object {
    @Volatile private var INSTANCE: RecipeDatabase? = null

    fun getDatabase(context: Context): RecipeDatabase {
      // Singleton pattern to prevent multiple instances
      return INSTANCE
          ?: synchronized(this) {
            val instance =
                Room.databaseBuilder(
                        context.applicationContext, RecipeDatabase::class.java, "recipe")
                    .build()
            INSTANCE = instance
            instance
          }
    }
  }
}
