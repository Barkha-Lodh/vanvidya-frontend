package com.vanvidya.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [PlantEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // ✅ Adds new columns to existing database
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE plants ADD COLUMN soilType TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE plants ADD COLUMN indoorOutdoor TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE plants ADD COLUMN edible TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE plants ADD COLUMN toxic TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE plants ADD COLUMN warning TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE plants ADD COLUMN funFacts TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE plants ADD COLUMN origin TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE plants ADD COLUMN growthRate TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE plants ADD COLUMN diseasesJson TEXT NOT NULL DEFAULT '[]'")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vanvidya_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}