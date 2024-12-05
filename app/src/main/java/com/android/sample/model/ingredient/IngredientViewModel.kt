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
) : ViewModel(), SearchIngredientViewModel {

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

  private val _ingredientDownloadList = MutableStateFlow<List<Ingredient>>(emptyList())
  val ingredientDownloadList: StateFlow<List<Ingredient>>
    get() = _ingredientDownloadList

  /**
   * Fetch ingredient
   *
   * @param barCode: the barcode of the ingredient to search for
   */
  override fun fetchIngredient(barCode: Long) {
    if (_ingredient.value.first?.barCode == barCode) {
      return
    }
    fetchIngredientByBarcodeAndAddToList(
        barCode,
        _ingredient,
        repository,
        { _isFetchingByBarcode.value = true },
        { _isFetchingByBarcode.value = false })
  }
  /**
   * Add bar code ingredient
   *
   * @param ingredient: the ingredient to add
   */
  override fun addIngredient(ingredient: Ingredient) {
    addIngredientToList(ingredient, _ingredientList)
  }

  /**
   * Update quantity
   *
   * @param ingredient: the ingredient to update
   * @param quantity: the quantity to update
   */
  fun updateQuantity(ingredient: Ingredient, quantity: String) {
    _ingredientList.value =
        _ingredientList.value.map {
          if (it.first == ingredient) {
            Pair(it.first, quantity)
          } else {
            it
          }
        }
  }

  /**
   * Fetch ingredient by name
   *
   * @param name: the name of the ingredient to search for
   */
  override fun fetchIngredientByName(name: String) {
    fetchIngredientByNameAndAddToList(
        name,
        _searchingIngredientList,
        repository,
        { _isSearching.value = true },
        { _isSearching.value = false })
  }

  /**
   * Remove ingredient
   *
   * @param ingredient
   */
  fun removeIngredient(ingredient: Ingredient) {
    _ingredientList.value = _ingredientList.value.filter { it.first != ingredient }
  }
  /** Clear ingredient after use */
  override fun clearIngredient() {
    _ingredient.value = Pair(null, null)
  }

  /** Clear search */
  override fun clearSearchingIngredientList() {
    _searchingIngredientList.value = emptyList()
  }

  /** Clear ingredient list */
  override fun clearIngredientList() {
    _ingredientList.value = emptyList()
  }

  /**
   * Downloads and saves the images of an ingredient.
   *
   * @param ingredient: The ingredient whose images are to be downloaded.
   * @param context: The context used to access resources.
   * @param dispatcher: The coroutine dispatcher to use for the download operations.
   */
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
                    val uri = imgDownload.downloadAndSaveImage(context, fileName, url, dispatcher)
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
