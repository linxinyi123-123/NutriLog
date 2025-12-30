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
    RecordListScreen(
        navController = navController,
        viewModel = viewModel
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    // 预览组件
}