package com.android.sample.model.user

import androidx.lifecycle.ViewModel
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.recipe.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
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

  private val _fridge: MutableStateFlow<List<Ingredient>?> = MutableStateFlow(null)
  val fridge: StateFlow<List<Ingredient>?> = _fridge

  private val _savedRecipes: MutableStateFlow<List<Recipe>?> = MutableStateFlow(null)
  val savedRecipes: StateFlow<List<Recipe>?> = _savedRecipes

  private val _createdRecipes: MutableStateFlow<List<Recipe>?> = MutableStateFlow(null)
  val createdRecipes: StateFlow<List<Recipe>?> = _createdRecipes

  init {
    userRepository.init { getCurrentUser() }
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
          _savedRecipes.value = emptyList()
          _createdRecipes.value = emptyList()
        },
        onFailure = {
          userRepository.addUser(
              user =
                  User(
                      uid = userId,
                      userName = userId,
                      profilePictureUrl = "",
                      fridge = emptyList(),
                      savedRecipes = emptyList(),
                      createdRecipes = emptyList()),
              onSuccess = { getCurrentUser() },
              onFailure = {})
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
            fridge = _fridge.value?.map { it.barCode.toString() } ?: emptyList(),
            savedRecipes = _savedRecipes.value?.map { it.idMeal.toString() } ?: emptyList(),
            createdRecipes = _createdRecipes.value?.map { it.idMeal.toString() } ?: emptyList())

    userRepository.updateUser(user = savedUser, onSuccess = {}, onFailure = {})

    getCurrentUser()
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

  /**
   * Adds an ingredient to the user fridge.
   *
   * @param ingredient the ingredient to be added
   */
  fun addIngredientToUserFridge(ingredient: Ingredient) {
    val currentFridge = _fridge.value ?: emptyList()
    val newFridge = currentFridge.toMutableList()
    newFridge.add(ingredient)
    _fridge.value = newFridge
  }

  /**
   * Removes an ingredient from the user fridge.
   *
   * @param ingredient the ingredient to be removed
   */
  fun removeIngredientFromUserFridge(ingredient: Ingredient) {
    val currentFridge = _fridge.value ?: emptyList()
    val newFridge = currentFridge.toMutableList()
    newFridge.remove(ingredient)
    _fridge.value = newFridge
  }

  /**
   * Adds a recipe to the user saved recipes.
   *
   * @param recipe the recipe to be added
   */
  fun addRecipeToUserSavedRecipes(recipe: Recipe) {
    val currentSavedRecipes = _savedRecipes.value ?: emptyList()
    val newSavedRecipes = currentSavedRecipes.toMutableList()
    newSavedRecipes.add(recipe)
    _savedRecipes.value = newSavedRecipes
  }

  /**
   * Removes a recipe from the user saved recipes.
   *
   * @param recipe the recipe to be removed
   */
  fun removeRecipeFromUserSavedRecipes(recipe: Recipe) {
    val currentSavedRecipes = _savedRecipes.value ?: emptyList()
    val newSavedRecipes = currentSavedRecipes.toMutableList()
    newSavedRecipes.remove(recipe)
    _savedRecipes.value = newSavedRecipes
  }

  /**
   * Adds a recipe to the user created recipes.
   *
   * @param recipe the recipe to be added
   */
  fun addRecipeToUserCreatedRecipes(recipe: Recipe) {
    val currentCreatedRecipes = _createdRecipes.value ?: emptyList()
    val newCreatedRecipes = currentCreatedRecipes.toMutableList()
    newCreatedRecipes.add(recipe)
    _createdRecipes.value = newCreatedRecipes
  }

  /**
   * Removes a recipe from the user created recipes.
   *
   * @param recipe the recipe to be removed
   */
  fun removeRecipeFromUserCreatedRecipes(recipe: Recipe) {
    val currentCreatedRecipes = _createdRecipes.value ?: emptyList()
    val newCreatedRecipes = currentCreatedRecipes.toMutableList()
    newCreatedRecipes.remove(recipe)
    _createdRecipes.value = newCreatedRecipes
  }
}
