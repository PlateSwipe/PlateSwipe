package com.android.sample.model.ingredient.localData

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * The Room database for storing ingredients.
 *
 * @Database annotation specifies the entities and version of the database.
 * @TypeConverters annotation registers the TypeConverters class for custom data type conversions.
 */
@Database(entities = [IngredientEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class IngredientDatabase : RoomDatabase() {

  /**
   * Provides access to the IngredientDAO.
   *
   * @return The IngredientDAO instance.
   */
  abstract fun ingredientDao(): IngredientDAO

  /**
   * Retrieves the singleton instance of the IngredientDatabase.
   *
   * @return The singleton instance of the IngredientDatabase.
   */
  companion object {
    @Volatile private var INSTANCE: IngredientDatabase? = null

    fun getDatabase(context: Context): IngredientDatabase {
      // Singleton pattern to prevent multiple instances
      return INSTANCE
          ?: synchronized(this) {
            val instance =
                Room.databaseBuilder(
                        context.applicationContext,
                        IngredientDatabase::class.java,
                        "ingredients_database")
                    .build()
            INSTANCE = instance
            instance
          }
    }
  }
}
