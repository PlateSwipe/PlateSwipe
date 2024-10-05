package com.android.sample.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.android.sample.R

object Route {
  const val AUTH = "Auth"
  const val MAIN = "Overview"
  const val FRIDGE = "Fridge"
  const val SEARCH = "Search"
  const val ADD_RECIPE = "AddRecipe"
  const val ACCOUNT = "Account"
}

object Screen {
  const val AUTH = "Auth Screen"

  const val MAIN = "Main Screen"

  const val FRIDGE = "Fridge Screen"
  const val INGREDIENT = "Ingredient Screen"

  const val SEARCH = "Search Screen"
  const val RECIPE = "Recipe Screen"

  const val ADD_RECIPE = "AddRecipe Screen"

  const val ACCOUNT = "Account Screen"
}

data class TopLevelDestination(val route: String, val iconId: Int, val textId: String)

// TODO: Find good icons for each Route
object TopLevelDestinations {
  val MAIN = TopLevelDestination(Route.MAIN, R.drawable.mainpageicon, "Main")
  val FRIDGE = TopLevelDestination(Route.FRIDGE, R.drawable.fridgeicon, "Fridge")
  val SEARCH = TopLevelDestination(Route.SEARCH, R.drawable.searchicon, "Search")
  val ADD_RECIPE = TopLevelDestination(Route.ADD_RECIPE, R.drawable.addicon, "Add Recipe")
  val ACCOUNT = TopLevelDestination(Route.ACCOUNT, R.drawable.downloadicon, "Account")
}

val LIST_TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestinations.MAIN,
        TopLevelDestinations.SEARCH,
        TopLevelDestinations.FRIDGE,
        TopLevelDestinations.ADD_RECIPE,
        TopLevelDestinations.ACCOUNT)

open class NavigationActions(
    private val navController: NavHostController,
) {
  /**
   * Navigate to the specified [TopLevelDestination]
   *
   * @param destination The top level destination to navigate to Clear the back stack when
   *   navigating to a new destination This is useful when navigating to a new screen from the
   *   bottom navigation bar as we don't want to keep the previous screen in the back stack
   */
  open fun navigateTo(destination: TopLevelDestination) {

    navController.navigate(destination.route) {
      // Pop up to the start destination of the graph to
      // avoid building up a large stack of destinations
      popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
        inclusive = true
      }

      // Avoid multiple copies of the same destination when reselecting same item
      launchSingleTop = true

      // Restore state when reselecting a previously selected item
      if (destination.route != Route.AUTH) {
        restoreState = true
      }
    }
  }

  /**
   * Navigate to the specified screen.
   *
   * @param screen The screen to navigate to
   */
  open fun navigateTo(screen: String) {
    navController.navigate(screen)
  }

  /** Navigate back to the previous screen. */
  open fun goBack() {
    navController.popBackStack()
  }

  /**
   * Get the current route of the navigation controller.
   *
   * @return The current route
   */
  open fun currentRoute(): String {
    return navController.currentDestination?.route ?: ""
  }
}
