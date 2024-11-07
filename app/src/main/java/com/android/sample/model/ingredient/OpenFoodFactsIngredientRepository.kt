package com.android.sample.model.ingredient

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import com.android.sample.model.image.ImageDirectoryType
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.resources.C.Tag.INGREDIENT_IMAGE_ADDED_SUCCESSFULLY
import com.android.sample.resources.C.Tag.OPEN_FOOD_FACTS_INGREDIENT_REPOSITORY_TAG
import com.android.sample.resources.C.Tag.OPEN_FOOD_FACTS_URL
import com.android.sample.resources.C.Tag.PRODUCT_BRAND
import com.android.sample.resources.C.Tag.PRODUCT_CATEGORIES
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_SMALL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_SMALL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_URL
import com.android.sample.resources.C.Tag.PRODUCT_ID
import com.android.sample.resources.C.Tag.PRODUCT_NAME
import com.android.sample.resources.C.Tag.PRODUCT_QUANTITY
import java.io.ByteArrayInputStream
import java.io.IOException
import java.net.URL
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject

class OpenFoodFactsIngredientRepository(
    private val client: OkHttpClient,
    private val imageStorage: ImageRepositoryFirebase
) : IngredientRepository {

  private fun parseOpenFoodFactsJsonToIngredient(json: JSONObject): Ingredient {

    val ingredientName = json.getString(PRODUCT_NAME)
    // Null if there is no branding since it's an optional field
    val brands = json.getString(PRODUCT_BRAND) ?: null
    val barcode = json.getLong(PRODUCT_ID)
    val quantity = json.getString(PRODUCT_QUANTITY) ?: null
    val categories = json.getString(PRODUCT_CATEGORIES).split(", ")

    val images = mutableListOf<String>()
    val displayNormal = json.getString(PRODUCT_FRONT_IMAGE_URL)
    if (displayNormal.isNotEmpty()) {
      uploadImageToStorage(displayNormal, barcode, PRODUCT_FRONT_IMAGE)
      images.add(PRODUCT_FRONT_IMAGE)
    }

    val displayThumbnail = json.getString(PRODUCT_FRONT_IMAGE_THUMBNAIL_URL)
    if (displayThumbnail.isNotEmpty()) {
      uploadImageToStorage(displayThumbnail, barcode, PRODUCT_FRONT_IMAGE_THUMBNAIL)
      images.add(PRODUCT_FRONT_IMAGE_THUMBNAIL)
    }

    val displaySmall = json.getString(PRODUCT_FRONT_IMAGE_SMALL_URL)
    if (displaySmall.isNotEmpty()) {
      uploadImageToStorage(displaySmall, barcode, PRODUCT_FRONT_IMAGE_SMALL)
      images.add(PRODUCT_FRONT_IMAGE_SMALL)
    }

    return Ingredient(
        barCode = barcode,
        name = ingredientName,
        brands = brands,
        quantity = quantity,
        categories = categories,
        images = images.toList())
  }

  private fun uploadImageToStorage(imageURL: String, barcode: Long, fileName: String) {
    val imageUrl = URL(imageURL)
    val imageInputStream = imageUrl.openStream().readBytes()
    val bitmapImage =
        BitmapFactory.decodeStream(ByteArrayInputStream(imageInputStream)).asImageBitmap()
    imageStorage.uploadImage(
        barcode.toString(),
        fileName,
        ImageDirectoryType.INGREDIENT,
        bitmapImage,
        onSuccess = {
          Log.i(OPEN_FOOD_FACTS_INGREDIENT_REPOSITORY_TAG, INGREDIENT_IMAGE_ADDED_SUCCESSFULLY)
        },
        onFailure = { e ->
          e.message?.let { Log.e(OPEN_FOOD_FACTS_INGREDIENT_REPOSITORY_TAG, it) }
        })
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
