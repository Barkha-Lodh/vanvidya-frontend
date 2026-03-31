package com.vanvidya.app.data

import android.content.Context
import com.vanvidya.app.data.model.Plant
import com.vanvidya.app.data.network.PlantApiService
import com.vanvidya.app.data.network.toPlant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlantRepository(private val context: Context) {

    private val apiService = PlantApiService.create()

    suspend fun getPlantInfo(plantName: String): PlantResult {
        return withContext(Dispatchers.IO) {
            try {
                // it will Call my Gemini backend
                val response = apiService.getPlantInfo(plantName)

                // it will convert to Plant model
                val plant = response.toPlant()

                PlantResult(
                    data = plant,
                    message = "Success",
                    isOnline = true
                )
            } catch (e: Exception) {
                PlantResult(
                    data = null,
                    message = "Error: ${e.message}\n\nMake sure your Django backend is running!",
                    isOnline = false
                )
            }
        }
    }


    suspend fun getPopularPlants(): List<Plant> {
        return withContext(Dispatchers.IO) {
            try {
                // Fetch 3 popular plants from my backend
                val plants = mutableListOf<Plant>()

                val plantNames = listOf("Monstera", "Dracaena trifasciata", "Rose")

                for (name in plantNames) {
                    try {
                        val response = apiService.getPlantInfo(name)
                        plants.add(response.toPlant())
                    } catch (e: Exception) {
                        // Skip if one fails
                        continue
                    }
                }

                plants
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}

/**
 * Result wrapper for repository operations
 */
data class PlantResult(
    val data: Plant?,
    val message: String,
    val isOnline: Boolean
)