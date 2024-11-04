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
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.android.sample.R
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipeOverviewViewModel
import com.android.sample.resources.C.Tag.LOADING
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.goldenBronze
import com.android.sample.ui.theme.starColor
import com.android.sample.ui.utils.PlateSwipeScaffold
import com.android.sample.ui.utils.Tag

@Composable
fun RecipeOverview(
    navigationActions: NavigationActions,
    recipeOverviewViewModel: RecipeOverviewViewModel
) {
  val selectedItem = navigationActions.currentRoute()

  val currentRecipe by recipeOverviewViewModel.currentRecipe.collectAsState()
  val scrollState = rememberScrollState()

  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = selectedItem,
      showBackArrow = true,
      content = { paddingValues ->
        Column(
            modifier =
                Modifier.testTag("draggableItem")
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(scrollState)) {
              // Display of the recipe image
              RecipeImage(currentRecipe)
              // Display of the recipe title
              RecipeDescription(currentRecipe)
              // Display of the prepare, cook and the total time
              PrepareCookTotalTimeDisplay()
              // Display of the Ingredient and Instruction buttons that allow us to change between
              // the different views
              RecipeInformation(currentRecipe)
            }
      })
}

@Composable
private fun RecipeInformation(currentRecipe: Recipe?) {
  var servingsCount by remember { mutableIntStateOf(1) }
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier =
          Modifier.background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(5))
              .fillMaxSize()
              .padding(8.dp)) {
        // Variable to track the current selection
        var isIngredientsView by remember { mutableStateOf(true) }

        // Custom switch style with two button-like labels
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier =
                Modifier.padding(vertical = 8.dp)
                    .background(
                        MaterialTheme.colorScheme.background, shape = RoundedCornerShape(10.dp))) {
              Box(
                  modifier =
                      Modifier.weight(1f)
                          .background(
                              if (isIngredientsView) goldenBronze else Color.Transparent,
                              shape = RoundedCornerShape(10.dp))
                          .clickable { isIngredientsView = true }
                          .padding(vertical = 8.dp),
                  contentAlignment = Alignment.Center) {
                    Text(
                        text = "Ingredients",
                        fontSize = 14.sp,
                        color = if (isIngredientsView) Color.White else Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp).testTag("ingredientButton"))
                  }

              Box(
                  modifier =
                      Modifier.weight(1f)
                          .background(
                              if (!isIngredientsView) goldenBronze else Color.Transparent,
                              shape = RoundedCornerShape(10.dp))
                          .clickable { isIngredientsView = false }
                          .padding(vertical = 8.dp),
                  contentAlignment = Alignment.Center) {
                    Text(
                        text = "Instructions",
                        fontSize = 14.sp,
                        color = if (!isIngredientsView) Color.White else Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp).testTag("instructionsButton"))
                  }
            }

        Spacer(modifier = Modifier.height(8.dp))

        // Display the appropriate view based on the toggle state
        IngredientInstructionView(isIngredientsView, servingsCount, currentRecipe) {
            newServingsCount ->
          servingsCount = newServingsCount
        }
      }
}

/** Display of the recipe image */
@Composable
private fun RecipeImage(currentRecipe: Recipe?) {
  val height = LocalConfiguration.current.screenHeightDp.dp * 1 / 3

  Card(
      modifier = Modifier.fillMaxWidth().padding(8.dp),
      shape = RoundedCornerShape(16.dp),
      elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.onPrimary)) {
          Image(
              painter = rememberAsyncImagePainter(model = currentRecipe?.strMealThumbUrl),
              contentDescription = "Recipe Image",
              modifier = Modifier.fillMaxWidth().height(height).testTag("recipeImage"),
              contentScale = ContentScale.Crop,
          )
        }
      }
}

/** Display of the recipe title, rating and category */
@Composable
private fun RecipeDescription(currentRecipe: Recipe?) {
  Column {
    Text(
        text = currentRecipe?.strMeal ?: LOADING,
        modifier = Modifier.testTag("recipeTitle"),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSecondary)

    Spacer(modifier = Modifier.size(8.dp))

    currentRecipe?.strCategory?.let { Tag(it) }

    Spacer(modifier = Modifier.size(8.dp))
    // Display of the rating of the recipe as well as the category

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.testTag("ratingIcon")) {
          // Display of the rating Icon
          Icon(
              painter = painterResource(R.drawable.star_rate),
              contentDescription = stringResource(R.string.star_rate_description),
              modifier = Modifier.testTag("recipeStar").size(24.dp), // Use fixed size for the icon
              tint = starColor)

          Spacer(modifier = Modifier.width(6.dp)) // Add spacing between icon and rate

          // Rating Text
          Text(
              text = stringResource(R.string.rate),
              modifier = Modifier.testTag("recipeRate"),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSecondary)

          Spacer(modifier = Modifier.size(8.dp))
          // Display of the recipe category
        }
  }
}

