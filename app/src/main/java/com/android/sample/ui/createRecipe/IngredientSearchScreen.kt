package com.android.sample.ui.createRecipe

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.animation.LoadingCook
import com.android.sample.model.ingredient.Ingredient
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
    ingredientViewModel: IngredientViewModel,
) {
  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = navigationActions.currentRoute(),
      showBackArrow = true,
      content = { paddingValues ->
        val listIngredient = ingredientViewModel.searchingIngredientList.collectAsState()
        var showConfirmation by remember { mutableStateOf(false) }
        var selectedIngredient by remember { mutableStateOf<Ingredient?>(null) }
        var isLoading by remember { mutableStateOf(false) }

        LaunchedEffect(listIngredient) { isLoading = false }

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.testTag("DraggableItem").fillMaxSize().padding(paddingValues)) {
              Row(
                  modifier = Modifier.fillMaxWidth().padding(PADDING.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center) {
                    Spacer(modifier = Modifier.width(PADDING.dp).weight(1f))
                    SearchBar(
                        modifier = Modifier.padding(PADDING.dp).weight(4f).testTag("DraggableItem"),
                        onValueChange = { query ->
                          ingredientViewModel.fetchIngredientByName(query)
                        },
                        onDebounce = { query ->
                          if (query.isNotEmpty()) {
                            isLoading = true
                            ingredientViewModel.fetchIngredientByName(query)
                          }
                        })
                    Icon(
                        painter = painterResource(id = R.drawable.scanner),
                        modifier =
                            Modifier.weight(1f).size(40.dp).clickable {
                              navigationActions.navigateTo(Screen.CAMERA_SCAN_CODE_BAR)
                            },
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Scanner Icon")
                  }
              Row(
                  modifier = Modifier.fillMaxWidth().padding(PADDING.dp),
                  horizontalArrangement = Arrangement.Start,
              ) {
                Text(
                    text = "Result",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.onPrimary)
              }

              Column(
                  modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                  verticalArrangement = Arrangement.Center,
                  horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isLoading) {
                      LoadingCook(modifier = Modifier.weight(1f), size = 150)
                      Spacer(modifier = Modifier.weight(1f))
                    } else {
                      for (ingredient in listIngredient.value) {
                        IngredientItem(
                            ingredient = ingredient,
                            onClick = {
                              selectedIngredient = ingredient
                              showConfirmation = true
                            })
                      }
                    }
                  }

              if (showConfirmation && selectedIngredient != null) {
                ConfirmationPopUp(
                    onConfirm = {
                      ingredientViewModel.addBarCodeIngredient(selectedIngredient!!)
                      selectedIngredient = null
                      showConfirmation = false
                      navigationActions.navigateTo(Screen.CREATE_RECIPE_LIST_INGREDIENTS)
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
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary)
      },
      text = {
        Text(
            text = "Do you want to add this ingredient to your recipe?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary)
      },
      confirmButton = {
        TextButton(onClick = onConfirm) {
          Text(
              text = "Add to Recipe",
              style = MaterialTheme.typography.titleSmall,
              color = MaterialTheme.colorScheme.onPrimary)
        }
      },
      dismissButton = {
        TextButton(onClick = onDismiss) {
          Text(
              text = "Cancel",
              style = MaterialTheme.typography.titleSmall,
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
