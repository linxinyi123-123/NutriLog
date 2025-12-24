package com.nutrilog.features.recommendation.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nutrilog.features.recommendation.database.dao.AchievementDao
import com.nutrilog.features.recommendation.database.dao.HealthGoalDao
import com.nutrilog.features.recommendation.database.dao.RecommendationDao
import com.nutrilog.features.recommendation.database.entity.AchievementEntity
import com.nutrilog.features.recommendation.database.entity.HealthGoalEntity
import com.nutrilog.features.recommendation.database.entity.RecommendationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        RecommendationEntity::class,
        HealthGoalEntity::class,
        AchievementEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RecommendationDatabase : RoomDatabase() {

    abstract fun recommendationDao(): RecommendationDao
    abstract fun healthGoalDao(): HealthGoalDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile
        private var INSTANCE: RecommendationDatabase? = null

        fun getDatabase(context: Context): RecommendationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecommendationDatabase::class.java,
                    "recommendation_database"
                )
                    .addCallback(RecommendationDatabaseCallback(context))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class RecommendationDatabaseCallback(
    private val context: Context
) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // 在后台线程初始化数据
        CoroutineScope(Dispatchers.IO).launch {
            val database = RecommendationDatabase.getDatabase(context)
            initializeDatabase(database)
        }
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        // 启用外键约束
        db.execSQL("PRAGMA foreign_keys = ON;")
        db.execSQL("PRAGMA journal_mode = WAL;")
    }

    private suspend fun initializeDatabase(database: RecommendationDatabase) {
        // 初始化默认成就数据
        val defaultAchievements = listOf(
            AchievementEntity(
                id = 1L,
                userId = 0, // 0表示模板成就，实际用户成就需要复制这个模板
                name = "首次记录",
                description = "记录你的第一餐饮食",
                type = "DAILY",
                icon = "first_log",
                points = 10
            ),
            AchievementEntity(
                id = 2L,
                userId = 0,
                name = "连续记录7天",
                description = "连续7天记录饮食",
                type = "MILESTONE",
                icon = "streak_7",
                points = 50
            ),
            AchievementEntity(
                id = 3L,
                userId = 0,
                name = "营养均衡大师",
                description = "一周内每日营养评分超过85分",
                type = "SPECIAL",
                icon = "balance_master",
                points = 100
            ),
            AchievementEntity(
                id = 4L,
                userId = 0,
                name = "蔬菜达人",
                description = "连续5天摄入足够蔬菜",
                type = "MILESTONE",
                icon = "veggie_master",
                points = 40
            ),
            AchievementEntity(
                id = 5L,
                userId = 0,
                name = "饮水冠军",
                description = "连续3天饮水达标",
                type = "DAILY",
                icon = "water_champion",
                points = 30
            ),
            AchievementEntity(
                id = 6L,
                userId = 0,
                name = "蛋白质专家",
                description = "蛋白质摄入持续达标一周",
                type = "SPECIAL",
                icon = "protein_pro",
                points = 80
            ),
            AchievementEntity(
                id = 7L,
                userId = 0,
                name = "食物探索家",
                description = "尝试20种不同食物",
                type = "MILESTONE",
                icon = "variety_explorer",
                points = 60
            )
        )

        database.achievementDao().insertAll(defaultAchievements)
    }
}