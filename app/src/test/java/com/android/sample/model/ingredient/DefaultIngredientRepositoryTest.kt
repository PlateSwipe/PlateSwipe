package com.android.sample.model.ingredient

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.ingredient.localData.IngredientLocalRepository
import com.android.sample.model.ingredient.networkData.IngredientNetworkRepository
import com.android.sample.ui.utils.testIngredients
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class DefaultIngredientRepositoryTest {

  @Mock private lateinit var mockLocalRepository: IngredientLocalRepository
  @Mock private lateinit var mockNetworkRepository: IngredientNetworkRepository
  private lateinit var defaultIngredientRepository: DefaultIngredientRepository
  private lateinit var ingredient: Ingredient
  private lateinit var onSuccess: (Ingredient?) -> Unit
  private lateinit var onSuccess2: (List<Ingredient>) -> Unit
  private lateinit var onFailure: (Exception) -> Unit

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    defaultIngredientRepository =
        DefaultIngredientRepository(mockLocalRepository, mockNetworkRepository)
    ingredient = testIngredients[0]
    onSuccess = { "Success" }
    onSuccess2 = { "Success" }
    onFailure = { "Failure" }
  }

  @Test
  fun getCallNetworkRepository() {
    defaultIngredientRepository.get(ingredient.barCode!!, onSuccess, onFailure)
    verify(mockNetworkRepository).get(ingredient.barCode!!, onSuccess, onFailure)
  }

  @Test
  fun searchCallNetworkRepository() {
    defaultIngredientRepository.search(ingredient.name, onSuccess2, onFailure, 10)
    verify(mockNetworkRepository).search(ingredient.name, onSuccess2, onFailure, 10)
  }

  @Test
  fun addDownloadCallLocalRepository() {
    defaultIngredientRepository.addDownload(ingredient)
    verify(mockLocalRepository).add(ingredient)
  }

  @Test
  fun updateDownloadCallLocalRepository() {
    defaultIngredientRepository.updateDownload(ingredient)
    verify(mockLocalRepository).update(ingredient)
  }

  @Test
  fun deleteDownloadCallLocalRepository() {
    defaultIngredientRepository.deleteDownload(ingredient)
    verify(mockLocalRepository).delete(ingredient)
  }

  @Test
  fun getAllDownloadCallLocalRepository() {
    defaultIngredientRepository.getAllDownload(onSuccess2, onFailure)
    verify(mockLocalRepository).getAll(onSuccess2, onFailure)
  }

  @Test
  fun getByBarcodeCallLocalRepository() {
    defaultIngredientRepository.getByBarcode(ingredient.barCode!!, onSuccess, onFailure)
    verify(mockLocalRepository).getByBarcode(ingredient.barCode!!, onSuccess, onFailure)
  }
}
