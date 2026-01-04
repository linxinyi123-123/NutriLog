package com.example.nutrilog.data

import android.content.Context
import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseCallback(
    private val context: Context
) : RoomDatabase.Callback() {
    
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Log.d("DatabaseCallback", "数据库创建完成，开始初始化数据...")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                initializeDatabase(context)
                Log.d("DatabaseCallback", "数据库初始化成功")
                
                // 验证初始化结果
                verifyDatabaseInitialization()
                
            } catch (e: Exception) {
                Log.e("DatabaseCallback", "数据库初始化失败", e)
            }
        }
    }
    
    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        Log.d("DatabaseCallback", "数据库已打开，开始检查数据初始化...")
        
        // 在数据库打开时检查是否需要初始化数据
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(context)
                val foodDao = database.foodDao()
                val foodCount = foodDao.count()
                
                Log.d("DatabaseCallback", "检查数据库状态: 食物记录数量 = $foodCount")
                
                if (foodCount == 0) {
                    Log.d("DatabaseCallback", "数据库为空，执行初始化...")
                    initializeDatabase(context)
                    Log.d("DatabaseCallback", "数据库初始化完成")
                } else {
                    Log.d("DatabaseCallback", "数据库已有 $foodCount 条食物记录")
                }
            } catch (e: Exception) {
                Log.e("DatabaseCallback", "数据库初始化检查失败", e)
            }
        }
    }
    
    private suspend fun initializeDatabase(context: Context) {
        Log.d("DatabaseCallback", "开始执行数据库初始化...")
        
        // 获取数据库实例和DAO
        val database = AppDatabase.getDatabase(context)
        val foodDao = database.foodDao()
        
        // 使用DatabaseInitializer进行数据初始化
        val initializer = DatabaseInitializer(context, foodDao)
        initializer.initializeDatabase()
        
        Log.d("DatabaseCallback", "数据库初始化逻辑执行完成")
    }
    
    private suspend fun verifyDatabaseInitialization() {
        Log.d("DatabaseCallback", "开始验证数据库初始化结果...")
        
        val database = AppDatabase.getDatabase(context)
        val foodDao = database.foodDao()
        
        try {
            // 验证食物数据
            val foodCount = foodDao.count()
            Log.d("DatabaseCallback", "数据库中的食物记录数量: $foodCount")
            
            if (foodCount > 0) {
                Log.i("DatabaseCallback", "✅ 数据库初始化验证成功: 已加载 $foodCount 条食物记录")
                
                // 验证分类数据
                val categories = foodDao.getAllCategories()
                Log.d("DatabaseCallback", "可用的食物分类: ${categories.joinToString()}")
                
                // 验证常用食物
                val commonFoods = foodDao.getCommonFoods()
                Log.d("DatabaseCallback", "常用食物数量: ${commonFoods.size}")
                
                // 验证搜索功能
                val searchResults = foodDao.search("米饭", 5)
                Log.d("DatabaseCallback", "搜索'米饭'的结果数量: ${searchResults.size}")
                
            } else {
                Log.w("DatabaseCallback", "⚠️ 数据库初始化警告: 食物记录数量为0")
            }
            
        } catch (e: Exception) {
            Log.e("DatabaseCallback", "❌ 数据库初始化验证失败", e)
        }
    }
    
    private fun verifyDatabaseState(db: SupportSQLiteDatabase) {
        Log.d("DatabaseCallback", "开始验证数据库状态...")
        
        try {
            // 验证外键约束
            val foreignKeysCursor = db.query("PRAGMA foreign_keys;")
            foreignKeysCursor.use { cursor ->
                if (cursor.moveToFirst()) {
                    val foreignKeysEnabled = cursor.getInt(0) == 1
                    Log.d("DatabaseCallback", "外键约束状态: ${if (foreignKeysEnabled) "启用" else "禁用"}")
                }
            }
            
            // 验证WAL模式
            val journalModeCursor = db.query("PRAGMA journal_mode;")
            journalModeCursor.use { cursor ->
                if (cursor.moveToFirst()) {
                    val journalMode = cursor.getString(0)
                    Log.d("DatabaseCallback", "日志模式: $journalMode")
                }
            }
            
            // 验证表结构
            val tablesCursor = db.query("SELECT name FROM sqlite_master WHERE type='table';")
            tablesCursor.use { cursor ->
                val tableNames = mutableListOf<String>()
                while (cursor.moveToNext()) {
                    tableNames.add(cursor.getString(0))
                }
                Log.d("DatabaseCallback", "数据库表数量: ${tableNames.size}, 表名: ${tableNames.joinToString()}")
            }
            
            Log.i("DatabaseCallback", "✅ 数据库状态验证完成")
            
        } catch (e: Exception) {
            Log.e("DatabaseCallback", "❌ 数据库状态验证失败", e)
        }
    }
}