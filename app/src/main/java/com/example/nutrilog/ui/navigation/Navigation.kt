package com.example.nutrilog.ui.navigation

import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination

// 底部导航项数据类
data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

// 获取当前路由
@Composable
fun currentRoute(navController: NavController): String? {
    return navController.currentDestination?.route
}

// 底部导航栏组件
@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("home", "首页", Icons.Default.Home),
        BottomNavItem("food_categories", "食品类型", Icons.Default.Category),
        BottomNavItem("main", "饮食记录", Icons.Default.Restaurant),
        BottomNavItem("analysis", "分析", Icons.Outlined.Assessment),
        // BottomNavItem("reports", "报告", Icons.Outlined.Description),
        BottomNavItem("profile", "我的", Icons.Default.Person)
    )

    NavigationBar {
        val currentRoute = currentRoute(navController)

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // 弹出到起始目的地，避免栈溢出
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // 避免重复导航到同一目的地
                        launchSingleTop = true
                        // 恢复之前保存的状态
                        restoreState = true
                    }
                }
            )
        }
    }
}

// 页面切换动画组件
@Composable
fun AnimatedNavigation(
    currentScreen: String,
    content: @Composable (String) -> Unit
) {
    val transition = updateTransition(currentScreen, label = "navigation")
    
    val slideOffset = transition.animateDp(
        transitionSpec = {
            when {
                initialState == "home" && targetState == "analysis" ->
                    tween(durationMillis = 300)
                else ->
                    tween(durationMillis = 300)
            }
        }
    ) { screen ->
        when (screen) {
            "home" -> 0.dp
            "analysis" -> 0.dp
            else -> 0.dp
        }
    }
    
    val alpha = transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) }
    ) { screen ->
        when (screen) {
            currentScreen -> 1f
            else -> 0f
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        content(currentScreen)
    }
}