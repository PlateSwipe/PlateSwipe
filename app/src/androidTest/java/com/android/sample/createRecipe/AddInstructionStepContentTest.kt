package com.android.sample.createRecipe

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.model.recipe.Instruction
import com.android.sample.model.recipe.networkData.FirestoreRecipesRepository
import com.android.sample.resources.C.Tag.SAVE_BUTTON_TAG
import com.android.sample.resources.C.TestTag.AddInstructionStepScreen.DELETE_BUTTON
import com.android.sample.resources.C.TestTag.AddInstructionStepScreen.ICON_DROPDOWN
import com.android.sample.resources.C.TestTag.AddInstructionStepScreen.INPUT_CONTAINER
import com.android.sample.resources.C.TestTag.AddInstructionStepScreen.INSTRUCTION_INPUT
import com.android.sample.resources.C.TestTag.AddInstructionStepScreen.TIME_INPUT
import com.android.sample.resources.C.TestTag.RecipeList.CANCEL_BUTTON
import com.android.sample.resources.C.TestTag.RecipeList.CONFIRMATION_BUTTON
import com.android.sample.resources.C.TestTag.RecipeList.CONFIRMATION_POP_UP
import com.android.sample.ui.createRecipe.AddInstructionStepScreen
import com.android.sample.ui.createRecipe.navigateAfterDelete
import com.android.sample.ui.createRecipe.navigateAfterValidation
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.*
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddInstructionStepScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var createRecipeViewModel: CreateRecipeViewModel
  private val firestore = mockk<FirebaseFirestore>(relaxed = true)
  private lateinit var repoImg: ImageRepositoryFirebase
  private val repository = mockk<FirestoreRecipesRepository>(relaxed = true)

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    repoImg = mockk(relaxed = true)
    navigationActions = mockk(relaxed = true)
    createRecipeViewModel = spyk(CreateRecipeViewModel(repository, repoImg))
  }

  /** Verifies that all UI elements are displayed on the AddInstructionStepScreen. */
  @Test
  fun addInstructionStepScreen_allFieldsDisplayed() {
    composeTestRule.setContent {
      AddInstructionStepScreen(
          navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
    }
    composeTestRule.onNodeWithTag("StepLabel").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TimeInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("IconDropdown").assertIsDisplayed()
    composeTestRule.onNodeWithTag("InstructionInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag(SAVE_BUTTON_TAG).assertIsDisplayed()
  }

  /** Verifies that entering data updates the respective fields on the AddInstructionStepScreen. */
  @Test
  fun addInstructionStepScreen_enteringDataUpdatesFields() {
    composeTestRule.setContent {
      AddInstructionStepScreen(
          navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
    }
    composeTestRule.onNodeWithTag("TimeInput").performTextInput("10")
    composeTestRule.onNodeWithTag("InstructionInput").performTextInput("Preheat oven to 180°C...")
    composeTestRule.onNodeWithText("10").assertIsDisplayed()
    composeTestRule.onNodeWithText("Preheat oven to 180°C...").assertIsDisplayed()
  }

  /** Verifies that selecting an icon updates the display in the IconDropdown. */
  @Test
  fun addInstructionStepScreen_iconSelectionUpdatesDisplay() {
    composeTestRule.setContent {
      AddInstructionStepScreen(
          navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
    }
    composeTestRule.onNodeWithTag("IconDropdown").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Cook").performClick()
    composeTestRule.onNodeWithContentDescription("Cook").assertIsDisplayed()
  }

  /**
   * Verifies that clicking the save button triggers data saving and navigation to the next screen.
   */
  @Test
  fun addInstructionStepScreen_saveButtonSavesDataAndNavigates() = runTest {
    composeTestRule.setContent {
      AddInstructionStepScreen(
          navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
    }
    composeTestRule.onNodeWithTag("TimeInput").performTextInput("10")
    composeTestRule.onNodeWithTag("InstructionInput").performTextInput("Preheat oven to 180°C...")
    composeTestRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()
    advanceUntilIdle()
    verify {
      navigationActions.navigateToPop(
          Screen.CREATE_RECIPE_LIST_INSTRUCTIONS, Screen.CREATE_RECIPE_LIST_INGREDIENTS)
    }
  }

  /** Verifies that an error message is displayed when attempting to save without instructions. */
  @Test
  fun addInstructionStepScreen_saveButtonShowsErrorIfInstructionEmpty() {
    composeTestRule.setContent {
      AddInstructionStepScreen(
          navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
    }
    composeTestRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("InstructionError").assertIsDisplayed()
  }

  /** Verifies that `time` and `category` are only updated when they are not empty. */
  @Test
  fun addInstructionStepScreen_onlyUpdatesTimeWhenNotEmpty() = runTest {
    composeTestRule.setContent {
      AddInstructionStepScreen(
          navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    // Enter only the instruction text and leave time and category empty
    composeTestRule.onNodeWithTag("InstructionInput").performTextInput("Preheat oven to 180°C...")
    composeTestRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()
    advanceUntilIdle()

    // Verify time and category were not updated
    verify(exactly = 0) { createRecipeViewModel.updateRecipeTime(any()) }
    verify(exactly = 0) { createRecipeViewModel.updateRecipeCategory(any()) }

    // Now enter values for time and category and save again
    composeTestRule.onNodeWithTag("TimeInput").performTextInput("10")
    composeTestRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()
    advanceUntilIdle()

    // Verify time and category were updated with the new values
    verify { createRecipeViewModel.addRecipeInstruction(any()) }
  }

  @Test
  fun addInstructionDisplaysAlertBoxWhenTryingToDeleteTest() {
    createRecipeViewModel.addRecipeInstruction(
        Instruction("10", "Preheat oven to 180°C...", "Cook"))
    createRecipeViewModel.selectInstruction(0)
    composeTestRule.setContent {
      AddInstructionStepScreen(
          navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
    }
    composeTestRule.onNodeWithTag(INPUT_CONTAINER).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TIME_INPUT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(ICON_DROPDOWN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(SAVE_BUTTON_TAG).assertIsDisplayed()
    composeTestRule.onNodeWithTag(INSTRUCTION_INPUT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(DELETE_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(DELETE_BUTTON).performClick()

    composeTestRule.onNodeWithTag(CONFIRMATION_POP_UP).assertIsDisplayed()

    composeTestRule.onNodeWithTag(CONFIRMATION_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CANCEL_BUTTON).assertIsDisplayed()

    composeTestRule.onNodeWithText("Are you sure you want to delete this step?").assertIsDisplayed()
    composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
  }

  /**
   * Verifies that an error message is displayed when attempting to save with a blank instruction.
   */
  @Test
  fun addInstructionStepScreen_saveButtonShowsErrorIfInstructionBlank() {
    composeTestRule.setContent {
      AddInstructionStepScreen(
          navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    // Input a blank instruction
    composeTestRule.onNodeWithTag("InstructionInput").performTextInput("   ")
    composeTestRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()
    composeTestRule.waitForIdle()

    // Verify that the error message is displayed
    composeTestRule.onNodeWithTag("InstructionError").assertIsDisplayed()
  }

  @Test
  fun addInstructionStepScreen_allFieldsDisplayed_InEditMode() {
    composeTestRule.setContent {
      AddInstructionStepScreen(
          navigationActions = navigationActions,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = true)
    }
    composeTestRule.onNodeWithTag("StepLabel").assertIsDisplayed()
    composeTestRule.onNodeWithTag("TimeInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag("IconDropdown").assertIsDisplayed()
    composeTestRule.onNodeWithTag("InstructionInput").assertIsDisplayed()
    composeTestRule.onNodeWithTag(SAVE_BUTTON_TAG).assertIsDisplayed()
  }

  /** Verifies that entering data updates the respective fields on the AddInstructionStepScreen. */
  @Test
  fun addInstructionStepScreen_enteringDataUpdatesFields_InEditMode() {
    composeTestRule.setContent {
      AddInstructionStepScreen(
          navigationActions = navigationActions,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = true)
    }
    composeTestRule.onNodeWithTag("TimeInput").performTextInput("10")
    composeTestRule.onNodeWithTag("InstructionInput").performTextInput("Preheat oven to 180°C...")
    composeTestRule.onNodeWithText("10").assertIsDisplayed()
    composeTestRule.onNodeWithText("Preheat oven to 180°C...").assertIsDisplayed()
  }

  /** Verifies that selecting an icon updates the display in the IconDropdown. */
  @Test
  fun addInstructionStepScreen_iconSelectionUpdatesDisplay_InEditMode() {
    composeTestRule.setContent {
      AddInstructionStepScreen(
          navigationActions = navigationActions,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = true)
    }
    composeTestRule.onNodeWithTag("IconDropdown").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Cook").performClick()
    composeTestRule.onNodeWithContentDescription("Cook").assertIsDisplayed()
  }

  /**
   * Verifies that clicking the save button triggers data saving and navigation to the next screen.
   */
  @Test
  fun addInstructionStepScreen_saveButtonSavesDataAndNavigates_InEditMode() = runTest {
    composeTestRule.setContent {
      AddInstructionStepScreen(
          navigationActions = navigationActions,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = true)
    }
    composeTestRule.onNodeWithTag("TimeInput").performTextInput("10")
    composeTestRule.onNodeWithTag("InstructionInput").performTextInput("Preheat oven to 180°C...")
    composeTestRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()
    advanceUntilIdle()
    verify {
      navigationActions.navigateToPop(
          Screen.EDIT_RECIPE_LIST_INSTRUCTIONS, Screen.EDIT_CATEGORY_SCREEN)
    }
  }

  /** Verifies that an error message is displayed when attempting to save without instructions. */
  @Test
  fun addInstructionStepScreen_saveButtonShowsErrorIfInstructionEmpty_InEditMode() {
    composeTestRule.setContent {
      AddInstructionStepScreen(
          navigationActions = navigationActions,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = true)
    }
    composeTestRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("InstructionError").assertIsDisplayed()
  }

  /** Verifies that `time` and `category` are only updated when they are not empty. */
  @Test
  fun addInstructionStepScreen_onlyUpdatesTimeWhenNotEmpty_InEditMode() = runTest {
    composeTestRule.setContent {
      AddInstructionStepScreen(
          navigationActions = navigationActions,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = true)
    }

    // Enter only the instruction text and leave time and category empty
    composeTestRule.onNodeWithTag("InstructionInput").performTextInput("Preheat oven to 180°C...")
    composeTestRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()
    advanceUntilIdle()

    // Verify time and category were not updated
    verify(exactly = 0) { createRecipeViewModel.updateRecipeTime(any()) }
    verify(exactly = 0) { createRecipeViewModel.updateRecipeCategory(any()) }

    // Now enter values for time and category and save again
    composeTestRule.onNodeWithTag("TimeInput").performTextInput("10")
    composeTestRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()
    advanceUntilIdle()

    // Verify time and category were updated with the new values
    verify { createRecipeViewModel.addRecipeInstruction(any()) }
  }

  @Test
  fun addInstructionDisplaysAlertBoxWhenTryingToDeleteTest_InEditMode() {
    createRecipeViewModel.addRecipeInstruction(
        Instruction("10", "Preheat oven to 180°C...", "Cook"))
    createRecipeViewModel.selectInstruction(0)
    composeTestRule.setContent {
      AddInstructionStepScreen(
          navigationActions = navigationActions,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = true)
    }
    composeTestRule.onNodeWithTag(INPUT_CONTAINER).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TIME_INPUT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(ICON_DROPDOWN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(SAVE_BUTTON_TAG).assertIsDisplayed()
    composeTestRule.onNodeWithTag(INSTRUCTION_INPUT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(DELETE_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(DELETE_BUTTON).performClick()

    composeTestRule.onNodeWithTag(CONFIRMATION_POP_UP).assertIsDisplayed()

    composeTestRule.onNodeWithTag(CONFIRMATION_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CANCEL_BUTTON).assertIsDisplayed()

    composeTestRule.onNodeWithText("Are you sure you want to delete this step?").assertIsDisplayed()
    composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
  }

  /**
   * Verifies that an error message is displayed when attempting to save with a blank instruction.
   */
  @Test
  fun addInstructionStepScreen_saveButtonShowsErrorIfInstructionBlank_InEditMode() {
    composeTestRule.setContent {
      AddInstructionStepScreen(
          navigationActions = navigationActions,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = true)
    }

    // Input a blank instruction
    composeTestRule.onNodeWithTag("InstructionInput").performTextInput("   ")
    composeTestRule.onNodeWithTag(SAVE_BUTTON_TAG).performClick()
    composeTestRule.waitForIdle()

    // Verify that the error message is displayed
    composeTestRule.onNodeWithTag("InstructionError").assertIsDisplayed()
  }

  /**
   * Verifies that navigateAfterDelete calls navigateToPop with the correct parameters based on
   * isEditing.
   */
  @Test
  fun navigateAfterDelete_callsNavigateToPopCorrectly() {
    val targetScreenEditing = Screen.EDIT_RECIPE_LIST_INSTRUCTIONS
    val popUpToScreenEditing = Screen.EDIT_CATEGORY_SCREEN
    val targetScreenCreating = Screen.CREATE_RECIPE_LIST_INSTRUCTIONS
    val popUpToScreenCreating = Screen.CREATE_RECIPE_LIST_INGREDIENTS

    // Test when isEditing is true
    navigateAfterDelete(true, navigationActions)
    verify {
      navigationActions.navigateToPop(
          targetScreenEditing, popUpTo = popUpToScreenEditing, inclusive = false)
    }

    // Test when isEditing is false
    navigateAfterDelete(false, navigationActions)
    verify {
      navigationActions.navigateToPop(
          targetScreenCreating, popUpTo = popUpToScreenCreating, inclusive = false)
    }
  }

  /**
   * Verifies that navigateAfterValidation calls navigateToPop with the correct parameters based on
   * isEditing.
   */
  @Test
  fun navigateAfterValidation_callsNavigateToPopCorrectly() {
    val targetScreenEditing = Screen.EDIT_RECIPE_LIST_INSTRUCTIONS
    val popUpToScreenEditing = Screen.EDIT_CATEGORY_SCREEN
    val targetScreenCreating = Screen.CREATE_RECIPE_LIST_INSTRUCTIONS
    val popUpToScreenCreating = Screen.CREATE_RECIPE_LIST_INGREDIENTS

    // Test when isEditing is true
    navigateAfterValidation(true, navigationActions)
    verify {
      navigationActions.navigateToPop(
          targetScreenEditing, popUpTo = popUpToScreenEditing, inclusive = false)
    }

    // Test when isEditing is false
    navigateAfterValidation(false, navigationActions)
    verify {
      navigationActions.navigateToPop(
          targetScreenCreating, popUpTo = popUpToScreenCreating, inclusive = false)
    }
  }
}
