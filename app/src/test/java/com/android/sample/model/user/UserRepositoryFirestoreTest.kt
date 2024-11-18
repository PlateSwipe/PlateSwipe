package com.android.sample.model.user

import com.android.sample.ui.utils.testUsers
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
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

  private val user = testUsers[0]

  @Captor
  lateinit var onCompleteListenerCaptor: ArgumentCaptor<OnCompleteListener<DocumentSnapshot>>
  @Captor lateinit var onCompleteListenerCaptorVoid: ArgumentCaptor<OnCompleteListener<Void>>

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    `when`(mockFirestore.collection(any())).thenReturn(mockCollection)
    `when`(mockCollection.document(any())).thenReturn(mockDocument)

    userRepositoryFirestore = UserRepositoryFirestore(mockFirestore)
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
  fun getUserByIdSuccessTest() {
    `when`(mockDocument.get()).thenReturn(mockTask)
    `when`(mockTask.isSuccessful).thenReturn(true)
    `when`(mockTask.result).thenReturn(mockSnapshot)

    `when`(mockSnapshot.get("userName")).thenReturn("Gigel Frone")
    `when`(mockSnapshot.get("profilePictureUrl")).thenReturn("")
    `when`(mockSnapshot.get("fridge")).thenReturn(listOf<String>())
    `when`(mockSnapshot.get("createdRecipes")).thenReturn(listOf<String>())
    `when`(mockSnapshot.get("likedRecipes")).thenReturn(listOf<String>())

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
