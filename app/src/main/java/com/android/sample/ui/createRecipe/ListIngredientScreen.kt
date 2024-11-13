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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.resources.C.Tag.PADDING
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
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
            modifier = Modifier.testTag("DraggableItem").fillMaxSize().padding(paddingValues)) {
              Text(
                  text = createRecipeViewModel.recipeBuilder.getName(),
                  style = MaterialTheme.typography.titleLarge.copy(fontSize = 50.sp),
                  color = MaterialTheme.colorScheme.onPrimary,
                  modifier = Modifier.padding(PADDING.dp))
              Spacer(modifier = Modifier.height(PADDING.dp))

              Row(
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(start = PADDING.dp, end = PADDING.dp, top = PADDING.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                          text = createRecipeViewModel.recipeBuilder.getName(),
                          style = MaterialTheme.typography.titleMedium,
                          color = MaterialTheme.colorScheme.onPrimary)
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.add),
                        contentDescription = "Add",
                        modifier =
                            Modifier.clickable {
                              navigationActions.navigateTo(Screen.CREATE_RECIPE_SEARCH_INGREDIENTS)
                            })
                  }
              Text(
                  text = "4 People",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onPrimary,
                  modifier =
                      Modifier.padding(start = PADDING.dp, end = PADDING.dp, bottom = PADDING.dp))

              Column(
                  modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
              ) {
                for (ingredient in ingredientList) {
                  // Display the ingredient
                  IngredientPreview(ingredient)
                }
              }
            }
      })
}

@Composable
fun IngredientPreview(ingredient: Ingredient) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(PADDING.dp)
              .shadow(
                  elevation = 4.dp, // Adjust elevation as desired
                  shape = RoundedCornerShape(8.dp),
                  clip = true // Ensures background respects the shadow's rounded corners
                  )
              .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(8.dp))) {
        Spacer(modifier = Modifier.width(PADDING.times(4).dp)) // Adds left space
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
