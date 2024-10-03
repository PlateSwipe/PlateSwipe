// MainActivity.kt
package com.android.sample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), BarcodeResultHandler {
    private lateinit var cameraExecutor: ExecutorService
    private val barcodeValueLiveData = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions.launch(arrayOf(Manifest.permission.CAMERA))
        }

        // Observe the barcode value and update the TextView
        val barcodeTextView = findViewById<TextView>(R.id.barcodeTextView)
        barcodeValueLiveData.observe(this, Observer { barcodeValue ->
            barcodeTextView.text = barcodeValue
        })
    }

    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true) {
            startCamera()
        } else {
            // Handle permission denial
        }
    }

    private fun allPermissionsGranted() = arrayOf(Manifest.permission.CAMERA).all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            CameraUtils.startCamera(this, cameraExecutor, null, cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onBarcodeDetected(barcodeValue: String) {
        runOnUiThread {
            barcodeValueLiveData.value = barcodeValue
        }
    }
}