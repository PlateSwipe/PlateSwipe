package com.android.sample.resources

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.resources.C.Tag.ICON_CHEF_HAT

// Like R, but C
object C {
  const val ZERO = 0

  object Tag {

    const val main_screen_container = "main_screen_container"

    // General
    const val LOADING = "Loading..."
    const val PADDING = 16
    const val SMALL_PADDING = 8
    const val IMG_COMPRESS_SIZE = 100
    const val INGREDIENT_DATABASE_NAME = "ingredient_database"
    const val RECIPE_DATABASE_NAME = "recipe_database"
    // Error
    const val ERROR_NULL_IMAGE = "Image is null"
    const val ERROR_STR_MEAL_BLANK = "Recipe name is required and cannot be blank."
    const val ERROR_STR_INSTR_EMPTY = "Recipe instructions are required and cannot be empty."
    const val ERROR_LIST_INGREDIENT_EMPTY = "At least one ingredient is required."
    const val ERROR_STR_THUMBNAIL = "Recipe thumbnail is required and cannot be blank."
    // PlateSwipeButton
    object PlateSwipeButton {
      val BUTTON_WIDTH = 261.dp
      val BUTTON_HEIGHT = 46.dp
    }

    // SwipePage
    object SwipePage {
      const val END_ANIMATION = 1500f
      const val INITIAL_RETRIEVE_NEXT_RECIPE = false
      const val INITIAL_DISPLAY_CARD_1 = true
      const val INITIAL_IS_CLICKING = false
      const val INITIAL_DISPLAY_LIKE = false
      const val INITIAL_DISPLAY_DISLIKE = false
      const val HAT = "Chef's hat"
      const val LIKE = "Like"
      const val DISLIKE = "Dislike"
      const val SCALE_LABEL = "scale animation"
      const val OPACITY_LABEL = "opacity animation"
    }

    object SignInScreen {
      const val PLATE = "Plate"
      const val SWIPE = "Swipe"
      const val SHIFTING_SPACE_TITLE = 50
      const val LOGIN_SUCCESSFUL = "Login successful!"
      const val SIGN_IN_WITH_GOOGLE = "Sign in with Google"
      const val LOGIN_FAILED = "Login Failed!"
      const val ANIMATION_DURATION = 4000L
      const val SIGN_IN_TAG = "SignInScreen"
      const val SIGN_IN_SUCCESSED = "User signed in: "
      const val SIGN_IN_FAILED = "Failed to sign in: "
      const val TRANSITION_LABEL = "transition"
      const val ROTATION_LABEL = "rotation"
      const val X_TRANSLATION_LABEL = "xTranslation"
      const val Y_TRANSLATION_LABEL = "yTranslation"
      const val COOK_DESCRIPTION = "cook image"
      const val GOOGLE_DESCRIPTION = "Google Logo"
      const val SIGN_IN_ERROR_NO_CRED = "Invalid credential type"
    }

    object IngredientSearchScreen {
      const val DO_NOT_SHOW_CONFIRMATION = false
    }

    object Filter {
      const val MIN_SHOULD_NOT_BE_GREATER_THAN_MAX = "min should not be greater than max"
      const val MIN_SHOULD_NOT_BE_NEGATIVE = "Min should not be negative or different from -1"
      const val MAX_SHOULD_NOT_BE_NEGATIVE = "Max should not be negative or different from -1"
      const val MIN_BORN_SHOULD_NOT_BE_NEGATIVE =
          "Born min should not be negative or different from -1"
      const val MAX_BORN_SHOULD_NOT_BE_NEGATIVE =
          "Born max should not be negative or different from -1"
      const val UNINITIALIZED_BORN_VALUE = -1f
    }

    object FilterPage {
      const val TIME_RANGE_MIN = 0f
      const val TIME_RANGE_MAX = 1440f // 24h*60min
      const val MAX_ITEM_IN_ROW = 3 // Choose to optimize with medium and small phone
      const val SLIDER_COLOR_ACTIVE = 0.4f
      const val SLIDER_COLOR_INACTIVE = 0.2f
    }

    object LoadingAnimation {
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
    }

    // RecipesViewModel
    const val MINIMUM_RECIPES_BEFORE_FETCH = 5
    const val NUMBER_RECIPES_TO_FETCH = 15
    const val LOG_TAG_RECIPE_VIEWMODEL = "RecipesViewModel"
    const val ERROR_DELETE_DOWNLOAD = "Error deleting downloaded image"
    const val SUCCESS_DELETE_DOWNLOAD_ALL = "All downloads deleted"
    const val ERROR_DOWNLOAD_IMG = "Error downloading image"
    const val ERROR_RECIPE_WITH_NO_IMG = "Recipe does not have an image"
    const val RECIPE_DOWNLOAD_SUCCESS = "Recipe downloaded successfully"

    // ImageRepositoryFirebase
    const val USER_IMAGE_DIR = "images/user/"
    const val RECIPE_IMAGE_DIR = "images/recipe/"
    const val INGREDIENTS_IMAGE_DIR = "images/ingredient/"
    const val TEST_IMAGE_DIR = "images/test/"

