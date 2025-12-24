package com.example.nutrilog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nutrilog.data.entities.MealLocation
import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.data.entities.MealType
import com.example.nutrilog.ui.viewmodels.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    // 状态变量
    var selectedMealType by remember { mutableStateOf(MealType.BREAKFAST) }
    var selectedLocation by remember { mutableStateOf(MealLocation.HOME) }
    var mealTime by remember { mutableStateOf(getCurrentTime()) }
    var mealDate by remember { mutableStateOf(getCurrentDate()) }
    var mood by remember { mutableStateOf(3) }
    var note by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("添加饮食记录") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // 保存记录
                            val record = MealRecord(
                                date = mealDate,
                                time = mealTime,
                                mealType = selectedMealType,
                                location = selectedLocation,
                                mood = mood,
                                note = note
                            )
                            
                            // 保存到数据库
                            viewModel.addMealRecord(record)
                            
                            // 返回上一页
                            navController.popBackStack()
                        }
                    ) {
                        Icon(Icons.Default.Check, "保存")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 基本信息卡片
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "基本信息",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // 用餐类型选择
                    Text(
                        text = "用餐类型",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MealType.values().forEach { type ->
                            FilterChip(
                                selected = selectedMealType == type,
                                onClick = { selectedMealType = type },
                                label = { Text(type.displayName) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 用餐地点选择
                    Text(
                        text = "用餐地点",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MealLocation.values().forEach { location ->
                            FilterChip(
                                selected = selectedLocation == location,
                                onClick = { selectedLocation = location },
                                label = { Text(location.displayName) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 日期和时间
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // 日期选择
                        OutlinedTextField(
                            value = mealDate,
                            onValueChange = { mealDate = it },
                            label = { Text("日期") },
                            modifier = Modifier.weight(1f)
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // 时间选择
                        OutlinedTextField(
                            value = mealTime,
                            onValueChange = { mealTime = it },
                            label = { Text("时间") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 心情评分卡片
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "心情评分",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        (1..5).forEach { rating ->
                            FilterChip(
                                selected = mood == rating,
                                onClick = { mood = rating },
                                label = { Text(rating.toString()) }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 备注卡片
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "备注",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("添加备注...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 快速添加按钮
            Button(
                onClick = {
                    // 快速添加当前时间的记录
                    val currentTime = getCurrentTime()
                    val currentDate = getCurrentDate()
                    
                    val record = MealRecord(
                        date = currentDate,
                        time = currentTime,
                        mealType = MealType.fromTime(currentTime),
                        location = MealLocation.HOME,
                        mood = 3,
                        note = "快速添加",
                        isQuickAdd = true
                    )
                    
                    viewModel.addMealRecord(record)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Schedule, "快速添加")
                Spacer(modifier = Modifier.width(8.dp))
                Text("快速添加当前记录")
            }
        }
    }
}

// 获取当前时间（HH:mm格式）
private fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date())
}

// 获取当前日期（yyyy-MM-dd格式）
private fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}