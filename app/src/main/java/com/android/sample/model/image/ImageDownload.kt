package com.android.sample.model.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.core.net.toUri
import coil.ImageLoader
import coil.request.ImageRequest
import com.android.sample.resources.C.Tag.IMG_COMPRESS_SIZE
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.withContext

class ImageDownload {

  companion object {
    @Volatile private var imageLoader: ImageLoader? = null

    fun getImageLoader(context: Context): ImageLoader {
      // Use ApplicationContext to prevent memory leaks
      val appContext = context.applicationContext
      return imageLoader
          ?: synchronized(this) { imageLoader ?: ImageLoader(appContext).also { imageLoader = it } }
    }
  }

  /**
   * Downloads an image from the given URL and saves it to the local storage with the specified file
   * name.
   *
   * @param context The context of the application.
   * @param imageUrl The URL of the image to download.
   * @param fileName The name of the file to save the image as.
   * @return The URI of the saved image file as a String, or null if an error occurs.
   */
  suspend fun downloadAndSaveImage(
      context: Context,
      imageUrl: String,
      fileName: String,
      dispatcher: CoroutineContext
  ): String? {
    return withContext(dispatcher) {
      try {
        // Load the image from the URL and convert it to a Bitmap
        val loader = getImageLoader(context)
        val request = ImageRequest.Builder(context).data(imageUrl).build()
        val result = loader.execute(request)
        val bitmap = (result.drawable as BitmapDrawable).bitmap

        // Save the Bitmap to a file
        val file = File(context.filesDir, "$fileName.jpg")
        val outputStream = FileOutputStream(file)
        // Compress the bitmap and write it to the output stream
        bitmap.compress(Bitmap.CompressFormat.JPEG, IMG_COMPRESS_SIZE, outputStream)
        outputStream.close()

        // Return the file URI as a String
        file.toUri().toString()
      } catch (e: Exception) {
        e.printStackTrace()
        null
      }
    }
  }
}
