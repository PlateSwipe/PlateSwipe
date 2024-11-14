package com.android.sample.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.resources.C.Dimension.CameraScanCodeBarScreen.BACK_ARROW_ICON_SIZE
import com.android.sample.resources.C.Dimension.CameraScanCodeBarScreen.CHEF_HAT_ICON_END_PADDING
import com.android.sample.resources.C.Dimension.CameraScanCodeBarScreen.CHEF_HAT_ICON_SIZE
import com.android.sample.resources.C.Dimension.CameraScanCodeBarScreen.TOP_BAR_HEIGHT
import com.android.sample.resources.C.Dimension.CameraScanCodeBarScreen.TOP_BAR_TITLE_FONT_SIZE
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.TestTag.Utils.BACK_ARROW_ICON
import com.android.sample.resources.C.TestTag.Utils.CHEF_HAT_ICON
import com.android.sample.resources.C.TestTag.Utils.PLATESWIPE_SCAFFOLD
import com.android.sample.resources.C.TestTag.Utils.TOP_BAR
import com.android.sample.resources.C.TestTag.Utils.TOP_BAR_TITLE
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
      modifier = Modifier.fillMaxSize().testTag(PLATESWIPE_SCAFFOLD),
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
      modifier =
          Modifier.fillMaxWidth()
              .height(TOP_BAR_HEIGHT.dp)
              .padding(SMALL_PADDING.dp)
              .background(color = MaterialTheme.colorScheme.background)
              .testTag(TOP_BAR),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(modifier = Modifier.weight(1f)) {
      if (showBackArrow) {
        IconButton(
            onClick = { navigationActions.goBack() },
            modifier = Modifier.testTag(BACK_ARROW_ICON)) {
              Icon(
                  imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                  contentDescription = "Back",
                  modifier = Modifier.size(BACK_ARROW_ICON_SIZE.dp),
                  tint = MaterialTheme.colorScheme.onPrimary)
            }
      }
    }

    Image(
        painter = painterResource(id = R.drawable.chef_s_hat),
        contentDescription = "Chef's hat",
        modifier =
            Modifier.size(CHEF_HAT_ICON_SIZE.dp)
                .padding(end = CHEF_HAT_ICON_END_PADDING.dp)
                .testTag(CHEF_HAT_ICON),
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary))

    Text(
        text = stringResource(id = R.string.plate_swipe_title),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.testTag(TOP_BAR_TITLE),
        fontSize = TOP_BAR_TITLE_FONT_SIZE.sp,
        color = MaterialTheme.colorScheme.onPrimary,
    )

    Spacer(modifier = Modifier.weight(1f))
  }
}
