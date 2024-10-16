package com.android.sample.model.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.recipe.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel(
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth = Firebase.auth
) : ViewModel() {

  private val _userName: MutableStateFlow<String?> = MutableStateFlow(null)
  val userName: StateFlow<String?> = _userName

  private val _profilePictureUrl: MutableStateFlow<String?> = MutableStateFlow(null)
  val profilePictureUrl: StateFlow<String?> = _profilePictureUrl

  private val _fridge: MutableStateFlow<List<Ingredient>> = MutableStateFlow(emptyList())
  val fridge: StateFlow<List<Ingredient>> = _fridge

  private val _likedRecipes: MutableStateFlow<List<Recipe>> = MutableStateFlow(emptyList())
  val likedRecipes: StateFlow<List<Recipe>> = _likedRecipes

  private val _createdRecipes: MutableStateFlow<List<Recipe>> = MutableStateFlow(emptyList())
  val createdRecipes: StateFlow<List<Recipe>> = _createdRecipes

  init {
    userRepository.init { getCurrentUser() }
  }

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UserViewModel(UserRepositoryFirestore(com.google.firebase.Firebase.firestore))
                as T
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
          _fridge.value = emptyList()
          _likedRecipes.value = emptyList()
          _createdRecipes.value = emptyList()
        },
        onFailure = {
          userRepository.addUser(
              user =
                  User(
                      uid = userId,
                      userName = userName.value ?: userId,
                      profilePictureUrl = "",
                      fridge = emptyList(),
                      likedRecipes = emptyList(),
                      createdRecipes = emptyList()),
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
            fridge = _fridge.value.map { it.barCode.toString() },
            likedRecipes = _likedRecipes.value.map { it.idMeal },
            createdRecipes = _createdRecipes.value.map { it.idMeal })

    userRepository.updateUser(user = savedUser, onSuccess = {}, onFailure = { e -> throw e })
  }

  /**
   * Changes the user name to the new user name.
   *
   * @param newUserName the new user name
   */
  fun changeUserName(newUserName: String) {
    _userName.value = newUserName
  }

  /**
   * Changes the profile picture url to the new profile picture url.
   *
   * @param newProfilePictureUrl the new profile picture url
   */
  fun changeProfilePictureUrl(newProfilePictureUrl: String) {
    _profilePictureUrl.value = newProfilePictureUrl
  }

  private fun <T> updateList(list: MutableStateFlow<List<T>>, item: T, add: Boolean): List<T> {

    val currentList = list.value
    val newList = currentList.toMutableList()

    if (add) {
      newList.add(item)
    } else {
      newList.remove(item)
    }

    list.value = newList
    return newList
  }

  /**
   * Adds an ingredient to the user fridge.
   *
   * @param ingredient the ingredient to be added
   */
  fun addIngredientToUserFridge(ingredient: Ingredient) {
    updateList(_fridge, ingredient, true)
  }

  /**
   * Removes an ingredient from the user fridge.
   *
   * @param ingredient the ingredient to be removed
   */
  fun removeIngredientFromUserFridge(ingredient: Ingredient) {
    updateList(_fridge, ingredient, false)
  }

  /**
   * Adds a recipe to the user saved recipes.
   *
   * @param recipe the recipe to be added
   */
  fun addRecipeToUserSavedRecipes(recipe: Recipe) {
    updateList(_likedRecipes, recipe, true)
  }

  /**
   * Removes a recipe from the user saved recipes.
   *
   * @param recipe the recipe to be removed
   */
  fun removeRecipeFromUserSavedRecipes(recipe: Recipe) {
    updateList(_likedRecipes, recipe, false)
  }

  /**
   * Adds a recipe to the user created recipes.
   *
   * @param recipe the recipe to be added
   */
  fun addRecipeToUserCreatedRecipes(recipe: Recipe) {
    updateList(_createdRecipes, recipe, true)
  }

  /**
   * Removes a recipe from the user created recipes.
   *
   * @param recipe the recipe to be removed
   */
  fun removeRecipeFromUserCreatedRecipes(recipe: Recipe) {
    updateList(_createdRecipes, recipe, false)
  }
}
