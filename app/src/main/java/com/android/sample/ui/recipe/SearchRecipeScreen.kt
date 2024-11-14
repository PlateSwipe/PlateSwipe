package com.android.sample.ui.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.recipe.Recipe
import com.android.sample.resources.C.Tag.PADDING
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.TestTag.SearchScreen.SEARCH_LIST
import com.android.sample.resources.C.TestTag.SearchScreen.SEARCH_SCREEN
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.utils.*

@Composable
fun SearchRecipeScreen(navigationActions: NavigationActions, recipelist: List<Recipe>) {
  val selectedItem = navigationActions.currentRoute()

  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = selectedItem,
      showBackArrow = false,
      content = { paddingValues ->
        SearchScreenContent(
            paddingValues = paddingValues,
            recipeList = recipelist,
            navigationActions = navigationActions)
      })
}

@Composable
fun SearchScreenContent(
    paddingValues: PaddingValues,
    recipeList: List<Recipe>,
    navigationActions: NavigationActions
) {
  Column(
      modifier =
          Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).testTag(SEARCH_SCREEN),
      verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
      horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically) {
              SearchBar(modifier = Modifier.weight(1f), isSingleLine = true)
              Spacer(modifier = Modifier.size(SMALL_PADDING.dp))
              Icon(
                  painter = painterResource(id = R.drawable.filter),
                  contentDescription = null,
                  modifier =
                      Modifier.testTag("filter icon").weight(0.1f).clickable {
                        navigationActions.navigateTo(Screen.FILTER)
                      })
            }

        Spacer(modifier = Modifier.size(PADDING.dp))

        RecipeList(
            list = recipeList,
            onRecipeSelected = { navigationActions.navigateTo(Screen.OVERVIEW_RECIPE) },
            topCornerButton = { recipe -> TopCornerLikeButton(recipe) },
            modifier =
                Modifier.fillMaxSize().padding(top = 16.dp, bottom = 16.dp).testTag(SEARCH_LIST))
      }
}
