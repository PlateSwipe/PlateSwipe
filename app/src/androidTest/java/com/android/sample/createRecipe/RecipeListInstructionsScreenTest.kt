import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.model.recipe.FirestoreRecipesRepository
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.INSTRUCTION_LIST_ITEM
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.INSTRUCTION_TEXT
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.NEXT_STEP_BUTTON
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.RECIPE_NAME_TEXT
import com.android.sample.resources.C.TestTag.CreateRecipeListInstructionsScreen.SCREEN_COLUMN
import com.android.sample.ui.createRecipe.RecipeListInstructionsScreen
import com.android.sample.ui.navigation.NavigationActions
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
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

  @Before
  fun setUp() {
    navigationActions = mockk(relaxed = true)
    createRecipeViewModel = spyk(CreateRecipeViewModel(repository))

    every { createRecipeViewModel.getRecipeName() } returns "Recipe Name"
    every { createRecipeViewModel.getRecipeTime() } returns "30"
    every { createRecipeViewModel.getRecipeInstructions() } returns "Instructions"

    composeTestRule.setContent {
      RecipeListInstructionsScreen(
          navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
    }
  }

  /** Verifies that all UI elements are displayed on the RecipeListInstructionsScreen. */
  @Test
  fun recipeListInstructionsScreen_allFieldsDisplayed() {
    composeTestRule.onNodeWithTag(SCREEN_COLUMN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(RECIPE_NAME_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(INSTRUCTION_TEXT).assertIsDisplayed()
  }

  @Test
  fun recipeListInstructionsScreenAllClickableFieldsTest() {
    composeTestRule.onNodeWithTag(NEXT_STEP_BUTTON).performClick()
    composeTestRule.onNodeWithTag(INSTRUCTION_LIST_ITEM).performClick()
  }
}
