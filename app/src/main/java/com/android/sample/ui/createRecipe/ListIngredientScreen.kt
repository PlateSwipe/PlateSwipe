package com.android.sample.ui.createRecipe

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.android.sample.R
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.resources.C.Tag.BUTTON_HEIGHT
import com.android.sample.resources.C.Tag.BUTTON_WIDTH
import com.android.sample.resources.C.Tag.PADDING
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Typography
import com.android.sample.ui.theme.lightCream
import com.android.sample.ui.utils.PlateSwipeScaffold

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun IngredientListScreen(
    navigationActions: NavigationActions,
    ingredientViewModel: IngredientViewModel,
    createRecipeViewModel: CreateRecipeViewModel
) {
  val ingredientList by ingredientViewModel.ingredientList.collectAsState()
  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = navigationActions.currentRoute(),
      showBackArrow = true,
      content = { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxSize().padding(paddingValues).testTag("DraggableItem")) {
              Column(Modifier.fillMaxWidth()) {
                Text(
                    text = createRecipeViewModel.recipeBuilder.getName(),
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 30.sp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(PADDING.dp).testTag("recipeName"))
                Row(
                    modifier =
                        Modifier.fillMaxWidth() // Changed to fill the available width
                            .padding(start = PADDING.dp, end = PADDING.dp, top = PADDING.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Ingredients list",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                            color = MaterialTheme.colorScheme.onPrimary)
                      }
                      Icon(
                          painter = painterResource(id = R.drawable.add),
                          contentDescription = "Add",
                          modifier =
                              Modifier.testTag("addIngredientIcon").clickable {
                                ingredientViewModel.clearSearch()
                                navigationActions.navigateTo(
                                    Screen.CREATE_RECIPE_SEARCH_INGREDIENTS)
                              })
                    }
              }

              // Column for ingredients with scroll and weight for flexible space distribution
              Column(
                  modifier =
                      Modifier.fillMaxWidth() // Changed to fill available width instead of size
                          .weight(6f)
                          .verticalScroll(rememberScrollState())) {
                    for (ingredient in ingredientList) {
                      // Display the ingredient
                      IngredientPreview(ingredient, ingredientViewModel)
                    }
                  }

              // Box for the save button, positioned at the bottom center
              Box(
                  modifier = Modifier.fillMaxWidth().padding(PADDING.dp),
                  contentAlignment = Alignment.Center) {
                    Button(
                        onClick = {
                          for (ingredient in ingredientList) {
                            createRecipeViewModel.addIngredient(
                                ingredient.name, ingredient.quantity.toString())
                          }
                          navigationActions.navigateTo(Screen.CREATE_RECIPE_ADD_INSTRUCTION)
                        },
                        modifier =
                            Modifier.width(BUTTON_WIDTH)
                                .height(BUTTON_HEIGHT)
                                .background(
                                    color = lightCream, shape = RoundedCornerShape(size = 4.dp))
                                .align(Alignment.BottomCenter)
                                .zIndex(1f)
                                .testTag("nextStepButton"),
                        shape = RoundedCornerShape(4.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary),
                    ) {
                      Text(
                          text = "Next Step",
                          style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    }
                  }
            }
      })
}

@Composable
fun IngredientPreview(ingredient: Ingredient, ingredientViewModel: IngredientViewModel) {
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .padding(SMALL_PADDING.dp)
              .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp), clip = true)
              .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(8.dp))) {
        Row(modifier = Modifier.fillMaxWidth().padding(SMALL_PADDING.dp)) {
          // Adds left space
          Spacer(modifier = Modifier.width(PADDING.times(4).dp))
          Column(
              verticalArrangement = Arrangement.SpaceBetween,
              horizontalAlignment = Alignment.Start,
              modifier = Modifier.padding(PADDING.dp)) {
                Text(
                    text = ingredient.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis)
                ingredient.quantity?.let {
                  Spacer(modifier = Modifier.height(SMALL_PADDING.dp))
                  Text(
                      text = it,
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onPrimary)
                }
              }
        }

        IconButton(
            onClick = { ingredientViewModel.removeIngredient(ingredient) },
            modifier =
                Modifier.align(Alignment.TopEnd)
                    .padding(end = SMALL_PADDING.dp, top = SMALL_PADDING.dp)) {
              Icon(
                  modifier = Modifier.testTag("removeIngredientIcon${ingredient.name}"),
                  imageVector = Icons.Filled.Close,
                  contentDescription = "Close",
                  tint = MaterialTheme.colorScheme.onPrimary)
            }
      }
}
