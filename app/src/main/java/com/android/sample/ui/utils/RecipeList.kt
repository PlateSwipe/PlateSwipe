package com.android.sample.ui.utils

import android.os.Handler
import android.os.Looper
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.FileDownloadOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.android.sample.R
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.Dimension.PADDING_4
import com.android.sample.resources.C.Dimension.RecipeList.EDIT_ICON_SIZE
import com.android.sample.resources.C.Tag.RECIPE_DELETE_ICON_CONTENT_DESCRIPTION
import com.android.sample.resources.C.Tag.RECIPE_FAVORITE_ICON_CONTENT_DESCRIPTION
import com.android.sample.resources.C.Tag.RECIPE_LIST_CORNER_RADIUS
import com.android.sample.resources.C.Tag.RECIPE_LIST_DOWNLOAD
import com.android.sample.resources.C.Tag.RECIPE_RATING_CONTENT_DESCRIPTION
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_CARD_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_CATEGORIES_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_DELETE_ICON_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_DOWNLOAD_ICON_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_FAVORITE_ICON_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_IMAGE_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_LIST_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_PRICE_RATING_TEST_TAG
import com.android.sample.resources.C.TestTag.RecipeList.RECIPE_TITLE_TEST_TAG
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
      modifier = modifier.testTag(RECIPE_LIST_TEST_TAG),
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
          Modifier.fillMaxWidth()
              .height(88.dp)
              .padding(2.dp)
              .testTag(RECIPE_CARD_TEST_TAG)
              .clickable { onRecipeSelected(recipe) },
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
                      RecipeTitle(recipe, Modifier.weight(2f))
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

                      RecipePrice(cost = 3)
                    }
              }
        }
  }
}

@Composable
private fun RecipePrice(maxDollars: Int = 3, cost: Int) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.testTag(RECIPE_PRICE_RATING_TEST_TAG)) {
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
        contentDescription = RECIPE_RATING_CONTENT_DESCRIPTION,
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
      modifier = Modifier.testTag(RECIPE_CATEGORIES_TEST_TAG),
      horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        if (recipe.category?.isNotEmpty() == true) {
          for (category in recipe.category.split(",")) {
            Tag(category)
          }
        }
      }
}

@Composable
private fun RecipeImage(recipe: Recipe) {
  Image(
      painter = rememberAsyncImagePainter(recipe.url),
      contentDescription = null,
      modifier =
          Modifier.aspectRatio(1f)
              .fillMaxSize()
              .clip(RoundedCornerShape(RECIPE_LIST_CORNER_RADIUS.dp))
              .testTag(RECIPE_IMAGE_TEST_TAG))
}

@Composable
private fun RecipeTitle(recipe: Recipe, modifier: Modifier) {
  Text(
      modifier = modifier.testTag(RECIPE_TITLE_TEST_TAG),
      text = recipe.name,
      style = MaterialTheme.typography.titleSmall,
      fontWeight = FontWeight.Bold,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis)
}

/**
 * A button that when pressed, it will allow the user to edit the specific recipe.
 *
 * @param recipe the recipe to edit.
 * @param onEditClicked the callback to invoke when the edit button is clicked.
 */
@Composable
fun TopCornerEditButton(recipe: Recipe, onEditClicked: (Recipe) -> Unit) {
  Icon(
      painter = painterResource(id = R.drawable.pencil),
      contentDescription = stringResource(R.string.edit_recipe_icon_description),
      modifier =
          Modifier.padding(PADDING_4.dp)
              .size(EDIT_ICON_SIZE.dp)
              .testTag(RECIPE_FAVORITE_ICON_TEST_TAG)
              .clickable { onEditClicked(recipe) },
      tint = MaterialTheme.colorScheme.onPrimary)
}

/**
 * A like button that when pressed, it will remove a liked recipe from the users list of liked
 * recipes
 *
 * @param recipe the recipe to like.
 * @param userViewModel the current user view model
 */
