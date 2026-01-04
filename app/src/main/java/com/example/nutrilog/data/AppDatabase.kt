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
import com.example.nutrilog.data.dao.ComboFoodDao
import com.example.nutrilog.data.dao.FoodComboDao
import com.example.nutrilog.data.dao.FoodDao
import com.example.nutrilog.data.dao.MealRecordDao
import com.example.nutrilog.data.dao.RecordFoodDao
import com.example.nutrilog.data.entities.ComboFood
import com.example.nutrilog.data.entities.FoodCombo
import com.example.nutrilog.data.entities.FoodItem
import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.data.entities.RecordFoodItem

@Database(
    entities = [
        FoodItem::class,
        MealRecord::class,
        RecordFoodItem::class,
        FoodCombo::class,
        ComboFood::class
    ],
    version = 5,
    exportSchema = true
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
    abstract fun foodComboDao(): FoodComboDao
    abstract fun comboFoodDao(): ComboFoodDao
    
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
                 .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
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
        
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 创建食物组合表
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS FoodCombo (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        description TEXT
                    )
                """)
                
                // 创建组合食物关联表
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS ComboFood (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        comboId INTEGER NOT NULL,
                        foodId INTEGER NOT NULL,
                        foodName TEXT NOT NULL,
                        portion REAL NOT NULL,
                        unit TEXT NOT NULL,
                        FOREIGN KEY (comboId) REFERENCES FoodCombo(id) ON DELETE CASCADE
                    )
                """)
                
                // 为关联表创建索引
                database.execSQL("CREATE INDEX IF NOT EXISTS index_ComboFood_comboId ON ComboFood(comboId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_ComboFood_foodId ON ComboFood(foodId)")
            }
        }
        
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {

            }
        }
        
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 为meal_records表添加标签字段
                addColumnIfNotExists(database, "meal_records", "tag", "TEXT NOT NULL DEFAULT '未定义'")
                
                // 为meal_records表添加热量字段
                addColumnIfNotExists(database, "meal_records", "calories", "REAL NOT NULL DEFAULT 0.0")
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