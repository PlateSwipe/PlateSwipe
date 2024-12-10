package com.android.sample.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.android.sample.R
import com.android.sample.resources.C.Tag.MINUTES_PER_HOUR

/**
 * Composable function to reformat the time.
 *
 * @param time The time to reformat in minutes.
 */
@Composable
fun reformatTime(time: Float): String {
  val timeInt = time.toInt()
  return if (timeInt > MINUTES_PER_HOUR) {
    val hours = timeInt / MINUTES_PER_HOUR
    val minutes = timeInt % MINUTES_PER_HOUR
    "$hours h $minutes ${stringResource(id = R.string.time_unit)}"
  } else {
    "$timeInt ${stringResource(id = R.string.time_unit)}"
  }
}
