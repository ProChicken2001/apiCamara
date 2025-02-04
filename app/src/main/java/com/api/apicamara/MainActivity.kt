package com.api.apicamara

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.api.apicamara.ui.theme.ApiCamaraTheme

class MainActivity : ComponentActivity() {

    lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApiCamaraTheme(
                dynamicColor = false
            ) {
                navController = rememberNavController()
                SetupNavGraph(navController)
            }
        }
    }
}
