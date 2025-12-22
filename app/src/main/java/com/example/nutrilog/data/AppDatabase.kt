package com.example.nutrilog.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
    version = 1,
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
                 .fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}