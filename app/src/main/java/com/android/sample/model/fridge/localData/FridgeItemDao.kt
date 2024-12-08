package com.android.sample.model.fridge.localData

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.sample.resources.C.Tag.FRIDGE_DB_COL_NAME_TABLE

@Dao
interface FridgeItemDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(fridgeItem: FridgeItemEntity)

  @Delete suspend fun delete(fridgeItem: FridgeItemEntity)

  @Query("SELECT * FROM $FRIDGE_DB_COL_NAME_TABLE") suspend fun getAll(): List<FridgeItemEntity>
}
