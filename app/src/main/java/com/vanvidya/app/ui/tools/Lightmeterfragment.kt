package com.vanvidya.app.ui.tools

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.vanvidya.app.R
import kotlin.math.roundToInt

class LightMeterFragment : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null

    private lateinit var luxValueText: TextView
    private lateinit var lightLevelText: TextView
    private lateinit var recommendationText: TextView
    private lateinit var plantSuggestionsText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_light_meter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        luxValueText = view.findViewById(R.id.lux_value)
        lightLevelText = view.findViewById(R.id.light_level)
        recommendationText = view.findViewById(R.id.recommendation)
        plantSuggestionsText = view.findViewById(R.id.plant_suggestions)

        setupSensor()
    }

    private fun setupSensor() {
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        if (lightSensor == null) {
            Toast.makeText(
                context,
                "Light sensor not available on this device",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_LIGHT) {
                val lux = it.values[0]
                updateUI(lux)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for light sensor
    }

    private fun updateUI(lux: Float) {
        val luxInt = lux.roundToInt()
        luxValueText.text = "$luxInt lux"

        val (level, recommendation, plants) = getLightInfo(lux)

        lightLevelText.text = level
        recommendationText.text = recommendation
        plantSuggestionsText.text = plants
    }

    private fun getLightInfo(lux: Float): Triple<String, String, String> {
        return when {
            lux < 50 -> Triple(
                "Very Low Light",
                "This is very dim lighting. Most plants will struggle here. Consider using grow lights or moving plants to brighter location.",
                "Suitable plants: Snake Plant, ZZ Plant, Pothos (will survive but grow slowly)"
            )
            lux < 200 -> Triple(
                "Low Light",
                "Suitable for shade-loving plants. Good for bathrooms and darker corners of rooms.",
                "Suitable plants: Snake Plant, Pothos, Peace Lily, Chinese Evergreen, Cast Iron Plant"
            )
            lux < 400 -> Triple(
                "Medium-Low Light",
                "Good for many houseplants. Suitable for spaces away from windows but still receiving ambient light.",
                "Suitable plants: Monstera, Philodendron, Dracaena, Spider Plant, Ferns"
            )
            lux < 1000 -> Triple(
                "Medium Light",
                "Ideal for most common houseplants. Good for areas near windows but not in direct sun.",
                "Suitable plants: Most tropical plants, Rubber Plant, Calathea, Prayer Plant, Boston Fern"
            )
            lux < 10000 -> Triple(
                "Bright Indirect Light",
                "Excellent for most houseplants. Perfect spot near windows with filtered light.",
                "Suitable plants: Fiddle Leaf Fig, Bird of Paradise, Alocasia, most flowering plants"
            )
            lux < 25000 -> Triple(
                "Very Bright / Partial Direct Sun",
                "Very bright location with some direct sunlight. Great for sun-loving plants.",
                "Suitable plants: Succulents, Cacti, Jade Plant, Aloe Vera, most herbs"
            )
            else -> Triple(
                "Full Direct Sunlight",
                "Intense direct sunlight. Perfect for desert plants and outdoor species.",
                "Suitable plants: Cacti, Succulents, Desert Rose, Agave, outdoor plants"
            )
        }
    }
}