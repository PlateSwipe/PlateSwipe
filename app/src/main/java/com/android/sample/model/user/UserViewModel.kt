package com.android.sample.model.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.sample.model.image.ImageDirectoryType
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.ingredient.FirestoreIngredientRepository
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.recipe.FirestoreRecipesRepository
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipeOverviewViewModel
import com.android.sample.resources.C.Tag.UserViewModel.FAILED_TO_DELETE_IMAGE
import com.android.sample.resources.C.Tag.UserViewModel.FAILED_TO_DELETE_RECIPE
import com.android.sample.resources.C.Tag.UserViewModel.FAILED_TO_FETCH_CREATED_RECIPE_FROM_DATABASE_ERROR
import com.android.sample.resources.C.Tag.UserViewModel.FAILED_TO_FETCH_INGREDIENT_FROM_DATABASE_ERROR
import com.android.sample.resources.C.Tag.UserViewModel.FAILED_TO_FETCH_LIKED_RECIPE_FROM_DATABASE_ERROR
import com.android.sample.resources.C.Tag.UserViewModel.IMAGE_DELETION_SUCCESSFULY
import com.android.sample.resources.C.Tag.UserViewModel.IMAGE_NAME
import com.android.sample.resources.C.Tag.UserViewModel.LOG_TAG
import com.android.sample.resources.C.Tag.UserViewModel.NOT_FOUND_INGREDIENT_IN_DATABASE_ERROR
import com.android.sample.resources.C.Tag.UserViewModel.RECIPE_DELETED_SUCCESSFULY
import com.android.sample.resources.C.Tag.UserViewModel.RECIPE_NOT_FOUND
import com.android.sample.resources.C.Tag.UserViewModel.REMOVED_INGREDIENT_NOT_IN_FRIDGE_ERROR
import com.android.sample.resources.C.Tag.UserViewModel.REMOVED_TOO_MANY_INGREDIENTS_ERROR
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel(
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth = Firebase.auth,
    private val recipesRepository: FirestoreRecipesRepository =
        FirestoreRecipesRepository(Firebase.firestore),
    private val ingredientRepository: FirestoreIngredientRepository =
        FirestoreIngredientRepository(Firebase.firestore),
    private val imageRepositoryFirebase: ImageRepositoryFirebase =
        ImageRepositoryFirebase(Firebase.storage)
) : ViewModel(), RecipeOverviewViewModel {

  private val _userName: MutableStateFlow<String?> = MutableStateFlow(null)
  val userName: StateFlow<String?> = _userName

  private val _profilePictureUrl: MutableStateFlow<String?> = MutableStateFlow(null)
  val profilePictureUrl: StateFlow<String?> = _profilePictureUrl

  private val _fridge: MutableStateFlow<List<Pair<Ingredient, Int>>> = MutableStateFlow(emptyList())
  val fridge: StateFlow<List<Pair<Ingredient, Int>>> = _fridge

  private val _likedRecipes: MutableStateFlow<List<Recipe>> = MutableStateFlow(emptyList())
  val likedRecipes: StateFlow<List<Recipe>> = _likedRecipes

  private val _createdRecipes: MutableStateFlow<List<Recipe>> = MutableStateFlow(emptyList())
  val createdRecipes: StateFlow<List<Recipe>> = _createdRecipes

  private val _currentRecipe = MutableStateFlow<Recipe?>(null)
  override val currentRecipe: StateFlow<Recipe?>
    get() = _currentRecipe

  init {
    userRepository.init { getCurrentUser() }
  }

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UserViewModel(userRepository = UserRepositoryFirestore(Firebase.firestore)) as T
          }
        }
  }

  /**
   * Gets the current user from the database and updates the view model with the values. If the user
   * does not exist, it creates a new user with the current user id. If no user is logged in, it
   * does nothing.
   */
  fun getCurrentUser() {
    val userId: String = firebaseAuth.currentUser?.uid ?: return

    userRepository.getUserById(
        id = userId,
        onSuccess = { user ->
          _userName.value = user.userName
          _profilePictureUrl.value = user.profilePictureUrl
          user.fridge.forEach { (barcode, ingredientCount) ->
            ingredientRepository.get(
                barcode.toLong(),
                onSuccess = { ingredient ->
                  if (ingredient != null) {
                    addIngredientToUserFridge(ingredient, ingredientCount)
                  } else {
                    Log.e(LOG_TAG, NOT_FOUND_INGREDIENT_IN_DATABASE_ERROR)
                  }
                },
                onFailure = { e ->
                  Log.e(LOG_TAG, FAILED_TO_FETCH_INGREDIENT_FROM_DATABASE_ERROR, e)
                })
          }
          user.likedRecipes.forEach { uid ->
            fetchRecipe(
                uid,
                _likedRecipes,
                { recipe -> addRecipeToUserLikedRecipes(recipe) },
                { recipe -> removeRecipeFromUserLikedRecipes(recipe) },
                FAILED_TO_FETCH_LIKED_RECIPE_FROM_DATABASE_ERROR)
          }
          user.createdRecipes.forEach { uid ->
            fetchRecipe(
                uid,
                _createdRecipes,
                { recipe -> addRecipeToUserCreatedRecipes(recipe) },
                { recipe -> removeRecipeFromUserCreatedRecipes(recipe) },
                FAILED_TO_FETCH_CREATED_RECIPE_FROM_DATABASE_ERROR)
          }
        },
        onFailure = {
          userRepository.addUser(
              user =
                  User(
                      uid = userId,
                      userName = userName.value ?: userId,
                      profilePictureUrl = "",
                      fridge = _fridge.value.map { Pair(it.first.barCode.toString(), it.second) },
                      likedRecipes = _likedRecipes.value.map { it.uid },
                      createdRecipes = _createdRecipes.value.map { it.uid }),
              onSuccess = { getCurrentUser() },
              onFailure = { e -> throw e })
        })
  }

  /**
   * Updates the current user in the database with the values from the view model. If no user is
   * logged in, it does nothing.
   */
  fun updateCurrentUser() {
    val savedUser =
        User(
            uid = firebaseAuth.currentUser?.uid ?: return,
            userName = _userName.value ?: "",
            profilePictureUrl = _profilePictureUrl.value ?: "",
            fridge = _fridge.value.map { Pair(it.first.barCode.toString(), it.second) },
            likedRecipes = _likedRecipes.value.map { it.uid },
            createdRecipes = _createdRecipes.value.map { it.uid })

    userRepository.updateUser(user = savedUser, onSuccess = {}, onFailure = { e -> throw e })
  }

  /**
   * Changes the user name to the new user name.
   *
   * @param newUserName the new user name
   */
  fun changeUserName(newUserName: String) {
    _userName.value = newUserName
    updateCurrentUser()
  }

  /**
   * Changes the profile picture url to the new profile picture url.
   *
   * @param newProfilePictureUrl the new profile picture url
   */
  fun changeProfilePictureUrl(newProfilePictureUrl: String) {
    _profilePictureUrl.value = newProfilePictureUrl
    updateCurrentUser()
  }

  /**
   * Updates the list of items by adding or removing an item. If the item is already in the list and
   * add is true, it will not be added again.
   *
   * @param list the list to be updated
   * @param item the item to be added or removed
   * @param add true if the item should be added, false if the item should be removed
   * @return the updated list
   */
  private fun <T> updateList(list: MutableStateFlow<List<T>>, item: T, add: Boolean): List<T> {

    val currentList = list.value
    val newList = currentList.toMutableList()

    if (add && !newList.contains(item)) {
      newList.add(item)
    } else {
      newList.remove(item)
    }

    list.value = newList
    return newList
  }

  /**
   * Adds an ingredient to the user fridge. More precisely, it increases the count of said
   * ingredient in the fridge.
   *
   * @param ingredient the ingredient to be added
   * @param count the number of ingredients to be added
   */
  fun addIngredientToUserFridge(ingredient: Ingredient, count: Int = 1) {
    try {
      val changedElement: Pair<Ingredient, Int> =
          _fridge.value.first { it.first.barCode == ingredient.barCode }

      updateList(_fridge, changedElement, add = false)
      updateList(_fridge, Pair(changedElement.first, changedElement.second + count), add = true)
    } catch (e: NoSuchElementException) {
      updateList(_fridge, Pair(ingredient, count), add = true)
    }
    updateCurrentUser()
  }

  /**
   * Removes an ingredient from the user fridge. More precisely, it will decrease the count of said
   * ingredient in the fridge.
   *
   * @param ingredient the ingredient to be removed
   * @param count the number of ingredients to be removed
   * @throws IllegalArgumentException if the count is greater than the number of ingredients in the
   *   fridge
   */
  fun removeIngredientFromUserFridge(ingredient: Ingredient, count: Int = 1) {
    try {
      val changedElement: Pair<Ingredient, Int> =
          _fridge.value.first { it.first.barCode == ingredient.barCode }

      if (changedElement.second - count > 0) {
        updateList(_fridge, changedElement, add = false)
        updateList(_fridge, Pair(changedElement.first, changedElement.second - count), add = true)
      } else if (changedElement.second - count == 0) {
        updateList(_fridge, changedElement, add = false)
        updateList(_fridge, changedElement, add = false)
      } else {
        throw IllegalArgumentException(REMOVED_TOO_MANY_INGREDIENTS_ERROR)
      }
    } catch (e: NoSuchElementException) {
      throw IllegalArgumentException(REMOVED_INGREDIENT_NOT_IN_FRIDGE_ERROR)
    }
    updateCurrentUser()
  }

  /**
   * Adds a recipe to the user saved recipes.
   *
   * @param recipe the recipe to be added
   */
  fun addRecipeToUserLikedRecipes(recipe: Recipe) {
    updateList(_likedRecipes, recipe, true)
    updateCurrentUser()
  }

  /**
   * Removes a recipe from the user saved recipes.
   *
   * @param recipe the recipe to be removed
   */
  fun removeRecipeFromUserLikedRecipes(recipe: Recipe) {
    updateList(_likedRecipes, recipe, false)
    updateCurrentUser()
  }

  /**
   * Adds a recipe to the user created recipes.
   *
   * @param recipe the recipe to be added
   */
  fun addRecipeToUserCreatedRecipes(recipe: Recipe) {
    updateList(_createdRecipes, recipe, true)
    updateCurrentUser()
  }

  /**
   * Removes a recipe from the user created recipes.
   *
   * @param recipe the recipe to be removed
   */
  fun removeRecipeFromUserCreatedRecipes(recipe: Recipe) {
    updateList(_createdRecipes, recipe, false)
    recipesRepository.deleteRecipe(
        recipe.uid,
        onSuccess = { Log.i(LOG_TAG, RECIPE_DELETED_SUCCESSFULY) },
        onFailure = { e -> Log.e(LOG_TAG, FAILED_TO_DELETE_RECIPE, e) })
    imageRepositoryFirebase.deleteImage(
        recipe.uid,
        IMAGE_NAME,
        ImageDirectoryType.RECIPE,
        { Log.i(LOG_TAG, IMAGE_DELETION_SUCCESSFULY) },
        { e -> Log.e(LOG_TAG, FAILED_TO_DELETE_IMAGE, e) })
    updateCurrentUser()
  }

  /**
   * Selects a specific recipe to be shown on the overview screen of the account
   *
   * @param recipe the recipe to select
   */
  fun selectRecipe(recipe: Recipe) {
    _currentRecipe.value = recipe
  }

  /**
   * Function that fetches a recipe based on the uid from the database for a user
   *
   * @param uid the uid of the recipe in the database
   * @param recipes the list of recipes to where we add or from were we delete
   * @param addRecipe the function that adds the recipe to recipes once found
   * @param removeRecipe the function that removes the recipe from recipes if it is no longer in the
   *   database
   * @param errorMessage error message that will be displayed in the logs
   */
  private fun fetchRecipe(
      uid: String,
      recipes: MutableStateFlow<List<Recipe>>,
      addRecipe: (Recipe) -> Unit,
      removeRecipe: (Recipe) -> Unit,
      errorMessage: String
  ) {
    val recipeFound = recipes.value.find { item -> item.uid == uid }
    recipesRepository.search(
        uid,
        onSuccess = { recipe ->
          if (recipeFound == null) {
            addRecipe(recipe)
          }
        },
        onFailure = { e ->
          if (e.message != RECIPE_NOT_FOUND) {
            Log.e(LOG_TAG, errorMessage, e)
          } else {
            if (recipeFound != null) {
              removeRecipe(recipeFound)
            }
          }
        })
  }
}
