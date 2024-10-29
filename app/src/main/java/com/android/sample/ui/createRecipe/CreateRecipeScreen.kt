package com.android.sample.ui.createRecipe

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.android.sample.resources.C.Tag.INITIAL_RECIPE_STEP
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.topbar.MyAppBar

@Composable
fun CreateRecipeScreen(navigationActions: NavigationActions) {
  var currentStep by remember { mutableStateOf(INITIAL_RECIPE_STEP) }

  Scaffold(
      topBar = { MyAppBar(onBackClick = { navigationActions.goBack() }) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tab -> navigationActions.navigateTo(tab) },
            tabList = LIST_TOP_LEVEL_DESTINATIONS,
            selectedItem = navigationActions.currentRoute())
      }) { paddingValues ->
        CreateRecipeContent(
            navigationActions = navigationActions,
            currentStep = currentStep,
            paddingValues = paddingValues)
      }
}

@Composable
fun CreateRecipeContent(
    navigationActions: NavigationActions,
    currentStep: Int,
    paddingValues: PaddingValues
) {
  // Render the main screen for the recipe name step
  RecipeNameScreen(
      currentStep = currentStep,
      navigationActions = navigationActions,
      modifier = Modifier.fillMaxSize().padding(paddingValues))
}