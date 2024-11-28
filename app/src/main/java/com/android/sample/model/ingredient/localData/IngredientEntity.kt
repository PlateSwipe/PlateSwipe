package com.android.sample.model.ingredient.localData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.sample.resources.C.Tag.INGR_DB_COL_NAME_BARCODE
import com.android.sample.resources.C.Tag.INGR_DB_COL_NAME_BRANDS
import com.android.sample.resources.C.Tag.INGR_DB_COL_NAME_CATEGORIES
import com.android.sample.resources.C.Tag.INGR_DB_COL_NAME_IMAGES
import com.android.sample.resources.C.Tag.INGR_DB_COL_NAME_NAME
import com.android.sample.resources.C.Tag.INGR_DB_COL_NAME_QUANTITY
import com.android.sample.resources.C.Tag.INGR_DB_COL_NAME_TABLE
import com.android.sample.resources.C.Tag.INGR_DB_COL_NAME_UID

@Entity(tableName = INGR_DB_COL_NAME_TABLE)
data class IngredientEntity(
    @PrimaryKey @ColumnInfo(name = INGR_DB_COL_NAME_UID) val uid: String,
    @ColumnInfo(name = INGR_DB_COL_NAME_BARCODE) val barCode: Long? = null,
    @ColumnInfo(name = INGR_DB_COL_NAME_NAME) val name: String,
    @ColumnInfo(name = INGR_DB_COL_NAME_BRANDS) val brands: String? = null,
    @ColumnInfo(name = INGR_DB_COL_NAME_QUANTITY) val quantity: String? = null,
    @ColumnInfo(name = INGR_DB_COL_NAME_CATEGORIES) val categories: String,
    @ColumnInfo(name = INGR_DB_COL_NAME_IMAGES) val images: String
)
