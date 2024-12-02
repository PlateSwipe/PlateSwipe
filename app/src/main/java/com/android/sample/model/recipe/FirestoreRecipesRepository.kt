package com.android.sample.model.recipe

import android.util.Log
import com.android.sample.model.filter.Difficulty
import com.android.sample.model.filter.Filter
import com.android.sample.resources.C.Tag.CHARACTERS
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_AREA
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_CATEGORY
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_DIFFICULTY
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_INGREDIENTS
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_INSTRUCTIONS_TEXT
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_INSTRUCTION_ICON
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_INSTRUCTION_TIME
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_MEASUREMENTS
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_NAME
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_PICTURE_ID
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_PRICE
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_TIME
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_URL
import com.android.sample.resources.C.Tag.Filter.UNINITIALIZED_BORN_VALUE
import com.android.sample.resources.C.Tag.FirestoreRecipesRepository.ERROR_GETTING_DOCUMENT
import com.android.sample.resources.C.Tag.FirestoreRecipesRepository.FILTER_RANDOM_FACTOR
import com.android.sample.resources.C.Tag.FirestoreRecipesRepository.FIRESTORE_COLLECTION_NAME
import com.android.sample.resources.C.Tag.FirestoreRecipesRepository.MAX_FIRESTORE_FETCH
import com.android.sample.resources.C.Tag.FirestoreRecipesRepository.NOT_ENOUGH_RECIPE_MSG
import com.android.sample.resources.C.Tag.FirestoreRecipesRepository.NO_RECIPE_FOUND_MSG
import com.android.sample.resources.C.Tag.FirestoreRecipesRepository.REPOSITORY_TAG_MSG
import com.android.sample.resources.C.Tag.LIMIT_MUST_BE_POSITIVE_MESSAGE
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FirestoreRecipesRepository(private val db: FirebaseFirestore) : RecipesRepository {
  /** ****************************************** */

  /**
   * Generates a new unique identifier for a recipe.
   *
   * @return A new unique identifier for a recipe.
   */
  override fun getNewUid(): String {
    return db.collection(FIRESTORE_COLLECTION_NAME).document().id
  }

  override fun addRecipe(recipe: Recipe, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    performFirestoreOperation(
        db.collection(FIRESTORE_COLLECTION_NAME).document(recipe.uid).set(recipe.toFirestoreMap()),
        onSuccess,
        onFailure)
  }

  override fun updateRecipe(recipe: Recipe, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    performFirestoreOperation(
        db.collection(FIRESTORE_COLLECTION_NAME).document(recipe.uid).set(recipe.toFirestoreMap()),
        onSuccess,
        onFailure)
  }

  override fun deleteRecipe(idMeal: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    performFirestoreOperation(
        db.collection(FIRESTORE_COLLECTION_NAME).document(idMeal).delete(), onSuccess, onFailure)
  }

  /**
   * Converts a Firestore document to a Recipe object.
   *
   * @param document The Firestore document to convert.
   * @return recipe object.
   */
  fun documentToRecipe(document: DocumentSnapshot): Recipe? {
    return try {
      // Only check the mandatory arguments
      val id = document.id
      val name = document.getString(FIRESTORE_RECIPE_NAME) ?: return null
      val category = document.getString(FIRESTORE_RECIPE_CATEGORY)
      val area = document.getString(FIRESTORE_RECIPE_AREA)
      /*
      get the values for the intstruction and create all the list of instructions
       */
      val instructionsText =
          document.get(FIRESTORE_RECIPE_INSTRUCTIONS_TEXT) as List<*>? ?: return null
      val instructionsIcon =
          document.get(FIRESTORE_RECIPE_INSTRUCTION_ICON) as List<*>? ?: return null
      val instructionsTime =
          document.get(FIRESTORE_RECIPE_INSTRUCTION_TIME) as List<*>? ?: return null

      val instructions =
          instructionsText.mapIndexed { index, text ->
            val time = instructionsTime[index] as? String ?: return null // Ensure time is an Int
            val icon = instructionsIcon[index] as? String ?: return null // Ensure icon is a String
            Instruction(
                description = text as String, time = time, iconType = icon) // Cast text to String
          }

      val pictureID = document.getString(FIRESTORE_RECIPE_PICTURE_ID) ?: return null
      val ingredientsData = document.get(FIRESTORE_RECIPE_INGREDIENTS) as List<*>? ?: return null
      val measurementsData = document.get(FIRESTORE_RECIPE_MEASUREMENTS) as List<*>? ?: return null
      val time = document.getString(FIRESTORE_RECIPE_TIME)
      val difficulty = document.getString(FIRESTORE_RECIPE_DIFFICULTY)
      val price = document.getString(FIRESTORE_RECIPE_PRICE)
      val url = document.getString(FIRESTORE_RECIPE_URL)

      val ingredients = ingredientsData.mapNotNull { it as? String }
      val measurements = measurementsData.mapNotNull { it as? String }

      Recipe(
          uid = id,
          name = name,
          category = category,
          origin = area,
          instructions = instructions,
          strMealThumbUrl = pictureID,
          ingredientsAndMeasurements = ingredients.zip(measurements),
          time = time,
          difficulty = difficulty,
          price = price,
          url = url)
    } catch (e: Exception) {
      Log.e(
          "FirestoreRecipesRepository",
          "Error converting document to Recipe : idMeal " + document.id,
          e)
      null
    }
  }

  /**
   * Performs a Firestore operation and calls the appropriate callback based on the result.
   *
   * @param task The Firestore task to perform.
   * @param onSuccess The callback to call if the operation is successful.
   * @param onFailure The callback to call if the operation fails.
   */
  private fun performFirestoreOperation(
      task: Task<Void>,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    task.addOnCompleteListener { result ->
      if (result.isSuccessful) {
        onSuccess()
      } else {
        result.exception?.let { e ->
          Log.e("FirestoreRecipesRepository", "Error performing Firestore operation", e)
          onFailure(e)
        }
      }
    }
  }

  /** @return A random identifier for a recipe. */
  private fun generateRandomUID(): String {
    val characters = CHARACTERS
    val uidLength = 20

    return (1..uidLength).map { characters.random() }.joinToString("")
  }

  /**
   * Private method to fetch random recipes from Firestore.
   *
   * @param nbOfElements The number of random recipes to fetch.
   * @param onSuccess The callback to call if the operation is successful.
   * @param onFailure The callback to call if the operation fails.
   * @param recipes The list of recipes to fill.
   * @param iterationNumber The number of iterations.
   */
  private fun randomFetch(
      nbOfElements: Int,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit,
      recipes: MutableList<Recipe>,
      iterationNumber: Int
  ) {
    // Generate a random UID
    val randomUID = generateRandomUID()

    Log.d(REPOSITORY_TAG_MSG, "randomFetch: $randomUID")
    // Query for UIDs greater than or equal to the random UID
    db.collection(FIRESTORE_COLLECTION_NAME)
        .whereGreaterThanOrEqualTo(FieldPath.documentId(), randomUID)
        .limit(nbOfElements.toLong() - recipes.size)
        .get()
        .addOnCompleteListener { task ->
          if (task.isSuccessful) {

            // Add the recipes to the list
            task.result?.documents?.forEach { document ->
              val recipe = documentToRecipe(document).takeIf { !recipes.contains(it) }
              recipe?.let { recipes.add(it) }
            }
            when {
              recipes.size == nbOfElements -> {
                recipes.shuffle()
                onSuccess(recipes)
              }
              iterationNumber == MAX_FIRESTORE_FETCH -> {
                if (recipes.isNotEmpty()) {
                  Log.e(REPOSITORY_TAG_MSG, NOT_ENOUGH_RECIPE_MSG)
                  recipes.shuffle()
                  onSuccess(recipes)
                } else {
                  Log.e(REPOSITORY_TAG_MSG, NO_RECIPE_FOUND_MSG)
                  onFailure(Exception(NO_RECIPE_FOUND_MSG))
                }
              }
              recipes.size < nbOfElements -> {
                // Fetch more recipes if we don't have enough
                randomFetch(nbOfElements, onSuccess, onFailure, recipes, iterationNumber + 1)
              }
              else -> {
                Log.e(REPOSITORY_TAG_MSG, NO_RECIPE_FOUND_MSG)
                onFailure(Exception(NO_RECIPE_FOUND_MSG))
              }
            }
          } else {
            task.exception?.let { e ->
              Log.e(REPOSITORY_TAG_MSG, ERROR_GETTING_DOCUMENT, e)
              onFailure(e)
            }
          }
        }
  }

  /**
   * Fetches a random list of recipes from Firestore.
   *
   * @param nbOfElements The number of random recipes to fetch.
   * @param onSuccess The callback to call if the operation is successful.
   * @param onFailure The callback to call if the operation fails.
   */
  override fun random(
      nbOfElements: Int,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    require(nbOfElements > 0) { LIMIT_MUST_BE_POSITIVE_MESSAGE }
    val recipes = mutableListOf<Recipe>()
    randomFetch(nbOfElements, onSuccess, onFailure, recipes, 0)
  }

  override fun search(mealID: String, onSuccess: (Recipe) -> Unit, onFailure: (Exception) -> Unit) {
    Log.d("FirestoreRecipesRepository", "search")
    db.collection(FIRESTORE_COLLECTION_NAME).document(mealID).get().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val recipe = documentToRecipe(task.result!!)
        if (recipe != null) {
          onSuccess(recipe)
        } else onFailure(Exception("Recipe not found"))
      } else {
        task.exception?.let { e ->
          Log.e("FirestoreRecipesRepository", "Error search document : idMeal $mealID", e)
          onFailure(e)
        }
      }
    }
  }

  override fun searchByCategory(
      category: String,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit,
      limit: Int
  ) {
    require(limit > 0) { LIMIT_MUST_BE_POSITIVE_MESSAGE }
    val recipes = mutableListOf<Recipe>()
    db.collection(FIRESTORE_COLLECTION_NAME)
        .whereEqualTo(FIRESTORE_RECIPE_CATEGORY, category)
        .limit(limit.toLong())
        .get()
        .addOnSuccessListener { result ->
          result.documents.forEach { document ->
            val recipe = documentToRecipe(document)
            if (recipe != null) {
              recipes.add(recipe)
            }
          }
          onSuccess(recipes)
        }
        .addOnFailureListener { e -> onFailure(e) }
  }

  /**
   * Fetches a list of recipes from Firestore that match the given filter.
   *
   * @param filter The filter to apply to the search.
   * @param limit The number of recipes to fetch.
   * @param onSuccess The callback to call if the operation is successful.
   * @param onFailure The callback to call if the operation fails.
   */
  fun filterSearch(
      filter: Filter,
      limit: Int,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    require(limit > 0) { LIMIT_MUST_BE_POSITIVE_MESSAGE }
    val recipes = mutableListOf<Recipe>()
    var finalQuery: Query = db.collection(FIRESTORE_COLLECTION_NAME)

    // Filter the recipes based on the filter
    finalQuery =
        if (filter.category != null) {
          finalQuery.whereEqualTo(FIRESTORE_RECIPE_CATEGORY, (filter.category!!.toString()))
        } else {
          finalQuery
        }

    finalQuery =
        if (filter.timeRange.min != UNINITIALIZED_BORN_VALUE) {
          finalQuery.whereGreaterThan(
              FIRESTORE_RECIPE_TIME, filter.timeRange.min.toInt().toString())
        } else {
          finalQuery
        }

    finalQuery =
        if (filter.timeRange.max != UNINITIALIZED_BORN_VALUE) {
          finalQuery.whereLessThan(FIRESTORE_RECIPE_TIME, filter.timeRange.max.toInt().toString())
        } else {
          finalQuery
        }
    finalQuery =
        if (filter.priceRange.min != UNINITIALIZED_BORN_VALUE) {
          finalQuery.whereGreaterThan(FIRESTORE_RECIPE_PRICE, filter.priceRange.min.toString())
        } else {
          finalQuery
        }

    finalQuery =
        if (filter.priceRange.max != UNINITIALIZED_BORN_VALUE) {
          finalQuery.whereLessThan(FIRESTORE_RECIPE_PRICE, filter.priceRange.max.toString())
        } else {
          finalQuery
        }

    finalQuery =
        if (filter.difficulty != Difficulty.Undefined) {
          finalQuery.whereEqualTo(FIRESTORE_RECIPE_DIFFICULTY, filter.difficulty.toString())
        } else {
          finalQuery
        }

    // Fetch the recipes
    finalQuery
        .limit(limit.toLong() * FILTER_RANDOM_FACTOR)
        .get()
        .addOnSuccessListener { result ->
          result.documents.forEach { document ->
            val recipe = documentToRecipe(document)
            if (recipe != null) {
              recipes.add(recipe)
            }
          }
          recipes.shuffle()
          onSuccess(recipes.take(limit))
        }
        .addOnFailureListener(onFailure)
  }

  /** Will be deleted in next PR */
  override fun listCategories(onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit) {
    throw NotImplementedError("Not implemented")
  }
}
