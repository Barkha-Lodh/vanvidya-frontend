package com.vanvidya.app.ui.collection

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vanvidya.app.R
import com.vanvidya.app.data.local.AppDatabase
import com.vanvidya.app.data.local.PlantEntity
import com.vanvidya.app.ui.details.PlantDetailsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class CollectionListActivity : AppCompatActivity() {

    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_list)

        val type = intent.getStringExtra("type") ?: "plant"

        findViewById<View>(R.id.back_button)?.setOnClickListener { finish() }

        findViewById<TextView>(R.id.collection_title)?.text = "My Collection"

        loadFromRoom(type)
    }

    private fun loadFromRoom(type: String) {
        val container = findViewById<LinearLayout>(R.id.container)

        lifecycleScope.launch {
            val plants = withContext(Dispatchers.IO) {
                db.plantDao().getPlantsByCategory(type)
            }

            container.removeAllViews()

            if (plants.isEmpty()) {
                showEmptyState(container, type)
            } else {
                plants.forEach { plant ->
                    addPlantCard(container, plant, type)
                }
            }
        }
    }

    private fun showEmptyState(container: LinearLayout, type: String) {
        val emptyText = TextView(this).apply {
            text = when (type) {
                "plant" -> "No plants saved yet.\nScan or search a plant and tap '➕ Add to Collection'!"
                "flower" -> "No flowers saved yet.\nScan a flower and tap '➕ Add to Collection'!"
                "mushroom" -> "No mushrooms saved yet.\nScan a mushroom and tap '➕ Add to Collection'!"
                else -> "Collection is empty"
            }
            textSize = 16f
            setPadding(32, 64, 32, 32)
            gravity = android.view.Gravity.CENTER
            setTextColor(android.graphics.Color.parseColor("#888888"))
        }
        container.addView(emptyText)
    }

    private fun addPlantCard(container: LinearLayout, plant: PlantEntity, category: String) {
        val card = layoutInflater.inflate(R.layout.item_collection_card, container, false)

        card.findViewById<TextView>(R.id.item_name)?.text = plant.commonName

        val hindiText = card.findViewById<TextView>(R.id.item_hindi_name)
        if (plant.hindiName.isNotEmpty()) {
            hindiText?.text = plant.hindiName
            hindiText?.visibility = View.VISIBLE
        } else {
            hindiText?.visibility = View.GONE
        }

        card.findViewById<TextView>(R.id.item_scientific_name)?.text = plant.scientificName

        card.setOnClickListener {
            val plantJson = plantEntityToJson(plant)
            startActivity(
                Intent(this, PlantDetailsActivity::class.java).apply {
                    putExtra("plant_json", plantJson.toString())
                    putExtra("category", category)
                }
            )
        }

        container.addView(card)
    }

    private fun plantEntityToJson(plant: PlantEntity): JSONObject {
        return JSONObject().apply {
            put("common_name", plant.commonName)
            put("hindi_name", plant.hindiName)
            put("scientific_name", plant.scientificName)
            put("family", plant.family)
            put("description", plant.description)
            put("image_url", plant.imageUrl ?: "")
            put("watering", plant.watering)
            put("sunlight", plant.sunlight)
            put("soil_type", plant.soilType)
            put("indoor_outdoor", plant.indoorOutdoor)
            put("edible", plant.edible)
            put("toxic", plant.toxic)
            put("warning", plant.warning)
            put("fun_facts", plant.funFacts)
            put("origin", plant.origin)
            put("growth_rate", plant.growthRate)
            try {
                put("diseases", JSONArray(plant.diseasesJson))
            } catch (e: Exception) {
                put("diseases", JSONArray())
            }
        }
    }
}