package com.android.sample.ui.recipeOverview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.android.sample.R
import com.android.sample.animation.LoadingCook
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipeOverviewViewModel
import com.android.sample.resources.C.Dimension.RecipeOverview.COUNTER_MIN_MAX_SIZE
import com.android.sample.resources.C.Dimension.RecipeOverview.COUNTER_ROUND_CORNER
import com.android.sample.resources.C.Dimension.RecipeOverview.IMAGE_ROUND_CORNER
import com.android.sample.resources.C.Dimension.RecipeOverview.OVERVIEW_CHECKBOX_SIZE
import com.android.sample.resources.C.Dimension.RecipeOverview.OVERVIEW_COUNTER_TEXT_SIZE
import com.android.sample.resources.C.Dimension.RecipeOverview.OVERVIEW_INSTRUCTION_BOTTOM
import com.android.sample.resources.C.Dimension.RecipeOverview.OVERVIEW_INSTRUCTION_END
import com.android.sample.resources.C.Dimension.RecipeOverview.OVERVIEW_INSTRUCTION_START
import com.android.sample.resources.C.Dimension.RecipeOverview.OVERVIEW_INSTRUCTION_TOP
import com.android.sample.resources.C.Dimension.RecipeOverview.OVERVIEW_MAX_COUNTER_VALUE
import com.android.sample.resources.C.Dimension.RecipeOverview.OVERVIEW_MIN_COUNTER_VALUE
import com.android.sample.resources.C.Dimension.RecipeOverview.OVERVIEW_RECIPE_CARD_ELEVATION
import com.android.sample.resources.C.Dimension.RecipeOverview.OVERVIEW_RECIPE_CARD_SHAPE
import com.android.sample.resources.C.Dimension.RecipeOverview.OVERVIEW_RECIPE_COUNTER_PADDING
import com.android.sample.resources.C.Dimension.RecipeOverview.OVERVIEW_RECIPE_RATE
import com.android.sample.resources.C.Dimension.RecipeOverview.OVERVIEW_RECIPE_ROUND
import com.android.sample.resources.C.Dimension.RecipeOverview.OVERVIEW_RECIPE_ROUND_ROW
import com.android.sample.resources.C.Dimension.RecipeOverview.OVERVIEW_RECIPE_STAR_SIZE
import com.android.sample.resources.C.Dimension.RecipeOverview.OVERVIEW_TIME_DISPLAY_RATE
import com.android.sample.resources.C.Tag.LOADING
import com.android.sample.resources.C.Tag.PADDING
import com.android.sample.resources.C.Tag.SMALL_PADDING
import com.android.sample.resources.C.Values.RecipeOverview.INITIAL_NUMBER_VALUE_PER_RECIPE
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.starColor
import com.android.sample.ui.utils.PlateSwipeScaffold
import com.android.sample.ui.utils.Tag

@Composable
fun RecipeOverview(
    navigationActions: NavigationActions,
    recipeOverviewViewModel: RecipeOverviewViewModel
) {
  val currentRecipe by recipeOverviewViewModel.currentRecipe.collectAsState()

  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = navigationActions.currentRoute(),
      showBackArrow = true,
      content = { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier.testTag("draggableItem")
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())) {
              if (currentRecipe != null) {
                LoadingCook()
                Spacer(modifier = Modifier.size(SMALL_PADDING.dp))
                Text(
                    text = LOADING,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondary)
              } else {
                currentRecipe?.let { recipe ->
                  RecipeImage(recipe)
                  RecipeDescription(recipe)
                  PrepareCookTotalTimeDisplay()
                  RecipeInformation(recipe)
                }
              }
            }
      })
}

/**
 * Display of the recipe information
 *
 * @param currentRecipe: The recipe to display
 */
@Composable
private fun RecipeInformation(currentRecipe: Recipe) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(SMALL_PADDING.dp),
      modifier =
          Modifier.background(
                  MaterialTheme.colorScheme.primary,
                  shape = RoundedCornerShape(OVERVIEW_RECIPE_ROUND))
              .fillMaxSize()
              .padding(SMALL_PADDING.dp)) {
        // Variable to track the current selection
        var isIngredientDisplay by remember { mutableStateOf(true) }

        // Custom switch style with two button-like labels
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier =
                Modifier.padding(vertical = SMALL_PADDING.dp)
                    .background(
                        MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(OVERVIEW_RECIPE_ROUND_ROW.dp))) {
              SlidingButton(
                  !isIngredientDisplay,
                  stringResource(R.string.ingredients),
                  "ingredientButton",
                  Modifier.weight(1f),
                  clickable = { isIngredientDisplay = true })

              SlidingButton(
                  isIngredientDisplay,
                  stringResource(R.string.instructions),
                  "instructionsButton",
                  Modifier.weight(1f),
                  clickable = { isIngredientDisplay = false })
            }

        // Display the appropriate view based on the toggle state
        IngredientInstructionView(isIngredientDisplay, currentRecipe)
      }
}

