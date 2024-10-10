package com.android.sample.ui.testScreens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen

@Composable
fun AddRecipeScreen(navigationActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.testTag("addRecipeScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { destination -> navigationActions.navigateTo(destination) },
            tabList = LIST_TOP_LEVEL_DESTINATIONS,
            selectedItem = navigationActions.currentRoute())
      }) { innerPadding ->
        Text(text = "addRecipeScreenText", modifier = Modifier.padding(innerPadding))
        Button(
            onClick = { navigationActions.navigateTo(Screen.ADD_ITEM_CAM) },
            modifier = Modifier.padding(16.dp)) {
              Text("Take picture")
            }
      }
}
