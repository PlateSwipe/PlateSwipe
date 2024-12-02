package com.android.sample.model.recipe

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.recipe.localData.RecipeLocalRepository
import com.android.sample.model.recipe.networkData.RecipeNetworkRepository
import com.android.sample.ui.utils.testRecipes
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class DefaultRecipeRepositoryTest {

  @Mock private lateinit var mockLocalRepository: RecipeLocalRepository
  @Mock private lateinit var mockNetworkRepository: RecipeNetworkRepository
  private lateinit var defaultRecipeRepository: DefaultRecipeRepository
  private lateinit var recipe: Recipe
  private lateinit var onFailure: (Exception) -> Unit
  private lateinit var onSuccess: () -> Unit
  private lateinit var onSuccess2: (List<Recipe>) -> Unit
  private lateinit var onSuccess3: (Recipe) -> Unit
  private lateinit var onSuccess4: (List<String>) -> Unit

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    defaultRecipeRepository = DefaultRecipeRepository(mockLocalRepository, mockNetworkRepository)
    recipe = testRecipes[0]
    onFailure = { "Failure" }
    onSuccess = { "Success" }
    onSuccess2 = { "Success" }
    onSuccess3 = { "Success" }
    onSuccess4 = { "Success" }
  }

  @Test
  fun getNewUidCallNetworkRepository() {
    defaultRecipeRepository.getNewUid()
    verify(mockNetworkRepository).getNewUid()
  }

  @Test
  fun addRecipeCallNetworkRepository() {
    defaultRecipeRepository.addRecipe(recipe, onSuccess, onFailure)
    verify(mockNetworkRepository).addRecipe(recipe, onSuccess, onFailure)
  }

  @Test
  fun updateRecipeCallNetworkRepository() {
    defaultRecipeRepository.updateRecipe(recipe, onSuccess, onFailure)
    verify(mockNetworkRepository).updateRecipe(recipe, onSuccess, onFailure)
  }

  @Test
  fun deleteRecipeCallNetworkRepository() {
    defaultRecipeRepository.deleteRecipe(recipe.uid, onSuccess, onFailure)
    verify(mockNetworkRepository).deleteRecipe(recipe.uid, onSuccess, onFailure)
  }

  @Test
  fun randomCallNetworkRepository() {
    defaultRecipeRepository.random(10, onSuccess2, onFailure)
    verify(mockNetworkRepository).random(10, onSuccess2, onFailure)
  }

  @Test
  fun searchCallNetworkRepository() {
    defaultRecipeRepository.search(recipe.name, onSuccess3, onFailure)
    verify(mockNetworkRepository).search(recipe.name, onSuccess3, onFailure)
  }

  @Test
  fun searchByCategoryCallNetworkRepository() {
    defaultRecipeRepository.searchByCategory(recipe.name, onSuccess2, onFailure, 10)
    verify(mockNetworkRepository).searchByCategory(recipe.name, onSuccess2, onFailure, 10)
  }

  @Test
  fun listCategoriesCallNetworkRepository() {
    defaultRecipeRepository.listCategories(onSuccess4, onFailure)
    verify(mockNetworkRepository).listCategories(onSuccess4, onFailure)
  }

  @Test
  fun addDownloadCallLocalRepository() {
    defaultRecipeRepository.addDownload(recipe)
    verify(mockLocalRepository).add(recipe)
  }

  @Test
  fun updateDownloadCallLocalRepository() {
    defaultRecipeRepository.updateDownload(recipe)
    verify(mockLocalRepository).update(recipe)
  }

  @Test
  fun deleteDownloadCallLocalRepository() {
    defaultRecipeRepository.deleteDownload(recipe)
    verify(mockLocalRepository).delete(recipe)
  }

  @Test
  fun getAllDownloadCallLocalRepository() {
    defaultRecipeRepository.getAllDownload(onSuccess2, onFailure)
    verify(mockLocalRepository).getAll(onSuccess2, onFailure)
  }

  @Test
  fun deleteAllDownloadsCallLocalRepository() {
    defaultRecipeRepository.deleteAllDownloads(onSuccess, onFailure)
    verify(mockLocalRepository).deleteAll(onSuccess, onFailure)
  }
}
