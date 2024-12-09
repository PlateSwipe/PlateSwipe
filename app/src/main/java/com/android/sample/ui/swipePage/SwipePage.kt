package com.android.sample.ui.swipePage

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
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
import androidx.compose.ui.platform.LocalContext
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
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C.Dimension.SwipePage.ANIMATION_OPACITY_MAX
import com.android.sample.resources.C.Dimension.SwipePage.ANIMATION_OPACITY_MIN
import com.android.sample.resources.C.Dimension.SwipePage.ANIMATION_OPACITY_TIME
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
import com.android.sample.resources.C.Tag.Filter.UNINITIALIZED_BORN_VALUE
import com.android.sample.resources.C.Tag.LOADING
import com.android.sample.resources.C.Tag.PADDING
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Tag.SwipePage.DISLIKE
import com.android.sample.resources.C.Tag.SwipePage.END_ANIMATION
import com.android.sample.resources.C.Tag.SwipePage.HAT
import com.android.sample.resources.C.Tag.SwipePage.INITIAL_DISPLAY_CARD_1
import com.android.sample.resources.C.Tag.SwipePage.INITIAL_DISPLAY_DISLIKE
import com.android.sample.resources.C.Tag.SwipePage.INITIAL_DISPLAY_LIKE
import com.android.sample.resources.C.Tag.SwipePage.INITIAL_IS_CLICKING
import com.android.sample.resources.C.Tag.SwipePage.INITIAL_RETRIEVE_NEXT_RECIPE
import com.android.sample.resources.C.Tag.SwipePage.LIKE
import com.android.sample.resources.C.Tag.SwipePage.OPACITY_LABEL
import com.android.sample.resources.C.Tag.SwipePage.RATE_VALUE
import com.android.sample.resources.C.Tag.SwipePage.SCALE_LABEL
import com.android.sample.resources.C.TestTag.SwipePage.CATEGORY_CHIP
import com.android.sample.resources.C.TestTag.SwipePage.DELETE_SUFFIX
import com.android.sample.resources.C.TestTag.SwipePage.DIFFICULTY_CHIP
import com.android.sample.resources.C.TestTag.SwipePage.DRAGGABLE_ITEM
import com.android.sample.resources.C.TestTag.SwipePage.FILTER
import com.android.sample.resources.C.TestTag.SwipePage.FILTER_ROW
import com.android.sample.resources.C.TestTag.SwipePage.RECIPE_IMAGE_1
import com.android.sample.resources.C.TestTag.SwipePage.RECIPE_IMAGE_2
import com.android.sample.resources.C.TestTag.SwipePage.RECIPE_NAME
import com.android.sample.resources.C.TestTag.SwipePage.RECIPE_RATE
import com.android.sample.resources.C.TestTag.SwipePage.RECIPE_STAR
import com.android.sample.resources.C.TestTag.SwipePage.TIME_RANGE_CHIP
import com.android.sample.resources.C.TestTag.SwipePage.VIEW_RECIPE_BUTTON
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.firebrickRed
import com.android.sample.ui.theme.graySlate
import com.android.sample.ui.theme.greenSwipe
import com.android.sample.ui.theme.jungleGreen
import com.android.sample.ui.theme.redSwipe
import com.android.sample.ui.theme.starColor
import com.android.sample.ui.utils.PlateSwipeScaffold
import com.android.sample.ui.utils.Tag
import com.android.sample.utils.NetworkUtils
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.launch

/**
 * Composable for the Swipe Page
 *
 * @param navigationActions - Navigation Actions
 * @param recipesViewModel - Recipes View Model
 * @param userViewModel - User View Model
 */
