package com.android.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.sample.model.ingredient.IngredientViewModel
import com.android.sample.model.recipe.CreateRecipeViewModel
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.model.user.UserViewModel
import com.android.sample.resources.C
import com.android.sample.resources.C.Tag.SECOND_STEP_OF_THE_CREATION
import com.android.sample.resources.C.Tag.THIRD_STEP_OF_THE_CREATION
import com.android.sample.ui.account.AccountScreen
import com.android.sample.ui.authentication.SignInScreen
import com.android.sample.ui.camera.CameraScanCodeBarScreen
import com.android.sample.ui.camera.CameraTakePhotoScreen
import com.android.sample.ui.createRecipe.AddInstructionStepScreen
import com.android.sample.ui.createRecipe.CategoryScreen
import com.android.sample.ui.createRecipe.CreateRecipeScreen
import com.android.sample.ui.createRecipe.IngredientListScreen
import com.android.sample.ui.createRecipe.PublishRecipeScreen
import com.android.sample.ui.createRecipe.RecipeAddImageScreen
import com.android.sample.ui.createRecipe.RecipeIngredientsScreen
import com.android.sample.ui.createRecipe.RecipeInstructionsScreen
import com.android.sample.ui.createRecipe.RecipeListInstructionsScreen
import com.android.sample.ui.filter.FilterPage
import com.android.sample.ui.fridge.FridgeScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.recipe.SearchRecipeScreen
import com.android.sample.ui.recipeOverview.RecipeOverview
import com.android.sample.ui.searchIngredient.SearchIngredientScreen
import com.android.sample.ui.swipePage.SwipePage
import com.android.sample.ui.theme.SampleAppTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SampleAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
            color = MaterialTheme.colorScheme.background) {
              PlateSwipeApp()
            }
      }
    }
  }
}

@Composable
fun PlateSwipeApp() {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)
  val recipesViewModel: RecipesViewModel = viewModel(factory = RecipesViewModel.Factory)
  val ingredientViewModel: IngredientViewModel = viewModel(factory = IngredientViewModel.Factory)

  val userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory)
  val createRecipeViewModel: CreateRecipeViewModel =
      viewModel(factory = CreateRecipeViewModel.Factory)

  NavHost(navController = navController, startDestination = Route.AUTH) {
    navigation(
        startDestination = Screen.AUTH,
        route = Route.AUTH,
    ) {
      composable(Screen.AUTH) { SignInScreen(navigationActions, userViewModel) }
    }
    navigation(
        startDestination = Screen.SWIPE,
        route = Route.SWIPE,
    ) {
      composable(Screen.SWIPE) { SwipePage(navigationActions, recipesViewModel, userViewModel) }
      composable(Screen.OVERVIEW_RECIPE) { RecipeOverview(navigationActions, recipesViewModel) }
      composable(Screen.FILTER) { FilterPage(navigationActions, recipesViewModel) }
    }
    navigation(
        startDestination = Screen.FRIDGE,
        route = Route.FRIDGE,
    ) {
      composable(Screen.FRIDGE) { FridgeScreen(navigationActions) }
      composable(Screen.CAMERA_SCAN_CODE_BAR) {
        CameraScanCodeBarScreen(navigationActions, ingredientViewModel)
      }
      composable(Screen.CAMERA_TAKE_PHOTO) {
        CameraTakePhotoScreen(navigationActions, createRecipeViewModel)
      }
    }
    navigation(
        startDestination = Screen.SEARCH,
        route = Route.SEARCH,
    ) {
      composable(Screen.SEARCH) { SearchRecipeScreen(navigationActions) }
    }
    navigation(
        startDestination = Screen.CREATE_RECIPE,
        route = Route.CREATE_RECIPE,
    ) {
      composable(Screen.CREATE_RECIPE) {
        CreateRecipeScreen(
            navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
      }

      composable(Screen.CREATE_CATEGORY_SCREEN) {
        CategoryScreen(navigationActions, createRecipeViewModel)
      }
      composable(Screen.CREATE_RECIPE_INGREDIENTS) {
        RecipeIngredientsScreen(
            navigationActions = navigationActions,
            currentStep = SECOND_STEP_OF_THE_CREATION,
            ingredientViewModel = ingredientViewModel)
      }
      composable(Screen.CREATE_RECIPE_INSTRUCTIONS) {
        RecipeInstructionsScreen(
            navigationActions = navigationActions, currentStep = THIRD_STEP_OF_THE_CREATION)
      }
      composable(Screen.CREATE_RECIPE_ADD_INSTRUCTION) {
        AddInstructionStepScreen(
            navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
      }

      composable(Screen.CREATE_RECIPE_LIST_INSTRUCTIONS) {
        RecipeListInstructionsScreen(
            navigationActions = navigationActions, createRecipeViewModel = createRecipeViewModel)
      }
      composable(Screen.CREATE_RECIPE_ADD_IMAGE) {
        RecipeAddImageScreen(navigationActions, createRecipeViewModel)
      }
      composable(Screen.CAMERA_TAKE_PHOTO) {
        CameraTakePhotoScreen(navigationActions, createRecipeViewModel)
      }
      composable(Screen.PUBLISH_CREATED_RECIPE) {
        PublishRecipeScreen(
            navigationActions = navigationActions,
            createRecipeViewModel = createRecipeViewModel,
            userViewModel = userViewModel)
      }

      composable(Screen.CREATE_RECIPE_SEARCH_INGREDIENTS) {
        SearchIngredientScreen(
            navigationActions = navigationActions,
            searchIngredientViewModel = ingredientViewModel,
            popUpTitle = stringResource(R.string.pop_up_title),
            popUpConfirmationText = stringResource(R.string.pop_up_description),
            popUpConfirmationButtonText = stringResource(R.string.pop_up_confirmation),
            onConfirmation = {
              ingredientViewModel.addIngredient(it)
              navigationActions.navigateTo(Screen.CREATE_RECIPE_LIST_INGREDIENTS)
            })
      }

      composable(Screen.CREATE_RECIPE_LIST_INGREDIENTS) {
        IngredientListScreen(
            navigationActions = navigationActions,
            ingredientViewModel = ingredientViewModel,
            createRecipeViewModel = createRecipeViewModel)
      }
      composable(Screen.CAMERA_SCAN_CODE_BAR) {
        CameraScanCodeBarScreen(
            navigationActions = navigationActions, searchIngredientViewModel = ingredientViewModel)
      }
    }
    navigation(
        startDestination = Screen.ACCOUNT,
        route = Route.ACCOUNT,
    ) {
      composable(Screen.ACCOUNT) { AccountScreen(navigationActions, userViewModel) }
      composable(Screen.OVERVIEW_RECIPE_ACCOUNT) {
        RecipeOverview(navigationActions, userViewModel)
      }
    }
  }
}
