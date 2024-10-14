package com.android.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.sample.model.recipe.PreparationTime
import com.android.sample.model.recipe.Recipe
import com.android.sample.ui.screens.RecipeList
import com.android.sample.resources.C
import com.android.sample.ui.authentication.SignInScreen
import com.android.sample.ui.navigation.*
import com.android.sample.ui.screens.AccountScreen
import com.android.sample.ui.screens.AddRecipeScreen
import com.android.sample.ui.screens.FridgeScreen
import com.android.sample.ui.screens.IngredientScreen
import com.android.sample.ui.screens.MainScreen
import com.android.sample.ui.screens.SearchScreen
import com.android.sample.ui.theme.SampleAppTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SampleAppTheme {
        // A surface container using the 'background' color from the theme
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
  // val listViewModel = ListViewModel()

  NavHost(navController = navController, startDestination = Route.SEARCH) {
    navigation(
        startDestination = Screen.AUTH,
        route = Route.AUTH,
    ) {
      composable(Screen.AUTH) { SignInScreen() }
    }
    navigation(
        startDestination = Screen.MAIN,
        route = Route.MAIN,
    ) {
      composable(Screen.MAIN) { MainScreen(navigationActions) }
    }
    navigation(
        startDestination = Screen.FRIDGE,
        route = Route.FRIDGE,
    ) {
      composable(Screen.FRIDGE) { FridgeScreen(navigationActions) }
      composable(Screen.INGREDIENT) { IngredientScreen() }
    }
    navigation(
            startDestination = Screen.RECIPE,
        route = Route.SEARCH,
    ) {
      composable(Screen.SEARCH) { SearchScreen(navigationActions) }
      composable(Screen.RECIPE) { RecipeList(listOf(
          Recipe(
              0,
              "Meal1",
              "Meal1cat",
              "Meal1Area",
              "Meals 1 instructions",
              "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.marieclaire.fr%2Fcuisine%2F15-recettes-saines-et-equilibrees-pour-la-rentree%2C1400263.asp&psig=AOvVaw2EsPz3oLRsz1Nek_WY0sK7&ust=1729001857122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCe9f2HjokDFQAAAAAdAAAAABAE",
              listOf(1, 2, 3),
              listOf("peu", "beaucoup", "peu"),
              2.0,
              PreparationTime(1, 30),
              3),
          Recipe(
              1,
              "Meal2",
              "Meal2cat",
              "Meal2Area",
              "Meals 2 instructions",
              "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.marieclaire.fr%2Fcuisine%2F15-recettes-saines-et-equilibrees-pour-la-rentree%2C1400263.asp&psig=AOvVaw2EsPz3oLRsz1Nek_WY0sK7&ust=1729001857122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCe9f2HjokDFQAAAAAdAAAAABAE",
              listOf(2, 3, 4),
              listOf("un peu", "moyen", "beaucoup"),
              4.0,
              PreparationTime(0, 45),
              4),
          Recipe(
              2,
              "Meal3",
              "Meal3cat",
              "Meal3Area",
              "Meals 3 instructions",
              "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.marieclaire.fr%2Fcuisine%2F15-recettes-saines-et-equilibrees-pour-la-rentree%2C1400263.asp&psig=AOvVaw2EsPz3oLRsz1Nek_WY0sK7&ust=1729001857122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCe9f2HjokDFQAAAAAdAAAAABAE",
              listOf(1, 2),
              listOf("beaucoup", "peu"),
              1.0,
              PreparationTime(1, 15),
              2),
          Recipe(
              3,
              "Meal4",
              "Meal4cat",
              "Meal4Area",
              "Meals 4 instructions",
              "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.marieclaire.fr%2Fcuisine%2F15-recettes-saines-et-equilibrees-pour-la-rentree%2C1400263.asp&psig=AOvVaw2EsPz3oLRsz1Nek_WY0sK7&ust=1729001857122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCe9f2HjokDFQAAAAAdAAAAABAE",
              listOf(3, 5, 7),
              listOf("moyen", "beaucoup", "un peu"),
              3.0,
              PreparationTime(2, 0),
              5),
          Recipe(
              4,
              "Meal5",
              "Meal5cat",
              "Meal5Area",
              "Meals 5 instructions",
              "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.marieclaire.fr%2Fcuisine%2F15-recettes-saines-et-equilibrees-pour-la-rentree%2C1400263.asp&psig=AOvVaw2EsPz3oLRsz1Nek_WY0sK7&ust=1729001857122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCe9f2HjokDFQAAAAAdAAAAABAE",
              listOf(4, 6, 8),
              listOf("beaucoup", "peu", "un peu"),
              2.3,
              PreparationTime(0, 30),
              4)
      ), navigationActions) }
    }
    navigation(
        startDestination = Screen.ADD_RECIPE,
        route = Route.ADD_RECIPE,
    ) {
      composable(Screen.ADD_RECIPE) { AddRecipeScreen(navigationActions) }
    }
    navigation(
        startDestination = Screen.ACCOUNT,
        route = Route.ACCOUNT,
    ) {
      composable(Screen.ACCOUNT) { AccountScreen(navigationActions) }
    }
  }
}
