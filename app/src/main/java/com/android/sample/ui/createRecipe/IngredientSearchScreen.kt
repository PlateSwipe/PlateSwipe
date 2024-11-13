package com.android.sample.ui.createRecipe

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.resources.C.Tag.PADDING
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.utils.PlateSwipeScaffold
import com.android.sample.ui.utils.SearchBar

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun IngredientSearchScreen(
    navigationActions: NavigationActions,
    ingredientViewModel: IngredientViewModel,
    createRecipeViewModel: CreateRecipeViewModel
) {
  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = navigationActions.currentRoute(),
      showBackArrow = true,
      content = { paddingValues ->
        val listIngredient = ingredientViewModel.searchingIngredientList.collectAsState()
        var showConfirmation by remember { mutableStateOf(false) }
        var selectedIngredient by remember { mutableStateOf<Ingredient?>(null) }

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

              for (ingredient in listIngredient.value) {
                IngredientItem(
                    ingredient = ingredient,
                    onClick = {
                      selectedIngredient = ingredient
                      showConfirmation = true
                    })
              }

              if (showConfirmation && selectedIngredient != null) {
                ConfirmationPopUp(
                    onConfirm = {
                      ingredientViewModel.addBarCodeIngredient(selectedIngredient!!)
                      selectedIngredient = null
                      showConfirmation = false
                    },
                    onDismiss = {
                      selectedIngredient = null
                      showConfirmation = false
                    })
              }
            }
      })
}

@Composable
fun ConfirmationPopUp(onConfirm: () -> Unit, onDismiss: () -> Unit) {
  AlertDialog(
      onDismissRequest = onDismiss,
      title = {
        Text(
            text = "Add to Recipe?",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary)
      },
      text = {
        Text(
            text = "Do you want to add this ingredient to your recipe?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary)
      },
      confirmButton = {
        TextButton(onClick = onConfirm) {
          Text(
              text = "Add to Recipe",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onPrimary)
        }
      },
      dismissButton = {
        TextButton(onClick = onDismiss) {
          Text(
              text = "Cancel",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onPrimary)
        }
      },
      containerColor = MaterialTheme.colorScheme.secondary,
      modifier =
          Modifier.fillMaxWidth()
              .padding(PADDING.dp)
              .shadow(
                  elevation = 4.dp, // Adjust elevation as desired
                  clip = true // Ensures background respects the shadow's rounded corners
                  ))
}

@Composable
fun IngredientItem(ingredient: Ingredient, onClick: () -> Unit) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(PADDING.dp)
              .shadow(
                  elevation = 4.dp, // Adjust elevation as desired
                  shape = RoundedCornerShape(8.dp),
                  clip = true // Ensures background respects the shadow's rounded corners
                  )
              .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(8.dp))
              .clickable { onClick() }) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(PADDING.dp)) {
              Text(
                  text = ingredient.name,
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.onPrimary,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis)
              ingredient.quantity?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary)
              }
            }
      }
}
