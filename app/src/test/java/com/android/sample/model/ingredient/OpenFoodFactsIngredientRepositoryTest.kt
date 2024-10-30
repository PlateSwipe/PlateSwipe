package com.android.sample.model.ingredient

import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONException
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.capture
import org.mockito.kotlin.doNothing

class OpenFoodFactsIngredientRepositoryTest {

  private lateinit var mockHttpClient: OkHttpClient
  private lateinit var mockCall: Call
  private lateinit var response: Response
  private lateinit var mockResponseBody: ResponseBody

  private lateinit var openFoodFactsIngredientRepository: OpenFoodFactsIngredientRepository

  private val openFoodFactsJsonSearchJson =
      """
            {
                "count": 3,
                "page": 1,
                "page_count": 1,
                "page_size": 50,    
                "products": [
                    {
                        "_id": 1234567890,
                        "product_name": "Ingredient 1",
                        "brands": "Brand 1",
                        "selected_images": {
                        "front": {
                          "display": {
                            "fr": "https://images.openfoodfacts.net/images/products/761/006/200/1605/front_fr.16.400.jpg"
                          },
                          "small": {
                            "fr": "https://images.openfoodfacts.net/images/products/761/006/200/1605/front_fr.16.200.jpg"
                          },
                          "thumb": {
                            "fr": "https://images.openfoodfacts.net/images/products/761/006/200/1605/front_fr.16.100.jpg"
                          }
                        },
                    }
                        
                    },
                    {
                        "_id": 9876543210,
                        "product_name": "Ingredient 2",
                        "brands": "Brand 2",
                        "selected_images": {
                        "front": {
                          "display": {
                            "fr": "https://images.openfoodfacts.net/images/products/761/006/200/1605/front_fr.16.400.jpg"
                          },
                          "small": {
                            "fr": "https://images.openfoodfacts.net/images/products/761/006/200/1605/front_fr.16.200.jpg"
                          },
                          "thumb": {
                            "fr": "https://images.openfoodfacts.net/images/products/761/006/200/1605/front_fr.16.100.jpg"
                          }
                        },
                    }
                    },
                    {
                        "_id": 1357924680,
                        "product_name": "Ingredient 3",
                        "brands": "Brand 3",
                        "selected_images": {
                        "front": {
                          "display": {},
                          "small": {},
                          "thumb": {}
                        },
                    }
                    },
                ],
                "skip": 0
            }
            """

  private val openFoodFactsJsonSearchEmptyJson =
      """
            {
                "count": 0,
                "page": 1,
                "page_count": 0,
                "page_size": 50,
                "products": [],
                "skip": 0
            }
            """

  private val openFoodFactsJsonGetFoundJson =
      """
            {
                "code": "1234567890",
                "product": {
                    "_id": 1234567890,
                    "product_name": "apple",
                    "brands": "Brand 1",
                    "selected_images": {
                        "front": {
                          "display": {
                            "fr": "https://images.openfoodfacts.net/images/products/761/006/200/1605/front_fr.16.400.jpg"
                          },
                          "small": {
                            "fr": "https://images.openfoodfacts.net/images/products/761/006/200/1605/front_fr.16.200.jpg"
                          },
                          "thumb": {
                            "fr": "https://images.openfoodfacts.net/images/products/761/006/200/1605/front_fr.16.100.jpg"
                          }
                        },
                    }
                },
                "status": 1,
                "status_verbose": "product found"
            }
            """

  private val openFoodFactsJsonGetNotFoundJson =
      """
            {
                "code": "0",
                "status": 0,
                "status_verbose": "product not found"
            }
            """

