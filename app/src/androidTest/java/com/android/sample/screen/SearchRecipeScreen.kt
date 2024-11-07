package com.android.sample.screen

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.intent.Intents
import com.android.sample.model.recipe.RecipesRepository
import com.android.sample.model.recipe.RecipesViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.recipeOverview.SearchRecipeScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.anyOrNull

class SearchRecipeScreenTest {

    private lateinit var navigationActions: NavigationActions
    private lateinit var repository: RecipesRepository
    private lateinit var recipesViewModel: RecipesViewModel

    @get:Rule
    val composeTestRule = createComposeRule()


    @Before
    fun setUp() {
        navigationActions = mock(NavigationActions::class.java)
        repository = mock(RecipesRepository::class.java)
        recipesViewModel = RecipesViewModel(repository)
        `when`(navigationActions.currentRoute()).thenReturn(Route.SEARCH)
        `when`(repository.random(eq(1), anyOrNull(), anyOrNull())).then {}
        Intents.init()
    }
    
    @Test
    fun test(){
        composeTestRule.setContent { SearchRecipeScreen(navigationActions, recipesViewModel) }
    }
}