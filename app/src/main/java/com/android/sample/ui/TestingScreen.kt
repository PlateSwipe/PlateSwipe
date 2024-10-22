package com.android.sample.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.model.user.UserViewModel
import com.android.sample.ui.navigation.NavigationActions

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
fun TestingScreen(
    navigationActions: NavigationActions,
    recipesViewModel: RecipesViewModel = viewModel(factory = RecipesViewModel.Factory),
    userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory)
) {
  // val selectedItem = navigationActions.currentRoute()

  Scaffold(
      modifier = Modifier.fillMaxWidth(),
  ) { paddingValues ->
    // RecipeDisplay(paddingValues, recipesViewModel, userViewModel)
    SimpleComposable(paddingValues)
  }
}

@Composable
private fun SimpleComposable(paddingValues: PaddingValues) {
  Column(
      modifier =
          Modifier.fillMaxSize().padding(paddingValues), // Respect padding provided by Scaffold
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Text(modifier = Modifier.testTag("textInput"), text = "Hello World")

        Button(
            onClick = { /*TODO*/},
            modifier =
                Modifier.testTag("button")
                    .padding(16.dp)
                    .size(200.dp, 60.dp)
                    .clip(RoundedCornerShape(8.dp)),
            content = { Text(text = "Click Me", modifier = Modifier.testTag("buttonText")) })

        RowComposable()
      }
}

@Composable
private fun RowComposable() {
  Row {
    Text(modifier = Modifier.testTag("Row1"), text = "Row1")
    Row {
      Text(modifier = Modifier.testTag("Row2"), text = "Row2")
      Row { Text(modifier = Modifier.testTag("Row3"), text = "Row3") }
    }
  }
}
/*
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
              .padding(paddingValues)
              .padding(dimensionResource(id = R.dimen.paddingBasic))
              .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                      if (kotlin.math.abs(offsetX.value) > swipeThreshold) {
                        isDescriptionVisible = false
                        recipesViewModel.nextRecipe()
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
                    .graphicsLayer(translationX = offsetX.value),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)) {
              Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.onPrimary)) {
                Image(
                    painter = rememberAsyncImagePainter(model = currentRecipe?.strMealThumbUrl),
                    contentDescription = stringResource(R.string.recipe_image),
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
            currentRecipe?.strMeal ?: LOADING,
            currentRecipe?.strCategory ?: LOADING,
            modifier = Modifier.clickable { isDescriptionVisible = !isDescriptionVisible })

        // Spacer to push content to bottom
        Spacer(modifier = Modifier.weight(1f))

        // The last column for recipe description at the bottom
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier =
                Modifier.verticalScroll(rememberScrollState())
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
                      .padding(top = 16.dp / 2)
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
          offsetX.animateTo(animationTarget, animationSpec = tween(50))
        }
      }
}

/**
 * Composable for the Image Description
 *
 * @param name - Recipe Name
 * @param tag - Recipe Tag
 * @param modifier - Modifier
 */
@Composable
private fun ImageDescription(name: String, tag: String, modifier: Modifier) {

  Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.paddingBasic))) {
    Row(
        modifier =
            modifier.fillMaxWidth().testTag("draggableItem"), // Ensure the Row takes up full width
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {

          // Display Recipe Name
          RecipeDescription(name, stringResource(R.string.rate))
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
      modifier =
          Modifier.testTag("recipeName")
              .width(
                  LocalConfiguration.current.screenWidthDp.dp -
                      dimensionResource(id = R.dimen.paddingBasic) -
                      dimensionResource(id = R.dimen.paddingBasic) -
                      dimensionResource(id = R.dimen.star_size)),
      text = name,
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.onSecondary,
  )

  Row(
      horizontalArrangement = Arrangement.End, modifier = Modifier // Padding for inside spacing
      ) {
        Icon(
            painter = painterResource(R.drawable.star_rate),
            contentDescription = stringResource(R.string.star_rate_description),
            modifier = Modifier.testTag("recipeStar"),
            tint = starColor)

        Spacer(modifier = Modifier.padding(5.dp))

        Text(
            text = rate,
            modifier = Modifier.testTag("recipeRate"),
            style = MaterialTheme.typography.bodyLarge,
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

 */
