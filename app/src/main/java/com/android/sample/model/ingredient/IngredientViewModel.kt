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
import com.android.sample.resources.C.Tag.INGR_DOWNLOAD_ERROR_DOWNLOAD_IMAGE
import com.android.sample.resources.C.Tag.INGR_DOWNLOAD_ERROR_GET_ING
import com.android.sample.resources.C.Tag.INGR_DOWNLOAD_ERROR_NULL_POINTER
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
        onFailure = { Log.e(INGREDIENT_VIEWMODEL_LOG_TAG, INGR_DOWNLOAD_ERROR_DOWNLOAD_IMAGE, it) })
  }
  /** Retrieves all downloaded ingredients from the repository. */
  fun getAllDownloadedIngredients() {
    repository.getAllDownload(
        onSuccess = { ingredientDownloadList ->
          _ingredientDownloadList.value = ingredientDownloadList
        },
        onFailure = { e ->
          Log.e(INGREDIENT_VIEWMODEL_LOG_TAG, INGR_DOWNLOAD_ERROR_GET_ING, e)
          _ingredientList.value = emptyList()
        })
  }
  /**
   * Delete an existing downloaded ingredient in the repository.
   *
   * @param ingredient The ingredient to delete.
   */
  fun deleteDownloadedIngredient(ingredient: Ingredient) {
    repository.deleteDownload(ingredient)
  }

  /**
   * Downloads and saves the images of an ingredient.
   *
   * @param ingredient The ingredient whose images are to be downloaded.
   * @param context The context used to access resources.
   * @param dispatcher The coroutine dispatcher to use for the download operations.
   * @param onSuccess Callback function to be invoked when the images are successfully downloaded
   *   and saved.
   * @param onFailure Callback function to be invoked if an error occurs during the download
   *   process.
   */
  private fun downloadIngredientImage(
      ingredient: Ingredient,
      context: Context,
      dispatcher: CoroutineDispatcher,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    CoroutineScope(dispatcher).launch {
      try {
        // Download and save the images of the ingredient for each format.
        val imageFormats = ingredient.images.keys
        val deferredUri =
            imageFormats.map { format ->
              val fileName = ingredient.name + format
              val url = ingredient.images[format]
              async {
                try {
                  if (url != null) {
                    val uri = imgDownload.downloadAndSaveImage(context, fileName, url)
                    format to uri!!
                  } else {
                    null
                  }
                } catch (e: NullPointerException) {
                  e.printStackTrace()
                  null
                }
              }
            }
        // Wait for all the images to be downloaded and saved.
        val uris = deferredUri.awaitAll().filterNotNull()
        // If all the images were successfully downloaded and saved, add them to the ingredient.
        if (uris.size == imageFormats.size) {
          ingredient.images.putAll(uris.associate { it.first to it.second })
          onSuccess()
        } else {
          Log.e(INGREDIENT_VIEWMODEL_LOG_TAG, INGR_DOWNLOAD_ERROR_NULL_POINTER)
          throw Exception(INGR_DOWNLOAD_ERROR_NULL_POINTER)
        }
      } catch (e: Exception) {
        e.printStackTrace()
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
