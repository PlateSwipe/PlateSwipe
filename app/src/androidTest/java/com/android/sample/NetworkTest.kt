package com.android.sample

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.android.sample.model.image.ImageRepositoryFirebase
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@Retention(AnnotationRetention.RUNTIME) @Target(AnnotationTarget.FUNCTION) annotation class Network

@RunWith(AndroidJUnit4::class)
class NetworkTest {

  private var imageRepository: ImageRepositoryFirebase = ImageRepositoryFirebase(Firebase.storage)

  @Network
  @Test
  fun testUrlToBitmap() {
    if (!isNetworkAvailable()) return
    val url =
        "https://firebasestorage.googleapis.com/v0/b/plateswipe.appspot.com/o/images%2Ftest%2F4104420057326%2Fdisplay_small.jpg?alt=media&token=7fd91ca3-dbef-4c6e-a004-1e4ee5d0879d"
    val bitmap = imageRepository.urlToBitmap(url)
    assertNotNull(bitmap)
  }

  @Network
  @Test
  fun testUrlToBitmapFailure() {
    Log.d("NetworkTest", "testUrlToBitmapFailure")
    if (!isNetworkAvailable()) return
    Log.d("NetworkTest", "testUrlToBitmapFailure2")
    val url = "https://wrong"
    val bitmap = imageRepository.urlToBitmap(url)
    assertNull(bitmap)
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
