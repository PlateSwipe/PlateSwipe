package com.android.sample.resources

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R

// Like R, but C
object C {
  object Tag {

    const val main_screen_container = "main_screen_container"

    // General
    const val LOADING = "Loading..."
    const val PADDING = 16
    const val SMALL_PADDING = 8

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
      const val INITIAL_DISPLAY_CARD_2 = false
      const val INITIAL_IS_CLICKING = false
      const val INITIAL_DISPLAY_LIKE = false
      const val INITIAL_DISPLAY_DISLIKE = false
      const val RATE_VALUE = "4.5"
      const val HAT = "Chef's hat"
      const val LIKE = "Like"
      const val DISLIKE = "Dislike"
      const val SCALE_LABEL = "scale animation"
      const val OPACITY_LABEL = "opacity animation"
    }

    object SignInScreen {
      const val NONCE = "WhyShouldIUseThis"
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
    const val FIRESTORE_INGREDIENT_QUANTITY = "quantity"
    const val FIRESTORE_INGREDIENT_CATEGORIES = "categories"
    const val FIRESTORE_INGREDIENT_IMAGES = "images"

    // AggregatorIngredientRepository
    const val AGGREGATOR_TAG_ON_INGREDIENT_ADDED = "Ingredient added successfully"

    // OpenFoodFactsIngredientRepository
    const val OPEN_FOOD_FACTS_URL = "https://world.openfoodfacts.net"
    const val OPEN_FOOD_FACTS_INGREDIENT_REPOSITORY_TAG = "OpenFoodFactsIngredientRepository"
    const val PRODUCT_NAME = "product_name"
    const val PRODUCT_BRAND = "brands"
    const val PRODUCT_ID = "_id"
    const val PRODUCT_QUANTITY = "quantity"
    const val PRODUCT_CATEGORIES = "categories"
    const val PRODUCT_FRONT_IMAGE_URL = "image_front_url"
    const val PRODUCT_FRONT_IMAGE = "display_normal"
    const val PRODUCT_FRONT_IMAGE_THUMBNAIL_URL = "image_front_thumb_url"
    const val PRODUCT_FRONT_IMAGE_THUMBNAIL = "display_thumbnail"
    const val PRODUCT_FRONT_IMAGE_SMALL_URL = "image_front_small_url"
    const val PRODUCT_FRONT_IMAGE_SMALL = "display_small"
    const val INGREDIENT_IMAGE_ADDED_SUCCESSFULLY =
        "Ingredient image added successfully to the storage"

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

    // ChefImage composable
    val CHEF_IMAGE_CORNER_RADIUS = 16.dp
    val CHEF_IMAGE_STANDING_ORIGINAL_RATIO = 224 / 300f

    // RecipeProgressBar
    const val INITIAL_RECIPE_STEP = 0
    const val SECOND_STEP_OF_THE_CREATION = 1
    const val THIRD_STEP_OF_THE_CREATION = 2

    // RecipeNameScreen Specific Constants
    val RECIPE_NAME_BASE_PADDING = 16.dp
    val RECIPE_NAME_FIELD_SPACING = 30.dp
    val RECIPE_NAME_BUTTON_WIDTH = 261.dp
    val RECIPE_NAME_BUTTON_HEIGHT = 46.dp
    val RECIPE_NAME_FIELD_HEIGHT = 60.dp
    val RECIPE_NAME_FONT_SPACING = 0.14.sp
    val RECIPE_NAME_CHARACTER_LIMIT = 50
    const val SCREEN_WIDTH_THRESHOLD = 400
    const val SCREEN_HEIGHT_THRESHOLD = 865

    // RecipeStepScreen
    val BASE_PADDING = 16.dp

    val BUTTON_WIDTH = 200.dp
    val BUTTON_HEIGHT = 50.dp

    // AddInstructionStepScreen
    const val HORIZONTAL_PADDING = 16
    const val SAVE_BUTTON_TAG = "saveButton"

    // PublishRecipeScreen
    val CHEF_IMAGE_DESCRIPTION = "Chef illustration inside an egg"
    val CHEF_IN_EGG_ORIGINAL_RATIO = 412 / 500f

    // CreateRecipeViewModel
    const val RECIPE_PUBLISHED_SUCCESS_MESSAGE = "Recipe published successfully!"
    const val RECIPE_PUBLISH_ERROR_MESSAGE = "Failed to publish recipe: %1\$s"

    // UserViewModel
    const val REMOVED_TOO_MANY_INGREDIENTS_ERROR =
        "Cannot remove more ingredients than there are in the fridge."
    const val REMOVED_INGREDIENT_NOT_IN_FRIDGE_ERROR =
        "Cannot remove an ingredient that is not in the fridge."
  }

  object Values {
    object RecipeOverview {
      const val INITIAL_NUMBER_PERSON_PER_RECIPE = 1
    }
  }

