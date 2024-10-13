package com.android.sample.model.recipe

import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject

class MealDBRecipeRepository(private val client: OkHttpClient) : RecipeRepository {

  private val mealDBUrl = "https://www.themealdb.com/api/json/v1/1/"

  private fun parseMealDBJsontoRecipe(json: JSONObject): List<Recipe> {
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
      for (j in 1..20) {

        val ingredient = meal.optString("strIngredient$j", "")
        val measurement = meal.optString("strMeasure$j", "")
        if (ingredient.isNotEmpty() && measurement.isNotEmpty()) {
          ingredientsAndMeasurements.add(Pair(ingredient, measurement))
        }
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

  /** Please limit the number of recipes to 3 */
  override fun random(
      nbOfElements: Int,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    if (nbOfElements > 3) {
      throw IllegalArgumentException("Please limit the number of recipes to 3")
    }
    val listOfRandomRecipes = mutableListOf<Recipe>()
    for (i in 0 until nbOfElements) {
      getOneRandomRecipe(
          onSuccess = {
            listOfRandomRecipes.add(it[0])
            if (listOfRandomRecipes.size == nbOfElements.toInt()) {
              onSuccess(listOfRandomRecipes)
            }
          },
          onFailure = onFailure)
    }
  }

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
                  val recipe = parseMealDBJsontoRecipe(JSONObject(response.body!!.string()))
                  onSuccess(recipe)
                } catch (e: JSONException) {
                  onFailure(e)
                }
              }
            })
  }
}
