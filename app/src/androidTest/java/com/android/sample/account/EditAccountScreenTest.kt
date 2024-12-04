package com.android.sample.account

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.user.User
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.TestTag.Utils.BACK_ARROW_ICON
import com.android.sample.resources.C.TestTag.Utils.BOTTOM_BAR
import com.android.sample.resources.C.TestTag.Utils.EDIT_ACCOUNT_ICON
import com.android.sample.resources.C.TestTag.Utils.TOP_BAR
import com.android.sample.ui.account.AccountScreen
import com.android.sample.ui.account.EditAccountScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.SampleAppTheme
import com.android.sample.ui.utils.testUsers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

import org.mockito.kotlin.verify

class EditAccountScreenTest {
    private lateinit var mockNavigationActions: NavigationActions
    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var mockFirebaseStorage: FirebaseStorage
    private lateinit var mockFirebaseUser: FirebaseUser
    private lateinit var mockFirebaseStorageReference: StorageReference

    private lateinit var userViewModel: UserViewModel
    private lateinit var imageRepositoryFirebase: ImageRepositoryFirebase

    @get:Rule
    val composeTestRule = createComposeRule()

    private var testUser: User = testUsers[0]

    @Before
    fun setUp(){
        mockNavigationActions = mock(NavigationActions::class.java)
        mockFirebaseStorage = mock(FirebaseStorage::class.java)
        mockFirebaseAuth = mock(FirebaseAuth::class.java)
        mockFirebaseUser = mock(FirebaseUser::class.java)
        mockFirebaseStorageReference = mock(StorageReference::class.java)

        `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        `when`(mockFirebaseUser.email).thenReturn("example@mail.ch")
        `when`(mockFirebaseUser.uid).thenReturn(testUser.uid)

        `when`(mockFirebaseStorage.reference).thenReturn(mockFirebaseStorageReference)

        `when`(mockNavigationActions.currentRoute()).thenReturn(Screen.ACCOUNT)

        userViewModel = UserViewModel.Factory.create(UserViewModel::class.java)
        imageRepositoryFirebase = ImageRepositoryFirebase(mockFirebaseStorage)

        userViewModel.changeUserName(testUser.userName)
        userViewModel.changeDateOfBirth(testUser.dateOfBirth)
        userViewModel.changeProfilePictureUrl(testUser.profilePictureUrl)
    }

    @Test
    fun editAccountIsAccessibleTest(){
        composeTestRule.setContent {
            SampleAppTheme { AccountScreen(mockNavigationActions, userViewModel) }
        }

        composeTestRule.onNodeWithTag(EDIT_ACCOUNT_ICON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(EDIT_ACCOUNT_ICON).performClick()

        verify(mockNavigationActions).navigateTo(Screen.EDIT_ACCOUNT)
    }

    @Test
    fun accountEditScreenIsDisplayedCorrectlyTest(){
        composeTestRule.setContent {
            SampleAppTheme {
                EditAccountScreen(
                    mockNavigationActions,
                    userViewModel,
                    mockFirebaseAuth,
                    imageRepositoryFirebase) }
        }

        composeTestRule.onNodeWithTag(TOP_BAR, useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag(BACK_ARROW_ICON, useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag(BOTTOM_BAR, useUnmergedTree = true).assertIsDisplayed()

        composeTestRule.onNodeWithTag("Profile Picture", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("Change profile picture button", useUnmergedTree = true).assertIsDisplayed()

        composeTestRule.onNodeWithTag("Username text field", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("Email text field", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("Date of birth text field", useUnmergedTree = true).assertIsDisplayed()

        composeTestRule.onNodeWithTag("Save Changes Button", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun returnToAccountScreenTest(){
        composeTestRule.setContent {
            SampleAppTheme {
                EditAccountScreen(
                    mockNavigationActions,
                    userViewModel,
                    mockFirebaseAuth,
                    imageRepositoryFirebase) }
        }

        composeTestRule.onNodeWithTag(BACK_ARROW_ICON, useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag(BACK_ARROW_ICON, useUnmergedTree = true).performClick()

        verify(mockNavigationActions).goBack()
    }

    @Test
    fun saveWithoutModifyingTest(){
        composeTestRule.setContent {
            SampleAppTheme {
                EditAccountScreen(
                    mockNavigationActions,
                    userViewModel,
                    mockFirebaseAuth,
                    imageRepositoryFirebase) }
        }

        composeTestRule.onNodeWithTag("Save Changes Button", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("Save Changes Button", useUnmergedTree = true).performClick()

        verify(mockNavigationActions).goBack()
        assert(userViewModel.userName.value == testUser.userName)
        assert(userViewModel.dateOfBirth.value == testUser.dateOfBirth)
        assert(userViewModel.profilePictureUrl.value == testUser.profilePictureUrl)
    }

    @Test
    fun modifyUsernameAndSaveTest(){
        composeTestRule.setContent {
            SampleAppTheme {
                EditAccountScreen(
                    mockNavigationActions,
                    userViewModel,
                    mockFirebaseAuth,
                    imageRepositoryFirebase) }
        }

        composeTestRule.onNodeWithTag("Username text field", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("Username text field", useUnmergedTree = true).performTextClearance()
        composeTestRule.onNodeWithTag("Username text field", useUnmergedTree = true).performTextInput("Trump")

        composeTestRule.onNodeWithTag("Save Changes Button", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("Save Changes Button", useUnmergedTree = true).performClick()

        verify(mockNavigationActions).goBack()
        assert(userViewModel.userName.value == "Trump")
        assert(userViewModel.dateOfBirth.value == testUser.dateOfBirth)
        assert(userViewModel.profilePictureUrl.value == testUser.profilePictureUrl)
    }

    @Test
    fun cannotModifyEmailTest(){
        composeTestRule.setContent {
            SampleAppTheme {
                EditAccountScreen(
                    mockNavigationActions,
                    userViewModel,
                    mockFirebaseAuth,
                    imageRepositoryFirebase) }
        }

        composeTestRule.onNodeWithTag("Email text field", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("Email text field", useUnmergedTree = true).performTextClearance()
        composeTestRule.onNodeWithTag("Email text field", useUnmergedTree = true).performTextInput("fail@gmail.com")
        composeTestRule.onNodeWithTag("Email text field", useUnmergedTree = true).assertTextEquals("example@mail.ch")
    }
}