package com.android.sample.utils

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.ui.swipePage.RecipeImage
import com.android.sample.ui.utils.testRecipes
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipeImageTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testRecipeImageWithConnection() {

    val displayCard1 = true
    val currentRecipe = testRecipes[0]
    val nextRecipe = testRecipes[1]
    val isConnected = true
    composeTestRule.setContent {
      RecipeImage(displayCard1, currentRecipe, nextRecipe, isConnected, "recipeImage1")
    }

    composeTestRule.onNodeWithTag("recipeImage1").assertIsDisplayed()
  }

  @Test
  fun testRecipeImageWithOutConnection() {
    val displayCard1 = true
    val currentRecipe = testRecipes[0]
    val nextRecipe = testRecipes[1]
    val isConnected = false
    composeTestRule.setContent {
      RecipeImage(displayCard1, currentRecipe, nextRecipe, isConnected, "recipeImage1")
    }

    composeTestRule.onNodeWithTag("recipeImage1").assertIsDisplayed()
  }
}