    // RecipeList
    const val RECIPE_LIST_CORNER_RADIUS = 12
    const val RECIPE_RATING_CONTENT_DESCRIPTION = "rating"
    const val RECIPE_FAVORITE_ICON_CONTENT_DESCRIPTION = "like"
    const val RECIPE_DELETE_ICON_CONTENT_DESCRIPTION = "delete"
    const val RECIPE_LIST_DOWNLOAD = "download"

    // SearchBar
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
    object FirestoreRecipesRepository {
      const val FIRESTORE_COLLECTION_NAME = "Recipes with difficulty and time"
      const val MAX_FIRESTORE_FETCH = 5
      const val NO_RECIPE_FOUND_MSG = "No recipes found"
      const val NOT_ENOUGH_RECIPE_MSG = "Not enough recipes found"
      const val REPOSITORY_TAG_MSG = "FirestoreRecipesRepository"
      const val ERROR_GETTING_DOCUMENT = "Error getting documents"
      const val FILTER_RANDOM_FACTOR = 5
    }

    // UserRepositoryFirestore
    object UserRepositoryFirestore {
      const val FRIDGE_FIELD_ID = "id"
      const val FRIDGE_FIELD_QUANTITY = "quantity"
      const val FRIDGE_FIELD_EXPIRATION_DATE = "expirationDate"
      const val FRIDGE_FIELD_EXPIRATION_DATE_YEAR = "year"
      const val FRIDGE_FIELD_EXPIRATION_DATE_MONTH = "monthValue"
      const val FRIDGE_FIELD_EXPIRATION_DATE_DAY = "dayOfMonth"
    }

    // Values for storage
    const val FIRESTORE_RECIPE_NAME = "name"
    const val FIRESTORE_RECIPE_CATEGORY = "category"
    const val FIRESTORE_RECIPE_AREA = "area"
    const val FIRESTORE_RECIPE_PICTURE_ID = "pictureID"
    const val FIRESTORE_RECIPE_INSTRUCTIONS_TEXT = "instructions_text"
    const val FIRESTORE_RECIPE_INSTRUCTION_TIME = "instruction_time"
    const val FIRESTORE_RECIPE_INSTRUCTION_ICON = "instruction_icon"
    const val FIRESTORE_RECIPE_INGREDIENTS = "ingredients"
    const val FIRESTORE_RECIPE_MEASUREMENTS = "measurements"
    const val FIRESTORE_RECIPE_TIME = "time"
    const val FIRESTORE_RECIPE_DIFFICULTY = "difficulty"
    const val FIRESTORE_RECIPE_PRICE = "price"
    const val FIRESTORE_RECIPE_URL = "url"
    const val CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    const val FIRESTORE_RECIPE_IMAGE_NAME = "Main"

    // FirestoreIngredientRepository
    const val FIRESTORE_INGREDIENT_COLLECTION_NAME_TEST = "ingredients test"
    // Values for storage
    const val FIRESTORE_INGREDIENT_NAME = "name"
    const val FIRESTORE_INGREDIENT_BARCODE = "barCode"
    const val FIRESTORE_INGREDIENT_BRANDS = "brands"
    const val FIRESTORE_INGREDIENT_QUANTITY = "quantity"
    const val FIRESTORE_INGREDIENT_CATEGORIES = "categories"
    const val FIRESTORE_INGREDIENT_IMAGES = "images"

    // AggregatorIngredientRepository
    const val AGGREGATOR_LOG_TAG = "AggregatorIngredientRepository"
    const val AGGREGATOR_ERROR_FIRESTORE_ADD_INGR = "Error adding ingredient to Firestore:"
    const val AGGREGATOR_SUCCESS_FIRESTORE_ADD_INGR = "Ingredient successfully updated in Firestore"
    const val AGGREGATOR_ERROR_UPLOAD_IMAGE = "Failed to upload all images"
    const val AGGREGATOR_ERROR_UPLOAD_FORMAT_IMAGE = "Error uploading format"
    const val AGGREGATOR_ERROR_OPENFOOD_INGR_NOT_FOUND = "Ingredient not found in OpenFoodFacts: "
    const val AGGREGATOR_ERROR_OPENFOOD_INGR_NULL = "Ingredient from OpenFoodFacts is null: "
    const val AGGREGATOR_ERROR_GET_INGR_FIRESTORE = "Error getting ingredient from Firestore:"
    const val OPENFOODFACT_REPO_IMAGE_ULR_INVALID = "Image URLs are not provided"
    const val INGREDIENT_VIEWMODEL_LOG_TAG = "IngredientViewModel"
    const val INGR_DOWNLOAD_ERROR_GET_ING = "Error getting ingredient"
    const val INGR_DOWNLOAD_ERROR_DOWNLOAD_IMAGE = "Error downloading image"
    const val INGR_DOWNLOAD_ERROR_NULL_POINTER = "Error getting URI"
    const val INGR_ROOM_NOT_FOUND = "Ingredient not found in Room"
    const val INGR_DOWNLOAD_FAILED = "Ingredient download failed"
    const val INGR_DOWNLOAD_SUCCESS = "Ingredient downloaded: "

