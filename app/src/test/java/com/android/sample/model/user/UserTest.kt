package com.android.sample.model.user

import org.junit.Before
import org.junit.Test

class UserTest {

  private lateinit var uid: String
  private lateinit var userName: String
  private lateinit var profilePictureUrl: String
  private lateinit var fridge: List<String>
  private lateinit var likedRecipes: List<String>
  private lateinit var createdRecipes: List<String>

  @Before
  fun setUp() {
    uid = "001"
    userName = "Gigel Frone"
    profilePictureUrl = ""
    fridge = listOf("1")
    likedRecipes = listOf("2")
    createdRecipes = listOf("3")
  }

  @Test
  fun createUserTest() {
    val user = User(uid, userName, profilePictureUrl, fridge, likedRecipes, createdRecipes)

    assert(user.uid == "001")
    assert(user.userName == "Gigel Frone")
    assert(user.profilePictureUrl == "")
    assert(user.fridge == listOf("1"))
    assert(user.likedRecipes == listOf("2"))
    assert(user.createdRecipes == listOf("3"))
  }
}
