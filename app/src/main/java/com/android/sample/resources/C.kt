package com.android.sample.resources

import com.android.sample.R

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

    // Sign In
    const val NONCE = "WhyShouldIUseThis"
    const val PLATE = "Plate"
    const val SWIPE = "Swipe"
    const val SHIFTING_SPACE_TITLE = 50
    const val LOGIN_SUCCESSFUL = "Login successful!"
    const val SIGN_IN_WITH_GOOGLE = "Sign in with Google"
    const val LOGIN_FAILED = "Login Failed!"
    const val ANIMATION_DURATION = 4000L

    const val ORIGINAL_ICON_SIZE = 70
    const val TACO_X = -(250)
    const val TACO_Y = 400
    const val TACO_ROTATION = 15
    const val TACO_MVM_DURATION = 1500
    const val TACO_MVM_RANGE = 9f
    const val SUSHI_X = -(350)
    const val SUSHI_Y = 150
    const val SUSHI_ROTATION = 17
    const val SUSHI_MVM_DURATION = 1000
    const val SUSHI_MVM_RANGE = 8f
    const val AVOCADO_X = -(350)
    const val AVOCADO_Y = -(200)
    const val AVOCADO_ROTATION = 15
    const val AVOCADO_MVM_DURATION = 1250
    const val AVOCADO_MVM_RANGE = 9f
    const val TOMATO_X = -(180)
    const val TOMATO_Y = -(450)
    const val TOMATO_ROTATION = 20
    const val TOMATO_MVM_DURATION = 1500
    const val TOMATO_MVM_RANGE = 10f
    const val PANCAKES_X = 200
    const val PANCAKES_Y = -(450)
    const val PANCAKES_ROTATION = 15
    const val PANCAKES_MVM_DURATION = 800
    const val PANCAKES_MVM_RANGE = 6f
    const val BROCCOLI_X = 330
    const val BROCCOLI_Y = -(250)
    const val BROCCOLI_ROTATION = 17
    const val BROCCOLI_MVM_DURATION = 1500
    const val BROCCOLI_MVM_RANGE = 9f
    const val PASTA_X = 345
    const val PASTA_Y = 0
    const val PASTA_ROTATION = 10
    const val PASTA_MVM_DURATION = 1200
    const val PASTA_MVM_RANGE = 12f
    const val SALAD_X = 300
    const val SALAD_Y = 225
    const val SALAD_ROTATION = 17
    const val SALAD_MVM_DURATION = 900
    const val SALAD_MVM_RANGE = 9f
    const val PEPPER_X = 150
    const val PEPPER_Y = 400
    const val PEPPER_ROTATION = 14
    const val PEPPER_MVM_DURATION = 1500
    const val PEPPER_MVM_RANGE = 10f

    const val WIDTH_BASE = 360f
    const val HEIGHT_BASE = 755f
    const val DURATION_ROTATION_LOOP = 30000

    const val CUBE_EASING_A = 0.25f
    const val CUBE_EASING_B = 0.1f
    const val CUBE_EASING_C = 0.25f
    const val CUBE_EASING_D = 1.0f

    const val COOK_SIZE = 300
    const val GOOGLE_LOGO_SIZE = 30
    const val GOOGLE_SIGN_IN_BUTTON_HEIGHT = 48
    const val SPACE = 16

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

    // LoadingAnimation
    const val GRAVITY = 9.8f
    const val INITIAL_SPEED = 100
    val fruitImages =
        listOf(
            R.drawable.taco,
            R.drawable.sushi,
            R.drawable.avocado,
            R.drawable.tomato,
            R.drawable.pancakes,
            R.drawable.broccoli,
            R.drawable.pasta,
            R.drawable.salad,
            R.drawable.pepper)

    const val MAX_ROTATION_START = 90
    const val UNIT = 1
    const val TIME_INIT = 0f
    const val TIME_DELTA = 0.016f
    const val TIME_DELAY = 16L
    const val ROTATION_DELTA = 0.03f
    const val DELAY_RESPAWN = 150L
    const val DELAY_SPAWN = 300L
    const val NUMBER_FRUIT_MAX = 8
    const val FRUIT_SIZE = 120

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
    const val FIRESTORE_COLLECTION_NAME = "recipes"
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
    const val CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

    // FirestoreIngredientRepository
    const val FIRESTORE_INGREDIENT_COLLECTION_NAME = "ingredients"
    // Values for storage
    const val FIRESTORE_INGREDIENT_NAME = "name"
    const val FIRESTORE_INGREDIENT_BARCODE = "barCode"
    const val FIRESTORE_INGREDIENT_BRANDS = "brands"

    // AggregatorIngredientRepository
    const val AGGREGATOR_TAG_ON_INGREDIENT_ADDED = "Ingredient added successfully"

    // EXCEPTION MESSAGES
    const val LIMIT_MUST_BE_POSITIVE_MESSAGE = "Limit must be greater than 0"
    const val UNSUPPORTED_MESSAGE = "Operation not supported"
    const val INGREDIENT_NOT_FOUND_MESSAGE = "Ingredient not found"
    const val INGREDIENT_NAME_NOT_PROVIDED = "Ingredient name is required but has not been provided"

    // Camera Actions
    const val LOG_TAG_CAMERA_ACTIONS = "CameraActions"
    const val UNBINDING_ERR = "Error in unbinding all use cases"

    // Camera Utils
    const val SCAN_THRESHOLD = 3
    const val LOG_TAG_CAMERA_UTILS = "CameraUtils"
    const val INVALID_BARCODE_MSG = "Invalid barcode"

    // CreateRecipeViewModel
    const val RECIPE_PUBLISHED_SUCCESS_MESSAGE = "Recipe published successfully!"
    const val RECIPE_PUBLISH_ERROR_MESSAGE = "Failed to publish recipe: %1\$s"
  }

  object Dimension {

    // CameraScanCodeBarScreen
    object CameraScanCodeBarScreen {

      // BARCODE FRAME
      const val BARCODE_FRAME_WIDTH = 1f
      const val BARCODE_FRAME_HEIGHT = 0.4f
      const val BARCODE_FRAME_PADDING = 32
      const val BARCODE_FRAME_BORDER_WIDTH = 1
      const val BARCODE_FRAME_BORDER_RADIUS = 8

      // INGREDIENT OVERLAY
      const val INGREDIENT_OVERLAY_HEIGHT = 0.4f

      // INGREDIENT DISPLAY
      const val INGREDIENT_DISPLAY_BORDER_RADIUS = 10
      const val INGREDIENT_DISPLAY_PADDING = 8
      const val INGREDIENT_DISPLAY_IMAGE_WEIGHT = 0.3f
      const val INGREDIENT_DISPLAY_IMAGE_PADDING = 8
      const val INGREDIENT_DISPLAY_IMAGE_WIDTH = 100
      const val INGREDIENT_DISPLAY_IMAGE_HEIGHT = 100
      const val INGREDIENT_DISPLAY_IMAGE_BORDER_RADIUS = 8
      const val INGREDIENT_DISPLAY_IMAGE_BORDER_WIDTH = 1
      const val INGREDIENT_DISPLAY_TEXT_WEIGHT = 0.7f
      const val INGREDIENT_DISPLAY_TEXT_PADDING = 8
      const val INGREDIENT_DISPLAY_TEXT_NAME_PADDING_V = 4
      const val INGREDIENT_DISPLAY_TEXT_NAME_PADDING_H = 8
      const val INGREDIENT_DISPLAY_TEXT_BRAND_PADDING_V = 4
      const val INGREDIENT_DISPLAY_TEXT_BRAND_PADDING_H = 8
      const val INGREDIENT_DISPLAY_TEXT_BUTTON_PADDING_V = 4
      const val INGREDIENT_DISPLAY_TEXT_BUTTON_PADDING_H = 8
    }
  }

  object TestTag {
    object CameraScanCodeBarScreen {
      const val BARCODE_FRAME = "Barcode frame"
    }
  }
}
