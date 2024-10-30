package com.android.sample.model.ingredient

class AggregatorIngredientRepository(
    private val firestoreIngredientRepository: FirestoreIngredientRepository,
    private val openFoodFactsIngredientRepository: OpenFoodFactsIngredientRepository
) : IngredientRepository {

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
                        ingredientOpenFoodFacts, onSuccess = {}, onFailure = onFailure)
                  } else {
                    onFailure(Exception("Ingredient not found"))
                  }
                },
                onFailure)
          }
        },
        onFailure = onFailure)
  }

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
                      ingredientsOpenFoodFacts, onSuccess = {}, onFailure = onFailure)
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
