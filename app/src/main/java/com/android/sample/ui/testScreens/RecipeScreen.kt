package com.android.sample.ui.testScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
git import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import com.android.sample.ui.theme.goldenBronze
import java.util.Locale
import java.util.Random

/**
 * Recipe card composable that displays a recipe.
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
            RecipeImage(recipe)
            Column(
                  modifier = Modifier.weight(1f).padding(end = 16.dp).align(Alignment.CenterVertically)) {
                 Row(
                     verticalAlignment = Alignment.CenterVertically,
                     modifier = Modifier.fillMaxWidth(),
                     horizontalArrangement = Arrangement.SpaceBetween) {
                     RecipeTitle(recipe)
                     RecipeLike(recipe)
                  }

            RecipeRating(recipe)

            Row(verticalAlignment = Alignment.CenterVertically) {
                RecipeTime(recipe)
                Spacer(modifier = Modifier.width(8.dp))
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
  val recipesList: List<Recipe> = userViewModel.likedRecipes.collectAsState().value
  Scaffold(
      modifier = Modifier.testTag("recipeList"),
      containerColor = Color(0xFFFFFFFF),
      topBar = { SearchBar() },
      bottomBar = { BottomBarMenu(navigationActions) },
  ) {
      LazyColumn(
          modifier = Modifier.padding(it).fillMaxWidth(),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally
      ) {
          items(recipesList) { recipe ->
              RecipeCard(recipe)
          }
      }
  }
}

/**
 * Price rating composable that displays the price rating of a recipe with dollar icons.
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
@Composable
fun RecipeRating(recipe: Recipe){
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "rating",
            modifier = Modifier.size(16.dp).testTag("recipeRatingIcon${recipe.idMeal}"),
            tint = goldenBronze)
        Text(
            modifier = Modifier.padding(4.dp).testTag("recipeRating${recipe.idMeal}"),
            text = String.format(Locale.US, "%.1f", Random().nextFloat() * 3 + 2)
            ,
            style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun RecipeTime(recipe: Recipe){
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
}

@Composable
fun RecipeImage(recipe: Recipe){
    Image(
        painter = rememberAsyncImagePainter(recipe.strMealThumbUrl),
        contentDescription = null,
        modifier =
        Modifier.size(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .padding(end = 16.dp)
            .testTag("recipeImage${recipe.idMeal}"))
}

@Composable
fun RecipeTitle(recipe: Recipe){
    Text(
        modifier =
        Modifier.padding(top = 8.dp)
            .testTag("recipeTitle${recipe.idMeal}"),
        text = recipe.strMeal,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
fun RecipeLike(recipe: Recipe){
    var isLiked by remember { mutableStateOf(true)}
    Icon(
        imageVector =
        if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
        contentDescription = "like",
        modifier =
        Modifier.size(16.dp)
                .testTag("recipeFavoriteIcon${recipe.idMeal}")
                .clickable { isLiked = !isLiked },
        tint = Color.Red
    )
}

@Composable
fun SearchBar(){
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
                var searchText by remember { mutableStateOf("") }
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
}
@Composable
fun BottomBarMenu(navigationActions: NavigationActions){
    BottomNavigationMenu(
        onTabSelect = { destination -> navigationActions.navigateTo(destination) },
        tabList = LIST_TOP_LEVEL_DESTINATIONS,
        selectedItem = navigationActions.currentRoute())
}
