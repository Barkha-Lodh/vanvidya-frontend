package com.vanvidya.app.ui.search

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.vanvidya.app.R
import com.vanvidya.app.data.PlantRepository
import com.vanvidya.app.ui.details.PlantDetailsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class PlantListActivity : AppCompatActivity() {

    private lateinit var repository: PlantRepository
    private lateinit var backButton: ImageView
    private lateinit var categoryTitle: TextView

    private lateinit var plant1Card: CardView
    private lateinit var plant2Card: CardView
    private lateinit var plant3Card: CardView
    private lateinit var plant4Card: CardView
    private lateinit var plant5Card: CardView
    private lateinit var plant6Card: CardView
    private lateinit var plant7Card: CardView
    private lateinit var plant8Card: CardView

    // ✅ Correctly declared TextViews (Fixed copy-paste bug here!)
    private lateinit var plant1Name: TextView
    private lateinit var plant2Name: TextView
    private lateinit var plant3Name: TextView
    private lateinit var plant4Name: TextView
    private lateinit var plant5Name: TextView
    private lateinit var plant6Name: TextView
    private lateinit var plant7Name: TextView
    private lateinit var plant8Name: TextView

    private var plantNames = arrayOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_list)

        repository = PlantRepository(this)

        // Get data passed from PlantSearchActivity
        val title = intent.getStringExtra("category_title") ?: "Plants"
        plantNames = intent.getStringArrayExtra("plant_names") ?: arrayOf()

        initializeViews()
        categoryTitle.text = title
        setupPlantCards()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.back_button)
        categoryTitle = findViewById(R.id.category_title)

        plant1Card = findViewById(R.id.plant_1_card)
        plant2Card = findViewById(R.id.plant_2_card)
        plant3Card = findViewById(R.id.plant_3_card)
        plant4Card = findViewById(R.id.plant_4_card)
        plant5Card = findViewById(R.id.plant_5_card)
        plant6Card = findViewById(R.id.plant_6_card)
        plant7Card = findViewById(R.id.plant_7_card)
        plant8Card = findViewById(R.id.plant_8_card)

        plant1Name = findViewById(R.id.plant_1_name)
        plant2Name = findViewById(R.id.plant_2_name)
        plant3Name = findViewById(R.id.plant_3_name)
        plant4Name = findViewById(R.id.plant_4_name)
        plant5Name = findViewById(R.id.plant_5_name)
        plant6Name = findViewById(R.id.plant_6_name)
        plant7Name = findViewById(R.id.plant_7_name)
        plant8Name = findViewById(R.id.plant_8_name)

        backButton.setOnClickListener { finish() }
    }

    private fun setupPlantCards() {
        val cards = listOf(plant1Card, plant2Card, plant3Card, plant4Card, plant5Card, plant6Card, plant7Card, plant8Card)

        val names = listOf(plant1Name, plant2Name, plant3Name, plant4Name, plant5Name, plant6Name, plant7Name, plant8Name)


        cards.forEachIndexed { index, card ->
            if (index < plantNames.size) {
                card.visibility = android.view.View.VISIBLE
                names[index].text = plantNames[index]
                card.setOnClickListener {
                    searchPlantByName(plantNames[index])
                }
            } else {
                card.visibility = android.view.View.GONE
            }
        }
    }

    private fun searchPlantByName(name: String) {
        lifecycleScope.launch {
            try {
                Toast.makeText(this@PlantListActivity, "Loading $name...", Toast.LENGTH_SHORT).show()

                val result = withContext(Dispatchers.IO) {
                    repository.getPlantInfo(name)
                }

                if (result.data != null) {
                    val plant = result.data

                    val plantJson = JSONObject().apply {
                        put("common_name", plant.commonName)
                        put("scientific_name", plant.scientificName)
                        put("family", plant.family)
                        put("hindi_name", plant.hindiName ?: "")
                        put("description", plant.description)
                        put("image_url", plant.imageUrl ?: "")
                        put("watering", plant.watering ?: "")
                        put("sunlight", plant.sunlight ?: "")
                        put("soil_type", plant.soilType ?: "")
                        put("indoor_outdoor", plant.indoorOutdoor ?: "")
                        put("edible", plant.edible ?: "")
                        put("toxic", plant.toxic ?: "")
                        put("warning", plant.warning ?: "")
                        put("fun_facts", plant.funFacts ?: "")
                        put("origin", plant.origin ?: "")
                        put("growth_rate", plant.growthRate ?: "")
                        val diseasesArray = JSONArray()
                        plant.diseases?.forEach { disease ->
                            diseasesArray.put(JSONObject().apply {
                                put("name", disease.name)
                                put("symptom", disease.symptom)
                                put("treatment", disease.treatment)
                            })
                        }
                        put("diseases", diseasesArray)
                    }

                    startActivity(
                        Intent(this@PlantListActivity, PlantDetailsActivity::class.java).apply {
                            putExtra("plant_json", plantJson.toString())
                            putExtra("category", "plant")
                        }
                    )

                } else {
                    Toast.makeText(this@PlantListActivity,
                        result.message ?: "Plant not found", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@PlantListActivity,
                    "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}