package com.android.sample.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions

@Composable
fun EmptyScreen(navigationActions: NavigationActions, title: String) {
  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tab -> navigationActions.navigateTo(tab) },
            tabList = LIST_TOP_LEVEL_DESTINATIONS,
            selectedItem = navigationActions.currentRoute())
      }) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text("Work in progress... Stay tuned!", style = MaterialTheme.typography.bodyMedium)
              }
            }
      }
}
