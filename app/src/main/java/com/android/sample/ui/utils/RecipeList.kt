package com.android.sample.ui.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.android.sample.model.recipe.Recipe
import com.android.sample.ui.theme.goldenBronze

val cornerEdgeRadius: Dp = 12.dp

/**
 * Lists recipes in a vertical list. Shows the rating, price, and categories for each recipe.
 *
 * @param list the list of recipes to display.
 * @param onRecipeSelected the callback to invoke when a recipe is selected.
 * @param modifier the modifier to apply to this layout node.
 */
@Composable
fun RecipeList(
    list: List<Recipe>,
    onRecipeSelected: (Recipe) -> Unit,
    modifier: Modifier = Modifier,
) {
  LazyColumn(
      modifier = modifier.testTag("recipeList"),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    items(list) { recipe -> RecipeCard(recipe, onRecipeSelected = onRecipeSelected) }
  }
}

@Composable
private fun RecipeCard(recipe: Recipe, onRecipeSelected: (Recipe) -> Unit) {
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .height(88.dp)
              .padding(2.dp)
              .testTag("recipeCard${recipe.idMeal}")
              .clickable { onRecipeSelected(recipe) },
  ) {
    Row(
        modifier =
            Modifier.border(
                    BorderStroke(2.dp, MaterialTheme.colorScheme.onTertiary),
                    shape = RoundedCornerShape(cornerEdgeRadius))
                .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          RecipeImage(recipe)

          Column(
              modifier = Modifier.fillMaxSize(),
              verticalArrangement = Arrangement.SpaceBetween,
              horizontalAlignment = Alignment.Start) {
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                      RecipeTitle(recipe)
                      RecipeLike(recipe)
                    }

                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                      RecipeRating()
                    }

                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                      RecipeCategories(recipe)

                      RecipePrice(cost = 3, recipe = recipe)
                    }
              }
        }
  }
}

@Composable
private fun RecipePrice(maxDollars: Int = 3, cost: Int, recipe: Recipe) {
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
private fun RecipeRating() {
  Row(
      modifier = Modifier.height(24.dp),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
        imageVector = Icons.Filled.Star,
        contentDescription = "rating",
        modifier = Modifier.size(24.dp),
        tint = goldenBronze)
    Spacer(modifier = Modifier.width(8.dp))
    Text(
        text = "4.5",
        style = MaterialTheme.typography.bodyMedium,
    )
  }
}

@Composable
private fun RecipeCategories(recipe: Recipe) {
  Row(
      modifier = Modifier.testTag("recipeCategories${recipe.idMeal}"),
      horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        if (recipe.strCategory?.isNotEmpty() == true) {
          for (category in recipe.strCategory.split(",")) {
            Text(
                modifier =
                    Modifier.background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(cornerEdgeRadius / 2))
                        .padding(4.dp),
                text = category,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface)
          }
        }
      }
}

@Composable
private fun RecipeImage(recipe: Recipe) {
  Image(
      painter = rememberAsyncImagePainter(recipe.strMealThumbUrl),
      contentDescription = null,
      modifier =
          Modifier.aspectRatio(1f)
              .fillMaxSize()
              .clip(RoundedCornerShape(cornerEdgeRadius))
              .testTag("recipeImage${recipe.idMeal}"))
}

@Composable
private fun RecipeTitle(recipe: Recipe) {
  Text(
      modifier = Modifier.testTag("recipeTitle${recipe.idMeal}"),
      text = recipe.strMeal,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
  )
}

@Composable
private fun RecipeLike(recipe: Recipe) {
  var isLiked by remember { mutableStateOf(true) }
  Icon(
      imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
      contentDescription = "like",
      modifier =
          Modifier.padding(4.dp).testTag("recipeFavoriteIcon${recipe.idMeal}").clickable {
            isLiked = !isLiked
          },
      tint = Color.Red)
}
