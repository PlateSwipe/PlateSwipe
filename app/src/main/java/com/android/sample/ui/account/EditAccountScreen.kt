package com.android.sample.ui.account

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.feature.camera.openGallery
import com.android.sample.feature.camera.uriToBitmap
import com.android.sample.model.image.ImageDirectoryType
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.Tag.AccountScreen.PROFILE_PICTURE_CONTENT_DESCRIPTION
import com.android.sample.resources.C.Tag.UserViewModel.IMAGE_NAME
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.TopLevelDestinations
import com.android.sample.ui.utils.PlateSwipeScaffold
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.runBlocking

@Composable
fun EditAccountScreen(navigationActions: NavigationActions,
                      userViewModel: UserViewModel,
                      firebaseAuth: FirebaseAuth = Firebase.auth,
                      imageRepositoryFirebase: ImageRepositoryFirebase = ImageRepositoryFirebase(FirebaseStorage.getInstance())
) {
    val context = LocalContext.current

  PlateSwipeScaffold(
      navigationActions,
      TopLevelDestinations.ACCOUNT.route,
      showBackArrow = true,
      content = { padding ->
          val userUid = firebaseAuth.currentUser!!.uid
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
                      { Log.e("EditAccountScreen", it.message!!)}
                  )
              }
          }

          val photoPickerLauncher =
              rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri
                  ->
                  if (uri != null) {
                      newProfilePictureImageBitmap = uriToBitmap(context, uri)!!.asImageBitmap()
                  } else {
                      Toast.makeText(
                          context, context.getString(R.string.image_failed_to_load), Toast.LENGTH_SHORT)
                          .show()
                  }
              }

          Column (
              modifier = Modifier
                  .fillMaxSize()
                  .padding(padding)
                  .padding(8.dp),
              verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
              horizontalAlignment = Alignment.CenterHorizontally
          ) {
              Spacer(modifier = Modifier.height(16.dp))

              ProfilePicture(
                  newProfilePictureImageBitmap,
                  modifier = Modifier.weight(.4f),
                  photoPickerLauncher
              )

              ListChangeableInformation(newUserName, Modifier.weight(.8f), firebaseAuth) {newUserName = it}

              SaveButton(
                  userViewModel,
                  navigationActions,
                  imageRepositoryFirebase,
                  userUid,
                  userName,
                  newUserName,
                  newProfilePictureImageBitmap
              )

          }

      })
}

@Composable
private fun ProfilePicture(profilePictureUrl: ImageBitmap?, modifier: Modifier = Modifier, photoPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>) {
        Box (modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape), contentAlignment = Alignment.Center) {
            IconButton(
                onClick = {
                    runBlocking { openGallery(photoPickerLauncher) }
                },
                modifier = Modifier.fillMaxSize()
            ) {
                if (profilePictureUrl == null){
                    Image(
                        painter = painterResource(id = R.drawable.account),
                        contentDescription = PROFILE_PICTURE_CONTENT_DESCRIPTION,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        bitmap = profilePictureUrl,
                        contentDescription = PROFILE_PICTURE_CONTENT_DESCRIPTION,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
}

@Composable
private fun InputTextBox(boxName: String, boxValue: String, readOnly: Boolean, onValueChange: (String) -> Unit){
    OutlinedTextField(
        value = boxValue,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(boxName) },
        readOnly = readOnly,
        singleLine = true
    )
}

@Composable
private fun ListChangeableInformation(newUserName: String, modifier: Modifier, firebaseAuth: FirebaseAuth, onValueChangeUserName: (String) -> Unit){
    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        InputTextBox("Username", newUserName, false, onValueChangeUserName)

        Spacer(modifier = Modifier.height(32.dp))

        InputTextBox("Email", firebaseAuth.currentUser!!.email!!, true) {}

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SaveButton(
    userViewModel: UserViewModel,
    navigationActions: NavigationActions,
    imageRepositoryFirebase: ImageRepositoryFirebase,
    userUid: String,
    userName: State<String?>,
    newUserName: String,
    newProfilePictureImageBitmap: ImageBitmap?){
    Row (modifier = Modifier.width(221.dp)){
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (userName.value != newUserName) {
                    userViewModel.changeUserName(newUserName)
                }
                if (newProfilePictureImageBitmap != null) {
                    imageRepositoryFirebase.uploadImage(
                        userUid,
                        IMAGE_NAME,
                        ImageDirectoryType.USER,
                        newProfilePictureImageBitmap, {
                            imageRepositoryFirebase.getImageUrl(userUid,
                                IMAGE_NAME,
                                ImageDirectoryType.USER,
                                { userViewModel.changeProfilePictureUrl(it.toString()) },
                                { Log.e("EditAccountScreen", it.message!!) })
                        },
                        { Log.e("EditAccountScreen", it.message!!) }
                    )
                }

                navigationActions.goBack()
            }
        ) {
            Text("Save changes")
        }
    }
}