package com.android.sample.model.ingredient

import android.util.Log
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.image.ImageUploader
import com.android.sample.resources.C
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
            Log.d("AggregatorIngredientRepository", "Ingredient found in Firestore")
            onSuccess(ingredientFirestore)
          } else {
            Log.d("AggregatorIngredientRepository", "Ingredient not found in Firestore")
            openFoodFactsIngredientRepository.get(
                barCode,
                onSuccess = { ingredientOpenFoodFacts ->
                  Log.d("AggregatorIngredientRepository", "Ingredient found in OpenFoodFacts")
                  if (ingredientOpenFoodFacts != null) {
                    // Immediately return the ingredient from OpenFoodFacts
                    onSuccess(ingredientOpenFoodFacts)
                    // Start the background upload and update process
                    uploadAndSaveIngredientImages(ingredientOpenFoodFacts, Dispatchers.IO)
                  } else {
                    Log.d("AggregatorIngredientRepository", "Ingredient not found in OpenFoodFacts")
                    onFailure(Exception(C.Tag.INGREDIENT_NOT_FOUND_MESSAGE))
                  }
                },
                onFailure = { exception ->
                  Log.d(
                      "AggregatorIngredientRepository",
                      "Ingredient not found in OpenFoodFacts : ${exception.message}")
                  onFailure(exception)
                })
          }
        },
        onFailure = { exception ->
          Log.e(
              "AggregatorIngredientRepository",
              "Error getting ingredient from Firestore: ${exception.message}")
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
                  imageUploader.uploadAndRetrieveUrlAsync(ingredient, format, imageStorage)
                } catch (e: Exception) {
                  Log.e(
                      "AggregatorIngredientRepository",
                      "Error uploading format $format: ${e.message}")
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
              onSuccess = {
                Log.d(
                    "AggregatorIngredientRepository",
                    "Ingredient successfully updated in Firestore")
              },
              onFailure = { exception ->
                println("Error adding ingredient to Firestore: ${exception.message}")
                Log.e(
                    "AggregatorIngredientRepository",
                    "Error adding ingredient to Firestore: ${exception.message}")
              })
        } else {
          Log.e("AggregatorIngredientRepository", "Failed to upload all images")
        }
      } catch (e: Exception) {
        Log.e("AggregatorIngredientRepository", "Background upload failed: ${e.message}")
      }
    }
  }
}
