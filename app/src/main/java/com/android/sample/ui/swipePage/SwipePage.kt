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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.android.sample.R
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.model.user.UserViewModel
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.starColor
import com.android.sample.ui.theme.tagBackground
import kotlinx.coroutines.launch

private const val endAnimation = 1500f

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
                    containerColor = MaterialTheme.colorScheme.primary),
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
  val height = LocalConfiguration.current.screenHeightDp.dp * 1 / 2
  val width = height * 3 / 4
  var isDescriptionVisible by remember { mutableStateOf(false) }

  val offsetX = remember { Animatable(0f) }
  val coroutineScope = rememberCoroutineScope()
  val screenWidth = LocalConfiguration.current.screenWidthDp.toFloat()
  val swipeThreshold = screenWidth * 14 / 15

  val currentRecipe by recipesViewModel.currentRecipe.collectAsState()

  Column(
      modifier =
          Modifier.fillMaxSize()
              .padding(paddingValues) // Apply padding from the Scaffold
              .padding(16.dp)
              .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                      if (offsetX.value > swipeThreshold) {
                        isDescriptionVisible = false
                        recipesViewModel.nextRecipe()
                        if (currentRecipe != null)
                            userViewModel.addRecipeToUserLikedRecipes(currentRecipe!!)
                      } else if (offsetX.value < -swipeThreshold) {
                        isDescriptionVisible = false
                        recipesViewModel.nextRecipe()
                      }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                      coroutineScope.launch { offsetX.snapTo(offsetX.value + dragAmount) }
                    })
              }) {
        coroutineScope.launch {
          if (offsetX.value > endAnimation - 200) {
            offsetX.snapTo(0f)
          } else if (offsetX.value < -(endAnimation - 200)) {
            offsetX.snapTo(0f)
          }
        }
        // Recipe card with image
        Card(
            modifier =
                Modifier.fillMaxWidth().padding(8.dp).graphicsLayer(translationX = offsetX.value),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)) {
              Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.onPrimary)) {
                Image(
                    painter = rememberAsyncImagePainter(model = currentRecipe?.strMealThumbUrl),
                    contentDescription = "Recipe Image",
                    modifier =
                        Modifier.fillMaxWidth()
                            .size(
                                width = width,
                                height = if (isDescriptionVisible) height * 1 / 2 else height)
                            .testTag("recipeImage"),
                    contentScale =
                        if (!isDescriptionVisible) ContentScale.FillHeight
                        else ContentScale.FillWidth,
                )
              }
            }

        // Image Description
        ImageDescription(
            currentRecipe?.strMeal ?: "Loading...",
            "4.5",
            currentRecipe?.strCategory ?: "Loading...",
            modifier = Modifier.clickable { isDescriptionVisible = !isDescriptionVisible })

        // Spacer to push content to bottom
        Spacer(modifier = Modifier.weight(1f))

        // The last column for recipe description at the bottom
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(bottom = 16.dp),
        ) {
          // Display Recipe Description (truncated with ellipsis)
          Text(
              text = currentRecipe?.strInstructions ?: "Loading...",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSecondary,
              maxLines =
                  if (isDescriptionVisible) Int.MAX_VALUE
                  else 1, // Show full text if visible, otherwise one line
              overflow = TextOverflow.Ellipsis, // Add "..." if text exceeds one line
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(top = 8.dp)
                      .clickable {
                        isDescriptionVisible = !isDescriptionVisible // Toggle visibility on click
                      }
                      .testTag("recipeDescription"),
          )
        }

        // Animate back to center if not swiped
        LaunchedEffect(offsetX.value) {
          if (offsetX.value > swipeThreshold) {
            offsetX.animateTo(endAnimation, animationSpec = tween(50))
          } else if (offsetX.value < -swipeThreshold) {
            offsetX.animateTo(-endAnimation, animationSpec = tween(50)) // Animate back to center
          } else {
            offsetX.animateTo(0f, animationSpec = tween(50)) // Animate back to center
          }
        }
      }
}

/**
 * Composable for the Image Description
 *
 * @param name - Recipe Name
 * @param rate - Recipe Rate
 * @param tag - Recipe Tag
 * @param modifier - Modifier
 */
@Composable
private fun ImageDescription(name: String, rate: String, tag: String, modifier: Modifier) {

  Column(modifier = Modifier.padding(16.dp)) {
    Row(
        modifier =
            modifier.fillMaxWidth().testTag("draggableItem"), // Ensure the Row takes up full width
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {

          // Display Recipe Name
          RecipeDescription(name, rate)
        }
    Spacer(modifier = Modifier.padding(2.dp))
    Row(verticalAlignment = Alignment.CenterVertically) { Tag(tag) }
  }
}

/**
 * Composable for the Recipe Description
 *
 * @param name - Recipe Name
 * @param rate - Recipe Rate
 */
@Composable
fun RecipeDescription(name: String, rate: String) {
  Text(
      modifier = Modifier.testTag("recipeName"), // Let the text take up as much space as needed
      text = name,
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSecondary,
  )
  Row(
      horizontalArrangement = Arrangement.End,
      modifier = Modifier.size(75.dp, 24.dp) // Padding for inside spacing
      ) {
        Icon(
            painter = painterResource(R.drawable.star_rate),
            contentDescription = "Star rate",
            modifier =
                Modifier.size(24.dp).testTag("recipeStar"), // Adjust size to match the text size
            tint = starColor // Adjust color to match the text color
            )

        Spacer(modifier = Modifier.padding(5.dp))

        Text(
            text = rate,
            modifier = Modifier.testTag("recipeRate"),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onSecondary,
        )
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
