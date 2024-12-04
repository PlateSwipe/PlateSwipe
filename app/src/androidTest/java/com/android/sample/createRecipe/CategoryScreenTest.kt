package com.android.sample.createRecipe

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.model.recipe.FirestoreRecipesRepository
import com.android.sample.model.recipe.Recipe
import com.android.sample.resources.C.TestTag.Category.CATEGORY_SUBTITLE
import com.android.sample.resources.C.TestTag.Category.CATEGORY_TITLE
import com.android.sample.ui.createRecipe.CategoryScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.every
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
          navigationActions = mockNavigationActions,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = false)
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
          navigationActions = mockNavigationActions,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = false)
    }

    // Verify dropdown button is displayed with placeholder text
    composeTestRule
        .onNodeWithTag("DropdownMenuButton")
        .assertExists()
        .assertIsDisplayed()
        .assertTextEquals("No category selected")

    // Click dropdown button to open menu
    composeTestRule.onNodeWithTag("DropdownMenuButton").performClick()

    // Verify that at least one category is displayed
    composeTestRule
        .onNodeWithTag("DropdownMenuItem_${Recipe.getCategories().first()}")
        .assertExists()
        .assertIsDisplayed()

    // Scroll through the list and verify all categories exist
    Recipe.getCategories().forEach { category ->
      composeTestRule
          .onNodeWithTag("DropdownMenuItem_$category", useUnmergedTree = true)
          .assertExists()
    }
  }

  @Test
  fun testSelectingCategoryUpdatesButtonText() {
    composeTestRule.setContent {
      CategoryScreen(
          navigationActions = mockNavigationActions,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = false)
    }

    // Open dropdown menu
    composeTestRule.onNodeWithTag("DropdownMenuButton").performClick()

    // Scroll to the desired category if necessary
    composeTestRule
        .onNodeWithTag("DropdownMenuItem_Vegan", useUnmergedTree = true)
        .performScrollTo()

    // Wait for the UI to update
    composeTestRule.waitForIdle()

    // Select the category
    composeTestRule.onNodeWithTag("DropdownMenuItem_Vegan").performClick()

    // Verify dropdown button text updates to the selected category
    composeTestRule.onNodeWithTag("DropdownMenuButton").assertExists().assertTextEquals("Vegan")
  }

  @Test
  fun testNextStepButtonNavigatesToNextScreen() {
    composeTestRule.setContent {
      CategoryScreen(
          navigationActions = mockNavigationActions,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = false)
    }

    // Click the "Next Step" button
    composeTestRule.onNodeWithTag("NextStepButton").assertExists().performClick()

    // Verify navigation to the next screen
    verify { mockNavigationActions.navigateTo(Screen.CREATE_RECIPE_INGREDIENTS) }
  }

  @Test
  fun testCategoryScreenComponentsInEditMode() {
    every { createRecipeViewModel.getRecipeCategory() } returns "Vegan"

    composeTestRule.setContent {
      CategoryScreen(
          navigationActions = mockNavigationActions,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = true)
    }

    // Assert title and subtitle for edit mode
    composeTestRule.onNodeWithTag(CATEGORY_TITLE).assertTextEquals("Edit Recipe Category")
    composeTestRule
        .onNodeWithTag(CATEGORY_SUBTITLE)
        .assertTextEquals(
            "You can update the category for your recipe to make it more discoverable.")

    // Assert the dropdown displays the pre-selected category
    composeTestRule.onNodeWithTag("DropdownMenuButton").assertTextEquals("Vegan")
  }

  @Test
  fun testNextStepButtonNavigatesToEditScreen() {
    composeTestRule.setContent {
      CategoryScreen(
          navigationActions = mockNavigationActions,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = true)
    }

    // Click the "Next Step" button
    composeTestRule.onNodeWithTag("NextStepButton").performClick()

    // Verify navigation to the edit ingredients screen
    verify { mockNavigationActions.navigateTo(Screen.EDIT_RECIPE_LIST_INGREDIENTS) }
  }
}