    // OpenFoodFactsIngredientRepository
    const val OPEN_FOOD_FACTS_URL = "https://world.openfoodfacts.org"
    const val PRODUCT_NAME = "product_name"
    const val PRODUCT_BRAND = "brands"
    const val PRODUCT_ID = "_id"
    const val PRODUCT_QUANTITY = "quantity"
    const val PRODUCT_CATEGORIES = "categories_tags"
    const val PRODUCT_FRONT_IMAGE_NORMAL_URL = "image_front_url"
    const val PRODUCT_FRONT_IMAGE_NORMAL = "display_normal"
    const val PRODUCT_FRONT_IMAGE_THUMBNAIL_URL = "image_front_thumb_url"
    const val PRODUCT_FRONT_IMAGE_THUMBNAIL = "display_thumbnail"
    const val PRODUCT_FRONT_IMAGE_SMALL_URL = "image_front_small_url"
    const val PRODUCT_FRONT_IMAGE_SMALL = "display_small"
    val PRODUCT_NAME_OFF_SUFFIXES =
        arrayOf(
            "",
            "_en",
            "_fr",
            "_es",
            "_de",
            "_it",
            "_nl",
            "_pt",
            "_pl",
            "_sv",
            "_fi",
            "_da",
            "_cs",
            "_sk",
            "_hu",
            "_lt",
            "_lv",
            "_et",
            "_mt",
            "_hr",
            "_sl",
            "_bg",
            "_ro",
            "_el")
    const val PRODUCT_CATEGORIES_PREFIX = "en:"

    const val INGR_DB_COL_NAME_UID = "uid"
    const val INGR_DB_COL_NAME_BARCODE = "barcode"
    const val INGR_DB_COL_NAME_NAME = "name"
    const val INGR_DB_COL_NAME_BRANDS = "brands"
    const val INGR_DB_COL_NAME_QUANTITY = "quantity"
    const val INGR_DB_COL_NAME_CATEGORIES = "categories"
    const val INGR_DB_COL_NAME_IMAGES = "images"
    const val INGR_DB_COL_NAME_TABLE = "ingredient"

    const val FRIDGE_DB_COL_NAME_TABLE = "fridge"
    const val FRIDGE_DB_COL_NAME_ID = "id"
    const val FRIDGE_DB_COL_NAME_QTY = "quantity"
    const val FRIDGE_DB_COL_NAME_EXPIRATION_DATE = "expirationDate"
    const val FRIDGE_DATABASE_NAME = "fridge_database"
    // EXCEPTION MESSAGES
    const val LIMIT_MUST_BE_POSITIVE_MESSAGE = "Limit must be greater than 0"
    const val UNSUPPORTED_MESSAGE = "Operation not supported"
    const val INGREDIENT_NOT_FOUND_MESSAGE = "Ingredient not found"
    const val INGREDIENT_NAME_NOT_PROVIDED = "Ingredient name is required but has not been provided"
    const val EXCEPTION = "Exception: "

    const val GET_ALL_DOWNLOAD_RECIPE = "Downloaded recipes fetched"
    // Camera Actions
    const val LOG_TAG_CAMERA_ACTIONS = "CameraActions"
    const val UNBINDING_ERR = "Error in unbinding all use cases"

    // Camera Utils
    const val SCAN_THRESHOLD = 3
    const val LOG_TAG_CAMERA_UTILS = "CameraUtils"
    const val INVALID_BARCODE_MSG = "Invalid barcode"

    // ChefImage composable
    val CHEF_IMAGE_CORNER_RADIUS = 16.dp
    val CHEF_IMAGE_STANDING_ORIGINAL_RATIO = 224 / 300f

    // RecipeProgressBar
    const val INITIAL_RECIPE_STEP = 0
    const val SECOND_STEP_OF_THE_CREATION = 1
    const val THIRD_STEP_OF_THE_CREATION = 2

    // RecipeNameScreen
    val RECIPE_NAME_BASE_PADDING = 16.dp
    val RECIPE_NAME_BUTTON_HEIGHT = 46.dp
    val RECIPE_NAME_BUTTON_WIDTH = 261.dp
    val RECIPE_NAME_FIELD_HEIGHT = 60.dp
    val RECIPE_NAME_FONT_SPACING = 0.14.sp
    val RECIPE_NAME_CHARACTER_LIMIT = 50
    const val SCREEN_WIDTH_THRESHOLD = 400
    const val SCREEN_HEIGHT_THRESHOLD = 865
    val CORNER_SHAPE_TEXT_FIELD = 8
    val MAXLINES_RECIPE_NAME_FIELD = 2

    // RecipeStepScreen
    val BASE_PADDING = 8.dp

    val BUTTON_WIDTH = 200.dp
    val BUTTON_HEIGHT = 50.dp

