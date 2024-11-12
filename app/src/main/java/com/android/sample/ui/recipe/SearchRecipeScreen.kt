package com.android.sample.ui.recipeOverview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.model.recipe.Recipe
import com.android.sample.resources.C.TestTag.SearchScreen.SEARCH_SCREEN
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.utils.*
import com.android.sample.ui.theme.*

@Composable
fun SearchRecipeScreen(navigationActions: NavigationActions, recipelist: List<Recipe>) {
    val selectedItem = navigationActions.currentRoute()

    PlateSwipeScaffold(navigationActions = navigationActions, selectedItem = selectedItem, showBackArrow = false,
        content = {paddingValues -> SearchScreenContent(paddingValues = paddingValues, recipeList = recipelist, navigationActions = navigationActions)})
}

@Composable
fun SearchScreenContent(paddingValues: PaddingValues, recipeList: List<Recipe>, navigationActions: NavigationActions) {
    Column(
        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).testTag(SEARCH_SCREEN) ,
        verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,) {
            SearchBar(modifier =
            Modifier.shadow(
                elevation = 4.dp, spotColor = C4, ambientColor = C4)
                .border(width = 1.dp, color = C4)
                .width(329.dp)
                .height(64.dp)
                .background(color = lightCream)
                .padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 16.dp).testTag("searchBar"))

            RecipeList(
                list = recipeList,
                onRecipeSelected = { _ ->
                    navigationActions.navigateTo("Overview Recipe Screen")
                },
                topCornerButton = { recipe -> TopCornerLikeButton(recipe)
                },
                modifier = Modifier.fillMaxSize().padding(top = 16.dp, bottom = 16.dp).testTag("SearchRecipeList")
            )
        }
}


