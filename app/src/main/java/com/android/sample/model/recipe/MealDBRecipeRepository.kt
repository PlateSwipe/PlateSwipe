package com.android.sample.model.recipe

import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject

/**
 * Repository for fetching recipes from the MealDB API.
 *
 * @property client The OkHttpClient used to make network requests.
 */
class MealDBRecipeRepository(private val client: OkHttpClient) : RecipeRepository {

  private val mealDBUrl = "https://www.themealdb.com/api/json/v1/1/"

  /**
   * Fetches a specified number of random recipes from the MealDB API.
   *
   * @param json The JSON object to parse.
   */
  private fun parseMealDBJsonToRecipe(json: JSONObject): List<Recipe> {
    val parsedListOfRecipes = mutableListOf<Recipe>()
    val listOfRecipes = json.getJSONArray("meals")
    for (i in 0 until listOfRecipes.length()) {
      val meal = listOfRecipes.getJSONObject(i)
      val idMeal = meal.getString("idMeal")
      val strMeal = meal.getString("strMeal")
      val strCategory = meal.getString("strCategory")
      val strArea = meal.getString("strArea")
      val strInstructions = meal.getString("strInstructions")
      val strMealThumbUrl = meal.getString("strMealThumb")
      val ingredientsAndMeasurements = mutableListOf<Pair<String, String>>()
      var j = 1
      while ((j <= 20) && meal.optString("strIngredient$j", "").isNotEmpty()) {

        val ingredient = meal.optString("strIngredient$j", "")
        val measurement = meal.optString("strMeasure$j", "")
        if (ingredient.isNotEmpty() && measurement.isNotEmpty()) {
          ingredientsAndMeasurements.add(Pair(ingredient, measurement))
        }
        j++
      }
      parsedListOfRecipes.add(
          Recipe(
              idMeal,
              strMeal,
              strCategory,
              strArea,
              strInstructions,
              strMealThumbUrl,
              ingredientsAndMeasurements))
    }
    return parsedListOfRecipes
  }

  override fun random(
      nbOfElements: Int,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    require(nbOfElements <= 5) { "Please limit the number of recipes to 5" }
    val listOfRandomRecipes = mutableListOf<Recipe>()
    // Fetch a random recipe for each element because the API does not support fetching multiple
    // random recipes at once
    for (i in 0 until nbOfElements) {
      getOneRandomRecipe(
          onSuccess = {
            listOfRandomRecipes.add(it[0])
            if (listOfRandomRecipes.size == nbOfElements) {
              onSuccess(listOfRandomRecipes)
            }
          },
          onFailure = onFailure)
    }
  }

  override fun search(mealID: String, onSuccess: (Recipe) -> Unit, onFailure: (Exception) -> Unit) {
    val url = "$mealDBUrl/lookup.php?i=$mealID"
    val request =
        Request.Builder()
            .url(url)
            .header("User-Agent", "PlateSwipe/1.0 (plateswipe@gmail.com)")
            .build()

    client
        .newCall(request)
        .enqueue(
            object : Callback {
              override fun onFailure(call: Call, e: IOException) {
                onFailure(e)
              }

              override fun onResponse(call: Call, response: Response) {
                try {
                  val recipe = parseMealDBJsonToRecipe(JSONObject(response.body!!.string()))
                  if (recipe.isEmpty()) {
                    throw JSONException("No recipe found")
                  }
                  onSuccess(recipe[0])
                } catch (e: JSONException) {
                  onFailure(e)
                }
              }
            })
  }

  /**
   * Helper method to fetch a random recipe from the MealDB API.
   *
   * @param onSuccess The callback to be invoked when the recipe is successfully fetched.
   * @param onFailure The callback to be invoked when an error occurs.
   */
  private fun getOneRandomRecipe(
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val url = "$mealDBUrl/random.php"
    val request =
        Request.Builder()
            .url(url)
            .header("User-Agent", "PlateSwipe/1.0 (plateswipe@gmail.com)")
            .build()
    client
        .newCall(request)
        .enqueue(
            object : Callback {
              override fun onFailure(call: Call, e: IOException) {
                onFailure(e)
              }

              override fun onResponse(call: Call, response: Response) {
                try {
                  val recipe = parseMealDBJsonToRecipe(JSONObject(response.body!!.string()))
                  onSuccess(recipe)
                } catch (e: JSONException) {
                  onFailure(e)
                }
              }
            })
  }
}
