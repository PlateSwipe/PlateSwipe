package com.android.sample.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.sample.resources.C.Dimension.CameraScanCodeBarScreen.BOTTOM_BAR_HEIGHT
import com.android.sample.ui.theme.lightCream

@Composable
fun BottomNavigationMenu(
    onTabSelect: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>,
    selectedItem: String
) {
  NavigationBar(
      modifier =
          Modifier.fillMaxWidth().height(BOTTOM_BAR_HEIGHT.dp).testTag("bottomNavigationMenu"),
      containerColor = lightCream,
      content = {
        tabList.forEach { tab ->
          NavigationBarItem(
              icon = {
                Icon(
                    painter = painterResource(tab.iconId),
                    contentDescription = null,
                    modifier = Modifier.testTag("icon" + tab.textId))
              },
              selected = tab.route == selectedItem,
              onClick = { onTabSelect(tab) },
              modifier = Modifier.clip(RoundedCornerShape(50.dp)).testTag("tab" + tab.textId))
        }
      },
  )
}
