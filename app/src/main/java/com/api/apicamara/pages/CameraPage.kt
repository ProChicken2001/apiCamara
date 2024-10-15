package com.api.apicamara.pages

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.camera.core.Preview as CameraCorePreview
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.api.apicamara.R
import com.api.apicamara.routes.Routes
import com.api.apicamara.ui.theme.ApiCamaraTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

@Composable
fun CameraPage(
    navController: NavHostController
){

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    Scaffold(
        topBar = { CameraTopBar(navController) }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                )
                .fillMaxSize(),
        ) {
            CameraBody(context, lifecycleOwner)

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
fun CameraTopBar(
    navController: NavHostController
){

    TopAppBar(
        title = {
            Text(
                text = "Visualizador",
                fontSize = 30.sp,
                fontWeight = FontWeight.W400
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.navigate(Routes.MainPage.route)
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "return home"
                )
            }
        }
    )

}

//-------------------------------------------------------[BODY]

@Composable
fun CameraBody(
    context: Context,
    lifecycleOwner: LifecycleOwner
){
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isTaken by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        if(imageUri == null){
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize(0.85f)
            )

            LaunchedEffect(cameraProviderFuture) {
                val cameraProvider = cameraProviderFuture.get()

                val preview = CameraCorePreview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()

                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "No se pudo vincular la camara",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        takePicture(
                            context,
                            imageCapture,
                            onImageCaptured = {
                                    uri ->
                                imageUri = uri
                                isTaken = !isTaken
                            },
                            onError = {
                                Toast.makeText(
                                    context,
                                    "Error al tomar la foto",
                                    Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                ) {
                    Text("Tomar foto")
                }
            }
        }else{
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(25.dp)
                ) {
                    Button(
                        onClick = {
                            savePicture(context, imageUri)
                        }
                    ) {
                        Text("Guardar foto")
                    }

                    Button(
                        onClick = {
                            imageUri = null
                        }
                    ) {
                        Text("Volver a tomar foto")
                    }
                }
            }
        }


    }
}
//-------------------------------------------------------[FUNCIONES]


//-------------------------[TOMAR FOTO]
fun takePicture(
    context: Context,
    imageCapture: ImageCapture,
    onImageCaptured: (Uri) -> Unit,
    onError: (Exception) -> Unit
){
    val photoFile = File(
        getOutputDir(context),
        SimpleDateFormat(
            FILENAME_FORMAT, Locale.US
        ).format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback{
            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                onImageCaptured(savedUri)
                Toast.makeText(context, "Foto tomada: $savedUri", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

//-------------------------[OBTENER DIRECTORIO]

fun getOutputDir(context: Context): File {
    val mediaDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.let {
        File(it, context.getString(R.string.app_name)).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
}

//-------------------------[GUARDAR FOTO]

fun savePicture(
    context: Context,
    imageUri: Uri?
){
    imageUri?.let {

    } ?: run {
        Toast.makeText(
            context,
            "Hemos tenido un problema al tratar de cargar la foto",
            Toast.LENGTH_SHORT
        ).show()
    }
}