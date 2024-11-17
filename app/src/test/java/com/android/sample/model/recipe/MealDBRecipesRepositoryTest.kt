package com.android.sample.model.recipe

import com.android.sample.resources.C.Tag.MAXIMUM_RECIPES_TO_FETCH_MEAL_DB
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.capture
import org.mockito.kotlin.doNothing

class MealDBRecipesRepositoryTest {

  private lateinit var mockHttpClient: OkHttpClient
  private lateinit var mockCall: Call
  private lateinit var response: Response
  private lateinit var mockResponseBody: ResponseBody

  private lateinit var mealDBRecipesRepository: MealDBRecipesRepository

  private val mealDBJsonRandomJson1 =
      """{
  "meals": [
    {
      "idMeal": "52771",
      "strMeal": "Spicy Arrabiata Penne",
      "strDrinkAlternate": null,
      "strCategory": "Vegetarian",
      "strArea": "Italian",
      "strInstructions": "Bring a large pot of water to a boil. Add kosher salt to the boiling water, then add the pasta. Cook according to the package instructions, about 9 minutes.\r\nIn a large skillet over medium-high heat, add the olive oil and heat until the oil starts to shimmer. Add the garlic and cook, stirring, until fragrant, 1 to 2 minutes. Add the chopped tomatoes, red chile flakes, Italian seasoning and salt and pepper to taste. Bring to a boil and cook for 5 minutes. Remove from the heat and add the chopped basil.\r\nDrain the pasta and add it to the sauce. Garnish with Parmigiano-Reggiano flakes and more basil and serve warm.",
      "strMealThumb": "https://www.themealdb.com/images/media/meals/ustsqw1468250014.jpg",
      "strTags": "Pasta,Curry",
      "strYoutube": "https://www.youtube.com/watch?v=1IszT_guI08",
      "strIngredient1": "penne rigate",
      "strIngredient2": "olive oil",
      "strIngredient3": "garlic",
      "strIngredient4": "chopped tomatoes",
      "strIngredient5": "red chilli flakes",
      "strIngredient6": "italian seasoning",
      "strIngredient7": "basil",
      "strIngredient8": "Parmigiano-Reggiano",
      "strIngredient9": "",
      "strIngredient10": "",
      "strIngredient11": "",
      "strIngredient12": "",
      "strIngredient13": "",
      "strIngredient14": "",
      "strIngredient15": "",
      "strIngredient16": null,
      "strIngredient17": null,
      "strIngredient18": null,
      "strIngredient19": null,
      "strIngredient20": null,
      "strMeasure1": "1 pound",
      "strMeasure2": "1/4 cup",
      "strMeasure3": "3 cloves",
      "strMeasure4": "1 tin ",
      "strMeasure5": "1/2 teaspoon",
      "strMeasure6": "1/2 teaspoon",
      "strMeasure7": "6 leaves",
      "strMeasure8": "spinkling",
      "strMeasure9": "",
      "strMeasure10": "",
      "strMeasure11": "",
      "strMeasure12": "",
      "strMeasure13": "",
      "strMeasure14": "",
      "strMeasure15": "",
      "strMeasure16": null,
      "strMeasure17": null,
      "strMeasure18": null,
      "strMeasure19": null,
      "strMeasure20": null,
      "strSource": null,
      "strImageSource": null,
      "strCreativeCommonsConfirmed": null,
      "dateModified": null
    }
  ]
}"""

