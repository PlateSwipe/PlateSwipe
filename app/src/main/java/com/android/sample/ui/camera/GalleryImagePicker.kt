package com.android.sample.ui.camera

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.android.sample.R
import com.android.sample.model.takePhoto.TakePhotoViewModel
import com.android.sample.resources.C

/**
 * Photo Picker Example
 *
 * @return Unit Function to display the Photo Picker Example
 * @see <a
 *   href="https://developer.android.com/training/data-storage/shared/photopicker">ActivityResultContracts</a>
 */
@Composable
fun PhotoPicker(takePhotoViewModel: TakePhotoViewModel) {
  // State to hold the selected image URI
  val selectedImageUri: Uri? by takePhotoViewModel.uri.collectAsState()
  val context = LocalContext.current

  // Launcher for the photo picker activity
  val photoPickerLauncher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri
        ->
        // Callback to handle the selected image URI
        takePhotoViewModel.setUri(uri!!)
      }

  // Layout for the photo picker UI
  Column(
      modifier =
          Modifier.fillMaxSize()
              .padding(C.Dimension.PAD_4.dp)
              .testTag(C.TestTag.PhotoPicker.PHOTO_PICKER_COLUMN),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        // Button to launch the photo picker
        Button(
            modifier = Modifier.testTag(C.TestTag.PhotoPicker.PHOTO_PICKER_BUTTON),
            onClick = { openGallery(photoPickerLauncher) }) {
              Text(
                  text = stringResource(R.string.select_image),
                  style = MaterialTheme.typography.bodySmall,
                  modifier = Modifier.testTag(C.TestTag.PhotoPicker.PHOTO_PICKER_TEXT))
            }

        // Display the selected image if available
        selectedImageUri?.let { uri ->
          Image(
              painter =
                  rememberAsyncImagePainter(
                      ImageRequest.Builder(context).data(uri).crossfade(true).build()),
              contentDescription = stringResource(R.string.imported_image_from_the_gallery),
              modifier =
                  Modifier.size(
                          (C.Dimension.PhotoPicker.PHOTO_PICKER_IMAGE_SIZE *
                                  LocalConfiguration.current.screenWidthDp)
                              .dp)
                      .clickable {
                        // Re-open the photo picker when clicking the image
                        openGallery(photoPickerLauncher)
                      }
                      .testTag(C.TestTag.PhotoPicker.PHOTO_PICKER_IMAGE),
              contentScale = ContentScale.Crop)
        } ?: run { Text(text = stringResource(R.string.no_image_selected_yet)) }
      }
}

/**
 * Function to open the gallery to select an image
 *
 * @param photoPickerLauncher ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
 */
private fun openGallery(
    photoPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
) {
  photoPickerLauncher.launch(
      PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
}
