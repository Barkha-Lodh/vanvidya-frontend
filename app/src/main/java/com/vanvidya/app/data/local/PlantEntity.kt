package com.vanvidya.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val commonName: String,
    val hindiName: String,
    val scientificName: String,
    val family: String,
    val description: String,
    val imageUrl: String?,
    val watering: String,
    val sunlight: String,
    val category: String,
    val savedAt: Long = System.currentTimeMillis()
)