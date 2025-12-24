package com.example.nutrilog.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nutrilog.ui.components.*
import com.example.nutrilog.ui.viewmodels.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = viewModel()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 1. 欢迎区域
        WelcomeSection(user = viewModel.user)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 2. 今日摘要卡片
        TodaySummaryCard(
            summary = viewModel.todaySummary,
            onClick = { navController.navigate("today_detail") }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 3. 快速操作按钮
        QuickActionsRow(navController)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 4. 健康趋势卡片
        HealthTrendCard(trend = viewModel.weeklyTrend)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 5. 最近饮食记录
        RecentMealsSection(meals = viewModel.recentMeals)
    }
}
