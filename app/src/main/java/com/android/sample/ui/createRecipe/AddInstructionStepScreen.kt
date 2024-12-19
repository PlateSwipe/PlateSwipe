package com.android.sample.ui.createRecipe

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.model.recipe.IconType
import com.android.sample.model.recipe.Instruction
import com.android.sample.resources.C.Tag.CONTAINER_PADDING
import com.android.sample.resources.C.Tag.HORIZONTAL_PADDING
import com.android.sample.resources.C.Tag.INSTRUCTION_VERTICAL_PADDING
import com.android.sample.resources.C.Tag.MAXLINES_TIME_FIELD
import com.android.sample.resources.C.Tag.MAXLINES_VISIBLE_FOR_INSTRUCTION
import com.android.sample.resources.C.Tag.MINLINES_VISIBLE_FOR_INSTRUCTION
import com.android.sample.resources.C.Tag.SAVE_BUTTON_TAG
import com.android.sample.resources.C.Tag.SPACE_BETWEEN_ELEMENTS
import com.android.sample.resources.C.Tag.TIME_CHARACTER_LIMIT
import com.android.sample.resources.C.TestTag.AddInstructionStepScreen.DELETE_BUTTON
import com.android.sample.resources.C.TestTag.AddInstructionStepScreen.ICON_DROPDOWN
import com.android.sample.resources.C.TestTag.AddInstructionStepScreen.INPUT_CONTAINER
import com.android.sample.resources.C.TestTag.AddInstructionStepScreen.INSTRUCTION_ERROR
import com.android.sample.resources.C.TestTag.AddInstructionStepScreen.INSTRUCTION_INPUT
import com.android.sample.resources.C.TestTag.AddInstructionStepScreen.TIME_INPUT
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Typography
import com.android.sample.ui.theme.lightCream
import com.android.sample.ui.utils.ConfirmationPopUp
import com.android.sample.ui.utils.PlateSwipeButton
import com.android.sample.ui.utils.PlateSwipeScaffold

@Composable
fun AddInstructionStepScreen(
    navigationActions: NavigationActions,
    createRecipeViewModel: CreateRecipeViewModel,
    isEditing: Boolean = false
) {
  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = if (isEditing) Route.ACCOUNT else Route.CREATE_RECIPE,
      showBackArrow = true,
      content = { paddingValues ->
        AddInstructionStepContent(
            paddingValues = paddingValues,
            createRecipeViewModel = createRecipeViewModel,
            navigationActions = navigationActions,
            isEditing = isEditing)
      })
}

