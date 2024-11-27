package com.android.sample.model.ingredient

import com.android.sample.resources.C
import com.android.sample.resources.C.Tag.OPENFOODFACT_REPO_IMAGE_ULR_INVALID
import com.android.sample.resources.C.Tag.OPEN_FOOD_FACTS_URL
import com.android.sample.resources.C.Tag.PRODUCT_BRAND
import com.android.sample.resources.C.Tag.PRODUCT_CATEGORIES
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_NORMAL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_SMALL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL_URL
import com.android.sample.resources.C.Tag.PRODUCT_ID
import com.android.sample.resources.C.Tag.PRODUCT_NAME
import com.android.sample.resources.C.Tag.PRODUCT_QUANTITY
import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject

class OpenFoodFactsIngredientRepository(private val client: OkHttpClient) : IngredientRepository {

  /**
   * Parses a JSON object from the Open Food Facts API to create an Ingredient object.
   *
   * @param json The JSON object containing the ingredient data.
   * @return The Ingredient object created from the JSON data.
   * @throws Exception if the ingredient name is not provided in the JSON data.
   */
  private fun parseOpenFoodFactsJsonToIngredient(json: JSONObject): Ingredient {

    val ingredientName = json.getString(PRODUCT_NAME)

    if (ingredientName.isNullOrEmpty()) {
      throw Exception(C.Tag.INGREDIENT_NAME_NOT_PROVIDED)
    }

    val brands = json.getString(PRODUCT_BRAND) ?: null
    val barcode = json.getLong(PRODUCT_ID)
    val quantity = json.getString(PRODUCT_QUANTITY) ?: null
    val categories = json.getString(PRODUCT_CATEGORIES).split(", ")
    val displayNormal = json.getString(PRODUCT_FRONT_IMAGE_NORMAL_URL)
    val displayThumbnail = json.getString(PRODUCT_FRONT_IMAGE_THUMBNAIL_URL)
    val displaySmall = json.getString(PRODUCT_FRONT_IMAGE_SMALL_URL)

    // Mapping the image URLs to the respective image sizes
    if (displayNormal.isNullOrEmpty() ||
        displayThumbnail.isNullOrEmpty() ||
        displaySmall.isNullOrEmpty()) {
      throw JSONException(OPENFOODFACT_REPO_IMAGE_ULR_INVALID)
    }
    val imageMap =
        mutableMapOf(
            PRODUCT_FRONT_IMAGE_NORMAL_URL to displayNormal,
            PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to displayThumbnail,
            PRODUCT_FRONT_IMAGE_SMALL_URL to displaySmall)

    return Ingredient(
        barCode = barcode,
        name = ingredientName,
        brands = brands,
        quantity = quantity,
        categories = categories,
        images = imageMap)
  }

  override fun get(
      barCode: Long,
      onSuccess: (Ingredient?) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val url = "$OPEN_FOOD_FACTS_URL/api/v2/product/$barCode"

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
    val url = "$OPEN_FOOD_FACTS_URL/cgi/search.pl?search_terms=$name&json=1&page_size=$count"

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
                        try {
                          val ingredient =
                              parseOpenFoodFactsJsonToIngredient(products.getJSONObject(i))

                          ingredient
                        } catch (e: Exception) {
                          null
                        }
                      }

                  onSuccess(ingredients.take(count))
                } catch (e: JSONException) {
                  onFailure(e)
                }
              }
            })
  }

  override fun add(ingredient: Ingredient) {
    TODO("Not yet implemented")
  }

  override fun update(ingredient: Ingredient) {
    TODO("Not yet implemented")
  }

  override fun delete(ingredient: Ingredient) {
    TODO("Not yet implemented")
  }

  override fun getAll(onSuccess: (List<Ingredient>) -> Unit, onFailure: (Exception) -> Unit) {
    TODO("Not yet implemented")
  }
}