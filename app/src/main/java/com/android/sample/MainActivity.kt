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

  NavHost(navController = navController, startDestination = Route.AUTH) {
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
        startDestination = Screen.SEARCH,
        route = Route.SEARCH,
    ) {
      composable(Screen.SEARCH) { SearchScreen(navigationActions) }
      composable(Screen.RECIPE) {} // RecipeScreen(), TODO: repository not implemented
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
