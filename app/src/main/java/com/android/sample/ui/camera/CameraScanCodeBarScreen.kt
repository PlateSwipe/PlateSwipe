package com.android.sample.ui.camera

import android.Manifest
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.feature.camera.CameraView
import com.android.sample.feature.camera.RequestCameraPermission
import com.android.sample.feature.camera.createImageCapture
import com.android.sample.feature.camera.handleBarcodeDetection
import com.android.sample.feature.camera.scan.CodeBarAnalyzer
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.SearchIngredientViewModel
import com.android.sample.resources.C
import com.android.sample.resources.C.Dimension.PADDING_8
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.utils.IngredientImageBox
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
  if (ingredient.first != null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
      Box(
          modifier =
              Modifier.fillMaxWidth()
                  .height((C.Dimension.CameraScanCodeBarScreen.INGREDIENT_OVERLAY_HEIGHT).dp)
                  .wrapContentHeight()) {
            // Display the ingredient details
            IngredientDisplay(
                ingredient = ingredient.first!!, searchIngredientViewModel, navigationActions)
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
    ingredient: Ingredient?,
    searchIngredientViewModel: SearchIngredientViewModel,
    navigationActions: NavigationActions
) {
  Row(
      modifier =
          Modifier.fillMaxSize()
              .background(
                  color = MaterialTheme.colorScheme.background,
                  shape =
                      RoundedCornerShape(
                          topEndPercent =
                              C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_BORDER_RADIUS,
                          topStartPercent =
                              C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_BORDER_RADIUS))
              .padding(C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_PADDING.dp),
  ) {
    if (ingredient != null) {
      Column(
          modifier =
              Modifier.weight(C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_IMAGE_WEIGHT)
                  .fillMaxHeight()
                  .padding(PADDING_8.dp),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        IngredientImageBox(ingredient)
      }

      Column(
          modifier =
              Modifier.weight(C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_TEXT_WEIGHT)
                  .fillMaxSize()
                  .padding(C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_TEXT_PADDING.dp),
          verticalArrangement = Arrangement.Center) {
            Text(
                text = ingredient.name,
                style = MaterialTheme.typography.titleSmall,
                modifier =
                    Modifier.padding(
                        vertical =
                            C.Dimension.CameraScanCodeBarScreen
                                .INGREDIENT_DISPLAY_TEXT_NAME_PADDING_V
                                .dp,
                        horizontal =
                            C.Dimension.CameraScanCodeBarScreen
                                .INGREDIENT_DISPLAY_TEXT_NAME_PADDING_H
                                .dp))
            Text(
                text = ingredient.brands ?: "",
                style = MaterialTheme.typography.bodySmall,
                modifier =
                    Modifier.padding(
                        vertical =
                            C.Dimension.CameraScanCodeBarScreen
                                .INGREDIENT_DISPLAY_TEXT_BRAND_PADDING_V
                                .dp,
                        horizontal =
                            C.Dimension.CameraScanCodeBarScreen
                                .INGREDIENT_DISPLAY_TEXT_BRAND_PADDING_H
                                .dp))
            Button(
                onClick = {
                  searchIngredientViewModel.addIngredient(ingredient)
                  navigationActions.navigateTo(Screen.CREATE_RECIPE_LIST_INGREDIENTS)
                },
                modifier =
                    Modifier.padding(
                        vertical =
                            C.Dimension.CameraScanCodeBarScreen
                                .INGREDIENT_DISPLAY_TEXT_BUTTON_PADDING_V
                                .dp,
                        horizontal =
                            C.Dimension.CameraScanCodeBarScreen
                                .INGREDIENT_DISPLAY_TEXT_BUTTON_PADDING_H
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
    }
  }
}
