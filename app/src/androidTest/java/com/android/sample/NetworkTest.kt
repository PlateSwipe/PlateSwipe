package com.android.sample

import android.content.Context
import android.net.ConnectivityManager
import androidx.test.platform.app.InstrumentationRegistry
import com.android.sample.model.image.ImageDownload
import com.android.sample.model.image.ImageRepositoryFirebase
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.FrameworkMethod

@RunWith(NetworkAwareTestRunner::class)
class NetworkTest {

  private var imageRepository: ImageRepositoryFirebase = ImageRepositoryFirebase(Firebase.storage)
  private var imageDownload: ImageDownload = ImageDownload()

  @Network
  @Test
  fun testUrlToBitmap() {
    val url =
        "https://firebasestorage.googleapis.com/v0/b/plateswipe.appspot.com/o/images%2Ftest%2F4104420057326%2Fdisplay_small.jpg?alt=media&token=7fd91ca3-dbef-4c6e-a004-1e4ee5d0879d"
    val bitmap = imageRepository.urlToBitmap(url)
    assertNotNull(bitmap)
  }

  @Network
  @Test
  fun testUrlToBitmapFailure() {
    val url = "https://wrong"
    val bitmap = imageRepository.urlToBitmap(url)
    assertNull(bitmap)
  }

  @Network
  @Test
  fun testDownloadAndSaveImage() =
      runTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val imageUrl =
            "https://firebasestorage.googleapis.com/v0/b/plateswipe.appspot.com/o/images%2Ftest%2F4104420057326%2Fdisplay_small.jpg?alt=media&token=7fd91ca3-dbef-4c6e-a004-1e4ee5d0879d"
        val fileName = "test"
        val uri = imageDownload.downloadAndSaveImage(context, imageUrl, fileName, Dispatchers.IO)
        assertNotNull(uri)
      }

  @Network
  @Test
  fun testDownloadAndSaveImageFailure() =
      runTest() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val imageUrl = "https://wrong"
        val fileName = "test"
        val uri =
            imageDownload.downloadAndSaveImage(
                context, imageUrl, fileName, dispatcher = Dispatchers.IO)
        assertNull(uri)
      }
}

@Retention(AnnotationRetention.RUNTIME) @Target(AnnotationTarget.FUNCTION) annotation class Network

class NetworkAwareTestRunner(testClass: Class<*>) : BlockJUnit4ClassRunner(testClass) {

  override fun runChild(
      method: FrameworkMethod,
      notifier: org.junit.runner.notification.RunNotifier
  ) {

    // Check if the method has the @NetworkTest annotation
    if (method.getAnnotation(Network::class.java) != null) {
      if (!isNetworkAvailable()) {
        println("Skipping test: Network is not available")
        notifier.fireTestIgnored(describeChild(method))
        return
      }
    }
    // Run the test if network is available or if it's not a network test
    super.runChild(method, notifier)
  }

  /**
   * Checks if the network is available.
   *
   * @return True if the network is available and connected, false otherwise.
   * @see <a
   *   href="https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android">Detect
   *   whether there is an Internet connection available on Android</a>
   */
  private fun isNetworkAvailable(): Boolean {
    val connectivityManager =
        InstrumentationRegistry.getInstrumentation()
            .targetContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
  }
}
