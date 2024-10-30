package com.android.sample.model.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.android.sample.resources.C.Tag.DEFAULT_IMAGE_NAME
import com.android.sample.resources.C.Tag.INGREDIENTS_IMAGE_DIR
import com.android.sample.resources.C.Tag.PROFILE_IMAGE_DIR
import com.android.sample.resources.C.Tag.RECIPE_IMAGE_DIR
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Class that describes what the image represents. It can be either a PROFILE image, a RECIPE image
 * or an INGREDIENT image
 */
enum class ImageType {
  PROFILE,
  RECIPE,
  INGREDIENT
}

class ImageRepositoryFirebase(storage: FirebaseStorage) : ImageRepository {
  private val storageRef = storage.reference

  override fun getImage(
      imageUID: String,
      imageType: ImageType,
      onSuccess: (ImageBitmap) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val imageRef = imageRefCreation(imageUID, imageType)
    val localFile = File.createTempFile("image", ".jpg")

    imageRef.getFile(localFile).addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
        onSuccess(bitmap.asImageBitmap())
      } else {
        onFailure(
            Exception("Image download from Firebase storage has failed or image does not exist"))
      }
    }
  }

  override fun uploadImageFromDevice(
      imageUID: String,
      imageType: ImageType,
      path: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val file = Uri.fromFile(File(path))
    val imageRef = imageRefCreation(imageUID, imageType)
    val uploadTask = imageRef.putFile(file)
    uploadTask.addOnCompleteListener { task ->
      if (task.isSuccessful) {
        onSuccess()
      } else {
        onFailure(Exception("Image upload to Firebase storage has failed"))
      }
    }
  }

  override fun uploadImage(
      imageUID: String,
      imageType: ImageType,
      imageBitmap: ImageBitmap,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val imageRef = imageRefCreation(imageUID, imageType)
    val imageOutputStream = ByteArrayOutputStream()
    imageBitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.JPEG, 100, imageOutputStream)
    val uploadTask = imageRef.putStream(ByteArrayInputStream(imageOutputStream.toByteArray()))
    uploadTask.addOnCompleteListener { task ->
      if (task.isSuccessful) {
        onSuccess()
      } else {
        onFailure(Exception("Image upload to Firebase storage has failed"))
      }
    }
  }

  override fun deleteImage(
      imageUID: String,
      imageType: ImageType,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val imageRef = imageRefCreation(imageUID, imageType)
    imageRef.delete().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        onSuccess()
      } else {
        onFailure(Exception("Deletion from the Firebase storage has failed"))
      }
    }
  }

  /**
   * Function that creates the Image Reference in the Firebase Storage using the [ImageType]
   *
   * @param imageUID uid of the image that corresponds to the uid of either the user, ingredient or
   *   recipe
   * @param imageType the type of image that we need to retrieve; see [ImageType] for more details
   * @return a string that represents the Image Reference in the Firebase Storage
   */
  private fun imageRefCreation(imageUID: String, imageType: ImageType): StorageReference {
    val dir =
        when (imageType) {
          ImageType.PROFILE -> PROFILE_IMAGE_DIR
          ImageType.RECIPE -> RECIPE_IMAGE_DIR
          ImageType.INGREDIENT -> INGREDIENTS_IMAGE_DIR
        }
    return storageRef.child(
        dir +
            imageUID +
            if (dir == PROFILE_IMAGE_DIR) {
              ".jpg"
            } else {
              "/$DEFAULT_IMAGE_NAME"
            })
  }
}
