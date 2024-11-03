package com.android.sample

import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.navigation.TopLevelDestinations
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class NavigationActionsTest {

  private lateinit var navigationDestination: NavDestination
  private lateinit var navHostController: NavHostController
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navigationDestination = mock(NavDestination::class.java)
    navHostController = mock(NavHostController::class.java)
    navigationActions = NavigationActions(navHostController)
  }

  @Test
  fun navigateToCallsController() {
    navigationActions.navigateTo(TopLevelDestinations.SWIPE)
    verify(navHostController).navigate(eq(Route.SWIPE), any<NavOptionsBuilder.() -> Unit>())

    navigationActions.navigateTo(Screen.FRIDGE)
    verify(navHostController).navigate(Screen.FRIDGE)
  }

  @Test
  fun goBackCallsController() {
    navigationActions.goBack()
    verify(navHostController).popBackStack()
  }

  @Test
  fun currentRouteWorksWithDestination() {
    `when`(navHostController.currentDestination).thenReturn(navigationDestination)
    `when`(navigationDestination.route).thenReturn(Route.SWIPE)

    assertThat(navigationActions.currentRoute(), `is`(Route.SWIPE))
  }

  @Test
  fun navigateAndClearStackCallsController() {
    val screen = Screen.SEARCH
    val clearUpToRoute = Route.AUTH

    navigationActions.navigateAndClearStack(screen, clearUpToRoute)
    verify(navHostController).navigate(eq(screen), any<NavOptionsBuilder.() -> Unit>())
  }
}
