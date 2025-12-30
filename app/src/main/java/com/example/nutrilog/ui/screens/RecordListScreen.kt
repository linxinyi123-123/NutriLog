package com.example.nutrilog.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrilog.data.entities.FoodItem
import com.example.nutrilog.data.entities.MealLocation
import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.data.entities.MealType
import com.example.nutrilog.data.entities.FoodTags
import com.example.nutrilog.ui.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordListScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val allMealRecords by viewModel.allMealRecords.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // 删除确认对话框状态
    var showDeleteDialog by remember { mutableStateOf(false) }
    var recordToDelete by remember { mutableStateOf<MealRecord?>(null) }
    
    // 当屏幕进入焦点时重新加载数据
    LaunchedEffect(Unit) {
        viewModel.loadAllMealRecords()
    }
    
    // 处理错误消息
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.clearErrorMessage()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("饮食记录") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    navController.navigate("add_record") 
                }
            ) {
                Icon(Icons.Default.Add, "添加记录")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (allMealRecords.isEmpty()) {
                // 没有记录时的提示
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "暂无饮食记录",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "点击右下角按钮开始记录您的饮食",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                // 记录列表
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allMealRecords) { record ->
                        MealRecordItem(
                            record = record,
                            viewModel = viewModel,
                            navController = navController,
                            onEditClick = { recordToEdit ->
                                // 导航到编辑界面
                                navController.navigate("edit_record/${recordToEdit.id}")
                            },
                            onDeleteClick = { recordToDeleteItem ->
                                // 显示删除确认对话框
                                recordToDelete = recordToDeleteItem
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
        
        // 删除确认对话框
        if (showDeleteDialog && recordToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    recordToDelete = null
                },
                title = { Text("确认删除") },
                text = { Text("确定要删除这条饮食记录吗？此操作不可撤销。") },
                confirmButton = {
                    Button(
                        onClick = {
                            recordToDelete?.let { record ->
                                viewModel.deleteMealRecord(record.id)
                            }
                            showDeleteDialog = false
                            recordToDelete = null
                        }
                    ) {
                        Text("删除")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDeleteDialog = false
                            recordToDelete = null
                        }
                    ) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealRecordItem(
    record: MealRecord,
    viewModel: MainViewModel,
    navController: NavController,
    onEditClick: (MealRecord) -> Unit,
    onDeleteClick: (MealRecord) -> Unit
) {
    // 状态：记录的食物列表
    var foods by remember { mutableStateOf<List<Pair<FoodItem, Double>>>(emptyList()) }
    
    // 加载食物信息
    LaunchedEffect(record.id) {
        try {
            foods = viewModel.getFoodsForRecord(record.id)
        } catch (e: Exception) {
            // 处理加载错误
            foods = emptyList()
        }
    }
    
    // 格式化食物显示文本
    val foodDisplayText = if (foods.isNotEmpty()) {
        val foodNames = foods.take(3).map { it.first.name }
        val displayText = foodNames.joinToString(", ")
        if (foods.size > 3) "${displayText}等" else displayText
    } else {
        ""
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 第一行：食品1,食品2,食品3等
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = foodDisplayText,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${record.date} ${record.time}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // 操作按钮和标签
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 标签显示（在操作按钮左侧）
                    if (record.tag.isNotEmpty() && record.tag != "未定义") {
                        TagChip(
                            tag = record.tag,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    
                    // 编辑按钮
                    IconButton(
                        onClick = { onEditClick(record) }
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Edit,
                            contentDescription = "编辑记录",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // 删除按钮
                    IconButton(
                        onClick = { onDeleteClick(record) }
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                            contentDescription = "删除记录",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

        }
    }
}

// 标签显示组件（用于记录列表）
@Composable
fun TagChip(
    tag: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = FoodTags.TAG_COLORS[tag] ?: MaterialTheme.colorScheme.primary
    val textColor = FoodTags.TAG_TEXT_COLORS[tag] ?: MaterialTheme.colorScheme.onPrimary
    
    androidx.compose.material3.AssistChip(
        onClick = {},
        label = { 
            Text(
                text = tag,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            ) 
        },
        modifier = modifier.width(60.dp),
        colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
            containerColor = backgroundColor,
            labelColor = textColor
        ),
        border = androidx.compose.material3.AssistChipDefaults.assistChipBorder(
            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            borderWidth = 1.dp
        )
    )
}
