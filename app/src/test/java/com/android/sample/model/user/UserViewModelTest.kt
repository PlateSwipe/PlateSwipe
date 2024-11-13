package com.android.sample.model.user

import android.util.Log
import com.android.sample.model.ingredient.FirestoreIngredientRepository
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.recipe.FirestoreRecipesRepository
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
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

class UserViewModelTest {

  @Suppress("UNCHECKED_CAST")
  private val onSuccessClass: Class<Function1<User, Unit>> =
      Function1::class.java as Class<Function1<User, Unit>>

  @Suppress("UNCHECKED_CAST")
  private val onSuccessClassFridge: Class<Function1<Ingredient?, Unit>> =
      Function1::class.java as Class<Function1<Ingredient?, Unit>>

  @Suppress("UNCHECKED_CAST")
  private val onSuccessClassLikedRecipes: Class<Function1<Recipe, Unit>> =
      Function1::class.java as Class<Function1<Recipe, Unit>>

  @Suppress("UNCHECKED_CAST")
  private val onSuccessClassCreatedRecipes: Class<Function1<Recipe, Unit>> =
      Function1::class.java as Class<Function1<Recipe, Unit>>

  @Suppress("UNCHECKED_CAST")
  private val onFailureClass: Class<Function1<Exception, Unit>> =
      Function1::class.java as Class<Function1<Exception, Unit>>

  private lateinit var mockUserRepository: UserRepository
  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var mockCurrentUser: FirebaseUser
  private lateinit var mockIngredientRepository: FirestoreIngredientRepository
  private lateinit var mockRecipeRepository: FirestoreRecipesRepository

  private lateinit var userViewModel: UserViewModel

  private lateinit var mockCall: Call

  private val userExample: User =
      User("001", "Gigel Frone", "", emptyList(), emptyList(), emptyList())

  private val userExample2: User =
      User("001", "Gigelica Frone", "", listOf(Pair("133", 1)), emptyList(), emptyList())

  private val userExample3: User =
      User("001", "Florica Frone", "", emptyList(), listOf("123"), emptyList())

  private val userExample4: User =
      User("001", "Calcev Frone", "", emptyList(), emptyList(), listOf("456"))

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

  private val createdRecipeExample: Recipe =
      Recipe(
          "456",
          "recipe2",
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
    mockIngredientRepository = mock(FirestoreIngredientRepository::class.java)
    mockRecipeRepository = mock(FirestoreRecipesRepository::class.java)

    `when`(mockFirebaseAuth.currentUser).thenReturn(mockCurrentUser)
    `when`(mockCurrentUser.uid).thenReturn("001")

    userViewModel =
        UserViewModel(
            mockUserRepository, mockFirebaseAuth, mockRecipeRepository, mockIngredientRepository)
  }

  @Test
  fun `test get current user calls repository`() {
    userViewModel.getCurrentUser()
    verify(mockUserRepository).getUserById(any(), any(), any())
  }

  @Test
  fun `test select recipe`() {
    userViewModel.selectRecipe(recipe = recipeExample)
    assertEquals(userViewModel.currentRecipe.value, recipeExample)
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
    assertEquals(userViewModel.fridge.value[0].first.name, "apple")
    assertEquals(userViewModel.likedRecipes.value[0].idMeal, "123")
    assertEquals(userViewModel.createdRecipes.value[0].idMeal, "123")

    userViewModel.removeIngredientFromUserFridge(ingredientExample)
    userViewModel.removeRecipeFromUserLikedRecipes(recipeExample)
    userViewModel.removeRecipeFromUserCreatedRecipes(recipeExample)

    assertEquals(userViewModel.fridge.value.count(), 0)
    assertEquals(userViewModel.likedRecipes.value.count(), 0)
    assertEquals(userViewModel.createdRecipes.value.count(), 0)
  }

