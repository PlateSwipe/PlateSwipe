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
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
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
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScanCodeBarScreen(
    navigationActions: NavigationActions,
    ingredientViewModel: IngredientViewModel
) {
  val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

  RequestCameraPermission(
      cameraPermissionState,
      onPermissionGranted = {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
          CameraSection(ingredientViewModel)
          IngredientOverlay(ingredientViewModel)
        }
      })
}

/** Display the camera view and the barcode frame */
@Composable
fun CameraSection(ingredientViewModel: IngredientViewModel) {

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
                ingredientViewModel.fetchIngredient(barcodeValue)
                lastScannedBarcode = barcodeValue
              })
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
          Modifier.padding(32.dp)
              .width((1f * LocalConfiguration.current.screenWidthDp).dp)
              .height((0.4f * LocalConfiguration.current.screenHeightDp).dp)
              .border(width = 1.dp, color = Color.White, shape = RoundedCornerShape(8.dp))
              .testTag("Barcode frame"),
      contentAlignment = Alignment.Center) {}
}
/** Display the ingredient overlay */
@Composable
fun IngredientOverlay(viewModel: IngredientViewModel) {
  val ingredient by viewModel.ingredient.collectAsState()
  if (ingredient != null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
      Box(
          modifier =
              Modifier.fillMaxWidth()
                  .height((0.4f * LocalConfiguration.current.screenWidthDp).dp)
                  .wrapContentHeight()) {
            // Display the ingredient details
            IngredientDisplay(ingredient = ingredient!!)
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
fun IngredientDisplay(ingredient: Ingredient?) {
  Row(
      modifier =
          Modifier.fillMaxSize()
              .background(
                  color = Color.White,
                  shape = RoundedCornerShape(topEndPercent = 10, topStartPercent = 10))
              .padding(8.dp),
  ) {
    if (ingredient != null) {
      Column(
          modifier = Modifier.weight(0.3f).fillMaxHeight().padding(8.dp),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Box(
            modifier =
                Modifier.width(100.dp)
                    .height(100.dp)
                    .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
                    .background(Color.Gray),
        ) {}
      }

      Column(
          modifier = Modifier.weight(0.7f).fillMaxSize().padding(8.dp),
          verticalArrangement = Arrangement.Center) {
            Text(
                text = ingredient.name,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp))
            Text(
                text = ingredient.brands ?: "",
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp))
            Button(
                onClick = { /*TODO*/},
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)) {
                  Text(text = stringResource(R.string.add_to_fridge))
                }
          }
    }
  }
}
