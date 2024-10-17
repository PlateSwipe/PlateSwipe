package com.android.sample.ui.testScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.user.UserViewModel
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions
import java.util.Random

/** Recipe card composable that displays a recipe.
 *
 * @param recipe recipe to display
 */
@Composable
fun RecipeCard(recipe: Recipe) {
  Column(
      modifier =
          Modifier.fillMaxWidth().padding(vertical = 8.dp).testTag("recipeCard${recipe.idMeal}"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .align(Alignment.CenterHorizontally)) {
              // Recipe image
              Image(
                  painter = rememberAsyncImagePainter(recipe.strMealThumbUrl),
                  contentDescription = null,
                  modifier =
                      Modifier.size(80.dp)
                          .clip(RoundedCornerShape(8.dp))
                          .padding(end = 16.dp)
                          .testTag("recipeImage${recipe.idMeal}"))
              Column(
                  modifier =
                      Modifier.weight(1f).padding(end = 16.dp).align(Alignment.CenterVertically)) {
                    // recipe title
                    Text(
                        modifier =
                            Modifier.padding(top = 8.dp)
                                .testTag("recipeTitle${recipe.idMeal}")
                                .align(Alignment.CenterHorizontally),
                        text = recipe.strMeal,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    // recipe rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                          imageVector = Icons.Filled.Star,
                          contentDescription = "rating",
                          modifier =
                              Modifier.size(16.dp).testTag("recipeRatingIcon${recipe.idMeal}"),
                          tint = Color.Gray)
                      Text(
                          modifier = Modifier.padding(4.dp).testTag("recipeRating${recipe.idMeal}"),
                          text = "${Random().nextFloat() * 3 + 2}",
                          style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      // recipe time
                      Icon(
                          imageVector = Icons.Filled.AccessTime,
                          contentDescription = "rating",
                          modifier = Modifier.size(16.dp).testTag("recipeTimeIcon${recipe.idMeal}"),
                          tint = Color.Gray)
                      Text(
                          modifier = Modifier.padding(4.dp).testTag("recipeTime${recipe.idMeal}"),
                          text =
                              "${arrayOf(
                              10,
                              15,
                              20,
                              30,
                              45,
                              50
                          ).random()} min",
                          style = MaterialTheme.typography.bodyMedium)

                      Spacer(modifier = Modifier.width(8.dp))

                      // recipe cost
                      Price(cost = Random().nextInt(3), recipe = recipe)
                    }
                  }
            }
      }
}


/**
 * Recipe list composable that displays a list of recipes.
 *
 * @param userViewModel view model for user data
 * @param navigationActions navigation actions
 */
@Composable
fun RecipeList(userViewModel: UserViewModel, navigationActions: NavigationActions) {
  var searchText by remember { mutableStateOf("") }
  val recipesList: List<Recipe> = userViewModel.likedRecipes.collectAsState().value
  Scaffold(
      modifier = Modifier.testTag("recipeList"),
      containerColor = Color(0xFFFFFFFF),
      topBar = {
        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start) {
              Button(
                  onClick = {},
                  colors =
                      ButtonDefaults.buttonColors(containerColor = Color.White), // Button color
                  shape = RoundedCornerShape(50), // Circular edges for the button
                  border = BorderStroke(1.dp, Color.LightGray),
                  modifier =
                      Modifier.padding(start = 16.dp, top = 8.dp, bottom = 16.dp)
                          .height(64.dp)
                          .width(329.dp)
                          .testTag("SearchButton")) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                      Icon(
                          modifier = Modifier.testTag("searchButtonIcon"),
                          imageVector = Icons.Filled.Search,
                          contentDescription = "Search",
                          tint = Color.Gray)

                      TextField(
                          modifier = Modifier.testTag("searchText"),
                          value = searchText,
                          onValueChange = { searchText = it },
                          label = {
                            Text(
                                text = "search",
                                modifier = Modifier.testTag("searchTextText").fillMaxWidth())
                          })
                    }
                  }
            }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { destination -> navigationActions.navigateTo(destination) },
            tabList = LIST_TOP_LEVEL_DESTINATIONS,
            selectedItem = navigationActions.currentRoute())
      },
  ) {
    Column(
        modifier = Modifier.padding(it).fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
          for (recipe in recipesList) {
            RecipeCard(recipe)
          }
        }
  }
}

/** Price rating composable that displays the price rating of a recipe with dollar icons.
 *
 * @param maxDollars maximum number of dollar icons to display
 * @param cost cost of the recipe
 * @param recipe recipe to display the price rating for
 */
@Composable
fun Price(maxDollars: Int = 3, cost: Int, recipe: Recipe) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.testTag("priceRating${recipe.idMeal}")) {
        for (i in 1..maxDollars) {
          val isSelected = i <= cost
          val icon = Icons.Filled.AttachMoney
          val iconTintColor = if (isSelected) Color(0xFF000000) else Color(0xFFB0B0B0)
          Icon(
              imageVector = icon,
              contentDescription = null,
              tint = iconTintColor,
          )
        }
      }
}
