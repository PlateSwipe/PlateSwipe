package com.android.sample.ui.createRecipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.android.sample.R
import com.android.sample.animation.LoadingCook
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.resources.C.Tag.CORNER_SHAPE_TEXT_FIELD
import com.android.sample.resources.C.Tag.MAXLINES_RECIPE_NAME_FIELD
import com.android.sample.resources.C.Tag.RECIPE_NAME_BASE_PADDING
import com.android.sample.resources.C.Tag.RECIPE_NAME_BUTTON_WIDTH
import com.android.sample.resources.C.Tag.RECIPE_NAME_CHARACTER_LIMIT
import com.android.sample.resources.C.Tag.RECIPE_NAME_FIELD_HEIGHT
import com.android.sample.resources.C.Tag.RECIPE_NAME_FONT_SPACING
import com.android.sample.resources.C.Tag.SCREEN_HEIGHT_THRESHOLD
import com.android.sample.resources.C.Tag.SCREEN_WIDTH_THRESHOLD
import com.android.sample.resources.C.TestTag.RecipeNameScreen.LOADING_COOK_TEST_TAG
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.*
import com.android.sample.ui.utils.PlateSwipeButton

/**
 * Composable function that displays the screen for entering a recipe name.
 *
 * @param modifier Modifier to be applied to the screen.
 * @param currentStep The current step in the recipe creation process.
 * @param navigationActions Actions for navigating between screens.
 * @param createRecipeViewModel ViewModel for managing the recipe creation process.
 * @param isEditing Boolean indicating whether the recipe is being edited.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeNameScreen(
    modifier: Modifier = Modifier,
    currentStep: Int,
    navigationActions: NavigationActions,
    createRecipeViewModel: CreateRecipeViewModel,
    isEditing: Boolean
) {
  var isInitialized by remember { mutableStateOf(false) }

  InitializeRecipe(isEditing, createRecipeViewModel) { initialized -> isInitialized = initialized }

  DisplayLoadingScreen(isInitialized)
  if (!isInitialized) return

  val configuration = LocalConfiguration.current
  val screenWidthDp = configuration.screenWidthDp
  val screenHeightDp = configuration.screenHeightDp

  var recipeName by
      rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(initializeRecipeName(isEditing, createRecipeViewModel))
      }

  var showError by remember { mutableStateOf(false) }
  Box(
      modifier = modifier.padding(RECIPE_NAME_BASE_PADDING / 2),
      contentAlignment = Alignment.TopCenter) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()) {
              // Display the progress bar for the current step
              RecipeProgressBar(currentStep = currentStep)

              Spacer(modifier = Modifier.weight(0.1f))

              // Display the title text
              Text(
                  text =
                      getConditionalStringResource(
                          isEditing, R.string.edit_your_recipe, R.string.create_your_recipe),
                  style = Typography.displayLarge,
                  color = MaterialTheme.colorScheme.onPrimary,
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(horizontal = RECIPE_NAME_BASE_PADDING)
                          .testTag("RecipeTitle"),
                  textAlign = TextAlign.Center)

              Spacer(modifier = Modifier.weight(0.1f))

              // Display the description text
              Text(
                  text =
                      getConditionalStringResource(
                          isEditing,
                          R.string.edit_recipe_description,
                          R.string.create_recipe_description),
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onPrimary,
                  modifier =
                      Modifier.padding(horizontal = RECIPE_NAME_BASE_PADDING * 2)
                          .width(RECIPE_NAME_BUTTON_WIDTH)
                          .height(RECIPE_NAME_FIELD_HEIGHT)
                          .testTag("RecipeSubtitle"),
                  textAlign = TextAlign.Center)

              Spacer(modifier = Modifier.weight(0.2f))

              // Display the label for the recipe name field
              Text(
                  text = stringResource(R.string.recipe_name_label),
                  style =
                      MaterialTheme.typography.bodySmall.copy(
                          fontFamily = Roboto,
                          fontWeight = FontWeight.Bold,
                          letterSpacing = RECIPE_NAME_FONT_SPACING),
                  color = MaterialTheme.colorScheme.onPrimary,
                  modifier =
                      Modifier.padding(horizontal = RECIPE_NAME_BASE_PADDING)
                          .align(Alignment.Start))

              Spacer(modifier = Modifier.height(RECIPE_NAME_BASE_PADDING / 2))

              // Input field for the recipe name
              OutlinedTextField(
                  value = recipeName,
                  onValueChange = { newName ->
                    handleRecipeNameChange(
                        newName = newName,
                        onRecipeNameChange = { recipeName = it },
                        onShowErrorChange = { showError = it })
                  },
                  placeholder = getLabelText(recipeName),
                  shape = RoundedCornerShape(8.dp),
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(horizontal = RECIPE_NAME_BASE_PADDING)
                          .background(
                              lightCream, shape = RoundedCornerShape(CORNER_SHAPE_TEXT_FIELD.dp))
                          .testTag("recipeNameTextField"),
                  colors =
                      TextFieldDefaults.outlinedTextFieldColors(
                          unfocusedBorderColor = Color.Transparent,
                          focusedBorderColor = Color.Transparent,
                          cursorColor = MaterialTheme.colorScheme.onSecondary),
                  textStyle = MaterialTheme.typography.bodyMedium,
                  maxLines = MAXLINES_RECIPE_NAME_FIELD)

              getErrorMessage(showError).invoke()

              Spacer(modifier = Modifier.weight(0.6f))

              // Display the chef image if the screen dimensions are large enough
              if (shouldDisplayChefImage(screenWidthDp, screenHeightDp)) {
                Row(
                    modifier = Modifier.weight(0.6f),
                    verticalAlignment = Alignment.CenterVertically) {
                      ChefImage(modifier = Modifier.weight(0.6f).testTag("ChefImage").zIndex(-1f))
                    }
              }
              Spacer(modifier = Modifier.weight(0.35f))
            }

        // Button to proceed to the next step
        PlateSwipeButton(
            stringResource(R.string.next_step),
            modifier = Modifier.align(Alignment.BottomCenter).testTag("NextStepButton"),
            onClick = {
              handleOnClick(
                  recipeName = recipeName,
                  onShowErrorChange = { showError = it },
                  onUpdateRecipeName = { createRecipeViewModel.updateRecipeName(it) },
                  onNavigateToNextScreen = { navigateToNextScreen(isEditing, navigationActions) })
            })
      }
}

/**
 * Helper function to handle changes in the recipe name.
 *
 * @param newName The new value of the recipe name.
 * @param onRecipeNameChange Callback to update the recipe name.
 * @param onShowErrorChange Callback to update the error state.
 */
