package com.android.sample.model.ingredient

data class Ingredient(
    val barCode: Long? = null,
    val name: String? = null,
    val selectedImages: SelectedImages? = null,
    val brands: String? = null
)

data class SelectedImages(
    val front: ImageUrls? = null
)

data class ImageUrls(
    val display: String? = null,
    val small: String? = null,
    val thumb: String? = null
)
