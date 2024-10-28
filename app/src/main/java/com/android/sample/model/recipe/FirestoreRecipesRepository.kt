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
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRecipesRepository(private val db: FirebaseFirestore) : RecipesRepository {

  val recipeDB = db.collection(FIRESTORE_COLLECTION_NAME)

    /*********************************************/

    /**
     * Generates a new unique identifier for a recipe.
     *
     * @return A new unique identifier for a recipe.
     */
    fun getNewUid(): String{
        return recipeDB.document().id
    }

    fun addRecipe(recipe: Recipe, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        performFirestoreOperation(
            recipeDB.document(recipe.idMeal).set(recipe), onSuccess, onFailure)
    }

    fun updateRecipe(recipe: Recipe, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        performFirestoreOperation(
            recipeDB.document(recipe.idMeal).set(recipe), onSuccess, onFailure)
    }

    fun deleteRecipe(idMeal: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        performFirestoreOperation(
            recipeDB.document(idMeal).delete(), onSuccess, onFailure)
    }




    /**
     * Converts a Firestore document to a Recipe object.
     *
     * @param document The Firestore document to convert.
     * @return The ToDo object.
     */
    private fun documentToRecipe(document: DocumentSnapshot): Recipe? {
        return try {
            val id = document.id
            val name = document.getString(FIRESTORE_RECIPE_NAME)?: return null
            val category = document.getString(FIRESTORE_RECIPE_CATEGORY)
            val area = document.getString(FIRESTORE_RECIPE_AREA)
            val instructions = document.getString(FIRESTORE_RECIPE_INSTRUCTIONS)?: return null
            val pictureID = document.getString(FIRESTORE_RECIPE_PICTURE_ID)?: return null
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
        }catch (e: Exception){
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



    /*********************************************/

  override fun random(
      nbOfElements: Int,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    // TODO("Not yet implemented")
  }

  override fun search(mealID: String, onSuccess: (Recipe) -> Unit, onFailure: (Exception) -> Unit) {
      Log.d("FirestoreRecipesRepository", "search")
      recipeDB.document(mealID).get().addOnCompleteListener{
          task->
          if(task.isSuccessful){
                val recipe = documentToRecipe(task.result!!)
                if(recipe != null) {
                    onSuccess(recipe)
                }else
                    onFailure(Exception("Recipe not found"))
                }else{
                    task.exception?.let{
                        e->
                        Log.e("FirestoreRecipesRepository", "Error getting documents", e)
                        onFailure(e)
                    }

                }
          }
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


}
