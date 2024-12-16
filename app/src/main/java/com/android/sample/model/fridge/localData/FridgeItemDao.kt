package com.android.sample.model.fridge.localData

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.android.sample.resources.C.Tag.FRIDGE_DB_COL_NAME_EXPIRATION_DATE
import com.android.sample.resources.C.Tag.FRIDGE_DB_COL_NAME_ID
import com.android.sample.resources.C.Tag.FRIDGE_DB_COL_NAME_QTY
import com.android.sample.resources.C.Tag.FRIDGE_DB_COL_NAME_TABLE
import java.time.LocalDate

@Dao
interface FridgeItemDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(fridgeItem: FridgeItemEntity)

  @Delete suspend fun delete(fridgeItem: FridgeItemEntity)

  @Query("SELECT * FROM $FRIDGE_DB_COL_NAME_TABLE") suspend fun getAll(): List<FridgeItemEntity>

  @Query(
      """
        UPDATE $FRIDGE_DB_COL_NAME_TABLE 
        SET $FRIDGE_DB_COL_NAME_EXPIRATION_DATE = :newExpirationDate,
            $FRIDGE_DB_COL_NAME_QTY = :newQuantity
        WHERE $FRIDGE_DB_COL_NAME_ID = :id
          AND $FRIDGE_DB_COL_NAME_EXPIRATION_DATE = :currentExpirationDate
    """)
  suspend fun updateExpirationDate(
      id: String,
      currentExpirationDate: LocalDate,
      newExpirationDate: LocalDate,
      newQuantity: Int
  ): Int

  @Query(
      """
        SELECT * FROM $FRIDGE_DB_COL_NAME_TABLE 
        WHERE $FRIDGE_DB_COL_NAME_ID = :id 
          AND $FRIDGE_DB_COL_NAME_EXPIRATION_DATE = :expirationDate
    """)
  suspend fun getByIdAndExpirationDate(id: String, expirationDate: LocalDate): FridgeItemEntity?

  @Update suspend fun update(fridgeItem: FridgeItemEntity)
}
