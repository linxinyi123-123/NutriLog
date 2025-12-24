package com.example.nutrilog

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nutrilog.di.AppModule
import com.example.nutrilog.ui.screens.MainScreen
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
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // 创建ViewModel（使用我们的依赖注入）
    val mainViewModel: MainViewModel = remember {
        AppModule.provideMainViewModel(context)
    }
    
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(
                navController = navController,
                viewModel = mainViewModel
            )
        }
        
        // 添加记录页面（待实现）
        composable("add_record") {
            // 临时占位页面
            androidx.compose.material3.Text("添加记录页面（待实现）")
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