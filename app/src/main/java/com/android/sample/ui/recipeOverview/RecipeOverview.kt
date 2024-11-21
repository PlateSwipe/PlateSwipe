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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.android.sample.R
import com.android.sample.animation.LoadingCook
import com.android.sample.model.recipe.Instruction
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipeOverviewViewModel
import com.android.sample.resources.C.Dimension.CreateRecipeListInstructionsScreen.CARD_BORDER_ROUND
import com.android.sample.resources.C.Dimension.CreateRecipeListInstructionsScreen.CARD_SHADOW_ELEVATION
import com.android.sample.resources.C.Dimension.CreateRecipeListInstructionsScreen.ICON_SIZE
import com.android.sample.resources.C.Dimension.CreateRecipeListInstructionsScreen.MEDIUM_PADDING
import com.android.sample.resources.C.Dimension.CreateRecipeListInstructionsScreen.REALLY_SMALL_PADDING
import com.android.sample.resources.C.Dimension.CreateRecipeListInstructionsScreen.ROW_SIZE
import com.android.sample.resources.C.Dimension.RecipeOverview.COUNTER_MIN_MAX_SIZE
import com.android.sample.resources.C.Dimension.RecipeOverview.COUNTER_ROUND_CORNER
import com.android.sample.resources.C.Dimension.RecipeOverview.IMAGE_ROUND_CORNER
import com.android.sample.resources.C.Dimension.RecipeOverview.OVERVIEW_CHECKBOX_SIZE
import com.android.sample.resources.C.Dimension.RecipeOverview.OVERVIEW_COUNTER_TEXT_SIZE
import com.android.sample.resources.C.Dimension.RecipeOverview.OVERVIEW_FONT_SIZE_MEDIUM
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
import com.android.sample.resources.C.Tag.SwipePage.RATE_VALUE
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.EDIT_INSTRUCTION_ICON
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.INSTRUCTION_TEXT_IN_CARD
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.INSTRUCTION_TEXT_SPACE
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.INSTRUCTION_TIME
import com.android.sample.resources.C.TestTag.RecipeOverview.ADD_SERVINGS
import com.android.sample.resources.C.TestTag.RecipeOverview.COOK_TIME_TEXT
import com.android.sample.resources.C.TestTag.RecipeOverview.DRAGGABLE_ITEM
import com.android.sample.resources.C.TestTag.RecipeOverview.INGREDIENTS_VIEW
import com.android.sample.resources.C.TestTag.RecipeOverview.INGREDIENT_CHECKBOX
import com.android.sample.resources.C.TestTag.RecipeOverview.INGREDIENT_PREFIX
import com.android.sample.resources.C.TestTag.RecipeOverview.INSTRUCTIONS_VIEW
import com.android.sample.resources.C.TestTag.RecipeOverview.NUMBER_SERVINGS
import com.android.sample.resources.C.TestTag.RecipeOverview.PREP_TIME_TEXT
import com.android.sample.resources.C.TestTag.RecipeOverview.RATING_ICON
import com.android.sample.resources.C.TestTag.RecipeOverview.RECIPE_IMAGE
import com.android.sample.resources.C.TestTag.RecipeOverview.RECIPE_RATE
import com.android.sample.resources.C.TestTag.RecipeOverview.RECIPE_STAR
import com.android.sample.resources.C.TestTag.RecipeOverview.RECIPE_TITLE
import com.android.sample.resources.C.TestTag.RecipeOverview.REMOVE_SERVINGS
import com.android.sample.resources.C.TestTag.RecipeOverview.SLIDING_BUTTON_INGREDIENTS
import com.android.sample.resources.C.TestTag.RecipeOverview.SLIDING_BUTTON_INSTRUCTIONS
import com.android.sample.resources.C.TestTag.RecipeOverview.TOTAL_TIME_TEXT
import com.android.sample.resources.C.Values.RecipeOverview.INITIAL_NUMBER_PERSON_PER_RECIPE
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
            horizontalAlignment = Alignment.Start,
            modifier =
                Modifier.testTag(DRAGGABLE_ITEM)
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())) {
              if (currentRecipe == null) {
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
      modifier =
          Modifier.background(
                  MaterialTheme.colorScheme.onPrimaryContainer,
                  shape = RoundedCornerShape(OVERVIEW_RECIPE_ROUND))
              .fillMaxSize()
              .padding(SMALL_PADDING.dp)) {
        // Variable to track the current selection
        var isInstructionDisplay by remember { mutableStateOf(true) }

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
                  !isInstructionDisplay,
                  stringResource(R.string.ingredients),
                  SLIDING_BUTTON_INGREDIENTS,
                  Modifier.weight(1f),
                  clickable = { isInstructionDisplay = true })

              SlidingButton(
                  isInstructionDisplay,
                  stringResource(R.string.instructions),
                  SLIDING_BUTTON_INSTRUCTIONS,
                  Modifier.weight(1f),
                  clickable = { isInstructionDisplay = false })
            }

        // Display the appropriate view based on the toggle state
        IngredientInstructionView(isInstructionDisplay, currentRecipe)
      }
}

