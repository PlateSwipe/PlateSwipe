package com.android.sample.model.recipe

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.sample.model.filter.Difficulty
import com.android.sample.model.filter.Filter
import com.android.sample.model.filter.FilterPageViewModel
import com.android.sample.model.filter.FloatRange
import com.android.sample.model.image.ImageDownload
import com.android.sample.model.recipe.localData.RecipeDatabase
import com.android.sample.model.recipe.localData.RoomRecipeRepository
import com.android.sample.model.recipe.networkData.FirestoreRecipesRepository
import com.android.sample.resources.C.Tag.ERROR_DELETE_DOWNLOAD
import com.android.sample.resources.C.Tag.ERROR_DOWNLOAD_IMG
import com.android.sample.resources.C.Tag.ERROR_RECIPE_WITH_NO_IMG
import com.android.sample.resources.C.Tag.EXCEPTION
import com.android.sample.resources.C.Tag.Filter.UNINITIALIZED_BORN_VALUE
import com.android.sample.resources.C.Tag.GET_ALL_DOWNLOAD_RECIPE
import com.android.sample.resources.C.Tag.LOG_TAG_RECIPE_VIEWMODEL
import com.android.sample.resources.C.Tag.MINIMUM_RECIPES_BEFORE_FETCH
import com.android.sample.resources.C.Tag.NUMBER_RECIPES_TO_FETCH
import com.android.sample.resources.C.Tag.RECIPE_DOWNLOAD_SUCCESS
import com.android.sample.resources.C.Tag.SUCCESS_DELETE_DOWNLOAD_ALL
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for managing recipes.
 *
 * @property repository The repository used to fetch recipe data.
 */
