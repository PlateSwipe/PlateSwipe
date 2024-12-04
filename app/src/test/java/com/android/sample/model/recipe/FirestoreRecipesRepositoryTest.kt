package com.android.sample.model.recipe

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.android.sample.model.filter.Difficulty
import com.android.sample.model.filter.Filter
import com.android.sample.model.filter.FloatRange
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_AREA
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_CATEGORY
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_DIFFICULTY
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_INGREDIENTS
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_INSTRUCTIONS_TEXT
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_INSTRUCTION_ICON
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_INSTRUCTION_TIME
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_MEASUREMENTS
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_NAME
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_PICTURE_ID
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_PRICE
import com.android.sample.resources.C.Tag.FIRESTORE_RECIPE_TIME
import com.android.sample.resources.C.Tag.Filter.UNINITIALIZED_BORN_VALUE
import com.android.sample.ui.utils.testRecipes
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.util.Assert.fail
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
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

  private lateinit var firestoreFirebaseRepository: FirestoreRecipesRepository

  private val recipe = testRecipes[0]

  private val filterWithTimeAndPrice =
      Filter(
          timeRange = FloatRange(0f, 100f, 0f, 100f),
          difficulty = Difficulty.Undefined,
          category = null,
      )

  private val filterWithCategory =
      Filter(
          timeRange = FloatRange(0f, 100f, 0f, 100f),
          difficulty = Difficulty.Undefined,
          category = "Beef",
      )

  private val filterWithDifficultyAndTimeAndPrice =
      Filter(
          timeRange = FloatRange(0f, 100f, 0f, 100f),
          difficulty = Difficulty.Easy,
          category = null,
      )

  private val filterWithDifficulty =
      Filter(
          timeRange =
              FloatRange(
                  UNINITIALIZED_BORN_VALUE,
                  UNINITIALIZED_BORN_VALUE,
                  UNINITIALIZED_BORN_VALUE,
                  UNINITIALIZED_BORN_VALUE),
          difficulty = Difficulty.Easy,
          category = null,
      )

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
    // Ensure that mockQuerySnapshot is properly initialized and mocked
    `when`(mockCollectionReference.document(recipe.uid)).thenReturn(mockDocumentReference)

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
    `when`(document.get(FIRESTORE_RECIPE_INSTRUCTIONS_TEXT))
        .thenReturn(listOf("Instructions1", "Instructions2", "Instructions3"))
    `when`(document.get(FIRESTORE_RECIPE_INSTRUCTION_TIME)).thenReturn(listOf("Time1", "", "Time3"))
    `when`(document.get(FIRESTORE_RECIPE_INSTRUCTION_ICON)).thenReturn(listOf("Cook", "", "Fire"))

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
    assertEquals("1", recipe?.uid)
    assertEquals("Test Recipe", recipe?.name)
    assertEquals("Category", recipe?.category)
    assertEquals("Area", recipe?.origin)
    assertEquals(
        listOf(
            Instruction("Instructions1", "Time1", "Cook"),
            Instruction("Instructions2", "", ""),
            Instruction("Instructions3", "Time3", "Fire")),
        recipe?.instructions)
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
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_CATEGORY)).thenReturn("Category")
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTIONS_TEXT)).thenReturn(null)
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTION_TIME))
        .thenReturn(listOf("Time1", "", "Time3"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTION_ICON))
        .thenReturn(listOf("Cook", "", "Fire"))

    val recipe = firestoreFirebaseRepository.documentToRecipe(document)

    assertNull(recipe)
  }

  @Test
  fun documentToRecipe_missingPictureID() {
    val document = mock(DocumentSnapshot::class.java)
    `when`(document.getString(FIRESTORE_RECIPE_NAME)).thenReturn("Test Recipe")
    `when`(document.get(FIRESTORE_RECIPE_INSTRUCTIONS_TEXT))
        .thenReturn(listOf("Instructions1", "Instructions2", "Instructions3"))
    `when`(document.get(FIRESTORE_RECIPE_INSTRUCTION_TIME)).thenReturn(listOf("Time1", "", "Time3"))
    `when`(document.get(FIRESTORE_RECIPE_INSTRUCTION_ICON)).thenReturn(listOf("Cook", "", "Fire"))
    `when`(document.getString(FIRESTORE_RECIPE_PICTURE_ID)).thenReturn(null)

    val recipe = firestoreFirebaseRepository.documentToRecipe(document)

    assertNull(recipe)
  }

  @Test
  fun documentToRecipe_missingIngredients() {
    val document = mock(DocumentSnapshot::class.java)
    `when`(document.getString(FIRESTORE_RECIPE_NAME)).thenReturn("Test Recipe")
    `when`(document.get(FIRESTORE_RECIPE_INSTRUCTIONS_TEXT))
        .thenReturn(listOf("Instructions1", "Instructions2", "Instructions3"))
    `when`(document.get(FIRESTORE_RECIPE_INSTRUCTION_TIME)).thenReturn(listOf("Time1", "", "Time3"))
    `when`(document.get(FIRESTORE_RECIPE_INSTRUCTION_ICON)).thenReturn(listOf("Cook", "", "Fire"))
    `when`(document.getString(FIRESTORE_RECIPE_PICTURE_ID)).thenReturn("ThumbUrl")
    `when`(document.get(FIRESTORE_RECIPE_INGREDIENTS)).thenReturn(null)

    val recipe = firestoreFirebaseRepository.documentToRecipe(document)

    assertNull(recipe)
  }

  @Test
  fun documentToRecipe_missingMeasurements() {
    val document = mock(DocumentSnapshot::class.java)
    `when`(document.getString(FIRESTORE_RECIPE_NAME)).thenReturn("Test Recipe")
    `when`(document.get(FIRESTORE_RECIPE_INSTRUCTIONS_TEXT))
        .thenReturn(listOf("Instructions1", "Instructions2", "Instructions3"))
    `when`(document.get(FIRESTORE_RECIPE_INSTRUCTION_TIME)).thenReturn(listOf("Time1", "", "Time3"))
    `when`(document.get(FIRESTORE_RECIPE_INSTRUCTION_ICON)).thenReturn(listOf("Cook", "", "Fire"))
    `when`(document.getString(FIRESTORE_RECIPE_PICTURE_ID)).thenReturn("ThumbUrl")
    `when`(document.get(FIRESTORE_RECIPE_INGREDIENTS))
        .thenReturn(listOf("Ingredient1", "Ingredient2"))
    `when`(document.get(FIRESTORE_RECIPE_MEASUREMENTS)).thenReturn(null)

    val recipe = firestoreFirebaseRepository.documentToRecipe(document)

    assertNull(recipe)
  }

  @Test
  fun searchByCategory_throwsException() {
    assertThrows(IllegalArgumentException::class.java) {
      firestoreFirebaseRepository.searchByCategory(
          "Category",
          { fail("Success callback should not be called") },
          { fail("Failure callback should not be called") },
          -1)
    }
  }

  @Test
  fun searchByCategory_withValidCategory_returnsRecipes() {
    // Create and configure mock objects
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)
    val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)

    // Mock the Firestore documents to return a valid recipe
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.id).thenReturn("1")
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_NAME)).thenReturn("Test Recipe")
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_CATEGORY)).thenReturn("Category")
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTIONS_TEXT))
        .thenReturn(listOf("Instructions1", "Instructions2", "Instructions3"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTION_TIME))
        .thenReturn(listOf("Time1", "", "Time3"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTION_ICON))
        .thenReturn(listOf("Cook", "", "Fire"))
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_PICTURE_ID)).thenReturn("ThumbUrl")
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INGREDIENTS))
        .thenReturn(listOf("Ingredient1", "Ingredient2"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_MEASUREMENTS))
        .thenReturn(listOf("Measurement1", "Measurement2"))

    // Mock Firestore query to return the mock QuerySnapshot
    `when`(mockCollectionReference.whereEqualTo(FIRESTORE_RECIPE_CATEGORY, "Category"))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.limit(10)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    // Execute the searchByCategory method
    firestoreFirebaseRepository.searchByCategory(
        "Category",
        { recipes ->
          assertNotNull(recipes)
          assertEquals(1, recipes.size)
          assertEquals("Test Recipe", recipes[0].name)
        },
        { fail("Failure callback should not be called") },
        10)

    // Complete all asynchronous operations
    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun searchByCategory_withLimitZero_throwsException() {
    assertThrows(IllegalArgumentException::class.java) {
      firestoreFirebaseRepository.searchByCategory(
          "Category",
          { fail("Success callback should not be called") },
          { fail("Failure callback should not be called") },
          0)
    }
  }

  @Test
  fun searchByCategory_withFirestoreFailure_callsOnFailure() {
    val exception = Exception("Firestore error")
    `when`(mockCollectionReference.whereEqualTo(FIRESTORE_RECIPE_CATEGORY, "Category"))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.limit(10)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forException(exception))

    firestoreFirebaseRepository.searchByCategory(
        "Category",
        { fail("Success callback should not be called") },
        { e ->
          assertNotNull(e)
          assertEquals("Firestore error", e.message)
        },
        10)

    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun search_withValidMealId_returnsRecipe() {
    // Mock a valid document snapshot to simulate a found recipe
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.id).thenReturn("1")
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_NAME)).thenReturn("Chicken")
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_CATEGORY)).thenReturn("Category")
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTIONS_TEXT))
        .thenReturn(listOf("Instructions1", "Instructions2", "Instructions3"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTION_TIME))
        .thenReturn(listOf("Time1", "", "Time3"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTION_ICON))
        .thenReturn(listOf("Cook", "", "Fire"))
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_PICTURE_ID))
        .thenReturn("https://image.url")
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INGREDIENTS))
        .thenReturn(listOf("Chicken", "Salt"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_MEASUREMENTS)).thenReturn(listOf("1", "1 tsp"))

    firestoreFirebaseRepository.search(
        mealID = "1",
        onSuccess = { recipe ->
          assertNotNull(recipe)
          assertEquals("Chicken", recipe.name)
          assertEquals(
              listOf(
                  Instruction("Instructions1", "Time1", "Cook"),
                  Instruction("Instructions2", "", ""),
                  Instruction("Instructions3", "Time3", "Fire")),
              recipe.instructions)
          assertEquals("https://image.url", recipe.strMealThumbUrl)
          assertEquals(
              listOf("Chicken" to "1", "Salt" to "1 tsp"), recipe.ingredientsAndMeasurements)
        },
        onFailure = { fail("Failure callback should not be called") })

    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun search_withInvalidMealId_callsOnFailure() {
    // Simulate a failed document retrieval
    `when`(mockDocumentReference.get())
        .thenReturn(Tasks.forException(Exception("Recipe not found")))

    firestoreFirebaseRepository.search(
        mealID = "invalid_id",
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { exception ->
          assertNotNull(exception)
          assertEquals("Recipe not found", exception.message)
        })

    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun search_withMissingRecipeDetails_callsOnFailure() {
    // Mock a document missing crucial recipe details
    `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_NAME)).thenReturn(null) // Missing name

    firestoreFirebaseRepository.search(
        mealID = "1",
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { exception ->
          assertNotNull(exception)
          assertEquals("Recipe not found", exception.message)
        })

    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun random_withValidNumberOfElements_returnsRecipes() {
    // Arrange: Prepare mock documents and set up expectations for random UID generation
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)
    val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)

    // Set up a mock recipe document
    `when`(mockDocumentSnapshot.id).thenReturn("random_id_1")
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_NAME)).thenReturn("Test Recipe")
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_CATEGORY)).thenReturn("Category")
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTIONS_TEXT))
        .thenReturn(listOf("Instructions1", "Instructions2", "Instructions3"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTION_TIME))
        .thenReturn(listOf("Time1", "", "Time3"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTION_ICON))
        .thenReturn(listOf("Cook", "", "Fire"))
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_PICTURE_ID)).thenReturn("ThumbUrl")
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INGREDIENTS)).thenReturn(listOf("Ingredient1"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_MEASUREMENTS)).thenReturn(listOf("1 tsp"))

    // Mock Firestore query
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    val successfulTask = Tasks.forResult(mockQuerySnapshot)
    `when`(mockCollectionReference.get()).thenReturn(successfulTask)
    `when`(mockCollectionReference.limit(1)).thenReturn(mockCollectionReference)

    `when`(mockCollectionReference.whereGreaterThanOrEqualTo(any<FieldPath>(), any<String>()))
        .thenReturn(mockCollectionReference)
    val fetchedRecipes = mutableListOf<Recipe>()
    // Act: Call the random function
    firestoreFirebaseRepository.random(
        nbOfElements = 1,
        onSuccess = { recipes ->
          fetchedRecipes += recipes
          assertNotNull(recipes)
          assertEquals(1, recipes.size)
          assertEquals("Test Recipe", recipes[0].name)
        },
        onFailure = { fail("Failure callback should not be called") })

    // Ensure all asynchronous operations are completed
    shadowOf(Looper.getMainLooper()).idle()

    assertEquals(1, fetchedRecipes.size)
  }

  @Test
  fun random_withNotAllValidNumberOfElements_returnsRecipes() = runTest {
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)
    val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)

    // Set up a mock recipe document
    `when`(mockDocumentSnapshot.id).thenReturn("random_id_1")
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_NAME)).thenReturn("Test Recipe")
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTIONS_TEXT))
        .thenReturn(listOf("Instructions1", "Instructions2", "Instructions3"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTION_TIME))
        .thenReturn(listOf("Time1", "", "Time3"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTION_ICON))
        .thenReturn(listOf("Cook", "", "Fire"))
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_PICTURE_ID)).thenReturn("ThumbUrl")
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INGREDIENTS)).thenReturn(listOf("Ingredient1"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_MEASUREMENTS)).thenReturn(listOf("1 tsp"))

    // Mock Firestore query
    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    val successfulTask = Tasks.forResult(mockQuerySnapshot)
    `when`(mockCollectionReference.get()).thenReturn(successfulTask)
    `when`(mockCollectionReference.limit(any())).thenReturn(mockCollectionReference)

    `when`(mockCollectionReference.whereGreaterThanOrEqualTo(any<FieldPath>(), any<String>()))
        .thenReturn(mockCollectionReference)
    val fetchedRecipes = mutableListOf<Recipe>()
    // Act: Call the random function
    firestoreFirebaseRepository.random(
        nbOfElements = 2,
        onSuccess = { recipes ->
          fetchedRecipes += recipes
          assertNotNull(recipes)
          assertEquals(1, recipes.size)
          assertEquals("Test Recipe", recipes[0].name)
        },
        onFailure = { fail("Failure callback should not be called") })

    // Ensure all asynchronous operations are completed
    shadowOf(Looper.getMainLooper()).idle()

    assertEquals(1, fetchedRecipes.size)
  }

  @Test
  fun random_withZeroElements_throwsException() {
    // Act and Assert: Verify that calling `random` with zero elements throws an
    // IllegalArgumentException
    assertThrows(IllegalArgumentException::class.java) {
      firestoreFirebaseRepository.random(
          nbOfElements = 0,
          onSuccess = { fail("Success callback should not be called") },
          onFailure = { fail("Failure callback should not be called") })
    }
  }

  @Test
  fun random_withFirestoreFailure_callsOnFailure() {
    // Arrange: Simulate a Firestore exception
    val exception = Exception("Firestore error")
    `when`(mockCollectionReference.whereGreaterThanOrEqualTo(any<FieldPath>(), any<String>()))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.limit(5)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forException(exception))

    // Act: Call the random function
    firestoreFirebaseRepository.random(
        nbOfElements = 5,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { e ->
          assertNotNull(e)
          assertEquals("Firestore error", e.message)
        })

    // Ensure all asynchronous operations are completed
    shadowOf(Looper.getMainLooper()).idle()
  }

  @Test
  fun random_withNoRecipes_callsOnSuccessWithEmptyList() {
    // Arrange: Simulate no recipes returned
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)
    `when`(mockQuerySnapshot.documents).thenReturn(emptyList<DocumentSnapshot>())
    `when`(mockCollectionReference.whereGreaterThanOrEqualTo(any<FieldPath>(), any<String>()))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.limit(5)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
    var haveFailed = false

    // Act: Call the random function
    firestoreFirebaseRepository.random(
        nbOfElements = 5,
        onSuccess = { fail("Success callback should not be called") },
        onFailure = { haveFailed = true })

    // Ensure all asynchronous operations are completed
    shadowOf(Looper.getMainLooper()).idle()
    assertTrue(haveFailed)
  }

  @Test
  fun `test filterSearch function throws error when limit smaller than 0`() {
    assertThrows(IllegalArgumentException::class.java) {
      firestoreFirebaseRepository.filterSearch(
          filter = filterWithTimeAndPrice,
          onSuccess = { fail("Success callback should not be called") },
          onFailure = { fail("Failure callback should not be called") },
          limit = -1)
    }
  }

  @Test
  fun `test filterSearch function does not throw error when limit is equal to 1`() {
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)
    val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
    val mockQuery = mock(Query::class.java)

    `when`(mockCollectionReference.whereEqualTo(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockCollectionReference.whereGreaterThan(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.whereGreaterThan(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.whereLessThan(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.whereEqualTo(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.limit(any())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.id).thenReturn("1")
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_NAME)).thenReturn("Test Recipe")
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_CATEGORY)).thenReturn("Category")
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTIONS_TEXT))
        .thenReturn(listOf("Instructions1", "Instructions2", "Instructions3"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTION_TIME))
        .thenReturn(listOf("Time1", "", "Time3"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTION_ICON))
        .thenReturn(listOf("Cook", "", "Fire"))
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_PICTURE_ID)).thenReturn("ThumbUrl")
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INGREDIENTS))
        .thenReturn(listOf("Ingredient1", "Ingredient2"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_MEASUREMENTS))
        .thenReturn(listOf("Measurement1", "Measurement2"))

    // Mock Firestore query to return the mock QuerySnapshot
    `when`(mockCollectionReference.whereEqualTo(FIRESTORE_RECIPE_CATEGORY, "Category"))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.limit(10)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    val fetchedRecipes = mutableListOf<Recipe>()
    firestoreFirebaseRepository.filterSearch(
        filter = filterWithTimeAndPrice,
        onSuccess = { recipes -> fetchedRecipes += recipes },
        onFailure = { fail("Failure callback should not be called") },
        limit = 1)

    shadowOf(Looper.getMainLooper()).idle()
    assertEquals(1, fetchedRecipes.size)
  }

  @Test
  fun `test filterSearch function correctly passes through filter ifs filterWithTimeAndPrice`() {
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)
    val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
    val mockQuery = mock(Query::class.java)

    `when`(mockCollectionReference.whereEqualTo(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockCollectionReference.whereGreaterThan(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.whereGreaterThan(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.whereLessThan(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.whereEqualTo(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.limit(any())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.id).thenReturn("1")
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_NAME)).thenReturn("Test Recipe")
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_CATEGORY)).thenReturn("Category")
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTIONS_TEXT))
        .thenReturn(listOf("Instructions1", "Instructions2", "Instructions3"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTION_TIME))
        .thenReturn(listOf("Time1", "", "Time3"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTION_ICON))
        .thenReturn(listOf("Cook", "", "Fire"))
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_PICTURE_ID)).thenReturn("ThumbUrl")
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INGREDIENTS))
        .thenReturn(listOf("Ingredient1", "Ingredient2"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_MEASUREMENTS))
        .thenReturn(listOf("Measurement1", "Measurement2"))

    // Mock Firestore query to return the mock QuerySnapshot
    `when`(mockCollectionReference.whereEqualTo(FIRESTORE_RECIPE_CATEGORY, "Category"))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.limit(10)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    val fetchedRecipes = mutableListOf<Recipe>()
    firestoreFirebaseRepository.filterSearch(
        filter = filterWithTimeAndPrice,
        onSuccess = { recipes -> fetchedRecipes += recipes },
        onFailure = { fail("Failure callback should not be called") },
        limit = 1)

    // check if all calls were made correctly
    verify(mockCollectionReference, times(0)).whereEqualTo(any<String>(), any())
    verify(mockCollectionReference, times(1)).whereGreaterThan(any<String>(), any())

    verify(mockQuery, times(1)).whereLessThan(any<String>(), any())

    verify(mockQuery, times(0)).whereEqualTo(any<String>(), any())

    shadowOf(Looper.getMainLooper()).idle()

    assertEquals(1, fetchedRecipes.size)
  }

  @Test
  fun `test filterSearch function correctly passes through filter ifs filterWithCategory`() {
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)
    val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
    val mockQuery = mock(Query::class.java)

    `when`(mockCollectionReference.whereEqualTo(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockCollectionReference.whereGreaterThan(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.whereGreaterThan(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.whereLessThan(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.whereEqualTo(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.limit(any())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.id).thenReturn("1")
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_NAME)).thenReturn("Test Recipe")
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_CATEGORY)).thenReturn("Category")
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTIONS_TEXT))
        .thenReturn(listOf("Instructions1", "Instructions2", "Instructions3"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTION_TIME))
        .thenReturn(listOf("Time1", "", "Time3"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTION_ICON))
        .thenReturn(listOf("Cook", "", "Fire"))
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_PICTURE_ID)).thenReturn("ThumbUrl")
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INGREDIENTS))
        .thenReturn(listOf("Ingredient1", "Ingredient2"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_MEASUREMENTS))
        .thenReturn(listOf("Measurement1", "Measurement2"))

    // Mock Firestore query to return the mock QuerySnapshot
    `when`(mockCollectionReference.whereEqualTo(FIRESTORE_RECIPE_CATEGORY, "Category"))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.limit(10)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    val fetchedRecipes = mutableListOf<Recipe>()
    firestoreFirebaseRepository.filterSearch(
        filter = filterWithCategory,
        onSuccess = { recipes -> fetchedRecipes += recipes },
        onFailure = { fail("Failure callback should not be called") },
        limit = 1)

    /*
    check if all calls were made correctly, the logic here is done in a way where the first
    filter is done with the mockCollectionReference object and then it is casted  to a mockQuery
    */
    verify(mockCollectionReference, times(1)).whereEqualTo(any<String>(), any())

    verify(mockQuery, times(1)).whereGreaterThan(any<String>(), any())
    verify(mockQuery, times(1)).whereLessThan(any<String>(), any())

    verify(mockQuery, times(0)).whereEqualTo(any<String>(), any())

    shadowOf(Looper.getMainLooper()).idle()

    assertEquals(1, fetchedRecipes.size)
  }

  @Test
  fun `test filterSearch function correctly passes through filter ifs filterWithDifficultyAndTimeAndPrice`() {
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)
    val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
    val mockQuery = mock(Query::class.java)

    `when`(mockCollectionReference.whereEqualTo(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockCollectionReference.whereGreaterThan(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.whereGreaterThan(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.whereLessThan(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.whereEqualTo(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.limit(any())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.id).thenReturn("1")
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_NAME)).thenReturn("Test Recipe")
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_CATEGORY)).thenReturn("Category")
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTIONS_TEXT))
        .thenReturn(listOf("Instructions1", "Instructions2", "Instructions3"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTION_TIME))
        .thenReturn(listOf("Time1", "", "Time3"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTION_ICON))
        .thenReturn(listOf("Cook", "", "Fire"))
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_PICTURE_ID)).thenReturn("ThumbUrl")
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INGREDIENTS))
        .thenReturn(listOf("Ingredient1", "Ingredient2"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_MEASUREMENTS))
        .thenReturn(listOf("Measurement1", "Measurement2"))

    // Mock Firestore query to return the mock QuerySnapshot
    `when`(mockCollectionReference.whereEqualTo(FIRESTORE_RECIPE_CATEGORY, "Category"))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.limit(10)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    val fetchedRecipes = mutableListOf<Recipe>()
    firestoreFirebaseRepository.filterSearch(
        filter = filterWithDifficultyAndTimeAndPrice,
        onSuccess = { recipes -> fetchedRecipes += recipes },
        onFailure = { fail("Failure callback should not be called") },
        limit = 1)

    /*
    check if all calls were made correctly, the logic here is done in a way where the first
    filter is done with the mockCollectionReference object and then it is casted  to a mockQuery
    */
    verify(mockCollectionReference, times(0)).whereEqualTo(any<String>(), any())
    verify(mockCollectionReference, times(1)).whereGreaterThan(any<String>(), any())

    verify(mockQuery, times(1)).whereLessThan(any<String>(), any())

    verify(mockQuery, times(1)).whereEqualTo(any<String>(), any())

    shadowOf(Looper.getMainLooper()).idle()

    assertEquals(1, fetchedRecipes.size)
  }

  @Test
  fun `test filterSearch function correctly passes through filter ifs filterWithDifficulty`() {
    val mockQuerySnapshot = mock(QuerySnapshot::class.java)
    val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)
    val mockQuery = mock(Query::class.java)

    `when`(mockCollectionReference.whereEqualTo(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockCollectionReference.whereGreaterThan(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.whereGreaterThan(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.whereLessThan(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.whereEqualTo(any<String>(), any())).thenReturn(mockQuery)
    `when`(mockQuery.limit(any())).thenReturn(mockQuery)
    `when`(mockQuery.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    `when`(mockQuerySnapshot.documents).thenReturn(listOf(mockDocumentSnapshot))
    `when`(mockDocumentSnapshot.id).thenReturn("1")
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_NAME)).thenReturn("Test Recipe")
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_CATEGORY)).thenReturn("Category")
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTIONS_TEXT))
        .thenReturn(listOf("Instructions1", "Instructions2", "Instructions3"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTION_TIME))
        .thenReturn(listOf("Time1", "", "Time3"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INSTRUCTION_ICON))
        .thenReturn(listOf("Cook", "", "Fire"))
    `when`(mockDocumentSnapshot.getString(FIRESTORE_RECIPE_PICTURE_ID)).thenReturn("ThumbUrl")
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_INGREDIENTS))
        .thenReturn(listOf("Ingredient1", "Ingredient2"))
    `when`(mockDocumentSnapshot.get(FIRESTORE_RECIPE_MEASUREMENTS))
        .thenReturn(listOf("Measurement1", "Measurement2"))

    // Mock Firestore query to return the mock QuerySnapshot
    `when`(mockCollectionReference.whereEqualTo(FIRESTORE_RECIPE_CATEGORY, "Category"))
        .thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.limit(10)).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

    val fetchedRecipes = mutableListOf<Recipe>()
    firestoreFirebaseRepository.filterSearch(
        filter = filterWithDifficulty,
        onSuccess = { recipes -> fetchedRecipes += recipes },
        onFailure = { fail("Failure callback should not be called") },
        limit = 1)

    /*
    check if all calls were made correctly, the logic here is done in a way where the first
    filter is done with the mockCollectionReference object and then it is casted  to a mockQuery
    */
    verify(mockCollectionReference, times(1)).whereEqualTo(any<String>(), any())
    verify(mockCollectionReference, times(0)).whereGreaterThan(any<String>(), any())

    verify(mockQuery, times(0)).whereGreaterThan(any<String>(), any())
    verify(mockQuery, times(0)).whereLessThan(any<String>(), any())

    verify(mockQuery, times(0)).whereEqualTo(any<String>(), any())

    shadowOf(Looper.getMainLooper()).idle()

    assertEquals(1, fetchedRecipes.size)
  }
}
