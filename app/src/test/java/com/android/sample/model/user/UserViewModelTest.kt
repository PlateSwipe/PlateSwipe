package com.android.sample.model.user

import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.recipe.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import okhttp3.Call
import org.junit.Assert.assertEquals
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

  private val userToUnit: (User) -> Unit = { user -> { user } }

  private lateinit var mockUserRepository: UserRepository
  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var mockCurrentUser: FirebaseUser

  private lateinit var userViewModel: UserViewModel

  private lateinit var mockCall: Call

  val userExample: User = User("001", "Gigel Frone", "", listOf("1"), listOf("2"), listOf("3"))

  val ingredientExample: Ingredient = Ingredient(133L, "apple")
  val recipeExample: Recipe =
      Recipe(123L, "recipe1", null, null, "instructions", "thumb", listOf(123L), listOf("string"))

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
  fun `test get update user calls repository`() {
    userViewModel.updateCurrentUser()
    verify(mockUserRepository).updateUser(any(), any(), any())
  }

  @Test
  fun `test getting sets the values to current user`() {

    // Test with user

    @Suppress("UNCHECKED_CAST")
    val clazz1: Class<Function1<User, Unit>> = Function1::class.java as Class<Function1<User, Unit>>

    val onSuccessCaptor: ArgumentCaptor<Function1<User, Unit>> = ArgumentCaptor.forClass(clazz1)

    doNothing().`when`(mockUserRepository).getUserById(any(), capture(onSuccessCaptor), any())

    userViewModel.getCurrentUser()

    onSuccessCaptor.value.invoke(userExample)

    assertEquals(userViewModel.userName.value, userExample.userName)
  }

  @Test
  fun `test user not found adds user to repository`() {

    @Suppress("UNCHECKED_CAST")
    val clazz2: Class<Function1<Exception, Unit>> =
        Function1::class.java as Class<Function1<Exception, Unit>>

    val onFailureCaptor: ArgumentCaptor<Function1<Exception, Unit>> =
        ArgumentCaptor.forClass(clazz2)

    doNothing().`when`(mockUserRepository).getUserById(any(), any(), capture(onFailureCaptor))

    userViewModel.getCurrentUser()

    onFailureCaptor.value.invoke(Exception())

    verify(mockUserRepository).addUser(any(), any(), any())
  }

  @Test
  fun `test modifies elements correctly`() {
    userViewModel.changeUserName(userExample.userName)
    userViewModel.changeProfilePictureUrl(userExample.profilePictureUrl)
    userViewModel.addIngredientToUserFridge(ingredientExample)
    userViewModel.addRecipeToUserSavedRecipes(recipeExample)
    userViewModel.addRecipeToUserCreatedRecipes(recipeExample)

    assertEquals(userViewModel.userName.value, userExample.userName)
    assertEquals(userViewModel.profilePictureUrl.value, userExample.profilePictureUrl)
    assertEquals(userViewModel.fridge.value?.get(0)?.name, "apple")
    assertEquals(userViewModel.savedRecipes.value?.get(0)?.idMeal, 123L)
    assertEquals(userViewModel.createdRecipes.value?.get(0)?.idMeal, 123L)

    userViewModel.removeIngredientFromUserFridge(ingredientExample)
    userViewModel.removeRecipeFromUserSavedRecipes(recipeExample)
    userViewModel.removeRecipeFromUserCreatedRecipes(recipeExample)

    assertEquals(userViewModel.fridge.value?.count(), 0)
    assertEquals(userViewModel.savedRecipes.value?.count(), 0)
    assertEquals(userViewModel.createdRecipes.value?.count(), 0)
  }
}
