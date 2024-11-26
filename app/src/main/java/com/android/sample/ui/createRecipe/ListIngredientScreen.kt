package com.android.sample.ui.createRecipe

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.android.sample.R
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.resources.C.Dimension.IngredientListScreen.BUTTON_ROUND
import com.android.sample.resources.C.Dimension.IngredientListScreen.BUTTON_Z
import com.android.sample.resources.C.Dimension.IngredientListScreen.INGREDIENT_LIST_SIZE
import com.android.sample.resources.C.Dimension.IngredientListScreen.INGREDIENT_LIST_WEIGHT
import com.android.sample.resources.C.Dimension.IngredientListScreen.INGREDIENT_PREVIEW_CORNER
import com.android.sample.resources.C.Dimension.IngredientListScreen.INGREDIENT_PREVIEW_ELEVATION
import com.android.sample.resources.C.Dimension.IngredientListScreen.INPUT_MAX_LINE
import com.android.sample.resources.C.Dimension.IngredientListScreen.NAME_MAX_LINE
import com.android.sample.resources.C.Dimension.IngredientListScreen.NAME_SIZE
import com.android.sample.resources.C.Dimension.IngredientListScreen.POP_UP_DELAY
import com.android.sample.resources.C.Dimension.IngredientListScreen.POP_UP_WIDTH
import com.android.sample.resources.C.Dimension.IngredientListScreen.ROUND_CORNER
import com.android.sample.resources.C.Dimension.PADDING_16
import com.android.sample.resources.C.Dimension.PADDING_32
import com.android.sample.resources.C.Dimension.PADDING_8
import com.android.sample.resources.C.Tag.BUTTON_HEIGHT
import com.android.sample.resources.C.Tag.BUTTON_WIDTH
import com.android.sample.resources.C.TestTag.IngredientListScreen.ADD_INGREDIENT_ICON
import com.android.sample.resources.C.TestTag.IngredientListScreen.NEXT_STEP_BUTTON
import com.android.sample.resources.C.TestTag.IngredientListScreen.RECIPE_NAME
import com.android.sample.resources.C.TestTag.IngredientSearchScreen.DRAGGABLE_ITEM
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Typography
import com.android.sample.ui.theme.lightCream
import com.android.sample.ui.theme.lightGrayInput
import com.android.sample.ui.utils.IngredientImageBox
import com.android.sample.ui.utils.PlateSwipeScaffold
import kotlinx.coroutines.delay

/**
 * Composable that displays the list of ingredients for a recipe.
 *
 * @param navigationActions the navigation actions to handle navigation.
 * @param ingredientViewModel the view model to handle ingredient operations.
 * @param createRecipeViewModel the view model to handle recipe creation.
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun IngredientListScreen(
    navigationActions: NavigationActions,
    ingredientViewModel: IngredientViewModel,
    createRecipeViewModel: CreateRecipeViewModel
) {
  val ingredientList by ingredientViewModel.ingredientList.collectAsState()
  val displayErrorIngredientMessage = remember { mutableStateOf(false) }
  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = navigationActions.currentRoute(),
      showBackArrow = true,
      content = { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxSize().padding(paddingValues).testTag(DRAGGABLE_ITEM)) {
              Column(Modifier.fillMaxWidth()) {
                Text(
                    text = createRecipeViewModel.recipeBuilder.getName(),
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = NAME_SIZE.sp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(PADDING_16.dp).testTag(RECIPE_NAME))
                Row(
                    modifier =
                        Modifier.fillMaxWidth() // Changed to fill the available width
                            .padding(
                                start = PADDING_16.dp, end = PADDING_16.dp, top = PADDING_16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.ingredient_list),
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    fontSize = INGREDIENT_LIST_SIZE.sp),
                            color = MaterialTheme.colorScheme.onPrimary)
                      }
                      Icon(
                          painter = painterResource(id = R.drawable.add),
                          contentDescription = stringResource(R.string.add),
                          tint = MaterialTheme.colorScheme.onPrimaryContainer,
                          modifier =
                              Modifier.testTag(ADD_INGREDIENT_ICON).clickable {
                                ingredientViewModel.clearSearch()
                                navigationActions.navigateTo(
                                    Screen.CREATE_RECIPE_SEARCH_INGREDIENTS)
                              })
                    }
              }

              // Column for ingredients with scroll and weight for flexible space distribution
              Column(
                  modifier =
                      Modifier.fillMaxWidth() // Changed to fill available width instead of size
                          .weight(INGREDIENT_LIST_WEIGHT)
                          .verticalScroll(rememberScrollState())) {
                    for (ingredient in ingredientList) {
                      // Display the ingredient
                      IngredientPreview(ingredient, ingredientViewModel)
                    }
                  }

              // Box for the save button, positioned at the bottom center
              Box(
                  modifier = Modifier.fillMaxWidth().padding(PADDING_16.dp),
                  contentAlignment = Alignment.Center) {
                    Button(
                        onClick = {
                          for ((ingredient, quantity) in ingredientList) {
                            // Check if the ingredient has a quantity
                            if (!quantity.isNullOrEmpty()) {
                              createRecipeViewModel.addIngredientAndMeasurement(
                                  ingredient.name, quantity.toString())
                            } else {
                              // Display an error message if an ingredient has no quantity
                              displayErrorIngredientMessage.value = true
                            }
                          }
                          if (!displayErrorIngredientMessage.value) {
                            navigationActions.navigateTo(Screen.CREATE_RECIPE_ADD_INSTRUCTION)
                          }
                        },
                        modifier =
                            Modifier.width(BUTTON_WIDTH)
                                .height(BUTTON_HEIGHT)
                                .background(
                                    color = lightCream,
                                    shape = RoundedCornerShape(size = BUTTON_ROUND.dp))
                                .align(Alignment.BottomCenter)
                                .zIndex(BUTTON_Z)
                                .testTag(NEXT_STEP_BUTTON),
                        shape = RoundedCornerShape(BUTTON_ROUND.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary),
                    ) {
                      Text(
                          text = stringResource(R.string.next_step),
                          style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    }
                  }
            }
        if (displayErrorIngredientMessage.value) {
          ErrorPopUp(displayErrorIngredientMessage)
        }
      })
}

/**
 * Composable that displays an error message when an ingredient has no quantity.
 *
 * @param displayErrorIngredientMessage the state to display the error message.
 */
