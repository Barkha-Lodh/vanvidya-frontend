package com.vanvidya.app.ui.identify

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
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.vanvidya.app.R
import com.vanvidya.app.data.network.PlantIdService
import com.vanvidya.app.data.network.PlantApiService
import com.vanvidya.app.ui.details.PlantDetailsActivity
import kotlinx.coroutines.launch
import java.io.InputStream
import android.Manifest
import org.json.JSONObject

class MushroomIdentifyFragment : Fragment() {

    private lateinit var takePhotoButton: MaterialButton
    private lateinit var selectPhotoButton: MaterialButton
    private lateinit var identifyButton: MaterialButton
    private lateinit var imagePreview: ImageView
    private lateinit var resultText: TextView
    private lateinit var progressBar: ProgressBar

    private val plantIdService = PlantIdService()
    private val plantApiService = PlantApiService.create()
    private var selectedBitmap: Bitmap? = null

    private val CAMERA_REQUEST = 1
    private val GALLERY_REQUEST = 2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mushroom_identify, container, false)
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
        identifyButton.setOnClickListener { identifyMushroom() }
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

    private fun identifyMushroom() {
        val bitmap = selectedBitmap ?: return

        lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE
                identifyButton.isEnabled = false
                resultText.text = "Identifying mushroom..."
                resultText.visibility = View.VISIBLE

                // Step 1: Identify mushroom name using mushroom.kindwise.com
                val result = plantIdService.identifyMushroom(bitmap)

                if (result.error != null) {
                    progressBar.visibility = View.GONE
                    identifyButton.isEnabled = true
                    Toast.makeText(context, "Error: ${result.error}", Toast.LENGTH_LONG).show()
                    return@launch
                }

                if (result.suggestions.isEmpty()) {
                    progressBar.visibility = View.GONE
                    identifyButton.isEnabled = true
                    resultText.text = "No mushroom identified. Try a clearer photo."
                    return@launch
                }

                val top = result.suggestions[0]
                val confidence = (top.probability * 100).toInt()
                val mushroomName = top.commonName.ifEmpty { top.scientificName }

                // Step 2: Fetch full details from Django backend
                resultText.text = "Found: $mushroomName\nFetching details..."

                try {
                    val detailsResponse = plantApiService.getPlantInfo(mushroomName)

                    progressBar.visibility = View.GONE
                    identifyButton.isEnabled = true

                    // Build full JSON with mushroom.id data + Django details
                    val plantJson = JSONObject().apply {
                        put("common_name", detailsResponse.common_name.ifEmpty { mushroomName })
                        put("hindi_name", detailsResponse.hindi_name ?: "")
                        put("scientific_name", top.scientificName.ifEmpty {
                            detailsResponse.scientific_name
                        })
                        put("family", detailsResponse.family)
                        put("description", detailsResponse.description)
                        put("image_url", detailsResponse.image_url ?: "")
                        put("wikipedia_url", detailsResponse.wikipedia_url ?: "")
                        put("watering", detailsResponse.watering ?: "")
                        put("sunlight", detailsResponse.sunlight ?: "")
                        put("soil_type", detailsResponse.soil_type ?: "")
                        put("indoor_outdoor", detailsResponse.indoor_outdoor ?: "")
                        put("edible", detailsResponse.edible ?: "")
                        put("toxic", detailsResponse.toxic ?: "")
                        put("warning", detailsResponse.warning ?: "")
                        put("fun_facts", detailsResponse.fun_facts ?: "")
                        put("origin", detailsResponse.origin ?: "")
                        put("growth_rate", detailsResponse.growth_rate ?: "")
                        // Add confidence from mushroom.id
                        put("confidence", "$confidence%")
                    }

                    // Open PlantDetailsActivity with full data
                    startActivity(
                        Intent(requireContext(), PlantDetailsActivity::class.java).apply {
                            putExtra("plant_json", plantJson.toString())
                        }
                    )

                } catch (detailsError: Exception) {
                    // If Django fetch fails, show basic info from mushroom.id
                    progressBar.visibility = View.GONE
                    identifyButton.isEnabled = true

                    val basicJson = JSONObject().apply {
                        put("common_name", mushroomName)
                        put("scientific_name", top.scientificName)
                        put("description", top.description.ifEmpty {
                            "A mushroom identified with $confidence% confidence."
                        })
                        put("warning", "⚠️ Never eat wild mushrooms without expert identification!")
                        put("fun_facts", "Identified by mushroom.id with $confidence% confidence.")
                    }

                    startActivity(
                        Intent(requireContext(), PlantDetailsActivity::class.java).apply {
                            putExtra("plant_json", basicJson.toString())
                        }
                    )
                }

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                identifyButton.isEnabled = true
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}