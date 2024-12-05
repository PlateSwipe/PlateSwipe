package com.android.sample.ui.account

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.feature.camera.openGallery
import com.android.sample.feature.camera.uriToBitmap
import com.android.sample.model.image.ImageDirectoryType
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.Tag.AccountScreen.PROFILE_PICTURE_CONTENT_DESCRIPTION
import com.android.sample.resources.C.Tag.EditAccountScreen.CHANGE_PROFILE_PICTURE_BUTTON_DESCRIPTION
import com.android.sample.resources.C.Tag.EditAccountScreen.DATE_OF_BIRTH_FIELD_DESCRIPTION
import com.android.sample.resources.C.Tag.EditAccountScreen.LOG_MESSAGE_TAG
import com.android.sample.resources.C.Tag.UserViewModel.IMAGE_NAME
import com.android.sample.resources.C.TestTag.EditAccountScreen.CHANGE_PROFILE_PICTURE_BUTTON_TAG
import com.android.sample.resources.C.TestTag.EditAccountScreen.DATE_OF_BIRTH_CHANGE_BUTTON_TAG
import com.android.sample.resources.C.TestTag.EditAccountScreen.DATE_OF_BIRTH_TEXT_FIELD_TAG
import com.android.sample.resources.C.TestTag.EditAccountScreen.DATE_PICKER_POP_UP_CANCEL_TAG
import com.android.sample.resources.C.TestTag.EditAccountScreen.DATE_PICKER_POP_UP_CONFIRM_TAG
import com.android.sample.resources.C.TestTag.EditAccountScreen.DATE_PICKER_POP_UP_TAG
import com.android.sample.resources.C.TestTag.EditAccountScreen.PROFILE_PICTURE_TAG
import com.android.sample.resources.C.TestTag.EditAccountScreen.SAVE_CHANGES_BUTTON_TAG
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.TopLevelDestinations
import com.android.sample.ui.utils.PlateSwipeButton
import com.android.sample.ui.utils.PlateSwipeScaffold
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EditAccountScreen(
    navigationActions: NavigationActions,
    userViewModel: UserViewModel,
    firebaseAuth: FirebaseAuth = Firebase.auth,
    imageRepositoryFirebase: ImageRepositoryFirebase =
        ImageRepositoryFirebase(FirebaseStorage.getInstance())
) {
  val context = LocalContext.current
  val userUid = firebaseAuth.currentUser?.uid ?: return

  PlateSwipeScaffold(
      navigationActions,
      TopLevelDestinations.ACCOUNT.route,
      showBackArrow = true,
      content = { padding ->
        val userName = userViewModel.userName.collectAsState()
        var newUserName by remember { mutableStateOf(userName.value!!) }

        val profilePictureUrl = userViewModel.profilePictureUrl.collectAsState()
        var newProfilePictureImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
        LaunchedEffect(Unit) {
          if (!profilePictureUrl.value.isNullOrEmpty()) {
            imageRepositoryFirebase.getImage(
                userUid,
                IMAGE_NAME,
                ImageDirectoryType.USER,
                { newProfilePictureImageBitmap = it },
                { Log.e(LOG_MESSAGE_TAG, it.message!!) })
          }
        }
        val photoPickerLauncher =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia()) { uri ->
                  if (uri != null) {
                    newProfilePictureImageBitmap = uriToBitmap(context, uri)!!.asImageBitmap()
                  } else {
                    Toast.makeText(
                            context,
                            context.getString(R.string.image_failed_to_load),
                            Toast.LENGTH_SHORT)
                        .show()
                  }
                }

        val dateOfBirth = userViewModel.dateOfBirth.collectAsState()
        var newDateOfBirth by remember { mutableStateOf(dateOfBirth.value) }

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Spacer(modifier = Modifier.weight(.1f))

              ProfilePicture(
                  newProfilePictureImageBitmap,
                  modifier = Modifier.weight(.7f),
                  photoPickerLauncher)

              Spacer(modifier = Modifier.weight(.1f))

              ListChangeableInformation(
                  newUserName,
                  newDateOfBirth,
                  Modifier.weight(.8f),
                  firebaseAuth,
                  { newUserName = it },
                  { newDateOfBirth = it })

              Spacer(modifier = Modifier.weight(.2f))

              PlateSwipeButton(
                  stringResource(R.string.save_changes_button),
                  Modifier.testTag(SAVE_CHANGES_BUTTON_TAG),
                  onClick = {
                    saveChangesUsername(userViewModel, userName, newUserName)
                    saveChangesProfilePicture(
                        userViewModel,
                        imageRepositoryFirebase,
                        userUid,
                        newProfilePictureImageBitmap)
                    saveChangesDateOfBirth(userViewModel, dateOfBirth, newDateOfBirth)
                    navigationActions.goBack()
                  })

              Spacer(modifier = Modifier.weight(.025f))
            }
      })
}

