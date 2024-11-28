package com.android.sample.utils

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_NORMAL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_SMALL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL_URL
import com.android.sample.resources.C.TestTag.SwipePage.RECIPE_IMAGE_1
import com.android.sample.ui.utils.IngredientImageBox
import org.junit.Rule
import org.junit.Test

class ImageBoxTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testImageBox_displaysCorrectImage() {
    // Arrange: Mock the ingredient and its image URL
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

    // Act: Set the composable in the ComposeTestRule
    composeTestRule.setContent { IngredientImageBox(ingredient = ingredient) }

    // Assert: Verify the image and UI elements
    composeTestRule.onNodeWithTag(RECIPE_IMAGE_1).assertIsDisplayed()
  }
}