    // AddInstructionStepScreen
    const val HORIZONTAL_PADDING = 16
    const val CONTAINER_PADDING = 16
    const val SPACE_BETWEEN_ELEMENTS = 16
    const val INSTRUCTION_VERTICAL_PADDING = 8
    const val SAVE_BUTTON_TAG = "saveButton"
    const val MAXLINES_TIME_FIELD = 1
    const val MINLINES_VISIBLE_FOR_INSTRUCTION = 10
    const val MAXLINES_VISIBLE_FOR_INSTRUCTION = 10
    const val TIME_CHARACTER_LIMIT = 4

    // IconDropdownMenu
    const val HORIZONTAL_PADDING_ICON_DROPDOWN = 8
    const val VERTICAL_PADDING_ICON_DROPDOWN = 19
    const val SIZE_DROPDOWN_ICON = 24

    // PublishRecipeScreen
    val CHEF_IMAGE_DESCRIPTION = "Chef illustration inside an egg"
    val CHEF_IN_EGG_ORIGINAL_RATIO = 412 / 500f
    const val CORNER_SHAPE_PUBLISH_BUTTON = 8

    // CreateRecipeViewModel
    const val RECIPE_PUBLISHED_SUCCESS_MESSAGE = "Recipe published successfully!"
    const val RECIPE_PUBLISH_ERROR_MESSAGE = "Failed to publish recipe: %1\$s"
    const val RECIPE_UPDATED_SUCCESS_MESSAGE = "Recipe updated successfully!"
    const val RECIPE_UPDATE_ERROR_MESSAGE = "Failed to update recipe: %1\$s"

    // Offline Screen
    const val TEST_TAG_OFFLINE_SCREEN_TITLE = "offlineScreenTitle"
    const val TEST_TAG_OFFLINE_SCREEN_DESCRIPTION = "offlineScreenDescription"
    const val TEST_TAG_OFFLINE_SCREEN_IMAGE = "offlineScreenImage"

    // IconType
    const val ICON_FIRE = "fire"
    const val ICON_SALT = "salt"
    const val ICON_MORTAR = "mortar"
    const val ICON_AXE = "axe"
    const val ICON_CHEF_HAT = "chef hat"

    // UserViewModel
    object UserViewModel {
      const val REMOVED_INGREDIENT_NOT_IN_FRIDGE_ERROR =
          "Cannot remove an ingredient that is not in the fridge."
      const val LOG_TAG = "UserViewModel"
      const val IMAGE_NAME = "Main"
      const val NOT_FOUND_INGREDIENT_IN_DATABASE_ERROR = "Ingredient not found in the database."
      const val FAILED_TO_FETCH_INGREDIENT_FROM_DATABASE_ERROR =
          "Failed to fetch ingredient from the database."
      const val FAILED_TO_FETCH_LIKED_RECIPE_FROM_DATABASE_ERROR =
          "Failed to fetch liked recipes from the database."
      const val FAILED_TO_FETCH_CREATED_RECIPE_FROM_DATABASE_ERROR =
          "Failed to fetch created recipes from the database."
      const val RECIPE_DELETED_SUCCESSFULY = "Recipe deleted successfully"
      const val FAILED_TO_DELETE_RECIPE = "Recipe deletion unsuccessful"
      const val IMAGE_DELETION_SUCCESSFULY = "Recipe image deleted successfully"
      const val FAILED_TO_DELETE_IMAGE = "Recipe image deletion unsuccessful"
      const val RECIPE_NOT_FOUND = "Recipe not found"
    }

    // AccountScreen
    object AccountScreen {
      const val PROFILE_PICTURE_CONTENT_DESCRIPTION = "profilePicture"
    }

    // EditAccountScreen
    object EditAccountScreen {
      const val LOG_MESSAGE_TAG = "EditAccountScreen"
      const val CHANGE_PROFILE_PICTURE_BUTTON_DESCRIPTION = "Change Profile Picture button"
      const val DATE_OF_BIRTH_FIELD_DESCRIPTION = "Selected Date"
    }

    // Create Recipe Add Image
    const val ADD_IMAGE_STEP = 4

    // TimePickerScreen
    const val MINUTES_PER_HOUR = 60
    const val HOUR_RANGE_START = 0
    const val HOUR_RANGE_END = 23
    const val MINUTE_RANGE_START = 0
    const val MINUTE_RANGE_END = 59
  }

  object Values {
    object RecipeOverview {
      const val INITIAL_NUMBER_PERSON_PER_RECIPE = 1
    }
  }

  object Dimension {

    const val PADDING_4 = 4
    const val PADDING_8 = 8
    const val PADDING_16 = 16
    const val PADDING_32 = 32

