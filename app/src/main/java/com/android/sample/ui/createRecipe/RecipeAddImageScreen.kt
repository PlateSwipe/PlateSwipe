package com.android.sample.ui.createRecipe

import android.graphics.Bitmap
import android.widget.Toast
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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.resources.C
import com.android.sample.resources.C.Tag.RECIPE_NAME_BUTTON_HEIGHT
import com.android.sample.resources.C.Tag.RECIPE_NAME_BUTTON_WIDTH
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
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
  var isPictureTaken by remember { mutableStateOf(false) }
  val context = LocalContext.current
  // Collect the photo bitmap from the ViewModel's StateFlow
  val bitmap: Bitmap? by createRecipeViewModel.photo.collectAsState()
  bitmap?.let { isPictureTaken = true }
  Column(
      modifier =
          Modifier.fillMaxHeight()
              .width(
                  (C.Dimension.RecipeAddImageScreen.CONTENT_WIDTH *
                          LocalConfiguration.current.screenWidthDp)
                      .dp)
              .testTag("main column"),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        /** Title of the Screen * */
        Box(
            modifier = Modifier.fillMaxSize().testTag("main box"),
        ) {
          Column(
              modifier = Modifier.fillMaxSize().testTag("col 2"),
              verticalArrangement = Arrangement.Top,
              horizontalAlignment = Alignment.CenterHorizontally) {
                Column(
                    modifier =
                        Modifier.fillMaxWidth()
                            .height(
                                (C.Dimension.RecipeAddImageScreen.TEXT_HEIGHT *
                                        LocalConfiguration.current.screenHeightDp)
                                    .dp)
                            .testTag("title col"),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(
                          text = stringResource(R.string.add_image),
                          style = MaterialTheme.typography.h5,
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
                            .testTag("box for image"),
                    contentAlignment = Alignment.Center,
                ) {
                  // Display the image taken from the camera or default image
                  bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = stringResource(R.string.image_taken_from_camera),
                        modifier = Modifier.fillMaxSize().testTag("display_image"),
                        contentScale = ContentScale.Crop)
                  }
                      ?: Image(
                          painter = painterResource(id = R.drawable.crop_original),
                          contentDescription = stringResource(R.string.no_image),
                          modifier = Modifier.fillMaxSize().testTag("display_image_default"),
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
                    modifier = Modifier.fillMaxWidth().testTag("row for buttons"),
                    horizontalArrangement = Arrangement.Center) {
                      Column(
                          modifier =
                              Modifier.weight(C.Dimension.RecipeAddImageScreen.ICON_WEIGHT)
                                  // Navigate to the CameraTakePhotoScreen when the Camera button is
                                  // clicked
                                  .clickable {
                                    navigationActions.navigateTo(Screen.CAMERA_TAKE_PHOTO)
                                  }
                                  .testTag("camera button"),
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
                                style = MaterialTheme.typography.body1)
                          }
                      Column(
                          modifier =
                              Modifier.weight(C.Dimension.RecipeAddImageScreen.ICON_WEIGHT)
                                  .clickable {
                                    Toast.makeText(context, "Image", Toast.LENGTH_SHORT).show()
                                  }
                                  .testTag("gallery button"),
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
                                style = MaterialTheme.typography.body1)
                          }
                    }

                /** Container for the Next Button and the Chef Image * */
                Box(
                    modifier =
                        Modifier.fillMaxSize()
                            .padding(bottom = C.Dimension.PADDING_16.dp)
                            .testTag("box for next button"),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                  Row(
                      modifier = Modifier.fillMaxWidth().testTag("row for chef image"),
                  ) {
                    ChefImage()
                  }
                  Button(
                      onClick = { navigationActions.navigateTo(Screen.PUBLISH_CREATED_RECIPE) },
                      modifier =
                          Modifier.width(RECIPE_NAME_BUTTON_WIDTH)
                              .height(RECIPE_NAME_BUTTON_HEIGHT)
                              .background(
                                  color = lightCream,
                                  shape = RoundedCornerShape(size = C.Dimension.PADDING_4.dp)),
                      shape = RoundedCornerShape(C.Dimension.PADDING_4.dp),
                      enabled = isPictureTaken) {
                        Text(
                            text = stringResource(R.string.next),
                            style = MaterialTheme.typography.button)
                      }
                }
              }
        }
      }
}
