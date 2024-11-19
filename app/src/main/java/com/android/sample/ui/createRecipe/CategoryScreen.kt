package com.android.sample.ui.createRecipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.model.recipe.Recipe
import com.android.sample.resources.C.Tag.INITIAL_RECIPE_STEP
import com.android.sample.resources.C.Tag.RECIPE_NAME_BASE_PADDING
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Typography
import com.android.sample.ui.utils.PlateSwipeButton
import com.android.sample.ui.utils.PlateSwipeScaffold

@Composable
fun CategoryScreen(
    navigationActions: NavigationActions,
    createRecipeViewModel: CreateRecipeViewModel,
) {
  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = Route.CREATE_RECIPE,
      showBackArrow = true,
      content = { paddingValues ->
        CategoryContent(
            currentStep = INITIAL_RECIPE_STEP,
            navigationActions = navigationActions,
            createRecipeViewModel = createRecipeViewModel,
            modifier = Modifier.fillMaxSize().padding(paddingValues))
      })
}

/**
 * Composable function to optionally select a category for a recipe.
 *
 * @param modifier Modifier to be applied to the screen.
 * @param currentStep The current step in the recipe creation process.
 * @param navigationActions Actions for navigating between screens.
 * @param createRecipeViewModel ViewModel for managing the recipe creation process.
 */
@Composable
fun CategoryContent(
    modifier: Modifier = Modifier,
    currentStep: Int,
    navigationActions: NavigationActions,
    createRecipeViewModel: CreateRecipeViewModel
) {
  val categories = Recipe.getCategories()
  val selectedCategory = remember { mutableStateOf(createRecipeViewModel.getRecipeCategory()) }
  val expanded = remember { mutableStateOf(false) }

  Box(
      modifier = modifier.padding(RECIPE_NAME_BASE_PADDING / 2),
      contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top) {
              // Display the progress bar for the current step
              RecipeProgressBar(currentStep = currentStep)

              Spacer(modifier = Modifier.weight(0.05f))

              // Title
              Text(
                  text = stringResource(R.string.select_category),
                  style = Typography.displayLarge,
                  color = MaterialTheme.colorScheme.onPrimary,
                  modifier =
                      Modifier.fillMaxWidth()
                          .padding(horizontal = RECIPE_NAME_BASE_PADDING)
                          .testTag("CategoryTitle"),
                  textAlign = TextAlign.Center)

              Spacer(modifier = Modifier.weight(0.05f))

              // Subtitle
              Text(
                  text = stringResource(R.string.select_category_description_optional),
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onPrimary,
                  modifier =
                      Modifier.padding(horizontal = RECIPE_NAME_BASE_PADDING * 2)
                          .testTag("CategorySubtitle"),
                  textAlign = TextAlign.Center)

              Spacer(modifier = Modifier.weight(0.1f))

              // Dropdown menu for selecting category
              Box(
                  modifier =
                      Modifier.fillMaxWidth().padding(horizontal = RECIPE_NAME_BASE_PADDING)) {
                    OutlinedButton(
                        onClick = { expanded.value = !expanded.value },
                        modifier = Modifier.fillMaxWidth().testTag("DropdownMenuButton"),
                        shape = RoundedCornerShape(8.dp),
                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface)) {
                          Text(
                              text =
                                  selectedCategory.value
                                      ?: stringResource(R.string.select_category_placeholder),
                              style = MaterialTheme.typography.bodyMedium,
                              modifier = Modifier.padding(vertical = 8.dp))
                        }

                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false },
                        modifier =
                            Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
                          categories.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                  Text(text = category, style = MaterialTheme.typography.bodyMedium)
                                },
                                onClick = {
                                  selectedCategory.value = category
                                  expanded.value = false
                                },
                                modifier = Modifier.testTag("DropdownMenuItem_$category"))
                          }
                        }
                  }

              Spacer(modifier = Modifier.weight(0.4f))
            }

        // Next Step Button
        PlateSwipeButton(
            text = stringResource(R.string.next_step),
            modifier = Modifier.align(Alignment.BottomCenter).testTag("NextStepButton"),
            onClick = {
              selectedCategory.value?.let { createRecipeViewModel.updateRecipeCategory(it) }
              navigationActions.navigateTo(Screen.CREATE_RECIPE_INGREDIENTS)
            })
      }
}
