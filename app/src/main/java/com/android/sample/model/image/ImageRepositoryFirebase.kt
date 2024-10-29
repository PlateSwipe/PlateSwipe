package com.android.sample.model.image

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileInputStream

class ImageRepositoryFirebase (storage: FirebaseStorage) : ImageRepository{
    private val storageRef = storage.reference
    private val profileImageDir = "images/profile/"
    private val recipeImageDir = "images/recipe/"
    private val ingredientImageDir = "images/ingredient/"
    private val defaultImageName = "default.jpg"

    override fun getImage(
        imageUID: String,
        imageType: ImageType,
        onSuccess: (ImageBitmap) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val imageRef = imageRefCreation(imageUID, imageType)
        val localFile = File.createTempFile("image", ".jpg")

        imageRef.getFile(localFile).addOnCompleteListener{ task ->
            if(task.isSuccessful){
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                onSuccess(bitmap.asImageBitmap())
            } else {
                onFailure(Exception("Image download from Firebase storage has failed or image does not exist"))
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
            if (task.isSuccessful){
                onSuccess()
            } else {
                onFailure(Exception("Image upload to Firebase storage has failed"))
            }
        }
    }

    override fun uploadImage(
        imageUID: String,
        imageType: ImageType,
        imageFileStream: FileInputStream,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
       val imageRef = imageRefCreation(imageUID, imageType)
        val uploadTask = imageRef.putStream(imageFileStream)
        uploadTask.addOnCompleteListener { task ->
            if(task.isSuccessful){
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
            if(task.isSuccessful) {
                onSuccess()
            } else {
                onFailure(Exception("Deletion from the Firebase storage has failed"))
            }
        }
    }

    /**
     * Function that creates the Image Reference in the Firebase Storage using the [ImageType]
     *
     * @param imageUID uid of the image that corresponds to the uid of either the user, ingredient or recipe
     * @param imageType the type of image that we need to retrieve; see [ImageType] for more details
     * @return a string that represents the Image Reference in the Firebase Storage
     */
    private fun imageRefCreation(imageUID: String, imageType: ImageType): StorageReference{
        val dir = when(imageType){
            ImageType.PROFILE -> profileImageDir
            ImageType.RECIPE -> recipeImageDir
            ImageType.INGREDIENT -> ingredientImageDir
        }
        return storageRef.child(
            dir + imageUID +
                    if (dir != recipeImageDir) {
                        ".jpg"
                    } else {
                        "/$defaultImageName"
                    }
        )
    }
}
