package com.android.sample.ui.createRecipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import com.android.sample.resources.C.Dimension.PADDING_16
import com.android.sample.resources.C.Dimension.PADDING_32
import com.android.sample.resources.C.Dimension.PADDING_8
import com.android.sample.resources.C.Tag.INITIAL_RECIPE_STEP
import com.android.sample.resources.C.TestTag.Category.BUTTON_TEST_TAG
import com.android.sample.resources.C.TestTag.Category.CATEGORY_DROPDOWN
import com.android.sample.resources.C.TestTag.Category.CATEGORY_SUBTITLE
import com.android.sample.resources.C.TestTag.Category.DIFFICULTY_DROPDOWN
import com.android.sample.resources.C.TestTag.Category.DIFFICULTY_SUBTITLE
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.Typography
import com.android.sample.ui.utils.PlateSwipeButton
import com.android.sample.ui.utils.PlateSwipeDropdownMenu
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
  val noCategoryString = stringResource(R.string.no_category)
  val categories = listOf(noCategoryString) + Recipe.getCategories()
  val selectedCategory = remember { mutableStateOf(createRecipeViewModel.getRecipeCategory()) }

  val noDifficultyString = stringResource(R.string.no_difficulty)
  val difficulties = listOf(noDifficultyString) + Recipe.getDifficulties()
  val selectedDifficulty = remember { mutableStateOf(createRecipeViewModel.getRecipeDifficulty()) }

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
              text = stringResource(R.string.optional_information),
              style = Typography.displayLarge,
              color = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.fillMaxWidth().padding(horizontal = PADDING_16.dp),
              textAlign = TextAlign.Center)

          Spacer(modifier = Modifier.weight(1f))

          // Subtitle
          Text(
              text = stringResource(R.string.select_information_description_optional),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.padding(horizontal = PADDING_32.dp),
              textAlign = TextAlign.Center)

          Spacer(modifier = Modifier.weight(1f))

          // Subtitle Category
          Text(
              text = stringResource(R.string.select_category),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.padding(horizontal = PADDING_32.dp).testTag(CATEGORY_SUBTITLE),
              textAlign = TextAlign.Center)

          Spacer(modifier = Modifier.weight(.5f))

          // Dropdown Category
          PlateSwipeDropdownMenu(
              categories,
              onSelected = { selectedText, _ ->
                selectedCategory.value =
                    if (selectedText == noCategoryString) null else selectedText
              },
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(horizontal = PADDING_16.dp)
                      .testTag(CATEGORY_DROPDOWN),
              defaultItemIndex = selectedCategory.value?.let { categories.indexOf(it) })

          Spacer(modifier = Modifier.weight(1f))

          // Subtitle Difficulty
          Text(
              text = stringResource(R.string.select_difficulty),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.padding(horizontal = PADDING_32.dp).testTag(DIFFICULTY_SUBTITLE),
              textAlign = TextAlign.Center)

          Spacer(modifier = Modifier.weight(.5f))

          // Dropdown Difficulty
          PlateSwipeDropdownMenu(
              difficulties,
              onSelected = { selectedText, _ ->
                selectedDifficulty.value =
                    if (selectedText == noDifficultyString) null else selectedText
              },
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(horizontal = PADDING_16.dp)
                      .testTag(DIFFICULTY_DROPDOWN),
              defaultItemIndex = selectedDifficulty.value?.let { difficulties.indexOf(it) })

          Spacer(modifier = Modifier.weight(1f))

          // Next Step Button
          PlateSwipeButton(
              text = stringResource(R.string.next_step),
              modifier = Modifier.testTag(BUTTON_TEST_TAG),
              onClick = {
                createRecipeViewModel.updateRecipeCategory(selectedCategory.value)
                createRecipeViewModel.updateRecipeDifficulty(selectedDifficulty.value)
                navigationActions.navigateTo(Screen.CREATE_RECIPE_INGREDIENTS)
              })
        }
  }
}
