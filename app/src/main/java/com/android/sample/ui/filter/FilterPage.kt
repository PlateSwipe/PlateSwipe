package com.android.sample.ui.filter

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.filter.Difficulty
import com.android.sample.model.filter.FilterPageViewModel
import com.android.sample.model.filter.FloatRange
import com.android.sample.resources.C.Dimension.PADDING_16
import com.android.sample.resources.C.Dimension.PADDING_4
import com.android.sample.resources.C.Dimension.PADDING_8
import com.android.sample.resources.C.Dimension.SwipePage.BUTTON_ELEVATION
import com.android.sample.resources.C.Dimension.SwipePage.BUTTON_RADIUS
import com.android.sample.resources.C.Tag.Filter.UNINITIALIZED_BORN_VALUE
import com.android.sample.resources.C.Tag.FilterPage.MAX_ITEM_IN_ROW
import com.android.sample.resources.C.Tag.FilterPage.SLIDER_COLOR_ACTIVE
import com.android.sample.resources.C.Tag.FilterPage.SLIDER_COLOR_INACTIVE
import com.android.sample.resources.C.Tag.FilterPage.TIME_RANGE_MAX
import com.android.sample.resources.C.Tag.FilterPage.TIME_RANGE_MIN
import com.android.sample.resources.C.TestTag.FilterPage.TEST_TAG_CATEGORY
import com.android.sample.resources.C.TestTag.FilterPage.TEST_TAG_DIFFICULTY
import com.android.sample.resources.C.TestTag.FilterPage.TEST_TAG_TIME_RANGE_SLIDER
import com.android.sample.resources.C.TestTag.SwipePage.VIEW_RECIPE_BUTTON
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.utils.PlateSwipeScaffold
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Composable function to display the filter page.
 *
 * @param navigationActions The navigation actions.
 * @param filterViewModel The view model to manage the filter options.
 */
@Composable
fun FilterPage(navigationActions: NavigationActions, filterViewModel: FilterPageViewModel) {
  val selectedItem = navigationActions.currentRoute()
  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = selectedItem,
      showBackArrow = true,
      content = { paddingValues -> FilterBox(paddingValues, filterViewModel, navigationActions) })
}

/**
 * Composable function to display the filter options.
 *
 * @param paddingValues Padding values to apply to the composable.
 * @param filterViewModel The view model to manage the filter options.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FilterBox(
    paddingValues: PaddingValues,
    filterViewModel: FilterPageViewModel,
    navigationActions: NavigationActions
) {
  Column(
      modifier =
          Modifier.padding(paddingValues).fillMaxSize().verticalScroll(rememberScrollState())) {
        val filter by filterViewModel.tmpFilter.collectAsState()

        ValueRangeSlider(
            modifier = Modifier.testTag(TEST_TAG_TIME_RANGE_SLIDER),
            name = stringResource(id = R.string.time_range_name),
            min = TIME_RANGE_MIN,
            max = TIME_RANGE_MAX,
            unit = stringResource(id = R.string.time_unit),
            range = filterViewModel.timeRangeState,
            updateRange = { newMin, newMax ->
              // only update when time range is changed
              if (newMin.toInt() > TIME_RANGE_MIN.toInt() ||
                  newMax.toInt() < TIME_RANGE_MAX.toInt()) {
                filterViewModel.updateTimeRange(newMin, newMax)
              } else {
                filterViewModel.updateTimeRange(UNINITIALIZED_BORN_VALUE, UNINITIALIZED_BORN_VALUE)
              }
            })

        val difficultyLevels = listOf(Difficulty.Easy, Difficulty.Medium, Difficulty.Hard)
        val selectedDifficulty = filter.difficulty
        val emptyDifficulty = Difficulty.Undefined

        CheckboxGroup(
            title = stringResource(id = R.string.difficulty_name),
            items = difficultyLevels,
            selectedItem = MutableStateFlow(selectedDifficulty),
            onItemSelect = { newDifficulty -> filterViewModel.updateDifficulty(newDifficulty) },
            testTagPrefix = TEST_TAG_DIFFICULTY,
            emptyValue = emptyDifficulty)

        val categories = filterViewModel.categories.value
        val selectedCategory = filter.category
        val emptyCategory: String? = null

        CheckboxGroup(
            title = stringResource(id = R.string.category_name),
            items = categories,
            selectedItem = MutableStateFlow(selectedCategory),
            onItemSelect = { newCategory -> filterViewModel.updateCategory(newCategory) },
            testTagPrefix = TEST_TAG_CATEGORY,
            emptyValue = emptyCategory)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
          // Apply button to apply the changes made to the filters
          Button(
              onClick = {
                filterViewModel.applyChanges()
                navigationActions.navigateTo(Screen.SWIPE)
              },
              colors =
                  ButtonDefaults.buttonColors(
                      containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                      contentColor = MaterialTheme.colorScheme.onPrimary),
              elevation = ButtonDefaults.buttonElevation(BUTTON_ELEVATION.dp),
              shape = RoundedCornerShape(BUTTON_RADIUS.dp),
              modifier =
                  Modifier.padding(horizontal = PADDING_8.dp, vertical = (PADDING_4).dp)
                      .wrapContentSize()
                      .testTag(VIEW_RECIPE_BUTTON)) {
                Text(
                    text = stringResource(id = R.string.apply_filter),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
              }

          // Reset button to reset all filters to their default values
          Button(
              onClick = {
                filterViewModel.resetFilters()
                filterViewModel.applyChanges()
                navigationActions.navigateTo(Screen.SWIPE)
              },
              colors =
                  ButtonDefaults.buttonColors(
                      containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                      contentColor = MaterialTheme.colorScheme.background),
              elevation = ButtonDefaults.buttonElevation(BUTTON_ELEVATION.dp),
              shape = RoundedCornerShape(BUTTON_RADIUS.dp),
              modifier =
                  Modifier.padding(horizontal = PADDING_8.dp, vertical = (PADDING_4).dp)
                      .wrapContentSize()
                      .testTag(VIEW_RECIPE_BUTTON)) {
                Text(
                    text = stringResource(id = R.string.reset_filter),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.background,
                )
              }
        }
      }
}

/**
 * Composable function to reformat the time.
 *
 * @param time The time to reformat in minutes.
 */
