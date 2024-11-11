package com.android.sample.ui.createRecipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.resources.C.Tag.HORIZONTAL_PADDING
import com.android.sample.resources.C.Tag.SAVE_BUTTON_TAG
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Typography
import com.android.sample.ui.theme.lightCream
import com.android.sample.ui.utils.PlateSwipeScaffold

@Composable
fun AddInstructionStepScreen(
    navigationActions: NavigationActions,
    createRecipeViewModel: CreateRecipeViewModel
) {
  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = Route.CREATE_RECIPE,
      showBackArrow = true,
      content = { paddingValues ->
        AddInstructionStepContent(
            paddingValues = paddingValues,
            createRecipeViewModel = createRecipeViewModel,
            navigationActions = navigationActions)
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
    modifier: Modifier = Modifier
) {
  var stepDescription by remember { mutableStateOf(createRecipeViewModel.getRecipeInstructions()) }
  var stepTime by remember { mutableStateOf(createRecipeViewModel.getRecipeTime()) }
  var stepCategory by remember { mutableStateOf(createRecipeViewModel.getRecipeCategory()) }
  var selectedIcon by remember {
    mutableStateOf<IconType?>(createRecipeViewModel.getSelectedIcon())
  }
  var showError by remember { mutableStateOf(false) }

  Column(
      modifier =
          modifier.fillMaxSize().padding(paddingValues).padding(horizontal = HORIZONTAL_PADDING.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Top) {
        // Step Label
        Text(
            text = stringResource(R.string.step_label),
            style = Typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(vertical = 8.dp).testTag("StepLabel"))

        // Container for input fields
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .background(color = lightCream, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
                    .testTag("InputContainer")) {
              Column {
                // Row for input fields for time, category, and icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                      // Time input field
                      OutlinedTextField(
                          value = if (stepTime.isNullOrEmpty()) "" else stepTime.toString(),
                          onValueChange = { stepTime = it },
                          label = {
                            Text(stringResource(R.string.time_label), style = Typography.bodySmall)
                          },
                          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                          colors =
                              TextFieldDefaults.outlinedTextFieldColors(
                                  containerColor = Color.White),
                          modifier = Modifier.weight(1f).heightIn(min = 56.dp).testTag("TimeInput"))

                      // Category input field
                      OutlinedTextField(
                          value = if (stepCategory.isNullOrEmpty()) "" else stepCategory.toString(),
                          onValueChange = { stepCategory = it },
                          label = {
                            Text(
                                stringResource(R.string.category_label),
                                style = Typography.bodySmall)
                          },
                          colors =
                              TextFieldDefaults.outlinedTextFieldColors(
                                  containerColor = Color.White),
                          modifier =
                              Modifier.weight(1f).heightIn(min = 56.dp).testTag("CategoryInput"))
                      // Icon dropdown menu
                      IconDropdownMenu(
                          selectedIcon = selectedIcon,
                          onIconSelected = { selectedIcon = it },
                          modifier =
                              Modifier.weight(1f).heightIn(min = 56.dp).testTag("IconDropdown"))
                    }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = stepDescription,
                    onValueChange = { stepDescription = it },
                    label = {
                      Text(
                          stringResource(R.string.instruction_label), style = Typography.bodyMedium)
                    },
                    colors =
                        TextFieldDefaults.outlinedTextFieldColors(containerColor = Color.White),
                    modifier =
                        Modifier.fillMaxWidth()
                            .heightIn(min = 200.dp)
                            .padding(vertical = 8.dp)
                            .testTag("InstructionInput"),
                    isError = verifyStepDescription(showError, stepDescription))

                // Error message for empty instruction
                if (verifyStepDescription(showError, stepDescription)) {
                  Text(
                      text = stringResource(R.string.error_message_empty_instruction),
                      style = Typography.bodySmall,
                      color = MaterialTheme.colorScheme.error,
                      modifier = Modifier.padding(top = 4.dp).testTag("InstructionError"))
                }
              }
            }

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
              showError = stepDescription.isEmpty() // Set error if instructions are empty
              if (stepDescription.isNotEmpty()) {
                createRecipeViewModel.updateRecipeInstructions(stepDescription)
                if (!stepTime.isNullOrEmpty()) {
                  createRecipeViewModel.updateRecipeTime(stepTime.toString())
                }
                if (!stepCategory.isNullOrEmpty()) {
                  createRecipeViewModel.updateRecipeCategory(stepCategory.toString())
                }
                if (selectedIcon != null) {
                  createRecipeViewModel.selectIcon(selectedIcon!!)
                }

                navigationActions.navigateTo(Screen.CREATE_RECIPE_LIST_INSTRUCTIONS)
              }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).testTag(SAVE_BUTTON_TAG),
            colors =
                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(size = 4.dp),
        ) {
          Text(
              text = stringResource(R.string.save_label),
              style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
        }
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
  return showError && stepDescription.isEmpty()
}
