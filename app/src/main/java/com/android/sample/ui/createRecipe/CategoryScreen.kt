package com.android.sample.ui.createRecipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import com.android.sample.resources.C.Dimension.CategoryScreen.DIVIDER_ALPHA
import com.android.sample.resources.C.Dimension.CategoryScreen.DROPDOWN_HEIGHT_FRACTION
import com.android.sample.resources.C.Dimension.PADDING_16
import com.android.sample.resources.C.Dimension.PADDING_32
import com.android.sample.resources.C.Dimension.PADDING_8
import com.android.sample.resources.C.Tag.INITIAL_RECIPE_STEP
import com.android.sample.resources.C.TestTag.Category.BUTTON_TEST_TAG
import com.android.sample.resources.C.TestTag.Category.CATEGORY_SUBTITLE
import com.android.sample.resources.C.TestTag.Category.CATEGORY_TITLE
import com.android.sample.resources.C.TestTag.Category.DROPDOWN_CORNER_RADIUS
import com.android.sample.resources.C.TestTag.Category.DROPDOWN_TEST_TAG
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
    isEditing: Boolean
) {
  PlateSwipeScaffold(
      navigationActions = navigationActions,
      selectedItem = if (isEditing) Route.ACCOUNT else Route.CREATE_RECIPE,
      showBackArrow = true,
      content = { paddingValues ->
        CategoryContent(
            currentStep = INITIAL_RECIPE_STEP,
            navigationActions = navigationActions,
            createRecipeViewModel = createRecipeViewModel,
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            isEditing = isEditing)
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
    createRecipeViewModel: CreateRecipeViewModel,
    isEditing: Boolean
) {
  val noCategoryString = stringResource(R.string.no_category)
  val categories = listOf(noCategoryString) + Recipe.getCategories()
  val selectedCategory = remember { mutableStateOf(createRecipeViewModel.getRecipeCategory()) }
  val expanded = remember { mutableStateOf(false) }

  Box(modifier = modifier.padding(PADDING_8.dp), contentAlignment = Alignment.TopCenter) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top) {
          // Display the progress bar for the current step
          RecipeProgressBar(currentStep = currentStep)

          Spacer(modifier = Modifier.weight(1f))

          // Title
          Text(
              text =
                  getConditionalStringResource(
                      isEditing, R.string.edit_category, R.string.select_category),
              style = Typography.displayLarge,
              color = MaterialTheme.colorScheme.onPrimary,
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(horizontal = PADDING_16.dp)
                      .testTag(CATEGORY_TITLE),
              textAlign = TextAlign.Center)

          Spacer(modifier = Modifier.weight(1f))

          // Subtitle
          Text(
              text =
                  getConditionalStringResource(
                      isEditing,
                      R.string.edit_category_description_optional,
                      R.string.select_category_description_optional),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.padding(horizontal = PADDING_32.dp).testTag(CATEGORY_SUBTITLE),
              textAlign = TextAlign.Center)

          Spacer(modifier = Modifier.weight(2f))

          // Dropdown menu for selecting category
          Box(modifier = Modifier.fillMaxWidth().padding(horizontal = PADDING_16.dp)) {
            OutlinedButton(
                onClick = { expanded.value = !expanded.value },
                modifier = Modifier.fillMaxWidth().testTag(DROPDOWN_TEST_TAG),
                shape = RoundedCornerShape(DROPDOWN_CORNER_RADIUS.dp),
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface)) {
                  Text(
                      text =
                          selectedCategory.value ?: stringResource(R.string.no_category_selected),
                      style = MaterialTheme.typography.bodyMedium,
                      modifier = Modifier.padding(vertical = PADDING_8.dp))
                }

            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
                modifier =
                    Modifier.padding(horizontal = PADDING_16.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(DROPDOWN_HEIGHT_FRACTION)
                        .background(MaterialTheme.colorScheme.surface)) {
                  categories.forEachIndexed { index, category ->
                    DropdownMenuItem(
                        text = {
                          Text(text = category, style = MaterialTheme.typography.bodyMedium)
                        },
                        onClick = {
                          selectedCategory.value =
                              if (category == noCategoryString) null else category
                          expanded.value = false
                        },
                        modifier = Modifier.testTag("DropdownMenuItem_$category"))
                    if (index < categories.size - 1) {
                      HorizontalDivider(
                          color = MaterialTheme.colorScheme.onSurface.copy(alpha = DIVIDER_ALPHA))
                    }
                  }
                }
          }

          Spacer(modifier = Modifier.weight(8f))
        }

    // Next Step Button
    PlateSwipeButton(
        text = stringResource(R.string.next_step),
        modifier = Modifier.align(Alignment.BottomCenter).testTag(BUTTON_TEST_TAG),
        onClick = {
          createRecipeViewModel.updateRecipeCategory(selectedCategory.value)
          fromCategoryNavigateToNextScreen(isEditing, navigationActions)
        })
  }
}

/**
 * Helper function to navigate to the next screen based on the isEditing flag.
 *
 * @param isEditing Boolean indicating whether the recipe is being edited.
 * @param navigationActions Actions for navigating between screens.
 */
fun fromCategoryNavigateToNextScreen(isEditing: Boolean, navigationActions: NavigationActions) {
  if (isEditing) {
    navigationActions.navigateTo(Screen.EDIT_RECIPE_LIST_INGREDIENTS)
  } else {
    navigationActions.navigateTo(Screen.CREATE_RECIPE_INGREDIENTS)
  }
}
