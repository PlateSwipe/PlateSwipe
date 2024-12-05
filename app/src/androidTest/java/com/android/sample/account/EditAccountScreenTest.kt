package com.android.sample.account

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.user.User
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.Tag.EditAccountScreen.DATE_OF_BIRTH_FIELD_DESCRIPTION
import com.android.sample.resources.C.TestTag.EditAccountScreen.CHANGE_PROFILE_PICTURE_BUTTON_TAG
import com.android.sample.resources.C.TestTag.EditAccountScreen.DATE_OF_BIRTH_CHANGE_BUTTON_TAG
import com.android.sample.resources.C.TestTag.EditAccountScreen.DATE_OF_BIRTH_TEXT_FIELD_TAG
import com.android.sample.resources.C.TestTag.EditAccountScreen.DATE_PICKER_POP_UP_CANCEL_TAG
import com.android.sample.resources.C.TestTag.EditAccountScreen.DATE_PICKER_POP_UP_CONFIRM_TAG
import com.android.sample.resources.C.TestTag.EditAccountScreen.DATE_PICKER_POP_UP_TAG
import com.android.sample.resources.C.TestTag.EditAccountScreen.PROFILE_PICTURE_TAG
import com.android.sample.resources.C.TestTag.EditAccountScreen.SAVE_CHANGES_BUTTON_TAG
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class EditAccountScreenTest {
  @Captor
  private lateinit var onCompleteFileDownloadTaskListenerCaptor:
      ArgumentCaptor<OnCompleteListener<FileDownloadTask.TaskSnapshot>>

  @Mock private lateinit var mockFirebaseStorage: FirebaseStorage
  @Mock private lateinit var mockStorageRef: StorageReference
  @Mock private lateinit var mockImageRef: StorageReference
  @Mock private lateinit var mockDownload: FileDownloadTask

  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var mockFirebaseUser: FirebaseUser
  private lateinit var mockFirebaseStorageReference: StorageReference

  private lateinit var userViewModel: UserViewModel
  private lateinit var imageRepositoryFirebase: ImageRepositoryFirebase

  @get:Rule val composeTestRule = createComposeRule()

  private var testUser: User = testUsers[0]

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    mockNavigationActions = mock(NavigationActions::class.java)
    mockFirebaseAuth = mock(FirebaseAuth::class.java)
    mockFirebaseUser = mock(FirebaseUser::class.java)
    mockFirebaseStorageReference = mock(StorageReference::class.java)

    `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
    `when`(mockFirebaseUser.email).thenReturn("example@mail.ch")
    `when`(mockFirebaseUser.uid).thenReturn(testUser.uid)

    `when`(mockFirebaseStorage.reference).thenReturn(mockStorageRef)
    `when`(mockStorageRef.child(any())).thenReturn(mockImageRef)

    `when`(mockNavigationActions.currentRoute()).thenReturn(Screen.ACCOUNT)

    userViewModel =
        UserViewModel.provideFactory(ApplicationProvider.getApplicationContext())
            .create(UserViewModel::class.java)
    imageRepositoryFirebase = ImageRepositoryFirebase(mockFirebaseStorage)

    userViewModel.changeUserName(testUser.userName)
    userViewModel.changeDateOfBirth(testUser.dateOfBirth)
    userViewModel.changeProfilePictureUrl(testUser.profilePictureUrl)
  }

  @Test
  fun editAccountIsAccessibleTest() {
    composeTestRule.setContent {
      SampleAppTheme { AccountScreen(mockNavigationActions, userViewModel) }
    }

    composeTestRule.onNodeWithTag(EDIT_ACCOUNT_ICON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(EDIT_ACCOUNT_ICON).performClick()

    verify(mockNavigationActions).navigateTo(Screen.EDIT_ACCOUNT)
  }

  @Test
  fun accountEditScreenIsDisplayedCorrectlyTest() {
    composeTestRule.setContent {
      SampleAppTheme {
        EditAccountScreen(
            mockNavigationActions, userViewModel, mockFirebaseAuth, imageRepositoryFirebase)
      }
    }

    composeTestRule.onNodeWithTag(TOP_BAR, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(BACK_ARROW_ICON, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(BOTTOM_BAR, useUnmergedTree = true).assertIsDisplayed()

    composeTestRule.onNodeWithTag(PROFILE_PICTURE_TAG, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(CHANGE_PROFILE_PICTURE_BUTTON_TAG, useUnmergedTree = true)
        .assertIsDisplayed()

    composeTestRule.onNodeWithTag("Username text field", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("Email text field", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(DATE_OF_BIRTH_CHANGE_BUTTON_TAG, useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithContentDescription(DATE_OF_BIRTH_FIELD_DESCRIPTION, useUnmergedTree = true)
        .assertIsDisplayed()

    composeTestRule
        .onNodeWithTag(SAVE_CHANGES_BUTTON_TAG, useUnmergedTree = true)
        .assertIsDisplayed()
  }

  @Test
  fun returnToAccountScreenTest() {
    composeTestRule.setContent {
      SampleAppTheme {
        EditAccountScreen(
            mockNavigationActions, userViewModel, mockFirebaseAuth, imageRepositoryFirebase)
      }
    }

    composeTestRule.onNodeWithTag(BACK_ARROW_ICON, useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag(BACK_ARROW_ICON, useUnmergedTree = true).performClick()

    verify(mockNavigationActions).goBack()
  }

  @Test
  fun saveWithoutModifyingTest() {
    composeTestRule.setContent {
      SampleAppTheme {
        EditAccountScreen(
            mockNavigationActions, userViewModel, mockFirebaseAuth, imageRepositoryFirebase)
      }
    }

    composeTestRule
        .onNodeWithTag(SAVE_CHANGES_BUTTON_TAG, useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag(SAVE_CHANGES_BUTTON_TAG, useUnmergedTree = true).performClick()

    verify(mockNavigationActions).goBack()
    assert(userViewModel.userName.value == testUser.userName)
    assert(userViewModel.dateOfBirth.value == testUser.dateOfBirth)
    assert(userViewModel.profilePictureUrl.value == testUser.profilePictureUrl)
  }

  @Test
  fun modifyUsernameAndSaveTest() {
    composeTestRule.setContent {
      SampleAppTheme {
        EditAccountScreen(
            mockNavigationActions, userViewModel, mockFirebaseAuth, imageRepositoryFirebase)
      }
    }

    composeTestRule.onNodeWithTag("Username text field", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("Username text field", useUnmergedTree = true)
        .performTextClearance()
    composeTestRule
        .onNodeWithTag("Username text field", useUnmergedTree = true)
        .performTextInput("Trump")

    composeTestRule
        .onNodeWithTag(SAVE_CHANGES_BUTTON_TAG, useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag(SAVE_CHANGES_BUTTON_TAG, useUnmergedTree = true).performClick()

    verify(mockNavigationActions).goBack()
    assert(userViewModel.userName.value == "Trump")
    assert(userViewModel.dateOfBirth.value == testUser.dateOfBirth)
    assert(userViewModel.profilePictureUrl.value == testUser.profilePictureUrl)
  }

  @Test
  fun cannotModifyEmailTest() {
    composeTestRule.setContent {
      SampleAppTheme {
        EditAccountScreen(
            mockNavigationActions, userViewModel, mockFirebaseAuth, imageRepositoryFirebase)
      }
    }

    composeTestRule.onNodeWithTag("Email text field", useUnmergedTree = true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("Email text field", useUnmergedTree = true).performTextClearance()
    composeTestRule
        .onNodeWithTag("Email text field", useUnmergedTree = true)
        .performTextInput("fail@gmail.com")
    composeTestRule
        .onNodeWithTag("Email text field", useUnmergedTree = true)
        .assertTextEquals("example@mail.ch")
  }

  @Test
  fun datePickerPopUpIsDisplayedTest() {
    composeTestRule.setContent {
      SampleAppTheme {
        EditAccountScreen(
            mockNavigationActions, userViewModel, mockFirebaseAuth, imageRepositoryFirebase)
      }
    }

    composeTestRule
        .onNodeWithTag(DATE_OF_BIRTH_CHANGE_BUTTON_TAG, useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(DATE_OF_BIRTH_CHANGE_BUTTON_TAG, useUnmergedTree = true)
        .performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag(DATE_PICKER_POP_UP_TAG, useUnmergedTree = true)
        .assertIsDisplayed()
  }

  @Test
  fun datePickerPopUpCancelTest() {
    userViewModel.changeDateOfBirth("12/12/2024")
    composeTestRule.setContent {
      SampleAppTheme {
        EditAccountScreen(
            mockNavigationActions, userViewModel, mockFirebaseAuth, imageRepositoryFirebase)
      }
    }

    composeTestRule
        .onNodeWithTag(DATE_OF_BIRTH_CHANGE_BUTTON_TAG, useUnmergedTree = true)
        .performClick()
    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag(DATE_PICKER_POP_UP_CANCEL_TAG, useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(DATE_PICKER_POP_UP_CANCEL_TAG, useUnmergedTree = true)
        .performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag(DATE_PICKER_POP_UP_TAG, useUnmergedTree = true)
        .assertIsNotDisplayed()
    composeTestRule
        .onNodeWithTag(DATE_OF_BIRTH_TEXT_FIELD_TAG, useUnmergedTree = true)
        .assertTextEquals("12/12/2024")
  }

  @Test
  fun datePickerPopUpConfirmNoDateSelectedTest() {
    userViewModel.changeDateOfBirth("12/12/2024")
    composeTestRule.setContent {
      SampleAppTheme {
        EditAccountScreen(
            mockNavigationActions, userViewModel, mockFirebaseAuth, imageRepositoryFirebase)
      }
    }

    composeTestRule
        .onNodeWithTag(DATE_OF_BIRTH_CHANGE_BUTTON_TAG, useUnmergedTree = true)
        .performClick()
    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag(DATE_PICKER_POP_UP_CONFIRM_TAG, useUnmergedTree = true)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(DATE_PICKER_POP_UP_CONFIRM_TAG, useUnmergedTree = true)
        .performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag(DATE_PICKER_POP_UP_TAG, useUnmergedTree = true)
        .assertIsNotDisplayed()
    composeTestRule
        .onNodeWithTag(DATE_OF_BIRTH_TEXT_FIELD_TAG, useUnmergedTree = true)
        .assertTextEquals(" ")
  }

  @Test
  fun modifyProfilePictureAndSaveTest() {
    `when`(mockImageRef.getFile(any(File::class.java))).thenReturn(mockDownload)
    `when`(mockDownload.isSuccessful).thenReturn(true)

    userViewModel.changeProfilePictureUrl(
        "app/src/androidTest/res/drawable/scoobygourmand_normal.jpg")

    composeTestRule.setContent {
      SampleAppTheme {
        EditAccountScreen(
            mockNavigationActions, userViewModel, mockFirebaseAuth, imageRepositoryFirebase)
      }
    }

    composeTestRule.onNodeWithTag(SAVE_CHANGES_BUTTON_TAG, useUnmergedTree = true).performClick()
  }

  @Test
  fun modifyDateOfBirthAndSaveTest() {
    composeTestRule.setContent {
      SampleAppTheme {
        EditAccountScreen(
            mockNavigationActions, userViewModel, mockFirebaseAuth, imageRepositoryFirebase)
      }
    }

    composeTestRule
        .onNodeWithTag(DATE_OF_BIRTH_CHANGE_BUTTON_TAG, useUnmergedTree = true)
        .performClick()
    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag(DATE_PICKER_POP_UP_TAG, useUnmergedTree = true)
        .performTouchInput { click(center) }

    composeTestRule
        .onNodeWithTag(DATE_PICKER_POP_UP_CONFIRM_TAG, useUnmergedTree = true)
        .performClick()

    composeTestRule
        .onNodeWithTag(DATE_OF_BIRTH_TEXT_FIELD_TAG, useUnmergedTree = true)
        .assert(!hasText(testUser.dateOfBirth))

    composeTestRule.onNodeWithTag(SAVE_CHANGES_BUTTON_TAG, useUnmergedTree = true).performClick()
  }

  @Test
  fun failedToFetchUserProfilePictureFromFirebaseTest() {
    `when`(mockImageRef.getFile(any(File::class.java))).thenReturn(mockDownload)
    `when`(mockDownload.isSuccessful).thenReturn(true)

    userViewModel.changeProfilePictureUrl(
        "app/src/androidTest/res/drawable/scoobygourmand_normal.jpg")

    `when`(mockImageRef.getFile(any(File::class.java))).thenReturn(mockDownload)
    `when`(mockDownload.isSuccessful).thenReturn(false)
    `when`(mockDownload.exception).thenReturn(Exception("Failed to fetch user profile picture"))

    composeTestRule.setContent {
      SampleAppTheme {
        EditAccountScreen(
            mockNavigationActions, userViewModel, mockFirebaseAuth, imageRepositoryFirebase)
      }
    }

    verify(mockDownload).addOnCompleteListener(onCompleteFileDownloadTaskListenerCaptor.capture())

    onCompleteFileDownloadTaskListenerCaptor.value.onComplete(mockDownload)
  }
}