@Composable
fun SwipePage(
    navigationActions: NavigationActions,
    recipesViewModel: RecipesViewModel =
        viewModel(factory = RecipesViewModel.provideFactory(LocalContext.current)),
    userViewModel: UserViewModel =
        viewModel(factory = UserViewModel.provideFactory(context = LocalContext.current))
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
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun RecipeDisplay(
    navigationActions: NavigationActions,
    paddingValues: PaddingValues,
    recipesViewModel: RecipesViewModel,
    userViewModel: UserViewModel
) {
  var retrieveNextRecipe by remember { mutableStateOf(INITIAL_RETRIEVE_NEXT_RECIPE) }
  var displayCard1 by remember { mutableStateOf(INITIAL_DISPLAY_CARD_1) }
  var isClicking by remember { mutableStateOf(INITIAL_IS_CLICKING) }
  var isSwiping by remember { mutableStateOf(false) }
  var displayLike by remember { mutableStateOf(INITIAL_DISPLAY_LIKE) }
  var displayDisLike by remember { mutableStateOf(INITIAL_DISPLAY_DISLIKE) }

  // Offset for the swipe animation
  val offsetXCard1 = remember { Animatable(INITIAL_OFFSET_X) }
  val offsetXCard2 = remember { Animatable(INITIAL_OFFSET_X) }

  val coroutineScope = rememberCoroutineScope()
  val density = LocalDensity.current.density
  val screenWidth = LocalConfiguration.current.screenWidthDp * density
  val swipeThreshold = screenWidth * SWIPE_THRESHOLD
  // Collect the current and next recipe from the ViewModel
  val currentRecipe by recipesViewModel.currentRecipe.collectAsState()
  val nextRecipe by recipesViewModel.nextRecipe.collectAsState()
  val filter by recipesViewModel.filter.collectAsState()
  val context = LocalContext.current
  val isConnected = NetworkUtils().isNetworkAvailable(context)
  Box(
      modifier =
          Modifier.fillMaxSize()
              .background(
                  getBackgroundColor(
                      if (displayCard1) offsetXCard1.value else offsetXCard2.value, screenWidth))) {
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
                            manageRecipeLiked(
                                offsetXCard1,
                                displayCard1,
                                swipeThreshold,
                                currentRecipe,
                                userViewModel) {
                                  retrieveNextRecipe = true
                                }
                            manageRecipeLiked(
                                offsetXCard2,
                                !displayCard1,
                                swipeThreshold,
                                currentRecipe,
                                userViewModel) {
                                  retrieveNextRecipe = true
                                }
                          },
                          onDragCancel = {
                            // animate the card if the user release the drag without moving
                            isClicking = false
                          },
                          onHorizontalDrag = { _, dragAmount ->
                            coroutineScope.launch {
                              // need to always update both as coroutine scope have small delay
                              if (displayCard1) {
                                offsetXCard1.snapTo(
                                    if (!isSwiping) offsetXCard1.value + dragAmount
                                    else getTarget(offsetXCard1, dragAmount, swipeThreshold))
                              } else {
                                offsetXCard2.snapTo(
                                    if (!isSwiping) offsetXCard2.value + dragAmount
                                    else getTarget(offsetXCard2, dragAmount, swipeThreshold))
                              }
                            }
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
                            Modifier.testTag(FILTER).size(FILTER_ICON_SIZE.dp).clickable {
                              recipesViewModel.initFilter()
                              navigationActions.navigateTo(Screen.FILTER)
                            },
                        tint = graySlate)
                  }
              Row(
                  horizontalArrangement = Arrangement.Start,
                  verticalAlignment = Alignment.CenterVertically,
                  modifier =
                      Modifier.testTag(FILTER_ROW)
                          .fillMaxWidth()
                          .weight(CHIPS_WEIGHT)
                          .horizontalScroll(rememberScrollState())) {
                    // Star Icon (fixed size, no weight needed)
                    var displayTimeRange by remember {
                      mutableStateOf(!filter.timeRange.isLimited())
                    }

                    var displayDifficulty by remember {
                      mutableStateOf(filter.difficulty != Difficulty.Undefined)
                    }
                    var displayCategory by remember { mutableStateOf(filter.category != null) }

                    FilterChip(
                        displayState = displayTimeRange,
                        onDelete = {
                          displayTimeRange = false
                          if (checkIfFilterUsed(
                              displayTimeRange,
                              displayDifficulty,
                              displayCategory,
                              recipesViewModel)) {
                            recipesViewModel.updateTimeRange(
                                UNINITIALIZED_BORN_VALUE, UNINITIALIZED_BORN_VALUE)
                          }
                          recipesViewModel.applyChanges()
                        },
                        label =
                            "${filter.timeRange.min.toInt()} - ${filter.timeRange.max.toInt()} min",
                        testTag = TIME_RANGE_CHIP,
                        contentDescription = stringResource(R.string.time_range_name))

                    FilterChip(
                        displayState = displayDifficulty,
                        onDelete = {
                          displayDifficulty = false
                          if (checkIfFilterUsed(
                              displayTimeRange,
                              displayDifficulty,
                              displayCategory,
                              recipesViewModel)) {
                            recipesViewModel.updateDifficulty(Difficulty.Undefined)
                          }
                          recipesViewModel.applyChanges()
                        },
                        label = filter.difficulty.toString(),
                        testTag = DIFFICULTY_CHIP,
                        contentDescription = stringResource(R.string.difficulty_name))

                    FilterChip(
                        displayState = displayCategory,
                        onDelete = {
                          displayCategory = false
                          if (checkIfFilterUsed(
                              displayTimeRange,
                              displayDifficulty,
                              displayCategory,
                              recipesViewModel)) {
                            recipesViewModel.updateCategory(null)
                          }
                          recipesViewModel.applyChanges()
                        },
                        label = filter.category.orEmpty(),
                        testTag = CATEGORY_CHIP,
                        contentDescription = stringResource(R.string.category_name))
                  }

              // Space between the filter chips and the recipe cards
              Spacer(modifier = Modifier.size(SMALL_PADDING.dp))

              // First Recipe card with image
              Box(modifier = Modifier.weight(CARD_WEIGHT)) {
                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(SMALL_PADDING.dp)
                            .zIndex(if (displayCard1) DISPLAY_CARD_FRONT else DISPLAY_CARD_BACK)
                            .graphicsLayer(translationX = offsetXCard1.value),
                    shape = RoundedCornerShape(CORNER_RADIUS.dp),
                    elevation = CardDefaults.cardElevation(CARD_ELEVATION.dp)) {
                      Column(
                          modifier =
                              Modifier.background(color = MaterialTheme.colorScheme.onPrimary)) {
                            Image(
                                painter =
                                    rememberAsyncImagePainter(
                                        model =
                                            if (displayCard1) currentRecipe?.url
                                            else nextRecipe?.url),
                                contentDescription = stringResource(R.string.recipe_image),
                                modifier = Modifier.fillMaxSize().testTag(RECIPE_IMAGE_1),
                                contentScale = ContentScale.Crop,
                            )
                          }
                    }

                // Second Recipe card with image
                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(SMALL_PADDING.dp)
                            .zIndex(if (!displayCard1) DISPLAY_CARD_FRONT else DISPLAY_CARD_BACK)
                            .graphicsLayer(translationX = offsetXCard2.value),
                    shape = RoundedCornerShape(CORNER_RADIUS.dp),
                    elevation = CardDefaults.cardElevation(CARD_ELEVATION.dp)) {
                      Column(
                          modifier =
                              Modifier.background(color = MaterialTheme.colorScheme.onPrimary)) {
                            Image(
                                painter =
                                    rememberAsyncImagePainter(
                                        model =
                                            if (!displayCard1) currentRecipe?.url
                                            else nextRecipe?.url),
                                contentDescription = stringResource(R.string.recipe_image),
                                modifier = Modifier.fillMaxSize().testTag(RECIPE_IMAGE_2),
                                contentScale = ContentScale.Crop,
                            )
                          }
                    }
              }

              // Image Description
              ImageDescription(currentRecipe?.name ?: LOADING, currentRecipe?.category ?: LOADING)

              Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                ShawRecipeButton(navigationActions)
              }

              LaunchedEffect(offsetXCard1.value, isClicking) {
                animateCard(
                    offsetX = offsetXCard1,
                    screenWidth = screenWidth,
                    isClicking = isClicking,
                    retrieveNextRecipe = retrieveNextRecipe,
                    recipesViewModel = recipesViewModel,
                    updateDisplayCard = { displayCard1 = false },
                    displayIcons = { animationTarget ->
                      displayLike = animationTarget == END_ANIMATION
                      displayDisLike = animationTarget == -END_ANIMATION
                    },
                    blockRetrieveNextRecipe = { retrieveNextRecipe = false },
                    startSwipe = { isSwiping = true },
                    endSwipe = { isSwiping = false })
              }
              LaunchedEffect(offsetXCard2.value, isClicking) {
                animateCard(
                    offsetX = offsetXCard2,
                    screenWidth = screenWidth,
                    isClicking = isClicking,
                    retrieveNextRecipe = retrieveNextRecipe,
                    recipesViewModel = recipesViewModel,
                    updateDisplayCard = { displayCard1 = true },
                    displayIcons = { animationTarget ->
                      displayLike = animationTarget == END_ANIMATION
                      displayDisLike = animationTarget == -END_ANIMATION
                    },
                    blockRetrieveNextRecipe = { retrieveNextRecipe = true },
                    startSwipe = { isSwiping = true },
                    endSwipe = { isSwiping = false })
              }
            }
      }
}

