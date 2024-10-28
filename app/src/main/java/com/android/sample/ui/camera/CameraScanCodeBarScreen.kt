package com.android.sample.ui.camera

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.feature.camera.CameraView
import com.android.sample.feature.camera.RequestCameraPermission
import com.android.sample.feature.camera.createImageCapture
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
  val context = LocalContext.current
  val imageCapture = createImageCapture()
  val ingredient by ingredientViewModel.ingredient.collectAsState()
  var lastScannedBarcode: Long? = null
  val recentBarcodes = remember { mutableListOf<Long>() }
  val barcodeThreshold = 3 // Number of consistent scans required

  RequestCameraPermission(
      cameraPermissionState,
      onPermissionGranted = {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Box(modifier = Modifier.weight(2f), contentAlignment = Alignment.TopStart) {
                CameraView(
                    imageCapture,
                    CodeBarAnalyzer { barcode ->
                      // Take the rave value of the barcode and convert it to a Long
                      val barcodeValue = barcode.rawValue?.toLongOrNull()
                      if (barcodeValue != null) {
                        // Add the barcode to the list of recent barcodes
                        recentBarcodes.add(barcodeValue)
                        // If the barcode has been scanned barcodeThreshold times, this is a
                        // probably? a valid barcode.
                        // When the user scan the barcode, it can result in an invalid barcode,
                        // because he is moving when doing so or other external factor.
                        // To assure that the barcode is valid, we need to check if the barcode has
                        // been scanned barcodeThreshold times. For now the number was set
                        // arbitrarily to 3.
                        if (recentBarcodes.count { it == barcodeValue } >= barcodeThreshold) {
                          // If the barcode is different from the last scanned barcode, fetch the
                          // ingredient
                          if (barcodeValue != lastScannedBarcode) {
                            ingredientViewModel.fetchIngredient(barcodeValue)
                            lastScannedBarcode = barcodeValue
                          }
                          recentBarcodes.clear()
                        }
                      } else {
                        Toast.makeText(context, "Invalid barcode", Toast.LENGTH_SHORT).show()
                      }
                    })
                BarCodeFrame()
              }
              IngredientDisplay(ingredient)
            }
      })
}

/** Display the camera view and the barcode frame */
@Composable
fun BarCodeFrame() {
  Box(
      modifier =
          Modifier.fillMaxSize()
              .padding(32.dp)
              .border(width = 4.dp, color = Color.White, shape = RoundedCornerShape(0.dp))
              .testTag("Barcode frame"))
}

/**
 * Display the ingredient information
 *
 * @param ingredient the ingredient to display
 */
@Composable
fun IngredientDisplay(ingredient: Ingredient?) {
  Row(modifier = Modifier.width(412.dp).height(146.dp).background(color = Color.White)) {
    if (ingredient != null) {
      Box(modifier = Modifier.width(146.dp).height(146.dp).background(color = Color.Black))

      Column {
        Text(
            text = ingredient.name,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp))
        Text(
            text = ingredient.barCode.toString(),
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp))
        Button(
            onClick = { /*TODO*/},
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)) {
              Text(text = "Add to Fridge")
            }
      }
    }
  }
}
