package com.android.sample.model.recipe

/** Interface for retrieving recipe data. */
interface RecipesRepository {

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
   */
  fun searchByCategory(
      category: String,
      onSuccess: (List<Recipe>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  /** Lists all the categories in the API. */
  fun listCategories(onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit)
}
