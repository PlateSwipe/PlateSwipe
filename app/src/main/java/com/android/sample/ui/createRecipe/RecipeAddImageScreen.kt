package com.android.sample.ui.createRecipe

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.feature.camera.openGallery
import com.android.sample.feature.camera.uriToBitmap
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.resources.C
import com.android.sample.resources.C.Tag.RECIPE_NAME_BUTTON_HEIGHT
import com.android.sample.resources.C.Tag.RECIPE_NAME_BUTTON_WIDTH
import com.android.sample.resources.C.TestTag.RecipeAddImageScreen.BOX_IMAGE
import com.android.sample.resources.C.TestTag.RecipeAddImageScreen.BOX_NEXT_BUTTON
import com.android.sample.resources.C.TestTag.RecipeAddImageScreen.CAMERA_BUTTON
import com.android.sample.resources.C.TestTag.RecipeAddImageScreen.COL_2
import com.android.sample.resources.C.TestTag.RecipeAddImageScreen.DISPLAY_IMAGE
import com.android.sample.resources.C.TestTag.RecipeAddImageScreen.DISPLAY_IMAGE_DEFAULT
import com.android.sample.resources.C.TestTag.RecipeAddImageScreen.GALLERY_BUTTON
import com.android.sample.resources.C.TestTag.RecipeAddImageScreen.MAIN_BOX
import com.android.sample.resources.C.TestTag.RecipeAddImageScreen.MAIN_COL
import com.android.sample.resources.C.TestTag.RecipeAddImageScreen.ROW_BUTTON
import com.android.sample.resources.C.TestTag.RecipeAddImageScreen.ROW_FOR_CHEF
import com.android.sample.resources.C.TestTag.RecipeAddImageScreen.TITLE_COL
import com.android.sample.resources.C.ZERO
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Typography
import com.android.sample.ui.theme.lightCream
import com.android.sample.ui.utils.PlateSwipeScaffold

/**
 * Composable function that displays the screen (Scaffold and Content) for adding an image to a
 * recipe.
 *
 * @param navigationActions The navigation actions to handle navigation events.
 * @param createRecipeViewModel The ViewModel for creating a recipe.
 */
@Composable
fun RecipeAddImageScreen(
    navigationActions: NavigationActions,
    createRecipeViewModel: CreateRecipeViewModel
) {
  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = navigationActions.currentRoute(),
      showBackArrow = true,
      content = { paddingValues ->
        RecipeAddImageContent(navigationActions, createRecipeViewModel, paddingValues)
      })
}

/**
 * Composable function that displays the content for adding an image to a recipe.
 *
 * @param navigationActions The navigation actions to handle navigation events.
 * @param createRecipeViewModel The ViewModel for creating a recipe.
 * @param paddingValues The padding values to apply to the content.
 */
@Composable
fun RecipeAddImageContent(
    navigationActions: NavigationActions,
    createRecipeViewModel: CreateRecipeViewModel,
    paddingValues: PaddingValues
) {
  Column(
      modifier = Modifier.fillMaxSize().padding(paddingValues),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        // Display the progress bar for the recipe creation process
        RecipeProgressBar(currentStep = C.Tag.ADD_IMAGE_STEP)

        Spacer(
            modifier =
                Modifier.height(
                    (C.Dimension.RecipeAddImageScreen.SPACER *
                            LocalConfiguration.current.screenHeightDp)
                        .dp))
        // Display the content for adding an image
        AddImageContent(navigationActions, createRecipeViewModel)
      }
}

/**
 * Composable function that displays the content (Without the stepper) for adding an image to a
 * recipe.
 *
 * @param navigationActions The navigation actions to handle navigation events.
 * @param createRecipeViewModel The ViewModel for creating a recipe.
 */
