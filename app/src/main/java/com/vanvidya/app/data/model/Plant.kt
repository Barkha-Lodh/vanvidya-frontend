package com.vanvidya.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * Comprehensive Plant data model with all details
 */
data class Plant(
    @SerializedName("common_name")
    val commonName: String,

    @SerializedName("hindi_name")
    val hindiName: String? = null,

    @SerializedName("scientific_name")
    val scientificName: String,

    @SerializedName("family")
    val family: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("image_url")
    val imageUrl: String? = null,

    @SerializedName("wikipedia_url")
    val wikipediaUrl: String? = null,

    // Care Instructions
    @SerializedName("watering")
    val watering: String? = "Water regularly when top inch of soil is dry",

    @SerializedName("sunlight")
    val sunlight: String? = "Bright indirect light to full sun",

    @SerializedName("soil_type")
    val soilType: String? = "Well-draining soil",

    @SerializedName("indoor_outdoor")
    val indoorOutdoor: String? = "Can be grown both indoors and outdoors",

    // Additional Info
    @SerializedName("edible")
    val edible: String? = null,

    @SerializedName("toxic")
    val toxic: String? = null,

    @SerializedName("warning")
    val warning: String? = null,

    @SerializedName("fun_facts")
    val funFacts: String? = null,

    @SerializedName("origin")
    val origin: String? = null,

    @SerializedName("growth_rate")
    val growthRate: String? = null,

    @SerializedName("diseases")
    val diseases: List<Disease>? = null
) {
    // Backward compatibility properties
    val careInstructions: String
        get() = buildString {
            append(watering ?: "")
            if (sunlight != null) append("\n\n$sunlight")
            if (soilType != null) append("\n\nSoil: $soilType")
        }

    val lightRequirements: String
        get() = sunlight ?: "Bright indirect light"

    val waterRequirements: String
        get() = watering ?: "Regular watering"

    val temperatureRange: String
        get() = "15-25°C (60-77°F)" // Default if not provided by API
}

/**
 * Disease information
 */
data class Disease(
    @SerializedName("name")
    val name: String,

    @SerializedName("symptom")
    val symptom: String,

    @SerializedName("treatment")
    val treatment: String
)