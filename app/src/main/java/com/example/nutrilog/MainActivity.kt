package com.example.nutrilog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nutrilog.ui.navigation.BottomNavigationBar
import com.example.nutrilog.ui.screens.AnalysisScreen
import com.example.nutrilog.ui.screens.HomeScreen
import com.example.nutrilog.ui.screens.ProfileScreen
import com.example.nutrilog.ui.screens.ReportsScreen
import com.example.nutrilog.ui.theme.NutriLogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriLogTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) {
            innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(navController)
            }
            composable("analysis") {
                AnalysisScreen(navController)
            }
            composable("reports") {
                ReportsScreen(navController)
            }
            composable("profile") {
                ProfileScreen(navController)
            }
        }
    }
}
