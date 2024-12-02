package com.android.sample.createRecipe

import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.model.recipe.FirestoreRecipesRepository
import com.android.sample.resources.C.TestTag.TimePicker.HOURS_LABEL
import com.android.sample.resources.C.TestTag.TimePicker.HOUR_PICKER
import com.android.sample.resources.C.TestTag.TimePicker.MINUTES_LABEL
import com.android.sample.resources.C.TestTag.TimePicker.MINUTE_PICKER
import com.android.sample.resources.C.TestTag.TimePicker.NEXT_BUTTON
import com.android.sample.resources.C.TestTag.TimePicker.TIME_PICKER_DESCRIPTION
import com.android.sample.resources.C.TestTag.TimePicker.TIME_PICKER_TITLE
import com.android.sample.ui.createRecipe.TimePickerScreen
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

class TimePickerScreenTest {

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
  fun testTimePickerScreenComponentsAreDisplayed() {
    composeTestRule.setContent {
      TimePickerScreen(
          navigationActions = mockNavigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    // Verify title is displayed
    composeTestRule.onNodeWithTag(TIME_PICKER_TITLE).assertExists().assertIsDisplayed()

    // Verify description is displayed
    composeTestRule.onNodeWithTag(TIME_PICKER_DESCRIPTION).assertExists().assertIsDisplayed()

    // Verify "Hours" label is displayed
    composeTestRule.onNodeWithTag(HOURS_LABEL).assertExists().assertIsDisplayed()

    // Verify "Minutes" label is displayed
    composeTestRule.onNodeWithTag(MINUTES_LABEL).assertExists().assertIsDisplayed()

    // Verify hour and minute pickers are displayed
    composeTestRule.onNodeWithTag(HOUR_PICKER).assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag(MINUTE_PICKER).assertExists().assertIsDisplayed()

    // Verify Next button is displayed
    composeTestRule.onNodeWithTag(NEXT_BUTTON).assertExists().assertIsDisplayed()
  }

  @Test
  fun testNextStepButtonNavigatesToNextScreen() {
    composeTestRule.setContent {
      TimePickerScreen(
          navigationActions = mockNavigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    // Click the "Next Step" button
    composeTestRule.onNodeWithTag(NEXT_BUTTON).assertExists().performClick()

    // Verify navigation to the next screen
    verify { mockNavigationActions.navigateTo(Screen.CREATE_RECIPE_ADD_IMAGE) }
  }

  @Test
  fun testTimePickerUpdatesRecipeTimeInViewModel() {
    composeTestRule.setContent {
      TimePickerScreen(
          navigationActions = mockNavigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    // Simulate selecting hours and minutes
    composeTestRule.onNodeWithTag(HOUR_PICKER).performClick()
    composeTestRule.onNodeWithTag(MINUTE_PICKER).performClick()

    // Click "Next Step" to trigger time update
    composeTestRule.onNodeWithTag(NEXT_BUTTON).assertExists().performClick()

    // Verify that the recipe time in the ViewModel was updated
    verify {
      createRecipeViewModel.updateRecipeTime(
          match { time ->
            (time.toIntOrNull() ?: 0) >= 0 // Ensure it's set to a valid integer time
          })
    }
  }

  @Test
  fun testWheelTimePickerDisplaysInitialValues() {
    // Mock the initial recipe time (e.g., 125 minutes)
    every { createRecipeViewModel.getRecipeTime() } returns "125"

    composeTestRule.setContent {
      TimePickerScreen(
          navigationActions = mockNavigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    // Verify the hour picker displays the correct initial hour (2)
    composeTestRule
        .onNodeWithTag(HOUR_PICKER)
        .assertExists()
        .assertContentDescriptionEquals("2") // Use content description instead of text

    // Verify the minute picker displays the correct initial minute (5)
    composeTestRule
        .onNodeWithTag(MINUTE_PICKER)
        .assertExists()
        .assertContentDescriptionEquals("5") // Use content description instead of text
  }

  @Test
  fun testInvalidStateHandling() {
    every { createRecipeViewModel.getRecipeTime() } returns null // Simulate missing time

    composeTestRule.setContent {
      TimePickerScreen(
          navigationActions = mockNavigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    // Verify that the hour picker starts at 0
    composeTestRule.onNodeWithTag(HOUR_PICKER).assertExists().assertContentDescriptionEquals("0")

    // Verify that the minute picker starts at 0
    composeTestRule.onNodeWithTag(MINUTE_PICKER).assertExists().assertContentDescriptionEquals("0")
  }

  @Test
  fun testGetRecipeTimeCalledOnce() {
    // Mock the initial recipe time
    every { createRecipeViewModel.getRecipeTime() } returns "125"

    composeTestRule.setContent {
      TimePickerScreen(
          navigationActions = mockNavigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    // Verify that getRecipeTime was called exactly once
    verify(exactly = 1) { createRecipeViewModel.getRecipeTime() }
  }

  @Test
  fun testPickerStateUpdatesIndependently() {
    every { createRecipeViewModel.getRecipeTime() } returns "125"

    composeTestRule.setContent {
      TimePickerScreen(
          navigationActions = mockNavigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    // Simulate interacting with the hour picker
    composeTestRule.onNodeWithTag(HOUR_PICKER).performClick()
    composeTestRule
        .onNodeWithTag(HOUR_PICKER)
        .assertContentDescriptionEquals("2") // Initial value: 2

    // Simulate changing the hour
    composeTestRule.onNodeWithTag(HOUR_PICKER).performClick()
    // Ensure minute picker value remains unchanged
    composeTestRule
        .onNodeWithTag(MINUTE_PICKER)
        .assertContentDescriptionEquals("5") // Initial value: 5
  }
}
