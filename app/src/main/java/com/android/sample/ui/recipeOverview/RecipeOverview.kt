package com.android.sample.ui.recipeOverview

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.ui.navigation.BottomNavigationMenu
import com.android.sample.ui.navigation.LIST_TOP_LEVEL_DESTINATIONS
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.theme.goldenBronze
import com.android.sample.ui.theme.starColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeOverview(navigationActions: NavigationActions, recipesViewModel: RecipesViewModel) {
  val selectedItem = navigationActions.currentRoute()
  val height = LocalConfiguration.current.screenHeightDp.dp * 1 / 2
  val width = height * 3 / 4
  val currentRecipe by recipesViewModel.currentRecipe.collectAsState()
  var ingredientsView by remember { mutableStateOf(false) }
  val servingsCount by remember { mutableIntStateOf(1) }
  val scrollState = rememberScrollState()

  Scaffold(
      contentColor = MaterialTheme.colorScheme.background,
      // Top bar of the app
      topBar = {
        CenterAlignedTopAppBar(
            title = { Text("PlateSwipe") },
            modifier = Modifier.testTag("topBar"),
            navigationIcon = {
              IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Return button")
              }
            })
      },
      // Bottom bar of the app where we find the navigation menu
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { tab -> navigationActions.navigateTo(tab) },
            tabList = LIST_TOP_LEVEL_DESTINATIONS,
            selectedItem = selectedItem)
      }) { paddingValues ->
        Column(
            modifier =
                Modifier.testTag("draggableItem")
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(scrollState)) {
              // Display of the recipe image
              RecipeImage(currentRecipe, width, height)
              // Display of the recipe title
              RecipeDescription(currentRecipe)
              Spacer(modifier = Modifier.size(16.dp))
              // Display of the prepare, cook and the total time
              PrepareCookTotalTimeDisplay()
              Spacer(modifier = Modifier.size(14.dp))
              // Display of the Ingredient and Instruction buttons that allow us to change between
              // the different views
              Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  modifier =
                      Modifier.background(
                              MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(5))
                          .fillMaxSize()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    // Display of the buttons
                    Row {
                      // Display of the Ingredients button
                      Button(
                          onClick = { ingredientsView = true },
                          shape = RoundedCornerShape(0.dp),
                          modifier = Modifier.width(150.dp).testTag("ingredientsButton"),
                          colors =
                              ButtonColors(goldenBronze, Color.Black, goldenBronze, Color.Black),
                          border = BorderStroke(2.dp, Color.Black)) {
                            Text(
                                text = "Ingredients",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center)
                          }
                      // Display of the Instruction buttons
                      Button(
                          onClick = { ingredientsView = false },
                          shape = RoundedCornerShape(0.dp),
                          modifier = Modifier.width(150.dp).testTag("instructionsButton"),
                          colors =
                              ButtonColors(goldenBronze, Color.Black, goldenBronze, Color.Black),
                          border = BorderStroke(2.dp, Color.Black)) {
                            Text(
                                text = "Instructions",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center)
                          }
                    }
                    // Display of the list of ingredients with the ability to change the number of
                    // servings
                    IngredientInstructionView(ingredientsView, servingsCount, currentRecipe)
                  }
            }
      }
}

/** Display of the recipe image */
@Composable
private fun RecipeImage(currentRecipe: Recipe?, width: Dp, height: Dp) {
  Card(
      modifier = Modifier.fillMaxWidth().padding(8.dp),
      shape = RoundedCornerShape(16.dp),
      elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.onPrimary)) {
          Image(
              painter = rememberAsyncImagePainter(model = currentRecipe?.strMealThumbUrl),
              contentDescription = "Recipe Image",
              modifier =
                  Modifier.fillMaxWidth()
                      .size(width = width, height = height)
                      .testTag("recipeImage"),
              contentScale = ContentScale.FillHeight,
          )
        }
      }
}

/** Display of the recipe title, rating and category */
@Composable
private fun RecipeDescription(currentRecipe: Recipe?) {
  Column {
    Row {
      currentRecipe?.let {
        Text(
            text = it.strMeal,
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.testTag("recipeTitle"))
      }
    }
    Spacer(modifier = Modifier.size(17.dp))
    // Display of the rating of the recipe as well as the category
    Row(horizontalArrangement = Arrangement.Start) {
      // Display of the rating Icon
      Icon(
          imageVector = Icons.Filled.Star,
          contentDescription = "Rating",
          tint = starColor,
          modifier = Modifier.width(12.dp).height(12.dp).testTag("ratingIcon"))
      Spacer(modifier = Modifier.size(8.dp))
      // Display of the recipe rating
      Row {
        Text(
            text = "Rating",
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier.testTag("ratingText"))
      }
      Spacer(modifier = Modifier.size(8.dp))
      // Display of the recipe category
      Row {
        currentRecipe?.strCategory?.let {
          Text(
              text = it,
              fontSize = 12.sp,
              color = Color.Black,
              modifier = Modifier.testTag("categoryText"))
        }
      }
    }
  }
}