@Composable
fun AddImageContent(
    navigationActions: NavigationActions,
    createRecipeViewModel: CreateRecipeViewModel
) {

  val context = LocalContext.current
  // Collect the photo bitmap from the ViewModel's StateFlow
  val bitmap: Bitmap? by createRecipeViewModel.photo.collectAsState()
  val isPictureTaken by remember { derivedStateOf { bitmap != null } }
  // Launcher for the photo picker activity ( built-in option to  pick an image from the gallery)
  val photoPickerLauncher =
      rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri
        ->
        if (uri != null) {
          createRecipeViewModel.setBitmap(uriToBitmap(context, uri)!!, ZERO)
        } else {
          Toast.makeText(
                  context, context.getString(R.string.image_failed_to_load), Toast.LENGTH_SHORT)
              .show()
        }
      }
  Column(
      modifier =
          Modifier.fillMaxHeight()
              .width(
                  (C.Dimension.RecipeAddImageScreen.CONTENT_WIDTH *
                          LocalConfiguration.current.screenWidthDp)
                      .dp)
              .testTag(MAIN_COL),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        /** Title of the Screen * */
        Box(
            modifier = Modifier.fillMaxSize().testTag(MAIN_BOX),
        ) {
          Column(
              modifier = Modifier.fillMaxSize().testTag(COL_2),
              verticalArrangement = Arrangement.Top,
              horizontalAlignment = Alignment.CenterHorizontally) {
                Column(
                    modifier =
                        Modifier.fillMaxWidth()
                            .height(
                                (C.Dimension.RecipeAddImageScreen.TEXT_HEIGHT *
                                        LocalConfiguration.current.screenHeightDp)
                                    .dp)
                            .testTag(TITLE_COL),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(
                          text = stringResource(R.string.add_image),
                          style = Typography.titleMedium,
                          color = MaterialTheme.colorScheme.onPrimary,
                          textAlign = TextAlign.Center)
                    }
                Spacer(
                    modifier =
                        Modifier.height(
                            (C.Dimension.RecipeAddImageScreen.SPACER *
                                    LocalConfiguration.current.screenHeightDp)
                                .dp))

                /** Container for the Image, display the imported/taken image or default image * */
                Box(
                    modifier =
                        Modifier.fillMaxWidth()
                            .height(
                                (C.Dimension.RecipeAddImageScreen.IMAGE *
                                        LocalConfiguration.current.screenHeightDp)
                                    .dp)
                            .clip(RoundedCornerShape(C.Dimension.PADDING_8.dp))
                            .testTag(BOX_IMAGE),
                    contentAlignment = Alignment.Center,
                ) {
                  // Display the image taken from the camera or default image
                  bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = stringResource(R.string.image_taken_from_camera),
                        modifier = Modifier.fillMaxSize().testTag(DISPLAY_IMAGE),
                        contentScale = ContentScale.Crop)
                  }
                      ?: Image(
                          painter = painterResource(id = R.drawable.crop_original),
                          contentDescription = stringResource(R.string.no_image),
                          modifier = Modifier.fillMaxSize().testTag(DISPLAY_IMAGE_DEFAULT),
                      )
                }

                Spacer(
                    modifier =
                        Modifier.height(
                            (C.Dimension.RecipeAddImageScreen.SPACER *
                                    LocalConfiguration.current.screenHeightDp)
                                .dp))

                /** Container for the Camera and Gallery buttons * */
                Row(
                    modifier = Modifier.fillMaxWidth().testTag(ROW_BUTTON),
                    horizontalArrangement = Arrangement.Center) {
                      /** Camera buttons * */
                      Column(
                          modifier =
                              Modifier.weight(C.Dimension.RecipeAddImageScreen.ICON_WEIGHT)
                                  // Navigate to the CameraTakePhotoScreen when the Camera button is
                                  // clicked
                                  .clickable {
                                    navigationActions.navigateTo(Screen.CAMERA_TAKE_PHOTO)
                                  }
                                  .testTag(CAMERA_BUTTON),
                          horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = stringResource(R.string.camera),
                                modifier =
                                    Modifier.size(
                                        (C.Dimension.RecipeAddImageScreen.ICON_SIZE *
                                                LocalConfiguration.current.screenWidthDp)
                                            .dp),
                                tint = Color.Black)
                            Text(
                                text = stringResource(R.string.camera),
                                style = Typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary)
                          }
                      /** Gallery buttons * */
                      Column(
                          modifier =
                              Modifier.weight(C.Dimension.RecipeAddImageScreen.ICON_WEIGHT)
                                  .clickable { openGallery(photoPickerLauncher) }
                                  .testTag(GALLERY_BUTTON),
                          horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = stringResource(R.string.gallery),
                                modifier =
                                    Modifier.size(
                                        (C.Dimension.RecipeAddImageScreen.ICON_SIZE *
                                                LocalConfiguration.current.screenWidthDp)
                                            .dp),
                                tint = Color.Black)
                            Text(
                                text = stringResource(R.string.gallery),
                                style = Typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary)
                          }
                    }

                /** Container for the Next Button and the Chef Image * */
                Box(
                    modifier =
                        Modifier.fillMaxSize()
                            .padding(bottom = C.Dimension.PADDING_16.dp)
                            .testTag(BOX_NEXT_BUTTON),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                  Row(
                      modifier = Modifier.fillMaxWidth().testTag(ROW_FOR_CHEF),
                  ) {
                    if (shouldDisplayChefImage(
                        LocalConfiguration.current.screenWidthDp,
                        LocalConfiguration.current.screenHeightDp)) {
                      ChefImage()
                    }
                  }
                  Button(
                      onClick = {
                        if (isPictureTaken) {
                          navigationActions.navigateTo(Screen.PUBLISH_CREATED_RECIPE)
                        } else {
                          Toast.makeText(
                                  context,
                                  context.getString(R.string.error_picture_not_taken),
                                  Toast.LENGTH_SHORT)
                              .show()
                        }
                      },
                      modifier =
                          Modifier.width(RECIPE_NAME_BUTTON_WIDTH)
                              .height(RECIPE_NAME_BUTTON_HEIGHT)
                              .background(
                                  color = lightCream,
                                  shape = RoundedCornerShape(size = C.Dimension.PADDING_4.dp))
                              .testTag("NextStepButton"),
                      shape = RoundedCornerShape(C.Dimension.PADDING_4.dp)) {
                        Text(
                            text = stringResource(R.string.next),
                            style = Typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary)
                      }
                }
              }
        }
      }
}
