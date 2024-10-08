package com.android.sample.feature.codebar

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class CodeBarAnalyzer(
    private val barcodeResultHandler: BarcodeResultHandler,
    private val cameraProvider: ProcessCameraProvider
) : ImageAnalysis.Analyzer {

  @OptIn(ExperimentalGetImage::class)
  override fun analyze(imageProxy: ImageProxy) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
      val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
      val options =
          BarcodeScannerOptions.Builder()
              .setBarcodeFormats(
                  Barcode.FORMAT_QR_CODE,
                  Barcode.FORMAT_AZTEC,
                  Barcode.FORMAT_EAN_13,
              )
              .build()
      val scanner = BarcodeScanning.getClient(options)

      scanner
          .process(image)
          .addOnSuccessListener { barcodes ->
            for (barcode in barcodes) {
              val rawValue = barcode.rawValue
              Log.d(TAG, "Barcode detected: $rawValue")
              barcodeResultHandler.onBarcodeDetected(rawValue?.toString() ?: "")
              cameraProvider.unbindAll()
              break
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
