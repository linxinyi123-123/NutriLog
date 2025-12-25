package com.example.nutrilog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nutrilog.data.entities.FoodCategory
import com.example.nutrilog.data.entities.FoodItem
import com.example.nutrilog.ui.viewmodels.MainViewModel
import com.example.nutrilog.utils.SearchUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodListScreen(
    navController: NavController,
    viewModel: MainViewModel,
    category: String
) {
    var foodList by remember { mutableStateOf<List<FoodItem>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var filteredFoods by remember { mutableStateOf<List<FoodItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    
    // 获取分类对应的枚举
    val foodCategory = remember(category) {
        try {
            FoodCategory.valueOf(category)
        } catch (e: IllegalArgumentException) {
            FoodCategory.OTHERS
        }
    }
    
    // 加载食物列表
    LaunchedEffect(foodCategory) {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val foods = viewModel.getFoodsByCategory(foodCategory)
                foodList = foods
                filteredFoods = foods
            } catch (e: Exception) {
                errorMessage = "加载食物列表失败: ${e.message}"
                foodList = emptyList()
                filteredFoods = emptyList()
            } finally {
                isLoading = false
            }
        }
    }
    
    // 搜索过滤 - 使用高级搜索功能
    LaunchedEffect(searchQuery, foodList) {
        filteredFoods = SearchUtils.advancedSearch(foodList, searchQuery)
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text("${foodCategory.displayName}类食物") 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // 搜索框
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("在${foodCategory.displayName}中搜索...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索"
                    )
                },
                singleLine = true
            )
            
            // 错误信息显示
            errorMessage?.let { message ->
                Text(
                    text = message,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            // 食物数量统计
            Text(
                text = if (isLoading) "正在加载..." else "共找到 ${filteredFoods.size} 种食物",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 食物列表
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredFoods.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isEmpty()) "该分类下暂无食物" else "未找到相关食物",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    items(filteredFoods) { food ->
                        FoodItemCard(
                            food = food,
                            onClick = {
                                // 点击食物后的操作
                                println("选中食物: ${food.name}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FoodListScreenPreview(
    navController: NavController,
    viewModel: MainViewModel,
    category: String
) {
    // 使用与FoodSearchScreen相同的FoodItemCard组件
    FoodItemCard(
        food = FoodItem(
            id = 1,
            name = "示例食物",
            englishName = "Sample Food",
            category = FoodCategory.GRAINS,
            calories = 100.0,
            protein = 10.0,
            carbs = 20.0,
            fat = 5.0
        ),
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun FoodListScreenPreview() {
    // 预览组件
}