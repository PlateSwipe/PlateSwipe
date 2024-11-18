package com.android.sample.model.image

import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_NORMAL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_NORMAL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_SMALL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_SMALL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL_URL
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageUploader {
  /**
   * Uploads an image and retrieves its URL asynchronously.
   *
   * @param ingredient The ingredient containing the image to be uploaded.
   * @param imageFormat The format of the image to be uploaded.
   * @return A pair containing the image format and the new URL, or null if an error occurs.
   */
  suspend fun uploadAndRetrieveUrlAsync(
      ingredient: Ingredient,
      imageFormat: String,
      imageStorage: ImageRepositoryFirebase,
  ): Pair<String, String>? {
    return withContext(Dispatchers.IO) {
      try {
        assert(ingredient.images.containsKey(imageFormat)) {
          "Image format $imageFormat not found in ingredient"
        }
        assert(ingredient.images[imageFormat] != "") {
          "Image URL for format $imageFormat is blank"
        }
        assert(ingredient.barCode != null) { "Ingredient barcode is null" }
        assert(
            imageFormat in
                listOf(
                    PRODUCT_FRONT_IMAGE_SMALL_URL,
                    PRODUCT_FRONT_IMAGE_NORMAL_URL,
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL)) {
              "Image format : $imageFormat is not supported"
            }
        Log.d("AggregatorIngredientRepository", "Uploading and Retrieving image $imageFormat")
        val url = ingredient.images[imageFormat]
        val bitmap = imageStorage.urlToBitmap(url!!)
        val imageName = urlToName(imageFormat)
        // Upload image
        val uploadResult = CompletableDeferred<String>()
        imageStorage.uploadImage(
            ingredient.barCode.toString(),
            imageName,
            ImageDirectoryType.TEST,
            bitmap!!.asImageBitmap(),
            onSuccess = {
              uploadResult.complete("")
            }, // We don't care about the result, we just want to know when it's done
            onFailure = { uploadResult.completeExceptionally(it) })

        // Wait for upload to complete
        uploadResult.await()
        Log.d("AggregatorIngredientRepository", "Image $imageFormat uploaded successfully")

        // Retrieve URL
        val urlResult = CompletableDeferred<String>()
        imageStorage.getImageUrl(
            ingredient.barCode.toString(),
            imageName,
            ImageDirectoryType.TEST,
            onSuccess = { uri -> urlResult.complete(uri.toString()) },
            onFailure = { exception -> urlResult.completeExceptionally(exception) })

        // Wait for URL retrieval to complete
        val newUrl = urlResult.await()
        Log.d(
            "AggregatorIngredientRepository",
            "URL for $imageFormat retrieved successfully : $newUrl")
        // Return the pair of format -> new Url
        imageFormat to newUrl
      } catch (e: Exception) {
        Log.e("AggregatorIngredientRepository", "Error in uploadAndRetrieveUrlAsync: ${e.message}")
        e.printStackTrace()
        null
      }
    }
  }

  fun urlToName(imageFormat: String): String {
    return when (imageFormat) {
      PRODUCT_FRONT_IMAGE_SMALL_URL -> PRODUCT_FRONT_IMAGE_SMALL
      PRODUCT_FRONT_IMAGE_NORMAL_URL -> PRODUCT_FRONT_IMAGE_NORMAL
      PRODUCT_FRONT_IMAGE_THUMBNAIL_URL -> PRODUCT_FRONT_IMAGE_THUMBNAIL
      else -> throw IllegalArgumentException("Unsupported image format: $imageFormat")
    }
  }
}
