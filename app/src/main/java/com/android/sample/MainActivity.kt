package com.android.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.sample.ui.account.AccountScreen
import com.android.sample.ui.authentication.SignInScreen
import com.android.sample.ui.fridge.FridgeScreen
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.recipe.CreateRecipeScreen
import com.android.sample.ui.recipe.SearchRecipeScreen
import com.android.sample.ui.swipePage.SwipePage
import com.android.sample.ui.theme.SampleAppTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SampleAppTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
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

    // Authentication Screen Navigation
    navigation(
        startDestination = Screen.AUTH,
        route = Route.AUTH,
    ) {
      composable(Screen.AUTH) { SignInScreen(navigationActions) }
    }
    // Swipe Screen Navigation
    navigation(
        startDestination = Screen.SWIPE,
        route = Route.SWIPE,
    ) {
      composable(Screen.SWIPE) { SwipePage(navigationActions) }
    }

    // Fridge Screen Navigation
    navigation(
        startDestination = Screen.FRIDGE,
        route = Route.FRIDGE,
    ) {
      composable(Screen.FRIDGE) { FridgeScreen(navigationActions) }
    }

    // Search Recipe Screen Navigation
    navigation(
        startDestination = Screen.SEARCH,
        route = Route.SEARCH,
    ) {
      composable(Screen.SEARCH) { SearchRecipeScreen(navigationActions) }
    }
    // Create Recipe Screen Navigation
    navigation(
        startDestination = Screen.CREATE_RECIPE,
        route = Route.CREATE_RECIPE,
    ) {
      composable(Screen.CREATE_RECIPE) { CreateRecipeScreen(navigationActions) }
    }

    // Account Screen Navigation
    navigation(
        startDestination = Screen.ACCOUNT,
        route = Route.ACCOUNT,
    ) {
      composable(Screen.ACCOUNT) { AccountScreen(navigationActions) }
    }
  }
}
