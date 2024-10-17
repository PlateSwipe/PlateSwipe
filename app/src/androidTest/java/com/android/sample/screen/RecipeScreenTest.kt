package com.android.sample.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import com.android.sample.model.recipe.Recipe
import com.android.sample.model.user.UserRepository
import com.android.sample.model.user.UserViewModel
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.testScreens.Price
import com.android.sample.ui.testScreens.RecipeCard
import com.android.sample.ui.testScreens.RecipeList
import com.android.sample.ui.testScreens.RecipeRating
import com.android.sample.ui.testScreens.RecipeTime
import com.android.sample.ui.testScreens.RecipeTitle
import com.android.sample.ui.testScreens.SearchBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import okhttp3.Call
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class RecipeScreenTest {
  private lateinit var mockNavigationActions: NavigationActions
  private lateinit var mockUserRepository: UserRepository
  private lateinit var mockFirebaseAuth: FirebaseAuth
  private lateinit var mockCurrentUser: FirebaseUser
  private lateinit var userViewModel: UserViewModel
  private lateinit var mockCall: Call
  private val recipesList: List<Recipe> =
      listOf(
          Recipe(
              "0",
              "Meal1",
              "Meal1cat",
              "Meal1Area",
              "Meals 1 instructions",
              "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.marieclaire.fr%2Fcuisine%2F15-recettes-saines-et-equilibrees-pour-la-rentree%2C1400263.asp&psig=AOvVaw2EsPz3oLRsz1Nek_WY0sK7&ust=1729001857122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCe9f2HjokDFQAAAAAdAAAAABAE",
              listOf(Pair("1", "peu"), Pair("2", "beaucoup"), Pair("3", "peu")),
          ),
          Recipe(
              "1",
              "Meal2",
              "Meal2cat",
              "Meal2Area",
              "Meals 2 instructions",
              "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.marieclaire.fr%2Fcuisine%2F15-recettes-saines-et-equilibrees-pour-la-rentree%2C1400263.asp&psig=AOvVaw2EsPz3oLRsz1Nek_WY0sK7&ust=1729001857122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCe9f2HjokDFQAAAAAdAAAAABAE",
              listOf(Pair("1", "peu"), Pair("2", "beaucoup"), Pair("3", "peu")),
          ),
          Recipe(
              "2",
              "Meal3",
              "Meal3cat",
              "Meal3Area",
              "Meals 3 instructions",
              "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.marieclaire.fr%2Fcuisine%2F15-recettes-saines-et-equilibrees-pour-la-rentree%2C1400263.asp&psig=AOvVaw2EsPz3oLRsz1Nek_WY0sK7&ust=1729001857122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCe9f2HjokDFQAAAAAdAAAAABAE",
              listOf(Pair("1", "peu"), Pair("2", "beaucoup"), Pair("3", "peu")),
          ),
          Recipe(
              "3",
              "Meal4",
              "Meal4cat",
              "Meal4Area",
              "Meals 4 instructions",
              "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.marieclaire.fr%2Fcuisine%2F15-recettes-saines-et-equilibrees-pour-la-rentree%2C1400263.asp&psig=AOvVaw2EsPz3oLRsz1Nek_WY0sK7&ust=1729001857122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCe9f2HjokDFQAAAAAdAAAAABAE",
              listOf(Pair("1", "peu"), Pair("2", "beaucoup"), Pair("3", "peu")),
          ),
          Recipe(
              "4",
              "Meal5",
              "Meal5cat",
              "Meal5Area",
              "Meals 5 instructions",
              "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.marieclaire.fr%2Fcuisine%2F15-recettes-saines-et-equilibrees-pour-la-rentree%2C1400263.asp&psig=AOvVaw2EsPz3oLRsz1Nek_WY0sK7&ust=1729001857122000&source=images&cd=vfe&opi=89978449&ved=0CBQQjRxqFwoTCJCe9f2HjokDFQAAAAAdAAAAABAE",
              listOf(Pair("1", "peu"), Pair("2", "beaucoup"), Pair("3", "peu")),
          ))

  private val testRecipe =
      Recipe(
          idMeal = "12345",
          strMeal = "Test Recipe",
          strCategory = "Test Category",
          strArea = "Test Area",
          strInstructions = "Test Instructions",
          strMealThumbUrl = "https://example.com/image.jpg",
          ingredientsAndMeasurements = listOf(Pair("1", "Test Ingredient")),
      )

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    // Mock is a way to create a fake object that can be used in place of a real object
    MockitoAnnotations.openMocks(this)
    mockCall = mock(Call::class.java)

    mockNavigationActions = mock(NavigationActions::class.java)
    mockUserRepository = mock(UserRepository::class.java)
    mockFirebaseAuth = mock(FirebaseAuth::class.java)
    mockCurrentUser = mock(FirebaseUser::class.java)

    `when`(mockFirebaseAuth.currentUser).thenReturn(mockCurrentUser)
    `when`(mockCurrentUser.uid).thenReturn("001")

    userViewModel = UserViewModel(mockUserRepository, mockFirebaseAuth)

    composeTestRule.onRoot(useUnmergedTree = true)

    for (recipe in recipesList) {
      userViewModel.addRecipeToUserLikedRecipes(recipe)
    }

    `when`(mockNavigationActions.currentRoute()).thenReturn(Screen.RECIPE)
  }

  @Test
  fun recipeCard_displaysCorrectContent() {

    composeTestRule.setContent { RecipeCard(recipe = testRecipe) }

    composeTestRule.onNodeWithTag("recipeCard${testRecipe.idMeal}").assertIsDisplayed()
  }

  @Test
  fun recipeTitle_displaysCorrectContent() {

    composeTestRule.setContent { RecipeTitle(recipe = testRecipe) }

    composeTestRule
        .onNodeWithTag("recipeTitle${testRecipe.idMeal}")
        .assertIsDisplayed()
        .assertTextEquals(testRecipe.strMeal)
  }

  @Test
  fun recipeList_displaysCorrectContent() {

    composeTestRule.setContent { RecipeList(userViewModel, mockNavigationActions) }

    composeTestRule.onNodeWithTag("recipeList").assertIsDisplayed()
  }

  @Test
  fun price_displaysCorrectContent() {

    composeTestRule.setContent { Price(cost = 2, recipe = testRecipe) }

    composeTestRule.onNodeWithTag("priceRating${testRecipe.idMeal}").assertIsDisplayed()
  }

  @Test
  fun recipeRating_displaysCorrectContent() {

    composeTestRule.setContent { RecipeRating(recipe = testRecipe) }

    composeTestRule.onNodeWithTag("recipeRating${testRecipe.idMeal}").assertIsDisplayed()
  }

  @Test
  fun recipeTime_displaysCorrectContent() {

    composeTestRule.setContent { RecipeTime(recipe = testRecipe) }

    composeTestRule.onNodeWithTag("recipeTime${testRecipe.idMeal}").assertIsDisplayed()
  }

  @Test
  fun searchBar_displaysCorrectContent() {

    composeTestRule.setContent { SearchBar() }

    composeTestRule.onNodeWithTag("SearchButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("searchText").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("searchTextText", useUnmergedTree = true)
        .assertTextEquals("search")
    composeTestRule.onNodeWithTag("searchButtonIcon", useUnmergedTree = true).assertIsDisplayed()
  }
}
