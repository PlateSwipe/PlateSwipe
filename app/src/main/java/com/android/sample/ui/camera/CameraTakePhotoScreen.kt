package com.android.sample.ui.camera

import android.Manifest
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.feature.camera.CameraView
import com.android.sample.feature.camera.RequestCameraPermission
import com.android.sample.feature.camera.createImageCapture
import com.android.sample.feature.camera.takePhoto
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.resources.C
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

/**
 * Composable function to display the camera screen and handle photo capture.
 *
 * @param navigationActions Actions to navigate between screens.
 * @param createRecipeViewModel ViewModel to manage the state of the photo and its rotation.
 */
@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraTakePhotoScreen(
    navigationActions: NavigationActions,
    createRecipeViewModel: CreateRecipeViewModel
) {
  // Remember the state of the camera permission
  val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
  // Create an ImageCapture instance
  val imageCapture = createImageCapture()
  // Get the current context
  val context = LocalContext.current

  // Request camera permission and display the camera view if granted
  RequestCameraPermission(
      cameraPermissionState,
      onPermissionGranted = {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          // Display the camera view
          CameraView(imageCapture)
          // Display the button to take a photo
          TakePhotoButton {
            takePhoto(
                context,
                imageCapture,
                onPhotoTaken = { image ->
                  createRecipeViewModel.setBitmap(image.toBitmap(), image.imageInfo.rotationDegrees)
                  // Navigate to the display image screen
                  navigationActions.navigateTo(Screen.CREATE_RECIPE_ADD_IMAGE)
                })
          }
        }
      })
}

/**
 * Composable function to display a button for taking a photo.
 *
 * @param onTakePhoto Lambda function to be called when the button is clicked.
 */
@Composable
fun TakePhotoButton(onTakePhoto: () -> Unit) {
  Box(
      modifier =
          Modifier.fillMaxSize()
              .padding(
                  bottom =
                      (C.Dimension.CameraTakePhotoScreen.BUTTON_PADDING *
                              LocalConfiguration.current.screenHeightDp)
                          .dp)
              .testTag(C.TestTag.CameraTakePhotoScreen.BUTTON_BOX),
      contentAlignment = Alignment.BottomCenter) {
        Button(
            modifier =
                Modifier.size(
                        (C.Dimension.CameraTakePhotoScreen.BUTTON_SIZE *
                                LocalConfiguration.current.screenHeightDp)
                            .dp)
                    .testTag(C.TestTag.CameraTakePhotoScreen.BUTTON),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            onClick = { onTakePhoto() }) {}
      }
}
