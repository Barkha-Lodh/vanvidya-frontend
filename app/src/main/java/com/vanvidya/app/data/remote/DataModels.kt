package com.vanvidya.app.data.remote

import com.google.gson.annotations.SerializedName

// ════════════════════════════════════════════════════════════════
// MAIN PLANT INFO (Used by Home Search & Leaf Scan)
// ════════════════════════════════════════════════════════════════

data class CompletePlantInfo(
    @SerializedName("common_name") val commonName: String,
    @SerializedName("hindi_name") val hindiName: String,
    @SerializedName("scientific_name") val scientificName: String,
    @SerializedName("family") val family: String,
    @SerializedName("description") val description: String,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("wikipedia_url") val wikipediaUrl: String?,
    @SerializedName("watering") val watering: String,
    @SerializedName("sunlight") val sunlight: String,
    @SerializedName("soil_type") val soilType: String,
    @SerializedName("indoor_outdoor") val indoorOutdoor: String,
    @SerializedName("edible") val edible: String,
    @SerializedName("toxic") val toxic: String,
    @SerializedName("warning") val warning: String?,
    @SerializedName("fun_facts") val funFacts: String,
    @SerializedName("origin") val origin: String,
    @SerializedName("growth_rate") val growthRate: String,
    @SerializedName("diseases") val diseases: List<Disease>
)

data class Disease(
    @SerializedName("name") val name: String,
    @SerializedName("symptom") val symptom: String,
    @SerializedName("treatment") val treatment: String
)

// ════════════════════════════════════════════════════════════════
// LEAF IDENTIFICATION RESPONSE (Used by Scan Feature)
// ════════════════════════════════════════════════════════════════

data class LeafIdentificationResponse(
    @SerializedName("plant_identification") val plantIdentification: PlantIdentificationInfo,
    @SerializedName("disease_detection") val diseaseDetection: DiseaseDetectionInfo,
    @SerializedName("plant_details") val plantDetails: CompletePlantInfo
)

data class PlantIdentificationInfo(
    @SerializedName("plant_name") val plantName: String,
    @SerializedName("confidence") val confidence: Float,
    @SerializedName("identified_by") val identifiedBy: String
)

data class DiseaseDetectionInfo(
    @SerializedName("is_healthy") val isHealthy: Boolean,
    @SerializedName("disease") val disease: DiseaseDetail?
)

data class DiseaseDetail(
    @SerializedName("name") val name: String,
    @SerializedName("probability") val probability: Float,
    @SerializedName("description") val description: String,
    @SerializedName("treatment") val treatment: String?
)