private fun handleRecipeNameChange(
    newName: TextFieldValue,
    onRecipeNameChange: (TextFieldValue) -> Unit,
    onShowErrorChange: (Boolean) -> Unit
) {
  if (newName.text.length <= RECIPE_NAME_CHARACTER_LIMIT) {
    onRecipeNameChange(newName)
    onShowErrorChange(false)
  }
}

/**
 * Helper function to get the label text for the recipe name field.
 *
 * @param recipeName The current value of the recipe name.
 * @return A composable function that displays the label text.
 */
private fun getLabelText(recipeName: TextFieldValue): @Composable () -> Unit {
  return {
    if (recipeName.text.isEmpty()) {
      Text(
          text = stringResource(R.string.recipe_name_hint),
          style =
              MaterialTheme.typography.bodySmall.copy(
                  fontFamily = MeeraInimai, letterSpacing = RECIPE_NAME_FONT_SPACING / 1.4f),
          color = MaterialTheme.colorScheme.onSecondary)
    }
  }
}

/**
 * Helper function to get the error message for the recipe name field.
 *
 * @param showError Boolean indicating whether to show the error message.
 * @return A composable function that displays the error message.
 */
private fun getErrorMessage(showError: Boolean): @Composable () -> Unit {
  return {
    if (showError) {
      Text(
          text = stringResource(R.string.recipe_name_error),
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
          modifier = Modifier.zIndex(1f).testTag("ErrorMessage"))
    }
  }
}

