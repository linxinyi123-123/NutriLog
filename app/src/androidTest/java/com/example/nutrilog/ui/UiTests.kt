package com.example.nutrilog.ui

import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nutrilog.ui.screens.HomeScreen
import com.example.nutrilog.ui.theme.NutriLogTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UiTests {
    @get:Rule
    val composeTestRule = androidx.compose.ui.test.junit4.createComposeRule()

    @Test
    fun testHomeScreenLayout() {
        composeTestRule.setContent {
            NutriLogTheme {
                HomeScreen(navController = rememberNavController())
            }
        }

        // 验证关键元素存在
        composeTestRule.onNodeWithText("今日摘要").assertExists()
        composeTestRule.onNodeWithText("记录饮食").assertExists()
        composeTestRule.onNodeWithText("最近饮食").assertExists()
    }

    @Test
    fun testHomeScreenNavigation() {
        composeTestRule.setContent {
            NutriLogTheme {
                HomeScreen(navController = rememberNavController())
            }
        }

        // 验证导航按钮存在
        composeTestRule.onNodeWithContentDescription("用户头像").assertExists()
    }

    @Test
    fun testAccessibility() {
        composeTestRule.setContent {
            NutriLogTheme {
                HomeScreen(navController = rememberNavController())
            }
        }

        // 验证关键元素可访问
        composeTestRule.onNodeWithText("今日摘要").assertExists()
        composeTestRule.onNodeWithText("记录饮食").assertExists()
    }
}
