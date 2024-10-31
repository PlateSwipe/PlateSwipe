package com.android.sample.model.imageRepositoryFirebase

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.android.sample.model.image.ImageRepositoryFirebase
import com.android.sample.model.image.ImageDirectoryType
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ImageRepositoryFirebase {

  @Mock private lateinit var mockFirebaseStorage: FirebaseStorage
  @Mock private lateinit var mockStorageRef: StorageReference
  @Mock private lateinit var mockImageRef: StorageReference
  @Mock private lateinit var mockUpload: UploadTask
  @Mock private lateinit var mockDownload: FileDownloadTask
  @Mock private lateinit var mockTaskVoid: Task<Void>

  @Captor
  private lateinit var onCompleteUploadTaskListenerCaptor:
      ArgumentCaptor<OnCompleteListener<UploadTask.TaskSnapshot>>
  @Captor
  private lateinit var onCompleteFileDownloadTaskListenerCaptor:
      ArgumentCaptor<OnCompleteListener<FileDownloadTask.TaskSnapshot>>
  @Captor
  private lateinit var onCompleteVoidListenerCaptor: ArgumentCaptor<OnCompleteListener<Void>>

  private lateinit var testImageDirectoryUID: String
  private lateinit var testName: String
  private lateinit var imageStorage: ImageRepositoryFirebase
  private lateinit var testFilePath: String
  private lateinit var imageBitmap: ImageBitmap

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    `when`(mockFirebaseStorage.reference).thenReturn(mockStorageRef)
    `when`(mockStorageRef.child(any())).thenReturn(mockImageRef)

    testImageDirectoryUID = "testUID"
    testName = "testName"
    imageStorage = ImageRepositoryFirebase(mockFirebaseStorage)
    testFilePath = "app/src/test/resources/images/Muscle mice sticker.jpg"
    imageBitmap = BitmapFactory.decodeFile(testFilePath).asImageBitmap()
  }

  @Test
  fun uploadImageFromDeviceSuccessful() {
    `when`(mockImageRef.putFile(any())).thenReturn(mockUpload)
    `when`(mockUpload.isSuccessful).thenReturn(true)

    imageStorage.uploadImageFromDevice(
      testImageDirectoryUID, testName ,ImageDirectoryType.INGREDIENT, testFilePath, onSuccess = {}, onFailure = {})

    verify(mockUpload).addOnCompleteListener(onCompleteUploadTaskListenerCaptor.capture())

    onCompleteUploadTaskListenerCaptor.value.onComplete(mockUpload)

    verify(mockImageRef).putFile(any())
  }

  @Test
  fun uploadImageFromDeviceFailure() {
    `when`(mockImageRef.putFile(any())).thenReturn(mockUpload)
    val exceptionMessage = "Image upload to Firebase storage has failed"
    `when`(mockUpload.isSuccessful).thenReturn(false)
    `when`(mockUpload.exception).thenReturn(Exception(exceptionMessage))

    imageStorage.uploadImageFromDevice(
      testImageDirectoryUID,
      testName,
        ImageDirectoryType.INGREDIENT,
        testFilePath,
        onSuccess = {},
        onFailure = { e ->
          assertNotNull(e)
          assertEquals(exceptionMessage, e.message)
        })

    verify(mockUpload).addOnCompleteListener(onCompleteUploadTaskListenerCaptor.capture())

    onCompleteUploadTaskListenerCaptor.value.onComplete(mockUpload)
  }

  @Test
  fun uploadImageSuccessful() {
    `when`(mockImageRef.putStream(any())).thenReturn(mockUpload)
    `when`(mockUpload.isSuccessful).thenReturn(true)

    imageStorage.uploadImage(testImageDirectoryUID, testName, ImageDirectoryType.RECIPE, imageBitmap, onSuccess = {}, onFailure = {})

    verify(mockUpload).addOnCompleteListener(onCompleteUploadTaskListenerCaptor.capture())

    onCompleteUploadTaskListenerCaptor.value.onComplete(mockUpload)

    verify(mockImageRef).putStream(any())
  }

  @Test
  fun uploadImageFailure() {
    `when`(mockImageRef.putStream(any())).thenReturn(mockUpload)
    val exceptionMessage = "Image upload to Firebase storage has failed"
    `when`(mockUpload.isSuccessful).thenReturn(false)
    `when`(mockUpload.exception).thenReturn(Exception(exceptionMessage))

    imageStorage.uploadImage(
      testImageDirectoryUID,
      testName,
        ImageDirectoryType.RECIPE,
        imageBitmap,
        onSuccess = {},
        onFailure = { e ->
          assertNotNull(e)
          assertEquals(exceptionMessage, e.message)
        })

    verify(mockUpload).addOnCompleteListener(onCompleteUploadTaskListenerCaptor.capture())

    onCompleteUploadTaskListenerCaptor.value.onComplete(mockUpload)
  }

  @Test
  fun deleteImageSuccessful() {
    `when`(mockImageRef.delete()).thenReturn(mockTaskVoid)
    `when`(mockTaskVoid.isSuccessful).thenReturn(true)

    imageStorage.deleteImage(testImageDirectoryUID, testName, ImageDirectoryType.INGREDIENT, onSuccess = {}, onFailure = {})

    verify(mockTaskVoid).addOnCompleteListener(onCompleteVoidListenerCaptor.capture())

    onCompleteVoidListenerCaptor.value.onComplete(mockTaskVoid)

    verify(mockImageRef).delete()
  }

  @Test
  fun deleteImageFailure() {
    `when`(mockImageRef.delete()).thenReturn(mockTaskVoid)
    val exceptionMessage = "Deletion from the Firebase storage has failed"
    `when`(mockTaskVoid.isSuccessful).thenReturn(false)
    `when`(mockTaskVoid.exception).thenReturn(Exception(exceptionMessage))

    imageStorage.deleteImage(
      testImageDirectoryUID,
      testName,
        ImageDirectoryType.INGREDIENT,
        onSuccess = {},
        onFailure = { e ->
          assertNotNull(e)
          assertEquals(exceptionMessage, e.message)
        })

    verify(mockTaskVoid).addOnCompleteListener(onCompleteVoidListenerCaptor.capture())

    onCompleteVoidListenerCaptor.value.onComplete(mockTaskVoid)
  }

  @Test
  fun getImageSuccessful() {
    `when`(mockImageRef.getFile(any(File::class.java))).thenReturn(mockDownload)
    `when`(mockDownload.isSuccessful).thenReturn(true)

    imageStorage.getImage(testImageDirectoryUID, testName, ImageDirectoryType.USER, onSuccess = {}, onFailure = {})

    verify(mockDownload).addOnCompleteListener(onCompleteFileDownloadTaskListenerCaptor.capture())

    onCompleteFileDownloadTaskListenerCaptor.value.onComplete(mockDownload)

    verify(mockImageRef).getFile(any(File::class.java))
  }

  @Test
  fun getImageFailure() {
    `when`(mockImageRef.getFile(any(File::class.java))).thenReturn(mockDownload)
    val exceptionMessage = "Image download from Firebase storage has failed or image does not exist"
    `when`(mockDownload.isSuccessful).thenReturn(false)
    `when`(mockDownload.exception).thenReturn(Exception(exceptionMessage))

    imageStorage.getImage(
      testImageDirectoryUID,
      testName,
        ImageDirectoryType.USER,
        onSuccess = { imageBitmap -> assertNotNull(imageBitmap) },
        onFailure = { e ->
          assertNotNull(e)
          assertEquals(exceptionMessage, e.message)
        })

    verify(mockDownload).addOnCompleteListener(onCompleteFileDownloadTaskListenerCaptor.capture())

    onCompleteFileDownloadTaskListenerCaptor.value.onComplete(mockDownload)
  }
}
