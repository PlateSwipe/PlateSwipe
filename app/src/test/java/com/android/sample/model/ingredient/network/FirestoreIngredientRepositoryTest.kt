package com.android.sample.model.ingredient.network

import com.android.sample.model.ingredient.Ingredient
import com.android.sample.model.ingredient.networkData.FirestoreIngredientRepository
import com.android.sample.ui.utils.testIngredients
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.mockito.kotlin.verify

class FirestoreIngredientRepositoryTest {
  @Mock private lateinit var mockFirebaseFirestore: FirebaseFirestore
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockTaskQuerySnapshot: Task<QuerySnapshot>
  @Mock private lateinit var mockTaskVoid: Task<Void>
  @Mock private lateinit var mockQuerySnapshot: QuerySnapshot
  @Mock private lateinit var mockDocumentSnapshot: QueryDocumentSnapshot
  @Mock private lateinit var mockQuery: Query

  @Captor
  private lateinit var onCompleteListenerQueryCaptor:
      ArgumentCaptor<OnCompleteListener<QuerySnapshot>>
  @Captor
  private lateinit var onCompleteListenerVoidCaptor: ArgumentCaptor<OnCompleteListener<Void>>
  @Captor private lateinit var objectCapture: ArgumentCaptor<Any>

  private lateinit var firestoreIngredientRepository: FirestoreIngredientRepository

