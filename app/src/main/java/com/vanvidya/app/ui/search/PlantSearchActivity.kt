package com.vanvidya.app.ui.search

import android.R.attr.name
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import com.vanvidya.app.R
import com.vanvidya.app.data.PlantRepository
import com.vanvidya.app.ui.details.PlantDetailsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import kotlin.jvm.java

class PlantSearchActivity : AppCompatActivity() {

    private var hasSetupListeners = false
    private lateinit var repository: PlantRepository
    private lateinit var backButton: ImageView
    private lateinit var searchInput: EditText
    private lateinit var allPlantsCard: CardView
    private lateinit var climbPlantsCard: CardView
    private lateinit var fernsCard: CardView
    private lateinit var conifersCard: CardView
    private lateinit var treesCard: CardView
    private lateinit var herbsCard: CardView
    private lateinit var cactiCard: CardView
    private lateinit var flowersCard: CardView

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_search)

        try {
            repository = PlantRepository(this)
            initializeViews()
            setupListeners()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.back_button)
        searchInput = findViewById(R.id.search_input)
        allPlantsCard = findViewById(R.id.all_plants_card)
        climbPlantsCard = findViewById(R.id.climb_plants_card)
        fernsCard = findViewById(R.id.ferns_card)
        conifersCard = findViewById(R.id.conifers_card)
        treesCard = findViewById(R.id.trees_card)
        herbsCard = findViewById(R.id.herbs_card)
        cactiCard = findViewById(R.id.cacti_card)
        flowersCard = findViewById(R.id.flowers_card)
        searchInput.requestFocus()
    }

    private fun setupListeners() {
        if (hasSetupListeners) return
        hasSetupListeners = true

        backButton.setOnClickListener { finish() }

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchInput.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchPlantByName(query)
                } else {
                    Toast.makeText(this, "Please enter plant name", Toast.LENGTH_SHORT).show()
                }
                true
            } else false
        }

        // ✅ Now opens PlantListActivity with cards instead of AlertDialog

        allPlantsCard.setOnClickListener {
            openPlantList("Popular Plants",
                arrayOf("Hibiscus", "Bamboo", "Peace Lily", "Stropharia rugosoannulata", "Sunflower", "Jade Plant", "Ficus religiosa", "Rubber fig"))
        }

        climbPlantsCard.setOnClickListener {
            openPlantList("Climbing Plants", arrayOf("Ivy", "Bougainvillea", "Jasmine", "Pothos (plant)", "Epipremnum aureum", "Ipomoea cairica", "Combretum indicum", "Pyrostegia venusta"))
        }

        fernsCard.setOnClickListener {
            openPlantList("Ferns", arrayOf("Nephrolepis exaltata", "Maidenhair Fern", "Asplenium nidus", "Staghorn Fern"))
        }

        conifersCard.setOnClickListener {
            openPlantList("Conifers", arrayOf("Pine", "Cedrus", "Cupressus sempervirens", "Juniper", "Taxus", "Abies koreana", "Picea glauca"))
        }

        treesCard.setOnClickListener {
            openPlantList("Trees", arrayOf("Oak", "Maple", "Banyan", "Neem", "Platanus occidentalis", "Date Palm", "Coconut", "Sandalwood"))
        }

        herbsCard.setOnClickListener {
            openPlantList("Herbs", arrayOf("Basil", "Mentha", "Coriander", "Tulsi", "Curry tree", "Rosemary", "Ginger", "Ashwagandha"))
        }

        cactiCard.setOnClickListener {
            openPlantList("Cacti & Succulents", arrayOf("Cactus", "Aloe Vera", "Jade Plant", "Echeveria", "Kalanchoe", "Cleistocactus strausii", "Opuntia", "Aeonium"))
        }

        flowersCard.setOnClickListener {
            openPlantList("Flowers", arrayOf("Tulip", "Orchid", "Tagetes", "Bellis perennis", "Lavender", "Dahlia", "chrysanthemum", "Dahlia")) }
    }

    // ✅ Opens PlantListActivity with plant cards

    private fun openPlantList(title: String, plants: Array<String>) {
        val intent = Intent(this, PlantListActivity::class.java).apply{
            putExtra("category_title", title)
            putExtra("plant_names", plants)
        }
        startActivity(intent)
    }

    private fun searchPlantByName(name: String) {
        lifecycleScope.launch {
            try {
                Toast.makeText(this@PlantSearchActivity, "Searching for $name...", Toast.LENGTH_SHORT).show()

                val result = withContext(Dispatchers.IO){
                    repository.getPlantInfo(name)
                }
                if (result.data != null) {
                    val plant = result.data

                    // Create JSON with ALL backend data
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
                        put("growth_rate", plant.growthRate ?:
                        "")

                        // Add diseases array
                        val diseasesArray = JSONArray()
                        plant.diseases?.forEach { disease -> diseasesArray.put(JSONObject().apply {
                            put("name", disease.name)
                            put("symptom", disease.symptom)
                            put("treatment", disease.treatment)
                            })
                        }
                            put("diseases", diseasesArray)
                        }

                        startActivity(
                            Intent(this@PlantSearchActivity, PlantDetailsActivity::class.java).apply {
                                putExtra("plant_json", plantJson.toString())
                            }
                        )
                    } else {
                        Toast.makeText(this@PlantSearchActivity,
                            result.message ?: "Plant not found",
                            Toast.LENGTH_LONG).show()
                    }
            } catch (e: Exception) {
                Toast.makeText(this@PlantSearchActivity,
                    "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}