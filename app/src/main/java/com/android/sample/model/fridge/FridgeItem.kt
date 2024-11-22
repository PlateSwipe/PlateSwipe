package com.android.sample.model.fridge

import java.time.LocalDate

data class FridgeItem(
    val id: String? = null,
    val quantity: String? = null,
    val expirationDate: LocalDate? = null
)
