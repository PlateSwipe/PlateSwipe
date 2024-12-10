package com.android.sample.ui.filter

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.filter.Difficulty
import com.android.sample.model.filter.FilterPageViewModel
import com.android.sample.resources.C.Dimension.PADDING_4
import com.android.sample.resources.C.Dimension.PADDING_8
import com.android.sample.resources.C.Dimension.SwipePage.BUTTON_ELEVATION
import com.android.sample.resources.C.Dimension.SwipePage.BUTTON_RADIUS
import com.android.sample.resources.C.Tag.Filter.UNINITIALIZED_BORN_VALUE
import com.android.sample.resources.C.Tag.FilterPage.TIME_RANGE_MAX
import com.android.sample.resources.C.Tag.FilterPage.TIME_RANGE_MIN
import com.android.sample.resources.C.TestTag.FilterPage.TEST_TAG_CATEGORY
import com.android.sample.resources.C.TestTag.FilterPage.TEST_TAG_DIFFICULTY
import com.android.sample.resources.C.TestTag.FilterPage.TEST_TAG_TIME_RANGE_SLIDER
import com.android.sample.resources.C.TestTag.SwipePage.VIEW_RECIPE_BUTTON
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.utils.CheckboxGroup
import com.android.sample.ui.utils.PlateSwipeScaffold
import com.android.sample.ui.utils.ValueRangeSlider
import kotlinx.coroutines.flow.MutableStateFlow

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
        }
      }
}
