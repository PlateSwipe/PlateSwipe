package com.android.sample.feature.camera

import android.graphics.Bitmap
import android.graphics.Matrix
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
    onBarcodeDetected: (Long) -> Unit,
    scanThreshold: Int
) {
  if (barcodeValue != null) {
    recentBarcodes.add(barcodeValue)
    if (recentBarcodes.count { it == barcodeValue } >= scanThreshold) {
      if (barcodeValue != lastScannedBarcode) {
        onBarcodeDetected(barcodeValue)
      }
      recentBarcodes.clear()
    }
  } else {
    Log.e(C.Tag.LOG_TAG_CAMERA_UTILS, C.Tag.INVALID_BARCODE_MSG)
  }
}

/**
 * Rotate clock-wisely the bitmap by the given degrees.
 *
 * @param bitmap The bitmap to rotate.
 * @param rotationDegrees The degrees to rotate the bitmap.
 * @return The rotated bitmap.
 * @see <a href="https://stackoverflow.com/questions/56590783/how-to-rotate-bitmap-in-android">How
 *   to rotate bitmap in Android</a>
 */
fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Int): Bitmap {
  assert(rotationDegrees in -360..360) { "Rotation degrees must be between -360 and 360" }
  assert(rotationDegrees % 90 == 0) { "Rotation degrees must be a multiple of 90" }
  if (rotationDegrees == 0) return bitmap
  if (rotationDegrees < 0) return rotateBitmap(bitmap, rotationDegrees + 360)
  val matrix = Matrix()
  matrix.preRotate(rotationDegrees.toFloat())
  val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
  return rotatedBitmap
}