/**
 * Helper function to determine if the chef image should be displayed.
 *
 * @param screenWidthDp The width of the screen in dp.
 * @param screenHeightDp The height of the screen in dp.
 * @return Boolean indicating whether to display the chef image.
 */
fun shouldDisplayChefImage(screenWidthDp: Int, screenHeightDp: Int): Boolean {
  return screenWidthDp >= SCREEN_WIDTH_THRESHOLD && screenHeightDp >= SCREEN_HEIGHT_THRESHOLD
}

/**
 * Helper function to handle the onClick event for the next step button.
 *
 * @param recipeName The current value of the recipe name.
 * @param onShowErrorChange Callback to update the error state.
 * @param onUpdateRecipeName Callback to update the recipe name.
 * @param onNavigateToNextScreen Callback to navigate to the next screen.
 */
private fun handleOnClick(
    recipeName: TextFieldValue,
    onShowErrorChange: (Boolean) -> Unit,
    onUpdateRecipeName: (String) -> Unit,
    onNavigateToNextScreen: () -> Unit
) {
  if (recipeName.text.isBlank()) {
    onShowErrorChange(true)
  } else {
    onUpdateRecipeName(recipeName.text)
    onNavigateToNextScreen()
  }
}

/**
 * Helper function to initialize the recipe creation process.
 *
 * @param isEditing Boolean indicating whether the recipe is being edited.
 * @param createRecipeViewModel ViewModel for managing the recipe creation process.
 * @param onInitialized Callback to be invoked when the initialization is complete.
 */
@Composable
private fun InitializeRecipe(
    isEditing: Boolean,
    createRecipeViewModel: CreateRecipeViewModel,
    onInitialized: (Boolean) -> Unit
) {
  LaunchedEffect(isEditing) {
    if (!isEditing) {
      createRecipeViewModel.startNewRecipe()
    }
    onInitialized(true) // Mark as initialized after starting new recipe or editing
  }
}

/**
 * Helper function to display the loading screen.
 *
 * @param isInitialized Boolean indicating whether the initialization is complete.
 */
@Composable
private fun DisplayLoadingScreen(isInitialized: Boolean) {
  if (!isInitialized) {
    Box(
        modifier = Modifier.fillMaxSize().testTag(LOADING_COOK_TEST_TAG),
        contentAlignment = Alignment.Center) {
          LoadingCook()
        }
  }
}

/**
 * Helper function to initialize the recipe name.
 *
 * @param isEditing Boolean indicating whether the recipe is being edited.
 * @param createRecipeViewModel ViewModel for managing the recipe creation process.
 * @return The initialized TextFieldValue for the recipe name.
 */
private fun initializeRecipeName(
    isEditing: Boolean,
    createRecipeViewModel: CreateRecipeViewModel
): TextFieldValue {
  return TextFieldValue(if (isEditing) createRecipeViewModel.getRecipeName() else "")
}

/**
 * Helper function to get the appropriate string resource based on the isEditing flag.
 *
 * @param isEditing Boolean indicating whether the recipe is being edited.
 * @param editStringRes The string resource to use when editing.
 * @param createStringRes The string resource to use when creating.
 * @return The appropriate string resource.
 */
@Composable
private fun getConditionalStringResource(
    isEditing: Boolean,
    editStringRes: Int,
    createStringRes: Int
): String {
  return stringResource(if (isEditing) editStringRes else createStringRes)
}

/**
 * Helper function to navigate to the next screen based on the isEditing flag.
 *
 * @param isEditing Boolean indicating whether the recipe is being edited.
 * @param navigationActions Actions for navigating between screens.
 */
private fun navigateToNextScreen(isEditing: Boolean, navigationActions: NavigationActions) {
  if (isEditing) {
    navigationActions.navigateTo(Screen.EDIT_CATEGORY_SCREEN)
  } else {
    navigationActions.navigateTo(Screen.CREATE_CATEGORY_SCREEN)
  }
}
