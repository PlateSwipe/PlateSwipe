package com.android.sample.model.ingredient

import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import com.android.sample.model.image.ImageDirectoryType
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.resources.C
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_NORMAL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_NORMAL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_SMALL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_SMALL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL_URL
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class AggregatorIngredientRepository(
    private val firestoreIngredientRepository: FirestoreIngredientRepository,
    private val openFoodFactsIngredientRepository: OpenFoodFactsIngredientRepository,
    private val imageStorage: ImageRepositoryFirebase
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
                    CoroutineScope(Dispatchers.IO).launch {
                      try {
                        val imageFormats = ingredientOpenFoodFacts.images.keys
                        val deferredUrls =
                            imageFormats.map { format ->
                              async {
                                try {
                                  uploadAndRetrieveUrlAsync(ingredientOpenFoodFacts, format)
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
                          ingredientOpenFoodFacts.images.putAll(
                              urls.associate { it.first to it.second })

                          // Save the updated ingredient to Firestore
                          firestoreIngredientRepository.add(
                              ingredientOpenFoodFacts,
                              onSuccess = {
                                Log.d(
                                    "AggregatorIngredientRepository",
                                    "Ingredient successfully updated in Firestore")
                              },
                              onFailure = { exception ->
                                Log.e(
                                    "AggregatorIngredientRepository",
                                    "Error adding ingredient to Firestore: ${exception.message}")
                              })
                        } else {
                          Log.e("AggregatorIngredientRepository", "Failed to upload all images")
                        }
                      } catch (e: Exception) {
                        Log.e(
                            "AggregatorIngredientRepository",
                            "Background upload failed: ${e.message}")
                      }
                    }
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
   * Uploads an image and retrieves its URL asynchronously.
   *
   * @param ingredient The ingredient containing the image to be uploaded.
   * @param imageFormat The format of the image to be uploaded.
   * @return A pair containing the image format and the new URL, or null if an error occurs.
   */
  suspend fun uploadAndRetrieveUrlAsync(
      ingredient: Ingredient,
      imageFormat: String
  ): Pair<String, String>? {
    return withContext(Dispatchers.IO) {
      try {
        assert(ingredient.images.containsKey(imageFormat)) {
          "Image format $imageFormat not found in ingredient"
        }
        assert(ingredient.images[imageFormat] != "") {
          "Image URL for format $imageFormat is blank"
        }
        assert(ingredient.barCode != null) { "Ingredient barcode is null" }
        assert(
            imageFormat in
                listOf(
                    PRODUCT_FRONT_IMAGE_SMALL_URL,
                    PRODUCT_FRONT_IMAGE_NORMAL_URL,
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL)) {
              "Image format : $imageFormat is not supported"
            }
        Log.d("AggregatorIngredientRepository", "Uploading and Retrieving image $imageFormat")
        val url = ingredient.images[imageFormat]
        val bitmap = imageStorage.urlToBitmap(url!!)
        val imageName = urlToName(imageFormat)
        // Upload image
        val uploadResult = CompletableDeferred<String>()
        imageStorage.uploadImage(
            ingredient.barCode.toString(),
            imageName,
            ImageDirectoryType.TEST,
            bitmap!!.asImageBitmap(),
            onSuccess = {
              uploadResult.complete("")
            }, // We don't care about the result, we just want to know when it's done
            onFailure = { uploadResult.completeExceptionally(it) })

        // Wait for upload to complete
        uploadResult.await()
        Log.d("AggregatorIngredientRepository", "Image $imageFormat uploaded successfully")

        // Retrieve URL
        val urlResult = CompletableDeferred<String>()
        imageStorage.getImageUrl(
            ingredient.barCode.toString(),
            imageName,
            ImageDirectoryType.TEST,
            onSuccess = { uri -> urlResult.complete(uri.toString()) },
            onFailure = { exception -> urlResult.completeExceptionally(exception) })

        // Wait for URL retrieval to complete
        val newUrl = urlResult.await()
        Log.d(
            "AggregatorIngredientRepository",
            "URL for $imageFormat retrieved successfully : $newUrl")
        // Return the pair of format -> new Url
        imageFormat to newUrl
      } catch (e: Exception) {
        Log.e("AggregatorIngredientRepository", "Error in uploadAndRetrieveUrlAsync: ${e.message}")
        e.printStackTrace()
        null
      }
    }
  }

  fun urlToName(imageFormat: String): String {
    return when (imageFormat) {
      PRODUCT_FRONT_IMAGE_SMALL_URL -> PRODUCT_FRONT_IMAGE_SMALL
      PRODUCT_FRONT_IMAGE_NORMAL_URL -> PRODUCT_FRONT_IMAGE_NORMAL
      PRODUCT_FRONT_IMAGE_THUMBNAIL_URL -> PRODUCT_FRONT_IMAGE_THUMBNAIL
      else -> throw IllegalArgumentException("Unsupported image format: $imageFormat")
    }
  }
}
