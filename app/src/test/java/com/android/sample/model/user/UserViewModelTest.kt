package com.android.sample.model.user

import android.util.Log
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.ingredient.FirestoreIngredientRepository
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.recipe.FirestoreRecipesRepository
import com.android.sample.model.recipe.Recipe
import com.android.sample.resources.C.Tag.UserViewModel.FAILED_TO_DELETE_IMAGE
import com.android.sample.resources.C.Tag.UserViewModel.FAILED_TO_DELETE_RECIPE
import com.android.sample.resources.C.Tag.UserViewModel.FAILED_TO_FETCH_CREATED_RECIPE_FROM_DATABASE_ERROR
import com.android.sample.resources.C.Tag.UserViewModel.FAILED_TO_FETCH_INGREDIENT_FROM_DATABASE_ERROR
import com.android.sample.resources.C.Tag.UserViewModel.FAILED_TO_FETCH_LIKED_RECIPE_FROM_DATABASE_ERROR
import com.android.sample.resources.C.Tag.UserViewModel.IMAGE_DELETION_SUCCESSFULY
import com.android.sample.resources.C.Tag.UserViewModel.LOG_TAG
import com.android.sample.resources.C.Tag.UserViewModel.NOT_FOUND_INGREDIENT_IN_DATABASE_ERROR
import com.android.sample.resources.C.Tag.UserViewModel.RECIPE_DELETED_SUCCESSFULY
import com.android.sample.ui.utils.testIngredients
import com.android.sample.ui.utils.testRecipes
import com.android.sample.ui.utils.testUsers
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
import org.mockito.kotlin.times
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
  private val onSuccessRecipeDeletionClass: Class<Function0<Unit>> =
      Function0::class.java as Class<Function0<Unit>>

  @Suppress("UNCHECKED_CAST")
  private val onSuccessImageDeletionClass: Class<Function0<Unit>> =
      Function0::class.java as Class<Function0<Unit>>

  @Suppress("UNCHECKED_CAST")
  private val onFailureClass: Class<Function1<Exception, Unit>> =
      Function1::class.java as Class<Function1<Exception, Unit>>

  private lateinit var mockUserRepository: UserRepository
  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var mockCurrentUser: FirebaseUser
  private lateinit var mockIngredientRepository: FirestoreIngredientRepository
  private lateinit var mockRecipeRepository: FirestoreRecipesRepository
  private lateinit var mockImageRepositoryFirebase: ImageRepositoryFirebase

  private lateinit var userViewModel: UserViewModel

  private lateinit var mockCall: Call

  private val userExample: User = testUsers[0]

  private val ingredientExample: Ingredient = testIngredients[0]

  private val recipeExample: Recipe = testRecipes[0]

  private val createdRecipeExample: Recipe = testRecipes[1]

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    mockCall = mock(Call::class.java)

    mockUserRepository = mock(UserRepository::class.java)
    mockFirebaseAuth = mock(FirebaseAuth::class.java)
    mockCurrentUser = mock(FirebaseUser::class.java)
    mockIngredientRepository = mock(FirestoreIngredientRepository::class.java)
    mockRecipeRepository = mock(FirestoreRecipesRepository::class.java)
    mockImageRepositoryFirebase = mock(ImageRepositoryFirebase::class.java)

    `when`(mockFirebaseAuth.currentUser).thenReturn(mockCurrentUser)
    `when`(mockCurrentUser.uid).thenReturn(userExample.uid)

    userViewModel =
        UserViewModel(
            mockUserRepository,
            mockFirebaseAuth,
            mockRecipeRepository,
            mockIngredientRepository,
            mockImageRepositoryFirebase)
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

    verify(mockUserRepository, times(3)).updateUser(capture(userCaptor), any(), any())

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
    verify(mockIngredientRepository, times(userExample.fridge.count())).get(any(), any(), any())
    verify(
            mockRecipeRepository,
            times(userExample.likedRecipes.count() + userExample.createdRecipes.count()))
        .search(any(), any(), any())
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
    verify(mockIngredientRepository, times(userExample.fridge.count())).get(any(), any(), any())
    verify(
            mockRecipeRepository,
            times(userExample.likedRecipes.count() + userExample.createdRecipes.count()))
        .search(any(), any(), any())
  }

  @Test
  fun `test modifies elements correctly`() {
    userViewModel.changeUserName(userExample.userName)
    userViewModel.changeProfilePictureUrl(userExample.profilePictureUrl)
    userViewModel.addIngredientToUserFridge(ingredientExample)
    userViewModel.addRecipeToUserLikedRecipes(recipeExample)
    userViewModel.addRecipeToUserCreatedRecipes(createdRecipeExample)

    assertEquals(userViewModel.userName.value, userExample.userName)
    assertEquals(userViewModel.profilePictureUrl.value, userExample.profilePictureUrl)
    assertEquals(userViewModel.fridge.value[0].first.name, ingredientExample.name)
    assertEquals(userViewModel.likedRecipes.value[0].uid, recipeExample.uid)
    assertEquals(userViewModel.createdRecipes.value[0].uid, createdRecipeExample.uid)

    userViewModel.removeIngredientFromUserFridge(ingredientExample)
    userViewModel.removeRecipeFromUserLikedRecipes(recipeExample)
    userViewModel.removeRecipeFromUserCreatedRecipes(createdRecipeExample)

    assertEquals(userViewModel.fridge.value.count(), 0)
    assertEquals(userViewModel.likedRecipes.value.count(), 0)
    assertEquals(userViewModel.createdRecipes.value.count(), 0)
  }

  @Test
  fun `test correctly adds and removes existing ingredient to count pairs in fridge`() {
    userViewModel.addIngredientToUserFridge(ingredientExample, 2)

    assertEquals(userViewModel.fridge.value[0].first.name, ingredientExample.name)
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

    onSuccessCaptor.value.invoke(userExample)
    onSuccessCaptorFridge.value.invoke(ingredientExample)

    assertEquals(userExample.userName, userViewModel.userName.value)
    assertEquals(userExample.profilePictureUrl, userViewModel.profilePictureUrl.value)
    assertEquals(listOf(Pair(ingredientExample, 1)), userViewModel.fridge.value)
  }

  @Test
  fun `test parsing of each element in fridge but not found in the database`() {
    val onSuccessCaptor: ArgumentCaptor<Function1<User, Unit>> =
        ArgumentCaptor.forClass(onSuccessClass)
    val onSuccessCaptorFridge: ArgumentCaptor<Function1<Ingredient?, Unit>> =
        ArgumentCaptor.forClass(onSuccessClassFridge)

    doNothing().`when`(mockUserRepository).getUserById(any(), capture(onSuccessCaptor), any())

    doNothing().`when`(mockIngredientRepository).get(any(), capture(onSuccessCaptorFridge), any())

    mockStatic(Log::class.java).use { mockedLog ->
      userViewModel.getCurrentUser()

      onSuccessCaptor.value.invoke(userExample)
      onSuccessCaptorFridge.value.invoke(null)

      mockedLog.verify { Log.e(eq(LOG_TAG), eq(NOT_FOUND_INGREDIENT_IN_DATABASE_ERROR)) }
    }
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

      onSuccessCaptor.value.invoke(userExample)
      onFailureCaptorFridge.value.invoke(Exception())

      mockedLog.verify {
        Log.e(eq(LOG_TAG), eq(FAILED_TO_FETCH_INGREDIENT_FROM_DATABASE_ERROR), any<Exception>())
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

    // we need to make a copy without any created recipes
    // to avoid crushing the argument captor
    onSuccessCaptor.value.invoke(userExample.copy(createdRecipes = emptyList()))
    onSuccessCaptorLikedRecipe.value.invoke(recipeExample)

    assertEquals(userExample.userName, userViewModel.userName.value)
    assertEquals(userExample.profilePictureUrl, userViewModel.profilePictureUrl.value)
    assertEquals(listOf(recipeExample), userViewModel.likedRecipes.value)
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

      // we need to make a copy without any created recipes
      // to avoid crushing the argument captor
      onSuccessCaptor.value.invoke(userExample.copy(createdRecipes = emptyList()))
      onFailureCaptorLikedRecipe.value.invoke(Exception())

      mockedLog.verify {
        Log.e(eq(LOG_TAG), eq(FAILED_TO_FETCH_LIKED_RECIPE_FROM_DATABASE_ERROR), any<Exception>())
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

    onSuccessCaptor.value.invoke(userExample)
    onSuccessCaptorCreatedRecipe.value.invoke(createdRecipeExample)

    assertEquals(userExample.userName, userViewModel.userName.value)
    assertEquals(userExample.profilePictureUrl, userViewModel.profilePictureUrl.value)
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

      onSuccessCaptor.value.invoke(userExample)
      onFailureCaptorCreatedRecipe.value.invoke(Exception())

      mockedLog.verify {
        Log.e(eq(LOG_TAG), eq(FAILED_TO_FETCH_CREATED_RECIPE_FROM_DATABASE_ERROR), any<Exception>())
      }
    }
  }

  @Test
  fun `test deletion of recipe and of its image from the database is successful`() {
    val onSuccessRecipeDeletionCaptor: ArgumentCaptor<Function0<Unit>> =
        ArgumentCaptor.forClass(onSuccessRecipeDeletionClass)

    val onSuccessImageDeletionCaptor: ArgumentCaptor<Function0<Unit>> =
        ArgumentCaptor.forClass(onSuccessImageDeletionClass)

    doNothing()
        .`when`(mockRecipeRepository)
        .deleteRecipe(any(), capture(onSuccessRecipeDeletionCaptor), any())
    doNothing()
        .`when`(mockImageRepositoryFirebase)
        .deleteImage(any(), any(), any(), capture(onSuccessImageDeletionCaptor), any())

    mockStatic(Log::class.java).use { mockedLog ->
      userViewModel.removeRecipeFromUserCreatedRecipes(recipeExample)
      onSuccessRecipeDeletionCaptor.value.invoke()
      onSuccessImageDeletionCaptor.value.invoke()

      mockedLog.verify {
        Log.i(LOG_TAG, RECIPE_DELETED_SUCCESSFULY)
        Log.i(LOG_TAG, IMAGE_DELETION_SUCCESSFULY)
      }
    }
  }

  @Test
  fun `test deletion of recipe and of its image from the database failed`() {
    val onFailureRecipeDeletionCaptor: ArgumentCaptor<Function1<Exception, Unit>> =
        ArgumentCaptor.forClass(onFailureClass)

    val onFailureImageDeletionCaptor: ArgumentCaptor<Function1<Exception, Unit>> =
        ArgumentCaptor.forClass(onFailureClass)

    doNothing()
        .`when`(mockRecipeRepository)
        .deleteRecipe(any(), any(), capture(onFailureRecipeDeletionCaptor))
    doNothing()
        .`when`(mockImageRepositoryFirebase)
        .deleteImage(any(), any(), any(), any(), capture(onFailureImageDeletionCaptor))

    mockStatic(Log::class.java).use { mockedLog ->
      userViewModel.removeRecipeFromUserCreatedRecipes(recipeExample)
      onFailureRecipeDeletionCaptor.value.invoke(Exception())
      onFailureImageDeletionCaptor.value.invoke(Exception())

      mockedLog.verify {
        Log.e(eq(LOG_TAG), eq(FAILED_TO_DELETE_RECIPE), any<Exception>())
        Log.e(eq(LOG_TAG), eq(FAILED_TO_DELETE_IMAGE), any<Exception>())
      }
    }
  }
}
