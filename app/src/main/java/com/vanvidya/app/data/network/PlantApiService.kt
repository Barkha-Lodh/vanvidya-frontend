package com.vanvidya.app.data.network

import android.os.Build
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.vanvidya.app.data.model.Plant

interface PlantApiService {

    @GET("api/external/complete/")
    suspend fun getPlantInfo(@Query("name") plantName: String): PlantApiResponse

    companion object {
        private const val LAPTOP_IP = "192.168.29.28"  // CHANGE THIS TO LAPTOP IP!

        private fun getBaseUrl(): String {
            // Detect if running on emulator or real phone it will run
            val isEmulator = Build.FINGERPRINT.contains("generic") ||
                    Build.MODEL.contains("Emulator") ||
                    Build.MANUFACTURER.contains("Genymotion") ||
                    Build.PRODUCT.contains("sdk")

            return if (isEmulator) {
                // Emulator: use 10.0.2.2
                "http://10.0.2.2:8000/"
            } else {
                // Real Phone: use laptop's actual IP
                "http://$LAPTOP_IP:8000/"
            }
        }

        fun create(): PlantApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(PlantApiService::class.java)
        }
    }
}

data class PlantApiResponse(
    val common_name: String,
    val scientific_name: String,
    val family: String,
    val hindi_name: String? = null,
    val description: String,
    val image_url: String? = null,
    val wikipedia_url: String? = null,
    val watering: String? = null,
    val sunlight: String? = null,
    val soil_type: String? = null,
    val indoor_outdoor: String? = null,
    val edible: String? = null,
    val toxic: String? = null,
    val warning: String? = null,
    val origin: String? = null,
    val growth_rate: String? = null,
    val fun_facts: String? = null,
    val diseases: List<DiseaseResponse>? = null
)

data class DiseaseResponse(
    val name: String,
    val symptom: String,
    val treatment: String
)

fun PlantApiResponse.toPlant() = Plant(
    commonName = common_name,
    scientificName = scientific_name,
    family = family,
    hindiName = hindi_name,
    description = description,
    imageUrl = image_url,
    wikipediaUrl = wikipedia_url,
    watering = watering,
    sunlight = sunlight,
    soilType = soil_type,
    indoorOutdoor = indoor_outdoor,
    edible = edible,
    toxic = toxic,
    warning = warning,
    funFacts = fun_facts,
    origin = origin,
    growthRate = growth_rate,
    diseases = diseases?.map {
        com.vanvidya.app.data.model.Disease(
            name = it.name,
            symptom = it.symptom,
            treatment = it.treatment
        )
    }
)