fun checkIfFilterUsed(
    time: Boolean,
    difficulty: Boolean,
    category: Boolean,
    recipesViewModel: RecipesViewModel
): Boolean {
  Log.d("SwipePage", "checkIfFilterUsed $time $difficulty $category")
  if ((!time) && (!difficulty) && (!category)) {
    recipesViewModel.resetFilters()
    return false
  }
  return true
}

/**
 * limit the swipe gesture if the user is spamming the swipe
 *
 * @param offsetX - Offset for the swipe animation
 * @param dragAmount - Drag amount
 * @param swipeThreshold - Swipe Threshold
 */
private fun getTarget(
    offsetX: Animatable<Float, AnimationVector1D>,
    dragAmount: Float,
    swipeThreshold: Float
): Float {
  return if (offsetX.value > 0) {
    max(offsetX.value + dragAmount, swipeThreshold)
  } else {
    min(offsetX.value + dragAmount, -swipeThreshold)
  }
}

/**
 * Manage the Recipe Liked state
 *
 * @param offsetX - Offset for the swipe animation
 * @param retrieveRecipeAuthorized - Retrieve Recipe Authorized state
 * @param swipeThreshold - Swipe Threshold
 * @param currentRecipe - Current Recipe
 * @param userViewModel - User View Model
 * @param authorizeRetrieveNextRecipe - Authorize Retrieve Next Recipe function
 */
