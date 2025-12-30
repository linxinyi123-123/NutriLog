package com.example.nutrilog.data.entities

import androidx.compose.ui.graphics.Color

object FoodTags {
    // 预设标签列表
    val TAG_LIST = listOf("未定义", "油炸", "健康", "高蛋白", "低碳")

    // 标签背景颜色
    val TAG_COLORS = mapOf(
        "油炸" to Color(0xFFF3C7C2),   // 红色
        "健康" to Color(0xFFCBE7CC),   // 绿色
        "高蛋白" to Color(0xFFCBE1F3),  // 蓝色
        "低碳" to Color(0xFFF8EDC7)    // 紫色
    )

    // 标签文字颜色
    val TAG_TEXT_COLORS = mapOf(
        "油炸" to Color.Black,
        "健康" to Color.Black,
        "高蛋白" to Color.Black,
        "低碳" to Color.Black
    )
}