/**
 * Custom button that slides to the left or right based on the selection
 *
 * @param isInstructionDisplay: Boolean to determine if the ingredient is displayed
 * @param text: Text to display on the button
 * @param testTag: Test tag for the button
 * @param modifier: Modifier to apply to the button
 */
@Composable
private fun SlidingButton(
    isInstructionDisplay: Boolean,
    text: String,
    testTag: String,
    modifier: Modifier = Modifier,
    clickable: () -> Unit
) {
  Box(
      modifier =
          modifier
              .background(
                  if (!isInstructionDisplay) MaterialTheme.colorScheme.onSecondaryContainer
                  else Color.Transparent,
                  shape = RoundedCornerShape(IMAGE_ROUND_CORNER.dp))
              .clickable { clickable() }
              .padding(vertical = SMALL_PADDING.dp),
      contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color =
                if (!isInstructionDisplay) MaterialTheme.colorScheme.background
                else MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            modifier =
                Modifier.padding(SMALL_PADDING.dp).testTag(testTag).semantics {
                  contentDescription = text
                })
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
      modifier =
          Modifier.fillMaxWidth().padding(start = PADDING.dp, top = PADDING.dp, end = PADDING.dp),
      shape = RoundedCornerShape(OVERVIEW_RECIPE_CARD_SHAPE.dp),
      elevation = CardDefaults.cardElevation(OVERVIEW_RECIPE_CARD_ELEVATION.dp)) {
        Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.onPrimary)) {
          Image(
              painter = rememberAsyncImagePainter(model = currentRecipe.url),
              contentDescription = stringResource(R.string.recipe_image),
              modifier = Modifier.fillMaxWidth().height(height).testTag(RECIPE_IMAGE),
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
  Column(horizontalAlignment = Alignment.Start, modifier = Modifier.padding(start = PADDING.dp)) {
    Text(
        text = currentRecipe.name,
        modifier = Modifier.testTag(RECIPE_TITLE),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSecondary)

    Spacer(modifier = Modifier.size(SMALL_PADDING.dp))
    currentRecipe.category?.let { Tag(it) }
    Spacer(modifier = Modifier.size(SMALL_PADDING.dp))
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.testTag(RATING_ICON)) {
          // Display of the rating Icon
          Icon(
              painter = painterResource(R.drawable.star_rate),
              contentDescription = stringResource(R.string.star_rate_description),
              modifier = Modifier.testTag(RECIPE_STAR).size(OVERVIEW_RECIPE_STAR_SIZE.dp),
              tint = starColor)

          Spacer(modifier = Modifier.size(SMALL_PADDING.dp))

          // Rating Text
          Text(
              text = RATE_VALUE,
              modifier = Modifier.testTag(RECIPE_RATE),
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
                  MaterialTheme.colorScheme.onPrimaryContainer,
                  shape = RoundedCornerShape(IMAGE_ROUND_CORNER))
              .fillMaxWidth()
              .padding(padding / 2),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        // Display of the preparation time
        RecipePropertyRowText(stringResource(R.string.prep_time), "30 min", PREP_TIME_TEXT)
        // Display of the cooking time
        RecipePropertyRowText(stringResource(R.string.cook_time), "20 min", COOK_TIME_TEXT)
        // Display of the total time that it takes
        RecipePropertyRowText(stringResource(R.string.total_time), "50 min", TOTAL_TIME_TEXT)
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
private fun IngredientView(
    currentRecipe: Recipe,
    initialServings: Int = INITIAL_NUMBER_PERSON_PER_RECIPE
) {
  var servingsCount by remember { mutableIntStateOf(initialServings) }

  Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = PADDING.dp).testTag(INGREDIENTS_VIEW),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween) {
        // Text label on the left
        Text(
            text = stringResource(R.string.servings),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = OVERVIEW_FONT_SIZE_MEDIUM.sp,
            modifier =
                Modifier.weight(1f).semantics { contentDescription = "$servingsCount servings" },
        )

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
                  MaterialTheme.colorScheme.onSecondaryContainer,
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
                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    contentColor = MaterialTheme.colorScheme.background),
            modifier = Modifier.size(COUNTER_MIN_MAX_SIZE.dp).testTag(REMOVE_SERVINGS),
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
            fontSize = OVERVIEW_FONT_SIZE_MEDIUM.sp,
            modifier = Modifier.testTag(NUMBER_SERVINGS).width(OVERVIEW_COUNTER_TEXT_SIZE.dp))

        // + button
        Button(
            onClick = {
              if (servingsCount < OVERVIEW_MAX_COUNTER_VALUE) onCounterChange(servingsCount + 1)
            },
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    contentColor = MaterialTheme.colorScheme.background),
            modifier = Modifier.size(COUNTER_MIN_MAX_SIZE.dp).testTag(ADD_SERVINGS),
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
private fun scaleFirstIntByServings(measurement: String, servingsCount: Int): String {
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
      val modifiedMeasurement = scaleFirstIntByServings(measurement, servingsCount)

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
                          Modifier.size(OVERVIEW_CHECKBOX_SIZE.dp).testTag(INGREDIENT_CHECKBOX))

                  Text(
                      text = "$ingredient: $modifiedMeasurement",
                      textAlign = TextAlign.Left,
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onPrimary,
                      modifier =
                          Modifier.testTag("$INGREDIENT_PREFIX$ingredient").semantics {
                            contentDescription = "$ingredient, $modifiedMeasurement"
                          })
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
              .testTag(INSTRUCTIONS_VIEW)) {
        for (index in currentRecipe.instructions.indices) {
          InstructionValue(currentRecipe.instructions[index], index)
        }
      }
}

