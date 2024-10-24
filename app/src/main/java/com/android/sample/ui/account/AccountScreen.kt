package com.android.sample.ui.account

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.android.sample.model.user.UserViewModel
import com.android.sample.ui.EmptyScreen
import com.android.sample.ui.navigation.NavigationActions

@Composable
fun AccountScreen(navigationActions: NavigationActions, userViewModel: UserViewModel) {
  EmptyScreen(navigationActions, "Account Screen")
}
