package com.android.sample.model.fridge

import java.time.LocalDate

data class FridgeItem(val id: String, val quantity: Int, val expirationDate: LocalDate)
