package com.android.sample.ui.camera

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
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

  var bitmapRotated by remember { mutableStateOf<Bitmap?>(null) }

  // Use LaunchedEffect to ensure the image rotation logic is only executed once
  LaunchedEffect(bitmap, rotation) {
    if (bitmap != null) {
      bitmapRotated = rotateBitmap(bitmap!!, rotation)
    }
  }

  bitmapRotated?.let {
    Image(
        bitmap = it.asImageBitmap(),
        contentDescription = "Image taken from camera",
        modifier = Modifier.fillMaxSize().testTag("display_image"))
  }
}
