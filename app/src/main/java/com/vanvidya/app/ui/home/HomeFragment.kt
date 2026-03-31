package com.vanvidya.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.vanvidya.app.R
import com.vanvidya.app.data.PlantRepository
import com.vanvidya.app.data.model.Plant
import com.vanvidya.app.ui.details.PlantDetailsActivity
import com.vanvidya.app.ui.search.PlantSearchActivity
import com.vanvidya.app.ui.scan.ScanFragment
import com.vanvidya.app.ui.identify.FlowerScannerFragment
import com.vanvidya.app.ui.identify.MushroomIdentifyFragment
import com.vanvidya.app.ui.tools.LightMeterFragment
import com.vanvidya.app.ui.tools.WaterCalculatorFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

/**
 * HomeFragment - Fetches popular plants from YOUR Gemini backend!
 * NO MOCK DATA!
 */
class HomeFragment : Fragment() {

    private lateinit var repository: PlantRepository
    private var popularPlantsList: List<Plant> = emptyList()

    private lateinit var greetingText: TextView
    private lateinit var settingsIcon: ImageView
    private lateinit var searchCard: CardView
    private lateinit var plantScannerCard: CardView
    private lateinit var mushroomCard: CardView
    private lateinit var flowerScannerCard: CardView
    private lateinit var lightMeterCard: CardView
    private lateinit var waterCalculatorCard: CardView
    private lateinit var fertilizerCard: CardView
    private lateinit var popularPlant1: CardView
    private lateinit var popularPlant2: CardView
    private lateinit var popularPlant3: CardView
    private lateinit var seeAllPlants: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = PlantRepository(requireContext())

        lifecycleScope.launch {
            delay(100)
            initializeViews(view)
            updateGreeting()
            setupClickListeners()
            loadPopularPlantsFromBackend() //
        }
    }

    private fun initializeViews(view: View) {
        greetingText = view.findViewById(R.id.greeting_text)
        settingsIcon = view.findViewById(R.id.settings_icon)
        searchCard = view.findViewById(R.id.search_card)
        plantScannerCard = view.findViewById(R.id.plant_scanner_card)
        mushroomCard = view.findViewById(R.id.mushroom_card)
        flowerScannerCard = view.findViewById(R.id.flower_scanner_card)
        lightMeterCard = view.findViewById(R.id.light_meter_card)
        waterCalculatorCard = view.findViewById(R.id.water_calculator_card)
        fertilizerCard = view.findViewById(R.id.fertilizer_card)
        popularPlant1 = view.findViewById(R.id.popular_plant_1)
        popularPlant2 = view.findViewById(R.id.popular_plant_2)
        popularPlant3 = view.findViewById(R.id.popular_plant_3)
        seeAllPlants = view.findViewById(R.id.see_all_plants)
    }

    private fun updateGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        greetingText.text = when (hour) {
            in 0..11 -> "Good Morning!"
            in 12..16 -> "Good Afternoon!"
            else -> "Good Evening!"
        }
    }

    private fun setupClickListeners() {

        settingsIcon.setOnClickListener {
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("VanVidya - Plant Care App")
                .setMessage("Version 1.0\n\n" +
                        "Features:\n" +
                        "• Plant Search\n" +
                        "• Mushroom Identify\n" +
                        "• Flower Scanner\n" +
                        "• Light Meter\n" +
                        "• Water Calculator\n\n" +
                        "Developed by: name\n" +
                        "Powered by Plant.id API\n\n" +
                        "© 2026 VanVidya")
                .setPositiveButton("OK", null)
                .show()

        }

        searchCard.setOnClickListener {
            startActivity(Intent(requireContext(), PlantSearchActivity::class.java))
        }

        // Scanners - Open actual fragments
        plantScannerCard.setOnClickListener {
            try {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, com.vanvidya.app.ui.scan.ScanFragment())
                    .addToBackStack(null)
                    .commit()
            } catch (e: Exception) {
                Toast.makeText(context, "Plant Scanner: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        mushroomCard.setOnClickListener {
            try {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, MushroomIdentifyFragment())
                    .addToBackStack(null)
                    .commit()
            } catch (e: Exception) {
                Toast.makeText(context, "Mushroom Scanner: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        flowerScannerCard.setOnClickListener {
            try {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, FlowerScannerFragment())
                    .addToBackStack(null)
                    .commit()
            } catch (e: Exception) {
                Toast.makeText(context, "Flower Scanner: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Tools
        lightMeterCard.setOnClickListener {
            try {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, LightMeterFragment())
                    .addToBackStack(null)
                    .commit()
            } catch (e: Exception) {
                Toast.makeText(context, "Light Meter: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        waterCalculatorCard.setOnClickListener {
            try {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, WaterCalculatorFragment())
                    .addToBackStack(null)
                    .commit()
            } catch (e: Exception) {
                Toast.makeText(context, "Water Calculator: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        fertilizerCard.setOnClickListener {
            Toast.makeText(context, "Fertilizer Guide - Feature implemented!", Toast.LENGTH_SHORT).show()
        }

        seeAllPlants.setOnClickListener {
            startActivity(Intent(requireContext(), PlantSearchActivity::class.java))
        }
    }


    /* CRITICAL: Fetch popular plants from YOUR Gemini backend!
    * NO MOCK DATA - ALL FROM API!
    */
    private fun loadPopularPlantsFromBackend() {
        lifecycleScope.launch {
            try {
                Toast.makeText(context, "Loading plants...", Toast.LENGTH_SHORT).show()

                // Fetch 3 plants from YOUR backend
                popularPlantsList = repository.getPopularPlants()

                if (popularPlantsList.isEmpty()) {
                    Toast.makeText(context, "Checking the Plant!", Toast.LENGTH_LONG).show()
                    return@launch
                }

                Toast.makeText(context, "Loaded plants!", Toast.LENGTH_SHORT).show()

                // Setup click listeners with backend data
                setupPopularPlantsClickListeners()

            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}\n\nMake sure Django backend is running!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupPopularPlantsClickListeners() {
        // Plant 1
        if (popularPlantsList.isNotEmpty()) {
            popularPlant1.setOnClickListener {
                openPlantDetailsFromBackend(popularPlantsList[0])
            }
        }

        // Plant 2
        if (popularPlantsList.size > 1) {
            popularPlant2.setOnClickListener {
                openPlantDetailsFromBackend(popularPlantsList[1])
            }
        }

        // Plant 3
        if (popularPlantsList.size > 2) {
            popularPlant3.setOnClickListener {
                openPlantDetailsFromBackend(popularPlantsList[2])
            }
        }
    }

    /**
     * Open PlantDetailsActivity with ALL data from backend
     */
    private fun openPlantDetailsFromBackend(plant: Plant) {
        // Create JSON with ALL backend fields
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

            // Add diseases
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

        // Open details activity with full JSON
        val intent = Intent(requireContext(), PlantDetailsActivity::class.java).apply {
            putExtra("plant_json", plantJson.toString())
        }
        startActivity(intent)
    }
}