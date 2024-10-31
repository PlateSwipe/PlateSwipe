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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.model.filter.Difficulty
import com.android.sample.model.filter.FilterPageViewModel
import com.android.sample.model.filter.FloatRange
import com.android.sample.resources.C.Tag.CATEGORY_NAME
import com.android.sample.resources.C.Tag.DIFFICULTY_NAME
import com.android.sample.resources.C.Tag.MAX_ITEM_IN_ROW
import com.android.sample.resources.C.Tag.PRICE_RANGE_MAX
import com.android.sample.resources.C.Tag.PRICE_RANGE_MIN
import com.android.sample.resources.C.Tag.PRICE_RANGE_NAME
import com.android.sample.resources.C.Tag.PRICE_RANGE_UNIT
import com.android.sample.resources.C.Tag.TIME_RANGE_MAX
import com.android.sample.resources.C.Tag.TIME_RANGE_MIN
import com.android.sample.resources.C.Tag.TIME_RANGE_NAME
import com.android.sample.resources.C.Tag.TIME_RANGE_UNIT
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.utils.PlateSwipeScaffold

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
      content = { paddingValues -> FilterBox(paddingValues, filterViewModel) })
}

/**
 * Composable function to display the filter options.
 *
 * @param paddingValues Padding values to apply to the composable.
 * @param filterViewModel The view model to manage the filter options.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FilterBox(paddingValues: PaddingValues, filterViewModel: FilterPageViewModel) {
  val filter by filterViewModel.filter.collectAsState()
  Column(
      modifier =
          Modifier.padding(paddingValues).fillMaxSize().verticalScroll(rememberScrollState())) {
        ValueRangeSlider(
            modifier = Modifier.testTag("timeRangeSlider"),
            name = TIME_RANGE_NAME,
            min = TIME_RANGE_MIN,
            max = TIME_RANGE_MAX,
            unit = TIME_RANGE_UNIT,
            range = filter.timeRange,
            updateRange = { newMin, newMax -> filterViewModel.updateTimeRange(newMin, newMax) })
        ValueRangeSlider(
            modifier = Modifier.testTag("priceRangeSlider"),
            name = PRICE_RANGE_NAME,
            min = PRICE_RANGE_MIN,
            max = PRICE_RANGE_MAX,
            unit = PRICE_RANGE_UNIT,
            range = filter.priceRange,
            updateRange = { newMin, newMax -> filterViewModel.updatePriceRange(newMin, newMax) })

        val difficultyLevels = listOf(Difficulty.Easy, Difficulty.Medium, Difficulty.Hard)
        val selectedDifficulty = filter.difficulty
        val emptyDifficulty = Difficulty.Undefined

        CheckboxGroup(
            title = DIFFICULTY_NAME,
            items = difficultyLevels,
            selectedItem = selectedDifficulty,
            onItemSelect = { newDifficulty -> filterViewModel.updateDifficulty(newDifficulty) },
            testTagPrefix = "difficulty",
            emptyValue = emptyDifficulty)

        val categories = filterViewModel.categories.value
        val selectedCategory = filter.category
        val emptyCategory: String? = null

        CheckboxGroup(
            title = CATEGORY_NAME,
            items = categories,
            selectedItem = selectedCategory,
            onItemSelect = { newCategory -> filterViewModel.updateCategory(newCategory) },
            testTagPrefix = "category",
            emptyValue = emptyCategory)
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
@Composable
fun ValueRangeSlider(
    modifier: Modifier,
    name: String,
    min: Float,
    max: Float,
    unit: String,
    range: FloatRange,
    updateRange: (Float, Float) -> Unit,
) {
  // Update the range to the [min, max] if it is unbounded
  if (range.isLimited()) {
    range.update(min, max)
  }
  // Mutable state to store the slider position

  var sliderPosition by remember { mutableStateOf(range.min..range.max) }
  Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
        text = name,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.align(Alignment.Start))

    Spacer(modifier = Modifier.height(8.dp))

    // Display selected min and max values
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text(
          "${sliderPosition.start.toInt()} " + unit,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onPrimary)
      Text(
          "${sliderPosition.endInclusive.toInt()} " + unit,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onPrimary)
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Range slider with minimum 0 and maximum 120 minutes
    RangeSlider(
        modifier = modifier.fillMaxWidth(),
        value = sliderPosition,
        onValueChange = {
          sliderPosition = it
          updateRange(it.start, it.endInclusive)
        },
        valueRange = min..max,
        colors =
            SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)))
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
    selectedItem: T?,
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
    if (selectedItem != emptyValue) {
      itemStates[selectedItem!!] = true
    }
  }

  Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
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
              Spacer(modifier = Modifier.height(4.dp))

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
              Spacer(modifier = Modifier.height(4.dp))
            }
          }
        }
  }
}
