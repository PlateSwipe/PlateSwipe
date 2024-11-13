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
    Log.i("test", "search")

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
    val foundIngredients = mutableListOf<Ingredient>()

    for (ingredient in ingredientsOpenFoodFacts) {
      Log.i("test", ingredient.name + "  " + ingredient.barCode)
      if (ingredient.barCode != null) {
        firestoreIngredientRepository.get(
            ingredient.barCode,
            onSuccess = { ingredientFirestore ->
              if (ingredientFirestore != null) {
                Log.i("test", "found " + ingredientFirestore.name)
                foundIngredients.add(ingredientFirestore)
              } else {
                foundIngredients.add(ingredient)

                firestoreIngredientRepository.add(
                    ingredientsOpenFoodFacts,
                    onSuccess = {
                      Log.i(
                          C.Tag.AGGREGATOR_TAG_ON_INGREDIENT_ADDED,
                          ingredientsOpenFoodFacts.toString())
                      Log.i("test", "added" + ingredientsOpenFoodFacts.toString())
                    },
                    onFailure = onFailure)
              }
            },
            onFailure = onFailure)
      }
    }
    onSuccess(foundIngredients)
  }
}
