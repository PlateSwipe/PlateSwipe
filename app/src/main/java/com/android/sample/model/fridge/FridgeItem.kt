package com.android.sample.model.fridge

import java.util.Date

data class FridgeItem(
    val id: String? = null,
    val quantity: Int? = null,
    val expirationDate: Date? = null
)
