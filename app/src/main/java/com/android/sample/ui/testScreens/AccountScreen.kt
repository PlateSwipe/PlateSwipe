package com.android.sample.ui.testScreens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions

@Composable
fun AccountScreen(navigationActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.testTag("accountScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { destination -> navigationActions.navigateTo(destination) },
            tabList = LIST_TOP_LEVEL_DESTINATIONS,
            selectedItem = navigationActions.currentRoute())
      }) { innerPadding ->
        Text(text = "accountScreenText", modifier = Modifier.padding(innerPadding))
      }
}
