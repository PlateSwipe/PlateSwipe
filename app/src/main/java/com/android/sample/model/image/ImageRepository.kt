package com.android.sample.model.image

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Class that describes by what the image is used. It can be either a PROFILE image, a RECIPE image
 * or an INGREDIENT image
 */
enum class ImageDirectoryType {
  USER,
  RECIPE,
  INGREDIENT
}

/** Interface for interacting with the Firebase storage */
interface ImageRepository {

  /**
   * Method that retrieves an image from the Firebase storage
   *
   * @param imageDirectoryUID uid of the image directory that corresponds to the uid of either the
   *   user, ingredient or recipe
   * @param imageName name of the image that we will be displayed in the Storage
   * @param imageDirectoryType see [ImageDirectoryType] for more details
   * @param onSuccess method that will be called when the retrieval is successful
   * @param onFailure method that will be called when the retrieval has failed
   */
  fun getImage(
      imageDirectoryUID: String,
      imageName: String,
      imageDirectoryType: ImageDirectoryType,
      onSuccess: (ImageBitmap) -> Unit,
      onFailure: (Exception) -> Unit
  )

  /**
   * Method that uploads an image to the Firebase storage from the device
   *
   * @param imageDirectoryUID uid of the image directory that corresponds to the uid of either the
   *   user, ingredient or recipe
   * @param imageName name of the image that we will be displayed in the Storage
   * @param imageDirectoryType see [ImageDirectoryType] for more details
   * @param path the path of where the image is located on the device
   * @param onSuccess method that will be called when the retrieval is successful
   * @param onFailure method that will be called when the retrieval has failed
   */
  fun uploadImageFromDevice(
      imageDirectoryUID: String,
      imageName: String,
      imageDirectoryType: ImageDirectoryType,
      path: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  /**
   * Method that uploads an image to the Firebase storage
   *
   * @param imageDirectoryUID uid of the image directory that corresponds to the uid of either the
   *   user, ingredient or recipe
   * @param imageName name of the image that we will be displayed in the Storage
   * @param imageDirectoryType see [ImageDirectoryType] for more details
   * @param imageBitmap the image that we want to upload in the format [ImageBitmap]
   * @param onSuccess method that will be called when the retrieval is successful
   * @param onFailure method that will be called when the retrieval has failed
   */
  fun uploadImage(
      imageDirectoryUID: String,
      imageName: String,
      imageDirectoryType: ImageDirectoryType,
      imageBitmap: ImageBitmap,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  /**
   * Method that deletes an image from the Firebase storage
   *
   * @param imageDirectoryUID uid of the image directory that corresponds to the uid of either the
   *   user, ingredient or recipe
   * @param imageName name of the image that we will be displayed in the Storage
   * @param imageDirectoryType see [ImageDirectoryType] for more details
   * @param onSuccess method that will be called when the retrieval is successful
   * @param onFailure method that will be called when the retrieval has failed
   */
  fun deleteImage(
      imageDirectoryUID: String,
      imageName: String,
      imageDirectoryType: ImageDirectoryType,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )
}
