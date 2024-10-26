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
  val stepIcons =
      listOf(
          R.drawable.chef_s_hat, // First step icon
          R.drawable.whisk, // Second step icon
          R.drawable.assignment, // Third step icon
          R.drawable.crop_original // Fourth step icon
          )

  Row(
      modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("progressBar"),
      verticalAlignment = Alignment.CenterVertically) {
        stepIcons.forEachIndexed { index, iconResId ->
          val isCompletedStep = index < currentStep
          val isCurrentStep = index == currentStep
          val iconBackgroundColor =
              when {
                isCompletedStep -> Orange80
                else -> lightCream
              }

          val borderModifier: Modifier =
              if (isCurrentStep) {
                Modifier.border(2.dp, Orange80, CircleShape)
              } else {
                Modifier
              }

          Box(
              modifier =
                  Modifier.size(50.dp) // Circle size
                      .clip(CircleShape)
                      .then(borderModifier)
                      .background(iconBackgroundColor)
                      .testTag("step_$index"), // Add testTag for each step
              contentAlignment = Alignment.Center) {
                // Display the icon, white tint for completed steps
                Icon(
                    painter = painterResource(id = iconResId),
                    contentDescription = "Step ${index + 1}",
                    modifier = Modifier.size(24.dp),
                    tint =
                        if (isCompletedStep) Color.White
                        else Color.Unspecified // Tint the icon white if the step is completed
                    )
              }

          // Add a horizontal line between the steps
          if (index != stepIcons.lastIndex) {
            Box(
                modifier =
                    Modifier.height(2.dp)
                        .weight(1f)
                        .background(if (isCompletedStep) Orange80 else graySlate)
                        .testTag("line_$index") // Add testTag for each horizontal line
                )
          }
        }
      }
}
