package com.android.sample.feature.camera.scan

import com.android.sample.feature.camera.handleBarcodeDetection
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.kotlin.verify

class HandleBarcodeDetectionTest {

  @Test
  fun `handleBarcodeDetection does nothing with null barcode`() {

    val recentBarcodes = mutableListOf<Long>()
    val lastScannedBarcode: Long? = null
    val onBarcodeDetected: (Long) -> Unit = {}

    handleBarcodeDetection(
        barcodeValue = null,
        recentBarcodes = recentBarcodes,
        lastScannedBarcode = lastScannedBarcode,
        onBarcodeDetected = onBarcodeDetected,
        scanThreshold = 3)

    // Verify that recentBarcodes remains empty when barcodeValue is null
    assertTrue(recentBarcodes.isEmpty())
  }

  @Test
  fun `handleBarcodeDetection doesn't trigger onBarcodeDetected with different barcode`() {

    val recentBarcodes = mutableListOf<Long>()
    val lastScannedBarcode: Long? = null
    val onBarcodeDetected: (Long) -> Unit = mock()

    handleBarcodeDetection(
        barcodeValue = 1L,
        recentBarcodes = recentBarcodes,
        lastScannedBarcode = lastScannedBarcode,
        onBarcodeDetected = onBarcodeDetected,
        scanThreshold = 3)

    handleBarcodeDetection(
        barcodeValue = 2L,
        recentBarcodes = recentBarcodes,
        lastScannedBarcode = lastScannedBarcode,
        onBarcodeDetected = onBarcodeDetected,
        scanThreshold = 3)

    handleBarcodeDetection(
        barcodeValue = 3L,
        recentBarcodes = recentBarcodes,
        lastScannedBarcode = lastScannedBarcode,
        onBarcodeDetected = onBarcodeDetected,
        scanThreshold = 3)

    // Retry with the first barcode
    handleBarcodeDetection(
        barcodeValue = 1L,
        recentBarcodes = recentBarcodes,
        lastScannedBarcode = lastScannedBarcode,
        onBarcodeDetected = onBarcodeDetected,
        scanThreshold = 3)

    // Ensure that onBarcodeDetected was not called since the first barcode did not reach the
    // threshold again
    verify(onBarcodeDetected, never()).invoke(1L)
  }

  @Test
  fun `handleBarcodeDetection invokes onBarcodeDetected after same barcode scanned three times`() {

    val recentBarcodes = mutableListOf<Long>()
    val lastScannedBarcode: Long? = null
    val onBarcodeDetected: (Long) -> Unit = mock()
    val barcodeValue = 1L

    handleBarcodeDetection(
        barcodeValue = barcodeValue,
        recentBarcodes = recentBarcodes,
        lastScannedBarcode = lastScannedBarcode,
        onBarcodeDetected = onBarcodeDetected,
        scanThreshold = 3)

    handleBarcodeDetection(
        barcodeValue = barcodeValue,
        recentBarcodes = recentBarcodes,
        lastScannedBarcode = lastScannedBarcode,
        onBarcodeDetected = onBarcodeDetected,
        scanThreshold = 3)

    // OnBarCodeDetected should be called here since we reach the threshold
    handleBarcodeDetection(
        barcodeValue = barcodeValue,
        recentBarcodes = recentBarcodes,
        lastScannedBarcode = lastScannedBarcode,
        onBarcodeDetected = onBarcodeDetected,
        scanThreshold = 3)
    // The recentBarcodes should be empty after reaching the threshold
    assert(recentBarcodes.isEmpty())
    // OnBarCodeDetected should not be called here, since it's the as the last one
    handleBarcodeDetection(
        barcodeValue = barcodeValue,
        recentBarcodes = recentBarcodes,
        lastScannedBarcode = lastScannedBarcode,
        onBarcodeDetected = onBarcodeDetected,
        scanThreshold = 3)

    // Verify that onBarcodeDetected was called once after reaching the threshold
    verify(onBarcodeDetected, times(1)).invoke(barcodeValue)
  }
}
