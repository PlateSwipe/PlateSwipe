package com.android.sample.feature.camera.scan

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

/**
 * CodeBarAnalyzer
 *
 * @constructor Create empty Code bar analyzer
 * @property onBarcodeDetected
 * @author https://developers.google.com/ml-kit/vision/barcode-scanning/android
 */
class CodeBarAnalyzer(private val onBarcodeDetected: (Barcode) -> Unit) : ImageAnalysis.Analyzer {

  @OptIn(ExperimentalGetImage::class)
  override fun analyze(imageProxy: ImageProxy) {
    // Get the media image from the image proxy
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
      // Create an InputImage object from the media image
      val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
      // Define the option of the barcode scanner
      val options =
          BarcodeScannerOptions.Builder()
              .setBarcodeFormats(
                  Barcode.FORMAT_QR_CODE,
                  Barcode.FORMAT_AZTEC,
                  Barcode.FORMAT_EAN_13,
                  Barcode.FORMAT_EAN_8 // Format for food item in Europe
                  )
              .build()
      val scanner = BarcodeScanning.getClient(options)

      scanner
          .process(image)
          .addOnSuccessListener { barcodes ->
            for (barcode in barcodes) {
              onBarcodeDetected(barcode)
            }
          }
          .addOnFailureListener { Log.e(TAG, "Barcode detection failed", it) }
          .addOnCompleteListener { imageProxy.close() }
    } else {
      imageProxy.close()
    }
  }

  companion object {
    private const val TAG = "CodeBarAnalyzer"
  }
}
