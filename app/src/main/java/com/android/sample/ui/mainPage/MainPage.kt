package com.android.sample.ui.mainPage

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
fun MainPage(navigationActions: NavigationActions) {
  val selectedItem = navigationActions.currentRoute()

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text("PlateSwipe", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
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
        ImageSwipeToDismissGallery(paddingValues)
      }
}

@Composable
fun ImageSwipeToDismissGallery(paddingValues: PaddingValues) {
  val height = LocalConfiguration.current.screenHeightDp.dp * 1 / 2
  val width = height * 3 / 4
  var isTextVisible by remember { mutableStateOf(false) } // Tracks if the text should be visible

  var currentId by remember { mutableIntStateOf(R.drawable.burger) }
  val offsetX = remember { Animatable(0f) } // For tracking drag offset
  var isDragging by remember { mutableStateOf(false) } // Flag to track if currently dragging
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current

  Column(
      modifier =
          Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).pointerInput(Unit) {
            detectHorizontalDragGestures(
                onDragStart = { isDragging = true },
                onDragEnd = { isDragging = false },
                onDragCancel = { isDragging = false },
                onHorizontalDrag = { change, dragAmount ->
                  change.consume() // Consume gesture so it doesn't propagate further
                  coroutineScope.launch { offsetX.snapTo(offsetX.value + dragAmount) }
                })
          }) {
        Card(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(8.dp)
                    .graphicsLayer(
                        translationX = offsetX.value), // Apply translation based on drag offset ,
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
        ) {
          Column {
            Image(
                painter = painterResource(id = currentId),
                contentDescription = "Recipe Image",
                modifier =
                    Modifier.fillMaxWidth().let {
                      if (isTextVisible) it.size(width, height * 1 / 2) else it.size(width, height)
                    },
                contentScale = ContentScale.FillWidth,
            )

            Description(
                isTextVisible, modifier = Modifier.clickable { isTextVisible = !isTextVisible })
          }
        }

        // Swipe Instructions
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Swipe to like or dislike the recipe.",
            color = Color.Gray,
            modifier = Modifier.padding(16.dp))

        // Handle gesture end to check if we need to change images
        LaunchedEffect(offsetX.value) {
          if (isDragging) {
            if (offsetX.value > 150) {
              // Right Swipe
              isDragging = false
              isTextVisible = false
              currentId = setNextImage(currentId)
              val toast = Toast.makeText(context, "Dislike", Toast.LENGTH_SHORT) // in Activity
              toast.show()
              offsetX.animateTo(0f, animationSpec = tween(50)) // Animate back to center
            } else if (offsetX.value < -150) {
              // Left Swipe
              isDragging = false
              isTextVisible = false
              currentId = setNextImage(currentId)
              val toast = Toast.makeText(context, "Like", Toast.LENGTH_SHORT) // in Activity
              toast.show()
              offsetX.animateTo(0f, animationSpec = tween(50)) // Animate back to center
            }
          } else {
            // If not swiped enough, animate back to center
            offsetX.animateTo(0f, animationSpec = tween(50))
          }
        }
      }
}

private fun setNextImage(current: Int): Int {
  if (current == R.drawable.burger) return R.drawable.salad
  return R.drawable.burger
}

@Composable
private fun Description(isTextVisible: Boolean, modifier: Modifier) {

  Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {

    // Small Description always visible
    Text(text = "Pasta", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Text(modifier = modifier, text = "...", fontSize = 24.sp, fontWeight = FontWeight.Bold)

    Row(modifier = Modifier.padding(5.dp)) {
      Icon(painter = painterResource(R.drawable.time_line), contentDescription = "recipes timing")
      Text(
          modifier = Modifier.padding(start = 5.dp),
          text = "30min",
          fontSize = 22.sp,
      )
    }

    // Animate when Displaying
    AnimatedVisibility(visible = isTextVisible, enter = fadeIn(animationSpec = tween(500))) {
      Text(text = getRecipeDescription(), fontSize = 16.sp)
    }
  }
}

@Composable
private fun getRecipeDescription(): String {
  return "Delicious pasta with tomato sauce. In a separate pan, prepare the sauce with garlic, onions, and fresh tomatoes. Combine the cooked pasta with the sauce, and serve hot. Enjoy your meal! Ingredients: Basil, Tomato, Pasta" +
      "Delicious pasta with tomato sauce. In a separate pan, prepare the sauce with garlic, onions, and fresh tomatoes. Combine the cooked pasta with the sauce, and serve hot. Enjoy your meal! Ingredients: Basil, Tomato, Pasta"
}
