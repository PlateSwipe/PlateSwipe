package com.android.sample.utils

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.utils.SearchBar
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class SearchBarTest {
  private lateinit var mockNavigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    mockNavigationActions = mock(NavigationActions::class.java)

    `when`(mockNavigationActions.currentRoute()).thenReturn(Screen.ACCOUNT)
  }

  @Test
  fun testSearchBarDisplayed() {

    composeTestRule.setContent { SearchBar() }
    composeTestRule.onNodeWithTag("searchBar").assertIsDisplayed()
  }

  @Test
  fun testSearchBarCallsOnSearch() {
    val query = "test"
    var calledOnValueChange = false

    val onValueChange: (String) -> Unit = { searchQuery ->
      assertEquals(query, searchQuery)
      calledOnValueChange = true
    }

    composeTestRule.setContent { SearchBar(onValueChange = onValueChange) }
    composeTestRule.onNodeWithTag("searchBar").performTextInput(query)
    composeTestRule.waitForIdle()
    assert(calledOnValueChange)
  }
}
