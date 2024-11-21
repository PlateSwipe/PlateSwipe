package com.android.sample.ui.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.android.sample.R
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.Dimension.AccountScreen.ACCOUNT_SCREEN_SELECTED_LIST_HEIGHT
import com.android.sample.resources.C.Dimension.AccountScreen.ACCOUNT_SCREEN_SELECTED_LIST_SEPARATOR_FILL_MAX_WIDTH
import com.android.sample.resources.C.Dimension.AccountScreen.ACCOUNT_SCREEN_SELECTED_LIST_SEPARATOR_THICKNESS
import com.android.sample.resources.C.Dimension.AccountScreen.ACCOUNT_SCREEN_SELECTED_LIST_SPACER_ELEMENTS
import com.android.sample.resources.C.Dimension.AccountScreen.ACCOUNT_SCREEN_SELECTED_LIST_WEIGHT
import com.android.sample.resources.C.Tag.AccountScreen.PROFILE_PICTURE_CONTENT_DESCRIPTION
import com.android.sample.resources.C.TestTag.AccountScreen.CREATED_RECIPES_BUTTON_TEST_TAG
import com.android.sample.resources.C.TestTag.AccountScreen.LIKED_RECIPES_BUTTON_TEST_TAG
import com.android.sample.resources.C.TestTag.AccountScreen.PROFILE_PICTURE_TEST_TAG
import com.android.sample.resources.C.TestTag.AccountScreen.USERNAME_TEST_TAG
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.navigation.TopLevelDestinations
import com.android.sample.ui.utils.PlateSwipeScaffold
import com.android.sample.ui.utils.RecipeList
import com.android.sample.ui.utils.TopCornerLikeButton

@Composable
fun AccountScreen(navigationActions: NavigationActions, userViewModel: UserViewModel) {
  LaunchedEffect(Unit) { userViewModel.getCurrentUser() }

  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = TopLevelDestinations.ACCOUNT.route,
      showBackArrow = false,
      content = { padding ->
        val userName = userViewModel.userName.collectAsState()
        val profilePictureUrl = userViewModel.profilePictureUrl.collectAsState()

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Spacer(modifier = Modifier.height(16.dp))

          ProfilePicture(profilePictureUrl.value, modifier = Modifier.weight(.4f))

          Text(
              text = userName.value ?: stringResource(R.string.account_screen_default_user_name),
              modifier = Modifier.weight(.1f).testTag(USERNAME_TEST_TAG),
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onPrimary)

          ListSelection(navigationActions, userViewModel, modifier = Modifier.weight(.8f))
        }
      })
}

@Composable
private fun ListSelectionButton(
    modifier: Modifier,
    title: String,
    onClick: () -> Unit,
    isSelected: Boolean = false
) {
  Box(
      modifier = modifier.clickable { onClick() },
      contentAlignment = Alignment.Center,
  ) {
    Text(
        modifier = Modifier,
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color =
            if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onTertiary)
  }
}

@Composable
private fun ListSelection(
    navigationActions: NavigationActions,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
  val likedRecipes = userViewModel.likedRecipes.collectAsState()
  val createdRecipes = userViewModel.createdRecipes.collectAsState()

  var selectedList by remember { mutableStateOf(likedRecipes) }
  var selectedListIndex by remember { mutableIntStateOf(0) }

  Column(
      modifier = modifier,
      verticalArrangement =
          Arrangement.spacedBy(ACCOUNT_SCREEN_SELECTED_LIST_SPACER_ELEMENTS.dp, Alignment.Top),
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Row(modifier = Modifier.height(ACCOUNT_SCREEN_SELECTED_LIST_HEIGHT.dp).fillMaxWidth()) {
      ListSelectionButton(
          modifier =
              Modifier.weight(ACCOUNT_SCREEN_SELECTED_LIST_WEIGHT)
                  .testTag(LIKED_RECIPES_BUTTON_TEST_TAG),
          onClick = {
            selectedList = likedRecipes
            selectedListIndex = 0
          },
          title = stringResource(R.string.account_screen_liked_recipe_button_title),
          isSelected = selectedListIndex == 0)
      ListSelectionButton(
          modifier =
              Modifier.weight(ACCOUNT_SCREEN_SELECTED_LIST_WEIGHT)
                  .testTag(CREATED_RECIPES_BUTTON_TEST_TAG),
          onClick = {
            selectedList = createdRecipes
            selectedListIndex = 1
          },
          title = stringResource(R.string.account_screen_created_recipe_button_title),
          isSelected = selectedListIndex == 1)
    }

    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(ACCOUNT_SCREEN_SELECTED_LIST_SEPARATOR_FILL_MAX_WIDTH),
        thickness = ACCOUNT_SCREEN_SELECTED_LIST_SEPARATOR_THICKNESS.dp,
        color = MaterialTheme.colorScheme.onTertiary)

    RecipeList(
        list = selectedList.value,
        modifier = Modifier.weight(ACCOUNT_SCREEN_SELECTED_LIST_WEIGHT).fillMaxWidth(),
        onRecipeSelected = { recipe ->
          userViewModel.selectRecipe(recipe)
          navigationActions.navigateTo(Screen.OVERVIEW_RECIPE_ACCOUNT)
        },
        topCornerButton = { recipe ->
          if (selectedListIndex == 0) {
            TopCornerLikeButton(recipe, userViewModel, true)
          } else {
            TopCornerLikeButton(recipe, userViewModel, false)
          }
        })
  }
}

@Composable
private fun ProfilePicture(profilePictureUrl: String?, modifier: Modifier = Modifier) {
  Box(modifier = modifier.aspectRatio(1f).clip(CircleShape), contentAlignment = Alignment.Center) {
    Image(
        painter =
            if (profilePictureUrl.isNullOrEmpty()) {
              painterResource(id = R.drawable.account)
            } else {
              rememberAsyncImagePainter(model = profilePictureUrl)
            },
        contentDescription = PROFILE_PICTURE_CONTENT_DESCRIPTION,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize().testTag(PROFILE_PICTURE_TEST_TAG))
  }
}