    object SwipePage {
      const val FILTER_ICON_SIZE = 30
      const val FILTER_ICON_WEIGHT = 1f
      const val CHIPS_WEIGHT = 1f
      const val CARD_WEIGHT = 15f
      const val BUTTON_COOK_SIZE = 24
      const val INITIAL_OFFSET_X = 0f
      const val MIN_OFFSET_X = 0
      const val SWIPE_THRESHOLD = 1f / 3f
      const val DISPLAY_CARD_FRONT = 1f
      const val DISPLAY_CARD_BACK = 0f
      const val CORNER_RADIUS = 16
      const val CARD_ELEVATION = 4
      const val BUTTON_ELEVATION = 4
      const val BUTTON_RADIUS = 12
      const val BUTTON_PADDING = 12
      const val ANIMATION_PADDING_TOP = 64
      const val ANIMATION_SWIPE_TIME = 60
      const val ANIMATION_OPACITY_TIME = 300
      const val ANIMATION_OPACITY_MIN = 0f
      const val ANIMATION_OPACITY_MAX = 1f
      const val MIN_INTENSITY = -1f
      const val MAX_INTENSITY = 1f
      const val THRESHOLD_INTENSITY = 0
      const val SCREEN_MIN = 0f
      const val DESCRIPTION_WEIGHT = 3f
      const val DESCRIPTION_FONT_SIZE = 20
      const val FRIDGE_INGREDIENTS_WEIGHT = 1f
      const val FRIDGE_INGREDIENT_SIZE = 24
      const val LIKE_DISLIKE_ANIMATION_PADDING_RATE = 2f / 11f
      const val LIKE_DISLIKE_ANIMATION_ICON_SCALE_MIN = 0.8f
      const val LIKE_DISLIKE_ANIMATION_ICON_SCALE_MAX = 1.5f
      const val DURATION_ICON_SCALE = 500
      const val SCALE_UP = 1.2f
      const val SCALE_UP_TIME = 150
      const val SCALE_MAX = 1.5f
      const val SCALE_MAX_TIME = 250
      const val SCALE_END = 1.4f
      const val SCALE_END_TIME = 400
      const val BACKGROUND_ANIMATION = 2f
    }

    object LoadingCook {
      const val COOK_SIZE = 250
      const val ROTATION_MIN = 0f
      const val ROTATION_MAX = -360f
      const val ROTATION_DURATION = 2000
    }

    object RecipeOverview {
      const val IMAGE_ROUND_CORNER = 10
      const val COUNTER_ROUND_CORNER = 25
      const val OVERVIEW_RECIPE_STAR_SIZE = 24
      const val OVERVIEW_TIME_DISPLAY_RATE = 1f / 12f
      const val OVERVIEW_FRIDGE_INGREDIENT_THUMBNAIL_SIZE = 25
      const val OVERVIEW_CHECKBOX_SIZE = 15
      const val OVERVIEW_RECIPE_ROUND = 5
      const val OVERVIEW_RECIPE_ROUND_ROW = 10
      const val OVERVIEW_RECIPE_CARD_SHAPE = 16
      const val OVERVIEW_RECIPE_CARD_ELEVATION = 4
      const val OVERVIEW_RECIPE_RATE = 1f / 3f
      const val OVERVIEW_INSTRUCTION_START = 25
      const val OVERVIEW_INSTRUCTION_END = 15
      const val OVERVIEW_INSTRUCTION_TOP = 10
      const val OVERVIEW_INSTRUCTION_BOTTOM = 5
      const val OVERVIEW_FONT_SIZE_MEDIUM = 20
    }

    object Counter {
      const val MIN_VALUE = 1
      const val MAX_VALUE = 99
      const val COUNTER_MIN_MAX_SIZE = 38
      const val COUNTER_TEXT_SIZE = 24
      const val COUNTER_FONT_SIZE = 17
      const val RECIPE_COUNTER_PADDING = 0
      const val REMOVE = "removeServings"
      const val NUMBER = "numberServings"
      const val ADD = "addServings"
    }

    object EditFridgeItemScreen {
      const val TITLE_LINE_HEIGHT = 28
      const val TITLE_FONT_SIZE = 24
      const val TEXT_FONT_SIZE = 20
      const val EPOCH_LITERAL = 86400000L
    }

    object SignInScreen {
      const val SWIPE = "Swipe"
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

      const val WIDTH_BASE = 1080f
      const val HEIGHT_BASE = 2265f
      const val DURATION_ROTATION_LOOP = 30000

      const val CUBE_EASING_A = 0.25f
      const val CUBE_EASING_B = 0.1f
      const val CUBE_EASING_C = 0.25f
      const val CUBE_EASING_D = 1.0f

      const val COOK_SIZE = 300
      const val GOOGLE_LOGO_SIZE = 30
    }

    // CameraScanCodeBarScreen
    object CameraScanCodeBarScreen {

      // PLATE SWIPE SCAFFOLD
      const val TOP_BAR_HEIGHT = 50
      const val TOP_BAR_TITLE_FONT_SIZE = 28
      const val BACK_ARROW_ICON_SIZE = 26
      const val EDIT_ACCOUNT_ICON_SIZE = 26
      const val CHEF_HAT_ICON_SIZE = 35
      const val CHEF_HAT_ICON_END_PADDING = 8
      const val BOTTOM_BAR_HEIGHT = 60

      // BARCODE FRAME
      const val BARCODE_FRAME_WIDTH = 1f
      const val BARCODE_FRAME_HEIGHT = 0.4f
      const val BARCODE_FRAME_PADDING = 32
      const val BARCODE_FRAME_BORDER_WIDTH = 1
      const val BARCODE_FRAME_BORDER_RADIUS = 8