/**
 * Custom button that slides to the left or right based on the selection
 *
 * @param isIngredientDisplay: Boolean to determine if the ingredient is displayed
 * @param text: Text to display on the button
 * @param testTag: Test tag for the button
 * @param modifier: Modifier to apply to the button
 */
@Composable
private fun SlidingButton(
    isIngredientDisplay: Boolean,
    text: String,
    testTag: String,
    modifier: Modifier = Modifier,
    clickable: () -> Unit
) {
  Box(
      modifier =
          modifier
              .background(
                  if (!isIngredientDisplay) MaterialTheme.colorScheme.onBackground
                  else Color.Transparent,
                  shape = RoundedCornerShape(IMAGE_ROUND_CORNER.dp))
              .clickable { clickable() }
              .padding(vertical = SMALL_PADDING.dp),
      contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color =
                if (!isIngredientDisplay) MaterialTheme.colorScheme.background
                else MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(SMALL_PADDING.dp).testTag(testTag))
      }
}

/**
 * Display of the recipe image, title, rating and category
 *
 * @param currentRecipe: The recipe to display
 */
@Composable
private fun RecipeImage(currentRecipe: Recipe) {
  val height = LocalConfiguration.current.screenHeightDp.dp * OVERVIEW_RECIPE_RATE

  Card(
      modifier = Modifier.fillMaxWidth().padding(SMALL_PADDING.dp),
      shape = RoundedCornerShape(OVERVIEW_RECIPE_CARD_SHAPE.dp),
      elevation = CardDefaults.cardElevation(OVERVIEW_RECIPE_CARD_ELEVATION.dp)) {
        Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.onPrimary)) {
          Image(
              painter = rememberAsyncImagePainter(model = currentRecipe.strMealThumbUrl),
              contentDescription = "Recipe Image",
              modifier = Modifier.fillMaxWidth().height(height).testTag("recipeImage"),
              contentScale = ContentScale.Crop,
          )
        }
      }
}

/**
 * Display of the recipe title, rating and category
 *
 * @param currentRecipe: The recipe to display
 */
@Composable
private fun RecipeDescription(currentRecipe: Recipe) {
  Column {
    Text(
        text = currentRecipe.strMeal,
        modifier = Modifier.testTag("recipeTitle"),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSecondary)

    Spacer(modifier = Modifier.size(SMALL_PADDING.dp))
    currentRecipe.strCategory?.let { Tag(it) }
    Spacer(modifier = Modifier.size(SMALL_PADDING.dp))
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.testTag("ratingIcon")) {
          // Display of the rating Icon
          Icon(
              painter = painterResource(R.drawable.star_rate),
              contentDescription = stringResource(R.string.star_rate_description),
              modifier = Modifier.testTag("recipeStar").size(OVERVIEW_RECIPE_STAR_SIZE.dp),
              tint = starColor)

          Spacer(modifier = Modifier.size(SMALL_PADDING.dp))

          // Rating Text
          Text(
              text = stringResource(R.string.rate),
              modifier = Modifier.testTag("recipeRate"),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSecondary)
        }
  }
}

/** Display of the preparation, cooking and total time */
@Composable
private fun PrepareCookTotalTimeDisplay() {
  val padding = LocalConfiguration.current.screenWidthDp.dp * OVERVIEW_TIME_DISPLAY_RATE

  Row(
      modifier =
          Modifier.padding(padding, padding)
              .background(
                  MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(IMAGE_ROUND_CORNER))
              .fillMaxWidth()
              .padding(padding / 2),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        // Display of the preparation time
        RecipePropertyRowText(stringResource(R.string.prep_time), "30 min", "prepTimeText")
        // Display of the cooking time
        RecipePropertyRowText(stringResource(R.string.cook_time), "20 min", "cookTimeText")
        // Display of the total time that it takes
        RecipePropertyRowText(stringResource(R.string.total_time), "50 min", "totalTimeText")
      }
}

/**
 * Display of the recipe properties
 *
 * @param title: The title of the property
 * @param time: The time of the property
 * @param testTag: The test tag for the property
 */
@Composable
private fun RecipePropertyRowText(title: String, time: String, testTag: String) {
  Column(
      modifier = Modifier.testTag(testTag),
      verticalArrangement = Arrangement.spacedBy(SMALL_PADDING.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSecondary)
        Text(
            time,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondary)
      }
}

/**
 * Display of the ingredients and instructions
 *
 * @param ingredientsView: Boolean to determine if the ingredients are displayed
 */
@Composable
private fun IngredientInstructionView(
    ingredientsView: Boolean,
    currentRecipe: Recipe,
) {

  Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(SMALL_PADDING.dp)) {
        if (ingredientsView) {
          IngredientView(currentRecipe)
        } else {
          InstructionView(currentRecipe)
        }
      }
}

