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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.android.sample.R
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.Tag.RECIPE_LIST_CORNER_RADIUS
import com.android.sample.ui.theme.goldenBronze
import com.android.sample.ui.theme.valencia

/**
 * A list of recipes that can be displayed in a vertical list.
 *
 * @param list the list of recipes to display.
 * @param modifier the modifier to apply to this layout node.
 * @param onRecipeSelected the callback to invoke when a recipe is selected.
 * @param topCornerButton the composable to display in the top corner of the recipe card. Could be a
 *   button to like the recipe or to edit it. See RecipeCornerLikeButton for an example.
 */
@Composable
fun RecipeList(
    list: List<Recipe>,
    modifier: Modifier = Modifier,
    onRecipeSelected: (Recipe) -> Unit = {},
    topCornerButton: @Composable (Recipe) -> Unit = {},
) {
  LazyColumn(
      modifier = modifier.testTag("recipeList"),
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    items(list) { recipe ->
      RecipeCard(recipe, onRecipeSelected = onRecipeSelected, topCornerButton)
    }
  }
}

@Composable
private fun RecipeCard(
    recipe: Recipe,
    onRecipeSelected: (Recipe) -> Unit = {},
    topCornerButton: @Composable (Recipe) -> Unit = {},
) {
  Box(
      modifier =
          Modifier.fillMaxWidth().height(88.dp).padding(2.dp).testTag("recipeCard").clickable {
            onRecipeSelected(recipe)
          },
  ) {
    Row(
        modifier =
            Modifier.border(
                    BorderStroke(2.dp, MaterialTheme.colorScheme.onTertiary),
                    shape = RoundedCornerShape(RECIPE_LIST_CORNER_RADIUS.dp))
                .shadow(8.dp, shape = RoundedCornerShape(RECIPE_LIST_CORNER_RADIUS.dp))
                .background(MaterialTheme.colorScheme.surface)
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
                      RecipeTitle(recipe, Modifier.weight(3f))
                      topCornerButton(recipe)
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
  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.testTag("priceRating")) {
    for (i in 1..maxDollars) {
      val isSelected = i <= cost
      val iconTintColor =
          if (isSelected) MaterialTheme.colorScheme.onSecondary
          else MaterialTheme.colorScheme.onPrimary
      Icon(
          painter = painterResource(R.drawable.dollar_sign),
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
      modifier = Modifier.testTag("recipeCategories"),
      horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        if (recipe.strCategory?.isNotEmpty() == true) {
          for (category in recipe.strCategory.split(",")) {
            Tag(category)
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
              .clip(RoundedCornerShape(RECIPE_LIST_CORNER_RADIUS.dp))
              .testTag("recipeImage"))
}

@Composable
private fun RecipeTitle(recipe: Recipe, modifier: Modifier) {
  Text(
      modifier = modifier.testTag("recipeTitle"),
      text = recipe.strMeal,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis)
}

/**
 * A like button that toggles the like state of a recipe. For now it doesn't actually change whether
 * the recipe is liked or not. It serves more as an example of a top corner button for the recipe
 * card and a placeholder.
 *
 * @param recipe the recipe to like.
 */
@Composable
fun TopCornerLikeButton(recipe: Recipe, userViewModel: UserViewModel) {
  var isLiked by remember { mutableStateOf(true) }
  Icon(
      imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
      contentDescription = "like",
      modifier =
          Modifier.padding(4.dp).testTag("recipeFavoriteIcon").clickable {
            isLiked = !isLiked
            userViewModel.removeRecipeFromUserLikedRecipes(recipe)
          },
      tint = valencia)
}