/**
 * Composable function that displays the content for adding an instruction step.
 *
 * @param paddingValues Padding values to be applied to the content.
 * @param createRecipeViewModel ViewModel for managing the recipe creation process.
 * @param navigationActions Actions for navigating between screens.
 * @param modifier Modifier to be applied to the content.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInstructionStepContent(
    paddingValues: PaddingValues,
    createRecipeViewModel: CreateRecipeViewModel,
    navigationActions: NavigationActions,
    modifier: Modifier = Modifier,
    isEditing: Boolean
) {
  var stepDescription by remember {
    mutableStateOf(
        defaultValues(
            defaultValue = "",
            selectedInstruction = createRecipeViewModel.getSelectedInstruction(),
            onSuccess = { createRecipeViewModel.getInstruction(it).description }))
  }

  var stepTime by remember {
    mutableStateOf(
        defaultValues(
            defaultValue = "",
            selectedInstruction = createRecipeViewModel.getSelectedInstruction(),
            onSuccess = { createRecipeViewModel.getInstruction(it).time }))
  }
  var selectedIcon by remember { mutableStateOf<IconType?>(defaultIcon(createRecipeViewModel)) }
  var showError by remember { mutableStateOf(false) }

  // see if an instruction is being deleted
  var isDeleting by remember { mutableStateOf(false) }

  val focusManager = LocalFocusManager.current

  Column(
      modifier =
          modifier.fillMaxSize().padding(paddingValues).padding(horizontal = HORIZONTAL_PADDING.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Top) {
        // Step Label
        Text(
            text = stringResource(R.string.step_label),
            style = Typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(vertical = 8.dp).testTag("StepLabel"))

        // Container for input fields
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .background(color = lightCream, shape = RoundedCornerShape(8.dp))
                    .padding(CONTAINER_PADDING.dp)
                    .testTag(INPUT_CONTAINER)) {
              Column {
                // Row for input fields for time, and icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom) {

                      // Time input field
                      OutlinedTextField(
                          value = stepTime ?: "",
                          onValueChange = { newValue ->
                            if (checkTimeFormat(newValue)) {
                              stepTime = newValue
                            }
                          },
                          label = {
                            Text(stringResource(R.string.time_label), style = Typography.bodySmall)
                          },
                          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                          colors =
                              TextFieldDefaults.outlinedTextFieldColors(
                                  containerColor = MaterialTheme.colorScheme.background,
                                  focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                  unfocusedLabelColor = MaterialTheme.colorScheme.onSecondary,
                                  focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                                  unfocusedBorderColor = MaterialTheme.colorScheme.onSecondary),
                          modifier = Modifier.weight(1f).testTag(TIME_INPUT),
                          maxLines = MAXLINES_TIME_FIELD)

                      // Icon dropdown menu
                      IconDropdownMenu(
                          selectedIcon = selectedIcon,
                          onIconSelected = { selectedIcon = it },
                          modifier = Modifier.weight(1f).testTag(ICON_DROPDOWN))
                    }

                Spacer(modifier = Modifier.height(SPACE_BETWEEN_ELEMENTS.dp))

                // Instruction input field
                OutlinedTextField(
                    value = stepDescription,
                    onValueChange = { stepDescription = it },
                    label = {
                      Text(
                          stringResource(R.string.instruction_label), style = Typography.bodyMedium)
                    },
                    colors =
                        TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSecondary,
                            focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSecondary),
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(vertical = INSTRUCTION_VERTICAL_PADDING.dp)
                            .verticalScroll(rememberScrollState())
                            .testTag(INSTRUCTION_INPUT),
                    isError = verifyStepDescription(showError, stepDescription),
                    textStyle = Typography.bodySmall,
                    minLines = MINLINES_VISIBLE_FOR_INSTRUCTION,
                    maxLines = MAXLINES_VISIBLE_FOR_INSTRUCTION,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.clearFocus() }))

                // Error message for empty instruction
                if (verifyStepDescription(showError, stepDescription)) {
                  Text(
                      text = stringResource(R.string.error_message_empty_instruction),
                      style = Typography.bodySmall,
                      color = MaterialTheme.colorScheme.error,
                      modifier = Modifier.padding(top = 4.dp).testTag(INSTRUCTION_ERROR))
                }
              }
            }

        Spacer(modifier = Modifier.height(SPACE_BETWEEN_ELEMENTS.dp))

        Row {
          if (createRecipeViewModel.getSelectedInstruction() != null) {
            // suppress button if editing an existing instruction
            PlateSwipeButton(
                stringResource(R.string.RecipeListInstructionsScreen_Delete),
                modifier = Modifier.testTag(DELETE_BUTTON).weight(1f),
                onClick = { isDeleting = true },
                backgroundColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError)
          }
          // Save Button
          PlateSwipeButton(
              stringResource(R.string.save_label),
              modifier = Modifier.testTag(SAVE_BUTTON_TAG).weight(1f),
              onClick = {
                processValidInstruction(
                    stepDescription = stepDescription,
                    stepTime = stepTime,
                    selectedIcon = selectedIcon,
                    createRecipeViewModel = createRecipeViewModel,
                    navigationActions = navigationActions,
                    setShowError = { showError = it },
                    isEditing)
              })
        }

        Spacer(modifier = Modifier.height(SPACE_BETWEEN_ELEMENTS.dp))

        if (isDeleting) {
          ConfirmationPopUp(
              onConfirm = {
                createRecipeViewModel.deleteRecipeInstruction(
                    createRecipeViewModel.getSelectedInstruction()!!)
                createRecipeViewModel.resetSelectedInstruction()
                navigateAfterDelete(isEditing, navigationActions)
              },
              onDismiss = { isDeleting = false },
              titleText = stringResource(R.string.u_sure_u_want_to_delete),
              confirmationButtonText = stringResource(R.string.delete),
              dismissButtonText = stringResource(R.string.cancel))
        }
      }
}
/**
 * Processes a recipe step by validating the instruction and, if valid, assigning the provided
 * details to the recipe. Navigates to the instruction list screen upon success.
 *
 * @param stepDescription The description of the recipe step.
 * @param stepTime The time required for the step (optional).
 * @param selectedIcon The selected icon for the step (optional).
 * @param createRecipeViewModel The ViewModel managing the recipe creation process.
 * @param navigationActions The navigation actions for moving between screens.
 * @param setShowError A lambda to update the error state in the UI.
 */
private fun processValidInstruction(
    stepDescription: String,
    stepTime: String?,
    selectedIcon: IconType?,
    createRecipeViewModel: CreateRecipeViewModel,
    navigationActions: NavigationActions,
    setShowError: (Boolean) -> Unit,
    isEditing: Boolean
) {
  if (stepDescription.isBlank()) {
    setShowError(true) // Trigger the error state if the instruction is blank
  } else {
    confirmAndAssignStep(
        stepDescription,
        stepTime,
        selectedIcon,
        createRecipeViewModel,
        onSuccess = {
          createRecipeViewModel.resetSelectedInstruction()
          navigateAfterValidation(isEditing, navigationActions)
        })
  }
}