fun reformatTime(time: Float): String {
  val timeInt = time.toInt()
  if (timeInt > 60) {
    val hours = timeInt / 60
    val minutes = timeInt % 60
    return "$hours h $minutes min"
  } else {
    return "$timeInt min"
  }
}

/**
 * Composable function to display a range slider.
 *
 * @param name The name of the slider.
 * @param min The minimum value of the slider.
 * @param max The maximum value of the slider.
 * @param unit The unit of the slider.
 * @param range The range of the slider.
 * @param updateRange The function to update the range.
 */
@SuppressLint("StateFlowValueCalledInComposition", "UnrememberedMutableState")
@Composable
fun ValueRangeSlider(
    modifier: Modifier,
    name: String,
    min: Float,
    max: Float,
    unit: String,
    range: StateFlow<FloatRange>,
    updateRange: (Float, Float) -> Unit,
) {
  // Update the range to the [min, max] if it is unbounded
  if (range.value.isLimited()) {
    range.value.update(min, max)
  }
  // Mutable state to store the slider position
  val currentRange by range.collectAsState()
  val rangeSlider = mutableStateOf(currentRange.min..currentRange.max)

  Column(
      modifier = Modifier.padding(PADDING_16.dp),
      horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start))

        Spacer(modifier = Modifier.height(PADDING_8.dp))

        // Display selected min and max values
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(
              reformatTime(rangeSlider.value.start),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onPrimary)
          Text(
              reformatTime(rangeSlider.value.endInclusive),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onPrimary)
        }

        Spacer(modifier = Modifier.height(PADDING_8.dp))

        // Range slider with minimum 0 and maximum 120 minutes
        RangeSlider(
            modifier = modifier.fillMaxWidth(),
            value = rangeSlider.value,
            onValueChange = {
              rangeSlider.value = it
              updateRange(it.start, it.endInclusive)
            },
            valueRange = min..max,
            colors =
                SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor =
                        MaterialTheme.colorScheme.primary.copy(alpha = SLIDER_COLOR_ACTIVE),
                    inactiveTrackColor =
                        MaterialTheme.colorScheme.onSurface.copy(alpha = SLIDER_COLOR_INACTIVE)))
      }
}

/**
 * Composable function to display a group of checkboxes.
 *
 * @param title The title of the group.
 * @param items The list of items to display.
 * @param selectedItem The selected item.
 * @param onItemSelect The function to call when an item is selected.
 * @param testTagPrefix The prefix for the test tag.
 * @param emptyValue The value representing an empty selection.
 */
@SuppressLint("MutableCollectionMutableState", "StateFlowValueCalledInComposition")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> CheckboxGroup(
    title: String,
    items: List<T>,
    selectedItem: StateFlow<T?>,
    onItemSelect: (T) -> Unit,
    testTagPrefix: String,
    emptyValue: T?
) {
  // Map items to checkbox states, observed in LaunchedEffect to sync with selectedItem
  val itemStates = remember {
    mutableStateMapOf(*items.associateWith { false }.toList().toTypedArray())
  }

  // Synchronize item states with selectedItem on updates
  LaunchedEffect(selectedItem) {
    itemStates.keys.forEach { itemStates[it] = false }
    if (selectedItem.value != emptyValue) {
      itemStates[selectedItem.value!!] = true
    }
  }

  Column(
      modifier = Modifier.padding(PADDING_16.dp),
      horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center,
            maxItemsInEachRow = MAX_ITEM_IN_ROW) {
              itemStates.forEach { (item, checked) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Spacer(modifier = Modifier.height(PADDING_4.dp))

                  Text(
                      text = item.toString(),
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onPrimary)
                  Checkbox(
                      modifier = Modifier.testTag("${testTagPrefix}Checkbox$item"),
                      checked = checked,
                      onCheckedChange = { isChecked ->
                        // Uncheck all other items and select the new item
                        itemStates.keys.forEach { itemStates[it] = false }
                        itemStates[item] = isChecked
                        onItemSelect(item)
                      })
                  Spacer(modifier = Modifier.height(PADDING_4.dp))
                }
              }
            }
      }
}
