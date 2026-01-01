package com.example.nutrilog.features.recommendation.test

import org.junit.Test
import org.junit.Assert.*

class SimpleUnitTest {

    @Test
    fun `simple test should pass`() {
        println("=== 简单测试开始 ===")
        val result = 1 + 1
        assertEquals(2, result)
        println("简单测试通过！1+1=$result")
        println("=== 简单测试结束 ===")
    }

    @Test
    fun `another simple test`() {
        println("另一个简单测试")
        assertTrue(true)
    }
}