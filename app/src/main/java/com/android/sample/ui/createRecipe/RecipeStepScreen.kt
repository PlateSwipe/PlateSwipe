package com.android.sample.ui.createRecipe

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import com.android.sample.resources.C.Tag.BASE_PADDING
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.utils.PlateSwipeButton
import com.android.sample.ui.utils.PlateSwipeScaffold

/**
 * Composable function that displays the screen for a specific step in the recipe creation process.
 *
 * @param title The title text to be displayed at the top of the screen.
 * @param subtitle The subtitle text to be displayed below the title.
 * @param buttonText The text to be displayed on the action button.
 * @param onButtonClick The callback to be invoked when the action button is clicked.
 * @param navigationActions Actions for navigating between screens.
 * @param currentStep The current step in the recipe creation process.
 * @param modifier Modifier to be applied to the screen.
 */
@SuppressLint("SuspiciousIndentation")
@Composable
fun RecipeStepScreen(
    title: String,
    subtitle: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    navigationActions: NavigationActions,
    currentStep: Int,
    modifier: Modifier = Modifier,
    isEditing: Boolean = false
) {
  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = if (isEditing) Route.ACCOUNT else Route.CREATE_RECIPE,
      showBackArrow = true,
      content = { paddingValues ->
        Box(modifier = modifier.fillMaxSize().padding(paddingValues).padding(BASE_PADDING)) {
          Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Top,
              modifier = Modifier.fillMaxSize()) {
                // Progress bar to show the current step
                RecipeProgressBar(currentStep = currentStep)

                Spacer(modifier = Modifier.weight(0.1f))

                // Title text
                Text(
                    text = title,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = BASE_PADDING * 2),
                    textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.weight(0.05f))

                // Subtitle text
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = BASE_PADDING * 2).zIndex(1f),
                    textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.weight(0.05f))

                // Row to hold the chef image and change its position horizontally
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically) {
                      Spacer(modifier = Modifier.weight(0.05f))
                      ChefImage(modifier = Modifier.weight(0.9f).zIndex(-1f))
                      Spacer(modifier = Modifier.weight(0.05f))
                    }
                Spacer(modifier = Modifier.weight(0.1f))
              }

          // Action button
          PlateSwipeButton(
              buttonText,
              modifier = Modifier.align(Alignment.BottomCenter).testTag("NextStepButton"),
              onClick = onButtonClick)
        }
      })
}
