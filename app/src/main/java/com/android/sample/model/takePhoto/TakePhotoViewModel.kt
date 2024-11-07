package com.android.sample.model.takePhoto

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.sample.resources.C
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** ViewModel to manage the state of a photo and its rotation. */
class TakePhotoViewModel : ViewModel() {
  // MutableStateFlow to hold the Bitmap photo
  private val _photo = MutableStateFlow<Bitmap?>(null)

  /** StateFlow to expose the photo as an immutable state. */
  val photo: StateFlow<Bitmap?>
    get() = _photo

  // MutableStateFlow to hold the rotation value
  private val _rotation = MutableStateFlow(C.ZERO)

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
