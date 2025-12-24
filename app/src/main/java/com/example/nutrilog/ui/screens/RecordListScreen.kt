package com.example.nutrilog.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.ui.viewmodels.MainViewModel

@Composable
fun RecordListScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MainViewModel
) {
    val todayMealRecords by viewModel.todayMealRecords.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    // 手动下拉刷新状态
    var isRefreshing by remember { mutableStateOf(false) }
    
    Column(modifier = modifier.fillMaxSize()) {
        // 错误消息显示
        errorMessage?.let { message ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    
                    TextButton(onClick = { viewModel.clearErrorMessage() }) {
                        Text("关闭")
                    }
                }
            }
        }
        
        // 下拉刷新指示器
        if (isRefreshing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
        }
        
        // 加载状态
        if (isLoading && !isRefreshing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // 使用简单的下拉刷新实现
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (todayMealRecords.isEmpty()) {
                    EmptyState(
                        onRefresh = {
                            isRefreshing = true
                            viewModel.loadTodayMealRecords()
                        }
                    )
                } else {
                    MealRecordList(
                        records = todayMealRecords,
                        onRecordClick = { recordId ->
                            navController.navigate("record_detail/$recordId")
                        },
                        onDeleteRecord = { recordId -> viewModel.deleteMealRecord(recordId) },
                        onRefresh = {
                            isRefreshing = true
                            viewModel.loadTodayMealRecords()
                        }
                    )
                }
            }
        }
    }
    
    // 处理刷新状态
    LaunchedEffect(isLoading) {
        if (!isLoading) {
            isRefreshing = false
        }
    }
    
    // 添加手动刷新按钮（作为替代下拉刷新的方案）
    LaunchedEffect(Unit) {
        // 可以添加一个浮动按钮来手动触发刷新
    }
}

@Composable
fun MealRecordList(
    records: List<MealRecord>,
    onRecordClick: (Long) -> Unit,
    onDeleteRecord: (Long) -> Unit,
    onRefresh: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 刷新按钮
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { onRefresh() },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "刷新",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "下拉刷新数据",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        // 记录列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(records, key = { it.id }) { record ->
                MealRecordCard(
                    record = record,
                    onCardClick = { onRecordClick(record.id) },
                    onDelete = { onDeleteRecord(record.id) }
                )
            }
        }
    }
}

@Composable
fun MealRecordCard(
    record: MealRecord,
    onCardClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 日期和餐次信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    // 日期显示
                    Text(
                        text = record.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                    
                    // 餐次和时间
                    Text(
                        text = "${record.mealType} - ${record.time}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // 详情图标
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "查看详情",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 主要食物信息（简化显示）
            Text(
                text = "主要食物: ${getMainFoodDisplay(record)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 备注信息
            if (record.note.isNotBlank()) {
                Text(
                    text = "备注: ${record.note}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { /* 编辑功能 */ }) {
                    Icon(Icons.Default.Edit, "编辑")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "删除")
                }
            }
        }
    }
}

// 获取主要食物显示文本（简化版本）
private fun getMainFoodDisplay(record: MealRecord): String {
    return if (record.note.isNotBlank() && record.note.length > 10) {
        record.note.take(10) + "..."
    } else if (record.note.isNotBlank()) {
        record.note
    } else {
        "暂无具体食物信息"
    }
}

@Composable
fun EmptyState(onRefresh: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "暂无饮食记录",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRefresh,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Refresh, "刷新")
                Spacer(modifier = Modifier.width(8.dp))
                Text("刷新数据")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "点击右下角按钮添加第一条记录",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}