/** Display of the preparation time, cooking time and total time */
@Composable
private fun PrepareCookTotalTimeDisplay() {
  Row(
      modifier =
          Modifier.fillMaxWidth(0.85f)
              .padding(start = 55.dp)
              .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(10)),
      horizontalArrangement = Arrangement.Center,
  ) {
    // Display of the preparation time
    Column(modifier = Modifier.testTag("prepTimeText")) {
      Text("Prep time", fontSize = 12.sp, color = Color.Black)
      Spacer(modifier = Modifier.size(14.dp))
      Text("30 min", fontSize = 12.sp, color = Color.Black)
    }
    Spacer(modifier = Modifier.size(40.dp))
    // Display of the cooking time
    Column(modifier = Modifier.testTag("cookTimeText")) {
      Text("Cook time", fontSize = 12.sp, color = Color.Black)
      Spacer(modifier = Modifier.size(14.dp))
      Text("20 min", fontSize = 12.sp, color = Color.Black)
    }
    Spacer(modifier = Modifier.size(40.dp))
    // Display of the total time that it takes
    Column(modifier = Modifier.testTag("totalTimeText")) {
      Text("Total time", fontSize = 12.sp, color = Color.Black)
      Spacer(modifier = Modifier.size(14.dp))
      Text("50 min", fontSize = 12.sp, color = Color.Black)
    }
  }
}

/** Display of the list of ingredients and instructions */
@Composable
private fun IngredientInstructionView(
    ingredientsView: Boolean,
    servingsCount: Int,
    currentRecipe: Recipe?
) {
  Column {
    if (ingredientsView) {
      IngredientView(servingsCount, currentRecipe)
    } else {
      InstructionView(currentRecipe)
    }
  }
}

/** Display of the ingredients and the ability to change the number of servings */
@Composable
private fun IngredientView(servingsCount: Int, currentRecipe: Recipe?) {
  var servingsCountVar = servingsCount
  Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center) {
        Text(
            "Servings",
            textAlign = TextAlign.Left,
            color = Color.Black,
            fontSize = 12.sp,
            modifier = Modifier.width(56.dp).height(18.dp))
        Spacer(modifier = Modifier.width(190.dp))
        Row(
            modifier =
                Modifier.background(goldenBronze, shape = RoundedCornerShape(25))
                    .height(25.dp)
                    .width(53.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
              // Display of the buttons to change the number of servings
              Button(
                  onClick = { if (servingsCountVar > 1) --servingsCountVar },
                  colors = ButtonColors(goldenBronze, Color.Black, goldenBronze, Color.Black),
                  modifier = Modifier.size(20.dp).testTag("removeServings"),
                  contentPadding = PaddingValues()) {
                    Text("-", fontSize = 12.sp)
                  }
              Text(
                  servingsCountVar.toString(),
                  fontSize = 12.sp,
                  color = Color.Black,
                  modifier = Modifier.testTag("numberServings"))
              Button(
                  onClick = { ++servingsCountVar },
                  colors = ButtonColors(goldenBronze, Color.Black, goldenBronze, Color.Black),
                  modifier = Modifier.size(20.dp).testTag("addServings"),
                  contentPadding = PaddingValues()) {
                    Text("+", fontSize = 12.sp)
                  }
            }
      }
  Spacer(modifier = Modifier.height(7.dp))
  IngredientsList(currentRecipe)
}

/** Display of the list of ingredients */
@Composable
private fun IngredientsList(currentRecipe: Recipe?) {
  Column(modifier = Modifier.testTag("ingredientsView")) {
    currentRecipe?.ingredientsAndMeasurements?.forEach { (ingredient, measurement) ->
      var ticked by remember { mutableStateOf(false) }
      Row(
          modifier = Modifier.padding(start = 56.dp).testTag("requiredIngredientsList"),
          verticalAlignment = Alignment.CenterVertically) {
            // Display of the checkbox
            Checkbox(
                checked = ticked,
                onCheckedChange = { ticked = it },
                modifier = Modifier.size(15.dp).testTag("checkboxIngredient"))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                ingredient,
                textAlign = TextAlign.Left,
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier.testTag("ingredient"))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                measurement,
                textAlign = TextAlign.Left,
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier.testTag("measurement"))
          }
      Spacer(modifier = Modifier.height(12.dp))
    }
  }
}

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
