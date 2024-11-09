package com.android.sample.ui.swipePage

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
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
import com.android.sample.resources.C.Dimension.SwipePage.ANIMATION_OPACITY_MAX
import com.android.sample.resources.C.Dimension.SwipePage.ANIMATION_OPACITY_MIN
import com.android.sample.resources.C.Dimension.SwipePage.ANIMATION_OPACITY_TIME
import com.android.sample.resources.C.Dimension.SwipePage.ANIMATION_PADDING_SWIPE
import com.android.sample.resources.C.Dimension.SwipePage.ANIMATION_PADDING_TOP
import com.android.sample.resources.C.Dimension.SwipePage.ANIMATION_SWIPE_TIME
import com.android.sample.resources.C.Dimension.SwipePage.BACKGROUND_ANIMATION
import com.android.sample.resources.C.Dimension.SwipePage.BUTTON_COOK_SIZE
import com.android.sample.resources.C.Dimension.SwipePage.BUTTON_ELEVATION
import com.android.sample.resources.C.Dimension.SwipePage.BUTTON_PADDING
import com.android.sample.resources.C.Dimension.SwipePage.BUTTON_RADIUS
import com.android.sample.resources.C.Dimension.SwipePage.CARD_ELEVATION
import com.android.sample.resources.C.Dimension.SwipePage.CARD_WEIGHT
import com.android.sample.resources.C.Dimension.SwipePage.CHIPS_WEIGHT
import com.android.sample.resources.C.Dimension.SwipePage.CORNER_RADIUS
import com.android.sample.resources.C.Dimension.SwipePage.DESCRIPTION_FONT_SIZE
import com.android.sample.resources.C.Dimension.SwipePage.DESCRIPTION_WEIGHT
import com.android.sample.resources.C.Dimension.SwipePage.DISPLAY_CARD_BACK
import com.android.sample.resources.C.Dimension.SwipePage.DISPLAY_CARD_FRONT
import com.android.sample.resources.C.Dimension.SwipePage.DURATION_ICON_SCALE
import com.android.sample.resources.C.Dimension.SwipePage.FILTER_ICON_SIZE
import com.android.sample.resources.C.Dimension.SwipePage.FILTER_ICON_WEIGHT
import com.android.sample.resources.C.Dimension.SwipePage.INITIAL_OFFSET_X
import com.android.sample.resources.C.Dimension.SwipePage.LIKE_DISLIKE_ANIMATION_ICON_SCALE_MAX
import com.android.sample.resources.C.Dimension.SwipePage.LIKE_DISLIKE_ANIMATION_ICON_SCALE_MIN
import com.android.sample.resources.C.Dimension.SwipePage.LIKE_DISLIKE_ANIMATION_PADDING_RATE
import com.android.sample.resources.C.Dimension.SwipePage.MAX_INTENSITY
import com.android.sample.resources.C.Dimension.SwipePage.MIN_INTENSITY
import com.android.sample.resources.C.Dimension.SwipePage.MIN_OFFSET_X
import com.android.sample.resources.C.Dimension.SwipePage.SCALE_END
import com.android.sample.resources.C.Dimension.SwipePage.SCALE_END_TIME
import com.android.sample.resources.C.Dimension.SwipePage.SCALE_MAX
import com.android.sample.resources.C.Dimension.SwipePage.SCALE_MAX_TIME
import com.android.sample.resources.C.Dimension.SwipePage.SCALE_UP
import com.android.sample.resources.C.Dimension.SwipePage.SCALE_UP_TIME
import com.android.sample.resources.C.Dimension.SwipePage.SCREEN_MIN
import com.android.sample.resources.C.Dimension.SwipePage.STAR_SIZE
import com.android.sample.resources.C.Dimension.SwipePage.STAR_WEIGHT
import com.android.sample.resources.C.Dimension.SwipePage.SWIPE_THRESHOLD
import com.android.sample.resources.C.Dimension.SwipePage.THRESHOLD_INTENSITY
import com.android.sample.resources.C.Tag.LOADING
import com.android.sample.resources.C.Tag.PADDING
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.SwipePage.END_ANIMATION
import com.android.sample.resources.C.Tag.SwipePage.INITIAL_DISPLAY_CARD_1
import com.android.sample.resources.C.Tag.SwipePage.INITIAL_DISPLAY_CARD_2
import com.android.sample.resources.C.Tag.SwipePage.INITIAL_DISPLAY_DISLIKE
import com.android.sample.resources.C.Tag.SwipePage.INITIAL_DISPLAY_LIKE
import com.android.sample.resources.C.Tag.SwipePage.INITIAL_IS_CLICKING
import com.android.sample.resources.C.Tag.SwipePage.INITIAL_RETRIEVE_NEXT_RECIPE
import com.android.sample.resources.C.Tag.SwipePage.RATE_VALUE
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.firebrickRed
import com.android.sample.ui.theme.graySlate
import com.android.sample.ui.theme.greenSwipe
import com.android.sample.ui.theme.jungleGreen
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
  var retrieveNextRecipe by remember { mutableStateOf(INITIAL_RETRIEVE_NEXT_RECIPE) }
  var displayCard1 by remember { mutableStateOf(INITIAL_DISPLAY_CARD_1) }
  var displayCard2 by remember { mutableStateOf(INITIAL_DISPLAY_CARD_2) }
  var isClicking by remember { mutableStateOf(INITIAL_IS_CLICKING) }
  var displayLike by remember { mutableStateOf(INITIAL_DISPLAY_LIKE) }
  var displayDisLike by remember { mutableStateOf(INITIAL_DISPLAY_DISLIKE) }

  // Offset for the swipe animation
  val offsetX = remember { Animatable(INITIAL_OFFSET_X) }

  val coroutineScope = rememberCoroutineScope()
  val density = LocalDensity.current.density
  val screenWidth = LocalConfiguration.current.screenWidthDp * density
  val swipeThreshold = screenWidth * SWIPE_THRESHOLD
  // Collect the current and next recipe from the ViewModel
  val currentRecipe by recipesViewModel.currentRecipe.collectAsState()
  val nextRecipe by recipesViewModel.nextRecipe.collectAsState()
  val filter by recipesViewModel.filter.collectAsState()

  // Snap back to center when animation is finished
  coroutineScope.launch {
    if (offsetX.value.absoluteValue > END_ANIMATION - ANIMATION_PADDING_SWIPE) {
      displayCard1 = !displayCard1
      displayCard2 = !displayCard2
      offsetX.snapTo(INITIAL_OFFSET_X)
    }
  }
  Box(
      modifier =
          Modifier.fillMaxSize().background(getBackgroundColor(offsetX.value, screenWidth))) {
        LikeDislikeIconAnimation(displayLike, displayLike || displayDisLike)

        Column(
            verticalArrangement = Arrangement.Top,
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(PADDING.dp)
                    .background(Color.Transparent)
                    .pointerInput(Unit) {
                      detectHorizontalDragGestures(
                          onDragStart = { isClicking = true },
                          onDragEnd = {
                            isClicking = false
                            if (kotlin.math.abs(offsetX.value) > swipeThreshold) {
                              retrieveNextRecipe = true
                              if (MIN_OFFSET_X < offsetX.value && currentRecipe != null) {
                                userViewModel.addRecipeToUserLikedRecipes(currentRecipe!!)
                              }
                            }
                          },
                          onDragCancel = { isClicking = false },
                          onHorizontalDrag = { _, dragAmount ->
                            coroutineScope.launch { offsetX.snapTo(offsetX.value + dragAmount) }
                          })
                    }) {
              Row(
                  horizontalArrangement = Arrangement.End,
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.fillMaxWidth().weight(FILTER_ICON_WEIGHT)) {
                    // Star Icon (fixed size, no weight needed)
                    Icon(
                        painter = painterResource(R.drawable.filter),
                        contentDescription = stringResource(R.string.filter_icon),
                        modifier =
                            Modifier.testTag("filter").size(FILTER_ICON_SIZE.dp).clickable {
                              navigationActions.navigateTo(Screen.FILTER)
                            },
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
                          .weight(CHIPS_WEIGHT)
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
                        contentDescription = stringResource(R.string.time_range_input_description))

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
                        contentDescription = stringResource(R.string.price_range_input_description))

                    FilterChip(
                        displayState = displayDifficulty,
                        onDelete = {
                          displayDifficulty = false
                          recipesViewModel.updateDifficulty(Difficulty.Undefined)
                        },
                        label = filter.difficulty.toString(),
                        testTag = "difficultyChip",
                        contentDescription = stringResource(R.string.difficulty_input_description))

                    FilterChip(
                        displayState = displayCategory,
                        onDelete = {
                          displayCategory = false
                          recipesViewModel.updateCategory(null)
                        },
                        label = filter.category.orEmpty(),
                        testTag = "categoryChip",
                        contentDescription = stringResource(R.string.category_input_description))
                  }

              // Space between the filter chips and the recipe cards
              Spacer(modifier = Modifier.size(SMALL_PADDING.dp))

              // First Recipe card with image
              Box(modifier = Modifier.weight(CARD_WEIGHT)) {
                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(SMALL_PADDING.dp)
                            .graphicsLayer(
                                translationX =
                                    if (displayCard1) offsetX.value else INITIAL_OFFSET_X)
                            .zIndex(if (displayCard1) DISPLAY_CARD_FRONT else DISPLAY_CARD_BACK),
                    shape = RoundedCornerShape(CORNER_RADIUS.dp),
                    elevation = CardDefaults.cardElevation(CARD_ELEVATION.dp)) {
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
                            .padding(SMALL_PADDING.dp)
                            .graphicsLayer(
                                translationX =
                                    if (displayCard2) offsetX.value else INITIAL_OFFSET_X)
                            .zIndex(if (displayCard2) DISPLAY_CARD_FRONT else DISPLAY_CARD_BACK),
                    shape = RoundedCornerShape(CORNER_RADIUS.dp),
                    elevation = CardDefaults.cardElevation(CARD_ELEVATION.dp)) {
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

              Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                ShawRecipeButton(navigationActions)
              }

              // Animate back to center if not swiped
              LaunchedEffect(offsetX.value) {
                if (!isClicking) {
                  val animationTarget =
                      when {
                        offsetX.value > swipeThreshold -> END_ANIMATION
                        offsetX.value < -swipeThreshold -> -END_ANIMATION
                        else -> INITIAL_OFFSET_X
                      }
                  displayLike = animationTarget == END_ANIMATION
                  displayDisLike = animationTarget == -END_ANIMATION

                  if (retrieveNextRecipe && offsetX.value == INITIAL_OFFSET_X) {
                    recipesViewModel.nextRecipe()
                    retrieveNextRecipe = false
                  }
                  offsetX.animateTo(animationTarget, animationSpec = tween(ANIMATION_SWIPE_TIME))
                }
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
      elevation = ButtonDefaults.buttonElevation(BUTTON_ELEVATION.dp),
      shape = RoundedCornerShape(BUTTON_RADIUS.dp),
      modifier =
          Modifier.padding(horizontal = SMALL_PADDING.dp, vertical = (SMALL_PADDING / 2).dp)
              .wrapContentSize()
              .testTag("viewRecipeButton")) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(BUTTON_PADDING.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier.padding(
                    horizontal = SMALL_PADDING.dp, vertical = (SMALL_PADDING / 2).dp)) {
              Image(
                  painter = painterResource(id = R.drawable.chef_s_hat),
                  contentDescription = "Chef's hat",
                  modifier = Modifier.size(BUTTON_COOK_SIZE.dp),
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
  val colorIntensity = (offsetX / screenWidth).coerceIn(MIN_INTENSITY, MAX_INTENSITY)

  return when {
    colorIntensity > THRESHOLD_INTENSITY -> {
      // Right swipe - green gradient
      Brush.horizontalGradient(
          colors = listOf(Color.Transparent, greenSwipe.copy(alpha = colorIntensity)),
          startX = SCREEN_MIN,
          endX = screenWidth)
    }
    colorIntensity < THRESHOLD_INTENSITY -> {
      // Left swipe - red gradient
      Brush.horizontalGradient(
          colors = listOf(Color.Transparent, redSwipe.copy(alpha = -colorIntensity)),
          startX = screenWidth,
          endX = SCREEN_MIN)
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
  Column(modifier = Modifier.padding(PADDING.dp)) {
    Row(
        modifier =
            Modifier.fillMaxWidth().testTag("draggableItem"), // Ensure the Row takes up full width
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {

          // Display Recipe Name
          Text(
              modifier =
                  Modifier.testTag("recipeName")
                      .weight(DESCRIPTION_WEIGHT), // Takes up 3 parts of the available space
              text = name,
              style = MaterialTheme.typography.bodyLarge.copy(fontSize = DESCRIPTION_FONT_SIZE.sp),
              color = MaterialTheme.colorScheme.onSecondary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis)

          // Row for the star and rate text
          Row(
              horizontalArrangement = Arrangement.End,
              verticalAlignment = Alignment.CenterVertically,
              modifier =
                  Modifier.weight(STAR_WEIGHT) // Takes 1 part of the space for icon and rating
              ) {
                // Star Icon (fixed size, no weight needed)
                Icon(
                    painter = painterResource(R.drawable.star_rate),
                    contentDescription = stringResource(R.string.star_rate_description),
                    modifier =
                        Modifier.testTag("recipeStar")
                            .size(STAR_SIZE.dp), // Use fixed size for the icon
                    tint = starColor)

                Spacer(
                    modifier =
                        Modifier.width(SMALL_PADDING.dp)) // Add spacing between icon and rate

                // Rating Text
                Text(
                    text = RATE_VALUE,
                    modifier = Modifier.testTag("recipeRate"),
                    style =
                        MaterialTheme.typography.bodyLarge.copy(
                            fontSize = DESCRIPTION_FONT_SIZE.sp),
                    color = MaterialTheme.colorScheme.onSecondary)
              }
        }

    Spacer(modifier = Modifier.padding((SMALL_PADDING / 4).dp))

    Row(verticalAlignment = Alignment.CenterVertically) { Tag(tag) }

    Spacer(modifier = Modifier.size((PADDING * 2 / 3).dp))
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
    Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
  }
}

@Composable
fun LikeDislikeIconAnimation(
    isLiked: Boolean, // true for like, false for dislike
    isDisplaying: Boolean,
) {

  val padding = LocalConfiguration.current.screenWidthDp * LIKE_DISLIKE_ANIMATION_PADDING_RATE

  // Choose the appropriate icon and color based on `isLiked` value
  val iconRes = if (isLiked) R.drawable.like else R.drawable.dislike
  val iconColor = if (isLiked) jungleGreen else firebrickRed
  val arrangement = if (isLiked) Arrangement.End else Arrangement.Start

  // Define animation properties
  val iconScale by
      animateFloatAsState(
          targetValue =
              if (isDisplaying) LIKE_DISLIKE_ANIMATION_ICON_SCALE_MAX
              else LIKE_DISLIKE_ANIMATION_ICON_SCALE_MIN,
          animationSpec =
              keyframes {
                durationMillis = DURATION_ICON_SCALE
                SCALE_UP at SCALE_UP_TIME
                SCALE_MAX at SCALE_MAX_TIME
                SCALE_END at SCALE_END_TIME
              },
          label = "scale animation")

  val iconOpacity by
      animateFloatAsState(
          targetValue = if (isDisplaying) ANIMATION_OPACITY_MAX else ANIMATION_OPACITY_MIN,
          animationSpec = tween(durationMillis = ANIMATION_OPACITY_TIME),
          label = "opacity animation")

  // Display the animated icon
  if (isDisplaying) {
    Row(
        horizontalArrangement = arrangement,
        modifier =
            Modifier.fillMaxWidth()
                .padding(start = padding.dp, end = padding.dp, top = ANIMATION_PADDING_TOP.dp)) {
          Icon(
              painter = painterResource(id = iconRes),
              contentDescription = if (isLiked) "Like" else "Dislike",
              tint = iconColor,
              modifier = Modifier.scale(iconScale).alpha(iconOpacity).zIndex(BACKGROUND_ANIMATION))
        }
  }
}
