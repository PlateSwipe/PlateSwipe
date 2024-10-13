package com.android.sample.ui.swipePage

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipePage(navigationActions: NavigationActions) {
  val selectedItem = navigationActions.currentRoute()

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("PlateSwipe") },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary))
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tab -> navigationActions.navigateTo(tab) },
            tabList = LIST_TOP_LEVEL_DESTINATIONS,
            selectedItem = selectedItem)
      }) { paddingValues ->
        ImageSwipeGallery(paddingValues)
      }
}

/**
 * Main Composable for the Image Gallery with Swipe feature
 *
 * @param paddingValues - Padding values for the column
 */
@Composable
fun ImageSwipeGallery(paddingValues: PaddingValues) {
  val height = LocalConfiguration.current.screenHeightDp.dp * 1 / 2
  val width = height * 3 / 4
  var isDescriptionVisible by remember {
    mutableStateOf(false)
  } // Tracks if the text should be visible

  var currentImageId by remember { mutableIntStateOf(R.drawable.burger) }
  val offsetX = remember { Animatable(0f) } // For tracking drag offset
  val coroutineScope = rememberCoroutineScope() // Coroutine scope for animations
  val context = LocalContext.current
  // Get the screen width to manage swipe gestures
  val screenWidth = LocalConfiguration.current.screenWidthDp.toFloat()
  // Handle gesture end to check if we need to change images
  val swipeThreshold = screenWidth / 4

  Column(
      modifier =
          Modifier.testTag("draggableItem")
              .fillMaxSize()
              .padding(paddingValues)
              .padding(16.dp)
              .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                      if (offsetX.value > swipeThreshold) {
                        // Right Swipe
                        isDescriptionVisible = false
                        currentImageId = setNextImage(currentImageId)
                        // hard coded toast for now
                        val toast = Toast.makeText(context, "Dislike", Toast.LENGTH_SHORT)
                        toast.show()
                      } else if (offsetX.value < -swipeThreshold) {
                        // Left Swipe
                        isDescriptionVisible = false
                        currentImageId = setNextImage(currentImageId)
                        // hard coded toast for now
                        val toast =
                            Toast.makeText(context, "Like", Toast.LENGTH_SHORT) // in Activity
                        toast.show()
                      }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                      coroutineScope.launch { offsetX.snapTo(offsetX.value + dragAmount) }
                    })
              }) {
        Card(
            modifier =
                Modifier.fillMaxWidth().padding(8.dp).graphicsLayer(translationX = offsetX.value),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)) {
              Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.onPrimary)) {
                Image(
                    painter = painterResource(id = currentImageId),
                    contentDescription = "Recipe Image",
                    modifier =
                        Modifier.testTag("recipeImage")
                            .fillMaxWidth()
                            .size(
                                width = width,
                                height = if (isDescriptionVisible) height * 1 / 2 else height),
                    contentScale = ContentScale.FillWidth,
                )
              }
            }
        // Display Recipe Description

        Description(
            isDescriptionVisible,
            modifier = Modifier.clickable { isDescriptionVisible = !isDescriptionVisible })

        // Add some space between the image and the description
        Spacer(modifier = Modifier.height(16.dp))

        // Swipe explanation
        Text(
            text = "Swipe to like or dislike the recipe.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.testTag("swipeUIDescription").padding(16.dp))

        // Animate back to center if not swiped
        LaunchedEffect(offsetX.value) {
          offsetX.animateTo(0f, animationSpec = tween(50)) // Animate back to center
        }
      }
}

/** Set the next image based on the current image. Hardcoded for now */
private fun setNextImage(current: Int): Int {
  if (current == R.drawable.burger) return R.drawable.salad
  return R.drawable.burger
}

/**
 * Description of the recipe
 *
 * @param isDescriptionVisible - Tracks if the description should be visible
 * @param modifier - Modifier for the column
 */
@Composable
private fun Description(isDescriptionVisible: Boolean, modifier: Modifier) {

  Column(modifier = Modifier.testTag("recipeDescription").padding(16.dp)) {

    // Display Recipe Name
    Text(
        modifier = modifier,
        text = "Pasta",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSecondary,
    )
    if (!isDescriptionVisible) {
      // Display "..." if the description is not visible
      Text(
          modifier = modifier.testTag("displayDescription"),
          text = "...",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSecondary,
      )
    }
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
      // Display with animation Recipe Description
      AnimatedVisibility(
          visible = isDescriptionVisible, enter = fadeIn(animationSpec = tween(500))) {
            Text(
                modifier = Modifier.testTag("RecipeEntireDescription"),
                text = getRecipeDescription(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondary,
            )
          }

      Spacer(modifier = Modifier.padding(5.dp))

      // Display Recipe Timing
      Row(modifier = Modifier.padding(16.dp)) {
        Icon(painter = painterResource(R.drawable.time_line), contentDescription = "recipes timing")
        Text(
            text = "30min",
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onSecondary,
        )
      }
    }
  }
}

/** @return The recipe description hard coded for now */
@Composable
private fun getRecipeDescription(): String {
  return "Delicious pasta with tomato sauce. In a separate pan, prepare the sauce with garlic, onions, and fresh tomatoes. Combine the cooked pasta with the sauce, and serve hot. Enjoy your meal! Ingredients: Basil, Tomato, Pasta" +
      "Delicious pasta with tomato sauce. In a separate pan, prepare the sauce with garlic, onions, and fresh tomatoes. Combine the cooked pasta with the sauce, and serve hot. Enjoy your meal! Ingredients: Basil, Tomato, Pasta" +
      "Delicious pasta with tomato sauce. In a separate pan, prepare the sauce with garlic, onions, and fresh tomatoes. Combine the cooked pasta with the sauce, and serve hot. Enjoy your meal! Ingredients: Basil, Tomato, Pasta"
}
