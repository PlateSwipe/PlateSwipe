package com.android.sample.ui.recipe

import androidx.compose.runtime.Composable
import com.android.sample.ui.EmptyScreen
import com.android.sample.ui.navigation.NavigationActions

@Composable
fun CreateRecipeScreen(navigationActions: NavigationActions) {
  EmptyScreen(navigationActions = navigationActions, title = "Create Recipe Screen")
}
