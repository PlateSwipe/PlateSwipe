package com.android.sample.ui.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPage(navigationActions: NavigationActions) {
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
        FilterBox(paddingValues)
      }
}

@Composable
fun FilterBox(paddingValues: PaddingValues) {
  Column(modifier = Modifier.padding(paddingValues)) {
    TimeRangeSlider("Time", 0f, 120f)
    TimeRangeSlider("Price", 0f, 500f)
  }
}

@Composable
fun TimeRangeSlider(name: String, min: Float, max: Float) {
  // State variables to store the selected range
  var sliderPosition by remember { mutableStateOf(0f..100f) }
  Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
        text = name,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.align(Alignment.Start))

    Spacer(modifier = Modifier.height(8.dp))

    // Display selected min and max values
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text("${sliderPosition.start.toInt()}", fontSize = 14.sp)
      Text("${sliderPosition.endInclusive.toInt()}", fontSize = 14.sp)
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Range slider with minimum 0 and maximum 120 minutes
    RangeSlider(
        value = sliderPosition,
        onValueChange = { sliderPosition = it },
        valueRange = min..max,
        colors =
            SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)))
  }
}
