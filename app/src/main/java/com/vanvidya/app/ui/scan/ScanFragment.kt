package com.vanvidya.app.ui.scan

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.vanvidya.app.R
import com.vanvidya.app.data.remote.RetrofitClient
import com.vanvidya.app.ui.details.PlantDetailsActivity
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import android.Manifest

class ScanFragment : Fragment() {

    private lateinit var takePhotoButton: MaterialButton
    private lateinit var selectPhotoButton: MaterialButton
    private lateinit var identifyButton: MaterialButton
    private lateinit var imagePreview: ImageView
    private lateinit var resultText: TextView
    private lateinit var progressBar: ProgressBar

    private var selectedBitmap: Bitmap? = null

    private val CAMERA_REQUEST = 1
    private val GALLERY_REQUEST = 2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.CAMERA), 100)
        }

        takePhotoButton = view.findViewById(R.id.take_photo_button)
        selectPhotoButton = view.findViewById(R.id.select_photo_button)
        identifyButton = view.findViewById(R.id.identify_button)
        imagePreview = view.findViewById(R.id.image_preview)
        resultText = view.findViewById(R.id.result_text)
        progressBar = view.findViewById(R.id.progress_bar)

        takePhotoButton.setOnClickListener { openCamera() }
        selectPhotoButton.setOnClickListener { openGallery() }
        identifyButton.setOnClickListener { identifyPlant() }
        identifyButton.isEnabled = false
    }

    private fun openCamera() {
        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST)
    }

    private fun openGallery() {
        startActivityForResult(
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
            GALLERY_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST -> {
                    (data?.extras?.get("data") as? Bitmap)?.let { displayImage(it) }
                }
                GALLERY_REQUEST -> {
                    data?.data?.let { uri ->
                        uriToBitmap(uri)?.let { displayImage(it) }
                    }
                }
            }
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            BitmapFactory.decodeStream(
                requireContext().contentResolver.openInputStream(uri)
            )
        } catch (e: Exception) { null }
    }

    private fun displayImage(bitmap: Bitmap) {
        selectedBitmap = bitmap
        imagePreview.setImageBitmap(bitmap)
        imagePreview.visibility = View.VISIBLE
        identifyButton.isEnabled = true
        resultText.visibility = View.GONE
    }

    private fun identifyPlant() {
        val bitmap = selectedBitmap ?: return

        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                identifyButton.isEnabled = false
                resultText.text = "Identifying plant..."
                resultText.visibility = View.VISIBLE

                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                val byteArray = stream.toByteArray()

                val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData(
                    "image", "plant.jpg", requestBody
                )

                // ✅ Call Django identify-leaf endpoint
                val response = RetrofitClient.apiService.identifyLeaf(imagePart)

                progressBar.visibility = View.GONE
                identifyButton.isEnabled = true

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    val plant = result.plantDetails

                    // ✅ Build full JSON and open PlantDetailsActivity
                    val plantJson = JSONObject().apply {
                        put("common_name", plant.commonName)
                        put("hindi_name", plant.hindiName ?: "")
                        put("scientific_name", plant.scientificName)
                        put("family", plant.family)
                        put("description", plant.description)
                        put("image_url", plant.imageUrl ?: "")
                        put("wikipedia_url", plant.wikipediaUrl ?: "")
                        put("watering", plant.watering)
                        put("sunlight", plant.sunlight)
                        put("soil_type", plant.soilType)
                        put("indoor_outdoor", plant.indoorOutdoor)
                        put("edible", plant.edible)
                        put("toxic", plant.toxic)
                        put("warning", plant.warning ?: "")
                        put("fun_facts", plant.funFacts)
                        put("origin", plant.origin)
                        put("growth_rate", plant.growthRate)
                    }

                    // ✅ Open PlantDetailsActivity with full data
                    startActivity(
                        Intent(requireContext(), PlantDetailsActivity::class.java).apply {
                            putExtra("plant_json", plantJson.toString())
                        }
                    )

                } else {
                    resultText.text = "Could not identify plant. Try a clearer photo."
                    resultText.visibility = View.VISIBLE
                    Toast.makeText(context, "Failed: ${response.code()}", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                identifyButton.isEnabled = true
                resultText.text = "Error: ${e.message}"
                resultText.visibility = View.VISIBLE
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}