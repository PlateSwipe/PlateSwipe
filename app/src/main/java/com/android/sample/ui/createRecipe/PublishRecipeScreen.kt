package com.android.sample.ui.createRecipe

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import com.android.sample.R
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.Tag.CHEF_IMAGE_DESCRIPTION
import com.android.sample.resources.C.Tag.CHEF_IN_EGG_ORIGINAL_RATIO
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Typography
import com.android.sample.ui.utils.PlateSwipeButton
import com.android.sample.ui.utils.PlateSwipeScaffold

@Composable
fun PublishRecipeScreen(
    navigationActions: NavigationActions,
    createRecipeViewModel: CreateRecipeViewModel,
    userViewModel: UserViewModel,
    isEditing: Boolean = false
) {
  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = if (isEditing) Route.ACCOUNT else Route.CREATE_RECIPE,
      showBackArrow = true,
      content = { paddingValues ->
        PublishRecipeContent(
            navigationActions,
            createRecipeViewModel,
            userViewModel,
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            isEditing)
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
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier,
    isEditing: Boolean
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
        Spacer(modifier = Modifier.weight(1f))
        // Display the done text
        Text(
            text = stringResource(R.string.done_text),
            style = Typography.titleLarge,
            modifier = Modifier.weight(2f).zIndex(1f).testTag("DoneText"),
            color = MaterialTheme.colorScheme.onPrimary)
        Spacer(modifier = Modifier.weight(8f))

        // Display the chef in egg image
        Image(
            painter = painterResource(id = R.drawable.chef_image_in_egg1),
            contentDescription = CHEF_IMAGE_DESCRIPTION,
            modifier =
                Modifier.weight(1f)
                    .fillMaxSize(1f)
                    .aspectRatio(CHEF_IN_EGG_ORIGINAL_RATIO)
                    .zIndex(-1f)
                    .testTag("ChefImage"))

        Spacer(modifier = Modifier.weight(6f))

        // Display the publish button

        PlateSwipeButton(
            onClick = {
              createRecipeViewModel.publishRecipe(
                  isEditing,
                  onSuccess = { recipe ->
                    if (isEditing) {
                      userViewModel.replaceRecipeInUserCreatedRecipes(
                          createRecipeViewModel.getId(), recipe)
                    } else {
                      userViewModel.addRecipeToUserCreatedRecipes(recipe)
                    }
                    navigationActions.navigateTo(Screen.SWIPE)
                  },
                  onFailure = { exception ->
                    Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
                  })
            },
            modifier = Modifier.testTag("PublishButton"),
            text = stringResource(R.string.publish_recipe_button))

        Spacer(modifier = Modifier.weight(1f))
      }
}
