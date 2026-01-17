package com.example.nutrilog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nutrilog.ui.components.ThemeSwitcher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("个人资料") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center  // Box 内容居中
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