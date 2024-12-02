package com.android.sample.ui.account

import androidx.compose.runtime.Composable
import com.android.sample.model.user.UserViewModel
import com.android.sample.ui.EmptyScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.TopLevelDestinations
import com.android.sample.ui.utils.PlateSwipeScaffold

@Composable
fun EditAccountScreen(navigationActions: NavigationActions, userViewModel: UserViewModel) {
  PlateSwipeScaffold(
      navigationActions,
      TopLevelDestinations.ACCOUNT.route,
      showBackArrow = true,
      content = { EmptyScreen(navigationActions, "Work in progress") })
}
