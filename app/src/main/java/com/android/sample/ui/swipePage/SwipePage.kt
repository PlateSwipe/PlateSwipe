package com.android.sample.ui.swipePage

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.android.sample.R
import com.android.sample.model.filter.Difficulty
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.Tag.CATEGORY_INPUT_DESCRIPTION
import com.android.sample.resources.C.Tag.DIFFICULTY_INPUT_DESCRIPTION
import com.android.sample.resources.C.Tag.END_ANIMATION
import com.android.sample.resources.C.Tag.FILTER_ICON_DESCRIPTION
import com.android.sample.resources.C.Tag.LOADING
import com.android.sample.resources.C.Tag.PRICE_RANGE_INPUT_DESCRIPTION
import com.android.sample.resources.C.Tag.TIME_RANGE_INPUT_DESCRIPTION
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.graySlate
import com.android.sample.ui.theme.greenSwipe
import com.android.sample.ui.theme.redSwipe
import com.android.sample.ui.theme.starColor
import com.android.sample.ui.theme.tagBackground
import com.android.sample.ui.utils.PlateSwipeScaffold
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
@Composable
fun SwipePage(
    navigationActions: NavigationActions,
    recipesViewModel: RecipesViewModel = viewModel(factory = RecipesViewModel.Factory),
    userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory)
) {
  val selectedItem = navigationActions.currentRoute()

  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = selectedItem,
      showBackArrow = false,
      content = { paddingValues ->
        RecipeDisplay(navigationActions, paddingValues, recipesViewModel, userViewModel)
      })
}

/**
 * Main Composable for the Image Gallery with Swipe feature
 *
 * @param paddingValues - Padding values for the column
 */
