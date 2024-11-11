package com.android.sample.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.resources.C.TestTag.Utils.TEST_TAG
import com.android.sample.ui.theme.tagBackground

/**
 * Composable for the Tags of the recipe
 *
 * @param tag - Tag Name
 */
@Composable
fun Tag(tag: String) {
  if (tag != "") {
    Box(
        modifier =
            Modifier.background(
                    color = tagBackground,
                    shape = RoundedCornerShape(16.dp)) // Smooth rounded corners
                .padding(horizontal = 12.dp, vertical = 4.dp) // Padding for inside spacing
        ) {
          Text(
              modifier = Modifier.testTag(TEST_TAG),
              text = tag,
              fontSize = 14.sp,
              color = Color.White // Text color
              )
        }
  }
}
