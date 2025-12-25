package com.example.nutrilog

import android.app.Application
import timber.log.Timber

class NutriLogApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 初始化日志库
        initializeTimber()

        // 初始化应用组件
        initializeApp()
    }

    private fun initializeTimber() {
        // 使用 ApplicationInfo.FLAG_DEBUGGABLE 来检查是否是调试版本
        // 这是一个更可靠的方法，不需要导入 BuildConfig
        val isDebug = applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0

        // 只有在调试模式才打印日志
        if (isDebug) {
            Timber.plant(Timber.DebugTree())
            Timber.d("Timber调试日志已启用")
        }
    }

    private fun initializeApp() {
        Timber.d("NutriLog应用初始化开始...")

        // 这里不需要手动初始化数据库
        // 数据库会在首次访问时自动初始化（通过Room的getDatabase方法）

        Timber.d("NutriLog应用初始化完成")
    }
}