package com.example.nutrilog.data

import android.content.Context
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
        CoroutineScope(Dispatchers.IO).launch {
            initializeDatabase(context)
        }
    }
    
    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        db.execSQL("PRAGMA foreign_keys = ON;")
        db.execSQL("PRAGMA journal_mode = WAL;")
    }
    
    private suspend fun initializeDatabase(context: Context) {
        // 这里可以添加初始数据填充逻辑
        // 例如：插入默认的食物分类、单位等
    }
}