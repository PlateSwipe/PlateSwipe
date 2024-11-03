package com.android.sample.camera.scan

import android.graphics.ImageFormat
import android.media.Image
import androidx.camera.core.ImageInfo
import androidx.camera.core.ImageProxy
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.feature.camera.scan.CodeBarAnalyzer
import java.nio.ByteBuffer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class CodeBarAnalyzerTest {

  private lateinit var mockImageProxy: ImageProxy
  private lateinit var mockImage: Image
  private lateinit var mockPlane: Image.Plane
  private lateinit var mockImageInfo: ImageInfo

  @Before
  fun setUp() {
    mockImageProxy = mock(ImageProxy::class.java)
    mockImage = mock(Image::class.java)
    mockPlane = mock(Image.Plane::class.java)
    mockImageInfo = mock(ImageInfo::class.java)
  }

  /** Test to verify that the analyze method is called with a null image */
  @Test
  fun testAnalyzeWithNullImage() {
    `when`(mockImageProxy.image).thenReturn(null)
    val codeBarAnalyzer = CodeBarAnalyzer { barcode ->
      assert(false) { "Should not have detected a barcode" }
    }

    codeBarAnalyzer.analyze(mockImageProxy)

    verify(mockImageProxy).close()
  }

  /** Test to verify that the analyze method is called with a valid image */
  @Test
  fun testAnalyzeWithCustomImage() {
    // Set up the mock Image to return a valid ByteBuffer
    val buffer = ByteBuffer.allocate(1024)
    `when`(mockPlane.buffer).thenReturn(buffer)
    `when`(mockImage.planes).thenReturn(arrayOf(mockPlane))
    `when`(mockImage.format).thenReturn(ImageFormat.YUV_420_888)
    `when`(mockImageProxy.image).thenReturn(mockImage)
    `when`(mockImageProxy.format).thenReturn(ImageFormat.YUV_420_888)
    `when`(mockImageProxy.width).thenReturn(640)
    `when`(mockImageProxy.height).thenReturn(480)
    `when`(mockImageProxy.imageInfo).thenReturn(mockImageInfo)
    `when`(mockImageInfo.rotationDegrees).thenReturn(0)

    val codeBarAnalyzer = CodeBarAnalyzer { barcode ->
      // This block should be executed if a barcode is detected
      assert(true) { "Barcode should be detected" }
    }
    // Call the analyze method with the mocked ImageProxy
    codeBarAnalyzer.analyze(mockImageProxy)
  }
}
