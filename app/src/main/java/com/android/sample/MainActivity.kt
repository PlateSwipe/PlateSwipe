package com.android.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.sample.resources.C
import com.android.sample.ui.mainPage.MainPage
import com.android.sample.ui.navigation.*
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

  // Setting up the NavHost with Screen.MAIN as the starting destination
  NavHost(navController = navController, startDestination = Route.MAIN) {
    navigation(
        startDestination = Screen.AUTH,
        route = Route.AUTH,
    ) {
      composable(Screen.AUTH) {
        // Your Auth Screen Composable here
        Text("Auth Screen")
      }
    }
    navigation(
        startDestination = Screen.MAIN,
        route = Route.MAIN,
    ) {
      composable(Screen.MAIN) {
        MainPage(navigationActions) // Call your updated MainPage here
      }
    }
    navigation(
        startDestination = Screen.FRIDGE,
        route = Route.FRIDGE,
    ) {
      composable(Screen.FRIDGE) {
        // Your Fridge Screen Composable here
        Text("Fridge Screen")
      }
      composable(Screen.INGREDIENT) {
        // Your Ingredient Screen Composable here
        Text("Ingredient Screen")
      }
    }
    navigation(
        startDestination = Screen.SEARCH,
        route = Route.SEARCH,
    ) {
      composable(Screen.SEARCH) {
        // Your Search Screen Composable here
        Text("Search Screen")
      }
      composable(Screen.RECIPE) {
        // Your Recipe Screen Composable here
        Text("Recipe Screen")
      }
    }
    navigation(
        startDestination = Screen.ADD_RECIPE,
        route = Route.ADD_RECIPE,
    ) {
      composable(Screen.ADD_RECIPE) {
        // Your Add Recipe Screen Composable here
        Text("Add Recipe Screen")
      }
    }
    navigation(
        startDestination = Screen.ACCOUNT,
        route = Route.ACCOUNT,
    ) {
      composable(Screen.ACCOUNT) {
        // Your Account Screen Composable here
        Text("Account Screen")
      }
    }
  }
}
