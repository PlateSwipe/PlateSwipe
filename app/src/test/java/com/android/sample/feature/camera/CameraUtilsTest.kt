package com.android.sample.feature.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CameraUtilsTest {
  private val context: Context = ApplicationProvider.getApplicationContext<Context>()

  @Test
  fun testRotateBitmap_outOfRange() {
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    assertThrows(AssertionError::class.java) {
      rotateBitmap(bitmap, 361) // Should fail
    }
    assertThrows(AssertionError::class.java) {
      rotateBitmap(bitmap, -361) // Should fail
    }
  }

  @Test
  fun testRotateBitmap_withinRange() {
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    rotateBitmap(bitmap, 0) // Should pass
    rotateBitmap(bitmap, 90) // Should pass
    rotateBitmap(bitmap, 180) // Should pass
    rotateBitmap(bitmap, 270) // Should pass
    rotateBitmap(bitmap, 360) // Should pass
    rotateBitmap(bitmap, -90) // Should pass
    rotateBitmap(bitmap, -180) // Should pass
    rotateBitmap(bitmap, -270) // Should pass
    rotateBitmap(bitmap, -360) // Should pass
  }

  @Test
  fun testRotateBitmap_multipleOf90() {
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    assertThrows(AssertionError::class.java) {
      rotateBitmap(bitmap, 45) // Should fail
    }
    assertThrows(AssertionError::class.java) {
      rotateBitmap(bitmap, -45) // Should fail
    }
  }

  @Test
  fun rotateBitmap_handlesZeroRotation() {
    val bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
    bitmap.setPixel(0, 0, Color.BLACK)

    val result = rotateBitmap(bitmap, 0)

    assertEquals(Color.BLACK, result.getPixel(0, 0))
  }

  @Test
  fun rotateBitmap_dontChangeDim() {
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    bitmap.setPixel(0, 0, Color.BLACK)

    val result = rotateBitmap(bitmap, -90)
    assert(result.width == 100)
    assert(result.height == 100)
  }

  @Test
  fun testScoobyGourmandRotateCorrectly90() {
    val pathNoRotation = "app/src/test/resources/images/scoobygourmand_normal.jpg"
    val path90Rotation = "app/src/test/resources/images/scoobygourmand_90.jpg"
    val noRotationBitmap = BitmapFactory.decodeFile(pathNoRotation)
    val rotatedBitmap = BitmapFactory.decodeFile(path90Rotation)
    assert(noRotationBitmap != null)
    assert(rotatedBitmap != null)
    assert(noRotationBitmap.width == rotatedBitmap.width)
    assert(noRotationBitmap.height == rotatedBitmap.height)

    val result = rotateBitmap(noRotationBitmap, 90)
    assert(result.width == rotatedBitmap.width)
    assert(result.height == rotatedBitmap.height)
    for (y in 0 until rotatedBitmap.height) {
      for (x in 0 until rotatedBitmap.width) {
        assertEquals(rotatedBitmap.getPixel(0, 0), result.getPixel(0, 0))
      }
    }
  }

  @Test
  fun testScoobyGourmandRotateCorrectlyMoin90() {
    val pathNoRotation = "app/src/test/resources/images/scoobygourmand_normal.jpg"
    val pathMoin90Rotation = "app/src/test/resources/images/Scoobygourmand_Moin90.jpg"
    val noRotationBitmap = BitmapFactory.decodeFile(pathNoRotation)
    val rotatedBitmap = BitmapFactory.decodeFile(pathMoin90Rotation)
    assert(noRotationBitmap != null)
    assert(rotatedBitmap != null)
    assert(noRotationBitmap.width == rotatedBitmap.width)
    assert(noRotationBitmap.height == rotatedBitmap.height)

    val result = rotateBitmap(noRotationBitmap, -90)
    assert(result.width == rotatedBitmap.width)
    assert(result.height == rotatedBitmap.height)
    for (y in 0 until rotatedBitmap.height) {
      for (x in 0 until rotatedBitmap.width) {
        assertEquals(rotatedBitmap.getPixel(0, 0), result.getPixel(0, 0))
      }
    }
  }

  @Test
  fun testUriToBitmapWork() {
    val path = "src/test/resources/images/scoobygourmand_normal.jpg"
    val file = File(path)
    val uri = Uri.fromFile(file)

    // Create a reference Bitmap from the file path directly
    val referenceBitmap = BitmapFactory.decodeFile(file.path)
    assertNotNull("Reference Bitmap should not be null", referenceBitmap)

    // Call the uriToBitmap function to get the Bitmap from URI
    val resultBitmap = uriToBitmap(context, uri)
    assertNotNull("Result Bitmap should not be null", resultBitmap)

    // Compare the Bitmaps (use width and height as a quick check)
    assertTrue("Bitmaps should be of the same width", resultBitmap?.width == referenceBitmap.width)
    assertTrue(
        "Bitmaps should be of the same height", resultBitmap?.height == referenceBitmap.height)

    // Compare the Bitmaps pixel by pixel
    assertTrue("Bitmaps should be equal", bitmapsAreEqual(referenceBitmap, resultBitmap))
  }

  @Test
  fun testUriToBitmapErrorHandling() {
    // Create an invalid URI (non-existent file)
    val invalidUri = Uri.parse("file:///non_existent_path/non_existent_image.jpg")

    // Call uriToBitmap with the invalid URI
    val result = uriToBitmap(context, invalidUri)

    // Assert that the result is null due to error handling
    assertNull(result)
  }

  // Helper function to compare two bitmaps pixel by pixel
  private fun bitmapsAreEqual(bitmap1: Bitmap?, bitmap2: Bitmap?): Boolean {
    if (bitmap1 == null || bitmap2 == null) return false
    if (bitmap1.width != bitmap2.width || bitmap1.height != bitmap2.height) return false

    for (x in 0 until bitmap1.width) {
      for (y in 0 until bitmap1.height) {
        if (bitmap1.getPixel(x, y) != bitmap2.getPixel(x, y)) {
          return false
        }
      }
    }
    return true
  }
}
