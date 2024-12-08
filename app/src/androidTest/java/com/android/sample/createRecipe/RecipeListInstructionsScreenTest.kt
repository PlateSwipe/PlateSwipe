import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.model.recipe.Instruction
import com.android.sample.model.recipe.networkData.FirestoreRecipesRepository
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.ADD_INSTRUCTION_BUTTON
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.INSTRUCTION_LIST_ITEM
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.INSTRUCTION_TEXT
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.NEXT_STEP_BUTTON
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.RECIPE_NAME_TEXT
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.SCREEN_COLUMN
import com.android.sample.ui.createRecipe.RecipeListInstructionsScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipeListInstructionsScreenTest {
  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navigationActions: NavigationActions
  private lateinit var createRecipeViewModel: CreateRecipeViewModel
  private val repository = mockk<FirestoreRecipesRepository>(relaxed = true)
  private val repoImg = mockk<ImageRepositoryFirebase>(relaxed = true)

  @Before
  fun setUp() {
    navigationActions = mockk(relaxed = true)
    createRecipeViewModel = spyk(CreateRecipeViewModel(repository, repoImg))
    createRecipeViewModel.addRecipeInstruction(Instruction("Instruction 1", "10"))
    createRecipeViewModel.updateRecipeName("Recipe Name")
    createRecipeViewModel.updateRecipeTime("30")

    Intents.init()
  }

  @After
  fun tearDown() {
    Intents.release()
  }

  /** Verifies that all UI elements are displayed on the RecipeListInstructionsScreen. */
  @Test
  fun recipeListInstructionsScreen_allFieldsDisplayed() {
    composeTestRule.setContent {
      RecipeListInstructionsScreen(
          navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    composeTestRule.onNodeWithTag(SCREEN_COLUMN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(RECIPE_NAME_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(INSTRUCTION_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(ADD_INSTRUCTION_BUTTON).assertIsDisplayed()
  }

  // Check if everything is clickable and verify navigation action
  @Test
  fun recipeListInstructionsScreen_allFieldsClickable_and_navigatesCorrectly() {
    composeTestRule.setContent {
      RecipeListInstructionsScreen(
          navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
    }

    // Perform clicks on items
    composeTestRule.onNodeWithTag(INSTRUCTION_LIST_ITEM).assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag(NEXT_STEP_BUTTON).assertIsDisplayed().performClick()

    // Verify that the navigateTo function was called with the correct parameter
    verify { navigationActions.navigateTo(Screen.CREATE_RECIPE_TIME_PICKER) }
  }

  @Test
  fun recipeListInstructionsScreen_allFieldsDisplayed_InEditMode() {
    composeTestRule.setContent {
      RecipeListInstructionsScreen(
          navigationActions = navigationActions,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = true)
    }

    composeTestRule.onNodeWithTag(SCREEN_COLUMN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(RECIPE_NAME_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(INSTRUCTION_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(ADD_INSTRUCTION_BUTTON).assertIsDisplayed()
  }

  // Check if everything is clickable and verify navigation action
  @Test
  fun recipeListInstructionsScreen_allFieldsClickable_and_navigatesCorrectly_InEditMode() {
    composeTestRule.setContent {
      RecipeListInstructionsScreen(
          navigationActions = navigationActions,
          createRecipeViewModel = createRecipeViewModel,
          isEditing = true)
    }

    // Perform clicks on items
    composeTestRule.onNodeWithTag(INSTRUCTION_LIST_ITEM).assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag(NEXT_STEP_BUTTON).assertIsDisplayed().performClick()

    // Verify that the navigateTo function was called with the correct parameter
    verify { navigationActions.navigateTo(Screen.EDIT_RECIPE_TIME_PICKER) }
  }
}