  private val mealDBJsonRandomJson2 =
      """{
  "meals": [
    {
      "idMeal": "52989",
      "strMeal": "Christmas Pudding Trifle",
      "strDrinkAlternate": null,
      "strCategory": "Dessert",
      "strArea": "British",
      "strInstructions": "Peel the oranges using a sharp knife, ensuring all the pith is removed. Slice as thinly as possible and arrange over a dinner plate. Sprinkle with the demerara sugar followed by the Grand Marnier and set aside.\r\n\r\nCrumble the Christmas pudding into large pieces and scatter over the bottom of a trifle bowl. Lift the oranges onto the pudding in a layer and pour over any juices.\r\n\r\nBeat the mascarpone until smooth, then stir in the custard. Spoon the mixture over the top of the oranges.\r\n\r\nLightly whip the cream and spoon over the custard. Sprinkle with the flaked almonds and grated chocolate. You can make this a day in advance if you like, chill until ready to serve.",
      "strMealThumb": "https://www.themealdb.com/images/media/meals/r33cud1576791081.jpg",
      "strTags": "Christmas",
      "strYoutube": "https://www.youtube.com/watch?v=jRfyNQs5qhU",
      "strIngredient1": "Orange",
      "strIngredient2": "Demerara Sugar",
      "strIngredient3": "Grand Marnier",
      "strIngredient4": "Christmas Pudding",
      "strIngredient5": "Custard",
      "strIngredient6": "Mascarpone",
      "strIngredient7": "Double Cream",
      "strIngredient8": "Flaked Almonds",
      "strIngredient9": "Dark Chocolate",
      "strIngredient10": "",
      "strIngredient11": "",
      "strIngredient12": "",
      "strIngredient13": "",
      "strIngredient14": "",
      "strIngredient15": "",
      "strIngredient16": "",
      "strIngredient17": "",
      "strIngredient18": "",
      "strIngredient19": "",
      "strIngredient20": "",
      "strMeasure1": "3",
      "strMeasure2": "1 tbs",
      "strMeasure3": "2 tbs",
      "strMeasure4": "300g",
      "strMeasure5": "500g",
      "strMeasure6": "250g",
      "strMeasure7": "284ml",
      "strMeasure8": "Handful",
      "strMeasure9": "Grated",
      "strMeasure10": " ",
      "strMeasure11": " ",
      "strMeasure12": " ",
      "strMeasure13": " ",
      "strMeasure14": " ",
      "strMeasure15": " ",
      "strMeasure16": " ",
      "strMeasure17": " ",
      "strMeasure18": " ",
      "strMeasure19": " ",
      "strMeasure20": " ",
      "strSource": "https://www.bbcgoodfood.com/recipes/1826685/christmas-pudding-trifle",
      "strImageSource": null,
      "strCreativeCommonsConfirmed": null,
      "dateModified": null
    }
  ]
}"""

  private val mealDBJsonRandomJson3 =
      """{
  "meals": [
    {
      "idMeal": "52774",
      "strMeal": "Pad See Ew",
      "strDrinkAlternate": null,
      "strCategory": "Chicken",
      "strArea": "Thai",
      "strInstructions": "Mix Sauce in small bowl.\r\nMince garlic into wok with oil. Place over high heat, when hot, add chicken and Chinese broccoli stems, cook until chicken is light golden.\r\nPush to the side of the wok, crack egg in and scramble. Don't worry if it sticks to the bottom of the wok - it will char and which adds authentic flavour.\r\nAdd noodles, Chinese broccoli leaves and sauce. Gently mix together until the noodles are stained dark and leaves are wilted. Serve immediately!",
      "strMealThumb": "https://www.themealdb.com/images/media/meals/uuuspp1468263334.jpg",
      "strTags": "Pasta",
      "strYoutube": "https://www.youtube.com/watch?v=Ohy1DELF4is",
      "strIngredient1": "rice stick noodles",
      "strIngredient2": "dark soy sauce",
      "strIngredient3": "oyster sauce",
      "strIngredient4": "soy sauce",
      "strIngredient5": "white vinegar",
      "strIngredient6": "sugar",
      "strIngredient7": "water",
      "strIngredient8": "peanut oil",
      "strIngredient9": "garlic",
      "strIngredient10": "Chicken",
      "strIngredient11": "Egg",
      "strIngredient12": "Chinese broccoli",
      "strIngredient13": "",
      "strIngredient14": "",
      "strIngredient15": "",
      "strIngredient16": null,
      "strIngredient17": null,
      "strIngredient18": null,
      "strIngredient19": null,
      "strIngredient20": null,
      "strMeasure1": "6oz/180g",
      "strMeasure2": "2 tbsp",
      "strMeasure3": "2 tbsp",
      "strMeasure4": "2 tsp",
      "strMeasure5": "2 tsp",
      "strMeasure6": "2 tsp",
      "strMeasure7": "2 tbsp",
      "strMeasure8": "2 tbsp",
      "strMeasure9": "2 cloves",
      "strMeasure10": "1 cup",
      "strMeasure11": "1",
      "strMeasure12": "4 cups",
      "strMeasure13": "",
      "strMeasure14": "",
      "strMeasure15": "",
      "strMeasure16": null,
      "strMeasure17": null,
      "strMeasure18": null,
      "strMeasure19": null,
      "strMeasure20": null,
      "strSource": null,
      "strImageSource": null,
      "strCreativeCommonsConfirmed": null,
      "dateModified": null
    }
  ]
}"""

