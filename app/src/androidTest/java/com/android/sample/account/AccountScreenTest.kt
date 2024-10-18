package com.android.sample.account

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.android.sample.model.user.UserRepository
import com.android.sample.model.user.UserViewModel
import com.android.sample.ui.account.AccountScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class AccountScreenTest {

  private lateinit var mockUserRepository: UserRepository
  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var mockCurrentUser: FirebaseUser

  private lateinit var userViewModel: UserViewModel
  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Screen.ACCOUNT)
    navigationActions = mock(NavigationActions::class.java)
    mockUserRepository = mock(UserRepository::class.java)
    mockFirebaseAuth = mock(FirebaseAuth::class.java)
    userViewModel = UserViewModel(mockUserRepository, mockFirebaseAuth)
  }

  @Test
  fun emptyTest() {
    composeTestRule.setContent { AccountScreen(navigationActions, userViewModel) }
    composeTestRule.onNodeWithText("Fridge Screen").assertIsDisplayed()
  }
}