/**
 * Function that creates the composable for the profile picture and the edit button
 *
 * @param newProfilePictureImageBitmap the [ImageBitmap] of the profile picture which will be
 *   displayed if it exists; if it does not, a default image will be displayed
 * @param modifier the modifier which will be passed to the box containing the image of the profile
 *   picture and the button to change the picture
 * @param photoPickerLauncher the launch activity when we choose a photo from the gallery to be used
 *   as the profile picture
 */
@Composable
private fun ProfilePicture(
    newProfilePictureImageBitmap: ImageBitmap?,
    modifier: Modifier = Modifier,
    photoPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
) {
  Box(modifier = modifier.aspectRatio(1f), contentAlignment = Alignment.Center) {
    if (newProfilePictureImageBitmap == null) {
      Image(
          painter = painterResource(id = R.drawable.account),
          contentDescription = PROFILE_PICTURE_CONTENT_DESCRIPTION,
          contentScale = ContentScale.FillBounds,
          modifier =
              Modifier.fillMaxSize()
                  .clip(CircleShape)
                  .border(2.dp, Color.Black, shape = CircleShape)
                  .testTag(PROFILE_PICTURE_TAG))
    } else {
      Image(
          bitmap = newProfilePictureImageBitmap,
          contentDescription = PROFILE_PICTURE_CONTENT_DESCRIPTION,
          contentScale = ContentScale.FillBounds,
          modifier =
              Modifier.fillMaxSize()
                  .clip(CircleShape)
                  .border(2.dp, Color.Black, shape = CircleShape)
                  .testTag(PROFILE_PICTURE_TAG))
    }
    Box(
        modifier =
            Modifier.align(Alignment.BottomEnd)
                .size(50.dp)
                .offset(x = (-7.5).dp, y = (-7.5).dp)
                .background(MaterialTheme.colorScheme.background, shape = CircleShape)
                .border(1.dp, Color.Black, CircleShape)
                .clickable { openGallery(photoPickerLauncher) }) {
          Icon(
              imageVector = Icons.Default.CameraAlt,
              contentDescription = CHANGE_PROFILE_PICTURE_BUTTON_DESCRIPTION,
              modifier =
                  Modifier.fillMaxSize().padding(5.dp).testTag(CHANGE_PROFILE_PICTURE_BUTTON_TAG),
              tint = Color.Black)
        }
  }
}

/**
 * Function that creates an input text field composable for a given field
 *
 * @param boxName the name given to the text field, which will be displayed above the value inside
 *   it
 * @param boxValue the value which will be contained in the text field
 * @param readOnly [Boolean] indicating if the field is read only or not
 * @param onValueChange action to be performed when the value of the field changes
 */
@Composable
private fun InputTextBox(
    boxName: String,
    boxValue: String,
    readOnly: Boolean,
    onValueChange: (String) -> Unit
) {
  OutlinedTextField(
      value = boxValue,
      onValueChange = onValueChange,
      modifier = Modifier.width(350.dp).testTag("$boxName text field"),
      textStyle = TextStyle(fontSize = 16.sp),
      label = { Text(boxName) },
      readOnly = readOnly,
      singleLine = true)
}

