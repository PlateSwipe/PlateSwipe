package com.android.sample.ui.createRecipe

import android.widget.NumberPicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.android.sample.R
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.resources.C.Dimension.PADDING_32
import com.android.sample.resources.C.Dimension.PADDING_8
import com.android.sample.resources.C.Tag.THIRD_STEP_OF_THE_CREATION
import com.android.sample.resources.C.TestTag.TimePicker.HOURS_LABEL
import com.android.sample.resources.C.TestTag.TimePicker.HOUR_PICKER
import com.android.sample.resources.C.TestTag.TimePicker.MINUTES_LABEL
import com.android.sample.resources.C.TestTag.TimePicker.MINUTE_PICKER
import com.android.sample.resources.C.TestTag.TimePicker.NEXT_BUTTON
import com.android.sample.resources.C.TestTag.TimePicker.TIME_PICKER_DESCRIPTION
import com.android.sample.resources.C.TestTag.TimePicker.TIME_PICKER_TITLE
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Typography
import com.android.sample.ui.utils.PlateSwipeButton
import com.android.sample.ui.utils.PlateSwipeScaffold

@Composable
fun TimePickerScreen(
    navigationActions: NavigationActions,
    createRecipeViewModel: CreateRecipeViewModel,
) {
  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = Route.CREATE_RECIPE,
      showBackArrow = true,
      content = { paddingValues ->
        TimePickerContent(
            currentStep = THIRD_STEP_OF_THE_CREATION,
            navigationActions = navigationActions,
            createRecipeViewModel = createRecipeViewModel,
            modifier = Modifier.fillMaxSize().padding(paddingValues))
      })
}

/**
 * Composable function for selecting the total time for the recipe.
 *
 * @param modifier Modifier to be applied to the screen.
 * @param currentStep The current step in the recipe creation process.
 * @param navigationActions Actions for navigating between screens.
 * @param createRecipeViewModel ViewModel for managing the recipe creation process.
 */
@Composable
fun TimePickerContent(
    modifier: Modifier = Modifier,
    currentStep: Int,
    navigationActions: NavigationActions,
    createRecipeViewModel: CreateRecipeViewModel
) {
  val totalMinutes = createRecipeViewModel.getRecipeTime()?.toIntOrNull() ?: 0
  val initialHours = totalMinutes / 60
  val initialMinutes = totalMinutes % 60

  val hours = remember { mutableStateOf(initialHours) }
  val minutes = remember { mutableStateOf(initialMinutes) }

  Box(modifier = modifier.padding(PADDING_8.dp), contentAlignment = Alignment.TopCenter) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top) {
          Spacer(modifier = Modifier.weight(1f))
          // Progress Bar
          RecipeProgressBar(currentStep = currentStep)

          Spacer(modifier = Modifier.weight(2f))

          // Title
          Text(
              text = stringResource(R.string.select_total_time),
              style = Typography.displayLarge,
              color = MaterialTheme.colorScheme.onPrimary,
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(horizontal = PADDING_32.dp)
                      .testTag(TIME_PICKER_TITLE),
              textAlign = TextAlign.Center)

          Spacer(modifier = Modifier.weight(1f))

          // Description
          Text(
              text = stringResource(R.string.select_time_description),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onPrimary,
              modifier =
                  Modifier.padding(horizontal = PADDING_32.dp).testTag(TIME_PICKER_DESCRIPTION),
              textAlign = TextAlign.Center)

          Spacer(modifier = Modifier.weight(3f))

          // WheelTimePicker
          WheelTimePicker(
              selectedHour = hours.value,
              selectedMinute = minutes.value,
              onTimeSelected = { selectedHour, selectedMinute ->
                hours.value = selectedHour
                minutes.value = selectedMinute
              })

          Spacer(modifier = Modifier.weight(6f))
        }

    // Next Step Button
    PlateSwipeButton(
        text = stringResource(R.string.next_step),
        modifier = Modifier.align(Alignment.BottomCenter).testTag(NEXT_BUTTON),
        onClick = {
          val totalTimeInMinutes = (hours.value * 60) + minutes.value
          createRecipeViewModel.updateRecipeTime(totalTimeInMinutes.toString())
          navigationActions.navigateTo(Screen.CREATE_RECIPE_ADD_IMAGE)
        })
  }
}

/**
 * A composable function that displays a time picker with separate selectors for hours and minutes.
 *
 * @param selectedHour The currently selected hour (0-23).
 * @param selectedMinute The currently selected minute (0-59).
 * @param onTimeSelected A callback triggered when the user selects a new time. It provides the
 *   selected hour and minute as parameters.
 *
 * This composable uses two `NumberPickerComposable` components to let users select hours and
 * minutes independently. A `Text` component displays a colon (`:`) separator between the hour and
 * minute pickers. The layout is responsive and adapts to various screen widths.
 *
 * Example Usage:
 * ```
 * WheelTimePicker(
 *     selectedHour = 12,
 *     selectedMinute = 30,
 *     onTimeSelected = { hour, minute ->
 *         println("Selected time: $hour:$minute")
 *     }
 * )
 * ```
 */
@Composable
fun WheelTimePicker(selectedHour: Int, selectedMinute: Int, onTimeSelected: (Int, Int) -> Unit) {
  Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = PADDING_32.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically) {
        // Hour Column
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
          // Label for Hour
          Text(
              text = stringResource(R.string.hours),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.testTag(HOURS_LABEL))

          // Hour Picker
          NumberPickerComposable(
              value = selectedHour,
              range = 0..23,
              onValueChange = { hour -> onTimeSelected(hour, selectedMinute) },
              modifier = Modifier.testTag(HOUR_PICKER))
        }

        // Colon Separator
        Text(
            text = ":",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.CenterVertically))

        // Minute Column
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
          // Label for Minute
          Text(
              text = stringResource(R.string.minutes),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.testTag(MINUTES_LABEL))

          // Minute Picker
          NumberPickerComposable(
              value = selectedMinute,
              range = 0..59,
              onValueChange = { minute -> onTimeSelected(selectedHour, minute) },
              modifier = Modifier.testTag(MINUTE_PICKER))
        }
      }
}

/**
 * A composable function that wraps a native Android `NumberPicker` for use in Jetpack Compose.
 *
 * @param value The currently selected value of the `NumberPicker`.
 * @param range The valid range of values (inclusive) that the `NumberPicker` can display.
 * @param onValueChange A callback triggered when the user selects a new value. It provides the
 *   selected value as a parameter.
 * @param modifier A `Modifier` to apply to the `NumberPicker` composable.
 *
 * This composable uses `AndroidView` to integrate the native Android `NumberPicker` widget into a
 * Compose layout. It exposes the current value as a semantic property for testing and
 * accessibility. The value is dynamically updated when the `value` parameter changes.
 *
 * Example Usage:
 * ```
 * NumberPickerComposable(
 *     value = 5,
 *     range = 0..10,
 *     onValueChange = { newValue ->
 *         println("New value: $newValue")
 *     }
 * )
 * ```
 */
@Composable
fun NumberPickerComposable(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
  AndroidView(
      modifier = modifier.semantics { contentDescription = value.toString() },
      factory = { context ->
        NumberPicker(context).apply {
          minValue = range.first
          maxValue = range.last
          this.value = value
          setOnValueChangedListener { _, _, newValue -> onValueChange(newValue) }
        }
      },
      update = { numberPicker ->
        // Update the NumberPicker's value dynamically if needed
        if (numberPicker.value != value) {
          numberPicker.value = value
        }
      })
}
