package com.vanvidya.app.data.local

import androidx.room.*

@Dao
interface PlantDao {

    // Get all saved plants newest first
    @Query("SELECT * FROM plants ORDER BY savedAt DESC")
    suspend fun getAllPlants(): List<PlantEntity>

    // Get by category: "plant", "flower", "mushroom"
    @Query("SELECT * FROM plants WHERE category = :category ORDER BY savedAt DESC")
    suspend fun getPlantsByCategory(category: String): List<PlantEntity>

    // Check if already saved
    @Query("SELECT * FROM plants WHERE commonName = :name AND category = :category LIMIT 1")
    suspend fun getPlantByNameAndCategory(name: String, category: String): PlantEntity?

    @Query("SELECT * FROM plants WHERE type = :categoryType")
    suspend fun getItemsByCategory(categoryType: String): List<PlantEntity>

    // Count by category
    @Query("SELECT COUNT(*) FROM plants WHERE category = :category")
    suspend fun getCountByCategory(category: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantEntity)

    @Delete
    suspend fun deletePlant(plant: PlantEntity)

    @Query("DELETE FROM plants WHERE commonName = :name AND category = :category")
    suspend fun deleteByNameAndCategory(name: String, category: String)

    @Query("SELECT EXISTS(SELECT 1 FROM plants WHERE commonName = :name AND category = :category)")
    suspend fun isPlantSaved(name: String, category: String): Boolean
}