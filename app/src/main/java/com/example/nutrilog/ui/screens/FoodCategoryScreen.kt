package com.example.nutrilog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nutrilog.data.entities.FoodCategory
import com.example.nutrilog.ui.viewmodels.MainViewModel
import com.example.nutrilog.utils.SearchUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodCategoryScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var showSearchSuggestions by remember { mutableStateOf(false) }
    var searchSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    
    // 监听搜索查询变化，生成搜索建议
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            // 使用搜索功能获取相关食物数据用于生成搜索建议
            val searchResults = viewModel.searchFoods(searchQuery)
            searchSuggestions = SearchUtils.generateSearchSuggestions(searchResults, searchQuery)
            showSearchSuggestions = searchSuggestions.isNotEmpty()
        } else {
            showSearchSuggestions = false
            searchSuggestions = emptyList()
        }
    }
    
    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("食物分类") }
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
                placeholder = { Text("搜索食物（支持中文、拼音、英文）...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { 
                            searchQuery = ""
                            showSearchSuggestions = false
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "清除"
                            )
                        }
                    }
                },
                singleLine = true
            )
            
            // 搜索建议
            if (showSearchSuggestions) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    searchSuggestions.forEach { suggestion ->
                        Text(
                            text = suggestion,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    searchQuery = suggestion
                                    navController.navigate("food_search?query=$suggestion")
                                },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // 搜索按钮
            if (searchQuery.isNotEmpty()) {
                Button(
                    onClick = {
                        navController.navigate("food_search?query=$searchQuery")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("搜索")
                }
            }
            
            // 分类网格
            CategoryGrid(
                navController = navController,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CategoryGrid(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val categories = remember {
        FoodCategory.values().toList()
    }
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                onClick = {
                    navController.navigate("food_list/${category.name}")
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCard(
    category: FoodCategory,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(android.graphics.Color.parseColor(category.color))
        )
    ) {
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
                    text = category.displayName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${category.displayName}类食物",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FoodCategoryScreenPreview() {
    // 预览组件
}