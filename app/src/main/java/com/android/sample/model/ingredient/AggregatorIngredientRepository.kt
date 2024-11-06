package com.android.sample.model.ingredient

import android.util.Log
import com.android.sample.resources.C

class AggregatorIngredientRepository(
    private val firestoreIngredientRepository: FirestoreIngredientRepository,
    private val openFoodFactsIngredientRepository: OpenFoodFactsIngredientRepository
) : IngredientRepository {

  /**
   * Get an ingredient by barcode. If it isn't found in Firestore, it will be searched in
   * OpenFoodFacts.
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
                    onSuccess(ingredientOpenFoodFacts)
                    firestoreIngredientRepository.add(
                        ingredientOpenFoodFacts,
                        onSuccess = {
                          Log.i(
                              C.Tag.AGGREGATOR_TAG_ON_INGREDIENT_ADDED,
                              ingredientOpenFoodFacts.name)
                        },
                        onFailure = onFailure)
                  } else {
                    onFailure(Exception(C.Tag.INGREDIENT_NOT_FOUND_MESSAGE))
                  }
                },
                onFailure)
          }
        },
        onFailure = onFailure)
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
    firestoreIngredientRepository.search(
        name,
        onSuccess = { ingredientsFirestore ->
          val countOpenFoodFacts = count - ingredientsFirestore.size

          if (countOpenFoodFacts > 0) {
            openFoodFactsIngredientRepository.search(
                name,
                onSuccess = { ingredientsOpenFoodFacts ->
                  val ingredients = ingredientsFirestore + ingredientsOpenFoodFacts
                  firestoreIngredientRepository.add(
                      ingredientsOpenFoodFacts,
                      onSuccess = {
                        Log.i(
                            C.Tag.AGGREGATOR_TAG_ON_INGREDIENT_ADDED,
                            ingredientsOpenFoodFacts.toString())
                      },
                      onFailure = onFailure)
                  onSuccess(ingredients)
                },
                onFailure = onFailure,
                count = countOpenFoodFacts)
          } else {
            onSuccess(ingredientsFirestore)
          }
        },
        onFailure = onFailure,
        count = count)
  }
}
