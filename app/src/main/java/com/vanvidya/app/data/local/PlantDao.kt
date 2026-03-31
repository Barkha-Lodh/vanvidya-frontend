package com.vanvidya.app.data.local

import androidx.room.*

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants ORDER BY savedAt DESC")
    suspend fun getAllPlants(): List<PlantEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantEntity)

    @Delete
    suspend fun deletePlant(plant: PlantEntity)
}