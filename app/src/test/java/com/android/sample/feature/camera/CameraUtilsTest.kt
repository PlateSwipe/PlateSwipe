package com.android.sample.feature.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CameraUtilsTest {

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
}
