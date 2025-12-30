package com.example.nutrilog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nutrilog.data.entities.FoodItem
import com.example.nutrilog.data.entities.FoodCombo
import com.example.nutrilog.data.entities.MealRecord
import com.example.nutrilog.ui.viewmodels.AddRecordViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreen(
    navController: NavController,
    viewModel: AddRecordViewModel,
    recordId: Long? = null
) {
    val isEditing = recordId != null
    val scope = rememberCoroutineScope()
    
    // 从ViewModel获取状态
    val selectedFoods by viewModel.selectedFoods.collectAsState()
    val foodCombos by viewModel.foodCombos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val showSaveComboDialog by viewModel.showSaveComboDialog.collectAsState()
    val comboName by viewModel.comboName.collectAsState()
    val comboDescription by viewModel.comboDescription.collectAsState()
    
    // 本地状态
    var showFoodSearch by remember { mutableStateOf(false) }
    var showComboMenu by remember { mutableStateOf(false) }
    var recordSaved by remember { mutableStateOf(false) }
    
    // 获取当前日期和时间（合并为一个字段）
    var currentDateTime by remember { mutableStateOf("${LocalDate.now().format(DateTimeFormatter.ISO_DATE)} ${LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))}") }
    
    // 编辑模式：加载已有记录数据
    LaunchedEffect(recordId) {
        if (isEditing && recordId != null) {
            viewModel.loadRecordForEditing(recordId)
        }
    }
    
    // 处理错误消息
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            // 这里可以显示Snackbar或其他错误提示
            println("错误: $message")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "编辑饮食记录" else "新建饮食记录"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 保存为组合按钮（在保存按钮左边）
                        if (selectedFoods.isNotEmpty()) {
                            Button(
                                onClick = { viewModel.showSaveComboDialog() },
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("保存为组合", fontSize = 14.sp)
                            }
                        }
                        
                        // 保存记录按钮
                        Button(
                            onClick = {
                                    // 解析合并的日期时间字符串
                                    val parts = currentDateTime.split(" ")
                                    val date = if (parts.size >= 1) parts[0] else LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                                    val time = if (parts.size >= 2) parts[1] else LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                                    
                                    if (isEditing && recordId != null) {
                                        // 编辑模式：创建更新后的记录
                                        val updatedRecord = MealRecord(
                                            id = recordId,
                                            date = date,
                                            time = time,
                                            mealType = com.example.nutrilog.data.entities.MealType.LUNCH, // 默认值
                                            location = com.example.nutrilog.data.entities.MealLocation.HOME, // 默认值
                                            mood = 3, // 默认值
                                            note = "" // 空备注
                                        )
                                        viewModel.updateMealRecord(updatedRecord) {
                                            // 更新成功后设置标志并导航回主界面
                                            recordSaved = true
                                            navController.popBackStack()
                                        }
                                    } else {
                                        // 新建模式：创建新记录
                                        val record = MealRecord(
                                            id = 0,
                                            date = date,
                                            time = time,
                                            mealType = com.example.nutrilog.data.entities.MealType.LUNCH, // 默认值
                                            location = com.example.nutrilog.data.entities.MealLocation.HOME, // 默认值
                                            mood = 3, // 默认值
                                            note = "" // 空备注
                                        )
                                        viewModel.saveMealRecord(record) {
                                            // 保存成功后设置标志并导航回主界面
                                            recordSaved = true
                                            navController.popBackStack()
                                        }
                                    }
                                },
                                enabled = selectedFoods.isNotEmpty(),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(if (isEditing) "更新" else "保存", fontSize = 14.sp)
                            }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            // 就餐时间
            TimeSection(
                dateTime = currentDateTime,
                onDateTimeChange = { currentDateTime = it }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 食物选择区域
            FoodSelectionSection(
                selectedFoods = selectedFoods,
                onAddFoodClick = { showFoodSearch = true },
                onFoodQuantityChange = { food, newQuantity ->
                    viewModel.updateFoodPortion(food, newQuantity)
                },
                onRemoveFood = { food ->
                    viewModel.removeFood(food)
                },
                onSelectComboClick = { showComboMenu = true }
            )
        }
        
        // 食物搜索对话框
        if (showFoodSearch) {
            FoodSearchDialog(
                viewModel = viewModel,
                onDismiss = { showFoodSearch = false },
                onFoodSelected = { food ->
                    viewModel.addFood(food)
                    showFoodSearch = false
                }
            )
        }
        
        // 保存组合对话框
        if (showSaveComboDialog) {
            SaveComboDialog(
                comboName = comboName,
                comboDescription = comboDescription,
                onComboNameChange = { viewModel.updateComboName(it) },
                onComboDescriptionChange = { viewModel.updateComboDescription(it) },
                onSave = { viewModel.saveAsCombo() },
                onCancel = { viewModel.hideSaveComboDialog() }
            )
        }
        
        // 组合选择对话框
        if (showComboMenu) {
            ComboSelectionDialog(
                foodCombos = foodCombos,
                onComboSelected = { comboId ->
                    viewModel.applyCombo(comboId)
                    showComboMenu = false
                },
                onDismiss = { showComboMenu = false }
            )
        }
    }
}

