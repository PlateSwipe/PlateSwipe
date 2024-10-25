package com.android.sample.model.image

import java.io.FileInputStream

/**
 * @param name the name of the image
 * @param size the size of the image
 * @param contentType the type of content of the image (e.g. JPG)
 * @param stream the stream of bytes that will be used to upload the image
 */
data class Image(
    val name: String,
    val size: Int,
    val contentType: String,
    val stream: FileInputStream
)

/**
 * Class that describes what the image represents. It can be either a PROFILE image, a RECIPE image
 * or an INGREDIENT image
 */
enum class ImageType{
    PROFILE,
    RECIPE,
    INGREDIENT
}