  @Test
  fun `test correctly adds and removes existing ingredient to count pairs in fridge`() {
    userViewModel.addIngredientToUserFridge(ingredientExample, 2)

    assertEquals(userViewModel.fridge.value[0].first.name, "apple")
    assertEquals(userViewModel.fridge.value[0].second, 2)

    userViewModel.addIngredientToUserFridge(ingredientExample)

    assertEquals(userViewModel.fridge.value[0].second, 3)

    userViewModel.removeIngredientFromUserFridge(ingredientExample, 2)

    assertEquals(userViewModel.fridge.value[0].second, 1)

    assertThrows(IllegalArgumentException::class.java) {
      userViewModel.removeIngredientFromUserFridge(ingredientExample, 2)
    }

    userViewModel.removeIngredientFromUserFridge(ingredientExample)

    assertEquals(userViewModel.fridge.value.count(), 0)

    assertThrows(IllegalArgumentException::class.java) {
      userViewModel.removeIngredientFromUserFridge(ingredientExample)
    }
  }

  @Test
  fun `test parsing of each element in fridge`() {
    val onSuccessCaptor: ArgumentCaptor<Function1<User, Unit>> =
        ArgumentCaptor.forClass(onSuccessClass)
    val onSuccessCaptorFridge: ArgumentCaptor<Function1<Ingredient?, Unit>> =
        ArgumentCaptor.forClass(onSuccessClassFridge)

    doNothing().`when`(mockUserRepository).getUserById(any(), capture(onSuccessCaptor), any())

    doNothing().`when`(mockIngredientRepository).get(any(), capture(onSuccessCaptorFridge), any())

    userViewModel.getCurrentUser()

    onSuccessCaptor.value.invoke(userExample2)
    onSuccessCaptorFridge.value.invoke(ingredientExample)

    assertEquals(userExample2.userName, userViewModel.userName.value)
    assertEquals(userExample2.profilePictureUrl, userViewModel.profilePictureUrl.value)
    assertEquals(listOf(Pair(ingredientExample, 1)), userViewModel.fridge.value)
    assertEquals(userExample2.likedRecipes, userViewModel.likedRecipes.value)
    assertEquals(userExample2.createdRecipes, userViewModel.createdRecipes.value)
  }

  @Test
  fun `test failed to parse each element in fridge`() {
    val onSuccessCaptor: ArgumentCaptor<Function1<User, Unit>> =
        ArgumentCaptor.forClass(onSuccessClass)
    val onFailureCaptorFridge: ArgumentCaptor<Function1<Exception, Unit>> =
        ArgumentCaptor.forClass(onFailureClass)

    doNothing().`when`(mockUserRepository).getUserById(any(), capture(onSuccessCaptor), any())

    doNothing().`when`(mockIngredientRepository).get(any(), any(), capture(onFailureCaptorFridge))

    mockStatic(Log::class.java).use { mockedLog ->
      userViewModel.getCurrentUser()

      onSuccessCaptor.value.invoke(userExample2)
      onFailureCaptorFridge.value.invoke(Exception())

      mockedLog.verify {
        Log.e(
            eq("UserViewModel"),
            eq("Failed to fetch ingredient from the database."),
            any<Exception>())
      }
    }
  }

  @Test
  fun `test parsing of each element in liked recipes`() {
    val onSuccessCaptor: ArgumentCaptor<Function1<User, Unit>> =
        ArgumentCaptor.forClass(onSuccessClass)
    val onSuccessCaptorLikedRecipe: ArgumentCaptor<Function1<Recipe, Unit>> =
        ArgumentCaptor.forClass(onSuccessClassLikedRecipes)

    doNothing().`when`(mockUserRepository).getUserById(any(), capture(onSuccessCaptor), any())

    doNothing()
        .`when`(mockRecipeRepository)
        .search(any(), capture(onSuccessCaptorLikedRecipe), any())

    userViewModel.getCurrentUser()

    onSuccessCaptor.value.invoke(userExample3)
    onSuccessCaptorLikedRecipe.value.invoke(recipeExample)

    assertEquals(userExample3.userName, userViewModel.userName.value)
    assertEquals(userExample3.profilePictureUrl, userViewModel.profilePictureUrl.value)
    assertEquals(userExample3.fridge, userViewModel.fridge.value)
    assertEquals(listOf(recipeExample), userViewModel.likedRecipes.value)
    assertEquals(userExample3.createdRecipes, userViewModel.createdRecipes.value)
  }