private fun manageRecipeLiked(
    offsetX: Animatable<Float, AnimationVector1D>,
    retrieveRecipeAuthorized: Boolean,
    swipeThreshold: Float,
    currentRecipe: Recipe?,
    userViewModel: UserViewModel,
    authorizeRetrieveNextRecipe: () -> Unit
) {
  if (abs(offsetX.value) > swipeThreshold) {
    authorizeRetrieveNextRecipe()
    if (retrieveRecipeAuthorized && MIN_OFFSET_X < offsetX.value && currentRecipe != null) {
      userViewModel.addRecipeToUserLikedRecipes(currentRecipe)
    }
  }
}

/**
 * Animate the Swipe of the recipe card
 *
 * @param offsetX - Offset for the swipe animation
 * @param screenWidth - Screen width
 * @param isClicking - Clicking state
 * @param retrieveNextRecipe - Retrieve next recipe state
 * @param recipesViewModel - Recipes View Model
 * @param updateDisplayCard - Update Display Card function
 * @param displayIcons - Display Icons function
 * @param blockRetrieveNextRecipe - Block Retrieve Next Recipe function
 */
private suspend fun animateCard(
    offsetX: Animatable<Float, AnimationVector1D>,
    screenWidth: Float,
    isClicking: Boolean,
    retrieveNextRecipe: Boolean,
    recipesViewModel: RecipesViewModel,
    updateDisplayCard: () -> Unit,
    displayIcons: (Float) -> Unit,
    blockRetrieveNextRecipe: () -> Unit,
    startSwipe: () -> Unit,
    endSwipe: () -> Unit
) {
  val swipeThreshold = screenWidth * SWIPE_THRESHOLD

  // Check if the card has been swiped and animate it back to the initial position
  if (abs(offsetX.value) > screenWidth) {

    if (retrieveNextRecipe) {
      recipesViewModel.nextRecipe()
      // block the retrieve next recipe to avoid multiple calls during the animation
      blockRetrieveNextRecipe()
    }

    updateDisplayCard()
    endSwipe()
    offsetX.snapTo(INITIAL_OFFSET_X)
  }

  // ensure the card doesn't update anything while the animation is running
  if (!isClicking) {
    val animationTarget =
        when {
          offsetX.value > swipeThreshold -> END_ANIMATION
          offsetX.value < -swipeThreshold -> -END_ANIMATION
          else -> INITIAL_OFFSET_X
        }
    if (animationTarget != INITIAL_OFFSET_X) {
      startSwipe()
    }
    displayIcons(animationTarget)

    // update the displayed recipe
    offsetX.animateTo(animationTarget, animationSpec = tween(ANIMATION_SWIPE_TIME))
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
              .testTag(VIEW_RECIPE_BUTTON)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(BUTTON_PADDING.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier.padding(
                    horizontal = SMALL_PADDING.dp, vertical = (SMALL_PADDING / 2).dp)) {
              Image(
                  painter = painterResource(id = R.drawable.chef_s_hat),
                  contentDescription = HAT,
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

/**
 * Composable for the Background Gradient
 *
 * @param offsetX - Offset for the swipe animation
 * @param screenWidth - Screen width
 */
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
            Modifier.fillMaxWidth().testTag(DRAGGABLE_ITEM), // Ensure the Row takes up full width
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {

          // Display Recipe Name
          Text(
              modifier =
                  Modifier.testTag(RECIPE_NAME)
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
                        Modifier.testTag(RECIPE_STAR)
                            .size(STAR_SIZE.dp), // Use fixed size for the icon
                    tint = starColor)

                Spacer(
                    modifier =
                        Modifier.width(SMALL_PADDING.dp)) // Add spacing between icon and rate

                // Rating Text
                Text(
                    text = RATE_VALUE,
                    modifier = Modifier.testTag(RECIPE_RATE),
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
                  Modifier.testTag("${testTag}$DELETE_SUFFIX")
                      .size(InputChipDefaults.IconSize)
                      .clickable { onDelete() },
              tint = MaterialTheme.colorScheme.onSecondary)
        })
    Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
  }
}

/**
 * Composable for the Like and Dislike Icon Animation
 *
 * @param isLiked - True for Like, False for Dislike
 * @param isDisplaying - True if the icon is displaying
 */
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
          label = SCALE_LABEL)

  val iconOpacity by
      animateFloatAsState(
          targetValue = if (isDisplaying) ANIMATION_OPACITY_MAX else ANIMATION_OPACITY_MIN,
          animationSpec = tween(durationMillis = ANIMATION_OPACITY_TIME),
          label = OPACITY_LABEL)

  // Display the animated icon
  if (isDisplaying) {
    Row(
        horizontalArrangement = arrangement,
        modifier =
            Modifier.fillMaxWidth()
                .padding(start = padding.dp, end = padding.dp, top = ANIMATION_PADDING_TOP.dp)) {
          Icon(
              painter = painterResource(id = iconRes),
              contentDescription = if (isLiked) LIKE else DISLIKE,
              tint = iconColor,
              modifier = Modifier.scale(iconScale).alpha(iconOpacity).zIndex(BACKGROUND_ANIMATION))
        }
  }
}
