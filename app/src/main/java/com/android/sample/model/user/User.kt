package com.android.sample.model.user

import com.android.sample.model.fridge.FridgeItem

/**
 * @param uid unique id of the user
 * @param userName user name that will be displayed in the account page
 * @param profilePictureUrl profile picture that will be displayed in the account page
 * @param fridge list of ingredients
 * @param likedRecipes list of liked recipes
 * @param createdRecipes list of created recipes
 */
data class User(
    val uid: String,
    val userName: String,
    val profilePictureUrl: String,
    val fridge: List<FridgeItem>,
    val likedRecipes: List<String>,
    val createdRecipes: List<String>
)
