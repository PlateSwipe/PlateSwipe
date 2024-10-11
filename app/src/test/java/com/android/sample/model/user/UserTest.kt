package com.android.sample.model.user

import org.junit.Before
import org.junit.Test

class UserTest {

  private lateinit var uid: String
  private lateinit var userName: String
  private lateinit var profilePictureUrl: String
  private lateinit var fridge: List<Long>
  private lateinit var savedRecipes: List<Long>
  private lateinit var createdRecipes: List<Long>

  @Before
  fun setUp() {
    uid = "001"
    userName = "Gigel Frone"
    profilePictureUrl = ""
    fridge = listOf(1L)
    savedRecipes = listOf(2L)
    createdRecipes = listOf(3L)
  }

  @Test
  fun createUserTest() {
    val user = User(uid, userName, profilePictureUrl, fridge, savedRecipes, createdRecipes)

    assert(user.uid == "001")
    assert(user.userName == "Gigel Frone")
    assert(user.profilePictureUrl == "")
    assert(user.fridge == listOf(1L))
    assert(user.savedRecipes == listOf(2L))
    assert(user.createdRecipes == listOf(3L))
  }
}