@Composable
fun TopCornerDownloadAndLikeButton(
    recipe: Recipe,
    userViewModel: UserViewModel,
    recipeViewModel: RecipesViewModel
) {
  // List of the downloaded recipes
  val recipeDownloadList = recipeViewModel.downloadedRecipes.collectAsState()
  var recipeUnlike: Boolean by remember { mutableStateOf(false) }
  // Check if the recipe is in the recipe download list
  var recipeDownload: Boolean by remember {
    mutableStateOf(recipeDownloadList.value.any { it.uid == recipe.uid })
  }
  // Used to determine when we want to download a recipe
  var download: Boolean by remember { mutableStateOf(false) }
  // Used to determine when we want to remove a recipe
  var unDownload: Boolean by remember { mutableStateOf(false) }
  val context = LocalContext.current
  Row(
      horizontalArrangement = Arrangement.spacedBy(PADDING_4.dp),
      verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector =
                // If the recipe is not downloaded display Download icon else display remove
                // download icon
                if (!recipeDownload) Icons.Rounded.FileDownload else Icons.Rounded.FileDownloadOff,
            contentDescription = RECIPE_LIST_DOWNLOAD,
            modifier =
                Modifier.padding(PADDING_4.dp)
                    .size(30.dp)
                    .testTag(RECIPE_DOWNLOAD_ICON_TEST_TAG)
                    .clickable {
                      // If recipe is not download, set download to true
                      if (!recipeDownload) {
                        recipeDownload = true
                        download = true
                      } else {
                        // If recipe is already download and user click on the remove button ,set
                        // unDownload to true
                        recipeDownload = false
                        unDownload = true
                      }
                    },
            tint = MaterialTheme.colorScheme.onPrimary)
        Icon(
            imageVector = if (!recipeUnlike) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = RECIPE_FAVORITE_ICON_CONTENT_DESCRIPTION,
            modifier =
                Modifier.padding(PADDING_4.dp)
                    .size(30.dp)
                    .testTag(RECIPE_FAVORITE_ICON_TEST_TAG)
                    .clickable { recipeUnlike = true },
            tint = valencia)
      }

  if (recipeUnlike) {
    ConfirmationPopUp(
        onConfirm = {
          userViewModel.removeRecipeFromUserLikedRecipes(recipe)
          // If the user unlike a recipe, also remove it from download
          recipeViewModel.deleteDownload(recipe)
          recipeUnlike = false
        },
        onDismiss = { recipeUnlike = false },
        titleText = stringResource(R.string.message_pop_up_remove_liked))
  }
  // Confirmation Pop Up for download recipe
  if (download) {
    ConfirmationPopUp(
        onConfirm = {
          recipeViewModel.downloadRecipe(
              recipe,
              onSuccess = {
                Handler(Looper.getMainLooper()).post {
                  Toast.makeText(
                          context,
                          context.getString(R.string.recipe_downloaded),
                          Toast.LENGTH_SHORT)
                      .show()
                }
              },
              onFailure = {
                Handler(Looper.getMainLooper()).post {
                  Toast.makeText(
                          context,
                          context.getString(R.string.recipe_download_failed),
                          Toast.LENGTH_SHORT)
                      .show()
                }
              },
              context)
          download = false
        },
        onDismiss = {
          download = false
          recipeDownload = false
        },
        titleText = stringResource(R.string.do_you_want_to_download_this_recipe))
  }
  // confirmation Pop up to remove a recipe from the download
  if (unDownload) {
    ConfirmationPopUp(
        onConfirm = {
          recipeViewModel.deleteDownload(recipe)
          unDownload = false
        },
        onDismiss = {
          unDownload = false
          recipeDownload = true
        },
        titleText = stringResource(R.string.do_you_want_to_remove_this_recipe_from_your_downloads))
  }
}

/**
 * A delete button that when pressed, it will delete the specific created recipe from the users
 * account but also from the database, thus deleting it also for all the users that liked it
 *
 * @param recipe the recipe to delete.
 * @param userViewModel the current user view model
 */
@Composable
fun TopCornerDeleteButton(recipe: Recipe, userViewModel: UserViewModel) {
  var recipeDelete: Boolean by remember { mutableStateOf(false) }
  Icon(
      imageVector = Icons.Filled.Delete,
      contentDescription = RECIPE_DELETE_ICON_CONTENT_DESCRIPTION,
      modifier =
          Modifier.padding(PADDING_4.dp)
              .size(24.dp)
              .testTag(RECIPE_DELETE_ICON_TEST_TAG)
              .clickable { recipeDelete = true },
      tint = valencia)
  if (recipeDelete) {
    ConfirmationPopUp(
        onConfirm = {
          userViewModel.removeRecipeFromUserCreatedRecipes(recipe)
          recipeDelete = false
        },
        onDismiss = { recipeDelete = false },
        titleText = stringResource(R.string.message_pop_up_delete_created))
  }
}
