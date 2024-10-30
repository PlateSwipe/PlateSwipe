package com.android.sample.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.lightCream

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlateSwipeTopBar(navigationActions: NavigationActions, showBackArrow: Boolean = true) {
  TopAppBar(
      title = {
        Box(
            modifier = Modifier.fillMaxWidth().testTag("topBarBox"),
            contentAlignment = Alignment.Center) {
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.testTag("topBarRow")) {
                    Image(
                        painter = painterResource(id = R.drawable.chef_s_hat),
                        contentDescription = "Chef's hat",
                        modifier = Modifier.size(35.dp).padding(end = 8.dp).testTag("chefHatIcon"),
                        contentScale = ContentScale.Fit)

                    Text(
                        text = stringResource(id = R.string.plate_swipe_title),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.testTag("topBarTitle"))
                  }
            }
      },
      navigationIcon = {
        if (showBackArrow) {
          IconButton(
              onClick = { navigationActions.goBack() },
              modifier = Modifier.testTag("backArrowIcon")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.width(30.dp).height(30.dp))
              }
        }
      },
      colors = TopAppBarDefaults.topAppBarColors(containerColor = lightCream),
      modifier = Modifier.fillMaxWidth().testTag("topBar"))
}
