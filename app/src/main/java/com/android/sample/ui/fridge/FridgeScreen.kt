package com.android.sample.ui.fridge

import androidx.compose.runtime.Composable
import com.android.sample.ui.EmptyScreen
import com.android.sample.ui.navigation.NavigationActions

@Composable
fun FridgeScreen(navigationActions: NavigationActions) {
  EmptyScreen(navigationActions = navigationActions, title = "Fridge Screen")
}
