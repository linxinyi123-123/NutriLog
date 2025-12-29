package com.example.nutrilog

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nutrilog.di.AppModule
import com.example.nutrilog.ui.navigation.BottomNavigationBar
import com.example.nutrilog.ui.screens.AddRecordScreen
import com.example.nutrilog.ui.screens.AnalysisScreen
import com.example.nutrilog.ui.screens.FoodCategoryScreen
import com.example.nutrilog.ui.screens.FoodListScreen
import com.example.nutrilog.ui.screens.FoodSearchScreen
import com.example.nutrilog.ui.screens.HomeScreen
import com.example.nutrilog.ui.screens.MainScreen
import com.example.nutrilog.ui.screens.ProfileScreen
import com.example.nutrilog.ui.screens.RecordDetailScreen
import com.example.nutrilog.ui.screens.RecordListScreen
import com.example.nutrilog.ui.screens.ReportsScreen
import com.example.nutrilog.ui.screens.ReportGenerationScreen
import com.example.nutrilog.ui.screens.ReportPreviewScreen
import com.example.nutrilog.ui.theme.NutriLogTheme
import com.example.nutrilog.ui.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // 初始化数据库（这会触发DatabaseCallback.onCreate）
        AppModule.provideDatabase(this)
        
        setContent {
            NutriLogTheme {
                NutriLogApp()
            }
        }
    }
}

@Composable
fun NutriLogApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    // 创建ViewModel（使用我们的依赖注入）
    val mainViewModel: MainViewModel = remember {
        AppModule.provideMainViewModel(context)
    }
    
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(navController)
            }
            composable("analysis") {
                AnalysisScreen(navController)
            }
            composable("reports") {
                ReportsScreen(navController)
            }
            composable("profile") {
                ProfileScreen(navController)
            }
            
            // 主屏幕（原来的main页面）
            composable("main") {
                MainScreen(
                    navController = navController,
                    viewModel = mainViewModel
                )
            }
            

            
            // 食物分类页面
            composable("food_categories") {
                FoodCategoryScreen(
                    navController = navController,
                    viewModel = mainViewModel
                )
            }
            
            // 食物搜索页面
            composable("food_search?query={query}") { backStackEntry ->
                val query = backStackEntry.arguments?.getString("query") ?: ""
                FoodSearchScreen(
                    navController = navController,
                    viewModel = mainViewModel,
                    initialQuery = query
                )
            }
            
            // 食物列表页面
            composable("food_list/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                FoodListScreen(
                    navController = navController,
                    viewModel = mainViewModel,
                    category = category
                )
            }
            
            // 添加记录页面
            composable("add_record") {
                AddRecordScreen(
                    navController = navController,
                    viewModel = mainViewModel
                )
            }
            
            // 编辑记录页面
            composable("edit_record/{recordId}") { backStackEntry ->
                val recordId = backStackEntry.arguments?.getString("recordId")?.toLongOrNull()
                AddRecordScreen(
                    navController = navController,
                    viewModel = mainViewModel,
                    recordId = recordId
                )
            }
            
            // 记录详情页面
            composable("record_detail/{recordId}") { backStackEntry ->
                val recordId = backStackEntry.arguments?.getString("recordId")?.toLongOrNull() ?: 0L
                RecordDetailScreen(
                    recordId = recordId,
                    navController = navController,
                    viewModel = mainViewModel
                )
            }
            
            // 报告生成页面
            composable("report_generation") {
                ReportGenerationScreen(navController)
            }
            
            // 报告预览页面
            composable("report_preview/{reportId}") { backStackEntry ->
                val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
                ReportPreviewScreen(
                    reportId = reportId,
                    navController = navController
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NutriLogAppPreview() {
    NutriLogTheme {
        NutriLogApp()
    }
}
