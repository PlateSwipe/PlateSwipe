package com.android.sample.ui.createRecipe

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.resources.C.Tag.PADDING
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.utils.PlateSwipeScaffold
import com.android.sample.ui.utils.SearchBar

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun IngredientSearchScreen(
    navigationActions: NavigationActions,
    ingredientViewModel: IngredientViewModel
) {
  val ingredientList by ingredientViewModel.ingredientList.collectAsState()
  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = navigationActions.currentRoute(),
      showBackArrow = true,
      content = { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier.testTag("DraggableItem")
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())) {
              SearchBar(
                  modifier = Modifier.padding(PADDING.dp).testTag("DraggableItem"),
                  onValueChange = { query -> ingredientViewModel.fetchIngredientByName(query) })

              Button(
                  onClick = { navigationActions.navigateTo(Screen.CAMERA_SCAN_CODE_BAR) },
                  modifier = Modifier.fillMaxWidth(1f / 2f).fillMaxHeight(1f / 10f)) {
                    Text("Add with camera")
                  }

              for (ingredients in ingredientList) {
                // Display the ingredient
                Text(text = ingredients.name)
              }
            }
      })
}
