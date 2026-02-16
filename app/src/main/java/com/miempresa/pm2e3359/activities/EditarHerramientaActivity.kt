package com.miempresa.pm2e3359.activities

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.miempresa.pm2e3359.R
import com.miempresa.pm2e3359.database.Repositorio
import com.miempresa.pm2e3359.utils.UiUtils
import com.miempresa.pm2e3359.utils.Validators
import java.io.File

class EditarHerramientaActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etEspecificaciones: EditText
    private lateinit var imgFoto: ImageView
    private lateinit var btnCamara: Button
    private lateinit var btnGaleria: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnVolver: LinearLayout

    private var toolId: Int = -1
    private var fotoUri: String? = null
    private var tempImageUri: Uri? = null
    private lateinit var repo: Repositorio

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imgFoto.setImageURI(it)
            fotoUri = it.toString()
            imgFoto.alpha = 1.0f
        }
    }

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            imgFoto.setImageURI(tempImageUri)
            fotoUri = tempImageUri.toString()
            imgFoto.alpha = 1.0f
        }
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            prepararCamara()
        } else {
            UiUtils.showToast(this, "Permiso de cámara denegado")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_herramienta)

        repo = Repositorio(this)
        toolId = intent.getIntExtra("TOOL_ID", -1)

        if (toolId == -1) {
            UiUtils.showToast(this, "Error: No se encontró la herramienta")
            finish()
            return
        }

        etNombre = findViewById(R.id.etNombre)
        etDescripcion = findViewById(R.id.etDescripcion)
        etEspecificaciones = findViewById(R.id.etEspecificaciones)
        imgFoto = findViewById(R.id.imgFoto)
        btnCamara = findViewById(R.id.btnCamara)
        btnGaleria = findViewById(R.id.btnGaleria)
        btnActualizar = findViewById(R.id.btnActualizar)
        btnVolver = findViewById(R.id.btnVolver)

        cargarDatos()

        btnVolver.setOnClickListener { finish() }
        btnCamara.setOnClickListener { 
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                prepararCamara()
            } else {
                requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
        btnGaleria.setOnClickListener { pickImageLauncher.launch("image/*") }
        btnActualizar.setOnClickListener { actualizarHerramienta() }
    }

    private fun cargarDatos() {
        val tool = repo.getAllHerramientas().find { it.id == toolId }
        tool?.let {
            etNombre.setText(it.nombre)
            etDescripcion.setText(it.descripcion)
            etEspecificaciones.setText(it.especificaciones)
            fotoUri = it.foto_uri
            if (fotoUri != null) {
                imgFoto.setImageURI(Uri.parse(fotoUri))
                imgFoto.alpha = 1.0f
            }
        }
    }

    private fun prepararCamara() {
        val directory = externalCacheDir ?: cacheDir
        val tempFile = File.createTempFile("edit_tool_image_", ".jpg", directory).apply {
            createNewFile()
            deleteOnExit()
        }
        val uri = FileProvider.getUriForFile(this, "com.miempresa.pm2e3359.fileprovider", tempFile)
        tempImageUri = uri
        takePhotoLauncher.launch(uri)
    }

    private fun actualizarHerramienta() {
        val nombre = etNombre.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()
        val especificaciones = etEspecificaciones.text.toString().trim()

        if (!Validators.isRequired(nombre) || !Validators.isRequired(descripcion) || !Validators.isRequired(especificaciones)) {
            UiUtils.showAlert(this, "Campos obligatorios", "Debe completar todos los campos.")
            return
        }

        val success = repo.updateHerramienta(toolId, nombre, descripcion, especificaciones, fotoUri)
        if (success) {
            UiUtils.showToast(this, "Herramienta actualizada")
            finish()
        } else {
            UiUtils.showToast(this, "Error al actualizar")
        }
    }
}
