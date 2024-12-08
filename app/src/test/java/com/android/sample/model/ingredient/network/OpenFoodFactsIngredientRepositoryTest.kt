package com.android.sample.model.ingredient.network

import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.networkData.OpenFoodFactsIngredientRepository
import com.android.sample.resources.C.Tag.PRODUCT_BRAND
import com.android.sample.resources.C.Tag.PRODUCT_CATEGORIES
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_NORMAL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_SMALL_URL
import com.android.sample.resources.C.Tag.PRODUCT_FRONT_IMAGE_THUMBNAIL_URL
import com.android.sample.resources.C.Tag.PRODUCT_ID
import com.android.sample.resources.C.Tag.PRODUCT_NAME
import com.android.sample.resources.C.Tag.PRODUCT_QUANTITY
import com.android.sample.ui.utils.testIngredients
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.IOException
import java.io.InputStream
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.mockito.kotlin.doNothing
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OpenFoodFactsIngredientRepositoryTest {

  @Mock private lateinit var mockFirebaseStorage: FirebaseStorage
  @Mock private lateinit var mockStorageRef: StorageReference
  @Mock private lateinit var mockImageRef: StorageReference
  @Mock private lateinit var mockUpload: UploadTask

  private lateinit var mockHttpClient: OkHttpClient
  private lateinit var imageStorage: ImageRepositoryFirebase
  private lateinit var mockCall: Call
  private lateinit var response: Response
  private lateinit var mockResponseBody: ResponseBody
  private lateinit var mockInputStream: InputStream

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
                        "$PRODUCT_ID": 1234567890,
                        "$PRODUCT_NAME": "Ingredient 1",
                        "$PRODUCT_BRAND": "Brand 1",
                        "$PRODUCT_CATEGORIES": [],
                        "$PRODUCT_QUANTITY": "",
                        "$PRODUCT_FRONT_IMAGE_NORMAL_URL": "ww",
                        "$PRODUCT_FRONT_IMAGE_THUMBNAIL_URL": "ww",
                        "$PRODUCT_FRONT_IMAGE_SMALL_URL": "ww"
                    },
                    {
                        "$PRODUCT_ID": 9876543210,
                        "$PRODUCT_NAME": "Ingredient 2",
                        "$PRODUCT_BRAND": "Brand 2",
                        "$PRODUCT_CATEGORIES": [],
                        "$PRODUCT_QUANTITY": "",
                        "$PRODUCT_FRONT_IMAGE_NORMAL_URL": "ww",
                        "$PRODUCT_FRONT_IMAGE_THUMBNAIL_URL": "ww",
                        "$PRODUCT_FRONT_IMAGE_SMALL_URL": "ww"
                    },
                    {
                        "$PRODUCT_ID": 1357924680,
                        "$PRODUCT_NAME": "Ingredient 3",
                        "$PRODUCT_BRAND": "Brand 3",
                        "$PRODUCT_CATEGORIES": [],
                        "$PRODUCT_QUANTITY": "",
                        "$PRODUCT_FRONT_IMAGE_NORMAL_URL": "ww",
                        "$PRODUCT_FRONT_IMAGE_THUMBNAIL_URL": "ww",
                        "$PRODUCT_FRONT_IMAGE_SMALL_URL": "ww"
                    }
                ],
                "skip": 0
            }
            """

  private val openFoodFactsJsonSingleIngredientJsonWithEnglishProductName =
      """
            {
                "count": 3,
                "page": 1,
                "page_count": 1,
                "page_size": 50,    
                "products": [
                    {
                        "$PRODUCT_ID": 1234567890,
                        "$PRODUCT_NAME": "",
                        "${PRODUCT_NAME}_en": "Ingredient 1",
                        "$PRODUCT_BRAND": "Brand 1",
                        "$PRODUCT_CATEGORIES": [],
                        "$PRODUCT_QUANTITY": "",
                        "$PRODUCT_FRONT_IMAGE_NORMAL_URL": "ww",
                        "$PRODUCT_FRONT_IMAGE_THUMBNAIL_URL": "ww",
                        "$PRODUCT_FRONT_IMAGE_SMALL_URL": "ww"
                    }
                ],
                "skip": 0
            }
            """

  private val openFoodFactsJsonSingleIngredientJsonWithNoProductName =
      """
            {
                "count": 3,
                "page": 1,
                "page_count": 1,
                "page_size": 50,    
                "products": [
                    {
                        "$PRODUCT_ID": 1234567890,
                        "$PRODUCT_NAME": "",
                        "$PRODUCT_BRAND": "Brand 1",
                        "$PRODUCT_CATEGORIES": [],
                        "$PRODUCT_QUANTITY": "",
                        "$PRODUCT_FRONT_IMAGE_NORMAL_URL": "ww",
                        "$PRODUCT_FRONT_IMAGE_THUMBNAIL_URL": "ww",
                        "$PRODUCT_FRONT_IMAGE_SMALL_URL": "ww"
                    }
                ],
                "skip": 0
            }
            """

  private val openFoodFactsJsonSingleIngredientWithCategories =
      """
            {
                "count": 3,
                "page": 1,
                "page_count": 1,
                "page_size": 50,    
                "products": [
                    {
                        "$PRODUCT_ID": 1234567890,
                        "$PRODUCT_NAME": "Ingredient 1",
                        "$PRODUCT_BRAND": "Brand 1",
                        "$PRODUCT_CATEGORIES": [
                            "en:${testIngredients[0].categories[0]}",
                            "en:${testIngredients[0].categories[1]}"
                        ],
                        "$PRODUCT_QUANTITY": "",
                        "$PRODUCT_FRONT_IMAGE_NORMAL_URL": "ww",
                        "$PRODUCT_FRONT_IMAGE_THUMBNAIL_URL": "ww",
                        "$PRODUCT_FRONT_IMAGE_SMALL_URL": "ww"
                    }
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

  private val openFoodFactsJsonImagesGetFoundJson =
      """
            {
                "code": "1234567890",
                "product": {
                    "$PRODUCT_ID": 1234567890,
                    "$PRODUCT_NAME": "apple",
                    "$PRODUCT_BRAND": "Brand 1",
                    "$PRODUCT_CATEGORIES": [],
                    "$PRODUCT_QUANTITY": "",
                    "$PRODUCT_FRONT_IMAGE_NORMAL_URL": "https://images.openfoodfacts.net/images/products/544/900/021/4911/front_fr.224.200.jpg",
                    "$PRODUCT_FRONT_IMAGE_THUMBNAIL_URL": "https://images.openfoodfacts.net/images/products/544/900/021/4911/front_fr.224.200.jpg",
                    "$PRODUCT_FRONT_IMAGE_SMALL_URL": "https://images.openfoodfacts.net/images/products/544/900/021/4911/front_fr.224.200.jpg"
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

    `when`(mockFirebaseStorage.reference).thenReturn(mockStorageRef)
    `when`(mockStorageRef.child(any())).thenReturn(mockImageRef)

    mockHttpClient = mock(OkHttpClient::class.java)
    imageStorage = ImageRepositoryFirebase(mockFirebaseStorage)
    mockCall = mock(Call::class.java)
    mockResponseBody = mock(ResponseBody::class.java)
    mockFirebaseStorage = mock(FirebaseStorage::class.java)
    mockInputStream = mock(InputStream::class.java)

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
    `when`(mockImageRef.putStream(any())).thenReturn(mockUpload)
    `when`(mockUpload.isSuccessful).thenReturn(true)

    searchCallBack.onResponse(mockCall, response)

    assertNull(searchException)
    assertNotNull(searchIngredients)

    assert(searchIngredients?.get(0)?.name == "Ingredient 1")
    assert(searchIngredients?.get(0)?.barCode == 1234567890L)
    assert(searchIngredients?.get(0)?.brands == "Brand 1")
    assert(searchIngredients?.get(1)?.name == "Ingredient 2")
    assert(searchIngredients?.get(1)?.barCode == 9876543210L)
    assert(searchIngredients?.get(2)?.name == "Ingredient 3")
    assert(searchIngredients?.get(2)?.barCode == 1357924680L)
  }

  @Test
  fun `test searching will correctly parse name on english name field`() {

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

    `when`(mockResponseBody.string())
        .thenReturn(openFoodFactsJsonSingleIngredientJsonWithEnglishProductName)
    `when`(mockImageRef.putStream(any())).thenReturn(mockUpload)
    `when`(mockUpload.isSuccessful).thenReturn(true)

    searchCallBack.onResponse(mockCall, response)

    assertNull(searchException)
    assertNotNull(searchIngredients)

    assert(searchIngredients?.get(0)?.name == "Ingredient 1")
  }

  @Test
  fun `test searching will throw error on missing name`() {

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

    `when`(mockResponseBody.string())
        .thenReturn(openFoodFactsJsonSingleIngredientJsonWithNoProductName)
    `when`(mockImageRef.putStream(any())).thenReturn(mockUpload)
    `when`(mockUpload.isSuccessful).thenReturn(true)

    searchCallBack.onResponse(mockCall, response)

    assertNull(searchException)
    assertNotNull(searchIngredients)
    assert(searchIngredients?.isEmpty() == true)
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
  fun `test getting retrieves the right ingredient normal image`() {
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

    `when`(mockResponseBody.string()).thenReturn(openFoodFactsJsonImagesGetFoundJson)
    searchCallBack.onResponse(mockCall, response)

    assertNull(getException)
    assertNotNull(getIngredient)
    assert(getIngredient?.name == "apple")
    assert(getIngredient?.barCode == 1234567890L)
    assert(
        getIngredient?.images?.get(PRODUCT_FRONT_IMAGE_NORMAL_URL) ==
            "https://images.openfoodfacts.net/images/products/544/900/021/4911/front_fr.224.200.jpg")
    assert(
        getIngredient?.images?.get(PRODUCT_FRONT_IMAGE_SMALL_URL) ==
            "https://images.openfoodfacts.net/images/products/544/900/021/4911/front_fr.224.200.jpg")
    assert(
        getIngredient?.images?.get(PRODUCT_FRONT_IMAGE_THUMBNAIL_URL) ==
            "https://images.openfoodfacts.net/images/products/544/900/021/4911/front_fr.224.200.jpg")
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

  @Test
  fun `test getting returns correct categories`() {
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
    `when`(mockResponseBody.string()).thenReturn(openFoodFactsJsonSingleIngredientWithCategories)

    searchCallBack.onResponse(mockCall, response)

    assertNotNull(searchIngredients)
    assertEquals(searchIngredients?.get(0)?.categories, testIngredients[0].categories.take(2))
    assertNull(searchException)
  }
}
