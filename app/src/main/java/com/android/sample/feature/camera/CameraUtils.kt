package com.android.sample.feature.camera

import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview

fun createPreview(): Preview {
  return Preview.Builder().build()
}

fun createImageCapture(): ImageCapture {
  return ImageCapture.Builder().build()
}
