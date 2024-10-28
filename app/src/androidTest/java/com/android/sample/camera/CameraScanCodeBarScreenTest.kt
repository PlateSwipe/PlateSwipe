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
import com.android.sample.ui.camera.BarCodeFrame
import com.android.sample.ui.camera.CameraScanCodeBarScreen
import com.android.sample.ui.camera.IngredientDisplay
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

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

    val ingredient = Ingredient(122333, "Test Ingredient")
    composeTestRule.setContent { IngredientDisplay(ingredient) }

    composeTestRule.onNodeWithText("Test Ingredient").assertIsDisplayed()
    composeTestRule.onNodeWithText("122333").assertIsDisplayed()
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
