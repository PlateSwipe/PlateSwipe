package com.android.sample.createRecipe

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.model.recipe.FirestoreRecipesRepository
import com.android.sample.model.recipe.Recipe
import com.android.sample.ui.createRecipe.CategoryScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CategoryScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var createRecipeViewModel: CreateRecipeViewModel
  private lateinit var repoImg: ImageRepositoryFirebase

  @Before
  fun setUp() = runTest {
    mockNavigationActions = mockk(relaxed = true)

    val firestore = mockk<FirebaseFirestore>(relaxed = true)
    val repository = FirestoreRecipesRepository(firestore)
    repoImg = mockk(relaxed = true)
    createRecipeViewModel = spyk(CreateRecipeViewModel(repository, repoImg))
  }

  @Test
  fun testCategoryScreenComponentsAreDisplayed() {
    composeTestRule.setContent {
      CategoryScreen(
          navigationActions = mockNavigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    composeTestRule.onNodeWithText("Select A Category").assertExists().assertIsDisplayed()
    composeTestRule
        .onNodeWithText(
            "Selecting a category is optional, but it can help others find your recipe more easily.")
        .assertExists()
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag("DropdownMenuButton").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("NextStepButton").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("progressBar").assertExists().assertIsDisplayed()
  }

  @Test
  fun testDropdownMenuOpensAndDisplaysCategories() {
    composeTestRule.setContent {
      CategoryScreen(
          navigationActions = mockNavigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    // Verify dropdown button is displayed with placeholder text
    composeTestRule
        .onNodeWithTag("DropdownMenuButton")
        .assertExists()
        .assertIsDisplayed()
        .assertTextEquals("Choose a category")

    // Click dropdown button to open menu
    composeTestRule.onNodeWithTag("DropdownMenuButton").performClick()

    // Verify each category is displayed in the dropdown menu
    Recipe.getCategories().forEach { category ->
      composeTestRule.onNodeWithTag("DropdownMenuItem_$category").assertExists().assertIsDisplayed()
    }
  }

  @Test
  fun testSelectingCategoryUpdatesButtonText() {
    composeTestRule.setContent {
      CategoryScreen(
          navigationActions = mockNavigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    // Open dropdown menu and select a category
    composeTestRule.onNodeWithTag("DropdownMenuButton").performClick()
    composeTestRule.onNodeWithTag("DropdownMenuItem_Vegan").performClick()

    // Verify dropdown button text updates to the selected category
    composeTestRule.onNodeWithTag("DropdownMenuButton").assertExists().assertTextEquals("Vegan")
  }

  @Test
  fun testNextStepButtonNavigatesToNextScreen() {
    composeTestRule.setContent {
      CategoryScreen(
          navigationActions = mockNavigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    // Click the "Next Step" button
    composeTestRule.onNodeWithTag("NextStepButton").assertExists().performClick()

    // Verify navigation to the next screen
    verify { mockNavigationActions.navigateTo(Screen.CREATE_RECIPE_INGREDIENTS) }
  }
}
