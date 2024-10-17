package com.android.sample.ui.account

import androidx.compose.runtime.Composable
import com.android.sample.ui.EmptyScreen
import com.android.sample.ui.navigation.NavigationActions

@Composable
fun AccountScreen(navigationActions: NavigationActions) {
  EmptyScreen(navigationActions = navigationActions, title = "Account Screen")
}
