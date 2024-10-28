package com.android.sample.model.recipe

import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class FirestoreRecipesRepositoryTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockDocumentReference: DocumentReference
  @Mock private lateinit var mockCollectionReference: CollectionReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot
  @Mock private lateinit var mockToDoQuerySnapshot: QuerySnapshot


  private lateinit var firestoreFirebaseRepository: FirestoreRecipesRepository

  private val recipe = Recipe(
    idMeal = "1",
    strMeal = "Chicken",
    strCategory = "Main",
    strArea = "Italian",
    strInstructions = "Instructions",
    strMealThumbUrl = "https://www.themealdb.com/images/media/meals/1548772327.jpg",
    ingredientsAndMeasurements = listOf(
      Pair("Chicken", "1"),
      Pair("Salt", "1 tsp")
    ),
    time = "30 mins",
    difficulty = "Easy",
    price = "Low"
  )

  @Before
  fun setup(){
    MockitoAnnotations.openMocks(this)

    if(FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()){
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())

    }

    firestoreFirebaseRepository = FirestoreRecipesRepository(mockFirestore)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollectionReference)
    `when`(mockCollectionReference.document(any())).thenReturn(mockDocumentReference)
    `when`(mockCollectionReference.document()).thenReturn(mockDocumentReference)

  }

  @Test
  fun getNewUid(){
    `when`(mockDocumentReference.id).thenReturn("1")
    val uid = firestoreFirebaseRepository.getNewUid()
    assert(uid == "1")
  }
}