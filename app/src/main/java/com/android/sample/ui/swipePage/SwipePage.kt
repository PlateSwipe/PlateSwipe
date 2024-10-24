package com.android.sample.ui.swipePage

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.android.sample.R
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.Tag.END_ANIMATION
import com.android.sample.resources.C.Tag.LOADING
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.starColor
import com.android.sample.ui.theme.tagBackground
import kotlin.math.absoluteValue
import kotlinx.coroutines.launch

/**
 * Composable for the Swipe Page
 *
 * @param navigationActions - Navigation Actions
 * @param recipesViewModel - Recipes View Model
 * @param userViewModel - User View Model
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipePage(
    navigationActions: NavigationActions,
    recipesViewModel: RecipesViewModel = viewModel(factory = RecipesViewModel.Factory),
    userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory)
) {
  val selectedItem = navigationActions.currentRoute()

  Scaffold(
      modifier = Modifier.fillMaxWidth(),
      topBar = {
        TopAppBar(
            title = { Text("PlateSwipe") },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            modifier = Modifier.testTag("topBar"))
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tab -> navigationActions.navigateTo(tab) },
            tabList = LIST_TOP_LEVEL_DESTINATIONS,
            selectedItem = selectedItem)
      }) { paddingValues ->
        RecipeDisplay(paddingValues, recipesViewModel, userViewModel)
      }
}

/**
 * Main Composable for the Image Gallery with Swipe feature
 *
 * @param paddingValues - Padding values for the column
 */
@SuppressLint("StateFlowValueCalledInComposition", "CoroutineCreationDuringComposition")
@Composable
fun RecipeDisplay(
    paddingValues: PaddingValues,
    recipesViewModel: RecipesViewModel = viewModel(factory = RecipesViewModel.Factory),
    userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory)
) {
  var isDescriptionVisible by remember { mutableStateOf(false) }
  var retrieveNextRecipe by remember { mutableStateOf(false) }

  val offsetX = remember { Animatable(0f) }
  val coroutineScope = rememberCoroutineScope()
  val screenWidth = LocalConfiguration.current.screenWidthDp.toFloat()
  val swipeThreshold = screenWidth * 14 / 15

  val currentRecipe by recipesViewModel.currentRecipe.collectAsState()

  Column(
      modifier =
          Modifier.fillMaxSize()
              .padding(paddingValues)
              .padding(dimensionResource(id = R.dimen.paddingBasic))
              .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                      if (kotlin.math.abs(offsetX.value) > swipeThreshold) {
                        isDescriptionVisible = false
                        retrieveNextRecipe = true
                        if (offsetX.value > 0 && currentRecipe != null) {
                          userViewModel.addRecipeToUserLikedRecipes(currentRecipe!!)
                        }
                      }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                      coroutineScope.launch { offsetX.snapTo(offsetX.value + dragAmount) }
                    })
              }) {
        // Snap back to center when animation is finished
        coroutineScope.launch {
          if (offsetX.value.absoluteValue > END_ANIMATION - 200) {
            offsetX.snapTo(0f)
          }
        }
        // Recipe card with image
        Card(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.paddingBasic) / 2)
                    .graphicsLayer(translationX = offsetX.value)
                    .weight(15f),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)) {
              Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.onPrimary)) {
                Image(
                    painter = rememberAsyncImagePainter(model = currentRecipe?.strMealThumbUrl),
                    contentDescription = stringResource(R.string.recipe_image),
                    modifier = Modifier.fillMaxSize().testTag("recipeImage"),
                    contentScale = ContentScale.Crop,
                )
              }
            }

        // Image Description
        ImageDescription(currentRecipe?.strMeal ?: LOADING, currentRecipe?.strCategory ?: LOADING)

        // Spacer to push content to bottom
        Spacer(modifier = Modifier.weight(1f))

        // The last column for recipe description at the bottom
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier =
                Modifier.weight(if (!isDescriptionVisible) 2f else 20f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = dimensionResource(id = R.dimen.paddingBasic)),
        ) {
          // Display Recipe Description (truncated with ellipsis)
          Text(
              text = currentRecipe?.strInstructions ?: LOADING,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSecondary,
              maxLines =
                  if (isDescriptionVisible) Int.MAX_VALUE
                  else 1, // Show full text if visible, otherwise one line
              overflow = TextOverflow.Ellipsis, // Add "..." if text exceeds one line
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(top = dimensionResource(id = R.dimen.paddingBasic) / 2)
                      .clickable {
                        isDescriptionVisible = !isDescriptionVisible // Toggle visibility on click
                      }
                      .testTag("recipeDescription"),
          )
        }

        // Animate back to center if not swiped
        LaunchedEffect(offsetX.value) {
          val animationTarget =
              when {
                offsetX.value > swipeThreshold -> END_ANIMATION
                offsetX.value < -swipeThreshold -> -END_ANIMATION
                else -> 0f
              }
          if (retrieveNextRecipe && offsetX.value == 0f) {
            recipesViewModel.nextRecipe()
            retrieveNextRecipe = false
          }
          offsetX.animateTo(animationTarget, animationSpec = tween(50))
        }
      }
}

/**
 * Composable for the Image Description
 *
 * @param name - Recipe Name
 * @param tag - Recipe Tag
 */
@Composable
private fun ImageDescription(name: String, tag: String) {
  Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.paddingBasic))) {
    Row(
        modifier =
            Modifier.fillMaxWidth().testTag("draggableItem"), // Ensure the Row takes up full width
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
          // Display Recipe Name
          Text(
              modifier =
                  Modifier.testTag("recipeName")
                      .weight(3f), // Takes up 3 parts of the available space
              text = name,
              style = MaterialTheme.typography.bodyLarge,
              color = MaterialTheme.colorScheme.onSecondary,
              maxLines = 2, // Limit to 1 line to prevent overflow
              overflow = TextOverflow.Ellipsis // Show "..." if text is too long
              )

          // Row for the star and rate text
          Row(
              horizontalArrangement = Arrangement.End,
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f) // Takes 1 part of the space for icon and rating
              ) {
                // Star Icon (fixed size, no weight needed)
                Icon(
                    painter = painterResource(R.drawable.star_rate),
                    contentDescription = stringResource(R.string.star_rate_description),
                    modifier =
                        Modifier.testTag("recipeStar").size(24.dp), // Use fixed size for the icon
                    tint = starColor)

                Spacer(modifier = Modifier.width(8.dp)) // Add spacing between icon and rate

                // Rating Text
                Text(
                    text = stringResource(R.string.rate),
                    modifier = Modifier.testTag("recipeRate"),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondary)
              }
        }

    Spacer(modifier = Modifier.padding(2.dp))

    Row(verticalAlignment = Alignment.CenterVertically) { Tag(tag) }
  }
}

/**
 * Composable for the Tags of the recipe
 *
 * @param tag - Tag Name
 */
@Composable
private fun Tag(tag: String) {
  Box(
      modifier =
          Modifier.background(
                  color = tagBackground,
                  shape = RoundedCornerShape(16.dp)) // Smooth rounded corners
              .padding(horizontal = 12.dp, vertical = 4.dp) // Padding for inside spacing
      ) {
        Text(
            text = tag, fontSize = 14.sp, color = Color.White // Text color
            )
      }
}
