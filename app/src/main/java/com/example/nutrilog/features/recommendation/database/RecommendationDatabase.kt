package com.example.nutrilog.features.recommendation.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.nutrilog.features.recommendation.database.dao.AchievementDao
import com.example.nutrilog.features.recommendation.database.dao.HealthGoalDao
import com.example.nutrilog.features.recommendation.database.dao.RecommendationDao
import com.example.nutrilog.features.recommendation.database.entity.AchievementEntity
import com.example.nutrilog.features.recommendation.database.entity.HealthGoalEntity
import com.example.nutrilog.features.recommendation.database.entity.RecommendationEntity
import com.example.nutrilog.features.recommendation.database.entity.RecommendationRuleEntity
import com.example.nutrilog.features.recommendation.database.dao.RecommendationRuleDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        RecommendationEntity::class,
        HealthGoalEntity::class,
        AchievementEntity::class,
        RecommendationRuleEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class RecommendationDatabase : RoomDatabase() {

    abstract fun recommendationDao(): RecommendationDao
    abstract fun healthGoalDao(): HealthGoalDao
    abstract fun achievementDao(): AchievementDao
    abstract fun recommendationRuleDao(): RecommendationRuleDao

    companion object {
        @Volatile
        private var INSTANCE: RecommendationDatabase? = null

        // 数据库迁移：从版本1到版本2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 创建推荐规则表
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS recommendation_rules (
                        id INTEGER PRIMARY KEY NOT NULL,
                        type TEXT NOT NULL,
                        condition TEXT NOT NULL,
                        action TEXT NOT NULL,
                        priority TEXT NOT NULL,
                        message TEXT NOT NULL
                    )
                """)

                // 插入一些默认规则
                val defaultRules = listOf(
                    // 蛋白质严重不足规则
                    "INSERT OR REPLACE INTO recommendation_rules VALUES (1, 'NUTRITION_GAP', '{\"type\":\"NUTRIENT_GAP\",\"nutrient\":\"protein\",\"threshold\":30.0,\"comparison\":\"GREATER_THAN\"}', '{\"type\":\"SUGGEST_FOODS\",\"foodCategories\":[\"高蛋白\"],\"reason\":\"蛋白质补充\"}', 'HIGH', '检测到蛋白质摄入严重不足，建议增加高蛋白食物')",

                    // 健康评分过低规则
                    "INSERT OR REPLACE INTO recommendation_rules VALUES (2, 'HEALTH_SCORE', '{\"type\":\"HEALTH_SCORE\",\"score\":60,\"comparison\":\"LESS_THAN\"}', '{\"type\":\"SHOW_EDUCATIONAL_TIP\",\"tipId\":101,\"category\":\"营养均衡\"}', 'MEDIUM', '您的健康评分较低，建议查看营养知识')",

                    // 膳食纤维不足规则
                    "INSERT OR REPLACE INTO recommendation_rules VALUES (3, 'NUTRITION_GAP', '{\"type\":\"NUTRIENT_GAP\",\"nutrient\":\"fiber\",\"threshold\":20.0,\"comparison\":\"GREATER_THAN\"}', '{\"type\":\"SUGGEST_FOODS\",\"foodCategories\":[\"高纤维\"],\"reason\":\"膳食纤维补充\"}', 'MEDIUM', '膳食纤维摄入不足，建议增加蔬菜水果')"
                )

                defaultRules.forEach { sql ->
                    try {
                        database.execSQL(sql)
                    } catch (e: Exception) {
                        // 如果插入失败，继续执行下一条
                        e.printStackTrace()
                    }
                }
            }
        }

        fun getDatabase(context: Context): RecommendationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecommendationDatabase::class.java,
                    "recommendation_database"
                )
                    .addCallback(RecommendationDatabaseCallback(context))
                    .addMigrations(MIGRATION_1_2)  // 添加迁移策略
                    .fallbackToDestructiveMigration()  // 如果迁移失败，重建数据库
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

        // 检查并插入默认规则数据（如果规则表为空）
        try {
            val ruleCount = database.recommendationRuleDao().getRuleCount()
            if (ruleCount == 0) {
                insertDefaultRecommendationRules(database)
            }
        } catch (e: Exception) {
            // 如果表不存在，插入默认规则
            insertDefaultRecommendationRules(database)
        }
    }

    private suspend fun insertDefaultRecommendationRules(database: RecommendationDatabase) {
        val defaultRules = listOf(
            RecommendationRuleEntity(
                id = 1,
                type = "NUTRITION_GAP",
                condition = """{"type":"NUTRIENT_GAP","nutrient":"protein","threshold":30.0,"comparison":"GREATER_THAN"}""",
                action = """{"type":"SUGGEST_FOODS","foodCategories":["高蛋白"],"reason":"蛋白质补充"}""",
                priority = "HIGH",
                message = "检测到蛋白质摄入严重不足，建议增加高蛋白食物"
            ),
            RecommendationRuleEntity(
                id = 2,
                type = "HEALTH_SCORE",
                condition = """{"type":"HEALTH_SCORE","score":60,"comparison":"LESS_THAN"}""",
                action = """{"type":"SHOW_EDUCATIONAL_TIP","tipId":101,"category":"营养均衡"}""",
                priority = "MEDIUM",
                message = "您的健康评分较低，建议查看营养知识"
            ),
            RecommendationRuleEntity(
                id = 3,
                type = "NUTRITION_GAP",
                condition = """{"type":"NUTRIENT_GAP","nutrient":"fiber","threshold":20.0,"comparison":"GREATER_THAN"}""",
                action = """{"type":"SUGGEST_FOODS","foodCategories":["高纤维"],"reason":"膳食纤维补充"}""",
                priority = "MEDIUM",
                message = "膳食纤维摄入不足，建议增加蔬菜水果"
            )
        )

        database.recommendationRuleDao().insertAll(defaultRules)
    }
}