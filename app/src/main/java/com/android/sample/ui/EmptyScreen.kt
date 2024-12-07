package com.android.sample.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.android.sample.resources.C.Tag.RECIPE_LIST_CORNER_RADIUS
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_IMAGE_TEST_TAG
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
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary)
                Text(
                    "Work in progress... Stay tuned!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary)


                  Image(
                      painter = rememberAsyncImagePainter("file:///data/user/0/com.android.sample/files/Wiener%20Waffelimage_front_thumb_url.jpg"),
                      contentDescription = null,
                      modifier =
                      Modifier.aspectRatio(1f)
                          .fillMaxSize()
                          .clip(RoundedCornerShape(RECIPE_LIST_CORNER_RADIUS.dp))
                          .testTag(RECIPE_IMAGE_TEST_TAG))
            }
      }
}
}
