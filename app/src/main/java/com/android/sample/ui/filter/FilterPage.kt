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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sample.model.filter.Difficulty
import com.android.sample.model.filter.FloatRange
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPage(
    navigationActions: NavigationActions,
    recipesViewModel: RecipesViewModel = viewModel(factory = RecipesViewModel.Factory)
) {
  val selectedItem = navigationActions.currentRoute()
  Scaffold(
      modifier = Modifier.fillMaxWidth(),
      topBar = {
        TopAppBar(
            title = { Text("PlateSwipe") },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            modifier = Modifier.testTag("topBar"),
            navigationIcon = {
              IconButton(onClick = { navigationActions.goBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Return button")
              }
            })
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tab -> navigationActions.navigateTo(tab) },
            tabList = LIST_TOP_LEVEL_DESTINATIONS,
            selectedItem = selectedItem)
      }) { paddingValues ->
        FilterBox(paddingValues, recipesViewModel)
      }
}

/**
 * Composable function to display the filter options.
 *
 * @param paddingValues Padding values to apply to the composable.
 * @param recipesViewModel The view model to manage recipes.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FilterBox(paddingValues: PaddingValues, recipesViewModel: RecipesViewModel) {
  val filter by recipesViewModel.filter.collectAsState()
  Column(
      modifier =
          Modifier.padding(paddingValues).fillMaxSize().verticalScroll(rememberScrollState())) {
        ValueRangeSlider(
            modifier = Modifier.testTag("timeRangeSlider"),
            name = "Time",
            min = 0f,
            max = 200f,
            unit = "min",
            range = recipesViewModel.filter.value.timeRange,
            updateRange = { newMin, newMax -> recipesViewModel.updateTimeRange(newMin, newMax) })
        ValueRangeSlider(
            modifier = Modifier.testTag("priceRangeSlider"),
            name = "Price",
            min = 0f,
            max = 100f,
            unit = "$",
            range = filter.priceRange,
            updateRange = { newMin, newMax -> recipesViewModel.updatePriceRange(newMin, newMax) })
        CheckboxDifficulty(recipesViewModel = recipesViewModel)
        CheckboxCategories(recipesViewModel = recipesViewModel)
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
 * Composable function to display checkboxes for difficulty levels.
 *
 * @param recipesViewModel The view model to manage recipes.
 */
@SuppressLint("MutableCollectionMutableState", "StateFlowValueCalledInComposition")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CheckboxDifficulty(recipesViewModel: RecipesViewModel) {
  // List of difficulty names

  val difficultyLevels = remember {
    mutableStateMapOf(
        Difficulty.Easy to false, Difficulty.Medium to false, Difficulty.Hard to false)
  }
  val orderedDifficulties = listOf(Difficulty.Easy, Difficulty.Medium, Difficulty.Hard)

  if (recipesViewModel.filter.value.difficulty != Difficulty.Undefined) {
    difficultyLevels[recipesViewModel.filter.value.difficulty] = true
  }

  Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
        text = "Difficulty",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.align(Alignment.Start))

    // Display each checkbox with the corresponding difficulty name
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.Center,
        maxItemsInEachRow = 3) {
          orderedDifficulties.forEach { difficulty ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Spacer(modifier = Modifier.height(4.dp))

              Text(
                  text = difficulty.toString(),
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onPrimary) // Display the difficulty name
              Checkbox(
                  modifier = Modifier.testTag("difficultyCheckbox${difficulty}"),
                  checked = difficultyLevels[difficulty]!!,
                  onCheckedChange = { isChecked ->
                    // Update the individual state in boxStates
                    difficultyLevels.forEach { (i, _) ->
                      if (i != difficulty) {
                        difficultyLevels[i] = false
                      }
                    }
                    difficultyLevels[difficulty] = isChecked
                    recipesViewModel.updateDifficulty(difficulty)
                  })
              Spacer(modifier = Modifier.height(4.dp))
            }
          }
        }
  }
}

/**
 * Composable function to display checkboxes for categories.
 *
 * @param recipesViewModel The view model to manage recipes.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CheckboxCategories(recipesViewModel: RecipesViewModel) {

  Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
        text = "Category",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.align(Alignment.Start))

    // List of category names
    val categoriesMapping = remember {
      mutableStateMapOf(
          *recipesViewModel.categories.value.associateWith { false }.toList().toTypedArray())
    }

    if (recipesViewModel.filter.value.category != null) {
      categoriesMapping[recipesViewModel.filter.value.category!!] = true
    }

    // Display each checkbox with the corresponding difficulty name
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.Center,
        maxItemsInEachRow = 3) {
          categoriesMapping.forEach { (category, checked) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Spacer(modifier = Modifier.height(4.dp))

              Text(
                  text = category,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onPrimary) // Display the difficulty name
              Checkbox(
                  modifier = Modifier.testTag("difficultyCheckbox${category}"),
                  checked = checked,
                  onCheckedChange = { isChecked ->
                    // Update the individual state in boxStates
                    categoriesMapping.forEach { (i, _) ->
                      if (i != category) {
                        categoriesMapping[i] = false
                      }
                    }
                    categoriesMapping[category] = isChecked
                    recipesViewModel.updateCategory(category)
                  })
              Spacer(modifier = Modifier.height(4.dp))
            }
          }
        }
  }
}