  private val ingredient: Ingredient = testIngredients[0]

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)

    `when`(mockFirebaseFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.where(any())).thenReturn(mockQuery)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
    `when`(mockDocumentReference.id).thenReturn(ingredient.uid)
    `when`(mockDocumentReference.set(capture(objectCapture))).thenReturn(mockTaskVoid)
    `when`(mockTaskVoid.addOnCompleteListener(capture(onCompleteListenerVoidCaptor)))
        .thenReturn(mockTaskVoid)
    `when`(mockTaskQuerySnapshot.addOnCompleteListener(capture(onCompleteListenerQueryCaptor)))
        .thenReturn(mockTaskQuerySnapshot)
    `when`(mockQuery.limit(any())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(mockTaskQuerySnapshot)
    `when`(mockTaskQuerySnapshot.isSuccessful).thenReturn(true)
    `when`(mockTaskQuerySnapshot.result).thenReturn(mockQuerySnapshot)
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.id).thenReturn(ingredient.uid)
    `when`(mockDocumentSnapshot.getLong("barCode")).thenReturn(ingredient.barCode)
    `when`(mockDocumentSnapshot.getString("name")).thenReturn(ingredient.name)
    `when`(mockDocumentSnapshot.getString("brands")).thenReturn(ingredient.brands)
    `when`(mockDocumentSnapshot.getString("quantity")).thenReturn(ingredient.quantity)
    `when`(mockDocumentSnapshot.get("categories")).thenReturn(ingredient.categories)
    `when`(mockDocumentSnapshot.get("images")).thenReturn(ingredient.images)

    firestoreIngredientRepository = FirestoreIngredientRepository(mockFirebaseFirestore)
  }

  @Test
  fun testCorrectlySearchIngredient() {

    var resultingIngredient: Ingredient? = null
    var resultingException: Exception? = null

    firestoreIngredientRepository.searchFiltered(
        Filter.equalTo("barCode", ingredient.barCode!!),
        onSuccess = { res -> resultingIngredient = res[0] },
        onFailure = { e -> resultingException = e },
        count = 1)

    onCompleteListenerQueryCaptor.value.onComplete(mockTaskQuerySnapshot)

    assertNotNull(resultingIngredient)
    assertEquals(ingredient, resultingIngredient)

    assertNull(resultingException)
  }

  @Test
  fun testDocumentSnapshotConverterThrowsErrorOnInvalidIngredient() {
    var resultingIngredient: Ingredient? = null
    var resultingException: Exception? = null

    `when`(mockDocumentSnapshot.getString("name")).thenReturn(null)

    firestoreIngredientRepository.get(
        ingredient.barCode!!,
        onSuccess = { res -> resultingIngredient = res },
        onFailure = { e -> resultingException = e })

    verify(mockQuery.get()).addOnCompleteListener(capture(onCompleteListenerQueryCaptor))

    onCompleteListenerQueryCaptor.value.onComplete(mockTaskQuerySnapshot)

    assertNotNull(resultingException)

    assertNull(resultingIngredient)
  }

  @Test
  fun testSearchThrowsExceptionWhenUnsuccessful() {
    var resultingIngredient: Ingredient? = null
    var resultingException: Exception? = null

    `when`(mockTaskQuerySnapshot.isSuccessful).thenReturn(false)
    `when`(mockTaskQuerySnapshot.exception).thenReturn(Exception("Error"))

    firestoreIngredientRepository.searchFiltered(
        Filter.equalTo("barCode", ingredient.barCode!!),
        onSuccess = { res -> resultingIngredient = res[0] },
        onFailure = { e -> resultingException = e },
        count = 1)

    verify(mockQuery.get()).addOnCompleteListener(capture(onCompleteListenerQueryCaptor))

    onCompleteListenerQueryCaptor.value.onComplete(mockTaskQuerySnapshot)

    assertNull(resultingIngredient)
    assertNotNull(resultingException)
  }

  @Test
  fun testSearchByNameReturnsCorrectIngredient() {
    var resultingIngredient: Ingredient? = null
    var resultingException: Exception? = null

    firestoreIngredientRepository.search(
        ingredient.name,
        onSuccess = { res -> resultingIngredient = res[0] },
        onFailure = { e -> resultingException = e },
        count = 1)

    onCompleteListenerQueryCaptor.value.onComplete(mockTaskQuerySnapshot)

    assertNotNull(resultingIngredient)
    assertEquals(ingredient, resultingIngredient)
    assertNull(resultingException)
  }

  @Test
  fun testGetReturnsCorrectIngredient() {
    var resultingIngredient: Ingredient? = null
    var resultingException: Exception? = null

    firestoreIngredientRepository.get(
        ingredient.barCode!!,
        onSuccess = { res -> resultingIngredient = res },
        onFailure = { e -> resultingException = e })

    onCompleteListenerQueryCaptor.value.onComplete(mockTaskQuerySnapshot)

    assertNotNull(resultingIngredient)
    assertEquals(ingredient, resultingIngredient)
    assertNull(resultingException)
  }

  @Test
  fun testAddGivesIdWhenAddingIngredientThatDoesntHaveOne() {
    val nullUidIngredient = ingredient.copy(uid = null)

    firestoreIngredientRepository.add(nullUidIngredient, onSuccess = {}, onFailure = {})

    verify(mockDocumentReference).set(capture(objectCapture))

    val addedIngredient: Ingredient = objectCapture.value as Ingredient

    assertNotNull(addedIngredient.uid)
    assertEquals(ingredient, addedIngredient)
  }

  @Test
  fun testAddCorrectlyAddsIngredient() {
    firestoreIngredientRepository.add(ingredient, onSuccess = {}, onFailure = {})

    verify(mockDocumentReference).set(capture(objectCapture))

    val addedIngredient: Ingredient = objectCapture.value as Ingredient

    assertEquals(ingredient, addedIngredient)
  }

  @Test
  fun testAddThrowsExceptionWhenUnsuccessful() {
    `when`(mockTaskVoid.isSuccessful).thenReturn(false)
    `when`(mockTaskVoid.exception).thenReturn(Exception("Error"))

    var resultingException: Exception? = null

    firestoreIngredientRepository.add(
        ingredient, onSuccess = {}, onFailure = { e -> resultingException = e })

    onCompleteListenerVoidCaptor.value.onComplete(mockTaskVoid)

    assertNotNull(resultingException)
  }
}
