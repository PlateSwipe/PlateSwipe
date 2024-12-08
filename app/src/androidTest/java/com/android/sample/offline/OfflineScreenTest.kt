package com.android.sample.offline

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.resources.C.Tag.TEST_TAG_OFFLINE_SCREEN_DESCRIPTION
import com.android.sample.resources.C.Tag.TEST_TAG_OFFLINE_SCREEN_IMAGE
import com.android.sample.resources.C.Tag.TEST_TAG_OFFLINE_SCREEN_TITLE
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.offline.OfflineScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class OfflineScreenTest {

  private lateinit var mockNavigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    mockNavigationActions = mock(NavigationActions::class.java)
    `when`(mockNavigationActions.currentRoute()).thenReturn("Swipe Screen")
  }

  @Test
  fun testOfflineScreen() {
    composeTestRule.setContent { OfflineScreen(mockNavigationActions) }
    composeTestRule.onNodeWithTag(TEST_TAG_OFFLINE_SCREEN_TITLE).assertExists().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TEST_TAG_OFFLINE_SCREEN_DESCRIPTION)
        .assertExists()
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag(TEST_TAG_OFFLINE_SCREEN_IMAGE).assertExists().assertIsDisplayed()
  }
}
