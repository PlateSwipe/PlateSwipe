package com.android.sample.utils

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.sample.R
import com.android.sample.resources.C.TestTag.PlateSwipeDropdown.DROPDOWN
import com.android.sample.resources.C.TestTag.PlateSwipeDropdown.DROPDOWN_ITEM
import com.android.sample.resources.C.TestTag.PlateSwipeDropdown.DROPDOWN_TITLE
import com.android.sample.ui.utils.PlateSwipeDropdownMenu
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PlateSwipeDropdownMenuTest {
  @get:Rule val composeTestRule = createComposeRule()

  private val exampleList = listOf("item1", "item2", "item3")

  @Before fun setUp() {}

  @Test
  fun testDropdownAllComponentsAreDisplayed() {

    composeTestRule.setContent { PlateSwipeDropdownMenu(exampleList, onSelected = { _, _ -> }) }

    composeTestRule.onNodeWithTag(DROPDOWN).assertIsDisplayed().performClick()

    composeTestRule.waitForIdle()

    composeTestRule
        .onAllNodesWithTag(DROPDOWN_ITEM, useUnmergedTree = true)
        .assertCountEquals(exampleList.size)

    for (item in exampleList) {
      composeTestRule.onNodeWithText(item, useUnmergedTree = true).assertIsDisplayed()
    }
  }

  @Test
  fun testPlateSwipeSelectsCorrectly() {
    var wasSelected: String? = null
    var selectedIndex: Int? = null
    composeTestRule.setContent {
      PlateSwipeDropdownMenu(
          exampleList,
          onSelected = { string, index ->
            wasSelected = string
            selectedIndex = index
          })
    }

    composeTestRule.onNodeWithTag(DROPDOWN).assertIsDisplayed().performClick()

    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithText(exampleList[1], useUnmergedTree = true)
        .assertIsDisplayed()
        .performClick()

    composeTestRule.waitForIdle()

    assertEquals(exampleList[1], wasSelected)
    assertEquals(1, selectedIndex)
  }

  @Test
  fun testPlateSwipeCorrectlyUpdatesTitleAfterSelection() {
    var noneSelectedString = ""

    composeTestRule.setContent {
      PlateSwipeDropdownMenu(exampleList)
      noneSelectedString = stringResource(R.string.no_item_selected)
    }

    composeTestRule
        .onNodeWithTag(DROPDOWN_TITLE, useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextEquals(noneSelectedString)

    composeTestRule.onNodeWithTag(DROPDOWN).assertIsDisplayed().performClick()

    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithText(exampleList[0], useUnmergedTree = true)
        .assertIsDisplayed()
        .performClick()

    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag(DROPDOWN_TITLE, useUnmergedTree = true)
        .assertIsDisplayed()
        .assertTextEquals(exampleList[0])
  }

  @Test
  fun testThrowsExceptionIfDefaultItemIndexIsInvalid() {
    val invalidIndex = exampleList.size + 1
    assertThrows(IllegalArgumentException::class.java) {
      composeTestRule.setContent {
        PlateSwipeDropdownMenu(exampleList, defaultItemIndex = invalidIndex)
      }
    }
  }
}
