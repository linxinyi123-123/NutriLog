package com.example.nutrilog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nutrilog.ui.components.ThemeSwitcher
import com.example.nutrilog.features.recommendation.viewmodel.RecommendationViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    // 添加推荐ViewModel
    val recommendationViewModel: RecommendationViewModel = viewModel()
    // 收集成就数据
    val achievements = recommendationViewModel.achievements.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("个人资料") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally  // 列内容居中
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)  // 设置宽度为 90%，留出边距
                    .wrapContentHeight(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 用户信息卡片
                    UserInfoCard()

                    Spacer(modifier = Modifier.height(20.dp))

                    // 设置选项
                    SettingsCard()
                }
            }
            
            // 成就列表
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "我的成就",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                if (achievements.value.isNotEmpty()) {
                    achievements.value.forEach { achievement ->
                        AchievementCard(achievement = achievement)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                } else {
                    Text(
                        text = "暂无成就",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// 修改 UserInfoCard，移除 Card 包装
@Composable
private fun UserInfoCard() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 头像
        Surface(
            modifier = Modifier.size(80.dp),
            color = MaterialTheme.colorScheme.primary,
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "张",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 用户名
        Text(
            text = "张三",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 用户邮箱
        Text(
            text = "user@example.com",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// 修改 SettingsCard，移除 Card 包装
@Composable
private fun SettingsCard() {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 深色模式开关
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "深色模式",
                style = MaterialTheme.typography.bodyLarge
            )

            ThemeSwitcher()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 用户名
        Text(
            text = "张三",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 用户邮箱
        Text(
            text = "user@example.com",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
//@Composable
//private fun SettingsCard() {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        shape = MaterialTheme.shapes.large
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            // 深色模式开关
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 12.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "深色模式",
//                    style = MaterialTheme.typography.bodyLarge
//                )
//
//                ThemeSwitcher()
//            }
//        }
//    }
//}

// 成就卡片组件
@Composable
fun AchievementCard(achievement: com.example.nutrilog.features.recommendation.model.gamification.Achievement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 成就图标
            Surface(
                modifier = Modifier.size(48.dp),
                color = if (achievement.unlockedAt != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "成就图标",
                    modifier = Modifier.size(24.dp),
                    tint = if (achievement.unlockedAt != null) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = achievement.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                if (achievement.unlockedAt != null) {
                    Text(
                        text = "已解锁",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else {
                    Text(
                        text = "未解锁",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
//@Composable
//private fun SettingsCard() {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        shape = MaterialTheme.shapes.large
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            // 深色模式开关
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 12.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "深色模式",
//                    style = MaterialTheme.typography.bodyLarge
//                )
//
//                ThemeSwitcher()
//            }
//        }
//    }
//}

// ProfileScreen 预览
@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    device = "id:pixel_5",
    name = "Profile Screen Preview"
)
@Composable
private fun ProfileScreenPreview() {
    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            UserInfoCard()
            Spacer(modifier = Modifier.height(20.dp))
            SettingsCard()
        }
    }
}