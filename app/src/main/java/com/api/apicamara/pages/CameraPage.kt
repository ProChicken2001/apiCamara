package com.api.apicamara.pages

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
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
    var imageFile by remember { mutableStateOf<File?>(null) }
    var sharePicture by remember { mutableStateOf(false) }
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
                            onImageFileCapture = {
                                file ->
                                imageFile = file
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

                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth(0.80f)
                        .fillMaxHeight(0.80f),
                    model = imageUri,
                    contentDescription = "current picture"
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(25.dp)
                ) {
                    Button(
                        onClick = {
                            savePicture(
                                context,
                                imageUri,
                            )
                            sharePicture = true
                        }
                    ) {
                        Text("Guardar foto")
                    }

                    Button(
                        onClick = {
                            imageUri = null
                            sharePicture = false
                        }
                    ) {
                        Text("Volver a tomar foto")
                    }
                }

                Button(
                    onClick = {
                        if(sharePicture){
                            sharePicture(context, imageFile)
                        }else{
                            Toast.makeText(
                                context,
                                "Guarde la foto antes de compartir",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Text("Compartir Imagen")
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
    onImageFileCapture: (File) -> Unit,
    onImageCaptured: (Uri) -> Unit,
    onError: (Exception) -> Unit
){
    val photoFile = File(
        getOutputDir(context),
        SimpleDateFormat(
            FILENAME_FORMAT, Locale.US
        ).format(System.currentTimeMillis()) + ".jpg"
    )

    onImageFileCapture(photoFile)

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
                Log.d("Foto tomada:", "${savedUri}")
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
    imageUri: Uri?,
){
    imageUri?.let {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        )

        try{
            resolver.openOutputStream(uri!!).use {
                outputStream ->
                val inputStream = resolver.openInputStream(imageUri)
                inputStream?.copyTo(outputStream!!)
                inputStream?.close()
            }

            Toast.makeText(
                context,
                "Foto guardada en fotos",
                Toast.LENGTH_SHORT
            ).show()
        }catch (e: Exception){
            Toast.makeText(
                context,
                "Error al guardar la foto",
                Toast.LENGTH_SHORT
            ).show()
        }

    } ?: run {
        Toast.makeText(
            context,
            "Hemos tenido un problema al tratar de cargar la foto",
            Toast.LENGTH_SHORT
        ).show()
    }
}

//-------------------------[COMPARTIR FOTO]

fun sharePicture(
    context: Context,
    imageFile: File?
){
    try {
        imageFile?.let {
            val imageUri: Uri = FileProvider.getUriForFile(
                context,
                "com.api.apicamara.fileprovider",
                imageFile
            )

            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, imageUri)
                type = "image/jpeg"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Compartir Imagen")
            context.startActivity(chooser)
        } ?: run{
            Toast.makeText(context, "No hay imagen para compartir", Toast.LENGTH_SHORT).show()
        }
    }catch (e : Exception){
        Log.e("ERROR IMAGE ->", e.toString())
        Toast.makeText(context, "Error al tratar de compartir", Toast.LENGTH_SHORT).show()
    }
}