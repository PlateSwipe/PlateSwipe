package com.android.sample.model.recipe

import android.util.Log
import com.android.sample.resources.C.Tag.FIRESTORE_COLLECTION_NAME
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_AREA
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_CATEGORY
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_DIFFICULTY
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_INGREDIENTS
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_INSTRUCTIONS
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_MEASUREMENTS
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_NAME
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_PICTURE_ID
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_PRICE
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_TIME
import com.android.sample.resources.C.Tag.LIMIT_MUST_BE_POSITIVE_MESSAGE
import com.android.sample.resources.C.Tag.UNSUPPORTED_MESSAGE
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

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
        db.collection(FIRESTORE_COLLECTION_NAME)
            .document(recipe.idMeal)
            .set(recipe.toFirestoreMap()),
        onSuccess,
        onFailure)
  }

  override fun updateRecipe(recipe: Recipe, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    performFirestoreOperation(
        db.collection(FIRESTORE_COLLECTION_NAME)
            .document(recipe.idMeal)
            .set(recipe.toFirestoreMap()),
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
      val id = document.id
      val name = document.getString(FIRESTORE_RECIPE_NAME) ?: return null
      val category = document.getString(FIRESTORE_RECIPE_CATEGORY)
      val area = document.getString(FIRESTORE_RECIPE_AREA)
      val instructions = document.getString(FIRESTORE_RECIPE_INSTRUCTIONS) ?: return null
      val pictureID = document.getString(FIRESTORE_RECIPE_PICTURE_ID) ?: return null
      val ingredientsData = document.get(FIRESTORE_RECIPE_INGREDIENTS) as List<*>? ?: return null
      val measurementsData = document.get(FIRESTORE_RECIPE_MEASUREMENTS) as List<*>? ?: return null
      val time = document.getString(FIRESTORE_RECIPE_TIME)
      val difficulty = document.getString(FIRESTORE_RECIPE_DIFFICULTY)
      val price = document.getString(FIRESTORE_RECIPE_PRICE)

      val ingredients = ingredientsData.mapNotNull { it as? String }
      val measurements = measurementsData.mapNotNull { it as? String }

      Recipe(
          idMeal = id,
          strMeal = name,
          strCategory = category,
          strArea = area,
          strInstructions = instructions,
          strMealThumbUrl = pictureID,
          ingredientsAndMeasurements = ingredients.zip(measurements),
          time = time,
          difficulty = difficulty,
          price = price)
    } catch (e: Exception) {
      Log.e("FirestoreRecipesRepository", "Error converting document to Recipe", e)
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

  /** ****************************************** */
  override fun random(
      nbOfElements: Int,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    require(nbOfElements > 0) { LIMIT_MUST_BE_POSITIVE_MESSAGE }
    // TODO("Not yet implemented")
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
          Log.e("FirestoreRecipesRepository", "Error getting documents", e)
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

    /** This method will not be in the interface anymore in the future **/
  override fun listCategories(onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit) {
    throw UnsupportedOperationException(UNSUPPORTED_MESSAGE)
  }
}
