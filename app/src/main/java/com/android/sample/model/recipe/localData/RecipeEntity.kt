package com.android.sample.model.recipe.localData

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe")
data class RecipeEntity(
    @PrimaryKey val uid: String,
    val name: String,
    val category: String? = null,
    val origin: String? = null,
    val instructions: String,
    val strMealThumbUrl: String,
    val ingredientsAndMeasurements: String,
    val time: String? = null,
    val difficulty: String? = null,
    val price: String? = null,
    var url: String? = null
)
