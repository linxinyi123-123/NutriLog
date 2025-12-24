package com.example.nutrilog

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.nutrilog.data.AppDatabase
import com.example.nutrilog.ui.theme.NutriLogTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // 初始化数据库（异步）
        initializeDatabase()
        
        setContent {
            NutriLogTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "NutriLog",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
    
    private fun initializeDatabase() {
        Log.d("MainActivity", "开始初始化数据库...")
        
        // 使用协程在后台线程中初始化数据库
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 获取数据库实例，这会触发DatabaseCallback.onCreate()
                val database = AppDatabase.getDatabase(this@MainActivity)
                
                // 验证数据库是否创建成功
                val foodDao = database.foodDao()
                val foodCount = kotlin.runCatching { 
                    foodDao.count() 
                }.getOrNull()
                
                Log.d("MainActivity", "数据库初始化完成，食物记录数量: ${foodCount ?: "未知"}")
                
            } catch (e: Exception) {
                Log.e("MainActivity", "数据库初始化失败", e)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NutriLogTheme {
        Greeting("Android")
    }
}