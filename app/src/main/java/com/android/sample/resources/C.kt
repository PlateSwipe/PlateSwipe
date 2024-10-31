package com.android.sample.resources

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Like R, but C
object C {
  object Tag {

    const val main_screen_container = "main_screen_container"

    // SwipePage
    const val END_ANIMATION = 1500f
    const val FILTER_ICON_DESCRIPTION = "filterIcon"
    const val TIME_RANGE_INPUT_DESCRIPTION = "Time Range"
    const val PRICE_RANGE_INPUT_DESCRIPTION = "Price Range"
    const val DIFFICULTY_INPUT_DESCRIPTION = "Difficulty"
    const val CATEGORY_INPUT_DESCRIPTION = "Category"

    const val LOADING = "Loading..."

    // Filter
    const val MIN_SHOULD_NOT_BE_GREATER_THAN_MAX = "min should not be greater than max"
    const val NEW_MIN_SHOULD_NOT_EXCEED_MAX = "newMin should not exceed max"
    const val NEW_MAX_SHOULD_NOT_BE_NEGATIVE = "newMax should not be negative"
    const val NEW_MIN_SHOULD_NOT_BE_NEGATIVE = "newMin should not be negative"
    const val MIN_SHOULD_NOT_BE_NEGATIVE = "Min should not be negative or different from -1"
    const val MAX_SHOULD_NOT_BE_NEGATIVE = "Max should not be negative or different from -1"
    const val MIN_BORN_SHOULD_NOT_BE_NEGATIVE =
        "Born min should not be negative or different from -1"
    const val MAX_BORN_SHOULD_NOT_BE_NEGATIVE =
        "Born max should not be negative or different from -1"
    const val NEW_MIN_AND_NEW_MAX_SHOULD_BE_WITHIN_RANGE =
        "newMin and newMax should be within range"
    const val UNINITIALIZED_BORN_VALUE = -1f

    // Filter Page
    const val TIME_RANGE_NAME = "Time"
    const val TIME_RANGE_MIN = 0f
    const val TIME_RANGE_MAX = 200f
    const val TIME_RANGE_UNIT = "min"
    const val PRICE_RANGE_NAME = "Price"
    const val PRICE_RANGE_MIN = 0f
    const val PRICE_RANGE_MAX = 100f
    const val PRICE_RANGE_UNIT = "$"
    const val DIFFICULTY_NAME = "Difficulty"
    const val CATEGORY_NAME = "Category"
    const val MAX_ITEM_IN_ROW = 3 // Choose to optimize with medium and small phone

    // RecipesViewModel
    const val MINIMUM_RECIPES_BEFORE_FETCH = 3
    const val NUMBER_RECIPES_TO_FETCH = 2

    // ImageRepositoryFirebase
    const val USER_IMAGE_DIR = "images/user/"
    const val RECIPE_IMAGE_DIR = "images/recipe/"
    const val INGREDIENTS_IMAGE_DIR = "images/ingredient/"

    // RecipeList
    const val RECIPE_LIST_CORNER_RADIUS = 12

    // SearchBar
    const val SEARCH_BAR_PLACE_HOLDER = "Search"
    const val SEARCH_BAR_CORNER_RADIUS = 16
    const val MAXIMUM_RECIPES_TO_FETCH_MEAL_DB = 5
    const val MAX_NB_OF_INGREDIENTS_IN_A_RECIPE_MEAL_DB = 20
    const val MEAL_DB_USER_AGENT = "User-Agent"
    const val MEAL_DB_USER_AGENT_VALUE = "PlateSwipe/1.0 (plateswipe@gmail.com)"

    // MealDBRecipesRepository
    const val MEAL_DB_URL = "https://www.themealdb.com/api/json/v1/1/"
    const val MEAL_DB_ARRAY_NAME = "meals"
    const val MEAL_DB_MEAL_ID = "idMeal"
    const val MEAL_DB_MEAL_NAME = "strMeal"
    const val MEAL_DB_MEAL_CATEGORY = "strCategory"
    const val MEAL_DB_MEAL_AREA = "strArea"
    const val MEAL_DB_MEAL_THUMB = "strMealThumb"
    const val MEAL_DB_MEAL_INSTRUCTIONS = "strInstructions"
    const val MEAL_DB_MEAL_INGREDIENT = "strIngredient"
    const val MEAL_DB_MEAL_MEASURE = "strMeasure"
    const val MEAL_DB_CATEGORY_ARRAY = "categories"

    // FirestoreRecipeRepository
    const val FIRESTORE_COLLECTION_NAME = "created recipe"
    // Values for storage
    const val FIRESTORE_RECIPE_NAME = "name"
    const val FIRESTORE_RECIPE_CATEGORY = "category"
    const val FIRESTORE_RECIPE_AREA = "area"
    const val FIRESTORE_RECIPE_PICTURE_ID = "pictureID"
    const val FIRESTORE_RECIPE_INSTRUCTIONS = "instructions"
    const val FIRESTORE_RECIPE_INGREDIENTS = "ingredients"
    const val FIRESTORE_RECIPE_MEASUREMENTS = "measurements"
    const val FIRESTORE_RECIPE_TIME = "time"
    const val FIRESTORE_RECIPE_DIFFICULTY = "difficulty"
    const val FIRESTORE_RECIPE_PRICE = "price"

    // EXCEPTION MESSAGES
    const val LIMIT_MUST_BE_POSITIVE_MESSAGE = "Limit must be greater than 0"
    const val UNSUPPORTED_MESSAGE = "Operation not supported"

    // Used in ChefImage composable
    val CHEF_IMAGE_WIDTH = 250.dp
    val CHEF_IMAGE_HEIGHT = 300.dp
    val CHEF_IMAGE_CORNER_RADIUS = 16.dp

    // Initial step value for recipe creation flow
    const val INITIAL_RECIPE_STEP = 0

    // RecipeNameScreen Specific Constants
    val RECIPE_NAME_BASE_PADDING = 16.dp
    val RECIPE_NAME_FIELD_SPACING = 30.dp
    val RECIPE_NAME_BUTTON_WIDTH = 261.dp
    val RECIPE_NAME_BUTTON_HEIGHT = 46.dp
    val RECIPE_NAME_FIELD_HEIGHT = 60.dp
    val RECIPE_NAME_FONT_SPACING = 0.14.sp

    // RecipeStepScreen
    val BASE_PADDING = 16.dp

    val BUTTON_WIDTH = 200.dp
    val BUTTON_HEIGHT = 50.dp

    // AddInstructionStepScreen
    const val HORIZONTAL_PADDING = 16
    const val SAVE_BUTTON_WIDTH = 150
    const val SAVE_BUTTON_HEIGHT = 48
    const val DELETE_BUTTON_TAG = "deleteButton"
    const val SAVE_BUTTON_TAG = "saveButton"
    const val STEP_DESCRIPTION_HEIGHT = 150

    // PublishRecipeScreen
    val CHEF_IMAGE_DESCRIPTION = "Chef illustration inside an egg"

    // CreateRecipeViewModel
    const val RECIPE_PUBLISHED_SUCCESS_MESSAGE = "Recipe published successfully!"
    const val RECIPE_PUBLISH_ERROR_MESSAGE = "Failed to publish recipe: %1\$s"
  }
}