class RecipesViewModel(
    private val repository: RecipesRepository,
    private val imageDownload: ImageDownload
) : ViewModel(), FilterPageViewModel, RecipeOverviewViewModel {

  // StateFlow to monitor the list of recipes
  private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
  val recipes: StateFlow<List<Recipe>>
    get() = _recipes

  private val _downloadedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
  val downloadedRecipes: StateFlow<List<Recipe>>
    get() = _downloadedRecipes

  // StateFlow for loading/error states
  private val _loading = MutableStateFlow(false)
  val loading: StateFlow<Boolean>
    get() = _loading

  // StateFlow for the current selected recipe
  private val _currentRecipe = MutableStateFlow<Recipe?>(null)
  override val currentRecipe: StateFlow<Recipe?>
    get() = _currentRecipe

  private val _nextRecipe = MutableStateFlow<Recipe?>(null)
  val nextRecipe: StateFlow<Recipe?>
    get() = _nextRecipe

  private val _filter = MutableStateFlow(Filter())
  override val filter: StateFlow<Filter>
    get() = _filter

  private val _tmpFilter = MutableStateFlow(Filter())
  override val tmpFilter: StateFlow<Filter>
    get() = _tmpFilter

  private val _categories = MutableStateFlow<List<String>>(emptyList())
  override val categories: StateFlow<List<String>>
    get() = _categories

  private val isFilterUsed = MutableStateFlow(false)

  override val timeRangeState: StateFlow<FloatRange> =
      _tmpFilter
          .map { it.timeRange }
          .stateIn(
              viewModelScope,
              SharingStarted.WhileSubscribed(),
              initialValue =
                  FloatRange(
                      UNINITIALIZED_BORN_VALUE,
                      UNINITIALIZED_BORN_VALUE,
                      UNINITIALIZED_BORN_VALUE,
                      UNINITIALIZED_BORN_VALUE))

  init {
    viewModelScope.launch {
      getCategoryList()
      fetchRandomRecipes(NUMBER_RECIPES_TO_FETCH)
      getAllDownloads(
          onSuccess = { _ -> Log.d(LOG_TAG_RECIPE_VIEWMODEL, GET_ALL_DOWNLOAD_RECIPE) },
          onFailure = { e -> Log.e(LOG_TAG_RECIPE_VIEWMODEL, EXCEPTION + e.message) })

      _loading.collect { isLoading ->
        if (!isLoading) {
          if (_recipes.value.isNotEmpty()) updateCurrentRecipe(_recipes.value.first())
          return@collect
        }
      }
    }
  }

  /** Fetches the list of categories from the repository. */
  override fun getCategoryList() {
    _categories.value = Recipe.getCategories()
  }

  /**
   * Updates the difficulty filter.
   *
   * @param difficulty The difficulty to filter by.
   */
  override fun updateDifficulty(difficulty: Difficulty) {
    _tmpFilter.value.difficulty = difficulty
    isFilterUsed.value = checkIfFilterUsed()
  }

  /**
   * Updates the time range filter.
   *
   * @param min The minimum time.
   * @param max The maximum time.
   */
  override fun updateTimeRange(min: Float, max: Float) {
    _tmpFilter.value.timeRange.update(min, max)
    isFilterUsed.value = checkIfFilterUsed()
  }

  /** Applies the changes made to the filters. */
  override fun applyChanges() {
    _filter.value = _tmpFilter.value
    viewModelScope.launch {
      _recipes.value = emptyList()
      _currentRecipe.value = null
      _nextRecipe.value = null
      isFilterUsed.value = checkIfFilterUsed()
      fetchRandomRecipes(NUMBER_RECIPES_TO_FETCH)
      _loading.collect { isLoading ->
        if (!isLoading) {
          if (_recipes.value.isNotEmpty()) updateCurrentRecipe(_recipes.value.first())
          return@collect
        }
      }
    }
  }

  /** Checks if the filter is used. */
  private fun checkIfFilterUsed(): Boolean {
    return _filter.value.difficulty != Difficulty.Undefined ||
        _filter.value.category != null ||
        _filter.value.timeRange.min != UNINITIALIZED_BORN_VALUE ||
        _filter.value.timeRange.max != UNINITIALIZED_BORN_VALUE
  }

  /** Resets all filters to their default values. */
  override fun resetFilters() {
    isFilterUsed.value = false
    _tmpFilter.value = Filter()
    _filter.value = Filter()
  }

  /** Initializes the filter. */
  override fun initFilter() {

    if (_filter.value.timeRange.min != UNINITIALIZED_BORN_VALUE &&
        _filter.value.timeRange.max != UNINITIALIZED_BORN_VALUE) {
      _tmpFilter.value.timeRange.update(UNINITIALIZED_BORN_VALUE, UNINITIALIZED_BORN_VALUE)
      _filter.value.timeRange.update(UNINITIALIZED_BORN_VALUE, UNINITIALIZED_BORN_VALUE)
    }
    _tmpFilter.value.difficulty = _filter.value.difficulty
    _tmpFilter.value.category = _filter.value.category
  }

  /**
   * Updates the category filter.
   *
   * @param category The category to filter by.
   */
  override fun updateCategory(category: String?) {
    _tmpFilter.value.category = category
    isFilterUsed.value = checkIfFilterUsed()
  }

  /**
   * Fetches a specified number of random recipes from the repository.
   *
   * @param numberOfRecipes The number of random recipes to fetch.
   */
  fun fetchRandomRecipes(numberOfRecipes: Int) {
    require(numberOfRecipes >= 1) { "Number of fetched recipes must be at least 1" }
    _loading.value = true // Set loading to true while fetching
    // uncomment when backend is ready. Tested with hardcoded mealDB data
    if (isFilterUsed.value) {
      repository.filterSearch(
          filter = _filter.value,
          onSuccess = { categoryRecipes ->
            _recipes.value += categoryRecipes
            _loading.value = false
          },
          onFailure = { exception ->
            _loading.value = false
            Log.e("RecipesViewModel", "Error fetching recipes", exception)
          })
    } else {
      repository.random(
          nbOfElements = numberOfRecipes,
          onSuccess = { randomRecipes ->
            _recipes.value += randomRecipes // Add to the list of recipes
            _loading.value = false // Set loading to false after fetching
          },
          onFailure = { exception ->
            _loading.value = false // Set loading to false on failure
            Log.e("RecipesViewModel", "Error fetching recipes", exception)
          })
    }
  }

  /**
   * Updates the current selected recipe.
   *
   * @param recipe The recipe to set as the current recipe.
   */
  fun updateCurrentRecipe(recipe: Recipe) {
    _currentRecipe.value = recipe
    updateNextRecipe()
  }

  /** Clears the current selected recipe. */
  fun clearCurrentRecipe() {
    _currentRecipe.value = null
  }

  /** Gives the next recipe in the list of recipes. */
  fun nextRecipe() {
    val currentRecipes = _recipes.value.toMutableList()
    if (currentRecipes.isNotEmpty() && _currentRecipe.value != null) {
      // Get the index of the current recipe
      val currentIndex = currentRecipes.indexOf(_currentRecipe.value)

      // Calculate the next recipe index
      val nextRecipeIndex = (currentIndex + 1) % currentRecipes.size
      val nextRecipe = currentRecipes[nextRecipeIndex]

      // Remove the current recipe from the list
      currentRecipes.removeAt(currentIndex)
      _recipes.value = currentRecipes

      // Update the current recipe to the next one
      _currentRecipe.value = nextRecipe

      // Update the next recipe
      updateNextRecipe()

      // Check if there are only 3 recipes left and fetch 2 new recipes
      if (currentRecipes.size <= MINIMUM_RECIPES_BEFORE_FETCH) {
        viewModelScope.launch { fetchRandomRecipes(NUMBER_RECIPES_TO_FETCH) }
      }
    }
  }

  /**
   * Updates the next recipe in the list of recipes.
   *
   * This method assumes that _currentRecipe is already set and valid. It should be called after
   * nextRecipe() to ensure the next recipe is updated correctly.
   */
  private fun updateNextRecipe() {
    val currentRecipes = _recipes.value
    if (currentRecipes.isNotEmpty()) {
      val nextRecipeIndex = (currentRecipes.indexOf(_currentRecipe.value) + 1) % currentRecipes.size
      _nextRecipe.value = currentRecipes[nextRecipeIndex]
    } else {
      _nextRecipe.value = null
    }
  }

  /**
   * Searches for a recipe by its meal ID.
   *
   * @param mealID The ID of the meal to search for.
   * @param onSuccess Callback to be invoked when the search is successful.
   * @param onFailure Callback to be invoked when the search fails.
   */
  fun search(mealID: String, onSuccess: (Recipe) -> Unit, onFailure: (Exception) -> Unit) {
    repository.search(
        mealID = mealID,
        onSuccess = { recipe -> onSuccess(recipe) },
        onFailure = { exception -> onFailure(exception) })
  }

  /**
   * Downloads a recipe and updates the recipe with the new image URI.
   *
   * @param recipe The recipe to download.
   * @param onSuccess Callback to be invoked when the download is successful.
   * @param onFailure Callback to be invoked when the download fails.
   * @param context The context used for downloading the image.
   */
  fun downloadRecipe(
      recipe: Recipe,
      onSuccess: (Recipe) -> Unit,
      onFailure: (Exception) -> Unit,
      context: Context
  ) {
    if (recipe.url == null) {
      onFailure(Exception(ERROR_RECIPE_WITH_NO_IMG))
      return
    }
    // Download the Image of the Recipe, if successful update the recipe with the new URI and add it
    // to the database
    downloadImage(
        recipe.url!!,
        recipe.uid,
        context,
        Dispatchers.IO,
        onSuccess = { uri ->
          val newRecipe = recipe.copy(url = uri)
          repository.addDownload(
              newRecipe,
              onSuccess = {
                _downloadedRecipes.value += newRecipe
                onSuccess(newRecipe)
              },
              onFailure = { e -> onFailure(e) })
        },
        onFailure = { e -> onFailure(e) })
  }
  /**
   * Downloads an image from a URL and saves it locally.
   *
   * @param url The URL of the image to download.
   * @param name The name to save the image as.
   * @param context The context used for downloading the image.
   * @param dispatcher The coroutine dispatcher to use for the download.
   * @param onSuccess Callback to be invoked when the download is successful.
   * @param onFailure Callback to be invoked when the download fails.
   */
  private fun downloadImage(
      url: String,
      name: String,
      context: Context,
      dispatcher: CoroutineDispatcher,
      onSuccess: (String) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    CoroutineScope(dispatcher).launch {
      try {
        // Download the image and save it locally, return the URI of the saved image
        val deferredUri = async {
          imageDownload.downloadAndSaveImage(context, url, name, dispatcher)
        }
        val uri = deferredUri.await()
        if (uri != null) {
          onSuccess(uri)
        } else {
          onFailure(Exception(ERROR_DOWNLOAD_IMG))
        }
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }
  /**
   * Retrieves all downloaded recipes and updates the recipeDL StateFlow.
   *
   * @param onSuccess Callback to be invoked when the retrieval is successful.
   * @param onFailure Callback to be invoked when the retrieval fails.
   */
  fun getAllDownloads(onSuccess: (List<Recipe>) -> Unit, onFailure: (Exception) -> Unit) {
    repository.getAllDownload(
        onSuccess = { recipes ->
          _downloadedRecipes.value = recipes
          onSuccess(recipes)
        },
        onFailure = { exception -> onFailure(exception) })
  }

  /**
   * Deletes a downloaded recipe.
   *
   * @param recipe The recipe to delete.
   */
  fun deleteDownload(recipe: Recipe) {
    repository.deleteDownload(recipe)
    _downloadedRecipes.value = _downloadedRecipes.value.filter { it.uid != recipe.uid }
  }

  /** Deletes all downloaded recipes. */
  fun deleteAllDownloads() {
    repository.deleteAllDownloads(
        onSuccess = { Log.d(LOG_TAG_RECIPE_VIEWMODEL, SUCCESS_DELETE_DOWNLOAD_ALL) },
        onFailure = { Log.d(LOG_TAG_RECIPE_VIEWMODEL, ERROR_DELETE_DOWNLOAD) })
  }

  fun downloadAll(recipes: List<Recipe>, context: Context) {
    recipes.forEach {
      downloadRecipe(
          it,
          { Log.d(LOG_TAG_RECIPE_VIEWMODEL, RECIPE_DOWNLOAD_SUCCESS) },
          { e -> Log.e(LOG_TAG_RECIPE_VIEWMODEL, "Exception:${e.message}") },
          context)
    }
  }

  fun setDownload(recipes: List<Recipe>) {
    _downloadedRecipes.value = recipes
  }

  companion object {
    fun provideFactory(context: Context): ViewModelProvider.Factory {
      val appContext = context.applicationContext
      return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          val networkRepository = FirestoreRecipesRepository(Firebase.firestore)
          val appDatabase = RecipeDatabase.getDatabase(appContext)
          val recipeDao = appDatabase.recipeDao()
          val localRepository = RoomRecipeRepository(recipeDao, Dispatchers.IO)
          val defaultRepository = DefaultRecipeRepository(localRepository, networkRepository)
          return RecipesViewModel(defaultRepository, ImageDownload()) as T
        }
      }
    }
  }
}
