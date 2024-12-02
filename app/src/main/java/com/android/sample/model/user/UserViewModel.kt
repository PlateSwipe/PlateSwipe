package com.android.sample.model.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.sample.model.fridge.FridgeItem
import com.android.sample.model.image.ImageDirectoryType
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.image.ImageUploader
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.SearchIngredientViewModel
import com.android.sample.model.ingredient.networkData.AggregatorIngredientRepository
import com.android.sample.model.ingredient.networkData.FirestoreIngredientRepository
import com.android.sample.model.ingredient.networkData.OpenFoodFactsIngredientRepository
import com.android.sample.model.recipe.FirestoreRecipesRepository
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipeOverviewViewModel
import com.android.sample.resources.C.Tag.INGREDIENT_NOT_FOUND_MESSAGE
import com.android.sample.resources.C.Tag.INGREDIENT_VIEWMODEL_LOG_TAG
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import okhttp3.OkHttpClient

class UserViewModel(
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth = Firebase.auth,
    private val recipesRepository: FirestoreRecipesRepository =
        FirestoreRecipesRepository(Firebase.firestore),
    private val ingredientRepository: AggregatorIngredientRepository =
        AggregatorIngredientRepository(
            FirestoreIngredientRepository(Firebase.firestore),
            OpenFoodFactsIngredientRepository(OkHttpClient()),
            ImageRepositoryFirebase(Firebase.storage),
            ImageUploader()),
    private val imageRepositoryFirebase: ImageRepositoryFirebase =
        ImageRepositoryFirebase(Firebase.storage)
) : ViewModel(), RecipeOverviewViewModel, SearchIngredientViewModel {

  private val _userName: MutableStateFlow<String?> = MutableStateFlow(null)
  val userName: StateFlow<String?> = _userName

  private val _profilePictureUrl: MutableStateFlow<String?> = MutableStateFlow(null)
  val profilePictureUrl: StateFlow<String?> = _profilePictureUrl

  private val _fridgeItems = MutableStateFlow<List<Pair<FridgeItem, Ingredient>>>(emptyList())
  val fridgeItems: StateFlow<List<Pair<FridgeItem, Ingredient>>> = _fridgeItems

  private val _likedRecipes: MutableStateFlow<List<Recipe>> = MutableStateFlow(emptyList())
  val likedRecipes: StateFlow<List<Recipe>> = _likedRecipes

  private val _createdRecipes: MutableStateFlow<List<Recipe>> = MutableStateFlow(emptyList())
  val createdRecipes: StateFlow<List<Recipe>> = _createdRecipes

  private val _currentRecipe = MutableStateFlow<Recipe?>(null)
  override val currentRecipe: StateFlow<Recipe?>
    get() = _currentRecipe

  private val _ingredient = MutableStateFlow<Pair<Ingredient?, String?>>(Pair(null, null))
  override val ingredient: StateFlow<Pair<Ingredient?, String?>>
    get() = _ingredient

  private val _isFetchingByBarcode = MutableStateFlow(false)
  override val isFetchingByBarcode: StateFlow<Boolean>
    get() = _isFetchingByBarcode

  private val _ingredientList = MutableStateFlow<List<Pair<Ingredient, String?>>>(emptyList())
  override val ingredientList: StateFlow<List<Pair<Ingredient, String?>>>
    get() = _ingredientList

  private val _searchingIngredientList =
      MutableStateFlow<List<Pair<Ingredient, String?>>>(emptyList())
  override val searchingIngredientList: StateFlow<List<Pair<Ingredient, String?>>>
    get() = _searchingIngredientList

  private val _isSearching = MutableStateFlow(false)
  override val isFetchingByName: StateFlow<Boolean>
    get() = _isSearching

  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {

            return UserViewModel(userRepository = UserRepositoryFirestore(Firebase.firestore)) as T
          }
        }
  }

  init {
    // init the fridge item for testing

    /*var id = 0
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 2, id = id++.toString(), expirationDate = LocalDate.of(2024, 11, 29)),
            Ingredient(
                name =
                    "Ingredient222222222222222222222222222222222222222222222222222222222222222222222222",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "400g"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 2, id = id++.toString(), expirationDate = LocalDate.of(2024, 11, 30)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "400g"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 2, id = id++.toString(), expirationDate = LocalDate.of(2024, 12, 1)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "400g"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 2, id = id++.toString(), expirationDate = LocalDate.of(2024, 12, 2)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "400g"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 2, id = id++.toString(), expirationDate = LocalDate.of(2024, 12, 3)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "400g"))

    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 6, id = id++.toString(), expirationDate = LocalDate.of(2024, 11, 6)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "5pcs"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 1, id = id++.toString(), expirationDate = LocalDate.of(2024, 10, 6)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "10pcs"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 3, id = id++.toString(), expirationDate = LocalDate.of(2024, 9, 6)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "600g"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 2, id = id++.toString(), expirationDate = LocalDate.of(2024, 11, 7)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "400g"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 6, id = id++.toString(), expirationDate = LocalDate.of(2024, 11, 8)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "5pcs"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 1, id = id++.toString(), expirationDate = LocalDate.of(2024, 11, 9)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "10pcs"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 3, id = id++.toString(), expirationDate = LocalDate.of(2024, 11, 20)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "600g"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 2, id = id++.toString(), expirationDate = LocalDate.of(2024, 11, 21)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "400g"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 6, id = id++.toString(), expirationDate = LocalDate.of(2024, 11, 22)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "5pcs"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 1, id = id++.toString(), expirationDate = LocalDate.of(2024, 11, 23)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "10pcs"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 3, id = id++.toString(), expirationDate = LocalDate.of(2024, 11, 24)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "600g"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 2, id = id++.toString(), expirationDate = LocalDate.of(2024, 11, 25)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "400g"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 3, id = id++.toString(), expirationDate = LocalDate.of(2024, 11, 26)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "600g"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 2, id = id++.toString(), expirationDate = LocalDate.of(2024, 11, 27)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "400g"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 2, id = id++.toString(), expirationDate = LocalDate.of(2024, 11, 28)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "400g"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 2, id = id++.toString(), expirationDate = LocalDate.of(2024, 11, 29)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "400g"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 2, id = id++.toString(), expirationDate = LocalDate.of(2024, 11, 30)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "400g"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 2, id = id++.toString(), expirationDate = LocalDate.of(2024, 12, 1)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "400g"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 2, id = id++.toString(), expirationDate = LocalDate.of(2024, 12, 2)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "400g"))
    _fridgeItems.value +=
        Pair(
            FridgeItem(
                quantity = 2, id = id.toString(), expirationDate = LocalDate.of(2024, 12, 3)),
            Ingredient(
                name = "Ingredient",
                categories = listOf(),
                images = mutableMapOf(),
                quantity = "400g"))*/
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
          user.fridge.forEach { fridgeItem -> fetchIngredient(fridgeItem) }
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
                      fridge = _fridgeItems.value.map { it.first },
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
            fridge = _fridgeItems.value.map { it.first },
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
   * Method that add an [Ingredient] to the fridge of the user
   *
   * @param ingredient the ingredient to be added
   * @param quantity the quantity of the ingredient
   * @param expirationDate the expiration date of the ingredient
   */
  private fun addIngredientToUserFridge(
      ingredient: Ingredient,
      quantity: Int,
      expirationDate: LocalDate
  ) {
    val newFridgeItem = FridgeItem(ingredient.barCode.toString(), quantity, expirationDate)
    updateList(_fridgeItems, Pair(newFridgeItem, ingredient), add = true)
  }

  /**
   * Method that removes an [Ingredient] from the fridge of the user and updates the user in the
   * database
   *
   * @param ingredient the ingredient to be removed
   * @throws IllegalArgumentException if the count is greater than the number of ingredients in the
   *   fridge
   */
  fun removeIngredientFromUserFridge(ingredient: Ingredient, expirationDate: LocalDate) {
    try {
      val deletedIngredient: Pair<FridgeItem, Ingredient> =
          _fridgeItems.value.first {
            (it.first.id == ingredient.barCode.toString()) &&
                (it.first.expirationDate == expirationDate)
          }
      updateList(_fridgeItems, deletedIngredient, add = false)
    } catch (e: NoSuchElementException) {
      throw IllegalArgumentException(REMOVED_INGREDIENT_NOT_IN_FRIDGE_ERROR)
    }
    updateCurrentUser()
  }

  /**
   * Method that either add an ingredient to the user's fridge if it does not exist, and updates it
   * with the new quantity if it is already in the fridge
   *
   * @param ingredient the ingredient to be updated/added
   * @param quantity a valid quantity for the ingredient
   * @param expirationDate the expiration date of the ingredient
   * @param scannedItem boolean that the determines if we update via a scanned item or not
   */
  fun updateIngredientFromFridge(
      ingredient: Ingredient,
      quantity: Int,
      expirationDate: LocalDate,
      scannedItem: Boolean
  ) {
    val changedIngredient =
        _fridgeItems.value.find {
          (it.first.id == ingredient.barCode.toString()) &&
              (it.first.expirationDate == expirationDate)
        }
    if (changedIngredient != null) {
      if (scannedItem) { // case there we scan an item that we want to add to the fridge
        val actualQuantity = changedIngredient.first.quantity
        val newQuantity = actualQuantity + quantity

        val newFridgeItem =
            FridgeItem(
                changedIngredient.first.id, newQuantity, changedIngredient.first.expirationDate)

        updateList(_fridgeItems, changedIngredient, add = false)
        updateList(_fridgeItems, Pair(newFridgeItem, changedIngredient.second), add = true)
      } else { // case where we modify the quantity in the fridge item
        if (quantity <= 0) {
          removeIngredientFromUserFridge(ingredient, changedIngredient.first.expirationDate)
        } else {
          val newFridgeItem =
              FridgeItem(
                  changedIngredient.first.id, quantity, changedIngredient.first.expirationDate)
          updateList(_fridgeItems, changedIngredient, add = false)
          updateList(_fridgeItems, Pair(newFridgeItem, changedIngredient.second), add = true)
        }
      }
    } else {
      addIngredientToUserFridge(ingredient, quantity, expirationDate)
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

  /**
   * Method that fetches the ingredient described by the [FridgeItem] from the database
   *
   * @param fridgeItem the fridge item that describes the ingredient that we want to retrieve
   */
  private fun fetchIngredient(fridgeItem: FridgeItem) {
    ingredientRepository.get(
        fridgeItem.id.toLong(),
        onSuccess = { ingredient ->
          if (ingredient != null) {
            updateIngredientFromFridge(
                ingredient, fridgeItem.quantity, fridgeItem.expirationDate, false)
          } else {
            Log.e(LOG_TAG, NOT_FOUND_INGREDIENT_IN_DATABASE_ERROR)
          }
        },
        onFailure = { e -> Log.e(LOG_TAG, FAILED_TO_FETCH_INGREDIENT_FROM_DATABASE_ERROR, e) })
  }

  override fun fetchIngredient(barCode: Long) {
    clearIngredient()
    clearSearchingIngredientList()
    clearIngredientList()
    if (_ingredient.value.first?.barCode == barCode) {
      return
    }
    // Fetch ingredient from repository
    _isFetchingByBarcode.value = true
    ingredientRepository.get(
        barCode,
        onSuccess = { ingredient ->
          if (ingredient != null) {
            _ingredient.value = Pair(ingredient, ingredient.quantity)
          }

          _isFetchingByBarcode.value = false
        },
        onFailure = {
          Log.e(INGREDIENT_VIEWMODEL_LOG_TAG, INGREDIENT_NOT_FOUND_MESSAGE)
          _ingredient.value = Pair(null, null)
          _isFetchingByBarcode.value = false
        })
  }

  override fun clearIngredient() {
    _ingredient.value = Pair(null, null)
  }

  fun clearSearchingIngredientList() {
    _searchingIngredientList.value = emptyList()
  }

  fun clearIngredientList() {
    _ingredientList.value = emptyList()
  }

  /**
   * Add the first integer in the two strings
   *
   * @param quantity1
   * @param quantity2
   */
  private fun addFirstInt(quantity1: String?, quantity2: String?): String {
    if (quantity1 == null || quantity2 == null) {
      return quantity1 ?: quantity2 ?: ""
    }

    // Regular expression to find the first integer in the string
    val regex = Regex("""\d+""")
    val match1 = regex.find(quantity1)
    val match2 = regex.find(quantity2)

    // If both strings contain an integer, add them together
    return if (match1 != null && match2 != null) {
      val addition = match1.value.toInt() + match2.value.toInt()
      quantity1.replaceFirst(match1.value, addition.toString())
    } else if (match1 != null) {
      quantity1
    } else {
      quantity2
    }
  }

  override fun addIngredient(ingredient: Ingredient) {
    _ingredientList.update { currentList ->
      val existingItemIndex = currentList.indexOfFirst { it.first.barCode == ingredient.barCode }

      if (existingItemIndex != -1) {
        // Ingredient already exists; update its associated String value
        currentList.toMutableList().apply {
          this[existingItemIndex] =
              this[existingItemIndex].copy(
                  second = addFirstInt(this[existingItemIndex].second, ingredient.quantity))
        }
      } else {
        // Ingredient doesn't exist; add it to the list
        currentList + (ingredient to ingredient.quantity)
      }
    }
  }

  override fun fetchIngredientByName(name: String) {
    clearIngredient()
    clearSearchingIngredientList()
    clearIngredientList()
    _isSearching.value = true
    ingredientRepository.search(
        name,
        onSuccess = { ingredientList ->
          _isSearching.value = false
          _searchingIngredientList.value = ingredientList.map { Pair(it, it.quantity) }
        },
        onFailure = {
          _isSearching.value = false
          _searchingIngredientList.value = emptyList()
        })
  }
}
