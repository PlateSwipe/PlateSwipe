package com.android.sample

interface BarcodeResultHandler {
    fun onBarcodeDetected(barcodeValue: String)
}