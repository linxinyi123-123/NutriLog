package com.example.nutrilog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
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
import com.example.nutrilog.ui.components.TopBar
import com.example.nutrilog.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodCategoryScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    Scaffold(
        topBar = { 
            TopBar(
                title = "食物分类",
                onSettingsClick = { navController.navigate("food_search") }
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
                value = "",
                onValueChange = { /* 暂时留空，后续实现搜索功能 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("搜索食物...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索"
                    )
                },
                singleLine = true
            )
            
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