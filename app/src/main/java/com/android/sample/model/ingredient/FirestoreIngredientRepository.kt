package com.android.sample.model.ingredient

import com.android.sample.resources.C
import com.android.sample.resources.C.Tag.FIRESTORE_INGREDIENT_CATEGORIES
import com.android.sample.resources.C.Tag.FIRESTORE_INGREDIENT_IMAGES
import com.android.sample.resources.C.Tag.FIRESTORE_INGREDIENT_QUANTITY
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Firestore implementation of [IngredientRepository].
 *
 * @param db instance of [FirebaseFirestore]
 */
class FirestoreIngredientRepository(private val db: FirebaseFirestore) : IngredientRepository {

  @Suppress("UNCHECKED_CAST")
  private fun documentSnapshotToIngredient(documentSnapshot: DocumentSnapshot): Ingredient {

    val barCode = documentSnapshot.getLong(C.Tag.FIRESTORE_INGREDIENT_BARCODE)
    val name = documentSnapshot.getString(C.Tag.FIRESTORE_INGREDIENT_NAME)
    val brands = documentSnapshot.getString(C.Tag.FIRESTORE_INGREDIENT_BRANDS)
    val quantity = documentSnapshot.getString(FIRESTORE_INGREDIENT_QUANTITY)
    val categories = documentSnapshot[FIRESTORE_INGREDIENT_CATEGORIES] as List<String>
    val images = documentSnapshot[FIRESTORE_INGREDIENT_IMAGES] as MutableMap<String, String>

    if (name.isNullOrEmpty()) {
      throw Exception(C.Tag.INGREDIENT_NAME_NOT_PROVIDED)
    }

    return Ingredient(
        uid = documentSnapshot.id,
        barCode = barCode,
        name = name,
        brands = brands,
        quantity = quantity,
        categories = categories,
        images = images)
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
    db.collection(C.Tag.FIRESTORE_INGREDIENT_COLLECTION_NAME_TEST)
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
   * Add an ingredient. If the ingredient is already in the database, can be updated or not. If the
   * ingredient is not in the database, it will be added.
   *
   * @param ingredient ingredient to add
   * @param onSuccess callback when the ingredient is added
   * @param onFailure callback with an exception
   * @param updateIfPresent if the ingredient is already in the database, update it
   */
  fun add(
      ingredient: Ingredient,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit,
      updateIfPresent: Boolean = false
  ) {
    var addedIngredient = ingredient

    if (ingredient.uid.isNullOrEmpty()) {
      addedIngredient =
          ingredient.copy(
              uid = db.collection(C.Tag.FIRESTORE_INGREDIENT_COLLECTION_NAME_TEST).document().id)
    }

    fun addToUidOperation(uid: String) {
      db.collection(C.Tag.FIRESTORE_INGREDIENT_COLLECTION_NAME_TEST)
          .document(uid)
          .set(addedIngredient)
          .addOnCompleteListener { result ->
            if (result.isSuccessful) {
              onSuccess()
            } else {
              onFailure(result.exception!!)
            }
          }
    }

    // we need to check it has a barcode other wise it will be impossible to get
    // we end up considering ingredients without barcode as invalid
    if (ingredient.barCode == null) {
      onFailure(Exception(C.Tag.INGREDIENT_BARCODE_NOT_PROVIDED))
    } else if (ingredient.name.isEmpty()) {
      onFailure(Exception(C.Tag.INGREDIENT_NAME_NOT_PROVIDED))
    } else {
      // we need to check if there is already an ingredient with the same barcode
      // because we don't use the uids as identifiers
      get(
          ingredient.barCode,
          onSuccess = { existingIngredient ->
            if (existingIngredient == null) {
              addToUidOperation(addedIngredient.uid!!)
            } else {
              if (updateIfPresent) {
                addToUidOperation(existingIngredient.uid!!)
              } else {
                onSuccess()
              }
            }
          },
          onFailure = onFailure)
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
