package com.android.sample.model.ingredient

import com.android.sample.model.image.ImageRepositoryFirebase
import com.google.android.gms.tasks.OnCompleteListener
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OpenFoodFactsIngredientRepositoryTest {

  @Mock private lateinit var mockFirebaseStorage: FirebaseStorage
  @Mock private lateinit var mockStorageRef: StorageReference
  @Mock private lateinit var mockImageRef: StorageReference
  @Mock private lateinit var mockUpload: UploadTask

  @Captor
  private lateinit var onCompleteUploadTaskListenerCaptor:
      ArgumentCaptor<OnCompleteListener<UploadTask.TaskSnapshot>>

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
                        "_id": 1234567890,
                        "product_name": "Ingredient 1",
                        "brands": "Brand 1",
                        "categories": "",
                        "quantity": "",
                        "image_front_url": "",
                        "image_front_thumb_url": "",
                        "image_front_small_url": ""
                    },
                    {
                        "_id": 9876543210,
                        "product_name": "Ingredient 2",
                        "brands": "Brand 2",
                        "categories": "",
                        "quantity": "",
                        "image_front_url": "",
                        "image_front_thumb_url": "",
                        "image_front_small_url": ""
                    },
                    {
                        "_id": 1357924680,
                        "product_name": "Ingredient 3",
                        "brands": "Brand 3",
                        "categories": "",
                        "quantity": "",
                        "image_front_url": "",
                        "image_front_thumb_url": "",
                        "image_front_small_url": ""
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

  private val openFoodFactsJsonNormalImageGetFoundJson =
      """
            {
                "code": "1234567890",
                "product": {
                    "_id": 1234567890,
                    "product_name": "apple",
                    "brands": "Brand 1",
                    "categories": "",
                    "quantity": "",
                    "image_front_url": "https://images.openfoodfacts.net/images/products/544/900/021/4911/front_fr.224.200.jpg",
                    "image_front_thumb_url": "",
                    "image_front_small_url": ""
                },
                "status": 1,
                "status_verbose": "product found"
            }
            """

  private val openFoodFactsJsonThumbnailImageGetFoundJson =
      """
            {
                "code": "1234567890",
                "product": {
                    "_id": 1234567890,
                    "product_name": "apple",
                    "brands": "Brand 1",
                    "categories": "",
                    "quantity": "",
                    "image_front_url": "",
                    "image_front_thumb_url": "https://images.openfoodfacts.net/images/products/544/900/021/4911/front_fr.224.100.jpg",
                    "image_front_small_url": ""
                },
                "status": 1,
                "status_verbose": "product found"
            }
            """

  private val openFoodFactsJsonSmallImageGetFoundJson =
      """
            {
                "code": "1234567890",
                "product": {
                    "_id": 1234567890,
                    "product_name": "apple",
                    "brands": "Brand 1",
                    "categories": "",
                    "quantity": "",
                    "image_front_url": "",
                    "image_front_thumb_url": "",
                    "image_front_small_url": "https://images.openfoodfacts.net/images/products/544/900/021/4911/front_fr.224.200.jpg"
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

    openFoodFactsIngredientRepository =
        OpenFoodFactsIngredientRepository(mockHttpClient, imageStorage)
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

    `when`(mockResponseBody.string()).thenReturn(openFoodFactsJsonNormalImageGetFoundJson)
    `when`(mockImageRef.putStream(any())).thenReturn(mockUpload)
    `when`(mockUpload.isSuccessful).thenReturn(true)

    searchCallBack.onResponse(mockCall, response)

    verify(mockUpload).addOnCompleteListener(onCompleteUploadTaskListenerCaptor.capture())

    assertNull(getException)
    assertNotNull(getIngredient)
    assert(getIngredient?.name == "apple")
    assert(getIngredient?.barCode == 1234567890L)
  }

  @Test
  fun `test getting retrieves the right ingredient thumbnail image`() {
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

    `when`(mockResponseBody.string()).thenReturn(openFoodFactsJsonThumbnailImageGetFoundJson)
    `when`(mockImageRef.putStream(any())).thenReturn(mockUpload)
    `when`(mockUpload.isSuccessful).thenReturn(true)

    searchCallBack.onResponse(mockCall, response)

    verify(mockUpload).addOnCompleteListener(onCompleteUploadTaskListenerCaptor.capture())

    assertNull(getException)
    assertNotNull(getIngredient)
    assert(getIngredient?.name == "apple")
    assert(getIngredient?.barCode == 1234567890L)
  }

  @Test
  fun `test getting retrieves the right ingredient small image`() {
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

    `when`(mockResponseBody.string()).thenReturn(openFoodFactsJsonSmallImageGetFoundJson)
    `when`(mockImageRef.putStream(any())).thenReturn(mockUpload)
    `when`(mockUpload.isSuccessful).thenReturn(true)

    searchCallBack.onResponse(mockCall, response)

    verify(mockUpload).addOnCompleteListener(onCompleteUploadTaskListenerCaptor.capture())

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
