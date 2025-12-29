package com.example.nutrilog.features.recommendation.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 最简单的Android测试，用于验证测试环境
 */
@RunWith(AndroidJUnit4::class)
class SimpleAndroidTest {

    @Test
    fun testAppContext() {
        // 验证应用上下文
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.nutrilog", appContext.packageName)
    }

    @Test
    fun testBasicMath() {
        // 验证基础逻辑
        assertEquals(4, 2 + 2)
    }
}