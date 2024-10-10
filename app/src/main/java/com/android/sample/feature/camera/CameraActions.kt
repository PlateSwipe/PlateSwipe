package com.android.sample.feature.camera

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.android.sample.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import android.os.Build

private const val LOG_TAG = "CAMERA_ACTIONS"
private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
/**
 * @author https://developer.android.com/codelabs/camerax-getting-started
 * @param lifecycleOwner
 * @param context
 * @param imageCapture
 * @param previewView
 *
 * @return Unit
 * @throws Exception
 *
 * Function to start camera
 */
fun startCamera(lifecycleOwner: LifecycleOwner,context: Context,imageCapture: ImageCapture, previewView: PreviewView) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({

        val cameraProvider = cameraProviderFuture.get()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        val preview = createPreview().also{ it.setSurfaceProvider(previewView.surfaceProvider) }
        try{
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        }catch(exc: Exception){
            Log.e(LOG_TAG, context.getString(R.string.use_case_binding_failed), exc)
        }

    }, ContextCompat.getMainExecutor(context))
}

/**
 * @author https://developer.android.com/codelabs/camerax-getting-started
 * @param context
 * @param imageCapture
 * @param onPhotoSaved
 *
 * @return Unit
 * @throws ImageCaptureException
 *
 * Function to take photo
 */
fun takePhoto(context: Context, imageCapture: ImageCapture, onPhotoSaved: (ImageCapture.OutputFileResults) -> Unit) {

    // Create time stamped name and MediaStore entry.
    val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
        .format(System.currentTimeMillis())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }
    }

    // Create output options object which contains file + metadata
    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues)
        .build()

    // Set up image capture listener, which is triggered after photo has
    // been taken
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e(LOG_TAG, "Photo capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults){
                onPhotoSaved(output)
            }
        }
    )
}