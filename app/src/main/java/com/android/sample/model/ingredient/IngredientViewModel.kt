package com.android.sample.model.ingredient

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.sample.model.image.ImageDownload
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.image.ImageUploader
import com.android.sample.model.ingredient.localData.IngredientDatabase
import com.android.sample.model.ingredient.localData.RoomIngredientRepository
import com.android.sample.model.ingredient.networkData.AggregatorIngredientRepository
import com.android.sample.model.ingredient.networkData.FirestoreIngredientRepository
import com.android.sample.model.ingredient.networkData.OpenFoodFactsIngredientRepository
import com.android.sample.resources.C.Tag.INGREDIENT_NOT_FOUND_MESSAGE
import com.android.sample.resources.C.Tag.INGREDIENT_VIEWMODEL_LOG_TAG
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

/**
 * Ingredient View Model
 *
 * @param repository
 * @constructor Create empty Ingredient view model
 */
class IngredientViewModel(
    private val repository: IngredientRepository,
    private val imgDownload: ImageDownload
) : ViewModel() {

  private val _ingredient = MutableStateFlow<Ingredient?>(null)
  val ingredient: StateFlow<Ingredient?>
    get() = _ingredient

  private val _ingredientList = MutableStateFlow<List<Ingredient>>(emptyList())
  val ingredientList: StateFlow<List<Ingredient>>
    get() = _ingredientList

  private val _searchingIngredientList = MutableStateFlow<List<Ingredient>>(emptyList())
  val searchingIngredientList: StateFlow<List<Ingredient>>
    get() = _searchingIngredientList

  private val _isSearching = MutableStateFlow(false)
  val isSearching: StateFlow<Boolean>
    get() = _isSearching

  private val _ingredientDownloadList = MutableStateFlow<List<Ingredient>>(emptyList())
  val ingredientDownloadList: StateFlow<List<Ingredient>>
    get() = _ingredientDownloadList

  /**
   * Fetch ingredient
   *
   * @param barCode
   */
  fun fetchIngredient(barCode: Long) {
    if (_ingredient.value?.barCode == barCode) {
      return
    }
    repository.get(
        barCode,
        onSuccess = { ingredient -> _ingredient.value = ingredient },
        onFailure = {
          Log.e(INGREDIENT_VIEWMODEL_LOG_TAG, INGREDIENT_NOT_FOUND_MESSAGE)
          _ingredient.value = null
        })
  }

  /**
   * Add bar code ingredient
   *
   * @param ingredient
   */
  fun addIngredient(ingredient: Ingredient) {
    _ingredientList.value += ingredient
  }

  /**
   * Update quantity
   *
   * @param ingredient
   * @param quantity
   */
  fun updateQuantity(ingredient: Ingredient, quantity: String) {
    _ingredientList.value =
        _ingredientList.value.map {
          if (it == ingredient) {
            it.copy(quantity = quantity)
          } else {
            it
          }
        }
  }

  /**
   * Fetch ingredient by name
   *
   * @param name
   */
  fun fetchIngredientByName(name: String) {
    _isSearching.value = true
    repository.search(
        name,
        onSuccess = { ingredientList ->
          _isSearching.value = false
          _searchingIngredientList.value = ingredientList
        },
        onFailure = {
          _isSearching.value = false
          _searchingIngredientList.value = emptyList()
        })
  }

  /**
   * Remove ingredient
   *
   * @param ingredient
   */
  fun removeIngredient(ingredient: Ingredient) {
    _ingredientList.value = _ingredientList.value.filter { it != ingredient }
  }

  /** Clear search */
  fun clearSearch() {
    _searchingIngredientList.value = emptyList()
  }

  fun downloadIngredient(
      ingredient: Ingredient,
      context: Context,
      dispatcher: CoroutineDispatcher
  ) {
    downloadIngredientImage(
        ingredient,
        context,
        dispatcher,
        onSuccess = { repository.addDownload(ingredient) },
        onFailure = { Log.e("IngredientViewModel", "Error downloading images", it) })
  }

  fun getAllDownloadedIngredients() {
    repository.getAllDownload(
        onSuccess = { ingredientDownloadList ->
          _ingredientDownloadList.value = ingredientDownloadList
        },
        onFailure = { e ->
          Log.e("IngredientViewModel", "Error getting ingredients", e)
          _ingredientList.value = emptyList()
        })
  }

  private fun downloadIngredientImage(
      ingredient: Ingredient,
      context: Context,
      dispatcher: CoroutineDispatcher,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    CoroutineScope(dispatcher).launch {
      try {
        val imageFormats = ingredient.images.keys
        val deferredUri =
            imageFormats.map { format ->
              val fileName = ingredient.name + format
              async {
                try {
                  val uri =
                      imgDownload.downloadAndSaveImage(
                          context, fileName, ingredient.images[format]!!)
                  println("URI: $uri")
                  format to uri!!
                } catch (e: NullPointerException) {
                  e.printStackTrace()
                  null
                }
              }
            }
        val uris = deferredUri.awaitAll().filterNotNull()
        if (uris.size == imageFormats.size) {
          ingredient.images.putAll(uris.associate { it.first to it.second })
          onSuccess()
        } else {
          Log.e("IngredientViewModel", "Not all images were downloaded")
          throw Exception("Not all images were downloaded")
        }
      } catch (e: Exception) {
        e.printStackTrace()
        Log.e("IngredientViewModel", "Error downloading images", e)
        onFailure(e)
      }
    }
  }

  companion object {
    fun provideFactory(context: Context): ViewModelProvider.Factory {
      val appContext = context.applicationContext
      return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          val networkRepository =
              AggregatorIngredientRepository(
                  FirestoreIngredientRepository(Firebase.firestore),
                  OpenFoodFactsIngredientRepository(OkHttpClient()),
                  ImageRepositoryFirebase(Firebase.storage),
                  ImageUploader())
          val appDatabase = IngredientDatabase.getDatabase(appContext)
          val ingredientDao = appDatabase.ingredientDao()
          val localRepository = RoomIngredientRepository(ingredientDao, Dispatchers.IO)
          val defaultRepository = DefaultIngredientRepository(localRepository, networkRepository)
          return IngredientViewModel(defaultRepository, ImageDownload()) as T
        }
      }
    }
  }
}