/** Display of the ingredients and the ability to change the number of servings */
@Composable
private fun IngredientView(currentRecipe: Recipe) {
  var servingsCount by remember { mutableIntStateOf(INITIAL_NUMBER_VALUE_PER_RECIPE) }
  Row(
      modifier =
          Modifier.fillMaxWidth().padding(horizontal = PADDING.dp).testTag("ingredientsView"),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween) {
        // Text label on the left
        Text(
            text = stringResource(R.string.servings),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.weight(1f))

        // Button row on the right
        Counter(servingsCount) { newCounter -> servingsCount = newCounter }
      }

  IngredientsList(currentRecipe, servingsCount)
}

/**
 * Display of the counter to change the number of servings
 *
 * @param servingsCount: The current number of servings
 * @param onCounterChange: The function to change the number of servings
 */
@Composable
private fun Counter(servingsCount: Int, onCounterChange: (Int) -> Unit) {
  Row(
      modifier =
          Modifier.background(
                  MaterialTheme.colorScheme.onBackground,
                  shape = RoundedCornerShape(COUNTER_ROUND_CORNER.dp))
              .padding(horizontal = SMALL_PADDING.dp, vertical = (PADDING / 4).dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING.dp)) {
        // - button
        Button(
            onClick = {
              if (servingsCount > OVERVIEW_MIN_COUNTER_VALUE) onCounterChange(servingsCount - 1)
            },
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background),
            modifier = Modifier.size(COUNTER_MIN_MAX_SIZE.dp).testTag("removeServings"),
            contentPadding = PaddingValues(OVERVIEW_RECIPE_COUNTER_PADDING.dp)) {
              Text(
                  stringResource(R.string.counter_min),
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.background)
            }

        // Display the count
        Text(
            text = servingsCount.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.background,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("numberServings").width(OVERVIEW_COUNTER_TEXT_SIZE.dp))

        // + button
        Button(
            onClick = {
              if (servingsCount < OVERVIEW_MAX_COUNTER_VALUE) onCounterChange(servingsCount + 1)
            },
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background),
            modifier = Modifier.size(COUNTER_MIN_MAX_SIZE.dp).testTag("addServings"),
            contentPadding = PaddingValues(OVERVIEW_RECIPE_COUNTER_PADDING.dp)) {
              Text(
                  stringResource(R.string.counter_max),
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.background)
            }
      }
}

/**
 * Extracts the first integer from the measurement string, doubles it and replaces it in the string
 *
 * @param measurement: The measurement string
 * @param servingsCount: The number of servings
 * @return The modified string
 */
private fun extractAndDoubleFirstInt(measurement: String, servingsCount: Int): String {
  val regex = Regex("""\d+""")
  val match = regex.find(measurement)
  return if (match != null) {
    val originalNumber = match.value.toInt()
    val doubledNumber = originalNumber * servingsCount
    measurement.replaceFirst(match.value, doubledNumber.toString())
  } else {
    measurement
  }
}

/**
 * Display of the ingredients
 *
 * @param currentRecipe: The recipe to display
 * @param servingsCount: The number of servings
 */
@Composable
private fun IngredientsList(currentRecipe: Recipe, servingsCount: Int) {
  Column(verticalArrangement = Arrangement.spacedBy((PADDING).dp)) {
    currentRecipe.ingredientsAndMeasurements.forEach { (ingredient, measurement) ->
      var ticked by remember { mutableStateOf(false) }
      val modifiedMeasurement = extractAndDoubleFirstInt(measurement, servingsCount)

      Row(
          modifier = Modifier.padding(start = (PADDING * 3).dp),
          verticalAlignment = Alignment.CenterVertically) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SMALL_PADDING.dp)) {
                  Checkbox(
                      checked = ticked,
                      onCheckedChange = { ticked = it },
                      modifier =
                          Modifier.size(OVERVIEW_CHECKBOX_SIZE.dp).testTag("checkboxIngredient"))

                  Text(
                      text = "$ingredient: $modifiedMeasurement",
                      textAlign = TextAlign.Left,
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onPrimary,
                      modifier = Modifier.testTag("ingredient$ingredient"))
                }
          }
    }
  }
}

/**
 * Display of the instructions
 *
 * @param currentRecipe: The recipe to display
 */
@Composable
private fun InstructionView(currentRecipe: Recipe) {
  Column(
      modifier =
          Modifier.padding(
                  start = OVERVIEW_INSTRUCTION_START.dp,
                  end = OVERVIEW_INSTRUCTION_END.dp,
                  top = OVERVIEW_INSTRUCTION_TOP.dp,
                  bottom = OVERVIEW_INSTRUCTION_BOTTOM.dp)
              .testTag("instructionsView")) {
        // Display of the instructions

        Text(
            text = currentRecipe.strInstructions,
            color = Color.Black,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("instructionsText"))
      }
}
