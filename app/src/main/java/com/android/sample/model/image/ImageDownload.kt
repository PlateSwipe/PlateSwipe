package com.android.sample.model.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.core.net.toUri
import coil.ImageLoader
import coil.request.ImageRequest
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageDownload {

  /**
   * Downloads an image from the given URL and saves it to the local storage with the specified file
   * name.
   *
   * @param context The context of the application.
   * @param imageUrl The URL of the image to download.
   * @param fileName The name of the file to save the image as.
   * @return The URI of the saved image file as a String, or null if an error occurs.
   */
  suspend fun downloadAndSaveImage(context: Context, imageUrl: String, fileName: String): String? {
    return withContext(Dispatchers.IO) {
      try {
        // Load the image from the URL and convert it to a Bitmap
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context).data(imageUrl).build()
        val result = loader.execute(request)
        val bitmap = (result.drawable as BitmapDrawable).bitmap

        // Save the Bitmap to a file
        val file = File(context.filesDir, "$fileName.jpg")
        val outputStream = FileOutputStream(file)
        // Compress the bitmap and write it to the output stream
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
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
