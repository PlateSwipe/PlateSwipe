package com.android.sample.model.ingredient

data class Ingredient(
    val barCode: Long? = null,
    val name: String,
    val brands: String? = null,
    val quantity: String? = null,
    val categories: List<String>,
    val images: List<String> = listOf("display_normal", "display_thumbnail", "display_small")
)
