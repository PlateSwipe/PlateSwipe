package com.android.sample.model.ingredient

import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore

const val COLLECTION_PATH = "ingredients"

class FirestoreIngredientRepository(private val db: FirebaseFirestore) : IngredientRepository {

  private fun documentSnapshotToIngredient(documentSnapshot: Any): Ingredient {
    val document = documentSnapshot as Map<*, *>

    return Ingredient(
        barCode = document["barCode"] as Long,
        name = document["name"] as String,
    )
  }

  override fun get(
      barCode: Long,
      onSuccess: (Ingredient?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    db.collection(COLLECTION_PATH).document(barCode.toString()).get().addOnCompleteListener { result
      ->
      if (result.isSuccessful) {
        val ingredient = documentSnapshotToIngredient(result.result!!)
        onSuccess(ingredient)
      } else {
        onFailure(result.exception!!)
      }
    }
  }

  override fun search(
      name: String,
      onSuccess: (List<Ingredient>) -> Unit,
      onFailure: (Exception) -> Unit,
      count: Int
  ) {
    db.collection(COLLECTION_PATH)
        .whereEqualTo("name", name)
        .limit(count.toLong())
        .get()
        .addOnCompleteListener { result ->
          if (result.isSuccessful) {
            val ingredients = result.result!!.documents.map { d -> documentSnapshotToIngredient(d) }
            onSuccess(ingredients)
          } else {
            onFailure(result.exception!!)
          }
        }
  }

  fun searchFiltered(
      filter: Filter,
      onSuccess: (List<Ingredient>) -> Unit,
      onFailure: (Exception) -> Unit,
      count: Int
  ) {
    db.collection(COLLECTION_PATH)
        .where(filter)
        .limit(count.toLong())
        .get()
        .addOnCompleteListener { result ->
          if (result.isSuccessful) {
            val ingredients = result.result!!.documents.map { d -> documentSnapshotToIngredient(d) }
            onSuccess(ingredients)
          } else {
            onFailure(result.exception!!)
          }
        }
  }

  fun add(ingredient: Ingredient, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    db.collection(COLLECTION_PATH)
        .document(ingredient.barCode.toString())
        .set(ingredient)
        .addOnCompleteListener { result ->
          if (result.isSuccessful) {
            onSuccess()
          } else {
            onFailure(result.exception!!)
          }
        }
  }

  fun add(ingredient: List<Ingredient>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    ingredient.forEach { i -> add(i, onSuccess, onFailure) }
  }
}
