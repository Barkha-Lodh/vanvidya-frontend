package com.vanvidya.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val commonName: String,
    val hindiName: String = "",
    val scientificName: String = "",
    val family: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val watering: String = "",
    val sunlight: String = "",
    val soilType: String = "",
    val indoorOutdoor: String = "",
    val edible: String = "",
    val toxic: String = "",
    val warning: String = "",
    val funFacts: String = "",
    val origin: String = "",
    val growthRate: String = "",
    val type: String="",
    val diseasesJson: String = "[]", // Stored as JSON string
    val category: String = "plant",  // "plant", "flower", "mushroom"
    val savedAt: Long = System.currentTimeMillis()
)