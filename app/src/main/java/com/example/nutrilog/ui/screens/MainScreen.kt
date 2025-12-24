package com.example.nutrilog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.nutrilog.ui.components.TopBar
import com.example.nutrilog.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    Scaffold(
        topBar = { TopBar("NutriLog") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    navController.navigate("add_record") 
                }
            ) {
                Icon(Icons.Default.Add, "添加记录")
            }
        }
    ) { padding ->
        // 记录列表
        RecordListScreen(
            modifier = Modifier.padding(padding),
            viewModel = viewModel
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    // 预览组件
}