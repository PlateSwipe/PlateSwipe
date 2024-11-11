package com.android.sample.ui.createRecipe

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.android.sample.R
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.resources.C.Tag.CHEF_IMAGE_DESCRIPTION
import com.android.sample.resources.C.Tag.CHEF_IN_EGG_ORIGINAL_RATIO
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Typography
import com.android.sample.ui.utils.PlateSwipeScaffold

@Composable
fun PublishRecipeScreen(
    navigationActions: NavigationActions,
    createRecipeViewModel: CreateRecipeViewModel,
) {
  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = Route.CREATE_RECIPE,
      showBackArrow = true,
      content = { paddingValues ->
        PublishRecipeContent(
            navigationActions,
            createRecipeViewModel,
            modifier = Modifier.padding(paddingValues).fillMaxSize())
      })
}

/**
 * Composable function that displays the content for publishing a recipe.
 *
 * @param navigationActions Actions for navigating between screens.
 * @param createRecipeViewModel ViewModel for managing the recipe creation process.
 * @param modifier Modifier to be applied to the content.
 */
@Composable
fun PublishRecipeContent(
    navigationActions: NavigationActions,
    createRecipeViewModel: CreateRecipeViewModel,
    modifier: Modifier = Modifier
) {
  // Get the current context
  val context = LocalContext.current

  // Collect the publish error state
  val publishStatus = createRecipeViewModel.publishStatus.collectAsState(initial = null).value

  // Show a toast message if there is a publish error
  publishStatus?.let {
    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    createRecipeViewModel.clearPublishError()
  }

  Column(
      modifier = modifier,
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween) {
        Spacer(modifier = Modifier.weight(0.05f))
        // Display the done text
        Text(
            text = stringResource(R.string.done_text),
            style = Typography.titleLarge,
            modifier = Modifier.weight(0.4f).padding(bottom = 16.dp).testTag("DoneText"))
        Spacer(modifier = Modifier.weight(0.1f))

        // Display the chef in egg image
        Image(
            painter = painterResource(id = R.drawable.chef_image_in_egg),
            contentDescription = CHEF_IMAGE_DESCRIPTION,
            modifier =
                Modifier.weight(1f)
                    .fillMaxSize(1f)
                    .aspectRatio(CHEF_IN_EGG_ORIGINAL_RATIO)
                    .zIndex(-1f)
                    .testTag("ChefImage"))

        Spacer(modifier = Modifier.weight(0.1f))

        // Display the publish button
        Button(
            onClick = {
              createRecipeViewModel.publishRecipe()
              navigationActions.navigateTo(Screen.SWIPE)
            },
            colors =
                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(8.dp),
            modifier =
                Modifier.padding(horizontal = 16.dp)
                    .fillMaxWidth(0.7f)
                    .weight(0.2f)
                    .testTag("PublishButton")) {
              Text(
                  text = stringResource(R.string.publish_recipe_button),
                  style = Typography.bodyMedium,
                  fontWeight = FontWeight.Bold)
            }
      }
}