  private val openFoodFactsInvalidJson =
      """
            {
                "code": "0",
            }
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

    openFoodFactsIngredientRepository = OpenFoodFactsIngredientRepository(mockHttpClient)
  }

  @Test
  fun `test searching will return the right ingredients`() {

    val callbackCapture: ArgumentCaptor<Callback> = ArgumentCaptor.forClass(Callback::class.java)
    val requestCapture: ArgumentCaptor<Request> = ArgumentCaptor.forClass(Request::class.java)

    `when`(mockHttpClient.newCall(capture(requestCapture))).thenReturn(mockCall)
    doNothing().`when`(mockCall).enqueue(capture(callbackCapture))

    var searchIngredients: List<Ingredient>? = emptyList()
    var searchException: Exception? = null

    openFoodFactsIngredientRepository.search(
        "Ingredient 1",
        onSuccess = { ingredients -> searchIngredients = ingredients },
        onFailure = { exception -> searchException = exception })

    val searchCallBack = callbackCapture.value

    `when`(mockResponseBody.string()).thenReturn(openFoodFactsJsonSearchJson)

    searchCallBack.onResponse(mockCall, response)

    assertNull(searchException)
    assertNotNull(searchIngredients)

    assert(searchIngredients?.get(0)?.name == "Ingredient 1")
    assert(searchIngredients?.get(0)?.barCode == 1234567890L)
    assert(searchIngredients?.get(0)?.brands == "Brand 1")
    println(searchIngredients?.get(0)?.selectedImages?.front?.display)
    assert(
        searchIngredients?.get(0)?.selectedImages?.front?.display ==
            "https://images.openfoodfacts.net/images/products/761/006/200/1605/front_fr.16.400.jpg")
    assert(
        searchIngredients?.get(0)?.selectedImages?.front?.small ==
            "https://images.openfoodfacts.net/images/products/761/006/200/1605/front_fr.16.200.jpg")
    assert(
        searchIngredients?.get(0)?.selectedImages?.front?.thumb ==
            "https://images.openfoodfacts.net/images/products/761/006/200/1605/front_fr.16.100.jpg")
    assert(searchIngredients?.get(1)?.name == "Ingredient 2")
    assert(searchIngredients?.get(1)?.barCode == 9876543210L)
    assert(searchIngredients?.get(2)?.name == "Ingredient 3")
    assert(searchIngredients?.get(2)?.barCode == 1357924680L)
  }

  @Test
  fun `test searching limits the retrieved list length to the count given`() {

    val callbackCapture: ArgumentCaptor<Callback> = ArgumentCaptor.forClass(Callback::class.java)
    val requestCapture: ArgumentCaptor<Request> = ArgumentCaptor.forClass(Request::class.java)

    `when`(mockHttpClient.newCall(capture(requestCapture))).thenReturn(mockCall)
    doNothing().`when`(mockCall).enqueue(capture(callbackCapture))

    var searchIngredients: List<Ingredient>? = emptyList()
    var searchException: Exception? = null

    openFoodFactsIngredientRepository.search(
        "Ingredient 1",
        onSuccess = { ingredients -> searchIngredients = ingredients },
        onFailure = { exception -> searchException = exception },
        count = 2)

    val searchCallBack = callbackCapture.value

    `when`(mockResponseBody.string()).thenReturn(openFoodFactsJsonSearchJson)

    searchCallBack.onResponse(mockCall, response)

    assertNull(searchException)
    assertNotNull(searchIngredients)
    assert(searchIngredients?.count() == 2)
  }

  @Test
  fun `test searching gives empty list when nothing found`() {

    val callbackCapture: ArgumentCaptor<Callback> = ArgumentCaptor.forClass(Callback::class.java)
    val requestCapture: ArgumentCaptor<Request> = ArgumentCaptor.forClass(Request::class.java)

    `when`(mockHttpClient.newCall(capture(requestCapture))).thenReturn(mockCall)
    doNothing().`when`(mockCall).enqueue(capture(callbackCapture))

    var searchIngredients: List<Ingredient>? = emptyList()
    var searchException: Exception? = null

    openFoodFactsIngredientRepository.search(
        "Ingredient 1",
        onSuccess = { ingredients -> searchIngredients = ingredients },
        onFailure = { exception -> searchException = exception },
    )

    val searchCallBack = callbackCapture.value

    `when`(mockResponseBody.string()).thenReturn(openFoodFactsJsonSearchEmptyJson)

    searchCallBack.onResponse(mockCall, response)

    assertNull(searchException)
    assertNotNull(searchIngredients)
    assert(searchIngredients?.isEmpty() == true)
  }

  @Test
  fun `test searching gives error on IOException`() {

    val callbackCapture: ArgumentCaptor<Callback> = ArgumentCaptor.forClass(Callback::class.java)
    val requestCapture: ArgumentCaptor<Request> = ArgumentCaptor.forClass(Request::class.java)

    `when`(mockHttpClient.newCall(capture(requestCapture))).thenReturn(mockCall)
    doNothing().`when`(mockCall).enqueue(capture(callbackCapture))

    var searchIngredients: List<Ingredient>? = null
    var searchException: Exception? = null

    openFoodFactsIngredientRepository.search(
        "Ingredient 1",
        onSuccess = { ingredients -> searchIngredients = ingredients },
        onFailure = { exception -> searchException = exception },
    )

    val searchCallBack = callbackCapture.value

    searchCallBack.onFailure(mockCall, IOException())

    assertNotNull(searchException)
    assert(searchException is IOException)
    assertNull(searchIngredients)
  }

  @Test
  fun `test searching gives error on invalid json`() {

    val callbackCapture: ArgumentCaptor<Callback> = ArgumentCaptor.forClass(Callback::class.java)
    val requestCapture: ArgumentCaptor<Request> = ArgumentCaptor.forClass(Request::class.java)

    `when`(mockHttpClient.newCall(capture(requestCapture))).thenReturn(mockCall)
    doNothing().`when`(mockCall).enqueue(capture(callbackCapture))

    var searchIngredients: List<Ingredient>? = emptyList()
    var searchException: Exception? = null

    openFoodFactsIngredientRepository.search(
        "Ingredient 1",
        onSuccess = { ingredients -> searchIngredients = ingredients },
        onFailure = { exception -> searchException = exception },
    )

    val searchCallBack = callbackCapture.value

    `when`(mockResponseBody.string()).thenReturn(openFoodFactsInvalidJson)

    searchCallBack.onResponse(mockCall, response)

    assertNotNull(searchException)
    assertNotNull(searchException is JSONException)
    assertNotNull(searchIngredients)
    assert(searchIngredients?.isEmpty() == true)
  }

  @Test
  fun `test getting retrieves the right ingredient`() {
    val callbackCapture: ArgumentCaptor<Callback> = ArgumentCaptor.forClass(Callback::class.java)
    val requestCapture: ArgumentCaptor<Request> = ArgumentCaptor.forClass(Request::class.java)

    `when`(mockHttpClient.newCall(capture(requestCapture))).thenReturn(mockCall)
    doNothing().`when`(mockCall).enqueue(capture(callbackCapture))

    var getIngredient: Ingredient? = null
    var getException: Exception? = null

    openFoodFactsIngredientRepository.get(
        1234567890L,
        onSuccess = { ingredients -> getIngredient = ingredients },
        onFailure = { exception -> getException = exception })

    val searchCallBack = callbackCapture.value

    `when`(mockResponseBody.string()).thenReturn(openFoodFactsJsonGetFoundJson)

    searchCallBack.onResponse(mockCall, response)

    assertNull(getException)
    assertNotNull(getIngredient)
    assert(getIngredient?.name == "apple")
    assert(getIngredient?.barCode == 1234567890L)
  }

  @Test
  fun `test product not found when getting`() {
    val callbackCapture: ArgumentCaptor<Callback> = ArgumentCaptor.forClass(Callback::class.java)
    val requestCapture: ArgumentCaptor<Request> = ArgumentCaptor.forClass(Request::class.java)

    `when`(mockHttpClient.newCall(capture(requestCapture))).thenReturn(mockCall)
    doNothing().`when`(mockCall).enqueue(capture(callbackCapture))

    var getIngredient: Ingredient? = null
    var getException: Exception? = null

    openFoodFactsIngredientRepository.get(
        1234567890L,
        onSuccess = { ingredients -> getIngredient = ingredients },
        onFailure = { exception -> getException = exception })

    val searchCallBack = callbackCapture.value

    `when`(mockResponseBody.string()).thenReturn(openFoodFactsJsonGetNotFoundJson)

    searchCallBack.onResponse(mockCall, response)

    assertNull(getException)
    assertNull(getIngredient)
  }

  @Test
  fun `test getting returns IOException`() {
    val callbackCapture: ArgumentCaptor<Callback> = ArgumentCaptor.forClass(Callback::class.java)
    val requestCapture: ArgumentCaptor<Request> = ArgumentCaptor.forClass(Request::class.java)

    `when`(mockHttpClient.newCall(capture(requestCapture))).thenReturn(mockCall)
    doNothing().`when`(mockCall).enqueue(capture(callbackCapture))

    var getIngredient: Ingredient? = null
    var getException: Exception? = null

    openFoodFactsIngredientRepository.get(
        1234567890L,
        onSuccess = { ingredients -> getIngredient = ingredients },
        onFailure = { exception -> getException = exception })

    val searchCallBack = callbackCapture.value

    searchCallBack.onFailure(mockCall, IOException())

    assertNotNull(getException)
    assert(getException is IOException)
    assertNull(getIngredient)
  }

  @Test
  fun `test getting returns JsonException`() {
    val callbackCapture: ArgumentCaptor<Callback> = ArgumentCaptor.forClass(Callback::class.java)
    val requestCapture: ArgumentCaptor<Request> = ArgumentCaptor.forClass(Request::class.java)

    `when`(mockHttpClient.newCall(capture(requestCapture))).thenReturn(mockCall)
    doNothing().`when`(mockCall).enqueue(capture(callbackCapture))

    var getIngredient: Ingredient? = null
    var getException: Exception? = null

    openFoodFactsIngredientRepository.get(
        1234567890L,
        onSuccess = { ingredients -> getIngredient = ingredients },
        onFailure = { exception -> getException = exception })

    val searchCallBack = callbackCapture.value
    `when`(mockResponseBody.string()).thenReturn(openFoodFactsInvalidJson)

    searchCallBack.onResponse(mockCall, response)

    assertNotNull(getException)
    assertNotNull(getException is JSONException)
    assertNull(getIngredient)
  }
}
