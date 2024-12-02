package com.android.sample.fridge

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import com.android.sample.model.user.UserViewModel
import com.android.sample.ui.fridge.FridgeScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import java.time.format.DateTimeFormatter
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class FridgeScreenTest {

  private lateinit var navigationActions: NavigationActions

  private lateinit var userViewModel: UserViewModel
  private val userName: String = "John Doe"

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    userViewModel = UserViewModel.Factory.create(UserViewModel::class.java)
    userViewModel.changeUserName(userName)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.FRIDGE)
    composeTestRule.setContent {
      FridgeScreen(navigationActions = navigationActions, userViewModel = userViewModel)
    }
  }

  @Test
  fun mainTextIsDisplayed() {
    composeTestRule.onNodeWithText("Fridge").assertIsDisplayed()
    composeTestRule
        .onNodeWithText("${userViewModel.fridgeItems.value.size} items")
        .assertIsDisplayed()
  }

  @Test
  fun testFridgeDisplays() {
    userViewModel.fridgeItems.value.forEach { (fridgeItem, ingredient) ->
      // composeTestRule.onNodeWithText(ingredient.name).assertExists()
      // composeTestRule.onNodeWithContentDescription("Edit ${ingredient.name}
      // Quantity").assertIsDisplayed()
      composeTestRule
          .onAllNodesWithText(
              fridgeItem.expirationDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
          .assertCountEquals(
              userViewModel.fridgeItems.value
                  .filter { it.first.expirationDate == fridgeItem.expirationDate }
                  .size)
      // composeTestRule.onNodeWithContentDescription( "${ingredient.name} Image")
      // composeTestRule.onNodeWithText(fridgeItem.quantity).assertIsDisplayed()

    }
  }
}
