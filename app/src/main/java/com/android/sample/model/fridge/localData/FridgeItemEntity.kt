package com.android.sample.model.fridge.localData

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.android.sample.model.fridge.FridgeItem
import com.android.sample.resources.C.Tag.FRIDGE_DB_COL_NAME_EXPIRATION_DATE
import com.android.sample.resources.C.Tag.FRIDGE_DB_COL_NAME_ID
import com.android.sample.resources.C.Tag.FRIDGE_DB_COL_NAME_QTY
import com.android.sample.resources.C.Tag.FRIDGE_DB_COL_NAME_TABLE
import java.time.LocalDate

@Entity(
    tableName = FRIDGE_DB_COL_NAME_TABLE,
    primaryKeys = [FRIDGE_DB_COL_NAME_ID, FRIDGE_DB_COL_NAME_EXPIRATION_DATE])
data class FridgeItemEntity(
    @ColumnInfo(name = FRIDGE_DB_COL_NAME_ID) val id: String,
    @ColumnInfo(name = FRIDGE_DB_COL_NAME_QTY) val quantity: Int,
    @ColumnInfo(name = FRIDGE_DB_COL_NAME_EXPIRATION_DATE) val expirationDate: LocalDate
)

fun FridgeItemEntity.toFridgeItem() = FridgeItem(id, quantity, expirationDate)

fun FridgeItem.toFridgeItemEntity() = FridgeItemEntity(id, quantity, expirationDate)
