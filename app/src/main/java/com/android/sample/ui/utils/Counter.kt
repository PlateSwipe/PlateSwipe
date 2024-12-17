package com.android.sample.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.resources.C.Dimension.Counter.ADD
import com.android.sample.resources.C.Dimension.Counter.COUNTER_FONT_SIZE
import com.android.sample.resources.C.Dimension.Counter.COUNTER_MIN_MAX_SIZE
import com.android.sample.resources.C.Dimension.Counter.COUNTER_TEXT_SIZE
import com.android.sample.resources.C.Dimension.Counter.MAX_VALUE
import com.android.sample.resources.C.Dimension.Counter.MIN_VALUE
import com.android.sample.resources.C.Dimension.Counter.NUMBER
import com.android.sample.resources.C.Dimension.Counter.RECIPE_COUNTER_PADDING
import com.android.sample.resources.C.Dimension.Counter.REMOVE
import com.android.sample.resources.C.Dimension.RecipeOverview.COUNTER_ROUND_CORNER

/**
 * Display of the counter to change the number of servings.
 *
 * @param count: The current number of servings
 * @param onCounterChange: The function to change the number of servings
 */
@Composable
fun Counter(
    modifier: Modifier = Modifier,
    count: Int,
    onCounterChange: (Int) -> Unit,
    minValue: Int = MIN_VALUE,
    maxValue: Int = MAX_VALUE
) {
  var counter by remember { mutableIntStateOf(count) }
  Row(
      modifier =
          modifier.background(
              MaterialTheme.colorScheme.onSecondaryContainer,
              shape = RoundedCornerShape(COUNTER_ROUND_CORNER.dp)),
      verticalAlignment = Alignment.CenterVertically) {
        // - button
        Button(
            onClick = {
              if (counter > minValue) {
                onCounterChange(counter - 1)
                counter--
              }
            },
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    contentColor = MaterialTheme.colorScheme.background),
            modifier = Modifier.size(COUNTER_MIN_MAX_SIZE.dp).testTag(REMOVE),
            contentPadding = PaddingValues(RECIPE_COUNTER_PADDING.dp)) {
              Text(
                  stringResource(R.string.counter_min),
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.background)
            }

        // Display the count
        Text(
            text = counter.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.background,
            textAlign = TextAlign.Center,
            fontSize = COUNTER_FONT_SIZE.sp,
            modifier = Modifier.testTag(NUMBER).width(COUNTER_TEXT_SIZE.dp))

        // + button
        Button(
            onClick = {
              if (counter < maxValue) {
                onCounterChange(counter + 1)
                counter++
              }
            },
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    contentColor = MaterialTheme.colorScheme.background),
            modifier = Modifier.size(COUNTER_MIN_MAX_SIZE.dp).testTag(ADD),
            contentPadding = PaddingValues(RECIPE_COUNTER_PADDING.dp)) {
              Text(
                  stringResource(R.string.counter_max),
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.background)
            }
      }
}
