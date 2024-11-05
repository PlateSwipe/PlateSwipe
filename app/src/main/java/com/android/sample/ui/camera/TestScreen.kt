package com.android.sample.ui.camera

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.android.sample.feature.camera.rotateBitmap
import com.android.sample.model.takePhoto.TakePhotoViewModel

/**
 * Composable function to display an image on the screen.
 *
 * @param takePhotoViewModel ViewModel to manage the state of the photo and its rotation.
 */
@Composable
fun DisplayImageScreen(takePhotoViewModel: TakePhotoViewModel) {
  // Collect the photo bitmap from the ViewModel's StateFlow
  val bitmap: Bitmap? by takePhotoViewModel.photo.collectAsState()
  // Collect the rotation value from the ViewModel's StateFlow
  val rotation: Int by takePhotoViewModel.rotation.collectAsState()
  // Rotate the bitmap by the collected rotation value
  val bitmapRotated = rotateBitmap(bitmap!!, rotation)
  // Display the rotated bitmap as an image
  Image(
      bitmap = bitmapRotated.asImageBitmap(),
      contentDescription = "Image taken from camera",
      modifier = Modifier.fillMaxSize())
}
