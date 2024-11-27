package com.android.sample.model.ingredient.localData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredient")
data class IngredientEntity(
    @PrimaryKey @ColumnInfo(name = "uid") val uid: String,
    @ColumnInfo(name = "barcode") val barCode: Long? = null,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "brands") val brands: String? = null,
    @ColumnInfo(name = "quantity") val quantity: String? = null,
    @ColumnInfo(name = "categories") val categories: String,
    @ColumnInfo(name = "images") val images: String
)
