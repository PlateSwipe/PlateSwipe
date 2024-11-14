package com.android.sample.model.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.android.sample.resources.C.Tag.INGREDIENTS_IMAGE_DIR
import com.android.sample.resources.C.Tag.RECIPE_IMAGE_DIR
import com.android.sample.resources.C.Tag.TEST_IMAGE_DIR
import com.android.sample.resources.C.Tag.USER_IMAGE_DIR
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

class ImageRepositoryFirebase(storage: FirebaseStorage) : ImageRepository {
  private val storageRef = storage.reference

  override fun getImage(
      imageDirectoryUID: String,
      imageName: String,
      imageDirectoryType: ImageDirectoryType,
      onSuccess: (ImageBitmap) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val imageRef = imageRefCreation(imageDirectoryUID, imageName, imageDirectoryType)
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

  override fun getImageUrl(
      imageDirectoryUID: String,
      imageName: String,
      imageDirectoryType: ImageDirectoryType,
      onSuccess: (Uri) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val imageRef = imageRefCreation(imageDirectoryUID, imageName, imageDirectoryType)
    imageRef.downloadUrl
        .addOnSuccessListener { uri -> onSuccess(uri) }
        .addOnFailureListener {
          onFailure(
              Exception("Image download from Firebase storage has failed or image does not exist"))
        }
  }

  override fun uploadImageFromDevice(
      imageDirectoryUID: String,
      imageName: String,
      imageDirectoryType: ImageDirectoryType,
      path: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val file = Uri.fromFile(File(path))
    val imageRef = imageRefCreation(imageDirectoryUID, imageName, imageDirectoryType)
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
      imageDirectoryUID: String,
      imageName: String,
      imageDirectoryType: ImageDirectoryType,
      imageBitmap: ImageBitmap,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val imageRef = imageRefCreation(imageDirectoryUID, imageName, imageDirectoryType)
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
      imageDirectoryUID: String,
      imageName: String,
      imageDirectoryType: ImageDirectoryType,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    val imageRef = imageRefCreation(imageDirectoryUID, imageName, imageDirectoryType)
    imageRef.delete().addOnCompleteListener { task ->
      if (task.isSuccessful) {
        onSuccess()
      } else {
        onFailure(Exception("Deletion from the Firebase storage has failed"))
      }
    }
  }

  /**
   * Function that creates the Image Reference in the Firebase Storage using the
   * [ImageDirectoryType]
   *
   * @param imageDirectoryUID uid of the image that corresponds to the uid of either the user,
   *   ingredient or recipe
   * @param imageName name of the image that we will be displayed in the Storage
   * @param imageDirectoryType see [ImageDirectoryType] for more details
   * @return a string that represents the Image Reference in the Firebase Storage
   */
  private fun imageRefCreation(
      imageDirectoryUID: String,
      imageName: String,
      imageDirectoryType: ImageDirectoryType
  ): StorageReference {
    val dir =
        when (imageDirectoryType) {
          ImageDirectoryType.USER -> USER_IMAGE_DIR
          ImageDirectoryType.RECIPE -> RECIPE_IMAGE_DIR
          ImageDirectoryType.INGREDIENT -> INGREDIENTS_IMAGE_DIR
          ImageDirectoryType.TEST -> TEST_IMAGE_DIR
        }
    return storageRef.child(dir + imageDirectoryUID + "/$imageName" + ".jpg")
  }
}
