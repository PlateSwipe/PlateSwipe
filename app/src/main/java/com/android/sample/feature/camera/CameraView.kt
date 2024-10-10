package com.android.sample.feature.camera

import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.compose.runtime.Composable
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.view.PreviewView
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import java.io.File


@Composable
fun CameraView(

) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val imageCapture = createImageCapture()


    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize().testTag("camera_preview")) {
            startCamera(lifecycleOwner,context, imageCapture, previewView)
        }

        Button(
            onClick = { takePhoto(context, imageCapture,
                onPhotoSaved = {output ->
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                })
                      },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(80.dp)
                .padding(bottom = 20.dp).testTag("capture_button"),
            shape = CircleShape,
            colors = ButtonColors(
                containerColor = Color.White,
                contentColor = Color.Black,
                disabledContentColor = Color.Gray,
                disabledContainerColor = Color.Gray
            )
        ) {
            Text("Capture Photo", modifier = Modifier.testTag("capture_button_text"))
        }
    }
}