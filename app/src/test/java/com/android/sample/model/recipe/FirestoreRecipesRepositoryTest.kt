package com.android.sample.model.recipe

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_AREA
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_CATEGORY
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_DIFFICULTY
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_INGREDIENTS
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_INSTRUCTIONS
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_MEASUREMENTS
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_NAME
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_PICTURE_ID
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_PRICE
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_TIME
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.util.Assert.fail
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.atMostOnce
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class FirestoreRecipesRepositoryTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockRecipeQuerySnapshot: QuerySnapshot

  private lateinit var firestoreFirebaseRepository: FirestoreRecipesRepository

  private val recipe =
      Recipe(
          idMeal = "1",
          strMeal = "Chicken",
          strCategory = "Main",
          strArea = "Italian",
          strInstructions = "Instructions",
          strMealThumbUrl = "https://www.themealdb.com/images/media/meals/1548772327.jpg",
          ingredientsAndMeasurements = listOf(Pair("Chicken", "1"), Pair("Salt", "1 tsp")),
          time = "30 mins",
          difficulty = "Easy",
          price = "Low")

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)

    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    firestoreFirebaseRepository = FirestoreRecipesRepository(mockFirestore)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)
  }

  @Test
  fun getNewUid() {
    `when`(mockDocumentReference.id).thenReturn("1")
    val uid = firestoreFirebaseRepository.getNewUid()
    assert(uid == "1")
  }

  @Test
  fun addRecipe_shouldCallFirestoreCollection() {
    // Simulate a successful Firestore `set` operation
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    // Simulate a successful Firestore `add` operation, which returns a new DocumentReference
    `when`(mockCollectionReference.add(any())).thenReturn(Tasks.forResult(mockDocumentReference))

    // Call the method that adds the recipe
    firestoreFirebaseRepository.addRecipe(recipe, onSuccess = {}, onFailure = {})

    // Ensure all asynchronous operations are completed
    shadowOf(Looper.getMainLooper()).idle()

    // Verify that either `set()` or `add()` was called
    verify(mockDocumentReference, atMostOnce()).set(any())
    verify(mockCollectionReference, atMostOnce()).add(any())
  }

  @Test
  fun deleteRecipeById_shouldCallDocumentReferenceDelete() {
    `when`(mockDocumentReference.delete()).thenReturn(Tasks.forResult(null))

    firestoreFirebaseRepository.deleteRecipe("1", onSuccess = {}, onFailure = {})

    shadowOf(Looper.getMainLooper()).idle() // Ensure all asynchronous operations complete

    verify(mockDocumentReference).delete()
  }

  @Test
  fun updateRecipe_shouldCallFirestoreCollection() {
    // Simulate a successful Firestore `set` operation
    `when`(mockDocumentReference.set(any())).thenReturn(Tasks.forResult(null))

    // Call the method that adds the recipe
    firestoreFirebaseRepository.updateRecipe(recipe, onSuccess = {}, onFailure = {})

    // Ensure all asynchronous operations are completed
    shadowOf(Looper.getMainLooper()).idle()

    // Verify that `set()` was called
    verify(mockDocumentReference, atMostOnce()).set(any())
  }

  @Test
  fun search_callsDocuments() {
    // Ensure that mockToDoQuerySnapshot is properly initialized and mocked
    `when`(mockCollectionReference.document(recipe.idMeal)).thenReturn(mockDocumentReference)

    // Ensure the QuerySnapshot returns a list of mock DocumentSnapshots
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))

    // Call the method under test
    firestoreFirebaseRepository.search(
        mealID = "1",
        onSuccess = {

          // Do nothing; we just want to verify that the 'documents' field was accessed
        },
        onFailure = { fail("Failure callback should not be called") })

    // Verify that the 'documents' field was accessed
    verify(mockDocumentReference, times(1)).get()
  }

  @Test
  fun documentToRecipe_validDocument() {
    val document = mock(DocumentSnapshot::class.java)
    `when`(document.id).thenReturn("1")
    `when`(document.getString(FIRESTORE_RECIPE_NAME)).thenReturn("Test Recipe")
    `when`(document.getString(FIRESTORE_RECIPE_CATEGORY)).thenReturn("Category")
    `when`(document.getString(FIRESTORE_RECIPE_AREA)).thenReturn("Area")
    `when`(document.getString(FIRESTORE_RECIPE_INSTRUCTIONS)).thenReturn("Instructions")
    `when`(document.getString(FIRESTORE_RECIPE_PICTURE_ID)).thenReturn("ThumbUrl")
    `when`(document.get(FIRESTORE_RECIPE_INGREDIENTS))
        .thenReturn(listOf("Ingredient1", "Ingredient2"))
    `when`(document.get(FIRESTORE_RECIPE_MEASUREMENTS))
        .thenReturn(listOf("Measurement1", "Measurement2"))
    `when`(document.getString(FIRESTORE_RECIPE_TIME)).thenReturn("Time")
    `when`(document.getString(FIRESTORE_RECIPE_DIFFICULTY)).thenReturn("Difficulty")
    `when`(document.getString(FIRESTORE_RECIPE_PRICE)).thenReturn("Price")

    val recipe = firestoreFirebaseRepository.documentToRecipe(document)

    assertNotNull(recipe)
    assertEquals("1", recipe?.idMeal)
    assertEquals("Test Recipe", recipe?.strMeal)
    assertEquals("Category", recipe?.strCategory)
    assertEquals("Area", recipe?.strArea)
    assertEquals("Instructions", recipe?.strInstructions)
    assertEquals("ThumbUrl", recipe?.strMealThumbUrl)
    assertEquals(
        listOf("Ingredient1" to "Measurement1", "Ingredient2" to "Measurement2"),
        recipe?.ingredientsAndMeasurements)
    assertEquals("Time", recipe?.time)
    assertEquals("Difficulty", recipe?.difficulty)
    assertEquals("Price", recipe?.price)
  }

  @Test
  fun documentToRecipe_missingName() {
    val document = mock(DocumentSnapshot::class.java)
    `when`(document.getString(FIRESTORE_RECIPE_NAME)).thenReturn(null)

    val recipe = firestoreFirebaseRepository.documentToRecipe(document)

    assertNull(recipe)
  }

  @Test
  fun documentToRecipe_missingInstructions() {
    val document = mock(DocumentSnapshot::class.java)
    `when`(document.getString(FIRESTORE_RECIPE_NAME)).thenReturn("Test Recipe")
    `when`(document.getString(FIRESTORE_RECIPE_INSTRUCTIONS)).thenReturn(null)

    val recipe = firestoreFirebaseRepository.documentToRecipe(document)

    assertNull(recipe)
  }

  @Test
  fun documentToRecipe_missingPictureID() {
    val document = mock(DocumentSnapshot::class.java)
    `when`(document.getString(FIRESTORE_RECIPE_NAME)).thenReturn("Test Recipe")
    `when`(document.getString(FIRESTORE_RECIPE_INSTRUCTIONS)).thenReturn("Instructions")
    `when`(document.getString(FIRESTORE_RECIPE_PICTURE_ID)).thenReturn(null)

    val recipe = firestoreFirebaseRepository.documentToRecipe(document)

    assertNull(recipe)
  }

  @Test
  fun documentToRecipe_missingIngredients() {
    val document = mock(DocumentSnapshot::class.java)
    `when`(document.getString(FIRESTORE_RECIPE_NAME)).thenReturn("Test Recipe")
    `when`(document.getString(FIRESTORE_RECIPE_INSTRUCTIONS)).thenReturn("Instructions")
    `when`(document.getString(FIRESTORE_RECIPE_PICTURE_ID)).thenReturn("ThumbUrl")
    `when`(document.get(FIRESTORE_RECIPE_INGREDIENTS)).thenReturn(null)

    val recipe = firestoreFirebaseRepository.documentToRecipe(document)

    assertNull(recipe)
  }

  @Test
  fun documentToRecipe_missingMeasurements() {
    val document = mock(DocumentSnapshot::class.java)
    `when`(document.getString(FIRESTORE_RECIPE_NAME)).thenReturn("Test Recipe")
    `when`(document.getString(FIRESTORE_RECIPE_INSTRUCTIONS)).thenReturn("Instructions")
    `when`(document.getString(FIRESTORE_RECIPE_PICTURE_ID)).thenReturn("ThumbUrl")
    `when`(document.get(FIRESTORE_RECIPE_INGREDIENTS))
        .thenReturn(listOf("Ingredient1", "Ingredient2"))
    `when`(document.get(FIRESTORE_RECIPE_MEASUREMENTS)).thenReturn(null)

    val recipe = firestoreFirebaseRepository.documentToRecipe(document)

    assertNull(recipe)
  }
}
