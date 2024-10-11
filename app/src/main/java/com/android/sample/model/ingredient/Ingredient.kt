package com.android.sample.model.ingredient

data class Ingredient(
    val barCode: Long,
    val name: String,
    val quantity: Double,
    val quantityMeasurement: QuantityMeasurement,
    ) {}

enum class QuantityMeasurement {
    GRAM,
    KILOGRAM,
    LITER,
    MILLILITER,
    PIECE
}