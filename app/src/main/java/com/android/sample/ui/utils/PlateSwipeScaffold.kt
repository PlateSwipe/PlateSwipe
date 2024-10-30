package com.android.sample.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions

/**
 * PlateSwipeScaffold is a custom Scaffold that is used in the PlateSwipe app. It has a custom top
 * bar and bottom navigation bar.
 *
 * @param navigationActions: the navigation actions object to navigate between screens.
 * @param selectedItem: The selected item in the bottom navigation bar.
 * @param content: The content of the screen.
 * @param showBackArrow: Boolean to show the back arrow in the top bar. Should be false for every
 *   top level screens.
 */
@Composable
fun PlateSwipeScaffold(
    navigationActions: NavigationActions,
    selectedItem: String,
    content: @Composable (PaddingValues) -> Unit,
    showBackArrow: Boolean = true
) {
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("plateSwipeScaffold"),
      topBar = { PlateSwipeTopBar(navigationActions = navigationActions, showBackArrow) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tab -> navigationActions.navigateTo(tab) },
            tabList = LIST_TOP_LEVEL_DESTINATIONS,
            selectedItem = selectedItem)
      }) { paddingValues ->
        content(paddingValues)
      }
}

@Composable
private fun PlateSwipeTopBar(navigationActions: NavigationActions, showBackArrow: Boolean = true) {
  Row(
      modifier = Modifier.fillMaxWidth().height(40.dp).testTag("topBar"),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        Row(
            modifier = Modifier.weight(1f),
        ) {
          Spacer(modifier = Modifier.weight(1f))
          if (showBackArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Navigate back",
                modifier =
                    Modifier.size(26.dp).testTag("backArrowIcon").clickable {
                      navigationActions.goBack()
                    },
                tint = MaterialTheme.colorScheme.onSecondary,
            )
          }
          Spacer(modifier = Modifier.weight(7f))
        }

        Text(
            text = stringResource(R.string.plate_swipe_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.testTag("topBarTitle"),
            color = MaterialTheme.colorScheme.onPrimary)

        Spacer(modifier = Modifier.weight(1f))
      }
}