/**
 * Function that creates the composable which contains the date picker
 *
 * @param newDateOfBirth the date of birth which will be displayed and stored in the future
 * @param onValueChange action to be performed when the value of date of birth changes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateOfBirthBox(newDateOfBirth: String?, onValueChange: (String) -> Unit) {
  var showDatePicker by remember { mutableStateOf(false) }
  val datePickerState = rememberDatePickerState()
  var displayedDate by remember { mutableStateOf(newDateOfBirth!!) }
  val selectedDate =
      datePickerState.selectedDateMillis?.let {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatter.format(Date(it))
      } ?: " "

  OutlinedTextField(
      value = displayedDate,
      onValueChange = {},
      textStyle = TextStyle(fontSize = 16.sp),
      label = { Text(stringResource(R.string.date_of_birth_label)) },
      readOnly = true,
      trailingIcon = {
        IconButton(
            modifier = Modifier.testTag(DATE_OF_BIRTH_CHANGE_BUTTON_TAG),
            onClick = { showDatePicker = !showDatePicker }) {
              Icon(
                  imageVector = Icons.Default.DateRange,
                  contentDescription = DATE_OF_BIRTH_FIELD_DESCRIPTION)
            }
      },
      modifier = Modifier.width(350.dp).testTag(DATE_OF_BIRTH_TEXT_FIELD_TAG))

  DateOfBirthPopUpLogic(
      showDatePicker,
      datePickerState,
      onDismissRequest = { showDatePicker = false },
      confirmButtonClickAction = {
        showDatePicker = false
        displayedDate = selectedDate
        onValueChange(selectedDate)
      },
      dismissButtonClickAction = { showDatePicker = false })
}

/**
 * The logic of the date of birth picker pop up where we select the date
 *
 * @param showDatePicker boolean which indicates if we want to show the date picker or not
 * @param datePickerState [DatePickerState] which transforms the selected date into a string
 * @param onDismissRequest action to be performed when we dismiss the pop up
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateOfBirthPopUpLogic(
    showDatePicker: Boolean,
    datePickerState: DatePickerState,
    onDismissRequest: () -> Unit,
    confirmButtonClickAction: () -> Unit,
    dismissButtonClickAction: () -> Unit
) {
  if (showDatePicker) {
    DatePickerDialog(
        modifier = Modifier.testTag(DATE_PICKER_POP_UP_TAG),
        onDismissRequest = onDismissRequest,
        confirmButton = {
          TextButton(
              modifier = Modifier.testTag(DATE_PICKER_POP_UP_CONFIRM_TAG),
              onClick = confirmButtonClickAction) {
                Text(
                    text = stringResource(R.string.confirm_date_of_birth_pop_up),
                    color = MaterialTheme.colorScheme.onSecondaryContainer)
              }
        },
        dismissButton = {
          TextButton(
              modifier = Modifier.testTag(DATE_PICKER_POP_UP_CANCEL_TAG),
              onClick = dismissButtonClickAction) {
                Text(
                    text = stringResource(R.string.cancel_date_of_birth_pop_up),
                    color = MaterialTheme.colorScheme.onSecondaryContainer)
              }
        }) {
          DatePicker(state = datePickerState, showModeToggle = false)
        }
  }
}

/**
 * Method that creates the different fields of the user that can be read or modified
 *
 * @param newUserName the user name that will be displayed in the field as well as modified
 * @param newDateOfBirth the date of birth that will be displayed in the field as well as modified
 * @param modifier the modifier that will be applied on the column containing the different fields
 * @param firebaseAuth the firebase authentication used to retrieve the users email address
 * @param onValueChangeUserName the function which will be called when the user name is modified
 * @param onValueChangeDateOfBirth the function which will be called when the date of birth is
 *   modified
 */
@Composable
private fun ListChangeableInformation(
    newUserName: String,
    newDateOfBirth: String?,
    modifier: Modifier,
    firebaseAuth: FirebaseAuth,
    onValueChangeUserName: (String) -> Unit,
    onValueChangeDateOfBirth: (String) -> Unit
) {
  Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    InputTextBox(stringResource(R.string.username_label), newUserName, false, onValueChangeUserName)

    Spacer(modifier = Modifier.weight(.3f))

    InputTextBox(stringResource(R.string.email_label), firebaseAuth.currentUser!!.email!!, true) {}

    Spacer(modifier = Modifier.weight(.3f))

    DateOfBirthBox(newDateOfBirth, onValueChangeDateOfBirth)
  }
}

/**
 * Function that changes the new user name in the viewmodel as well as in the database
 *
 * @param userViewModel the view model of the user
 * @param userName the old user name
 * @param newUserName the new user name
 */
private fun saveChangesUsername(
    userViewModel: UserViewModel,
    userName: State<String?>,
    newUserName: String
) {
  if (userName.value != newUserName) {
    userViewModel.changeUserName(newUserName)
  }
}

/**
 * Function that changes the new profile picture in the viewmodel as well as in the database
 *
 * @param userViewModel the view model of the user
 * @param imageRepositoryFirebase the firebase repository of the images
 * @param userUid the uid of the user
 * @param newProfilePictureImageBitmap the new profile picture of the user
 */
private fun saveChangesProfilePicture(
    userViewModel: UserViewModel,
    imageRepositoryFirebase: ImageRepositoryFirebase,
    userUid: String,
    newProfilePictureImageBitmap: ImageBitmap?
) {
  if (newProfilePictureImageBitmap != null) {
    imageRepositoryFirebase.uploadImage(
        userUid,
        IMAGE_NAME,
        ImageDirectoryType.USER,
        newProfilePictureImageBitmap,
        {
          imageRepositoryFirebase.getImageUrl(
              userUid,
              IMAGE_NAME,
              ImageDirectoryType.USER,
              { userViewModel.changeProfilePictureUrl(it.toString()) },
              { Log.e(LOG_MESSAGE_TAG, it.message!!) })
        },
        { Log.e(LOG_MESSAGE_TAG, it.message!!) })
  }
}

/**
 * Function that changes the new date of birth in the viewmodel as well as in the database
 *
 * @param userViewModel the view model of the user
 * @param dateOfBirth old date of birth of the user
 * @param newDateOfBirth new date of birth of the user
 */
private fun saveChangesDateOfBirth(
    userViewModel: UserViewModel,
    dateOfBirth: State<String?>,
    newDateOfBirth: String?
) {
  if (newDateOfBirth != dateOfBirth.value) {
    userViewModel.changeDateOfBirth(newDateOfBirth!!)
  }
}
