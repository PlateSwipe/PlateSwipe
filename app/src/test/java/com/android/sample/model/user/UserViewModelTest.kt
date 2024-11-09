package com.android.sample.model.user

import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.recipe.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import okhttp3.Call
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify

class UserViewModelTest {

  @Suppress("UNCHECKED_CAST")
  private val onSuccessClass: Class<Function1<User, Unit>> =
      Function1::class.java as Class<Function1<User, Unit>>

  @Suppress("UNCHECKED_CAST")
  private val onFailureClass: Class<Function1<Exception, Unit>> =
      Function1::class.java as Class<Function1<Exception, Unit>>

  private lateinit var mockUserRepository: UserRepository
  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var mockCurrentUser: FirebaseUser

  private lateinit var userViewModel: UserViewModel

  private lateinit var mockCall: Call

  private val userExample: User =
      User("001", "Gigel Frone", "", emptyList(), emptyList(), emptyList())

  private val ingredientExample: Ingredient =
      Ingredient(
          barCode = 133L,
          name = "apple",
          brands = "",
          quantity = "",
          categories = listOf(""),
          images = listOf(""))
  private val recipeExample: Recipe =
      Recipe(
          "123",
          "recipe1",
          null,
          null,
          "instructions",
          "thumb",
          listOf(Pair("2134", "4231")),
      )

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    mockCall = mock(Call::class.java)

    mockUserRepository = mock(UserRepository::class.java)
    mockFirebaseAuth = mock(FirebaseAuth::class.java)
    mockCurrentUser = mock(FirebaseUser::class.java)

    `when`(mockFirebaseAuth.currentUser).thenReturn(mockCurrentUser)
    `when`(mockCurrentUser.uid).thenReturn("001")

    userViewModel = UserViewModel(mockUserRepository, mockFirebaseAuth)
  }

  @Test
  fun `test get current user calls repository`() {
    userViewModel.getCurrentUser()
    verify(mockUserRepository).getUserById(any(), any(), any())
  }

  @Test
  fun `test update user calls repository with correct values`() {
    userViewModel.changeUserName(userExample.userName)
    userViewModel.changeProfilePictureUrl(userExample.profilePictureUrl)

    val userCaptor: ArgumentCaptor<User> = ArgumentCaptor.forClass(User::class.java)

    userViewModel.updateCurrentUser()

    verify(mockUserRepository).updateUser(capture(userCaptor), any(), any())

    assertEquals(userCaptor.value.uid, mockCurrentUser.uid)
    assertEquals(userCaptor.value.userName, userExample.userName)
    assertEquals(userCaptor.value.profilePictureUrl, userExample.profilePictureUrl)
  }

  @Test
  fun `test getting sets the values to current user`() {

    val onSuccessCaptor: ArgumentCaptor<Function1<User, Unit>> =
        ArgumentCaptor.forClass(onSuccessClass)

    doNothing().`when`(mockUserRepository).getUserById(any(), capture(onSuccessCaptor), any())

    userViewModel.getCurrentUser()

    onSuccessCaptor.value.invoke(userExample)

    assertEquals(userViewModel.userName.value, userExample.userName)
    assertEquals(userViewModel.profilePictureUrl.value, userExample.profilePictureUrl)
    assertEquals(userViewModel.fridge.value, userExample.fridge)
    assertEquals(userViewModel.likedRecipes.value, userExample.likedRecipes)
    assertEquals(userViewModel.createdRecipes.value, userExample.createdRecipes)
  }

  @Test
  fun `test get throws error on repo failure when adding missing user`() {

    val onFailureCaptorGet: ArgumentCaptor<Function1<Exception, Unit>> =
        ArgumentCaptor.forClass(onFailureClass)

    val onFailureCaptorAdd: ArgumentCaptor<Function1<Exception, Unit>> =
        ArgumentCaptor.forClass(onFailureClass)

    doNothing().`when`(mockUserRepository).getUserById(any(), any(), capture(onFailureCaptorGet))
    doNothing().`when`(mockUserRepository).addUser(any(), any(), capture(onFailureCaptorAdd))

    userViewModel.getCurrentUser()

    onFailureCaptorGet.value.invoke(Exception())

    assertThrows(Exception::class.java) {
      verify(mockUserRepository).addUser(any(), any(), any())
      onFailureCaptorAdd.value.invoke(Exception())
    }
  }

  @Test
  fun `test updating throws error on repo failure`() {

    val onFailureCaptor: ArgumentCaptor<Function1<Exception, Unit>> =
        ArgumentCaptor.forClass(onFailureClass)

    doNothing().`when`(mockUserRepository).updateUser(any(), any(), capture(onFailureCaptor))

    userViewModel.getCurrentUser()

    assertThrows(Exception::class.java) { onFailureCaptor.value.invoke(Exception()) }
  }

  @Test
  fun `test user not found adds user to repository`() {

    val onInitialGetCaptor: ArgumentCaptor<Function1<User, Unit>> =
        ArgumentCaptor.forClass(onSuccessClass)

    val onFailureCaptor: ArgumentCaptor<Function1<Exception, Unit>> =
        ArgumentCaptor.forClass(onFailureClass)

    val addedUserCaptor: ArgumentCaptor<User> = ArgumentCaptor.forClass(User::class.java)

    doNothing().`when`(mockUserRepository).getUserById(any(), capture(onInitialGetCaptor), any())
    userViewModel.getCurrentUser()

    onInitialGetCaptor.value.invoke(userExample)

    doNothing().`when`(mockUserRepository).getUserById(any(), any(), capture(onFailureCaptor))
    doNothing().`when`(mockUserRepository).addUser(capture(addedUserCaptor), any(), any())
    userViewModel.getCurrentUser()

    onFailureCaptor.value.invoke(Exception())

    assertEquals(addedUserCaptor.value.uid, userExample.uid)
    assertEquals(addedUserCaptor.value.userName, userExample.userName)
    assertEquals(addedUserCaptor.value.profilePictureUrl, userExample.profilePictureUrl)
    assertEquals(addedUserCaptor.value.fridge, userExample.fridge)
    assertEquals(addedUserCaptor.value.likedRecipes, userExample.likedRecipes)
    assertEquals(addedUserCaptor.value.createdRecipes, userExample.createdRecipes)
  }

  @Test
  fun `test modifies elements correctly`() {
    userViewModel.changeUserName(userExample.userName)
    userViewModel.changeProfilePictureUrl(userExample.profilePictureUrl)
    userViewModel.addIngredientToUserFridge(ingredientExample)
    userViewModel.addRecipeToUserLikedRecipes(recipeExample)
    userViewModel.addRecipeToUserCreatedRecipes(recipeExample)

    assertEquals(userViewModel.userName.value, userExample.userName)
    assertEquals(userViewModel.profilePictureUrl.value, userExample.profilePictureUrl)
    assertEquals(userViewModel.fridge.value[0].name, "apple")
    assertEquals(userViewModel.likedRecipes.value[0].idMeal, "123")
    assertEquals(userViewModel.createdRecipes.value[0].idMeal, "123")

    userViewModel.removeIngredientFromUserFridge(ingredientExample)
    userViewModel.removeRecipeFromUserLikedRecipes(recipeExample)
    userViewModel.removeRecipeFromUserCreatedRecipes(recipeExample)

    assertEquals(userViewModel.fridge.value.count(), 0)
    assertEquals(userViewModel.likedRecipes.value.count(), 0)
    assertEquals(userViewModel.createdRecipes.value.count(), 0)
  }
}