/** Display of the preparation time, cooking time and total time */
@Composable
private fun PrepareCookTotalTimeDisplay() {
  val weight = LocalConfiguration.current.screenWidthDp.dp * 1 / 15

  Row(
      modifier =
          Modifier.padding(weight, weight)
              .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(10))
              .fillMaxWidth()
              .padding(weight / 2),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        // Display of the preparation time
        RecipePropertyText("Prep time", "30 min", "prepTimeText")
        // Display of the cooking time
        RecipePropertyText("Cook time", "20 min", "cookTimeText")
        // Display of the total time that it takes
        RecipePropertyText("Total time", "50 min", "totalTimeText")
      }
}

@Composable
private fun RecipePropertyText(title: String, time: String, testTag: String) {
  Column(modifier = Modifier.testTag(testTag)) {
    Text(
        title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSecondary)
    Spacer(modifier = Modifier.size(8.dp))
    Text(
        time,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSecondary)
  }
}

/** Display of the list of ingredients and instructions */
@Composable
private fun IngredientInstructionView(
    ingredientsView: Boolean,
    servingsCount: Int,
    currentRecipe: Recipe?,
    changeServingsCount: (Int) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    if (ingredientsView) {
      IngredientView(servingsCount, changeServingsCount, currentRecipe)
    } else {
      InstructionView(currentRecipe)
    }
  }
}

/** Display of the ingredients and the ability to change the number of servings */
@Composable
private fun IngredientView(
    servingsCount: Int,
    changeServingsCount: (Int) -> Unit,
    currentRecipe: Recipe?
) {
  var servingsCountVar = servingsCount
  Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).testTag("ingredientsView"),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween) {
        // Text label on the left
        Text(
            text = "Servings",
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.weight(1f))

        // Button row on the right
        Row(
            modifier =
                Modifier.background(goldenBronze, shape = RoundedCornerShape(25.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              // Minus button
              Button(
                  onClick = { if (servingsCountVar > 1) changeServingsCount(--servingsCountVar) },
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = goldenBronze, contentColor = Color.White),
                  modifier = Modifier.size(30.dp).testTag("removeServings"),
                  contentPadding = PaddingValues(0.dp)) {
                    Text("-", fontSize = 16.sp)
                  }

              // Display the count
              Text(
                  text = servingsCountVar.toString(),
                  fontSize = 16.sp,
                  color = Color.White,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.testTag("numberServings").width(20.dp))

              // Plus button
              Button(
                  onClick = { changeServingsCount(++servingsCountVar) },
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = goldenBronze, contentColor = Color.White),
                  modifier = Modifier.size(30.dp).testTag("addServings"),
                  contentPadding = PaddingValues(0.dp)) {
                    Text("+", fontSize = 16.sp)
                  }
            }
      }

  Spacer(modifier = Modifier.height(16.dp))

  IngredientsList(currentRecipe, servingsCountVar)
}

fun extractAndDoubleFirstInt(measurement: String, servingsCount: Int): String {
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

/** Display of the list of ingredients */
@Composable
private fun IngredientsList(currentRecipe: Recipe?, servingsCount: Int) {
  Column() {
    currentRecipe?.ingredientsAndMeasurements?.forEach { (ingredient, measurement) ->
      var ticked by remember { mutableStateOf(false) }
      val modifiedMeasurement = extractAndDoubleFirstInt(measurement, servingsCount)

      Row(
          modifier = Modifier.padding(start = 56.dp),
          verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Spacer(modifier = Modifier.height(8.dp))

              Checkbox(
                  checked = ticked,
                  onCheckedChange = { ticked = it },
                  modifier = Modifier.size(15.dp).testTag("checkboxIngredient"))
              Spacer(modifier = Modifier.width(8.dp))

              Text(
                  text = "$ingredient: $modifiedMeasurement",
                  textAlign = TextAlign.Left,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onPrimary,
                  modifier = Modifier.testTag("ingredient$ingredient"))
            }
          }
      Spacer(modifier = Modifier.height(8.dp))
    }
  }
}

/** Display of the instructions */

/** Display of the instructions */
@Composable
private fun InstructionView(currentRecipe: Recipe?) {
  Column(
      modifier =
          Modifier.padding(start = 25.dp, end = 15.dp, top = 10.dp, bottom = 5.dp)
              .testTag("instructionsView")) {
        // Display of the instructions
        currentRecipe?.let {
          Text(
              text = it.strInstructions,
              color = Color.Black,
              fontSize = 16.sp,
              modifier = Modifier.testTag("instructionsText"))
        }
      }
}
