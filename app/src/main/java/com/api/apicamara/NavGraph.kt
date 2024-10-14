package com.api.apicamara

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.api.apicamara.pages.CameraPage
import com.api.apicamara.pages.MainPage
import com.api.apicamara.routes.Routes

@Composable
fun SetupNavGraph(
    navController: NavHostController
){

    NavHost(
        navController = navController,
        startDestination = Routes.MainPage.route
    ){
        composable(
            route = Routes.MainPage.route
        ){

            MainPage(navController)
        }
        composable(
            route = Routes.CameraPage.route
        ){
            CameraPage(navController)
        }
    }
}