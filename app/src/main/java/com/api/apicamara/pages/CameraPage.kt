package com.api.apicamara.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.api.apicamara.ui.theme.ApiCamaraTheme

@Composable
fun CameraPage(
    navController: NavHostController
){

    Scaffold(
        topBar = { CameraTopBar() }
    ) {
        paddingValues ->

        Column(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                    start = 12.dp,
                    end = 12.dp
                )
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }
    }

}

@Preview(showBackground = true)
@Composable
fun CameraPagePreview(){

    ApiCamaraTheme(
        dynamicColor = false
    ){
        CameraPage(rememberNavController())
    }

}

//-------------------------------------------------------[TOP BAR]

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraTopBar(){

    TopAppBar(
        title = {
            Text(
                text = "Api Camara",
                fontSize = 30.sp,
                fontWeight = FontWeight.W400
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "return home"
                )
            }
        }
    )

}

//-------------------------------------------------------[TOMAR FOTO]

@Composable
fun CameraTakePicture(){

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

    }

}