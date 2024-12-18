package com.android.sample.fridge

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.image.ImageDownload
import com.android.sample.model.ingredient.IngredientRepository
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.TestTag.Fridge.GREEN
import com.android.sample.resources.C.TestTag.Fridge.ORANGE
import com.android.sample.resources.C.TestTag.Fridge.RED
import com.android.sample.ui.fridge.FridgeScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.utils.testIngredients
import java.time.LocalDate
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify

class FridgeScreenTest {

  private lateinit var navigationActions: NavigationActions

  private lateinit var userViewModel: UserViewModel
  private lateinit var ingredientViewModel: IngredientViewModel
  private lateinit var mockIngredientRepository: IngredientRepository
  private val userName: String = "John Doe"

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    mockIngredientRepository = mock(IngredientRepository::class.java)
    navigationActions = mock(NavigationActions::class.java)
    userViewModel =
        UserViewModel.provideFactory(ApplicationProvider.getApplicationContext())
            .create(UserViewModel::class.java)
    userViewModel.changeUserName(userName)
    ingredientViewModel = IngredientViewModel(mockIngredientRepository, ImageDownload())

    `when`(navigationActions.currentRoute()).thenReturn(Screen.FRIDGE)
    composeTestRule.setContent {
      FridgeScreen(navigationActions = navigationActions, userViewModel = userViewModel)
    }
  }

  @Test
  fun emptyFridgeIsDisplayed() {
    composeTestRule.onNodeWithText("Empty Fridge").assertIsDisplayed()
    composeTestRule
        .onNodeWithText("Your fridge is currently empty, click below to add ingredient")
        .assertIsDisplayed()
    composeTestRule.onNodeWithText("Add Ingredient").assertIsDisplayed()
  }

  @Test
  fun emptyFridgeCorrectlyNavigateWhenButtonPushed() {
    composeTestRule.onNodeWithText("Add Ingredient").assertIsDisplayed()
    composeTestRule.onNodeWithText("Add Ingredient").performClick()

    verify(navigationActions).navigateTo(Screen.FRIDGE_SEARCH_ITEM)
  }

  @Test
  fun fridgeIsDisplayed() {
    userViewModel.updateIngredientFromFridge(testIngredients[0], 1, LocalDate.of(2022, 2, 1), true)
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Fridge").assertIsDisplayed()
    composeTestRule.onNodeWithText("1 items").assertIsDisplayed()
    composeTestRule.onNodeWithText(testIngredients[0].name).performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("1 x ${testIngredients[0].quantity}").assertIsDisplayed()
    composeTestRule
        .onNodeWithContentDescription("Edit ${testIngredients[0].name} Quantity")
        .assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Recipe Image").assertIsDisplayed()
    composeTestRule.onNodeWithText("Expired").assertIsDisplayed()
    composeTestRule.onNodeWithTag("expirationBar${testIngredients[0].name}2022-02-01$RED")

    composeTestRule.onNodeWithText("Add Ingredient").assertIsDisplayed()
  }

  @Test
  fun fridgeAddTwoSimilarItemsDisplayed() {
    userViewModel.updateIngredientFromFridge(testIngredients[0], 1, LocalDate.of(2022, 2, 1), true)
    userViewModel.updateIngredientFromFridge(testIngredients[0], 1, LocalDate.of(2022, 2, 1), true)

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Fridge").assertIsDisplayed()
    composeTestRule.onNodeWithText("1 items").assertIsDisplayed()
    composeTestRule.onNodeWithText(testIngredients[0].name).performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("2 x ${testIngredients[0].quantity}").assertIsDisplayed()
    composeTestRule
        .onNodeWithContentDescription("Edit ${testIngredients[0].name} Quantity")
        .assertIsDisplayed()
    composeTestRule.onNodeWithContentDescription("Recipe Image").assertIsDisplayed()
    composeTestRule.onNodeWithText("Expired").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("expirationBar${testIngredients[0].name}2022-02-01$RED")
        .assertIsDisplayed()

    composeTestRule.onNodeWithText("Add Ingredient").assertIsDisplayed()
  }

  @Test
  fun fridgeAddTwoSimilarItemsWithDifferentDate() {
    userViewModel.updateIngredientFromFridge(testIngredients[0], 1, LocalDate.of(2022, 2, 1), true)
    userViewModel.updateIngredientFromFridge(testIngredients[0], 1, LocalDate.of(2022, 1, 1), true)

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Fridge").assertIsDisplayed()
    composeTestRule.onNodeWithText("2 items").assertIsDisplayed()
    composeTestRule.onAllNodesWithText(testIngredients[0].name).assertCountEquals(2)
    composeTestRule.onAllNodesWithText("1 x ${testIngredients[0].quantity}").assertCountEquals(2)
    composeTestRule
        .onAllNodesWithContentDescription("Edit ${testIngredients[0].name} Quantity")
        .assertCountEquals(2)
    composeTestRule.onAllNodesWithContentDescription("Recipe Image").assertCountEquals(2)
    composeTestRule.onAllNodesWithText("Expired").assertCountEquals(2)
    composeTestRule
        .onNodeWithTag("expirationBar${testIngredients[0].name}2022-02-01$RED")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("expirationBar${testIngredients[0].name}2022-01-01$RED")
        .assertIsDisplayed()

    composeTestRule.onNodeWithText("Add Ingredient").assertIsDisplayed()
  }

  @Test
  fun fridgeCorrectlyNavigateWhenButtonPushed() {
    userViewModel.updateIngredientFromFridge(testIngredients[0], 1, LocalDate.of(2022, 2, 1), true)
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Add Ingredient").assertIsDisplayed()
    composeTestRule.onNodeWithText("Add Ingredient").performClick()

    verify(navigationActions).navigateTo(Screen.FRIDGE_SEARCH_ITEM)
  }

  @Test
  fun fridgeCorrectlyDisplayedWhenTwoItemAreDisplayed() {
    userViewModel.updateIngredientFromFridge(testIngredients[0], 1, LocalDate.of(2022, 2, 1), true)
    userViewModel.updateIngredientFromFridge(testIngredients[1], 1, LocalDate.of(2022, 2, 1), true)
    composeTestRule.onNodeWithText("Fridge").assertIsDisplayed()
    composeTestRule.onNodeWithText("2 items").assertIsDisplayed()
    composeTestRule.onNodeWithText(testIngredients[0].name).performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("1 x ${testIngredients[0].quantity}").assertIsDisplayed()
    composeTestRule
        .onNodeWithContentDescription("Edit ${testIngredients[0].name} Quantity")
        .assertIsDisplayed()
    composeTestRule.onAllNodesWithContentDescription("Recipe Image").assertCountEquals(2)
    composeTestRule.onAllNodesWithText("Expired").assertCountEquals(2)
    composeTestRule
        .onNodeWithTag("expirationBar${testIngredients[0].name}2022-02-01$RED")
        .assertIsDisplayed()

    composeTestRule.onNodeWithText(testIngredients[1].name).performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithText("1 x ${testIngredients[1].quantity}").assertIsDisplayed()
    composeTestRule
        .onNodeWithContentDescription("Edit ${testIngredients[1].name} Quantity")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("expirationBar${testIngredients[1].name}2022-02-01$RED")
        .assertIsDisplayed()

    composeTestRule.onNodeWithText("Add Ingredient").assertIsDisplayed()
  }

  @Test
  fun testBarColorInUiOrangeMin() {
    val testExpirationDate = LocalDate.now().plusDays(1)

    userViewModel.updateIngredientFromFridge(testIngredients[0], 1, testExpirationDate, true)
    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag("expirationBar${testIngredients[0].name}${testExpirationDate}$ORANGE")
        .assertIsDisplayed()
    composeTestRule.onNodeWithText("1 day left").assertIsDisplayed()
  }

  @Test
  fun testBarColorInUiOrangeMax() {
    val testExpirationDate = LocalDate.now().plusDays(5)

    userViewModel.updateIngredientFromFridge(testIngredients[0], 1, testExpirationDate, true)
    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag("expirationBar${testIngredients[0].name}${testExpirationDate}$ORANGE")
        .assertIsDisplayed()
    composeTestRule.onNodeWithText("5 days left").assertIsDisplayed()
  }

  @Test
  fun testBarColorInUiGreenMin() {
    val testExpirationDate = LocalDate.now().plusDays(6)

    userViewModel.updateIngredientFromFridge(testIngredients[0], 1, testExpirationDate, true)
    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag("expirationBar${testIngredients[0].name}${testExpirationDate}$GREEN")
        .assertIsDisplayed()
    composeTestRule.onNodeWithText("6 days left").assertIsDisplayed()
  }

  @Test
  fun testBarColorInUiRedMin() {
    val testExpirationDate = LocalDate.now().plusDays(0)

    userViewModel.updateIngredientFromFridge(testIngredients[0], 1, testExpirationDate, true)
    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag("expirationBar${testIngredients[0].name}${testExpirationDate}$RED")
        .assertIsDisplayed()
    composeTestRule.onNodeWithText("Expired").assertIsDisplayed()
  }

  @Test
  fun quantityCorrectlyUpdates() {
    userViewModel.updateIngredientFromFridge(testIngredients[0], 2, LocalDate.of(2022, 2, 1), true)
    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithContentDescription("Edit ${testIngredients[0].name} Quantity")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithContentDescription("Edit ${testIngredients[0].name} Quantity")
        .performClick()

    assert(userViewModel.currentEditingFridgeIngredient.value != null)
    assert(userViewModel.ingredientList.value.size == 1)
    verify(navigationActions).navigateTo(Screen.FRIDGE_EDIT)
  }

  @Test
  fun quantityCorrectlyUpdatesWithMultipleIngredient() {
    userViewModel.updateIngredientFromFridge(testIngredients[0], 2, LocalDate.of(2022, 2, 1), true)
    userViewModel.updateIngredientFromFridge(testIngredients[1], 2, LocalDate.of(2022, 2, 1), true)
    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithContentDescription("Edit ${testIngredients[0].name} Quantity")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithContentDescription("Edit ${testIngredients[0].name} Quantity")
        .performClick()

    assert(userViewModel.currentEditingFridgeIngredient.value != null)
    assert(userViewModel.ingredientList.value.size == 1)
    assert(userViewModel.ingredientList.value[0].first == testIngredients[0])
    verify(navigationActions).navigateTo(Screen.FRIDGE_EDIT)

    composeTestRule
        .onNodeWithContentDescription("Edit ${testIngredients[1].name} Quantity")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithContentDescription("Edit ${testIngredients[1].name} Quantity")
        .performClick()

    assert(userViewModel.currentEditingFridgeIngredient.value != null)
    assert(userViewModel.ingredientList.value.size == 1)
    assert(userViewModel.ingredientList.value[0].first == testIngredients[1])
    verify(navigationActions, times(2)).navigateTo(Screen.FRIDGE_EDIT)
  }

  @Test
  fun removeIngredientFromFridge() {
    userViewModel.updateIngredientFromFridge(testIngredients[0], 1, LocalDate.of(2022, 2, 1), true)
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithContentDescription(
            "Remove ${testIngredients[0].name} that expired 2022-02-01 from fridge")
        .performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Remove ${testIngredients[0].name}").assertIsDisplayed()
    composeTestRule
        .onNodeWithText(
            "Are you sure you want to remove ${testIngredients[0].name} from your fridge?")
        .assertIsDisplayed()
    composeTestRule.onNodeWithText("No").assertIsDisplayed()
    composeTestRule.onNodeWithText("Yes").assertIsDisplayed().performClick()

    composeTestRule.waitForIdle()
    assert(userViewModel.fridgeItems.value.isEmpty())
  }

  @Test
  fun cancelRemoveIngredientFromFridge() {
    userViewModel.updateIngredientFromFridge(testIngredients[0], 1, LocalDate.of(2022, 2, 1), true)
    val originalSize = userViewModel.fridgeItems.value.size

    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithContentDescription(
            "Remove ${testIngredients[0].name} that expired 2022-02-01 from fridge")
        .performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Remove ${testIngredients[0].name}").assertIsDisplayed()
    composeTestRule.onNodeWithText("No").assertIsDisplayed().performClick()

    composeTestRule.waitForIdle()
    assert(userViewModel.fridgeItems.value.size == originalSize)
    assert(userViewModel.fridgeItems.value.first().second == testIngredients[0])
  }
}