  @Test
  fun `test failed to parse each element in liked recipes`() {
    val onSuccessCaptor: ArgumentCaptor<Function1<User, Unit>> =
        ArgumentCaptor.forClass(onSuccessClass)
    val onFailureCaptorLikedRecipe: ArgumentCaptor<Function1<Exception, Unit>> =
        ArgumentCaptor.forClass(onFailureClass)

    doNothing().`when`(mockUserRepository).getUserById(any(), capture(onSuccessCaptor), any())

    doNothing()
        .`when`(mockRecipeRepository)
        .search(any(), any(), capture(onFailureCaptorLikedRecipe))

    mockStatic(Log::class.java).use { mockedLog ->
      userViewModel.getCurrentUser()

      onSuccessCaptor.value.invoke(userExample3)
      onFailureCaptorLikedRecipe.value.invoke(Exception())

      mockedLog.verify {
        Log.e(
            eq("UserViewModel"),
            eq("Failed to fetch liked recipes from the database."),
            any<Exception>())
      }
    }
  }

  @Test
  fun `test parsing of each element in created recipes`() {
    val onSuccessCaptor: ArgumentCaptor<Function1<User, Unit>> =
        ArgumentCaptor.forClass(onSuccessClass)
    val onSuccessCaptorCreatedRecipe: ArgumentCaptor<Function1<Recipe, Unit>> =
        ArgumentCaptor.forClass(onSuccessClassCreatedRecipes)

    doNothing().`when`(mockUserRepository).getUserById(any(), capture(onSuccessCaptor), any())

    doNothing()
        .`when`(mockRecipeRepository)
        .search(any(), capture(onSuccessCaptorCreatedRecipe), any())

    userViewModel.getCurrentUser()

    onSuccessCaptor.value.invoke(userExample4)
    onSuccessCaptorCreatedRecipe.value.invoke(createdRecipeExample)

    assertEquals(userExample4.userName, userViewModel.userName.value)
    assertEquals(userExample4.profilePictureUrl, userViewModel.profilePictureUrl.value)
    assertEquals(userExample4.fridge, userViewModel.fridge.value)
    assertEquals(userExample4.likedRecipes, userViewModel.likedRecipes.value)
    assertEquals(listOf(createdRecipeExample), userViewModel.createdRecipes.value)
  }

  @Test
  fun `test failed to parse each element in created recipes`() {
    val onSuccessCaptor: ArgumentCaptor<Function1<User, Unit>> =
        ArgumentCaptor.forClass(onSuccessClass)
    val onFailureCaptorCreatedRecipe: ArgumentCaptor<Function1<Exception, Unit>> =
        ArgumentCaptor.forClass(onFailureClass)

    doNothing().`when`(mockUserRepository).getUserById(any(), capture(onSuccessCaptor), any())

    doNothing()
        .`when`(mockRecipeRepository)
        .search(any(), any(), capture(onFailureCaptorCreatedRecipe))

    mockStatic(Log::class.java).use { mockedLog ->
      userViewModel.getCurrentUser()

      onSuccessCaptor.value.invoke(userExample4)
      onFailureCaptorCreatedRecipe.value.invoke(Exception())

      mockedLog.verify {
        Log.e(
            eq("UserViewModel"),
            eq("Failed to fetch created recipes from the database."),
            any<Exception>())
      }
    }
  }
}
