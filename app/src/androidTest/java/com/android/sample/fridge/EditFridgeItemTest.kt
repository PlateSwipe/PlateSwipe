package com.android.sample.fridge

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.fridge.FridgeItem
import com.android.sample.model.user.UserViewModel
import com.android.sample.ui.fridge.EditFridgeItemScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.utils.testIngredients
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify

class EditFridgeItemTest {
  private lateinit var navigationActions: NavigationActions

  private lateinit var userViewModel: UserViewModel
  private val userName: String = "John Doe"

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    userViewModel =
        UserViewModel.provideFactory(ApplicationProvider.getApplicationContext())
            .create(UserViewModel::class.java)
    userViewModel.changeUserName(userName)

    userViewModel.ingredientList
    `when`(navigationActions.currentRoute()).thenReturn(Screen.FRIDGE)
    userViewModel.addIngredient(testIngredients[0])
    composeTestRule.setContent {
      EditFridgeItemScreen(navigationActions = navigationActions, userViewModel = userViewModel)
    }
  }

  @Test
  fun assertEditComposableAreAllDisplayed() {
    composeTestRule
        .onNodeWithText(testIngredients[0].name, useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText("Quantity (x ${testIngredients[0].quantity}):", useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule.onNodeWithText("Expiration Date:", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule
        .onNodeWithText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        .assertIsDisplayed()
    composeTestRule.onNodeWithText("+", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithText("-", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithText("1", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithText("Add", useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun testIncreaseQuantity() {
    composeTestRule.onNodeWithText("+", useUnmergedTree = true).assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("2", useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun testDecreaseQuantity() {
    composeTestRule.onNodeWithText("+", useUnmergedTree = true).assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("-", useUnmergedTree = true).assertIsDisplayed().performClick()
    composeTestRule.onNodeWithText("1", useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun testExpirationDateSelection() {
    composeTestRule
        .onNodeWithText(
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            useUnmergedTree = true)
        .assertIsDisplayed()
        .performClick()
    composeTestRule.onNodeWithText("Confirm", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithText("Cancel", useUnmergedTree = true).assertIsDisplayed()
  }

  @Test
  fun testExpirationDateSelectAndConfirm() {
    composeTestRule
        .onNodeWithText(
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            useUnmergedTree = true)
        .assertIsDisplayed()
        .performClick()
    composeTestRule
        .onNodeWithText("Confirm", useUnmergedTree = true)
        .assertIsDisplayed()
        .performClick()
    composeTestRule
        .onNodeWithText(testIngredients[0].name, useUnmergedTree = true)
        .assertIsDisplayed()
  }

  @Test
  fun testExpirationDateSelectAndCancel() {
    composeTestRule
        .onNodeWithText(
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            useUnmergedTree = true)
        .assertIsDisplayed()
        .performClick()
    composeTestRule
        .onNodeWithText("Cancel", useUnmergedTree = true)
        .assertIsDisplayed()
        .performClick()
    composeTestRule
        .onNodeWithText(testIngredients[0].name, useUnmergedTree = true)
        .assertIsDisplayed()
  }

  @Test
  fun testAddButtonNavigation() {
    composeTestRule.onNodeWithText("Add", useUnmergedTree = true).assertIsDisplayed().performClick()
    assert(userViewModel.ingredientList.value.map { it.first }.contains(testIngredients[0]))
    verify(navigationActions).navigateTo(Screen.FRIDGE)
  }

  @Test
  fun editModeDisplay() {
    userViewModel.setEditingIngredient(
        Pair(FridgeItem("1", 1, LocalDate.of(2000, 1, 1)), testIngredients[0]))
    composeTestRule
        .onNodeWithText(testIngredients[0].name, useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText("Quantity (x ${testIngredients[0].quantity}):", useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule.onNodeWithText("Expiration Date:", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule
        .onNodeWithText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
        .assertIsDisplayed()
    composeTestRule.onNodeWithText("+", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithText("-", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithText("1", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithText("Save", useUnmergedTree = true).assertIsDisplayed()
  }
}
