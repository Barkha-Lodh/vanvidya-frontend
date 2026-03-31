package com.vanvidya.app.ui.ar

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.vanvidya.app.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class plantPlacementActivity : AppCompatActivity() {

    private lateinit var viewFinder: PreviewView
    private lateinit var instructionText: TextView
    private lateinit var scanButton: Button
    private lateinit var cameraExecutor: ExecutorService

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_placement)

        viewFinder = findViewById(R.id.view_finder)
        instructionText = findViewById(R.id.instruction_text)
        scanButton = findViewById(R.id.scan_button)

        // Set instruction text
        instructionText.text = "Position your phone at the spot where you want to place your plant"

        cameraExecutor = Executors.newSingleThreadExecutor()

        // Check camera permission
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Scan button click
        scanButton.setOnClickListener {
            analyzePlacement()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview
                )
            } catch (exc: Exception) {
                Toast.makeText(this, "Camera initialization failed: ${exc.message}", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun analyzePlacement() {
        // Simulate analysis
        Toast.makeText(this, "Analyzing placement spot...", Toast.LENGTH_SHORT).show()

        // Show results after delay
        scanButton.postDelayed({
            showPlacementResults()
        }, 2000)
    }

    private fun showPlacementResults() {
        // Simulate light and space analysis
        val lightLevel = (60..95).random()
        val spaceQuality = (70..95).random()

        val message = buildString {
            append("Placement Analysis:\n\n")
            append("☀️ Light Level: $lightLevel%\n")
            append("📏 Space Quality: $spaceQuality%\n\n")
            if (lightLevel > 70 && spaceQuality > 75) {
                append("✅ Excellent spot for your plant!")
            } else {
                append("⚠️ Consider finding a brighter location")
            }
        }

        android.app.AlertDialog.Builder(this)
            .setTitle("Placement Results")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> finish() }
            .show()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}