@SuppressLint("StateFlowValueCalledInComposition", "CoroutineCreationDuringComposition")
@Composable
fun RecipeDisplay(
    navigationActions: NavigationActions,
    paddingValues: PaddingValues,
    recipesViewModel: RecipesViewModel = viewModel(factory = RecipesViewModel.Factory),
    userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory)
) {
  var retrieveNextRecipe by remember { mutableStateOf(false) }
  var displayCard1 by remember { mutableStateOf(true) }
  var displayCard2 by remember { mutableStateOf(false) }

  // Offset for the swipe animation
  val offsetX = remember { Animatable(0f) }

  val coroutineScope = rememberCoroutineScope()
  val density = LocalDensity.current.density
  val screenWidth = LocalConfiguration.current.screenWidthDp * density
  val swipeThreshold = screenWidth * 1 / 3
  // Collect the current and next recipe from the ViewModel
  val currentRecipe by recipesViewModel.currentRecipe.collectAsState()
  val nextRecipe by recipesViewModel.nextRecipe.collectAsState()
  val filter by recipesViewModel.filter.collectAsState()

  // Snap back to center when animation is finished
  coroutineScope.launch {
    if (offsetX.value.absoluteValue > END_ANIMATION - 200) {
      offsetX.snapTo(0f)
      displayCard1 = !displayCard1
      displayCard2 = !displayCard2
    }
  }
  Box(
      modifier =
          Modifier.fillMaxSize().background(getBackgroundColor(offsetX.value, screenWidth))) {
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(dimensionResource(id = R.dimen.paddingBasic))
                    .background(Color.Transparent)
                    .pointerInput(Unit) {
                      detectHorizontalDragGestures(
                          onDragEnd = {
                            if (kotlin.math.abs(offsetX.value) > swipeThreshold) {
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
              Row(
                  horizontalArrangement = Arrangement.End,
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.fillMaxWidth().weight(1f)) {
                    // Star Icon (fixed size, no weight needed)
                    Icon(
                        painter = painterResource(R.drawable.filter),
                        contentDescription = FILTER_ICON_DESCRIPTION,
                        modifier =
                            Modifier.testTag("filter").size(30.dp).clickable {
                              navigationActions.navigateTo(Screen.FILTER)
                            }, // Use fixed size for the icon
                        tint = graySlate)
                  }
              // Space between the filter icon and the filter chips
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                  horizontalArrangement = Arrangement.Start,
                  verticalAlignment = Alignment.CenterVertically,
                  modifier =
                      Modifier.testTag("filterRow")
                          .fillMaxWidth()
                          .weight(1f)
                          .horizontalScroll(rememberScrollState())) {
                    // Star Icon (fixed size, no weight needed)
                    var displayTimeRange by remember {
                      mutableStateOf(!filter.timeRange.isLimited())
                    }
                    var displayPriceRange by remember {
                      mutableStateOf(!filter.priceRange.isLimited())
                    }
                    var displayDifficulty by remember {
                      mutableStateOf(filter.difficulty != Difficulty.Undefined)
                    }
                    var displayCategory by remember { mutableStateOf(filter.category != null) }

                    FilterChip(
                        displayState = displayTimeRange,
                        onDelete = {
                          displayTimeRange = false
                          recipesViewModel.updateTimeRange(
                              recipesViewModel.filter.value.timeRange.minBorn,
                              recipesViewModel.filter.value.timeRange.maxBorn)
                        },
                        label =
                            "${filter.timeRange.min.toInt()} - ${filter.timeRange.max.toInt()} min",
                        testTag = "timeRangeChip",
                        contentDescription = TIME_RANGE_INPUT_DESCRIPTION)

                    FilterChip(
                        displayState = displayPriceRange,
                        onDelete = {
                          displayPriceRange = false
                          recipesViewModel.updatePriceRange(
                              recipesViewModel.filter.value.priceRange.minBorn,
                              recipesViewModel.filter.value.priceRange.maxBorn)
                        },
                        label =
                            "${filter.priceRange.min.toInt()} - ${filter.priceRange.max.toInt()} $",
                        testTag = "priceRangeChip",
                        contentDescription = PRICE_RANGE_INPUT_DESCRIPTION)

                    FilterChip(
                        displayState = displayDifficulty,
                        onDelete = {
                          displayDifficulty = false
                          recipesViewModel.updateDifficulty(Difficulty.Undefined)
                        },
                        label = filter.difficulty.toString(),
                        testTag = "difficultyChip",
                        contentDescription = DIFFICULTY_INPUT_DESCRIPTION)

                    FilterChip(
                        displayState = displayCategory,
                        onDelete = {
                          displayCategory = false
                          recipesViewModel.updateCategory(null)
                        },
                        label = filter.category.orEmpty(),
                        testTag = "categoryChip",
                        contentDescription = CATEGORY_INPUT_DESCRIPTION)
                  }

              // Space between the filter chips and the recipe cards
              Spacer(modifier = Modifier.height(8.dp))

              // First Recipe card with image
              Box(modifier = Modifier.weight(15f)) {
                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.paddingBasic) / 2)
                            .graphicsLayer(translationX = if (displayCard1) offsetX.value else 0f)
                            .zIndex(if (displayCard1) 1f else 0f),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)) {
                      Column(
                          modifier =
                              Modifier.background(color = MaterialTheme.colorScheme.onPrimary)) {
                            Image(
                                painter =
                                    rememberAsyncImagePainter(
                                        model =
                                            if (displayCard1) currentRecipe?.strMealThumbUrl
                                            else nextRecipe?.strMealThumbUrl),
                                contentDescription = stringResource(R.string.recipe_image),
                                modifier = Modifier.fillMaxSize().testTag("recipeImage1"),
                                contentScale = ContentScale.Crop,
                            )
                          }
                    }

                // Second Recipe card with image
                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.paddingBasic) / 2)
                            .graphicsLayer(translationX = if (displayCard2) offsetX.value else 0f)
                            .zIndex(if (displayCard2) 1f else 0f),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)) {
                      Column(
                          modifier =
                              Modifier.background(color = MaterialTheme.colorScheme.onPrimary)) {
                            Image(
                                painter =
                                    rememberAsyncImagePainter(
                                        model =
                                            if (displayCard2) currentRecipe?.strMealThumbUrl
                                            else nextRecipe?.strMealThumbUrl),
                                contentDescription = stringResource(R.string.recipe_image),
                                modifier = Modifier.fillMaxSize().testTag("recipeImage2"),
                                contentScale = ContentScale.Crop,
                            )
                          }
                    }
              }

              // Image Description
              ImageDescription(
                  currentRecipe?.strMeal ?: LOADING, currentRecipe?.strCategory ?: LOADING)

              // Spacer to push content to bottom
              Spacer(modifier = Modifier.weight(1f))

              // The last column for recipe description at the bottom
              /*Column(
                  verticalArrangement = Arrangement.Bottom,
                  modifier =
                  Modifier.weight(2f)
                      .verticalScroll(rememberScrollState())
                      .padding(bottom = dimensionResource(id = R.dimen.paddingBasic)),
              ) {
                  // Display Recipe Description (truncated with ellipsis)
                  Text(
                      text = currentRecipe?.strInstructions ?: LOADING,
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSecondary,
                      maxLines = 1, // Show full text if visible, otherwise one line
                      overflow = TextOverflow.Ellipsis, // Add "..." if text exceeds one line
                      modifier =
                      Modifier.fillMaxWidth()
                          .padding(top = dimensionResource(id = R.dimen.paddingBasic) / 2)
                          .clickable { navigationActions.navigateTo(Screen.OVERVIEW_RECIPE) }
                          .testTag("recipeDescription"),
                  )*/

              Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                ShawRecipeButton(navigationActions)
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
}

