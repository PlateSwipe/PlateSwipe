package com.android.sample.model.takePhoto

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** ViewModel to manage the state of a photo and its rotation. */
class TakePhotoViewModel : ViewModel() {
  // MutableStateFlow to hold the Bitmap photo
  private val _photo = MutableStateFlow<Bitmap?>(null)

  // MutableStateFlow to hold the Uri
  private val _uri = MutableStateFlow<Uri?>(null)

  /** StateFlow to expose the Uri as an immutable state. */
  val uri: StateFlow<Uri?>
    get() = _uri

  /** StateFlow to expose the photo as an immutable state. */
  val photo: StateFlow<Bitmap?>
    get() = _photo

  // MutableStateFlow to hold the rotation value
  private val _rotation = MutableStateFlow(0)

  /** StateFlow to expose the rotation value as an immutable state. */
  val rotation: StateFlow<Int>
    get() = _rotation

  /**
   * Sets the Bitmap photo.
   *
   * @param bitmap The Bitmap to set.
   */
  fun setBitmap(bitmap: Bitmap) {
    _photo.value = bitmap
  }

  /**
   * Sets the rotation value.
   *
   * @param rotation The rotation value to set.
   */
  fun setRotation(rotation: Int) {
    _rotation.value = rotation
  }

  /**
   * Sets the Uri.
   *
   * @param uri The Uri to set.
   */
  fun setUri(uri: Uri) {
    _uri.value = uri
  }

  /** Factory to create instances of TakePhotoViewModel. */
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TakePhotoViewModel() as T
          }
        }
  }
}
