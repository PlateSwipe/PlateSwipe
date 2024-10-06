package com.android.sample.model.ingredient

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class OpenFoodFactsIngredientRepository (
    private val client: OkHttpClient
) : BareCodeToIngredientRepository {

    private val openFoodFactsUrl = "https://world.openfoodfacts.net/api/v2"

    override fun get(barCode: Long, onSuccess: (Ingredient) -> Unit, onFailure: (Exception) -> Unit) {
        val url = "$openFoodFactsUrl/product/$barCode"

        val request = Request.Builder()
            .url(url)
            //TODO: Add a proper User-Agent
            .header("User-Agent", "PlateSwipe/1.0 (andre.cadet@epfl.ch)")
            .build()

        client.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFailure(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val bodyJson = JSONObject(response.body!!.string())
                        .getJSONObject("product")

                    val ingredientName = bodyJson.getString("product_name")
                    println("name: $ingredientName")
                    println("body: $bodyJson")

                    onSuccess(
                        Ingredient(
                            name = ingredientName
                        )
                    )
                }
            }
        )
    }
}