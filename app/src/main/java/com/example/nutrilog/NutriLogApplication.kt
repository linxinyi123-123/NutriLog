package com.example.nutrilog

import android.app.Application
import android.util.Log
import com.example.nutrilog.di.AppModule

class NutriLogApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化应用组件
        initializeApp()
    }
    
    private fun initializeApp() {
        Log.d("NutriLogApplication", "应用初始化开始...")
        
        // 初始化数据库（这会触发DatabaseCallback.onCreate）
        val database = AppModule.provideDatabase(this)
        
        Log.d("NutriLogApplication", "应用初始化完成")
    }
}