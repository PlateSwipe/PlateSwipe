package com.android.sample.model.recipe

import com.android.sample.resources.C.Tag.FIRESTORE_COLLECTION_NAME
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRecipeRepository(private val db: FirebaseFirestore) : RecipesRepository {

  val recipeDB = db.collection(FIRESTORE_COLLECTION_NAME)

  override fun random(
      nbOfElements: Int,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // TODO("Not yet implemented")
  }

  override fun search(mealID: String, onSuccess: (Recipe) -> Unit, onFailure: (Exception) -> Unit) {
    // TODO("Not yet implemented")
  }

  override fun searchByCategory(
      category: String,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // TODO("Not yet implemented")
  }

  override fun listCategories(onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit) {
    // TODO("Not yet implemented")
  }

  fun addRecipe(recipe: Recipe) {
    // TODO("Not yet implemented")
  }

  fun deleteRecipe(idMeal: String) {
    // TODO("Not yet implemented")
  }

  fun modifyRecipe(recipe: Recipe) {
    db.collection(FIRESTORE_COLLECTION_NAME).document(recipe.idMeal).set(recipe)
  }
}
