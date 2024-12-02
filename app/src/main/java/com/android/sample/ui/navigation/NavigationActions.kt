package com.android.sample.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.android.sample.R

object Route {
  const val AUTH = "Auth"
  const val SWIPE = "Swipe"
  const val FRIDGE = "Fridge"
  const val SEARCH = "Search"
  const val CREATE_RECIPE = "AddRecipe"
  const val ACCOUNT = "Account"
}

object Screen {
  const val FILTER = "Filter Screen"

  const val AUTH = "Auth Screen"

  const val SWIPE = "Swipe Screen"

  const val FRIDGE = "Fridge Screen"

  const val SEARCH = "Search Screen"

  const val CREATE_RECIPE = "AddRecipe Screen"

  const val CREATE_CATEGORY_SCREEN = "Add Category Screen"

  const val CREATE_RECIPE_INGREDIENTS = "Add Recipe Ingredients"

  const val CREATE_RECIPE_INSTRUCTIONS = "Add Recipe Instructions"

  const val CREATE_RECIPE_ADD_INSTRUCTION = "Add One Recipe Instruction"

  const val CREATE_RECIPE_SEARCH_INGREDIENTS = "Search Ingredient Screen"

  const val CREATE_RECIPE_LIST_INSTRUCTIONS = "List Recipe Instructions"

  const val PUBLISH_CREATED_RECIPE = "Publish Created Recipe"

  const val ACCOUNT = "Account Screen"

  const val EDIT_ACCOUNT = "Edit Account Screen"

  const val OVERVIEW_RECIPE = "Overview Recipe Screen"

  const val OVERVIEW_RECIPE_ACCOUNT = "Overview Recipe Account Screen"

  const val CAMERA_SCAN_CODE_BAR = "Camera Scan Code Bar Screen"

  const val CAMERA_TAKE_PHOTO = "Camera Take Photo Screen"

  const val CREATE_RECIPE_ADD_IMAGE = "Create Recipe Add Image Screen"

  const val CREATE_RECIPE_LIST_INGREDIENTS = "List Ingredients Screen"

  const val CREATE_RECIPE_TIME_PICKER = "Time Picker Screen"
}

data class TopLevelDestination(val route: String, val iconId: Int, val textId: String)

object TopLevelDestinations {
  val SWIPE = TopLevelDestination(Route.SWIPE, R.drawable.mainpageicon, "Swipe")
  val FRIDGE = TopLevelDestination(Route.FRIDGE, R.drawable.fridgeicon, "Fridge")
  val SEARCH = TopLevelDestination(Route.SEARCH, R.drawable.searchicon, "Search")
  val ADD_RECIPE = TopLevelDestination(Route.CREATE_RECIPE, R.drawable.addicon, "Add Recipe")
  val ACCOUNT = TopLevelDestination(Route.ACCOUNT, R.drawable.account, "Account")
}

val LIST_TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestinations.SWIPE,
        TopLevelDestinations.SEARCH,
        TopLevelDestinations.ADD_RECIPE,
        TopLevelDestinations.FRIDGE,
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
    if (screen == Screen.CREATE_RECIPE_LIST_INGREDIENTS) {
      navController.navigate(screen) {
        popUpTo(Screen.CREATE_CATEGORY_SCREEN) { inclusive = false }
      }
    } else {
      navController.navigate(screen)
    }
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

  /**
   * Navigate to the specified screen and clear the back stack up to the specified destination. This
   * is useful when navigating to a new screen from the bottom navigation bar as we don't want to
   * keep the previous screen in the back stack.
   *
   * @param screen The screen to navigate to
   * @param popUpTo The destination to clear the back stack up to
   * @param inclusive Whether to include the destination in the back stack
   */
  open fun navigateToPop(screen: String, popUpTo: String?, inclusive: Boolean = false) {
    navController.navigate(screen) {
      popUpTo?.let { popUpTo(it) { this.inclusive = inclusive } }
      launchSingleTop = true
    }
  }
}