      // INGREDIENT OVERLAY
      const val INGREDIENT_OVERLAY_HEIGHT = 170

      // INGREDIENT DISPLAY
      const val INGREDIENT_DISPLAY_BORDER_RADIUS = 10
      const val INGREDIENT_DISPLAY_PADDING = 8
      const val INGREDIENT_DISPLAY_IMAGE_WEIGHT = 0.3f
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

    object ConfirmationPopUp {
      const val TEXT_LINE_HEIGHT = 20
      const val TEXT_FONT_SIZE = 18
    }

    object RecipeAddImageScreen {
      const val CONTENT_WIDTH = 0.8f
      const val TEXT_HEIGHT = 0.05f
      const val SPACER = 0.03f
      const val IMAGE = 0.3f
      const val ICON_WEIGHT = 0.5f
      const val ICON_SIZE = 0.1f
    }

    object CreateRecipeListInstructionsScreen {
      const val REALLY_SMALL_PADDING = 4
      const val BIG_PADDING = 32
      const val MEDIUM_PADDING = 16
      const val ROUNDED_CORNER_SHAPE = 4
      const val CARD_BORDER_ROUND = 8
      const val CARD_CORNER_RADIUS = 8
      const val CARD_SHADOW_ELEVATION = 8
      const val ICON_SIZE = 24
      const val ROW_SIZE = 1f
      const val RECIPE_NAME_MAX_CHAR = 14

      // progress bar value
      const val CURRENT_STEP = 2
    }

    object IngredientSearchScreen {
      const val SPACER_WEIGHT = 1f
      const val IMAGE_WEIGHT = 4f
      const val ICON_SCANNER_SIZE = 40
      const val ICON_SCANNER_WEIGHT = 1f
      const val RESULT_FONT_SIZE = 20
      const val LOADING_COOK_WEIGHT = 1f
      const val LOADING_COOK_SIZE = 150
      const val POP_UP_ELEVATION = 4
      const val INGREDIENT_ITEM_ELEVATION = 4
      const val INGREDIENT_ITEM_CORNER = 8
      const val INGREDIENT_ITEM_MAX_LINE = 2
    }

    object IngredientListScreen {
      const val NAME_SIZE = 30
      const val INGREDIENT_LIST_SIZE = 20
      const val INGREDIENT_LIST_WEIGHT = 6f
      const val INGREDIENT_PREVIEW_ELEVATION = 4
      const val INGREDIENT_PREVIEW_CORNER = 8
      const val INPUT_MAX_LINE = 1
      const val BUTTON_ROUND = 4
      const val BUTTON_Z = 1f
      const val POP_UP_DELAY = 3000L
      const val POP_UP_WIDTH = 2
      const val ROUND_CORNER = 12
      const val NAME_MAX_LINE = 2
    }

    object RecipeList {
      const val EDIT_ICON_SIZE = 24
    }

    object AccountScreen {
      const val ACCOUNT_SCREEN_SELECTED_LIST_SPACER_ELEMENTS = 16
      const val ACCOUNT_SCREEN_SELECTED_LIST_WEIGHT = 1f
      const val ACCOUNT_SCREEN_SELECTED_LIST_HEIGHT = 30
      const val ACCOUNT_SCREEN_SELECTED_LIST_SEPARATOR_FILL_MAX_WIDTH = .8f
      const val ACCOUNT_SCREEN_SELECTED_LIST_SEPARATOR_THICKNESS = 2
    }

    object CategoryScreen {
      const val DROPDOWN_HEIGHT_FRACTION = 0.3f
      const val DIVIDER_ALPHA = 0.12f
    }

    object FridgeScreen {
      const val TITLE_FONT_SIZE = 20
      const val MAX_ORANGE_DAY = 5
      const val MIN_ORANGE_DAY = 1
      const val ALL_BAR = 1.0f
      const val MAX_PROPORTION = 15f
      const val MIN_PROPORTION = 0.1f
      const val BAR_HEIGHT = 10
      const val BAR_ROUND_CORNER = 4
      const val ITEM_ALPHA = 0.7f
      const val INGREDIENT_MAX_LINE = 3
      const val INGREDIENT_NAME_FONT_SIZE = 16
      const val FRIDGE_TAG_CORNER = 16
      const val INGREDIENT_IMAGE_SIZE = 100
      const val EDIT_ICON_SIZE = 20
      const val CARD_ELEVATION = 4
      const val EMPTY_FRIDGE_FONT_SIZE = 40
      const val NUMBER_CARD_IN_A_ROW = 2
      const val BORDER_FRIDGE_WIDTH = 2.5
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

    object Utils {
      // PLATE SWIPE SCAFFOLD
      const val SEARCH_BAR = "searchBar"
      const val TOP_BAR = "topBar"
      const val TOP_BAR_TITLE = "topBarTitle"
      const val BACK_ARROW_ICON = "backArrowIcon"
      const val EDIT_ACCOUNT_ICON = "editAccountIcon"
      const val PLATESWIPE_SCAFFOLD = "plateSwipeScaffold"
      const val CHEF_HAT_ICON = "chefHatIcon"
      const val BOTTOM_BAR = "bottomNavigationMenu"
      const val TEST_TAG = "TagTestTag"
    }

