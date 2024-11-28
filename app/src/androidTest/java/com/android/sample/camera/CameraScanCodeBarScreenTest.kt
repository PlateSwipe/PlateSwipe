package com.android.sample.camera

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.IngredientRepository
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_NORMAL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_SMALL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL_URL
import com.android.sample.ui.camera.BarCodeFrame
import com.android.sample.ui.camera.CameraScanCodeBarScreen
import com.android.sample.ui.camera.IngredientOverlay
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

@RunWith(AndroidJUnit4::class)
class CameraScanCodeBarScreenTest {

  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockRepo: IngredientRepository
  private lateinit var mockIngredientViewModel: IngredientViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

  @Before
  fun setUp() {
    mockNavigationActions = mock(NavigationActions::class.java)
    mockRepo = mock(IngredientRepository::class.java)
    mockIngredientViewModel = IngredientViewModel(mockRepo)

    `when`(mockNavigationActions.currentRoute()).thenReturn(Screen.CAMERA_SCAN_CODE_BAR)
  }

  @Test
  fun ingredientDisplayTest() {

    val ingredient =
        Ingredient(
            barCode = 122333,
            name = "Test Ingredient",
            brands = "Test Brand",
            categories = listOf(""),
            images =
                mutableMapOf(
                    PRODUCT_FRONT_IMAGE_NORMAL_URL to "https://display_normal",
                    PRODUCT_FRONT_IMAGE_THUMBNAIL_URL to "https://display_thumbnail",
                    PRODUCT_FRONT_IMAGE_SMALL_URL to "https://display_small"))

    `when`(mockRepo.get(any(), any(), any())).thenAnswer { invocation ->
      composeTestRule.waitForIdle()
      val onSuccess = invocation.getArgument<(Ingredient) -> Unit>(1)
      onSuccess(ingredient)
      null
    }
    composeTestRule.setContent { IngredientOverlay(mockIngredientViewModel, mockNavigationActions) }

    mockIngredientViewModel.fetchIngredient(123L)

    composeTestRule.onNodeWithText("Test Ingredient", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithText("Test Brand", useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun barcodeFrameTest() {
    composeTestRule.setContent { BarCodeFrame() }
    composeTestRule.onNodeWithTag("Barcode frame").assertIsDisplayed()
  }

  @Test
  fun everythingIsDisplayed() {
    composeTestRule.setContent {
      CameraScanCodeBarScreen(mockNavigationActions, mockIngredientViewModel)
    }
    composeTestRule.onNodeWithTag("camera_preview").assertExists()
    composeTestRule.onNodeWithTag("camera_preview").assertIsDisplayed()
  }
}