@Composable
private fun ErrorPopUp(displayErrorIngredientMessage: MutableState<Boolean>) {
  LaunchedEffect(Unit) {
    delay(POP_UP_DELAY)
    displayErrorIngredientMessage.value = false
  }

  // Popup implementation
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Box(
        modifier =
            Modifier.wrapContentSize()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(ROUND_CORNER.dp))
                .border(
                    width = POP_UP_WIDTH.dp,
                    color = MaterialTheme.colorScheme.error,
                    shape = RoundedCornerShape(ROUND_CORNER.dp))
                .padding(PADDING_16.dp),
        contentAlignment = Alignment.Center) {
          Text(
              text = stringResource(R.string.pop_up_message),
              color = MaterialTheme.colorScheme.error,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              modifier = Modifier.zIndex(1f))
        }
  }
}

/**
 * Composable that displays an ingredient preview with a quantity field and a remove button.
 *
 * @param ingredient the ingredient to display.
 * @param ingredientViewModel the view model to handle ingredient operations.
 */
@Composable
private fun IngredientPreview(
    ingredient: Pair<Ingredient, String?>,
    ingredientViewModel: IngredientViewModel
) {
  var quantity by remember { mutableStateOf(ingredient.second ?: ingredient.first.quantity ?: "") }
  val focusManager = LocalFocusManager.current

  Box(
      modifier =
          Modifier.fillMaxWidth()
              .padding(PADDING_8.dp)
              .shadow(
                  elevation = INGREDIENT_PREVIEW_ELEVATION.dp,
                  shape = RoundedCornerShape(INGREDIENT_PREVIEW_CORNER.dp),
                  clip = true)
              .background(
                  MaterialTheme.colorScheme.secondary,
                  shape = RoundedCornerShape(INGREDIENT_PREVIEW_CORNER.dp))) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(PADDING_8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
              // Add Image to display the ingredient
              IngredientImageBox(ingredient.first)

              Column(
                  verticalArrangement = Arrangement.SpaceBetween,
                  horizontalAlignment = Alignment.Start,
                  modifier =
                      Modifier.weight(1f)
                          .padding(
                              start = PADDING_16.dp,
                              top = PADDING_16.dp,
                              bottom = PADDING_16.dp,
                              end = (PADDING_32).dp)) {
                    Text(
                        text = ingredient.first.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = NAME_MAX_LINE,
                        overflow = TextOverflow.Ellipsis)

                    Spacer(modifier = Modifier.height(PADDING_8.dp))

                    // Editable quantity field
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { newQuantity ->
                          quantity = newQuantity
                          ingredientViewModel.updateQuantity(ingredient.first, quantity)
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.clearFocus() }),
                        shape = RoundedCornerShape(INGREDIENT_PREVIEW_CORNER.dp),
                        modifier = Modifier.testTag("recipeNameTextField${ingredient.first.name}"),
                        colors =
                            TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.background,
                                unfocusedContainerColor = lightGrayInput,

                                // Make sure these are transparent to avoid unwanted lines
                                focusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        maxLines = INPUT_MAX_LINE,
                    )
                  }
            }

        // Remove button
        IconButton(
            onClick = { ingredientViewModel.removeIngredient(ingredient.first) },
            modifier =
                Modifier.align(Alignment.TopEnd).padding(end = PADDING_8.dp, top = PADDING_8.dp)) {
              Icon(
                  modifier = Modifier.testTag("removeIngredientIcon${ingredient.first.name}"),
                  imageVector = Icons.Filled.Close,
                  contentDescription = stringResource(R.string.close),
                  tint = MaterialTheme.colorScheme.onPrimary)
            }
      }
}