@Composable
fun TimeSection(
    dateTime: String,
    onDateTimeChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "就餐时间",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // 合并的日期时间输入
            OutlinedTextField(
                value = dateTime,
                onValueChange = onDateTimeChange,
                placeholder = { Text("例如：2024-01-01 12:30") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SaveComboDialog(
    comboName: String,
    comboDescription: String,
    onComboNameChange: (String) -> Unit,
    onComboDescriptionChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("保存为常用组合") },
        text = {
            Column {
                OutlinedTextField(
                    value = comboName,
                    onValueChange = onComboNameChange,
                    label = { Text("组合名称（可选）") },
                    placeholder = { Text("留空将使用前5个食物名称") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = comboDescription,
                    onValueChange = onComboDescriptionChange,
                    label = { Text("组合描述（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(onClick = onSave) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("取消")
            }
        }
    )
}

@Composable
fun ComboSelectionDialog(
    foodCombos: List<FoodCombo>,
    onComboSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择常用组合") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                if (foodCombos.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("暂无可用组合")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(foodCombos) { combo ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { onComboSelected(combo.id) },
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = combo.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (combo.description.isNotBlank()) {
                                        Text(
                                            text = combo.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun FoodSelectionSection(
    selectedFoods: List<Pair<FoodItem, Double>>,
    onAddFoodClick: () -> Unit,
    onFoodQuantityChange: (FoodItem, Double) -> Unit,
    onRemoveFood: (FoodItem) -> Unit,
    onSelectComboClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "食物列表",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 可选组合按钮
                    TextButton(
                        onClick = onSelectComboClick,
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("可选组合")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // 添加食物按钮
                    TextButton(
                        onClick = onAddFoodClick,
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("添加食物")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (selectedFoods.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "点击'添加食物'按钮开始记录您的饮食",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(selectedFoods, key = { it.first.id }) { (food, quantity) ->
                        FoodItemCard(
                            food = food,
                            quantity = quantity,
                            onQuantityChange = { newQuantity ->
                                onFoodQuantityChange(food, newQuantity)
                            },
                            onRemove = { onRemoveFood(food) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FoodItemCard(
    food: FoodItem,
    quantity: Double,
    onQuantityChange: (Double) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧：食物信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 食物名称
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // 热量信息
                Text(
                    text = "热量: ${String.format("%.0f", food.calories * quantity / 100)} kcal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 中间：克重填写框（靠近食品）
            OutlinedTextField(
                value = String.format("%.0f", quantity),
                onValueChange = { newValue ->
                    try {
                        val newQuantity = newValue.toDouble()
                        if (newQuantity >= 0.0) {
                            onQuantityChange(newQuantity)
                        }
                    } catch (e: NumberFormatException) {
                        // 忽略非数字输入
                    }
                },
                label = { Text("克") },
                textStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .width(70.dp)
                    .padding(end = 8.dp)
            )
            
            // 右侧：删除按钮（最右边）
            TextButton(
                onClick = onRemove,
                modifier = Modifier.height(32.dp)
            ) {
                Text("删除", fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodSearchDialog(
    viewModel: AddRecordViewModel,
    onDismiss: () -> Unit,
    onFoodSelected: (FoodItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<FoodItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    
    // 根据搜索查询获取结果
    LaunchedEffect(searchQuery) {
        isLoading = true
        try {
            if (searchQuery.isNotEmpty()) {
                // 使用搜索功能
                searchResults = viewModel.searchFoods(searchQuery)
            } else {
                // 空查询时显示最近使用的食物或空列表
                searchResults = viewModel.getRecentlyUsedFoods()
            }
        } catch (e: Exception) {
            // 处理错误
            searchResults = emptyList()
        } finally {
            isLoading = false
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("搜索食物") },
        text = {
            Column {
                // 搜索框
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("搜索食物名称或分类") },
                    placeholder = { Text("例如：苹果、蔬菜") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "搜索"
                        )
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 搜索结果列表
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("加载中...")
                    }
                } else if (searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (searchQuery.isNotEmpty()) "未找到相关食物" else "暂无食物数据",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(searchResults) { food ->
                            FoodSearchResultItem(
                                food = food,
                                onClick = { onFoodSelected(food) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun FoodSearchResultItem(
    food: FoodItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )
            }
            
            Text(
                text = "${food.calories} kcal/100g",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}