    object SwipePage {
      const val FILTER = "filter"
      const val FILTER_ROW = "filterRow"
      const val TIME_RANGE_CHIP = "timeRangeChip"
      const val DIFFICULTY_CHIP = "difficultyChip"
      const val CATEGORY_CHIP = "categoryChip"
      const val RECIPE_IMAGE_1 = "recipeImage1"
      const val RECIPE_IMAGE_2 = "recipeImage2"
      const val DRAGGABLE_ITEM = "draggableItem"
      const val RECIPE_NAME = "recipeName"
      const val RECIPE_FRIDGE_INGREDIENTS = "recipeFridgeIngredients"
      const val RECIPE_FRIDGE_INGREDIENTS_TEXT = "recipeFridgeIngredientsText"
      const val VIEW_RECIPE_BUTTON = "viewRecipeButton"
      const val DELETE_SUFFIX = "Delete"
    }

    object RecipeOverview {
      const val DRAGGABLE_ITEM = "draggableItem"
      const val RECIPE_IMAGE = "recipeImage"
      const val RECIPE_TITLE = "recipeTitle"
      const val RECIPE_FRIDGE_INGREDIENTS = "recipeFridgeIngredients"
      const val RECIPE_FRIDGE_INGREDIENTS_TEXT = "recipeFridgeIngredientsText"
      const val INGREDIENTS_VIEW = "ingredientsView"
      const val INSTRUCTIONS_VIEW = "instructionsView"
      const val INGREDIENT_CHECKBOX = "checkboxIngredient"
      const val INGREDIENT_PREFIX = "ingredient"
      const val SLIDING_BUTTON_INGREDIENTS = "ingredientButton"
      const val SLIDING_BUTTON_INSTRUCTIONS = "instructionsButton"
      const val PREP_TIME_TEXT = "prepTimeText"
      const val COOK_TIME_TEXT = "cookTimeText"
      const val TOTAL_TIME_TEXT = "totalTimeText"
    }

    object RecipeList {
      const val RECIPE_LIST_TEST_TAG = "recipeList"
      const val RECIPE_CARD_TEST_TAG = "recipeCard"
      const val RECIPE_PRICE_RATING_TEST_TAG = "priceRating"
      const val RECIPE_CATEGORIES_TEST_TAG = "recipeCategories"
      const val RECIPE_IMAGE_TEST_TAG = "recipeImage"
      const val RECIPE_TITLE_TEST_TAG = "recipeTitle"
      const val RECIPE_FAVORITE_ICON_TEST_TAG = "recipeFavoriteIcon"
      const val RECIPE_DOWNLOAD_ICON_TEST_TAG = "recipeDownloadIcon"
      const val RECIPE_DELETE_ICON_TEST_TAG = "recipeDeleteIcon"
      const val CONFIRMATION_POP_UP = "confirmationPopUp"
      const val CONFIRMATION_BUTTON = "confirmButton"
      const val CANCEL_BUTTON = "cancelButton"
    }

    object SignInScreen {
      const val TACO = "taco"
      const val SUSHI = "sushi"
      const val AVOCADO = "avocado"
      const val TOMATO = "tomato"
      const val PANCAKES = "pancakes"
      const val BROCCOLI = "broccoli"
      const val PASTA = "pasta"
      const val SALAD = "salad"
      const val PEPPER = "pepper"
      const val COOK_IMAGE = "cookImage"
      const val LOGIN_TITLE = "loginTitle"
      const val PLATE_TEXT = "plateText"
      const val SWIPE_TEXT = "swipeText"
      const val LOGIN_BUTTON = "loginButton"
    }

    object AccountScreen {
      const val USERNAME_TEST_TAG = "userName"
      const val LIKED_RECIPES_BUTTON_TEST_TAG = "likedRecipesButton"
      const val CREATED_RECIPES_BUTTON_TEST_TAG = "createdRecipesButton"
      const val PROFILE_PICTURE_TEST_TAG = "profilePicture"
      const val RECIPE_EDIT_ICON_TEST_TAG = "recipeEditIcon"
    }

    object EditAccountScreen {
      const val SAVE_CHANGES_BUTTON_TAG = "saveChangesButton"
      const val PROFILE_PICTURE_TAG = "profilePicture"
      const val USERNAME_FIELD_TAG = "usernameTextField"
      const val EMAIL_FIELD_TAG = "emailTextField"
      const val CHANGE_PROFILE_PICTURE_BUTTON_TAG = "changeProfilePictureButton"
      const val DATE_OF_BIRTH_TEXT_FIELD_TAG = "dateOfBirthTextField"
      const val DATE_OF_BIRTH_CHANGE_BUTTON_TAG = "dateOfBirthChangeButton"
      const val DATE_PICKER_POP_UP_TAG = "datePickerPopUp"
      const val DATE_PICKER_POP_UP_CONFIRM_TAG = "confirmationButtonPopUp"
      const val DATE_PICKER_POP_UP_CANCEL_TAG = "dismissButtonPopUp"
    }

