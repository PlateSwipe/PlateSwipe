package com.android.sample.ui.camera

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.android.sample.R
import com.android.sample.animation.LoadingCook
import com.android.sample.feature.camera.CameraView
import com.android.sample.feature.camera.RequestCameraPermission
import com.android.sample.feature.camera.createImageCapture
import com.android.sample.feature.camera.handleBarcodeDetection
import com.android.sample.feature.camera.scan.CodeBarAnalyzer
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.SearchIngredientViewModel
import com.android.sample.resources.C
import com.android.sample.resources.C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_IMAGE_BORDER_RADIUS
import com.android.sample.resources.C.Dimension.PADDING_8
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_SMALL_URL
import com.android.sample.resources.C.TestTag.SwipePage.RECIPE_IMAGE_1
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScanCodeBarScreen(
    navigationActions: NavigationActions,
    searchIngredientViewModel: SearchIngredientViewModel
) {
  val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

  RequestCameraPermission(
      cameraPermissionState,
      onPermissionGranted = {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
          CameraSection(searchIngredientViewModel)
          IngredientOverlay(searchIngredientViewModel, navigationActions)
        }
      })
}

/** Display the camera view and the barcode frame */
@Composable
fun CameraSection(searchIngredientViewModel: SearchIngredientViewModel) {

  val imageCapture = createImageCapture()
  var lastScannedBarcode: Long? = null
  val recentBarcodes = remember { mutableListOf<Long>() }
  Box(contentAlignment = Alignment.Center) {
    CameraView(
        imageCapture,
        CodeBarAnalyzer { barcode ->
          handleBarcodeDetection(
              barcode.rawValue?.toLongOrNull(),
              recentBarcodes,
              lastScannedBarcode,
              onBarcodeDetected = { barcodeValue ->
                searchIngredientViewModel.fetchIngredient(barcodeValue)
                lastScannedBarcode = barcodeValue
              },
              scanThreshold = C.Tag.SCAN_THRESHOLD)
        })
    BarCodeFrame()
  }
}

/** Display the camera view and the barcode frame */
@Composable
@Preview(showBackground = true, backgroundColor = 0x00000000)
fun BarCodeFrame() {
  Box(
      modifier =
          Modifier.padding(C.Dimension.CameraScanCodeBarScreen.BARCODE_FRAME_PADDING.dp)
              .width(
                  (C.Dimension.CameraScanCodeBarScreen.BARCODE_FRAME_WIDTH *
                          LocalConfiguration.current.screenWidthDp)
                      .dp)
              .height(
                  (C.Dimension.CameraScanCodeBarScreen.BARCODE_FRAME_HEIGHT *
                          LocalConfiguration.current.screenHeightDp)
                      .dp)
              .border(
                  width = C.Dimension.CameraScanCodeBarScreen.BARCODE_FRAME_BORDER_WIDTH.dp,
                  color = Color.White,
                  shape =
                      RoundedCornerShape(
                          C.Dimension.CameraScanCodeBarScreen.BARCODE_FRAME_BORDER_RADIUS.dp))
              .testTag(C.TestTag.CameraScanCodeBarScreen.BARCODE_FRAME),
      contentAlignment = Alignment.Center) {}
}

/** Display the ingredient overlay */
@Composable
fun IngredientOverlay(
    searchIngredientViewModel: SearchIngredientViewModel,
    navigationActions: NavigationActions,
) {
  val ingredient by searchIngredientViewModel.ingredient.collectAsState()

  val isFetchingByBarcode by searchIngredientViewModel.isFetchingByBarcode.collectAsState()

  // We need to know if the person has attempted to scan a barcode once
  // we do this to avoid showing the ingredientDisplay before any scan has even been done
  var hasFetchedBarcode by remember { mutableStateOf(false) }

  if (isFetchingByBarcode) {
    hasFetchedBarcode = true
  }

  if (hasFetchedBarcode) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
      Box(
          modifier =
              Modifier.fillMaxWidth()
                  .background(
                      color = MaterialTheme.colorScheme.background,
                      shape =
                          RoundedCornerShape(
                              topEndPercent =
                                  C.Dimension.CameraScanCodeBarScreen
                                      .INGREDIENT_DISPLAY_BORDER_RADIUS,
                              topStartPercent =
                                  C.Dimension.CameraScanCodeBarScreen
                                      .INGREDIENT_DISPLAY_BORDER_RADIUS))
                  .height((C.Dimension.CameraScanCodeBarScreen.INGREDIENT_OVERLAY_HEIGHT).dp)
                  .wrapContentHeight()) {
            if (isFetchingByBarcode) {
              IngredientBeingFetchedDisplay()
            } else if (ingredient.first == null) {
              IngredientNotFoundDisplay()
            } else {
              ingredient.first?.let {
                IngredientDisplay(ingredient = it, searchIngredientViewModel, navigationActions)
              }
            }
          }
    }
  }
}

