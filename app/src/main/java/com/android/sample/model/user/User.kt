package com.android.sample.model.user

data class User(
    val uid: String,
    val userName: String,
    val profilePictureUrl: String,
    val fridge: List<Long>,
    val savedRecipes: List<Long>,
    val createdRecipes: List<Long>
)