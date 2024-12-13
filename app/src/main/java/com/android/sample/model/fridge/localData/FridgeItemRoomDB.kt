package com.android.sample.model.fridge.localData

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android.sample.resources.C.Tag.FRIDGE_DATABASE_NAME

/**
 * The Room database for storing FridgeItem.
 *
 * @Database annotation specifies the entities and version of the database.
 * @TypeConverters annotation registers the TypeConverters class for custom data type conversions.
 */
@Database(entities = [FridgeItemEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class FridgeItemDatabase : RoomDatabase() {

  /**
   * Provides access to the IngredientDAO.
   *
   * @return The IngredientDAO instance.
   */
  abstract fun fridgeItemDao(): FridgeItemDao

  /**
   * Retrieves the singleton instance of the IngredientDatabase.
   *
   * @return The singleton instance of the IngredientDatabase.
   */
  companion object {
    @Volatile private var INSTANCE: FridgeItemDatabase? = null

    fun getDatabase(context: Context): FridgeItemDatabase {
      // Singleton pattern to prevent multiple instances
      return INSTANCE
          ?: synchronized(this) {
            val instance =
                Room.databaseBuilder(
                        context.applicationContext,
                        FridgeItemDatabase::class.java,
                        FRIDGE_DATABASE_NAME)
                    .build()
            INSTANCE = instance
            instance
          }
    }
  }
}
