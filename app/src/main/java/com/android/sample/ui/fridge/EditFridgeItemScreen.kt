package com.android.sample.ui.fridge

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.android.sample.R
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.Dimension.CameraScanCodeBarScreen.INGREDIENT_DISPLAY_IMAGE_BORDER_RADIUS
import com.android.sample.resources.C.Dimension.EditFridgeItemScreen.EPOCH_LITERAL
import com.android.sample.resources.C.Dimension.EditFridgeItemScreen.TEXT_FONT_SIZE
import com.android.sample.resources.C.Dimension.EditFridgeItemScreen.TITLE_FONT_SIZE
import com.android.sample.resources.C.Dimension.EditFridgeItemScreen.TITLE_LINE_HEIGHT
import com.android.sample.resources.C.Dimension.PADDING_16
import com.android.sample.resources.C.Dimension.PADDING_32
import com.android.sample.resources.C.Dimension.RecipeOverview.COUNTER_ROUND_CORNER
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL_URL
import com.android.sample.resources.C.TestTag.SwipePage.RECIPE_IMAGE_1
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.utils.Counter
import com.android.sample.ui.utils.PlateSwipeButton
import com.android.sample.ui.utils.PlateSwipeScaffold
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun EditFridgeItemScreen(navigationActions: NavigationActions, userViewModel: UserViewModel) {
  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = navigationActions.currentRoute(),
      content = { paddingValues ->
        EditComposable(paddingValues, navigationActions, userViewModel)
      },
      showBackArrow = true)
}

/**
 * Composable function to edit a fridge item
 *
 * @param paddingValues The padding values to be applied to the composable.
 * @param navigationActions The navigation actions to be applied to the composable.
 * @param userViewModel The view model to be applied to the composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun EditComposable(
    paddingValues: PaddingValues,
    navigationActions: NavigationActions,
    userViewModel: UserViewModel
) {

  val ingredient = userViewModel.ingredientList.collectAsState().value[0].first
  var quantity by remember { mutableIntStateOf(1) }
  var expirationDate by remember { mutableStateOf(LocalDate.now()) }

  val showDatePickerDialog = remember { mutableStateOf(false) }

  Column(
      modifier = Modifier.fillMaxSize().padding(paddingValues).padding(PADDING_16.dp),
      verticalArrangement = Arrangement.spacedBy(PADDING_16.dp)) {
        // Ingredient Name
        Box(modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)) {
          Text(
              text = ingredient.name,
              modifier = Modifier.padding(PADDING_16.dp),
              style = MaterialTheme.typography.titleLarge,
              lineHeight = TITLE_LINE_HEIGHT.sp,
              fontSize = TITLE_FONT_SIZE.sp,
              color = MaterialTheme.colorScheme.onPrimary)
        }

        // Ingredient Image
        Image(
            painter =
                rememberAsyncImagePainter(
                    model = ingredient.images[PRODUCT_FRONT_IMAGE_THUMBNAIL_URL]),
            contentDescription = stringResource(R.string.recipe_image),
            alignment = Alignment.Center,
            modifier =
                Modifier.fillMaxSize()
                    .weight(4f)
                    .testTag(RECIPE_IMAGE_1)
                    .clip(RoundedCornerShape(INGREDIENT_DISPLAY_IMAGE_BORDER_RADIUS.dp)),
            contentScale = ContentScale.Fit,
        )

        // Quantity Adjustment
        Row(
            modifier = Modifier.fillMaxSize().weight(2f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PADDING_16.dp)) {
              Text(
                  text = "Quantity (x ${ingredient.quantity}):",
                  modifier = Modifier.weight(1f),
                  style = MaterialTheme.typography.bodyMedium,
                  fontSize = TEXT_FONT_SIZE.sp,
                  color = MaterialTheme.colorScheme.onPrimary)
              Counter(
                  count = quantity,
                  onCounterChange = { quantity = it },
                  modifier = Modifier.background(Color.Transparent))
            }

        // Expiration Date Picker
        Row(
            modifier = Modifier.fillMaxSize().weight(2f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PADDING_16.dp)) {
              Text(
                  text = stringResource(R.string.expiration_date),
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier.weight(1f),
                  fontSize = TEXT_FONT_SIZE.sp,
                  color = MaterialTheme.colorScheme.onPrimary)
              // Button to display the date picker dialog
              Button(
                  shape = RoundedCornerShape(COUNTER_ROUND_CORNER.dp),
                  onClick = { showDatePickerDialog.value = true },
                  colors =
                      ButtonDefaults.buttonColors(MaterialTheme.colorScheme.onSecondaryContainer),
              ) {
                Text(
                    text =
                        expirationDate.format(
                            DateTimeFormatter.ofPattern(stringResource(R.string.date_pattern))),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White)
              }
            }

        Spacer(modifier = Modifier.size(PADDING_32.dp))

        // Save Button
        PlateSwipeButton(
            stringResource(R.string.save),
            modifier = Modifier.align(Alignment.CenterHorizontally).zIndex(1f),
            onClick = {
              userViewModel.updateIngredientFromFridge(ingredient, quantity, expirationDate, true)
              navigationActions.navigateTo(Screen.FRIDGE)
            })

        // Expiration Date Picker Dialog
        if (showDatePickerDialog.value) {
          DatePickerDialog(
              onDismissRequest = { showDatePickerDialog.value = false },
              confirmButton = {
                TextButton(onClick = { showDatePickerDialog.value = false }) {
                  Text(
                      text = stringResource(R.string.confirm),
                      style = MaterialTheme.typography.bodyMedium,
                      fontSize = TEXT_FONT_SIZE.sp,
                      color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
              },
              dismissButton = {
                TextButton(onClick = { showDatePickerDialog.value = false }) {
                  Text(
                      stringResource(R.string.cancel),
                      style = MaterialTheme.typography.bodyMedium,
                      fontSize = TEXT_FONT_SIZE.sp,
                      color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
              }) {
                val state =
                    rememberDatePickerState(
                        initialSelectedDateMillis = expirationDate.toEpochDay() * EPOCH_LITERAL)

                // Date Picker
                DatePicker(state = state)

                // Dispose the selected date
                DisposableEffect(state) {
                  onDispose {
                    state.selectedDateMillis?.let { millis ->
                      expirationDate = LocalDate.ofEpochDay(millis / EPOCH_LITERAL)
                    }
                  }
                }
              }
        }
      }
}
