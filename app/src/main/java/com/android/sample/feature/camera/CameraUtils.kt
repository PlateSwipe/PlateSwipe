package com.android.sample.feature.camera

import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import com.android.sample.resources.C

fun createPreview(): Preview {
  return Preview.Builder().build()
}

fun createImageCapture(): ImageCapture {
  return ImageCapture.Builder().build()
}

/**
 * Handle the barcode detection If the barcode has been scanned barcodeThreshold times, this is a
 * probably? a valid barcode. When the user scan the barcode, it can result in an invalid barcode,
 * because he is moving when doing so or other external factor. To assure that the barcode is valid,
 * we need to check if the barcode has been scanned barcodeThreshold times. For now the number was
 * set arbitrarily to 3.
 */
fun handleBarcodeDetection(
    barcodeValue: Long?,
    recentBarcodes: MutableList<Long>,
    lastScannedBarcode: Long?,
    onBarcodeDetected: (Long) -> Unit
) {
  if (barcodeValue != null) {
    recentBarcodes.add(barcodeValue)
    if (recentBarcodes.count { it == barcodeValue } >= C.Tag.SCAN_THRESHOLD) {
      if (barcodeValue != lastScannedBarcode) {
        onBarcodeDetected(barcodeValue)
      }
      recentBarcodes.clear()
    }
  } else {
    Log.e(C.Tag.LOG_TAG_CAMERA_UTILS, C.Tag.INVALID_BARCODE_MSG)
  }
}
