package com.android.sample.model.recipe

import com.android.sample.resources.C.Tag.MAXIMUM_RECIPES_TO_FETCH_MEAL_DB
import com.android.sample.resources.C.Tag.MAX_NB_OF_INGREDIENTS_IN_A_RECIPE_MEAL_DB
import com.android.sample.resources.C.Tag.MEAL_DB_USER_AGENT
import com.android.sample.resources.C.Tag.MEAL_DB_USER_AGENT_VALUE
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
class MealDBRecipesRepository(private val client: OkHttpClient) : RecipesRepository {

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
      while ((j <= MAX_NB_OF_INGREDIENTS_IN_A_RECIPE_MEAL_DB) &&
          meal.optString("strIngredient$j", "").isNotEmpty()) {

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

  /**
   * Parses the JSON object to a list of categories.
   *
   * @param json The JSON object to parse.
   */
  private fun parseMealDBJsonToCategory(json: JSONObject): List<String> {
    val parsedListOfCategories = mutableListOf<String>()
    val listOfCategories = json.getJSONArray("meals")
    for (i in 0 until listOfCategories.length()) {
      val category = listOfCategories.getJSONObject(i)
      val strCategory = category.getString("strCategory")
      parsedListOfCategories.add(strCategory)
    }
    return parsedListOfCategories
  }

  /**
   * Parses the JSON object to a list of thumbnails.
   *
   * @param json The JSON object to parse.
   */
  private fun parseMealDBJsonToThumbnails(json: JSONObject): List<List<String>> {
    val parsedListOfThumbnails = mutableListOf<List<String>>()
    val listOfThumbnails = json.getJSONArray("meals")
    for (i in 0 until listOfThumbnails.length()) {
      val thumbnail = listOfThumbnails.getJSONObject(i)
      val strMeal = thumbnail.getString("strMeal")
      val idMeal = thumbnail.getString("idMeal")
      val strMealThumb = thumbnail.getString("strMealThumb")
      parsedListOfThumbnails.add(listOf(strMeal, idMeal, strMealThumb))
    }
    return parsedListOfThumbnails
  }

  override fun random(
      nbOfElements: Int,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    require(nbOfElements <= MAXIMUM_RECIPES_TO_FETCH_MEAL_DB) {
      "Please limit the number of recipes to 5"
    }
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
        Request.Builder().url(url).header(MEAL_DB_USER_AGENT, MEAL_DB_USER_AGENT_VALUE).build()

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

  override fun searchByCategory(
      category: String,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {

    val url = "$mealDBUrl/filter.php?c=$category"
    val request =
        Request.Builder().url(url).header(MEAL_DB_USER_AGENT, MEAL_DB_USER_AGENT_VALUE).build()

    client
        .newCall(request)
        .enqueue(
            object : Callback {
              override fun onFailure(call: Call, e: IOException) {
                onFailure(e)
              }

              override fun onResponse(call: Call, response: Response) {
                try {

                  val listOfRecipes = mutableListOf<Recipe>()
                  val recipes = parseMealDBJsonToThumbnails(JSONObject(response.body!!.string()))
                  for (recipe in recipes) {
                    search(recipe[1], { rec -> listOfRecipes.add(rec) }, onFailure)
                  }
                  onSuccess(listOfRecipes)
                } catch (e: JSONException) {
                  onFailure(e)
                }
              }
            })
  }

  override fun listCategories(onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit) {
    val url = "$mealDBUrl/categories.php"
    val request =
        Request.Builder().url(url).header(MEAL_DB_USER_AGENT, MEAL_DB_USER_AGENT_VALUE).build()
    client
        .newCall(request)
        .enqueue(
            object : Callback {
              override fun onFailure(call: Call, e: IOException) {
                onFailure(e)
              }

              override fun onResponse(call: Call, response: Response) {
                try {
                  val categories = parseMealDBJsonToCategory(JSONObject(response.body!!.string()))
                  onSuccess(categories)
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
        Request.Builder().url(url).header(MEAL_DB_USER_AGENT, MEAL_DB_USER_AGENT_VALUE).build()
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
