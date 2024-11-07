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

  private fun parseOpenFoodFactsJsonToIngredient(json: JSONObject): Ingredient {

    val ingredientName = json.getString("product_name")
    // Null if there is no branding since it's an optional field
    val brands = json.getString("brands") ?: null
    val barcode = json.getLong("_id")
      val quantity = json.getString("quantity") ?: null
      val categories = json.getString("categories").split(", ")


    return Ingredient(barCode = barcode, name = ingredientName, brands = brands, quantity = quantity, categories =  categories)
  }

  override fun get(
      barCode: Long,
      onSuccess: (Ingredient?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val url = "$openFoodFactsUrl/api/v2/product/$barCode"

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
                  val body = JSONObject(response.body!!.string())

                  val status = body.getInt("status")

                  if (status != 1) {
                    onSuccess(null)
                  } else {
                    val productJson = body.getJSONObject("product")

                    onSuccess(parseOpenFoodFactsJsonToIngredient(productJson))
                  }
                } catch (e: JSONException) {
                  onFailure(e)
                }
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
                  val products = JSONObject(response.body!!.string()).getJSONArray("products")

                  val ingredients: List<Ingredient> =
                      (0 until products.length()).mapNotNull { i ->
                        val ingredient =
                            parseOpenFoodFactsJsonToIngredient(products.getJSONObject(i))

                        ingredient
                      }

                  onSuccess(ingredients.take(count))
                } catch (e: JSONException) {
                  onFailure(e)
                }
              }
            })
  }
}
