package com.android.sample.model.user

import com.android.sample.model.fridge.FridgeItem
import com.android.sample.ui.utils.testUsers
import org.junit.Before
import org.junit.Test

class UserTest {

  private lateinit var uid: String
  private lateinit var userName: String
  private lateinit var profilePictureUrl: String
  private lateinit var fridge: List<FridgeItem>
  private lateinit var likedRecipes: List<String>
  private lateinit var createdRecipes: List<String>

  @Before
  fun setUp() {
    uid = testUsers[0].uid
    userName = testUsers[0].userName
    profilePictureUrl = testUsers[0].profilePictureUrl
    fridge = testUsers[0].fridge
    likedRecipes = testUsers[0].likedRecipes
    createdRecipes = testUsers[0].createdRecipes
  }

  @Test
  fun createUserTest() {
    val user = User(uid, userName, profilePictureUrl, fridge, likedRecipes, createdRecipes)

    assert(user.uid == testUsers[0].uid)
    assert(user.userName == testUsers[0].userName)
    assert(user.profilePictureUrl == testUsers[0].profilePictureUrl)
    assert(user.fridge == testUsers[0].fridge)
    assert(user.likedRecipes == testUsers[0].likedRecipes)
    assert(user.createdRecipes == testUsers[0].createdRecipes)
  }
}
