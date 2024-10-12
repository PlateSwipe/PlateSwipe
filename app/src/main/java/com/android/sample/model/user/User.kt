package com.android.sample.model.user

/**
 * @param uid unique id of the user
 * @param userName user name that will be displayed in the account page
 * @param profilePictureUrl profile picture that will be displayed in the account page
 * @param fridge list of ingredients
 * @param savedRecipes list of saved/liked recipes
 * @param createdRecipes list of created recipes
 */
data class User(
    val uid: String,
    val userName: String,
    val profilePictureUrl: String,
    val fridge: List<Long>,
    val savedRecipes: List<Long>,
    val createdRecipes: List<Long>
)
