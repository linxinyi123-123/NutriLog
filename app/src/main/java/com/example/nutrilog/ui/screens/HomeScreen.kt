package com.example.nutrilog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nutrilog.ui.components.*
import com.example.nutrilog.ui.viewmodels.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = viewModel()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NutriLog") },
                navigationIcon = {
                    IconButton(onClick = { /* 打开抽屉菜单 */ }) {
                        Icon(Icons.Filled.Menu, contentDescription = "菜单")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    top = it.calculateTopPadding() + 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        ) {
            // 1. 欢迎区域
            WelcomeSection(user = viewModel.user)
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // 2. 今日摘要卡片
            TodaySummaryCard(
                summary = viewModel.todaySummary,
                onClick = { navController.navigate("today_detail") }
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // 3. 快速操作按钮
            QuickActionsRow(navController = navController)
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // 4. 健康趋势卡片
            HealthTrendCard(trend = viewModel.weeklyTrend)
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // 5. 最近饮食记录
            RecentMealsSection(meals = viewModel.recentMeals)
        }
    }
}

// HomeScreen 预览
@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    device = "id:pixel_5",
    name = "Home Screen Preview"
)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 1. 欢迎区域模拟
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(0.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 头像/图标
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "用户头像",
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "你好, 用户!",
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Text(
                            text = "今天是你的第7天健康之旅",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // 健康评分徽章
                    Surface(
                        modifier = Modifier.padding(8.dp),
                        color = Color.Green,
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(
                            text = "健康分: 85",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // 2. 今日摘要卡片模拟
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // 标题和日期
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "今日摘要",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Text(
                            text = "2024-01-12",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 营养数据网格
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(text = "热量", style = MaterialTheme.typography.bodySmall)
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(text = "1800", style = MaterialTheme.typography.titleMedium)
                                    Text(text = "kcal", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 4.dp, bottom = 2.dp))
                                }
                            }
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(text = "蛋白质", style = MaterialTheme.typography.bodySmall)
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(text = "120", style = MaterialTheme.typography.titleMedium)
                                    Text(text = "g", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 4.dp, bottom = 2.dp))
                                }
                            }
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(text = "碳水", style = MaterialTheme.typography.bodySmall)
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(text = "220", style = MaterialTheme.typography.titleMedium)
                                    Text(text = "g", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 4.dp, bottom = 2.dp))
                                }
                            }
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(text = "脂肪", style = MaterialTheme.typography.bodySmall)
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(text = "65", style = MaterialTheme.typography.titleMedium)
                                    Text(text = "g", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 4.dp, bottom = 2.dp))
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 健康评分进度条
                    Column {
                        // 分数标签
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "健康评分", style = MaterialTheme.typography.bodyMedium)
                            
                            Text(text = "85/100", style = MaterialTheme.typography.bodyMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // 进度条
                        LinearProgressIndicator(
                            progress = 0.85f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // 评分标签
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("需改善", style = MaterialTheme.typography.bodySmall)
                            Text("良好", style = MaterialTheme.typography.bodySmall)
                            Text("优秀", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // 3. 快速操作按钮模拟
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "快速操作",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QuickActionButton(
                            icon = Icons.Filled.AddCircle,
                            label = "添加记录",
                            onClick = {}
                        )
                        QuickActionButton(
                            icon = Icons.Outlined.Assessment,
                            label = "查看趋势",
                            onClick = {}
                        )
                        QuickActionButton(
                            icon = Icons.Outlined.CalendarToday,
                            label = "日历",
                            onClick = {}
                        )
                        QuickActionButton(
                            icon = Icons.Outlined.Description,
                            label = "营养建议",
                            onClick = {}
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // 4. 健康趋势卡片模拟
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "健康趋势",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // 简化的趋势图表
                    Column {
                        Text(text = "过去7天的营养摄入趋势", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "卡路里: 1950 kcal/天", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "蛋白质: 110 g/天", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "碳水: 240 g/天", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "脂肪: 68 g/天", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // 5. 最近饮食记录模拟
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "最近饮食记录",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // 模拟饮食记录项
                    repeat(3) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 食物图标或图片
                            Surface(
                                modifier = Modifier.size(48.dp),
                                color = Color.LightGray,
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Icon(
                                    Icons.Filled.Info,
                                    contentDescription = "食物",
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Black
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "午餐 - 鸡胸肉沙拉", style = MaterialTheme.typography.bodyMedium)
                                Text(text = "12:30", style = MaterialTheme.typography.bodySmall)
                            }

                            Text(text = "450 kcal", style = MaterialTheme.typography.titleMedium)
                        }
                        if (it < 2) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }
    }
}
