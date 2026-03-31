package com.vanvidya.app.data.network

import android.graphics.Bitmap
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.vanvidya.app.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class PlantIdService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // TODO: Replace with your API key from https://web.plant.id/
    private val MUSHROOM_API_KEY = BuildConfig.MUSHROOM_API_KEY

    private val MUSHROOM_BASE_URL = "https://mushroom.kindwise.com/api/v1"

    suspend fun identifyMushroom(bitmap: Bitmap): PlantIdResult {
        return withContext(Dispatchers.IO) {
            try {
                val base64Image = bitmapToBase64(bitmap)

                val json = JSONObject().apply {
                    put("images", JSONArray().apply {
                        put("data:image/jpeg;base64,$base64Image")
                    })
                    put("similar_images", true)
                }

                val request = Request.Builder()
                    .url("$MUSHROOM_BASE_URL/identification")
                    .addHeader("Api-Key", MUSHROOM_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(json.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    return@withContext PlantIdResult(
                        suggestions = emptyList(),
                        error = "API request failed: ${response.code}"
                    )
                }

                parseMushroomResponse(responseBody)

            } catch (e: Exception) {
                PlantIdResult(
                    suggestions = emptyList(),
                    error = e.message
                )
            }
        }
    }

    //
    private fun parseMushroomResponse(json: String): PlantIdResult {
        try {
            val jsonObject = JSONObject(json)
            val result = jsonObject.optJSONObject("result")
                ?: return PlantIdResult(
                    suggestions = emptyList(),
                    error = "Invalid response format"
                )

            val classification = result.optJSONObject("classification")
            val suggestions = classification?.optJSONArray("suggestions")

            val results = mutableListOf<PlantSuggestion>()

            if (suggestions != null) {
                for (i in 0 until minOf(suggestions.length(), 5)) {
                    val suggestion = suggestions.getJSONObject(i)
                    val probability = suggestion.optDouble("probability", 0.0)
                    val scientificName = suggestion.optString("name", "Unknown")

                    val details = suggestion.optJSONObject("details")
                    val commonNames = details?.optJSONArray("common_names")
                    val commonName = commonNames?.optString(0) ?: scientificName
                    val description = details?.optString("description") ?: ""

                    results.add(
                        PlantSuggestion(
                            commonName = commonName,
                            scientificName = scientificName,
                            probability = probability,
                            description = description
                        )
                    )
                }
            }

            return PlantIdResult(suggestions = results)

        } catch (e: Exception) {
            return PlantIdResult(
                suggestions = emptyList(),
                error = "Failed to parse: ${e.message}"
            )
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val maxSize = 1500
        val resized = if (bitmap.width > maxSize || bitmap.height > maxSize) {
            val ratio = maxSize.toFloat() / maxOf(bitmap.width, bitmap.height)
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * ratio).toInt(),
                (bitmap.height * ratio).toInt(),
                true
            )
        } else bitmap

        val outputStream = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}

data class PlantIdResult(
    val suggestions: List<PlantSuggestion>,
    val error: String? = null
)

data class PlantSuggestion(
    val commonName: String,
    val scientificName: String,
    val probability: Double,
    val description: String
)

data class DiseaseResult(
    val isHealthy: Boolean,
    val diseases: List<Disease>,
    val error: String? = null
)

data class Disease(
    val name: String,
    val probability: Double,
    val description: String,
    val treatment: String
)