    object CreateRecipeListInstructionsScreen {
      const val SCREEN_COLUMN = "ScreenColumn"
      const val INSTRUCTION_TEXT_SPACE = "InstructionTextSpace"
      const val INSTRUCTION_TEXT_IN_CARD = "InstructionTextInCard"
      const val INSTRUCTION_TIME = "InstructionTime"
      const val EDIT_INSTRUCTION_ICON = "EditInstructionIcon"
      const val INSTRUCTION_TEXT = "InstructionsText"
      const val RECIPE_NAME_TEXT = "RecipeNameText"
      const val INSTRUCTION_LIST = "InstructionList"
      const val INSTRUCTION_LIST_ITEM = "InstructionListItem"
      const val NEXT_STEP_BUTTON = "NextStepButton"
      const val RECIPE_LIST_INSTRUCTIONS_SCREEN_SPACER1 = "RecipeListInstructionsScreenSpacer1"
      const val RECIPE_LIST_ITEM_THUMBNAIL = "InstructionThumbnail"
      const val RECIPE_LIST_INSTRUCTION_ICON = "InstructionIcon"
      const val ADD_INSTRUCTION_BUTTON = "AddInstructionButton"
      const val ADD_ICON_DESCRIPTION = "Add"
      const val EDIT_ICON_DESCRIPTION = "Edit"
      const val STEP_ICON_DESCRIPTION = "Step"
    }

    object RecipeAddImageScreen {
      const val BOX_IMAGE = "box for image"
      const val DISPLAY_IMAGE = "display_image"
      const val DISPLAY_IMAGE_DEFAULT = "display_image_default"
      const val ROW_BUTTON = "row for buttons"
      const val CAMERA_BUTTON = "camera button"
      const val GALLERY_BUTTON = "gallery button"
      const val BOX_NEXT_BUTTON = "box for next button"
      const val ROW_FOR_CHEF = "row for chef image"
      const val MAIN_COL = "main column"
      const val MAIN_BOX = "main box"
      const val COL_2 = "col 2"
      const val TITLE_COL = "title col"
    }

    object ChefImage {
      const val CHEF_IMAGE = "Chef Image"
    }

    object IngredientSearchScreen {
      const val DRAGGABLE_ITEM = "DraggableItem"
      const val SCANNER_ICON = "scannerIcon"
      const val CONFIRMATION_POPUP = "confirmationPopUp"
      const val CONFIRMATION_BUTTON = "confirmButton"
      const val CANCEL_BUTTON = "cancelButton"
    }

    object IngredientListScreen {
      const val RECIPE_NAME = "recipeName"
      const val ADD_INGREDIENT_ICON = "addIngredientIcon"
      const val NEXT_STEP_BUTTON = "nextStepButton"
    }

    object FilterPage {
      const val TEST_TAG_TIME_RANGE_SLIDER = "timeRangeSlider"
      const val TEST_TAG_DIFFICULTY = "difficulty"
      const val TEST_TAG_CATEGORY = "category"
    }

    object Instruction {
      const val DEFAULT_ICON = ICON_CHEF_HAT
    }

    object RecipeBuilder {
      const val OUT_OF_BOUNDS_MESSAGE = "Index out of bounds"
    }

    object AddInstructionStepScreen {
      const val INPUT_CONTAINER = "InputContainer"
      const val TIME_INPUT = "TimeInput"
      const val ICON_DROPDOWN = "IconDropdown"
      const val INSTRUCTION_INPUT = "InstructionInput"
      const val INSTRUCTION_ERROR = "InstructionError"
      const val DELETE_BUTTON = "DeleteButton"
    }

    object Category {
      const val DROPDOWN_CORNER_RADIUS = 8
      const val BUTTON_TEST_TAG = "NextStepButton"

      // Category
      const val CATEGORY_SUBTITLE = "CategorySubtitle"
      const val CATEGORY_DROPDOWN = "CategoryDropdown"

      // Difficulty
      const val DIFFICULTY_SUBTITLE = "DifficultySubtitle"
      const val DIFFICULTY_DROPDOWN = "DifficultyDropdown"
    }

    object TimePicker {
      const val TIME_PICKER_TITLE = "TimePickerTitle"
      const val TIME_PICKER_DESCRIPTION = "TimePickerDescription"
      const val HOUR_PICKER = "HourPicker"
      const val MINUTE_PICKER = "MinutePicker"
      const val NEXT_BUTTON = "NextButton"
      const val HOURS_LABEL = "HoursLabel"
      const val MINUTES_LABEL = "MinutesLabel"
    }

    object Fridge {
      const val RED = "red"
      const val GREEN = "green"
      const val ORANGE = "orange"
    }

    object PlateSwipeDropdown {
      const val DROPDOWN = "PlateSwipeDropdown"
      const val DROPDOWN_ITEM = "PlateSwipeDropdownItem"
      const val DROPDOWN_TITLE = "PlateSwipeDropdownTitle"
    }

    object RecipeNameScreen {
      const val LOADING_COOK_TEST_TAG = "LoadingCook"
    }
  }
}
