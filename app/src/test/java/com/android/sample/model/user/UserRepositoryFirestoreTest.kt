package com.android.sample.model.user

import android.util.Log
import com.android.sample.ui.utils.testFridgeItem
import com.android.sample.ui.utils.testUsers
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.reflect.Method
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UserRepositoryFirestoreTest {

  @Mock private lateinit var mockFirestore: FirebaseFirestore
  @Mock private lateinit var mockDocument: DocumentReference
  @Mock private lateinit var mockCollection: CollectionReference
  @Mock private lateinit var mockSnapshot: DocumentSnapshot
  @Mock private lateinit var mockTask: Task<DocumentSnapshot>
  @Mock private lateinit var mockTaskVoid: Task<Void>

  private lateinit var userRepositoryFirestore: UserRepositoryFirestore

  private lateinit var fridgeItemExtractionMethod: Method
  private lateinit var convertSnapshotMethod: Method

  private val user = testUsers[0]

  private val fridgeItemExample = testFridgeItem[0]

  @Captor
  lateinit var onCompleteListenerCaptor: ArgumentCaptor<OnCompleteListener<DocumentSnapshot>>
  @Captor lateinit var onCompleteListenerCaptorVoid: ArgumentCaptor<OnCompleteListener<Void>>

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollection)
    `when`(mockCollection.document(any())).thenReturn(mockDocument)

    userRepositoryFirestore = UserRepositoryFirestore(mockFirestore)

    fridgeItemExtractionMethod =
        UserRepositoryFirestore::class
            .java
            .getDeclaredMethod("fridgeItemExtraction", Map::class.java)
    convertSnapshotMethod =
        UserRepositoryFirestore::class
            .java
            .getDeclaredMethod("convertSnapshot", String::class.java, DocumentSnapshot::class.java)
  }

  @Test
  fun addUserSuccessTest() {
    `when`(mockDocument.set(any())).thenReturn(mockTaskVoid)
    `when`(mockTaskVoid.isSuccessful).thenReturn(true)

    userRepositoryFirestore.addUser(user, onSuccess = {}, onFailure = {})

    verify(mockTaskVoid).addOnCompleteListener(onCompleteListenerCaptorVoid.capture())

    onCompleteListenerCaptorVoid.value.onComplete(mockTaskVoid)

    verify(mockDocument).set(any())
  }

  @Test
  fun addUserFailureTest() {
    `when`(mockDocument.set(any())).thenReturn(mockTaskVoid)
    `when`(mockTaskVoid.isSuccessful).thenReturn(false)
    val exception = Exception("Set user failure")
    `when`(mockTaskVoid.exception).thenReturn(exception)

    userRepositoryFirestore.addUser(
        user,
        onSuccess = {},
        onFailure = { e ->
          assertNotNull(e)
          assertEquals(exception, e)
        })

    verify(mockTaskVoid).addOnCompleteListener(onCompleteListenerCaptorVoid.capture())

    onCompleteListenerCaptorVoid.value.onComplete(mockTaskVoid)
  }

  @Test
  fun updateUserSuccessTest() {
    `when`(mockDocument.set(any())).thenReturn(mockTaskVoid)
    `when`(mockTaskVoid.isSuccessful).thenReturn(true)

    userRepositoryFirestore.updateUser(user, onSuccess = {}, onFailure = {})

    verify(mockTaskVoid).addOnCompleteListener(onCompleteListenerCaptorVoid.capture())

    onCompleteListenerCaptorVoid.value.onComplete(mockTaskVoid)

    verify(mockDocument).set(any())
  }

  @Test
  fun updateUserFailureTest() {
    `when`(mockDocument.set(any())).thenReturn(mockTaskVoid)
    `when`(mockTaskVoid.isSuccessful).thenReturn(false)
    val exception = Exception("Update user failed")
    `when`(mockTaskVoid.exception).thenReturn(exception)

    userRepositoryFirestore.updateUser(
        user,
        onSuccess = {},
        onFailure = { e ->
          assertNotNull(e)
          assertEquals(exception, e)
        })

    verify(mockTaskVoid).addOnCompleteListener(onCompleteListenerCaptorVoid.capture())

    onCompleteListenerCaptorVoid.value.onComplete(mockTaskVoid)
  }

  @Test
  fun fridgeItemExtractionTest() {
    val mapping =
        mapOf(
            "id" to fridgeItemExample.id,
            "quantity" to fridgeItemExample.quantity,
            "expirationDate" to
                mapOf(
                    "year" to fridgeItemExample.expirationDate.year.toLong(),
                    "monthValue" to fridgeItemExample.expirationDate.monthValue.toLong(),
                    "dayOfMonth" to fridgeItemExample.expirationDate.dayOfMonth.toLong()))

    fridgeItemExtractionMethod.isAccessible = true
    val fridgeItem = fridgeItemExtractionMethod.invoke(userRepositoryFirestore, mapping)

    assertEquals(fridgeItem, fridgeItemExample)
  }

  @Test
  fun snapshotConversionIsSuccessfulTest() {
    `when`(mockSnapshot.get("userName")).thenReturn(user.userName)
    `when`(mockSnapshot.get("profilePictureUrl")).thenReturn(user.profilePictureUrl)
    `when`(mockSnapshot.get("fridge"))
        .thenReturn(
            listOf(
                mapOf(
                    "id" to fridgeItemExample.id,
                    "quantity" to fridgeItemExample.quantity,
                    "expirationDate" to
                        mapOf(
                            "year" to fridgeItemExample.expirationDate.year.toLong(),
                            "monthValue" to fridgeItemExample.expirationDate.monthValue.toLong(),
                            "dayOfMonth" to fridgeItemExample.expirationDate.dayOfMonth.toLong()))))
    `when`(mockSnapshot.get("createdRecipes")).thenReturn(user.createdRecipes)
    `when`(mockSnapshot.get("likedRecipes")).thenReturn(user.likedRecipes)

    convertSnapshotMethod.isAccessible = true
    val obtainedUser = convertSnapshotMethod.invoke(userRepositoryFirestore, user.uid, mockSnapshot)

    assertEquals(user, obtainedUser)
  }

  @Test
  fun snapshotConversionFailsTest() {
    convertSnapshotMethod.isAccessible = true
    mockStatic(Log::class.java).use { mockedLog ->
      val obtainesUser = convertSnapshotMethod.invoke(userRepositoryFirestore, user.uid, null)

      mockedLog.verify {
        Log.e(
            eq("UserRepositoryFirestore"),
            eq("Error converting snapshot to user"),
            any<Exception>())
      }
      assertNull(obtainesUser)
    }
  }

  @Test
  fun getUserByIdSuccessTest() {
    `when`(mockDocument.get()).thenReturn(mockTask)
    `when`(mockTask.isSuccessful).thenReturn(true)
    `when`(mockTask.result).thenReturn(mockSnapshot)

    `when`(mockSnapshot.get("userName")).thenReturn(user.userName)
    `when`(mockSnapshot.get("profilePictureUrl")).thenReturn(user.profilePictureUrl)
    `when`(mockSnapshot.get("fridge"))
        .thenReturn(
            listOf(
                mapOf(
                    "id" to fridgeItemExample.id,
                    "quantity" to fridgeItemExample.quantity,
                    "expirationDate" to
                        mapOf(
                            "year" to fridgeItemExample.expirationDate.year.toLong(),
                            "monthValue" to fridgeItemExample.expirationDate.monthValue.toLong(),
                            "dayOfMonth" to fridgeItemExample.expirationDate.dayOfMonth.toLong()))))
    `when`(mockSnapshot.get("createdRecipes")).thenReturn(user.createdRecipes)
    `when`(mockSnapshot.get("likedRecipes")).thenReturn(user.likedRecipes)

    userRepositoryFirestore.getUserById(
        user.uid,
        onSuccess = { obtainedUser ->
          assertNotNull(obtainedUser)
          assert(obtainedUser == user)
        },
        onFailure = {})

    verify(mockTask).addOnCompleteListener(onCompleteListenerCaptor.capture())

    onCompleteListenerCaptor.value.onComplete(mockTask)
  }

  @Test
  fun getUserByIdFailureTest() {
    `when`(mockDocument.get()).thenReturn(mockTask)
    `when`(mockTask.isSuccessful).thenReturn(false)
    val exception = Exception("Firestore Error")
    `when`(mockTask.exception).thenReturn(exception)

    `when`(mockSnapshot.get("userName")).thenReturn("Gigel Frone")
    `when`(mockSnapshot.get("profilePictureUrl")).thenReturn("")
    `when`(mockSnapshot.get("fridge")).thenReturn(listOf<String>())
    `when`(mockSnapshot.get("createdRecipes")).thenReturn(listOf<String>())
    `when`(mockSnapshot.get("likedRecipes")).thenReturn(listOf<String>())

    userRepositoryFirestore.getUserById(
        user.uid,
        onSuccess = {},
        onFailure = { e ->
          assertNotNull(e)
          assertEquals(e, exception)
        })

    verify(mockTask).addOnCompleteListener(onCompleteListenerCaptor.capture())

    onCompleteListenerCaptor.value.onComplete(mockTask)
  }

  @Test
  fun deleteUserSuccessTest() {
    `when`(mockDocument.delete()).thenReturn(mockTaskVoid)
    `when`(mockTaskVoid.isSuccessful).thenReturn(true)

    userRepositoryFirestore.deleteUserById(user.uid, onSuccess = {}, onFailure = {})

    verify(mockTaskVoid).addOnCompleteListener(onCompleteListenerCaptorVoid.capture())

    onCompleteListenerCaptorVoid.value.onComplete(mockTaskVoid)

    verify(mockDocument).delete()
  }

  @Test
  fun deleteUserFailureTest() {
    `when`(mockDocument.delete()).thenReturn(mockTaskVoid)
    `when`(mockTaskVoid.isSuccessful).thenReturn(false)
    val exception = Exception("Delete user failed")
    `when`(mockTaskVoid.exception).thenReturn(exception)

    userRepositoryFirestore.deleteUserById(
        user.uid,
        onSuccess = {},
        onFailure = { e ->
          assertNotNull(e)
          assertEquals(e, exception)
        })

    verify(mockTaskVoid).addOnCompleteListener(onCompleteListenerCaptorVoid.capture())

    onCompleteListenerCaptorVoid.value.onComplete(mockTaskVoid)
  }
}
