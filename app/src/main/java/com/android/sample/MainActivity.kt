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

  // val listToDosViewModel: TODO: implement ListToDosViewModel

  NavHost(navController = navController, startDestination = Route.AUTH) {
    navigation(
        startDestination = Screen.AUTH,
        route = Route.AUTH,
    ) {
      composable(Screen.AUTH) {}
    }
    navigation(
        startDestination = Screen.MAIN,
        route = Route.MAIN,
    ) {
      composable(Screen.MAIN) {}
    }
    navigation(
        startDestination = Screen.FRIDGE,
        route = Route.FRIDGE,
    ) {
      composable(Screen.FRIDGE) {}
      composable(Screen.INGREDIENT) {}
    }
    navigation(
        startDestination = Screen.SEARCH,
        route = Route.SEARCH,
    ) {
      composable(Screen.SEARCH) {}
      composable(Screen.RECIPE) {}
    }
    navigation(
        startDestination = Screen.ADD_RECIPE,
        route = Route.ADD_RECIPE,
    ) {
      composable(Screen.ADD_RECIPE) {}
    }
    navigation(
        startDestination = Screen.ACCOUNT,
        route = Route.ACCOUNT,
    ) {
      composable(Screen.ACCOUNT) {}
    }
  }
}