/**
 * Composable for the Shaw Recipe Button
 *
 * @param navigationActions - Navigation Actions
 */
@Composable
private fun ShawRecipeButton(navigationActions: NavigationActions) {
  Button(
      onClick = { navigationActions.navigateTo(Screen.OVERVIEW_RECIPE) },
      colors =
          ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
              contentColor = MaterialTheme.colorScheme.onPrimary),
      elevation = ButtonDefaults.buttonElevation(4.dp),
      shape = RoundedCornerShape(12.dp),
      modifier =
          Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              .wrapContentSize()
              .testTag("viewRecipeButton")) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
              Image(
                  painter = painterResource(id = R.drawable.chef_s_hat),
                  contentDescription = "Chef's hat",
                  modifier = Modifier.size(24.dp),
                  contentScale = ContentScale.Fit)

              Text(
                  text = "View Recipe",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onPrimary,
              )
            }
      }
}

@Composable
private fun getBackgroundColor(offsetX: Float, screenWidth: Float): Brush {
  val colorIntensity = (offsetX / screenWidth).coerceIn(-1f, 1f)

  return when {
    colorIntensity > 0 -> {
      // Right swipe - green gradient
      Brush.horizontalGradient(
          colors = listOf(Color.Transparent, greenSwipe.copy(alpha = colorIntensity)),
          startX = 0f,
          endX = screenWidth)
    }
    colorIntensity < 0 -> {
      // Left swipe - red gradient
      Brush.horizontalGradient(
          colors = listOf(Color.Transparent, redSwipe.copy(alpha = -colorIntensity)),
          startX = screenWidth,
          endX = 0f)
    }
    else -> Brush.horizontalGradient(colors = listOf(Color.Transparent, Color.Transparent))
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

/**
 * Composable for the Filter Chip
 *
 * @param displayState - Display State of the Chip
 * @param onDelete - On Delete Function
 * @param label - Label for the Chip
 * @param testTag - Test Tag for the Chip
 * @param contentDescription - Content Description for the Chip
 */
@Composable
fun FilterChip(
    displayState: Boolean,
    onDelete: () -> Unit,
    label: String,
    testTag: String,
    contentDescription: String,
) {
  if (displayState) {
    InputChip(
        modifier = Modifier.testTag(testTag),
        selected = false,
        onClick = {},
        label = {
          Text(
              text = label,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSecondary,
          )
        },
        trailingIcon = {
          Icon(
              Icons.Filled.Close,
              contentDescription = contentDescription,
              modifier =
                  Modifier.testTag("${testTag}Delete").size(InputChipDefaults.IconSize).clickable {
                    onDelete()
                  },
              tint = MaterialTheme.colorScheme.onSecondary)
        })
    Spacer(modifier = Modifier.width(8.dp))
  }
}
