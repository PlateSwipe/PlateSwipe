package com.android.sample.model.image

/**
 * Interface for interacting with the Firebase storage
 */
interface ImageRepository {

    /**
     * Method that retrieves an image from the Firebase storage
     * @param name name of the image that we want to retrieve
     * @param imageType the type of image that we need to retrieve; see [ImageType] for more details
     * @param onSuccess method that will be called when the retrieval is successful
     * @param onFailure method that will be called when the retrieval has failed
     */
    fun getImage(name: String, imageType: ImageType, onSuccess: (Image) -> Unit, onFailure: (Exception) -> Unit)

    /**
     * Method that uploads an image to the Firebase storage
     * @param image the image that we want to upload to the storage
     * @param imageType the type of image that we need to retrieve; see [ImageType] for more details
     * @param onSuccess method that will be called when the retrieval is successful
     * @param onFailure method that will be called when the retrieval has failed
     */
    fun uploadImage(image: Image, imageType: ImageType, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

    /**
     * Method that deletes an image from the Firebase storage
     * @param name name of the image that we want to retrieve
     * @param imageType the type of image that we need to retrieve; see [ImageType] for more details
     * @param onSuccess method that will be called when the retrieval is successful
     * @param onFailure method that will be called when the retrieval has failed
     */
    fun deleteImage(name: String, imageType: ImageType, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}