  private val mealDBInvalidJSON = """{
            |
        """

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    mockHttpClient = mock(OkHttpClient::class.java)
    mockCall = mock(Call::class.java)
    mockResponseBody = mock(ResponseBody::class.java)

    response =
        Response.Builder()
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .request(Request.Builder().url("http://localhost").build())
            .body(mockResponseBody)
            .build()

    mealDBRecipesRepository = MealDBRecipesRepository(mockHttpClient)
  }

  @Test
  fun `throws IllegalArgumentException when nbOfElements is greater than 5`() {
    var searchRecipe: List<Recipe>? = emptyList()
    var searchException: Exception? = null
    try {
      mealDBRecipesRepository.random(
          nbOfElements = MAXIMUM_RECIPES_TO_FETCH_MEAL_DB + 1,
          onSuccess = { recipe -> searchRecipe = recipe },
          onFailure = { exception -> searchException = exception })
    } catch (e: Exception) {
      assert(e is IllegalArgumentException)
      assert(e.message == "Please limit the number of recipes to 5")
    }
  }

  @Test
  fun `test random will return random meal`() {
    val callbackCapture: ArgumentCaptor<Callback> = ArgumentCaptor.forClass(Callback::class.java)
    val requestCapture: ArgumentCaptor<Request> = ArgumentCaptor.forClass(Request::class.java)

    `when`(mockHttpClient.newCall(capture(requestCapture))).thenReturn(mockCall)
    doNothing().`when`(mockCall).enqueue(capture(callbackCapture))

    var searchRecipe: List<Recipe>? = emptyList()
    var searchException: Exception? = null

    mealDBRecipesRepository.random(
        nbOfElements = 1,
        onSuccess = { recipe -> searchRecipe = recipe },
        onFailure = { exception -> searchException = exception })

    val searchCallBack = callbackCapture.value

    `when`(mockResponseBody.string()).thenReturn(mealDBJsonRandomJson1)

    searchCallBack.onResponse(mockCall, response)

    assertNull(searchException)
    assertNotNull(searchRecipe)
    assert(searchRecipe?.get(0)?.uid == "52771")
    assert(searchRecipe?.get(0)?.name == "Spicy Arrabiata Penne")
    assert(searchRecipe?.get(0)?.category == "Vegetarian")
    assert(searchRecipe?.get(0)?.origin == "Italian")
    assert(
        searchRecipe?.get(0)?.instructions ==
            "Bring a large pot of water to a boil. Add kosher salt to the boiling water, then add the pasta. Cook according to the package instructions, about 9 minutes.\r\nIn a large skillet over medium-high heat, add the olive oil and heat until the oil starts to shimmer. Add the garlic and cook, stirring, until fragrant, 1 to 2 minutes. Add the chopped tomatoes, red chile flakes, Italian seasoning and salt and pepper to taste. Bring to a boil and cook for 5 minutes. Remove from the heat and add the chopped basil.\r\nDrain the pasta and add it to the sauce. Garnish with Parmigiano-Reggiano flakes and more basil and serve warm.")
    assert(
        searchRecipe?.get(0)?.strMealThumbUrl ==
            "https://www.themealdb.com/images/media/meals/ustsqw1468250014.jpg")
    assert(
        searchRecipe?.get(0)?.ingredientsAndMeasurements ==
            listOf(
                Pair("penne rigate", "1 pound"),
                Pair("olive oil", "1/4 cup"),
                Pair("garlic", "3 cloves"),
                Pair("chopped tomatoes", "1 tin "),
                Pair("red chilli flakes", "1/2 teaspoon"),
                Pair("italian seasoning", "1/2 teaspoon"),
                Pair("basil", "6 leaves"),
                Pair("Parmigiano-Reggiano", "spinkling")))
  }

  @Test
  fun `test random will return random multiple meals`() {
    val callbackCapture: ArgumentCaptor<Callback> = ArgumentCaptor.forClass(Callback::class.java)
    val requestCapture: ArgumentCaptor<Request> = ArgumentCaptor.forClass(Request::class.java)

    `when`(mockHttpClient.newCall(capture(requestCapture))).thenReturn(mockCall)
    doNothing().`when`(mockCall).enqueue(capture(callbackCapture))

    var searchRecipe: List<Recipe>? = mutableListOf()
    var searchException: Exception? = null

    mealDBRecipesRepository.random(
        nbOfElements = 3,
        onSuccess = { recipe -> searchRecipe = recipe },
        onFailure = { exception -> searchException = exception })

    val searchCallBack = callbackCapture.value

    `when`(mockResponseBody.string()).thenReturn(mealDBJsonRandomJson1)

    searchCallBack.onResponse(mockCall, response)

    `when`(mockResponseBody.string()).thenReturn(mealDBJsonRandomJson2)

    searchCallBack.onResponse(mockCall, response)

    `when`(mockResponseBody.string()).thenReturn(mealDBJsonRandomJson3)

    searchCallBack.onResponse(mockCall, response)

    assertNull(searchException)
    assert(searchRecipe?.size == 3)
    assertNotNull(searchRecipe)
    assert(searchRecipe?.get(0)?.uid == "52771")
    assert(searchRecipe?.get(0)?.name == "Spicy Arrabiata Penne")
    assert(searchRecipe?.get(0)?.category == "Vegetarian")
    assert(searchRecipe?.get(0)?.origin == "Italian")
    assert(
        searchRecipe?.get(0)?.instructions ==
            "Bring a large pot of water to a boil. Add kosher salt to the boiling water, then add the pasta. Cook according to the package instructions, about 9 minutes.\r\nIn a large skillet over medium-high heat, add the olive oil and heat until the oil starts to shimmer. Add the garlic and cook, stirring, until fragrant, 1 to 2 minutes. Add the chopped tomatoes, red chile flakes, Italian seasoning and salt and pepper to taste. Bring to a boil and cook for 5 minutes. Remove from the heat and add the chopped basil.\r\nDrain the pasta and add it to the sauce. Garnish with Parmigiano-Reggiano flakes and more basil and serve warm.")
    assert(
        searchRecipe?.get(0)?.strMealThumbUrl ==
            "https://www.themealdb.com/images/media/meals/ustsqw1468250014.jpg")

    assert(
        searchRecipe?.get(0)?.ingredientsAndMeasurements ==
            listOf(
                Pair("penne rigate", "1 pound"),
                Pair("olive oil", "1/4 cup"),
                Pair("garlic", "3 cloves"),
                Pair("chopped tomatoes", "1 tin "),
                Pair("red chilli flakes", "1/2 teaspoon"),
                Pair("italian seasoning", "1/2 teaspoon"),
                Pair("basil", "6 leaves"),
                Pair("Parmigiano-Reggiano", "spinkling")))

    assert(searchRecipe?.get(1)?.uid == "52989")
    assert(searchRecipe?.get(1)?.name == "Christmas Pudding Trifle")
    assert(searchRecipe?.get(1)?.category == "Dessert")
    assert(searchRecipe?.get(1)?.origin == "British")
    assert(
        searchRecipe?.get(1)?.instructions ==
            "Peel the oranges using a sharp knife, ensuring all the pith is removed. Slice as thinly as possible and arrange over a dinner plate. Sprinkle with the demerara sugar followed by the Grand Marnier and set aside.\r\n\r\nCrumble the Christmas pudding into large pieces and scatter over the bottom of a trifle bowl. Lift the oranges onto the pudding in a layer and pour over any juices.\r\n\r\nBeat the mascarpone until smooth, then stir in the custard. Spoon the mixture over the top of the oranges.\r\n\r\nLightly whip the cream and spoon over the custard. Sprinkle with the flaked almonds and grated chocolate. You can make this a day in advance if you like, chill until ready to serve.")
    assert(
        searchRecipe?.get(1)?.strMealThumbUrl ==
            "https://www.themealdb.com/images/media/meals/r33cud1576791081.jpg")
    assert(
        searchRecipe?.get(1)?.ingredientsAndMeasurements ==
            listOf(
                Pair("Orange", "3"),
                Pair("Demerara Sugar", "1 tbs"),
                Pair("Grand Marnier", "2 tbs"),
                Pair("Christmas Pudding", "300g"),
                Pair("Custard", "500g"),
                Pair("Mascarpone", "250g"),
                Pair("Double Cream", "284ml"),
                Pair("Flaked Almonds", "Handful"),
                Pair("Dark Chocolate", "Grated")))

    assert(searchRecipe?.get(2)?.uid == "52774")
    assert(searchRecipe?.get(2)?.name == "Pad See Ew")
    assert(searchRecipe?.get(2)?.category == "Chicken")
    assert(searchRecipe?.get(2)?.origin == "Thai")
    assert(
        searchRecipe?.get(2)?.instructions ==
            "Mix Sauce in small bowl.\r\nMince garlic into wok with oil. Place over high heat, when hot, add chicken and Chinese broccoli stems, cook until chicken is light golden.\r\nPush to the side of the wok, crack egg in and scramble. Don't worry if it sticks to the bottom of the wok - it will char and which adds authentic flavour.\r\nAdd noodles, Chinese broccoli leaves and sauce. Gently mix together until the noodles are stained dark and leaves are wilted. Serve immediately!")
    assert(
        searchRecipe?.get(2)?.strMealThumbUrl ==
            "https://www.themealdb.com/images/media/meals/uuuspp1468263334.jpg")
    assert(
        searchRecipe?.get(2)?.ingredientsAndMeasurements ==
            listOf(
                Pair("rice stick noodles", "6oz/180g"),
                Pair("dark soy sauce", "2 tbsp"),
                Pair("oyster sauce", "2 tbsp"),
                Pair("soy sauce", "2 tsp"),
                Pair("white vinegar", "2 tsp"),
                Pair("sugar", "2 tsp"),
                Pair("water", "2 tbsp"),
                Pair("peanut oil", "2 tbsp"),
                Pair("garlic", "2 cloves"),
                Pair("Chicken", "1 cup"),
                Pair("Egg", "1"),
                Pair("Chinese broccoli", "4 cups")))
  }

  @Test
  fun `test random will return random meal with invalid JSON`() {
    val callbackCapture: ArgumentCaptor<Callback> = ArgumentCaptor.forClass(Callback::class.java)
    val requestCapture: ArgumentCaptor<Request> = ArgumentCaptor.forClass(Request::class.java)

    `when`(mockHttpClient.newCall(capture(requestCapture))).thenReturn(mockCall)
    doNothing().`when`(mockCall).enqueue(capture(callbackCapture))

    var searchRecipe: List<Recipe>? = emptyList()
    var searchException: Exception? = null

    mealDBRecipesRepository.random(
        nbOfElements = 1,
        onSuccess = { recipe -> searchRecipe = recipe },
        onFailure = { exception -> searchException = exception })

    val searchCallBack = callbackCapture.value

    `when`(mockResponseBody.string()).thenReturn(mealDBInvalidJSON)

    searchCallBack.onResponse(mockCall, response)

    assertNotNull(searchException)
    assertNotNull(searchRecipe)
  }

  @Test
  fun `test the search function searches correctly`() {
    val callbackCapture: ArgumentCaptor<Callback> = ArgumentCaptor.forClass(Callback::class.java)
    val requestCapture: ArgumentCaptor<Request> = ArgumentCaptor.forClass(Request::class.java)

    `when`(mockHttpClient.newCall(capture(requestCapture))).thenReturn(mockCall)
    doNothing().`when`(mockCall).enqueue(capture(callbackCapture))

    var searchRecipe: Recipe? = null
    var searchException: Exception? = null

    mealDBRecipesRepository.search(
        mealID = "52771",
        onSuccess = { recipe -> searchRecipe = recipe },
        onFailure = { exception -> searchException = exception })

    val searchCallBack = callbackCapture.value

    `when`(mockResponseBody.string()).thenReturn(mealDBJsonRandomJson1)

    searchCallBack.onResponse(mockCall, response)

    assertNull(searchException)
    assertNotNull(searchRecipe)
    assert(searchRecipe?.uid == "52771")
    assert(searchRecipe?.name == "Spicy Arrabiata Penne")
    assert(searchRecipe?.category == "Vegetarian")
    assert(searchRecipe?.origin == "Italian")
    assert(
        searchRecipe?.instructions ==
            "Bring a large pot of water to a boil. Add kosher salt to the boiling water, then add the pasta. Cook according to the package instructions, about 9 minutes.\r\nIn a large skillet over medium-high heat, add the olive oil and heat until the oil starts to shimmer. Add the garlic and cook, stirring, until fragrant, 1 to 2 minutes. Add the chopped tomatoes, red chile flakes, Italian seasoning and salt and pepper to taste. Bring to a boil and cook for 5 minutes. Remove from the heat and add the chopped basil.\r\nDrain the pasta and add it to the sauce. Garnish with Parmigiano-Reggiano flakes and more basil and serve warm.")
    assert(
        searchRecipe?.strMealThumbUrl ==
            "https://www.themealdb.com/images/media/meals/ustsqw1468250014.jpg")

    assert(
        searchRecipe?.ingredientsAndMeasurements ==
            listOf(
                Pair("penne rigate", "1 pound"),
                Pair("olive oil", "1/4 cup"),
                Pair("garlic", "3 cloves"),
                Pair("chopped tomatoes", "1 tin "),
                Pair("red chilli flakes", "1/2 teaspoon"),
                Pair("italian seasoning", "1/2 teaspoon"),
                Pair("basil", "6 leaves"),
                Pair("Parmigiano-Reggiano", "spinkling")))
  }

  @Test
  fun `test the search function searches correctly with invalid JSON`() {
    val callbackCapture: ArgumentCaptor<Callback> = ArgumentCaptor.forClass(Callback::class.java)
    val requestCapture: ArgumentCaptor<Request> = ArgumentCaptor.forClass(Request::class.java)

    `when`(mockHttpClient.newCall(capture(requestCapture))).thenReturn(mockCall)
    doNothing().`when`(mockCall).enqueue(capture(callbackCapture))

    var searchRecipe: Recipe? = null
    var searchException: Exception? = null

    mealDBRecipesRepository.search(
        mealID = "52771",
        onSuccess = { recipe -> searchRecipe = recipe },
        onFailure = { exception -> searchException = exception })

    val searchCallBack = callbackCapture.value

    `when`(mockResponseBody.string()).thenReturn(mealDBInvalidJSON)

    searchCallBack.onResponse(mockCall, response)

    assertNotNull(searchException)
    assertNull(searchRecipe)
  }

  @Test
  fun `test the categories list function`() {
    val callbackCapture: ArgumentCaptor<Callback> = ArgumentCaptor.forClass(Callback::class.java)
    val requestCapture: ArgumentCaptor<Request> = ArgumentCaptor.forClass(Request::class.java)

    `when`(mockHttpClient.newCall(capture(requestCapture))).thenReturn(mockCall)
    doNothing().`when`(mockCall).enqueue(capture(callbackCapture))

    var searchCategories: List<String>? = null
    var searchException: Exception? = null

    mealDBRecipesRepository.listCategories(
        onSuccess = { categories -> searchCategories = categories },
        onFailure = { exception -> searchException = exception })

    val searchCallBack = callbackCapture.value

    `when`(mockResponseBody.string())
        .thenReturn(
            """{
                "categories": [
                    {
                        "strCategory": "Beef"
                    },
                    {
                        "strCategory": "Chicken"
                    },
                    {
                        "strCategory": "Dessert"
                    }
                ]
            }""")

    searchCallBack.onResponse(mockCall, response)

    assertNull(searchException)
    assertNotNull(searchCategories)
    assert(searchCategories?.size == 3)
    assert(searchCategories?.get(0) == "Beef")
    assert(searchCategories?.get(1) == "Chicken")
    assert(searchCategories?.get(2) == "Dessert")
  }

  @Test
  fun `test listCategory on failure`() {
    val callbackCapture: ArgumentCaptor<Callback> = ArgumentCaptor.forClass(Callback::class.java)
    val requestCapture: ArgumentCaptor<Request> = ArgumentCaptor.forClass(Request::class.java)

    `when`(mockHttpClient.newCall(capture(requestCapture))).thenReturn(mockCall)
    doNothing().`when`(mockCall).enqueue(capture(callbackCapture))

    var searchCategories: List<String>? = null
    var searchException: Exception? = null

    mealDBRecipesRepository.listCategories(
        onSuccess = { categories -> searchCategories = categories },
        onFailure = { exception -> searchException = exception })

    val searchCallBack = callbackCapture.value

    `when`(mockResponseBody.string()).thenReturn("")

    searchCallBack.onResponse(mockCall, response)

    assertNotNull(searchException)
    assertNull(searchCategories)
  }

  @Test
  fun `test the searchByCategory function correctly fetches recipes by category`() {
    val callbackCapture: ArgumentCaptor<Callback> = ArgumentCaptor.forClass(Callback::class.java)
    val requestCapture: ArgumentCaptor<Request> = ArgumentCaptor.forClass(Request::class.java)

    `when`(mockHttpClient.newCall(capture(requestCapture))).thenReturn(mockCall)
    doNothing().`when`(mockCall).enqueue(capture(callbackCapture))

    var searchRecipes: List<Recipe>? = null
    var searchException: Exception? = null

    // Call the function to test
    mealDBRecipesRepository.searchByCategory(
        category = "Beef",
        onSuccess = { recipes -> searchRecipes = recipes },
        onFailure = { exception -> searchException = exception })

    // First response: Return a list of thumbnails from searchByCategory
    val searchCategoryCallBack = callbackCapture.value
    `when`(mockResponseBody.string())
        .thenReturn(
            """{"meals":[{"strMeal":"Baked salmon with fennel & tomatoes","strMealThumb":"https:\/\/www.themealdb.com\/images\/media\/meals\/1548772327.jpg","idMeal":"52771"}]}""")
    searchCategoryCallBack.onResponse(mockCall, response)

    // Simulate individual search call to fetch detailed recipe info
    val searchDetailCallBack = callbackCapture.value
    `when`(mockResponseBody.string()).thenReturn(mealDBJsonRandomJson1)
    searchDetailCallBack.onResponse(mockCall, response)

    // Assertions
    assertNull(searchException)
    assertNotNull(searchRecipes)
    assert(searchRecipes?.size == 1)
    assert(searchRecipes?.get(0)?.uid == "52771")
    assert(searchRecipes?.get(0)?.name == "Spicy Arrabiata Penne")
    assert(searchRecipes?.get(0)?.category == "Vegetarian")
    assert(searchRecipes?.get(0)?.origin == "Italian")
    assert(
        searchRecipes?.get(0)?.instructions ==
            "Bring a large pot of water to a boil. Add kosher salt to the boiling water, then add the pasta. Cook according to the package instructions, about 9 minutes.\r\nIn a large skillet over medium-high heat, add the olive oil and heat until the oil starts to shimmer. Add the garlic and cook, stirring, until fragrant, 1 to 2 minutes. Add the chopped tomatoes, red chile flakes, Italian seasoning and salt and pepper to taste. Bring to a boil and cook for 5 minutes. Remove from the heat and add the chopped basil.\r\nDrain the pasta and add it to the sauce. Garnish with Parmigiano-Reggiano flakes and more basil and serve warm.")
    assert(
        searchRecipes?.get(0)?.strMealThumbUrl ==
            "https://www.themealdb.com/images/media/meals/ustsqw1468250014.jpg")
  }

  @Test
  fun unsupportedMethodsThrowErrors() {
    // check that it throws an error when called
    try {
      mealDBRecipesRepository.updateRecipe(
          recipe =
              Recipe(
                  name = "Spicy Arrabiata Penne",
                  category = "Vegetarian",
                  instructions = "alal",
                  strMealThumbUrl = "someurl",
                  origin = "Italian",
                  uid = "52771",
                  ingredientsAndMeasurements =
                      listOf(
                          Pair("penne rigate", "1 pound"),
                          Pair("olive oil", "1/4 cup"),
                          Pair("garlic", "3 cloves"),
                          Pair("chopped tomatoes", "1 tin "),
                          Pair("red chilli flakes", "1/2 teaspoon"),
                          Pair("italian seasoning", "1/2 teaspoon"),
                          Pair("basil", "6 leaves"),
                          Pair("Parmigiano-Reggiano", "spinkling"))),
          onSuccess = {},
          onFailure = {})
    } catch (e: Exception) {
      assert(e is UnsupportedOperationException)
    }

    try {
      mealDBRecipesRepository.deleteRecipe(idMeal = "1", onSuccess = {}, onFailure = {})
    } catch (e: Exception) {
      assert(e is UnsupportedOperationException)
    }

    try {
      mealDBRecipesRepository.addRecipe(
          recipe =
              Recipe(
                  name = "Spicy Arrabiata Penne",
                  category = "Vegetarian",
                  instructions = "alal",
                  strMealThumbUrl = "someurl",
                  origin = "Italian",
                  uid = "52771",
                  ingredientsAndMeasurements =
                      listOf(
                          Pair("penne rigate", "1 pound"),
                          Pair("olive oil", "1/4 cup"),
                          Pair("garlic", "3 cloves"),
                          Pair("chopped tomatoes", "1 tin "),
                          Pair("red chilli flakes", "1/2 teaspoon"),
                          Pair("italian seasoning", "1/2 teaspoon"),
                          Pair("basil", "6 leaves"),
                          Pair("Parmigiano-Reggiano", "spinkling"))),
          onSuccess = {},
          onFailure = {})
    } catch (e: Exception) {
      assert(e is UnsupportedOperationException)
    }
  }
}
