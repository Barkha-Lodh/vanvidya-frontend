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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.vanvidya.app.R
import com.vanvidya.app.data.PlantRepository
import com.vanvidya.app.ui.details.PlantDetailsActivity
import com.vanvidya.app.ui.search.PlantSearchActivity
import com.vanvidya.app.ui.identify.FlowerScannerFragment
import com.vanvidya.app.ui.identify.MushroomIdentifyFragment
import com.vanvidya.app.ui.tools.LightMeterFragment
import com.vanvidya.app.ui.tools.WaterCalculatorFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

class HomeFragment : Fragment() {

    private lateinit var repository: PlantRepository

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

    // ✅ Just names — no API call on load
    private val popularPlantNames = listOf("Monstera", "Dracaena trifasciata", "Sunflower")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = PlantRepository(requireContext())
        initializeViews(view)
        updateGreeting()
        setupClickListeners()
        // ✅ No loadPopularPlantsFromBackend() here — loads only on tap
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
            in 17..20 -> "Good Evening!"
            else -> "Good Night!"
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
                        "© 2026 VanVidya")
                .setPositiveButton("OK", null)
                .show()
        }

        searchCard.setOnClickListener {
            startActivity(Intent(requireContext(), PlantSearchActivity::class.java))
        }

        plantScannerCard.setOnClickListener {
            openFragment(com.vanvidya.app.ui.scan.ScanFragment())
        }

        mushroomCard.setOnClickListener {
            openFragment(MushroomIdentifyFragment())
        }

        flowerScannerCard.setOnClickListener {
            openFragment(FlowerScannerFragment())
        }

        lightMeterCard.setOnClickListener {
            openFragment(LightMeterFragment())
        }

        waterCalculatorCard.setOnClickListener {
            openFragment(WaterCalculatorFragment())
        }

        fertilizerCard.setOnClickListener {
            Toast.makeText(context, "Fertilizer Guide - Coming Soon!", Toast.LENGTH_SHORT).show()
        }

        seeAllPlants.setOnClickListener {
            startActivity(Intent(requireContext(), PlantSearchActivity::class.java))
        }

        // ✅ Popular plants — API called ONLY when user taps
        popularPlant1.setOnClickListener {
            fetchAndOpenPlant(popularPlantNames[0])
        }
        popularPlant2.setOnClickListener {
            fetchAndOpenPlant(popularPlantNames[1])
        }
        popularPlant3.setOnClickListener {
            fetchAndOpenPlant(popularPlantNames[2])
        }
    }

    // ✅ Fetch from backend only when tapped
    private fun fetchAndOpenPlant(plantName: String) {
        lifecycleScope.launch {
            try {
                Toast.makeText(context, "Loading Plants", Toast.LENGTH_SHORT).show()

                val result = withContext(Dispatchers.IO) {
                    repository.getPlantInfo(plantName)
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
                        Intent(requireContext(), PlantDetailsActivity::class.java).apply {
                            putExtra("plant_json", plantJson.toString())
                        }
                    )
                } else {
                    Toast.makeText(context,
                        "Could not load plants", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(context,
                    "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        try {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .addToBackStack(null)
                .commit()
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}