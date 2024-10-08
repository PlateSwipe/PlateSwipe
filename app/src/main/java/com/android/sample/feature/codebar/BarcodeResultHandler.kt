package com.android.sample.feature.codebar

interface BarcodeResultHandler {
  fun onBarcodeDetected(barcodeValue: String)
}
