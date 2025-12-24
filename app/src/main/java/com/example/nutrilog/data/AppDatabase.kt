package com.example.nutrilog.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nutrilog.data.converters.FoodCategoryConverter
import com.example.nutrilog.data.converters.FoodUnitConverter
import com.example.nutrilog.data.converters.LongListConverter
import com.example.nutrilog.data.converters.MealLocationConverter
import com.example.nutrilog.data.converters.MealTypeConverter
import com.example.nutrilog.data.dao.FoodDao
import com.example.nutrilog.data.dao.MealRecordDao
import com.example.nutrilog.data.dao.RecordFoodDao
import com.example.nutrilog.data.entities.FoodItem
import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.data.entities.RecordFoodItem

@Database(
    entities = [
        FoodItem::class,
        MealRecord::class,
        RecordFoodItem::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(
    FoodCategoryConverter::class,
    FoodUnitConverter::class,
    MealTypeConverter::class,
    MealLocationConverter::class,
    LongListConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun mealRecordDao(): MealRecordDao
    abstract fun recordFoodDao(): RecordFoodDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nutrilog_database"
                ).addCallback(DatabaseCallback(context))
                 .addMigrations(MIGRATION_1_2)
                 .fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }
        
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 安全地添加列，如果列已存在则跳过
                addColumnIfNotExists(database, "record_food_items", "custom_unit", "TEXT")
                addColumnIfNotExists(database, "record_food_items", "preparation", "TEXT")
                addColumnIfNotExists(database, "record_food_items", "note", "TEXT NOT NULL DEFAULT ''")
                addColumnIfNotExists(database, "record_food_items", "order_index", "INTEGER NOT NULL DEFAULT 0")
            }
            
            private fun addColumnIfNotExists(database: SupportSQLiteDatabase, tableName: String, columnName: String, columnType: String) {
                try {
                    // 尝试查询该列，如果列不存在会抛出异常
                    database.query("SELECT $columnName FROM $tableName LIMIT 0")
                    // 如果查询成功，说明列已存在，跳过添加
                } catch (e: Exception) {
                    // 如果查询失败，说明列不存在，需要添加
                    database.execSQL("ALTER TABLE $tableName ADD COLUMN $columnName $columnType")
                }
            }
        }
    }
}