package com.android.sample.model.recipe.localData

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android.sample.resources.C.Tag.RECIPE_DATABASE_NAME

/**
 * The Room database for storing recipes.
 *
 * @Database annotation specifies the entities and version of the database.
 * @TypeConverters annotation specifies the type converters used by the database.
 */
@Database(entities = [RecipeEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class RecipeDatabase : RoomDatabase() {
  /**
   * Provides access to the RecipeDAO for database operations.
   *
   * @return The RecipeDAO instance.
   */
  abstract fun recipeDao(): RecipeDAO

  companion object {
    @Volatile private var INSTANCE: RecipeDatabase? = null

    /**
     * Retrieves the singleton instance of the RecipeDatabase.
     *
     * @param context The context used to create the database.
     * @return The singleton instance of the RecipeDatabase.
     */
    fun getDatabase(context: Context): RecipeDatabase {
      // Singleton pattern to prevent multiple instances
      return INSTANCE
          ?: synchronized(this) {
            val instance =
                Room.databaseBuilder(
                        context.applicationContext,
                        RecipeDatabase::class.java,
                        RECIPE_DATABASE_NAME)
                    .build()
            INSTANCE = instance
            instance
          }
    }
  }
}
