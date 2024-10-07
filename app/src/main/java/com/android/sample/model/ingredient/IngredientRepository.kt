package com.android.sample.model.ingredient

interface IngredientRepository {

    /**
     * Get an ingredient by its barcode.
     * @param barCode The barcode of the ingredient. Can be any format of barcode (ex:EAN,UPC).
     * @param onSuccess The callback to be called when the ingredient is found.
     * @param onFailure The callback to be called when the ingredient is not found or an error occurs when retrieving it.
     */
    fun get(barCode: Long, onSuccess: (Ingredient) -> Unit, onFailure: (Exception) -> Unit)

    /**
     * Search for ingredients by name.
     * @param name The name of the ingredient to search for.
     * @param onSuccess The callback to be called when the ingredients are found.
     * @param onFailure The callback to be called when the ingredients are not found or an error occurs when retrieving them.
     * @param count The maximum number of ingredients to return.
     */
    fun search(
        name: String,
        onSuccess: (List<Ingredient>) -> Unit,
        onFailure: (Exception) -> Unit,
        count: Int = 20
    )
}
