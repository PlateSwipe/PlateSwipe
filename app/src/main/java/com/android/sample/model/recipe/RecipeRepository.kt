package com.android.sample.model.recipe

/** Interface for retrieving recipe data. */
interface RecipesRepository {


    /**
     * Generates a new unique identifier for a recipe.
     *
     * @return A new unique identifier for a recipe.
     */
    fun getNewUid(): String

    /**
     * Adds a recipe to the repository.
     *
     * @param recipe The recipe to add.
     * @param onSuccess Callback that is called when the recipe is added.
     * @param onFailure Callback that is called when an error occurs.
     */
    fun addRecipe(recipe: Recipe, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

    /**
     * Updates a recipe in the repository.
     *
     * @param recipe The recipe to update.
     * @param onSuccess Callback that is called when the recipe is updated.
     * @param onFailure Callback that is called when an error occurs.
     */
    fun updateRecipe(recipe: Recipe, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

    /**
     * Deletes a recipe from the repository.
     *
     * @param idMeal The ID of the recipe to delete.
     * @param onSuccess Callback that is called when the recipe is deleted.
     * @param onFailure Callback that is called when an error occurs.
     */
    fun deleteRecipe(idMeal: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)


  /**
   * Fetches a specified number of random recipes.
   *
   * @param nbOfElements The number of random recipes to fetch.
   * @param onSuccess Callback that returns the list of fetched recipes.
   * @param onFailure Callback that is called when an error occurs.
   */
  fun random(nbOfElements: Int, onSuccess: (List<Recipe>) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Fetches a recipe by its ID.
   *
   * @param mealID The ID of the recipe to fetch.
   * @param onSuccess Callback that returns the fetched recipe.
   * @param onFailure Callback that is called when an error occurs.
   */
  fun search(mealID: String, onSuccess: (Recipe) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Fetches a list of recipes by category.
   *
   * @param category The category of the recipes to fetch.
   * @param onSuccess Callback that returns the list of fetched recipes.
   * @param onFailure Callback that is called when an error occurs.
   * @param limit The maximum number of recipes to fetch.
   */
  fun searchByCategory(
      category: String,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit,
      limit: Int = 5
  )

  /** Lists all the categories in the API. */
  fun listCategories(onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit)
}
