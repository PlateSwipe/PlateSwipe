package com.android.sample.ui.offline

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.resources.C.Tag.TEST_TAG_OFFLINE_SCREEN_DESCRIPTION
import com.android.sample.resources.C.Tag.TEST_TAG_OFFLINE_SCREEN_IMAGE
import com.android.sample.resources.C.Tag.TEST_TAG_OFFLINE_SCREEN_TITLE
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.utils.PlateSwipeScaffold

/**
 * Composable function to display the offline screen.
 *
 * @param navigationActions The navigation actions used to handle navigation events.
 */
@Composable
fun OfflineScreen(navigationActions: NavigationActions) {
  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = navigationActions.currentRoute(),
      content = { padding -> OfflineScreenContent(padding) },
      showBackArrow = true)
}

/**
 * Composable function to display the content of the offline screen.
 *
 * @param padding The padding values to be applied to the content.
 */
@Composable
fun OfflineScreenContent(padding: PaddingValues) {
  Column(
      modifier = Modifier.padding(padding).padding(start = 16.dp, end = 16.dp).fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(64.dp))
        // Title
        Text(
            text = stringResource(R.string.you_are_offline),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag(TEST_TAG_OFFLINE_SCREEN_TITLE))
        Spacer(modifier = Modifier.height(8.dp))
        // Description
        Text(
            text =
                stringResource(
                    R.string
                        .this_feature_requires_an_internet_connection_please_check_your_connection_and_try_again),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag(TEST_TAG_OFFLINE_SCREEN_DESCRIPTION))
        Spacer(modifier = Modifier.height(16.dp))
        // Offline image
        Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {
          Image(
              painter = painterResource(id = R.drawable.round_wifi_off_24_black),
              contentDescription = stringResource(R.string.offline),
              modifier = Modifier.size(200.dp).testTag(TEST_TAG_OFFLINE_SCREEN_IMAGE))
        }
      }
}
