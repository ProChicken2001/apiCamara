package com.api.apicamara.pages

import android.Manifest
import android.graphics.drawable.Icon
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.api.apicamara.R
import com.api.apicamara.ui.theme.ApiCamaraTheme

@Composable
fun MainPage(
    navController: NavHostController
){
    val context = LocalContext.current
    var permission by remember { mutableStateOf(false) }

    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissions -> permissions.entries.forEach { entry ->
            when(entry.key) {
                Manifest.permission.READ_MEDIA_IMAGES -> {
                    if(entry.value){
                        permission = true
                    }else{
                        permission = false

                        Toast.makeText(
                            context,
                            "Permiso denegado: no se puede tomar fotos",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                Manifest.permission.READ_MEDIA_VIDEO -> {
                    if(entry.value){
                        permission = true
                    }else{
                        permission = false

                        Toast.makeText(
                            context,
                            "Permiso denegado: no se puede grabar",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = { MainTopBar() }
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
            if(permission){
                Text("Permisos otorgados")
            }else{
                ElevatedButton(
                    modifier = Modifier.fillMaxWidth(0.75f),
                    onClick = {
                        requestPermissionsLauncher.launch(
                            arrayOf(
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.READ_MEDIA_VIDEO
                            )
                        )
                    },
                    colors = ButtonDefaults.elevatedButtonColors(
                        contentColor = Color.Black,
                        containerColor = colorResource(R.color.btnAccesoCamara)
                    )
                ) {
                    Text(
                        text ="Acceso a la camara",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.W500
                    )
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun MainPagePreview(){
    ApiCamaraTheme(dynamicColor = false) {
        MainPage(rememberNavController())
    }
}

//-------------------------------------------------------[TOP BAR]
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(){

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
                    imageVector = Icons.Filled.Close,
                    contentDescription = "close app"
                )
            }
        }
    )

}