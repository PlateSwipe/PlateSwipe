package com.android.sample.model.ingredient

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IngredientEntity(
    @PrimaryKey val uid: String,
    val barCode: Long? = null,
    val name: String,
    val brands: String? = null,
    val quantity: String? = null,
    val categories: List<String>,
    val images: MutableMap<String, String> = mutableMapOf()
)