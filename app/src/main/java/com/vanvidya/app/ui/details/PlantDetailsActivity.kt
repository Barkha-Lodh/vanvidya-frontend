package com.vanvidya.app.ui.details

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.vanvidya.app.R
import com.vanvidya.app.data.local.AppDatabase
import com.vanvidya.app.data.local.PlantEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class PlantDetailsActivity : AppCompatActivity() {

    private lateinit var addToCollectionBtn: MaterialButton
    private lateinit var backButton: ImageView
    private lateinit var plantImage: ImageView
    private lateinit var plantNameText: TextView
    private lateinit var hindiNameText: TextView
    private lateinit var scientificNameText: TextView
    private lateinit var familyText: TextView
    private lateinit var originText: TextView
    private lateinit var growthRateText: TextView
    private lateinit var indoorOutdoorText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var waterText: TextView
    private lateinit var lightText: TextView
    private lateinit var soilText: TextView
    private lateinit var temperatureText: TextView
    private lateinit var edibleText: TextView
    private lateinit var toxicText: TextView
    private lateinit var warningCard: View
    private lateinit var warningText: TextView
    private lateinit var funFactsText: TextView
    private lateinit var diseasesCard: View
    private lateinit var diseasesContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_details)

        initializeViews()

        val plantJson = intent.getStringExtra("plant_json")
        if (plantJson != null) {
            displayFromJson(JSONObject(plantJson))
        } else {
            displayFromExtras()
        }
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.back_button)
        plantImage = findViewById(R.id.plant_image)
        plantNameText = findViewById(R.id.plant_name)
        hindiNameText = findViewById(R.id.hindi_name)
        scientificNameText = findViewById(R.id.scientific_name)
        familyText = findViewById(R.id.family_text)
        originText = findViewById(R.id.origin_text)
        growthRateText = findViewById(R.id.growth_rate_text)
        indoorOutdoorText = findViewById(R.id.indoor_outdoor_text)
        descriptionText = findViewById(R.id.description_text)
        waterText = findViewById(R.id.water_text)
        lightText = findViewById(R.id.light_text)
        soilText = findViewById(R.id.soil_text)
        temperatureText = findViewById(R.id.temperature_text)
        edibleText = findViewById(R.id.edible_text)
        toxicText = findViewById(R.id.toxic_text)
        warningCard = findViewById(R.id.warning_card)
        warningText = findViewById(R.id.warning_text)
        funFactsText = findViewById(R.id.fun_facts_text)
        diseasesCard = findViewById(R.id.diseases_card)
        diseasesContainer = findViewById(R.id.diseases_container)
        addToCollectionBtn = findViewById(R.id.add_to_collection_btn)

        backButton.setOnClickListener { finish() }
    }

    private fun displayFromJson(plant: JSONObject) {
        val plantName = plant.optString("common_name", "Unknown")
        val category = intent.getStringExtra("category")
            ?: plant.optString("type").ifEmpty { "plant" }
        val db = AppDatabase.getDatabase(this)

        // 1. Keep track of the state locally
        var isSaved = false

        // 2. Check the initial state on load
        lifecycleScope.launch {
            val existing = withContext(Dispatchers.IO) {
                db.plantDao().getPlantByNameAndCategory(plantName, category)
            }
            if (existing != null) {
                isSaved = true
                addToCollectionBtn.text = "➖ Remove from Collection"
                addToCollectionBtn.setBackgroundColor(android.graphics.Color.parseColor("#D32F2F")) // Red
            } else {
                isSaved = false
                addToCollectionBtn.text = "➕ Add to Collection"
                addToCollectionBtn.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50")) // Green
            }
        }

        // 3. Handle toggling on click
        addToCollectionBtn.setOnClickListener {
            lifecycleScope.launch {
                if (isSaved) {
                    // REMOVE FROM COLLECTION
                    withContext(Dispatchers.IO) {
                        db.plantDao().deleteByNameAndCategory(plantName, category)
                    }
                    isSaved = false
                    addToCollectionBtn.text = "➕ Add to Collection"
                    addToCollectionBtn.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50")) // Green
                    Toast.makeText(this@PlantDetailsActivity, "Removed from collection!", Toast.LENGTH_SHORT).show()
                } else {
                    // ADD TO COLLECTION
                    val entity = PlantEntity(
                        commonName = plantName,
                        hindiName = plant.optString("hindi_name", ""),
                        scientificName = plant.optString("scientific_name", ""),
                        family = plant.optString("family", ""),
                        description = plant.optString("description", ""),
                        imageUrl = plant.optString("image_url", "").ifEmpty { null },
                        watering = plant.optString("watering", ""),
                        sunlight = plant.optString("sunlight", ""),
                        soilType = plant.optString("soil_type", ""),
                        indoorOutdoor = plant.optString("indoor_outdoor", ""),
                        edible = plant.optString("edible", ""),
                        toxic = plant.optString("toxic", ""),
                        warning = plant.optString("warning", ""),
                        funFacts = plant.optString("fun_facts", ""),
                        origin = plant.optString("origin", ""),
                        growthRate = plant.optString("growth_rate", ""),
                        diseasesJson = plant.optJSONArray("diseases")?.toString() ?: "[]",
                        category = category
                    )

                    withContext(Dispatchers.IO) { db.plantDao().insertPlant(entity) }

                    isSaved = true
                    addToCollectionBtn.text = "➖ Remove from Collection"
                    addToCollectionBtn.setBackgroundColor(android.graphics.Color.parseColor("#D32F2F")) // Red
                    Toast.makeText(this@PlantDetailsActivity, "Added to collection!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Image
        val imageUrl = plant.optString("image_url", "")
        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(plantImage)
        }

        // Names
        plantNameText.text = plantName

        val hindiName = plant.optString("hindi_name", "")
        if (hindiName.isNotEmpty()) {
            hindiNameText.text = "($hindiName)"
            hindiNameText.visibility = View.VISIBLE
        }

        scientificNameText.text = plant.optString("scientific_name", "—")
        familyText.text = "Family: ${plant.optString("family", "—")}"

        originText.text = "📍 ${plant.optString("origin", "—")}"
        growthRateText.text = "📈 Growth: ${plant.optString("growth_rate", "—")}"

        val indoorOutdoor = plant.optString("indoor_outdoor", "")
        if (indoorOutdoor.isNotEmpty()) {
            indoorOutdoorText.text = indoorOutdoor
            indoorOutdoorText.visibility = View.VISIBLE
        }

        // Description
        descriptionText.text = plant.optString("description", "—")

        // Care
        waterText.text = plant.optString("watering", "—")
        lightText.text = plant.optString("sunlight", "—")
        soilText.text = plant.optString("soil_type", "—")

        // Temperature
        val temp = plant.optString("temperature_range", "")
        temperatureText.text = if (temp.isNotEmpty()) temp else "—"

        // Edible & Toxic
        edibleText.text = plant.optString("edible", "—")
        toxicText.text = plant.optString("toxic", "—")

        // Warning (only show card if warning exists)
        val warning = plant.optString("warning", "")
        if (warning.isNotEmpty()) {
            warningText.text = warning
            warningCard.visibility = View.VISIBLE
        }

        // Fun Facts
        funFactsText.text = plant.optString("fun_facts", "—")

        // Diseases
        val diseases = plant.optJSONArray("diseases")
        if (diseases != null && diseases.length() > 0) {
            diseasesCard.visibility = View.VISIBLE
            diseasesContainer.removeAllViews()

            for (i in 0 until diseases.length()) {
                val disease = diseases.getJSONObject(i)
                val name = disease.optString("name", "Unknown")
                val symptom = disease.optString("symptom", "—")
                val treatment = disease.optString("treatment", "—")

                val block = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(0, 0, 0, 16)
                }

                block.addView(TextView(this).apply {
                    text = "🔴 $name"
                    textSize = 15f
                    setTextColor(android.graphics.Color.parseColor("#D32F2F"))
                    setTypeface(null, android.graphics.Typeface.BOLD)
                })
                block.addView(TextView(this).apply {
                    text = "🔍 Symptom: $symptom"
                    textSize = 13f
                    setTextColor(android.graphics.Color.parseColor("#555555"))
                    setPadding(0, 4, 0, 0)
                })
                block.addView(TextView(this).apply {
                    text = "💊 Treatment: $treatment"
                    textSize = 13f
                    setTextColor(android.graphics.Color.parseColor("#2E7D32"))
                    setPadding(0, 4, 0, 0)
                })

                if (i < diseases.length() - 1) {
                    block.addView(View(this).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 1
                        ).apply { topMargin = 12 }
                        setBackgroundColor(android.graphics.Color.parseColor("#E0E0E0"))
                    })
                }

                diseasesContainer.addView(block)
            }
        }
    }

    private fun displayFromExtras() {
        plantNameText.text = intent.getStringExtra("common_name") ?: "Unknown"
        scientificNameText.text = intent.getStringExtra("scientific_name") ?: "—"
        familyText.text = "Family: ${intent.getStringExtra("family") ?: "—"}"
        descriptionText.text = intent.getStringExtra("description") ?: "—"
        waterText.text = intent.getStringExtra("water_requirements") ?: "—"
        lightText.text = intent.getStringExtra("light_requirements") ?: "—"
        temperatureText.text = intent.getStringExtra("temperature_range") ?: "—"

        val hindiName = intent.getStringExtra("hindi_name") ?: ""
        if (hindiName.isNotEmpty()) {
            hindiNameText.text = "($hindiName)"
            hindiNameText.visibility = View.VISIBLE
        }

        val imageUrl = intent.getStringExtra("image_url") ?: ""
        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(plantImage)
        }

        val care = intent.getStringExtra("care_instructions") ?: ""
        if (care.isNotEmpty()) soilText.text = care
    }
}