package com.android.sample.ui.createRecipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.resources.C.Tag.RECIPE_NAME_BASE_PADDING
import com.android.sample.resources.C.Tag.RECIPE_NAME_BUTTON_HEIGHT
import com.android.sample.resources.C.Tag.RECIPE_NAME_BUTTON_WIDTH
import com.android.sample.resources.C.Tag.RECIPE_NAME_CHARACTER_LIMIT
import com.android.sample.resources.C.Tag.RECIPE_NAME_FIELD_HEIGHT
import com.android.sample.resources.C.Tag.RECIPE_NAME_FIELD_SPACING
import com.android.sample.resources.C.Tag.RECIPE_NAME_FONT_SPACING
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.*

/**
 * Composable function that displays the screen for entering a recipe name.
 *
 * @param modifier Modifier to be applied to the screen.
 * @param currentStep The current step in the recipe creation process.
 * @param navigationActions Actions for navigating between screens.
 * @param createRecipeViewModel ViewModel for managing the recipe creation process.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeNameScreen(
    modifier: Modifier = Modifier,
    currentStep: Int,
    navigationActions: NavigationActions,
    createRecipeViewModel: CreateRecipeViewModel
) {
  var recipeName by remember { mutableStateOf(TextFieldValue("")) }
  var showError by remember { mutableStateOf(false) }

  Box(
      modifier = modifier.padding(RECIPE_NAME_BASE_PADDING),
      contentAlignment = Alignment.TopCenter) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()) {
              // Display the progress bar for the current step
              RecipeProgressBar(currentStep = currentStep)

              Spacer(modifier = Modifier.height(RECIPE_NAME_FIELD_SPACING))

              // Display the title text
              Text(
                  text = stringResource(R.string.create_your_recipe),
                  style = MaterialTheme.typography.titleLarge,
                  color = MaterialTheme.colorScheme.onPrimary,
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(horizontal = RECIPE_NAME_BASE_PADDING * 2)
                          .testTag("RecipeTitle"),
                  textAlign = TextAlign.Center)

              Spacer(modifier = Modifier.height(RECIPE_NAME_FIELD_SPACING / 3))

              // Display the description text
              Text(
                  text = stringResource(R.string.create_recipe_description),
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onPrimary,
                  modifier =
                      Modifier.padding(horizontal = RECIPE_NAME_BASE_PADDING * 2)
                          .width(RECIPE_NAME_BUTTON_WIDTH)
                          .height(RECIPE_NAME_FIELD_HEIGHT)
                          .testTag("RecipeSubtitle"),
                  textAlign = TextAlign.Center)

              Spacer(modifier = Modifier.height(RECIPE_NAME_FIELD_SPACING))

              Column(modifier = Modifier.fillMaxWidth()) {
                // Display the label for the recipe name field
                Text(
                    text = stringResource(R.string.recipe_name_label),
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            fontFamily = Roboto,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = RECIPE_NAME_FONT_SPACING),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = RECIPE_NAME_BASE_PADDING))

                Spacer(modifier = Modifier.height(RECIPE_NAME_BASE_PADDING / 2))

                // Input field for the recipe name
                OutlinedTextField(
                    value = recipeName,
                    onValueChange = {
                      if (it.text.length <= RECIPE_NAME_CHARACTER_LIMIT) {
                        recipeName = it
                        showError = false
                      }
                    },
                    label = {
                      if (recipeName.text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.recipe_name_hint),
                            style =
                                MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = MeeraInimai,
                                    letterSpacing = RECIPE_NAME_FONT_SPACING / 1.4f),
                            color = MaterialTheme.colorScheme.secondary)
                      }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(horizontal = RECIPE_NAME_BASE_PADDING)
                            .background(lightCream, shape = RoundedCornerShape(8.dp))
                            .testTag("recipeNameTextField"),
                    colors =
                        TextFieldDefaults.outlinedTextFieldColors(
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                        ),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    maxLines = 2)
              }

              // Display error message if the recipe name is empty
              if (showError) {
                Text(
                    text = stringResource(R.string.recipe_name_error),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = RECIPE_NAME_BASE_PADDING / 2))
              }
              Spacer(modifier = Modifier.height(RECIPE_NAME_FIELD_SPACING))
            }

        // Button to proceed to the next step
        Button(
            onClick = {
              if (recipeName.text.isEmpty()) {
                showError = true
              } else {
                createRecipeViewModel.updateRecipeName(recipeName.text)
                navigationActions.navigateTo(Screen.CREATE_RECIPE_INGREDIENTS)
              }
            },
            modifier =
                Modifier.align(Alignment.BottomCenter)
                    .width(RECIPE_NAME_BUTTON_WIDTH)
                    .height(RECIPE_NAME_BUTTON_HEIGHT)
                    .background(color = lightCream, shape = RoundedCornerShape(size = 4.dp))
                    .testTag("NextStepButton"),
            shape = RoundedCornerShape(4.dp)) {
              Text(stringResource(R.string.next_step))
            }
      }
}