@Composable
fun InstructionValue(instruction: Instruction, index: Int) {
  val officialStep = index + 1
  var isExpanded by remember { mutableStateOf(false) } // Track the expanded state

  Card(
      modifier =
          Modifier.fillMaxWidth(1f)
              .padding(horizontal = MEDIUM_PADDING.dp, vertical = REALLY_SMALL_PADDING.dp)
              .clickable(onClick = { isExpanded = !isExpanded })
              .shadow(
                  elevation = CARD_SHADOW_ELEVATION.dp,
                  shape = RoundedCornerShape(CARD_BORDER_ROUND.dp)),
      colors =
          CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.background,
              contentColor = MaterialTheme.colorScheme.onPrimary),
      shape = RoundedCornerShape(CARD_BORDER_ROUND.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(ROW_SIZE).padding(SMALL_PADDING.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
              Image(
                  painter = painterResource(id = instruction.icon.iconResId),
                  contentDescription = "Icon",
                  modifier = Modifier.size(ICON_SIZE.dp))

              Column(modifier = Modifier.testTag(INSTRUCTION_TEXT_SPACE)) {
                Text(
                    modifier = Modifier.testTag(INSTRUCTION_TEXT_IN_CARD),
                    color = Color.Black,
                    text =
                        "${stringResource(R.string.RecipeListInstructionsScreen_Step)} ${officialStep}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold)

                // This text represents the time that it takes to do the step
                if (!instruction.time.isNullOrBlank()) {
                  Text(
                      color = Color.Black,
                      text =
                          "${instruction.time} ${stringResource(R.string.RecipeListInstructionsScreen_Minutes)}",
                      style = MaterialTheme.typography.bodySmall,
                      modifier = Modifier.testTag(INSTRUCTION_TIME))
                }
              }
              Icon(
                  imageVector =
                      if (isExpanded) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                  contentDescription = "Expand",
                  modifier = Modifier.size(ICON_SIZE.dp).testTag(EDIT_INSTRUCTION_ICON),
                  tint = Color.Black)
            }
      }
  if (isExpanded) {
    Text(
        text = instruction.description,
        color = Color.Black,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(SMALL_PADDING.dp))
  }
}
