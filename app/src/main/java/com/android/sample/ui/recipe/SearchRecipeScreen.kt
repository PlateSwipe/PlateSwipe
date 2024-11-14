package com.android.sample.ui.recipe

import androidx.compose.runtime.Composable
import com.android.sample.ui.EmptyScreen
import com.android.sample.ui.navigation.NavigationActions

@Composable
fun SearchRecipeScreen(navigationActions: NavigationActions) {
  EmptyScreen(navigationActions = navigationActions, title = "Search Recipe Screen")
}
