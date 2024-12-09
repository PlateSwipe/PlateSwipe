package com.android.sample.ui.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.sample.model.filter.FloatRange
import com.android.sample.resources.C.Dimension.PADDING_16
import com.android.sample.resources.C.Dimension.PADDING_8
import com.android.sample.resources.C.Tag.FilterPage.SLIDER_COLOR_ACTIVE
import com.android.sample.resources.C.Tag.FilterPage.SLIDER_COLOR_INACTIVE
import kotlinx.coroutines.flow.StateFlow

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

        // Range slider with minimum 0 and maximum 1440 minutes
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