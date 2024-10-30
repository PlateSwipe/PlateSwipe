package com.android.sample.model.image

import androidx.compose.ui.graphics.ImageBitmap

/** Interface for interacting with the Firebase storage */
interface ImageRepository {

  /**
   * Method that retrieves an image from the Firebase storage
   *
   * @param imageUID uid of the image that corresponds to the uid of either the user, ingredient or
   *   recipe
   * @param imageType the type of image that we need to retrieve; see [ImageType] for more details
   * @param onSuccess method that will be called when the retrieval is successful
   * @param onFailure method that will be called when the retrieval has failed
   */
  fun getImage(
      imageUID: String,
      imageType: ImageType,
      onSuccess: (ImageBitmap) -> Unit,
      onFailure: (Exception) -> Unit
  )

  /**
   * Method that uploads an image to the Firebase storage from the device
   *
   * @param imageUID uid of the image that corresponds to the uid of either the user, ingredient or
   *   recipe
   * @param imageType the type of image that we need to retrieve; see [ImageType] for more details
   * @param path the path of where the image is located on the device
   * @param onSuccess method that will be called when the retrieval is successful
   * @param onFailure method that will be called when the retrieval has failed
   */
  fun uploadImageFromDevice(
      imageUID: String,
      imageType: ImageType,
      path: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  /**
   * Method that uploads an image to the Firebase storage
   *
   * @param imageUID uid of the image that corresponds to the uid of either the user, ingredient or
   *   recipe
   * @param imageType the type of image that we need to retrieve; see [ImageType] for more details
   * @param imageBitmap the image that we want to upload in the format [ImageBitmap]
   * @param onSuccess method that will be called when the retrieval is successful
   * @param onFailure method that will be called when the retrieval has failed
   */
  fun uploadImage(
      imageUID: String,
      imageType: ImageType,
      imageBitmap: ImageBitmap,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  /**
   * Method that deletes an image from the Firebase storage
   *
   * @param imageUID uid of the image that corresponds to the uid of either the user, ingredient or
   *   recipe
   * @param imageType the type of image that we need to retrieve; see [ImageType] for more details
   * @param onSuccess method that will be called when the retrieval is successful
   * @param onFailure method that will be called when the retrieval has failed
   */
  fun deleteImage(
      imageUID: String,
      imageType: ImageType,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )
}
