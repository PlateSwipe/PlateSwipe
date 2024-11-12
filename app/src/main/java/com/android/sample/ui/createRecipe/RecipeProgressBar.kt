package com.android.sample.ui.createRecipe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.ui.theme.Orange80
import com.android.sample.ui.theme.graySlate
import com.android.sample.ui.theme.lightCream

/**
 * Composable to display a dynamic progression bar for the recipe creation process.
 *
 * @param currentStep Current step in the recipe creation process.
 */
@Composable
fun RecipeProgressBar(currentStep: Int) {
  // List of icons representing each step in the recipe creation process
  val stepIcons =
      listOf(
          R.drawable.chef_s_hat, R.drawable.whisk, R.drawable.assignment, R.drawable.crop_original)

  // Ensure currentStep is within bounds
  val safeCurrentStep = currentStep.coerceIn(0, stepIcons.size - 1)

  Row(
      modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("progressBar"),
      verticalAlignment = Alignment.CenterVertically) {
        stepIcons.forEachIndexed { index, iconResId ->
          // Display each step icon with appropriate styling
          StepBox(
              iconResId = iconResId,
              isCompletedStep = index < safeCurrentStep,
              isCurrentStep = index == safeCurrentStep,
              index = index)

          // Display a line between steps, except after the last step
          if (index != stepIcons.lastIndex) {
            Box(
                modifier =
                    Modifier.height(2.dp)
                        .weight(1f)
                        .background(if (index < safeCurrentStep) Orange80 else graySlate)
                        .testTag("line_$index"))
          }
        }
      }
}

/**
 * Composable to display an individual step icon in the progression bar.
 *
 * @param iconResId Resource ID of the icon to display.
 * @param isCompletedStep Boolean indicating if the step is completed.
 * @param isCurrentStep Boolean indicating if this is the current step.
 * @param index Index of the step in the progression.
 */
@Composable
private fun StepBox(iconResId: Int, isCompletedStep: Boolean, isCurrentStep: Boolean, index: Int) {
  val iconBackgroundColor = if (isCompletedStep) Orange80 else lightCream
  val borderModifier = if (isCurrentStep) Modifier.border(2.dp, Orange80, CircleShape) else Modifier

  Box(
      modifier =
          Modifier.size(50.dp) // Circle size
              .clip(CircleShape)
              .then(borderModifier)
              .background(iconBackgroundColor)
              .testTag("step_$index"),
      contentAlignment = Alignment.Center) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = "Step ${index + 1}",
            modifier = Modifier.size(24.dp),
            tint = if (isCompletedStep) Color.White else Color.Unspecified)
      }
}
