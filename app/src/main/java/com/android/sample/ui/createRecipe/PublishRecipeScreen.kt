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
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.resources.C.Tag.CHEF_IMAGE_DESCRIPTION
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Typography
import com.android.sample.ui.topbar.MyAppBar

@Composable
fun PublishRecipeScreen(
    navigationActions: NavigationActions,
    createRecipeViewModel: CreateRecipeViewModel,
    modifier: Modifier = Modifier
) {
  Scaffold(
      topBar = { MyAppBar(onBackClick = { navigationActions.goBack() }, showBackButton = true) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tab -> navigationActions.navigateTo(tab) },
            tabList = LIST_TOP_LEVEL_DESTINATIONS,
            selectedItem = navigationActions.currentRoute())
      },
      modifier = modifier.fillMaxSize()) { paddingValues ->
        PublishRecipeContent(
            navigationActions,
            createRecipeViewModel,
            modifier = Modifier.padding(paddingValues).fillMaxSize())
      }
}

@Composable
fun PublishRecipeContent(
    navigationActions: NavigationActions,
    createRecipeViewModel: CreateRecipeViewModel,
    modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val publishError = createRecipeViewModel.publishError.collectAsState(initial = null).value

  publishError?.let {
    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    createRecipeViewModel
        .clearPublishError()
  }

  Column(
      modifier = modifier,
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = stringResource(R.string.done_text),
            style = Typography.titleLarge.copy(fontSize = 70.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(0.4f).padding(bottom = 16.dp).testTag("DoneText"))

        Image(
            painter = painterResource(id = R.drawable.chef_image_in_egg),
            contentDescription = CHEF_IMAGE_DESCRIPTION,
            modifier =
                Modifier.weight(2f)
                    .fillMaxWidth(1f)
                    .aspectRatio(0.5f)
                    .testTag("ChefImage"))

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
              createRecipeViewModel.publishRecipe()
              navigationActions.navigateAndClearStack(
                  screen = Screen.CREATE_RECIPE, clearUpToRoute = Route.CREATE_RECIPE)
            },
            colors =
                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(8.dp),
            modifier =
                Modifier.padding(horizontal = 16.dp)
                    .fillMaxWidth(0.7f)
                    .height(48.dp)
                    .weight(0.3f)
                    .testTag("PublishButton")) {
              Text(
                  text = stringResource(R.string.publish_recipe_button),
                  style = Typography.bodyMedium,
                  fontWeight = FontWeight.Bold)
            }
      }
}