/**
 * Selects the default value based on the selected instruction. If an instruction is selected, the
 * value of the instruction is returned. Otherwise, the default value is returned.
 *
 * @param defaultValue The default value to be returned if no instruction is selected.
 * @param selectedInstruction The index of the selected instruction.
 * @param onSuccess Callback to be executed if an instruction is selected.
 * @param T The type of the default value.
 * @return The selected instruction if it exists, otherwise the default value.
 */
fun <T> defaultValues(defaultValue: T, selectedInstruction: Int?, onSuccess: (Int) -> T): T {
  return if (selectedInstruction != null) {
    onSuccess(selectedInstruction)
  } else {
    defaultValue
  }
}

/**
 * Selects the default icon based on the selected instruction. If an instruction is selected, the
 * icon of the instruction is returned. Otherwise, the default icon is returned.
 *
 * @param createRecipeViewModel ViewModel for managing the recipe creation process.
 * @return The selected icon if it exists, otherwise null.
 */
fun defaultIcon(createRecipeViewModel: CreateRecipeViewModel): IconType? {
  return if (createRecipeViewModel.getSelectedInstruction() != null) {
    createRecipeViewModel.getInstruction(createRecipeViewModel.getSelectedInstruction()!!).icon
  } else {
    null
  }
}

/**
 * Verifies if the step description is empty and should show an error.
 *
 * @param showError Boolean indicating if the error should be shown.
 * @param stepDescription The description of the step.
 * @return True if the error should be shown, false otherwise.
 */
fun verifyStepDescription(showError: Boolean, stepDescription: String): Boolean {
  return showError && stepDescription.isBlank()
}

/**
 * Checks if the time format is valid.
 *
 * @param time The time to be checked.
 */
fun checkTimeFormat(time: String): Boolean {
  return time.all { char -> char.isDigit() } && time.length <= TIME_CHARACTER_LIMIT
}

/**
 * Confirms the step and assigns the step details to the view model.
 *
 * @param stepDescription The description of the step.
 * @param stepTime The time required for the step.
 * @param selectedIcon The icon selected for the step.
 * @param createRecipeViewModel ViewModel for managing the recipe creation process.
 * @param onSuccess Callback to be executed if the step is confirmed.
 */
fun confirmAndAssignStep(
    stepDescription: String,
    stepTime: String?,
    selectedIcon: IconType?,
    createRecipeViewModel: CreateRecipeViewModel,
    onSuccess: () -> Unit
) {
  if (createRecipeViewModel.getSelectedInstruction() != null) {
    createRecipeViewModel.updateRecipeInstruction(
        createRecipeViewModel.getSelectedInstruction()!!,
        Instruction(
            description = stepDescription, time = stepTime, iconType = selectedIcon?.iconName))
    onSuccess()
  } else if (stepDescription.isNotEmpty()) {
    createRecipeViewModel.addRecipeInstruction(
        Instruction(
            description = stepDescription, time = stepTime, iconType = selectedIcon?.iconName))
    onSuccess()
  }
}

/**
 * Navigates to the appropriate screen after a delete action based on the editing mode.
 *
 * @param isEditing True if editing an existing recipe, false if creating a new one.
 * @param navigationActions Handles the navigation logic.
 */
@VisibleForTesting
internal fun navigateAfterDelete(
    isEditing: Boolean,
    navigationActions: NavigationActions,
) {
  val targetScreen =
      if (isEditing) Screen.EDIT_RECIPE_LIST_INSTRUCTIONS
      else Screen.CREATE_RECIPE_LIST_INSTRUCTIONS
  val popUpToScreen =
      if (isEditing) Screen.EDIT_CATEGORY_SCREEN else Screen.CREATE_RECIPE_LIST_INGREDIENTS
  navigationActions.navigateToPop(targetScreen, popUpTo = popUpToScreen, inclusive = false)
}

/**
 * Navigates to the appropriate screen after input validation based on the editing mode.
 *
 * @param isEditing True if editing an existing recipe, false if creating a new one.
 * @param navigationActions Handles the navigation logic.
 */
@VisibleForTesting
internal fun navigateAfterValidation(
    isEditing: Boolean,
    navigationActions: NavigationActions,
) {
  val targetScreen =
      if (isEditing) Screen.EDIT_RECIPE_LIST_INSTRUCTIONS
      else Screen.CREATE_RECIPE_LIST_INSTRUCTIONS
  val popUpToScreen =
      if (isEditing) Screen.EDIT_CATEGORY_SCREEN else Screen.CREATE_RECIPE_LIST_INGREDIENTS
  navigationActions.navigateToPop(targetScreen, popUpTo = popUpToScreen, inclusive = false)
}
