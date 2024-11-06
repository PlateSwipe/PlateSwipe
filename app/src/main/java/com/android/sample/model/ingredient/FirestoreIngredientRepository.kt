package com.android.sample.model.ingredient

import com.android.sample.resources.C
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Firestore implementation of [IngredientRepository].
 *
 * @param db instance of [FirebaseFirestore]
 */
class FirestoreIngredientRepository(private val db: FirebaseFirestore) : IngredientRepository {

  private fun documentSnapshotToIngredient(documentSnapshot: DocumentSnapshot): Ingredient {

    val barCode = documentSnapshot.getLong(C.Tag.FIRESTORE_INGREDIENT_BARCODE)
    val name = documentSnapshot.getString(C.Tag.FIRESTORE_INGREDIENT_NAME)
    val brands = documentSnapshot.getString(C.Tag.FIRESTORE_INGREDIENT_BRANDS)

    if (name.isNullOrEmpty()) {
      throw Exception(C.Tag.INGREDIENT_NAME_NOT_PROVIDED)
    }

    return Ingredient(uid = documentSnapshot.id, barCode = barCode, name = name, brands = brands)
  }

  /**
   * Get an ingredient by barcode.
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
    searchFiltered(
        Filter.equalTo("barCode", barCode),
        onSuccess = { ingredients ->
          if (ingredients.isEmpty()) (onSuccess(null))
          else {
            onSuccess(ingredients[0])
          }
        },
        onFailure = onFailure,
        count = 1)
  }

  /**
   * Search for ingredients by name.
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
    searchFiltered(Filter.equalTo("name", name), onSuccess, onFailure, count)
  }

  /**
   * Search for ingredients by filter.
   *
   * @param filter filter to apply
   * @param onSuccess callback with the list of ingredients
   * @param onFailure callback with an exception
   * @param count number of ingredients to return
   */
  fun searchFiltered(
      filter: Filter,
      onSuccess: (List<Ingredient>) -> Unit,
      onFailure: (Exception) -> Unit,
      count: Int = 20
  ) {
    db.collection(C.Tag.FIRESTORE_INGREDIENT_COLLECTION_NAME)
        .where(filter)
        .limit(count.toLong())
        .get()
        .addOnCompleteListener { result ->
          if (result.isSuccessful) {
            val ingredients =
                result.result!!.documents.mapNotNull { d ->
                  try {
                    documentSnapshotToIngredient(d)
                  } catch (e: Exception) {
                    onFailure(e)
                    return@addOnCompleteListener
                  }
                }
            onSuccess(ingredients)
          } else {
            onFailure(result.exception!!)
          }
        }
  }

  /**
   * Add an ingredient. If the ingredient has a uid, it will be updated.
   *
   * @param ingredient ingredient to add
   * @param onSuccess callback when the ingredient is added
   * @param onFailure callback with an exception
   */
  fun add(ingredient: Ingredient, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    var addedIngredient = ingredient

    if (ingredient.uid.isNullOrEmpty()) {
      addedIngredient =
          ingredient.copy(
              uid = db.collection(C.Tag.FIRESTORE_INGREDIENT_COLLECTION_NAME).document().id)
    }

    db.collection(C.Tag.FIRESTORE_INGREDIENT_COLLECTION_NAME)
        .document(addedIngredient.uid!!)
        .set(addedIngredient)
        .addOnCompleteListener { result ->
          if (result.isSuccessful) {
            onSuccess()
          } else {
            onFailure(result.exception!!)
          }
        }
  }

  /**
   * Add a list of ingredients. If the ingredients exist, they will be updated.
   *
   * @param ingredient list of ingredients to add
   * @param onSuccess callback when the ingredients are added
   * @param onFailure callback with an exception
   */
  fun add(ingredient: List<Ingredient>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    ingredient.forEach { i -> add(i, onSuccess, onFailure) }
  }
}
