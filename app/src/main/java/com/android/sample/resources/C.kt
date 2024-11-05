package com.android.sample.resources

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

    // EXCEPTION MESSAGES
    const val LIMIT_MUST_BE_POSITIVE_MESSAGE = "Limit must be greater than 0"
    const val UNSUPPORTED_MESSAGE = "Operation not supported"

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

    object CameraTakePhotoScreen {
      const val BUTTON_SIZE = 0.1f
      const val BUTTON_PADDING = 0.05f
    }
  }

  object TestTag {

    object CameraScanCodeBarScreen {
      const val BARCODE_FRAME = "Barcode frame"
    }

    object CameraTakePhotoScreen {
      const val BUTTON_BOX = "Take photo button box"
      const val BUTTON = "Take photo button"
    }

    object CameraPreview {
      const val PREVIEW = "camera_preview"
    }
  }
}
