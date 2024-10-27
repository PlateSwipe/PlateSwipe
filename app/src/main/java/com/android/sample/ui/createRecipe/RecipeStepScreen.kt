package com.android.sample.ui.createRecipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.android.sample.resources.C.Tag.BASE_PADDING
import com.android.sample.resources.C.Tag.BUTTON_HEIGHT
import com.android.sample.resources.C.Tag.BUTTON_WIDTH
import com.android.sample.resources.C.Tag.CHEF_IMAGE_HEIGHT
import com.android.sample.resources.C.Tag.CHEF_IMAGE_WIDTH
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.lightCream
import com.android.sample.ui.topbar.MyAppBar

@Composable
fun RecipeStepScreen(
    title: String,
    subtitle: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    navigationActions: NavigationActions,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
  // Screen scaling based on width
  val configuration = LocalConfiguration.current
  val screenWidth = configuration.screenWidthDp.dp
  val scaleFactor = screenWidth / CHEF_IMAGE_WIDTH * 0.5f
  val chefImageWidth = CHEF_IMAGE_WIDTH * scaleFactor
  val chefImageHeight = CHEF_IMAGE_HEIGHT * scaleFactor

  Scaffold(topBar = { MyAppBar(onBackClick = { navigationActions.goBack() }) }) { paddingValues ->
    Box(modifier = modifier.fillMaxSize().padding(paddingValues).padding(BASE_PADDING)) {
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Top,
          modifier = Modifier.fillMaxSize()) {
            // Progress bar to show the current step
            RecipeProgressBar(currentStep = currentStep)

            Spacer(modifier = Modifier.height(BASE_PADDING * 3))

            // Title text
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.fillMaxWidth().padding(horizontal = BASE_PADDING * 2),
                textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(BASE_PADDING / 2))

            // Subtitle text
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier =
                    Modifier.padding(horizontal = BASE_PADDING * 2).width(260.dp).height(63.dp),
                textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(BASE_PADDING * 3))

            // Action button with dynamic text
            Button(
                onClick = onButtonClick,
                modifier =
                    Modifier.width(BUTTON_WIDTH)
                        .height(BUTTON_HEIGHT)
                        .background(color = lightCream, shape = RoundedCornerShape(size = 4.dp))
                        .align(Alignment.CenterHorizontally)
                        .zIndex(1f) // Ensures button is on top if overlapping with ChefImage
                ) {
                  Text(buttonText)
                }
          }

      // Chef image at the bottom left
      ChefImage(
          modifier =
              Modifier.align(Alignment.BottomStart)
                  .padding(start = BASE_PADDING, bottom = BASE_PADDING)
                  .size(width = chefImageWidth, height = chefImageHeight) // Proportional scaling
                  .zIndex(0f) // Ensures button overlaps if they collide
          )
    }
  }
}