  object Dimension {
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
      const val ANIMATION_PADDING_SWIPE = 200
      const val ANIMATION_PADDING_TOP = 64
      const val ANIMATION_SWIPE_TIME = 50
      const val ANIMATION_OPACITY_TIME = 300
      const val ANIMATION_OPACITY_MIN = 0f
      const val ANIMATION_OPACITY_MAX = 1f
      const val MIN_INTENSITY = -1f
      const val MAX_INTENSITY = 1f
      const val THRESHOLD_INTENSITY = 0
      const val SCREEN_MIN = 0f
      const val DESCRIPTION_WEIGHT = 3f
      const val DESCRIPTION_FONT_SIZE = 20
      const val STAR_WEIGHT = 1f
      const val STAR_SIZE = 24
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
      const val COUNTER_MIN_MAX_SIZE = 30
      const val OVERVIEW_TIME_DISPLAY_RATE = 1f / 12f
      const val OVERVIEW_MIN_COUNTER_VALUE = 1
      const val OVERVIEW_MAX_COUNTER_VALUE = 99
      const val OVERVIEW_CHECKBOX_SIZE = 15
      const val OVERVIEW_COUNTER_TEXT_SIZE = 28
      const val OVERVIEW_RECIPE_ROUND = 5
      const val OVERVIEW_RECIPE_ROUND_ROW = 10
      const val OVERVIEW_RECIPE_CARD_SHAPE = 16
      const val OVERVIEW_RECIPE_CARD_ELEVATION = 4
      const val OVERVIEW_RECIPE_COUNTER_PADDING = 0
      const val OVERVIEW_RECIPE_RATE = 1f / 3f
      const val OVERVIEW_INSTRUCTION_START = 25
      const val OVERVIEW_INSTRUCTION_END = 15
      const val OVERVIEW_INSTRUCTION_TOP = 10
      const val OVERVIEW_INSTRUCTION_BOTTOM = 5
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
      const val TOP_BAR_HEIGHT = 40
      const val TOP_BAR_TITLE_FONT_SIZE = 28
      const val BACK_ARROW_ICON_SIZE = 26
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

    object CreateRecipeListInstructionsScreen {
      const val REALLY_SMALL_PADDING = 4
      const val BIG_PADDING = 32
      const val SMALL_PADDING = 8
      const val MEDIUM_PADDING = 16
      const val LIST_HEIGHT_FRACTION = 0.8899f
      const val ROUNDED_CORNER_SHAPE = 4
      const val CARD_BORDER_THICKNESS = 1
      const val CARD_BORDER_ROUND = 8
      const val CARD_CORNER_RADIUS = 8
      const val CARD_SHADOW_ELEVATION = 8
      const val ICON_SIZE = 24
      const val ROW_SIZE = 1f
      const val SPACER_SIZE = 1f

      // progress bar value
      const val CURRENT_STEP = 2
    }
  }

  object TestTag {
    object CameraScanCodeBarScreen {
      const val BARCODE_FRAME = "Barcode frame"
    }

    object Utils {
      // PLATE SWIPE SCAFFOLD
      const val TOP_BAR = "topBar"
      const val TOP_BAR_TITLE = "topBarTitle"
      const val BACK_ARROW_ICON = "backArrowIcon"
      const val PLATESWIPE_SCAFFOLD = "plateSwipeScaffold"
      const val CHEF_HAT_ICON = "chefHatIcon"
      const val BOTTOM_BAR = "bottomNavigationMenu"
      const val TEST_TAG = "TagTestTag"
    }

    object SwipePage {
      const val FILTER = "filter"
      const val FILTER_ROW = "filterRow"
      const val TIME_RANGE_CHIP = "timeRangeChip"
      const val PRICE_RANGE_CHIP = "priceRangeChip"
      const val DIFFICULTY_CHIP = "difficultyChip"
      const val CATEGORY_CHIP = "categoryChip"
      const val RECIPE_IMAGE_1 = "recipeImage1"
      const val RECIPE_IMAGE_2 = "recipeImage2"
      const val DRAGGABLE_ITEM = "draggableItem"
      const val RECIPE_NAME = "recipeName"
      const val RECIPE_STAR = "recipeStar"
      const val RECIPE_RATE = "recipeRate"
      const val VIEW_RECIPE_BUTTON = "viewRecipeButton"
      const val DELETE_SUFFIX = "Delete"
    }

    object RecipeOverview {
      const val DRAGGABLE_ITEM = "draggableItem"
      const val RECIPE_IMAGE = "recipeImage"
      const val RECIPE_TITLE = "recipeTitle"
      const val RATING_ICON = "ratingIcon"
      const val RECIPE_STAR = "recipeStar"
      const val RECIPE_RATE = "recipeRate"
      const val INGREDIENTS_VIEW = "ingredientsView"
      const val REMOVE_SERVINGS = "removeServings"
      const val NUMBER_SERVINGS = "numberServings"
      const val ADD_SERVINGS = "addServings"
      const val INSTRUCTIONS_VIEW = "instructionsView"
      const val INSTRUCTIONS_TEXT = "instructionsText"
      const val INGREDIENT_CHECKBOX = "checkboxIngredient"
      const val INGREDIENT_PREFIX = "ingredient"
      const val SLIDING_BUTTON_INGREDIENTS = "ingredientButton"
      const val SLIDING_BUTTON_INSTRUCTIONS = "instructionsButton"
      const val PREP_TIME_TEXT = "prepTimeText"
      const val COOK_TIME_TEXT = "cookTimeText"
      const val TOTAL_TIME_TEXT = "totalTimeText"
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
      const val RECIPE_LIST_INSTRUCTIONS_SCREEN_SPACER2 = "RecipeListInstructionsScreenSpacer2"
      const val RECIPE_LIST_ITEM_THUMBNAIL = "InstructionThumbnail"
      const val RECIPE_LIST_INSTRUCTION_ICON = "InstructionIcon"
    }
  }
}
