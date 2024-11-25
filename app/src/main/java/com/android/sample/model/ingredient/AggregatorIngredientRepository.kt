package com.android.sample.model.ingredient

import android.util.Log
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.image.ImageUploader
import com.android.sample.resources.C
import com.android.sample.resources.C.Tag.AGGREGATOR_ERROR_FIRESTORE_ADD_INGR
import com.android.sample.resources.C.Tag.AGGREGATOR_ERROR_GET_INGR_FIRESTORE
import com.android.sample.resources.C.Tag.AGGREGATOR_ERROR_OPENFOOD_INGR_NOT_FOUND
import com.android.sample.resources.C.Tag.AGGREGATOR_ERROR_OPENFOOD_INGR_NULL
import com.android.sample.resources.C.Tag.AGGREGATOR_ERROR_UPLOAD_FORMAT_IMAGE
import com.android.sample.resources.C.Tag.AGGREGATOR_ERROR_UPLOAD_IMAGE
import com.android.sample.resources.C.Tag.AGGREGATOR_LOG_TAG
import com.android.sample.resources.C.Tag.AGGREGATOR_SUCCESS_FIRESTORE_ADD_INGR
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

open class AggregatorIngredientRepository(
    private val firestoreIngredientRepository: FirestoreIngredientRepository,
    private val openFoodFactsIngredientRepository: OpenFoodFactsIngredientRepository,
    private val imageStorage: ImageRepositoryFirebase,
    private val imageUploader: ImageUploader
) : IngredientRepository {

  /**
   * Get an ingredient by barcode. If it isn't found in Firestore, it will be searched in
   * OpenFoodFacts.Upload the image to Firebase Storage and update the ingredient in Firestore.
   *
   * @param barCode barcode of the ingredient
   * @param onSuccess callback with the ingredient
   * @param onFailure callback with an exception
   */
  override fun get(
      barCode: Long,
      onSuccess: (Ingredient?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    firestoreIngredientRepository.get(
        barCode,
        onSuccess = { ingredientFirestore ->
          if (ingredientFirestore != null) {
            onSuccess(ingredientFirestore)
          } else {
            openFoodFactsIngredientRepository.get(
                barCode,
                onSuccess = { ingredientOpenFoodFacts ->
                  if (ingredientOpenFoodFacts != null) {
                    // Immediately return the ingredient from OpenFoodFacts
                    onSuccess(ingredientOpenFoodFacts)
                    // Start the background upload and update process
                    uploadAndSaveIngredientImages(ingredientOpenFoodFacts, Dispatchers.IO)
                  } else {
                    Log.e(AGGREGATOR_LOG_TAG, AGGREGATOR_ERROR_OPENFOOD_INGR_NOT_FOUND)
                    onFailure(Exception(C.Tag.INGREDIENT_NOT_FOUND_MESSAGE))
                  }
                },
                onFailure = { exception ->
                  Log.e(AGGREGATOR_LOG_TAG, AGGREGATOR_ERROR_OPENFOOD_INGR_NULL + exception.message)
                  onFailure(exception)
                })
          }
        },
        onFailure = { exception ->
          Log.e(AGGREGATOR_LOG_TAG, AGGREGATOR_ERROR_GET_INGR_FIRESTORE + exception.message)
          onFailure(exception)
        })
  }

  /**
   * Search for ingredients by name. If they aren't found in Firestore, they will be searched in
   * OpenFoodFacts. If the count isn't reached when searching Firestore, the remaining ingredients
   * will be searched in OpenFoodFacts.
   *
   * @param name name of the ingredient
   * @param onSuccess callback with the list of ingredients
   * @param onFailure callback with an exception
   * @param count number of ingredients to return
   */
  override fun search(
      name: String,
      onSuccess: (List<Ingredient>) -> Unit,
      onFailure: (Exception) -> Unit,
      count: Int
  ) {
    openFoodFactsIngredientRepository.search(
        name,
        onSuccess = { ingredientsOpenFoodFacts ->
          checkFirestoreIngredients(
              ingredientsOpenFoodFacts,
              onSuccess = { ingredientsFirestore -> onSuccess(ingredientsFirestore) },
              onFailure = onFailure)
          onSuccess(ingredientsOpenFoodFacts)
        },
        onFailure = onFailure,
        count = count)
  }

  private fun checkFirestoreIngredients(
      ingredientsOpenFoodFacts: List<Ingredient>,
      onSuccess: (List<Ingredient>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    var confirmedCount = 0
    val foundIngredients = mutableListOf<Ingredient>()

    for (ingredient in ingredientsOpenFoodFacts) {
      if (ingredient.barCode != null) {
        firestoreIngredientRepository.get(
            ingredient.barCode,
            onSuccess = { ingredientFirestore ->
              if (ingredientFirestore != null) {
                foundIngredients.add(ingredientFirestore)
              } else {
                foundIngredients.add(ingredient)

                firestoreIngredientRepository.add(
                    ingredientsOpenFoodFacts,
                    onSuccess = {
                      Log.i(
                          C.Tag.AGGREGATOR_TAG_ON_INGREDIENT_ADDED,
                          ingredientsOpenFoodFacts.toString())
                    },
                    onFailure = onFailure)
              }

              confirmedCount++
              if (confirmedCount == ingredientsOpenFoodFacts.size) {
                onSuccess(foundIngredients)
              }
            },
            onFailure = onFailure)
      }
    }
  }
  /**
   * Uploads and saves ingredient images to Firebase Storage and updates the ingredient in
   * Firestore.
   *
   * @param ingredient The ingredient whose images are to be uploaded and saved.
   * @param dispatcher The CoroutineDispatcher to be used for the coroutine scope.
   */
  private fun uploadAndSaveIngredientImages(
      ingredient: Ingredient,
      dispatcher: CoroutineDispatcher
  ) {
    CoroutineScope(dispatcher).launch {
      try {
        val imageFormats = ingredient.images.keys
        val deferredUrls =
            imageFormats.map { format ->
              async {
                try {
                  imageUploader.uploadAndRetrieveUrlAsync(
                      ingredient, format, imageStorage, dispatcher)
                } catch (e: Exception) {
                  Log.d(
                      AGGREGATOR_LOG_TAG, AGGREGATOR_ERROR_UPLOAD_FORMAT_IMAGE + format + e.message)
                  null
                }
              }
            }

        // Await all URLs in the background
        val urls = deferredUrls.awaitAll().filterNotNull()

        if (urls.size == imageFormats.size) {
          // Update the ingredient with the new URLs
          ingredient.images.putAll(urls.associate { it.first to it.second })

          // Save the updated ingredient to Firestore
          firestoreIngredientRepository.add(
              ingredient,
              onSuccess = { Log.e(AGGREGATOR_LOG_TAG, AGGREGATOR_SUCCESS_FIRESTORE_ADD_INGR) },
              onFailure = { exception ->
                Log.e(AGGREGATOR_LOG_TAG, AGGREGATOR_ERROR_FIRESTORE_ADD_INGR + exception.message)
              })
        } else {
          Log.e(AGGREGATOR_LOG_TAG, AGGREGATOR_ERROR_UPLOAD_IMAGE)
        }
      } catch (e: Exception) {
        Log.e(AGGREGATOR_LOG_TAG, AGGREGATOR_ERROR_UPLOAD_IMAGE + e.message)
      }
    }
  }
}