/**
 * Display the ingredient information
 *
 * @param ingredient the ingredient to display
 */
@Composable
fun IngredientDisplay(
    ingredient: Ingredient,
    searchIngredientViewModel: SearchIngredientViewModel,
    navigationActions: NavigationActions
) {
  Row(
      modifier =
          Modifier.fillMaxSize()
              .padding(C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_PADDING.dp),
  ) {
    Column(
        modifier =
            Modifier.weight(C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_IMAGE_WEIGHT)
                .fillMaxHeight()
                .padding(PADDING_8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      IngredientImage(ingredient)
    }

    Column(
        modifier =
            Modifier.weight(C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_TEXT_WEIGHT)
                .fillMaxSize()
                .padding(C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_TEXT_PADDING.dp),
        verticalArrangement = Arrangement.Center) {
          IngredientNameAndBrand(ingredient)
          IngredientSelectButton(ingredient, searchIngredientViewModel, navigationActions)
        }
  }
}

@Composable
private fun IngredientImage(ingredient: Ingredient) {
  Box(
      modifier =
          Modifier.width(C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_IMAGE_WIDTH.dp)
              .height(C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_IMAGE_HEIGHT.dp)
              .border(
                  width =
                      C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_IMAGE_BORDER_WIDTH.dp,
                  color = Color.Black,
                  shape = RoundedCornerShape(INGREDIENT_DISPLAY_IMAGE_BORDER_RADIUS.dp))
              .background(MaterialTheme.colorScheme.background),
  ) {
    Image(
        painter =
            rememberAsyncImagePainter(model = ingredient.images[PRODUCT_FRONT_IMAGE_SMALL_URL]),
        contentDescription = stringResource(R.string.recipe_image),
        modifier =
            Modifier.fillMaxSize()
                .testTag(RECIPE_IMAGE_1)
                .clip(RoundedCornerShape(INGREDIENT_DISPLAY_IMAGE_BORDER_RADIUS.dp)),
        contentScale = ContentScale.Crop,
    )
  }
}

@Composable
private fun IngredientNameAndBrand(ingredient: Ingredient) {
  Text(
      text = ingredient.name,
      style = MaterialTheme.typography.titleSmall,
      modifier =
          Modifier.padding(
              vertical =
                  C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_TEXT_NAME_PADDING_V.dp,
              horizontal =
                  C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_TEXT_NAME_PADDING_H.dp))
  Text(
      text = ingredient.brands ?: "",
      style = MaterialTheme.typography.bodySmall,
      modifier =
          Modifier.padding(
              vertical =
                  C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_TEXT_BRAND_PADDING_V.dp,
              horizontal =
                  C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_TEXT_BRAND_PADDING_H.dp))
}

@Composable
private fun IngredientSelectButton(
    ingredient: Ingredient,
    searchIngredientViewModel: SearchIngredientViewModel,
    navigationActions: NavigationActions
) {
  Button(
      onClick = {
        searchIngredientViewModel.addIngredient(ingredient)
        searchIngredientViewModel.clearIngredient()
        navigationActions.navigateTo(Screen.CREATE_RECIPE_LIST_INGREDIENTS)
      },
      modifier =
          Modifier.padding(
              vertical =
                  C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_TEXT_BUTTON_PADDING_V.dp,
              horizontal =
                  C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_TEXT_BUTTON_PADDING_H
                      .dp)) {
        Text(
            text =
                if (navigationActions.currentRoute() == Route.FRIDGE)
                    stringResource(R.string.add_to_fridge)
                else stringResource(R.string.add_to_recipe),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary,
        )
      }
}

@Composable
private fun IngredientNotFoundDisplay() {
  Box(
      modifier =
          Modifier.fillMaxSize()
              .padding(C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_PADDING.dp),
      contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.ingredient_not_found),
            style = MaterialTheme.typography.bodyLarge,
            modifier =
                Modifier.padding(
                    vertical =
                        C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_TEXT_NAME_PADDING_V
                            .dp,
                    horizontal =
                        C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_TEXT_NAME_PADDING_H
                            .dp))
      }
}

@Preview
@Composable
private fun IngredientBeingFetchedDisplay() {
  Box(
      modifier =
          Modifier.fillMaxSize()
              .padding(C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_PADDING.dp),
      contentAlignment = Alignment.Center) {
        LoadingCook()
      }
}
