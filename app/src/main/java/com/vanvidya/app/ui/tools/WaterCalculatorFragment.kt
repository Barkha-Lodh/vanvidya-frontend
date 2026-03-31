package com.vanvidya.app.ui.tools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.vanvidya.app.R

class WaterCalculatorFragment : Fragment() {

    private lateinit var plantTypeSpinner: Spinner
    private lateinit var potSizeSpinner: Spinner
    private lateinit var seasonSpinner: Spinner
    private lateinit var climateSpinner: Spinner
    private lateinit var calculateButton: MaterialButton
    private lateinit var resultCard: MaterialCardView
    private lateinit var wateringFrequencyText: TextView
    private lateinit var waterAmountText: TextView
    private lateinit var tipsText: TextView

    private val plantTypes = listOf(
        "Succulents/Cacti",
        "Tropical Plants",
        "Ferns",
        "Herbs",
        "Flowering Plants",
        "Vegetables"
    )

    private val potSizes = listOf(
        "Small (4-6 inch)",
        "Medium (6-10 inch)",
        "Large (10-14 inch)",
        "Extra Large (14+ inch)"
    )

    private val seasons = listOf(
        "Spring",
        "Summer",
        "Fall",
        "Winter"
    )

    private val climates = listOf(
        "Humid",
        "Moderate",
        "Dry/Arid"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_water_calculator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        plantTypeSpinner = view.findViewById(R.id.plant_type_spinner)
        potSizeSpinner = view.findViewById(R.id.pot_size_spinner)
        seasonSpinner = view.findViewById(R.id.season_spinner)
        climateSpinner = view.findViewById(R.id.climate_spinner)
        calculateButton = view.findViewById(R.id.calculate_button)
        resultCard = view.findViewById(R.id.result_card)
        wateringFrequencyText = view.findViewById(R.id.watering_frequency)
        waterAmountText = view.findViewById(R.id.water_amount)
        tipsText = view.findViewById(R.id.watering_tips)

        setupSpinners()

        calculateButton.setOnClickListener {
            calculateWatering()
        }
    }

    private fun setupSpinners() {
        plantTypeSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            plantTypes
        )

        potSizeSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            potSizes
        )

        seasonSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            seasons
        )

        climateSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            climates
        )
    }

    private fun calculateWatering() {
        val plantType = plantTypeSpinner.selectedItemPosition
        val potSize = potSizeSpinner.selectedItemPosition
        val season = seasonSpinner.selectedItemPosition
        val climate = climateSpinner.selectedItemPosition

        val result = calculateWateringSchedule(plantType, potSize, season, climate)

        wateringFrequencyText.text = result.frequency
        waterAmountText.text = result.amount
        tipsText.text = result.tips

        resultCard.visibility = View.VISIBLE
    }

    private fun calculateWateringSchedule(
        plantType: Int,
        potSize: Int,
        season: Int,
        climate: Int
    ): WateringResult {
        // Base frequency in days
        var frequency = when (plantType) {
            0 -> 14.0 // Succulents/Cacti
            1 -> 7.0  // Tropical
            2 -> 5.0  // Ferns
            3 -> 3.0  // Herbs
            4 -> 4.0  // Flowering
            5 -> 2.0  // Vegetables
            else -> 7.0
        }

        // Adjust for season
        frequency *= when (season) {
            0 -> 1.0   // Spring
            1 -> 0.7   // Summer (water more often)
            2 -> 1.2   // Fall
            3 -> 1.5   // Winter (water less often)
            else -> 1.0
        }

        // Adjust for climate
        frequency *= when (climate) {
            0 -> 1.3   // Humid (less frequent)
            1 -> 1.0   // Moderate
            2 -> 0.8   // Dry (more frequent)
            else -> 1.0
        }

        // Calculate water amount
        val amount = when (potSize) {
            0 -> "50-100 ml"      // Small
            1 -> "150-250 ml"     // Medium
            2 -> "300-500 ml"     // Large
            3 -> "600-1000 ml"    // Extra Large
            else -> "200 ml"
        }

        val frequencyText = when {
            frequency < 2 -> "Daily"
            frequency < 4 -> "Every 2-3 days"
            frequency < 7 -> "Twice a week"
            frequency < 10 -> "Once a week"
            frequency < 14 -> "Every 10-12 days"
            else -> "Every 2 weeks"
        }

        val tips = generateTips(plantType, season, climate)

        return WateringResult(frequencyText, amount, tips)
    }

    private fun generateTips(plantType: Int, season: Int, climate: Int): String {
        val tipsList = mutableListOf<String>()

        // Plant-specific tips
        when (plantType) {
            0 -> tipsList.add("• Check soil is completely dry before watering")
            1 -> tipsList.add("• Keep soil consistently moist but not waterlogged")
            2 -> tipsList.add("• Ferns love humidity - mist regularly")
            3 -> tipsList.add("• Water when top inch of soil feels dry")
            4 -> tipsList.add("• Water more during blooming period")
            5 -> tipsList.add("• Consistent moisture is key for vegetables")
        }

        // Season tips
        when (season) {
            1 -> tipsList.add("• Summer heat = more frequent watering")
            3 -> tipsList.add("• Reduce watering in winter dormancy")
        }

        // Climate tips
        when (climate) {
            0 -> tipsList.add("• High humidity = less water needed")
            2 -> tipsList.add("• Dry climate = check soil more often")
        }

        // General tips
        tipsList.add("• Always check soil moisture before watering")
        tipsList.add("• Ensure pots have drainage holes")
        tipsList.add("• Water in the morning for best results")

        return tipsList.joinToString("\n")
    }

    data class WateringResult(
        val frequency: String,
        val amount: String,
        val tips: String
    )
}