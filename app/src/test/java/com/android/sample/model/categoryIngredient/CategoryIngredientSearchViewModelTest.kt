package com.android.sample.model.categoryIngredient

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.ui.utils.testIngredients
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.mockito.kotlin.doNothing

@RunWith(AndroidJUnit4::class)
class CategoryIngredientSearchViewModelTest {
  @Mock
  private lateinit var mockLocalCategoryIngredientRepository: LocalCategoryIngredientRepository
  private lateinit var categoryIngredientSearchViewModel: CategoryIngredientSearchViewModel

  @Captor
  private lateinit var onSuccessCollectionCapture: ArgumentCaptor<Function1<List<String>, Unit>>
  @Captor private lateinit var onFailureCapture: ArgumentCaptor<Function1<Exception, Unit>>

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    doNothing()
        .`when`(mockLocalCategoryIngredientRepository)
        .searchCategory(
            any(), capture(onSuccessCollectionCapture), capture(onFailureCapture), any())

    categoryIngredientSearchViewModel =
        CategoryIngredientSearchViewModel(mockLocalCategoryIngredientRepository)
  }

  @Test
  fun testNotImplementedMethodsThrowException() {
    var expectedException: NotImplementedError? = null

    try {
      categoryIngredientSearchViewModel.fetchIngredient(0)
    } catch (e: NotImplementedError) {
      expectedException = e
    }

    assertNotNull(expectedException)

    // we need to reset this
    expectedException = null

    try {
      categoryIngredientSearchViewModel.clearIngredient()
    } catch (e: NotImplementedError) {
      expectedException = e
    }

    assertNotNull(expectedException)
  }

  @Test
  fun testSearchIsSuccess() = runTest {
    val query = "beef"

    categoryIngredientSearchViewModel.fetchIngredientByName(query)

    onSuccessCollectionCapture.value.invoke(listOf(query))

    categoryIngredientSearchViewModel.searchingIngredientList.value.let {
      assert(it.isNotEmpty())
      assertEquals(it[0].first.name, query)
    }
  }

  @Test
  fun testSearchIsFailure() = runTest {
    val query = "beef"

    categoryIngredientSearchViewModel.fetchIngredientByName(query)

    onFailureCapture.value.invoke(Exception("Error searching for category"))

    categoryIngredientSearchViewModel.searchingIngredientList.value.let { assert(it.isEmpty()) }
  }

  @Test
  fun testClearSearchingIngredientList() {
    val query = "beef"
    // add an ingredient to check if it is cleared
    categoryIngredientSearchViewModel.fetchIngredientByName(query)

    onSuccessCollectionCapture.value.invoke(listOf(query))

    categoryIngredientSearchViewModel.searchingIngredientList.value.let { assert(it.isNotEmpty()) }

    // clear the list
    categoryIngredientSearchViewModel.clearSearchingIngredientList()
    categoryIngredientSearchViewModel.searchingIngredientList.value.let { assert(it.isEmpty()) }
  }

  @Test
  fun testHandlingIngredientList() {
    val testIngredient = testIngredients[0]

    categoryIngredientSearchViewModel.ingredientList.value.let { assert(it.isEmpty()) }

    categoryIngredientSearchViewModel.addIngredient(testIngredient)

    categoryIngredientSearchViewModel.ingredientList.value.let { assert(it.isNotEmpty()) }

    categoryIngredientSearchViewModel.clearIngredientList()

    categoryIngredientSearchViewModel.ingredientList.value.let { assert(it.isEmpty()) }
  }
}
