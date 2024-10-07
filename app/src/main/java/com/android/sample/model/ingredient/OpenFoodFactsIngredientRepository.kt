package com.android.sample.model.ingredient

import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject

class OpenFoodFactsIngredientRepository(private val client: OkHttpClient) : IngredientRepository {

  private val openFoodFactsUrl = "https://world.openfoodfacts.net"

  private fun parseOpenFoodFactsJsonToIngredient(json: JSONObject): Ingredient? {
    try {
      val ingredientName = json.getString("product_name")

      return Ingredient(barCode = json.getLong("_id"), name = ingredientName)
    } catch (e: JSONException) {
      return null
    }
  }

  override fun get(barCode: Long, onSuccess: (Ingredient) -> Unit, onFailure: (Exception) -> Unit) {
    val url = "$openFoodFactsUrl/api/v2/product/$barCode"

    val request =
        Request.Builder()
            .url(url)
            // TODO: Add a proper User-Agent
            .header("User-Agent", "PlateSwipe/1.0 (andre.cadet@epfl.ch)")
            .build()

    client
        .newCall(request)
        .enqueue(
            object : Callback {
              override fun onFailure(call: Call, e: IOException) {
                onFailure(e)
              }

              override fun onResponse(call: Call, response: Response) {
                val productJson = JSONObject(response.body!!.string()).getJSONObject("product")

                val ingredient = parseOpenFoodFactsJsonToIngredient(productJson)

                if (ingredient == null) {
                  onFailure(JSONException("Invalid JSON"))
                  return
                }

                onSuccess(ingredient)
              }
            })
  }

  override fun search(
      name: String,
      onSuccess: (List<Ingredient>) -> Unit,
      onFailure: (Exception) -> Unit,
      count: Int
  ) {
    val url = "$openFoodFactsUrl/cgi/search.pl?search_terms=$name&json=1&page_size=$count"

    val request =
        Request.Builder()
            .url(url)
            // TODO: Add a proper User-Agent
            .header("User-Agent", "PlateSwipe/1.0 (andre.cadet@epfl.ch)")
            .build()

    client
        .newCall(request)
        .enqueue(
            object : Callback {
              override fun onFailure(call: Call, e: IOException) {
                onFailure(e)
              }

              override fun onResponse(call: Call, response: Response) {
                val products = JSONObject(response.body!!.string()).getJSONArray("products")

                val ingredients: List<Ingredient> =
                    (0 until products.length()).mapNotNull { i ->
                      val ingredient = parseOpenFoodFactsJsonToIngredient(products.getJSONObject(i))

                      ingredient
                    }

                onSuccess(ingredients)
              }
            })
  }
}
