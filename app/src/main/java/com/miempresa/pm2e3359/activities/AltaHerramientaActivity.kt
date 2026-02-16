package com.miempresa.pm2e3359.activities

import android.view.View
import android.content.Intent
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

class AltaHerramientaActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etEspecificaciones: EditText
    private lateinit var imgFoto: ImageView
    private lateinit var btnCamara: Button
    private lateinit var btnGaleria: Button
    private lateinit var btnGuardar: Button
    private lateinit var btnVerLista: Button

    private var fotoUri: String? = null
    private var tempImageUri: Uri? = null
    private lateinit var repo: Repositorio

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imgFoto.setImageURI(it)
            fotoUri = it.toString()
        }
    }

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            imgFoto.setImageURI(tempImageUri)
            fotoUri = tempImageUri.toString()
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
        setContentView(R.layout.activity_alta_herramienta)

        repo = Repositorio(this)

        etNombre = findViewById(R.id.etNombre)
        etDescripcion = findViewById(R.id.etDescripcion)
        etEspecificaciones = findViewById(R.id.etEspecificaciones)
        imgFoto = findViewById(R.id.imgFoto)
        btnCamara = findViewById(R.id.btnCamara)
        btnGaleria = findViewById(R.id.btnGaleria)
        btnGuardar = findViewById(R.id.btnGuardar)
        val btnVerListaHeader = findViewById<View>(R.id.btnVerListaHeader)

        btnCamara.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    prepararCamara()
                } else {
                    requestCameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            } else {
                prepararCamara()
            }
        }
        btnGaleria.setOnClickListener { pickImageLauncher.launch("image/*") }
        btnGuardar.setOnClickListener { guardarHerramienta() }
        btnVerListaHeader.setOnClickListener {
            startActivity(Intent(this, ListaHerramientasActivity::class.java))
        }
    }

    private fun prepararCamara() {
        val directory = externalCacheDir ?: cacheDir
        val tempFile = File.createTempFile("tool_image_", ".jpg", directory).apply {
            createNewFile()
            deleteOnExit()
        }
        val uri = FileProvider.getUriForFile(this, "com.miempresa.pm2e3359.fileprovider", tempFile)
        tempImageUri = uri
        takePhotoLauncher.launch(uri)
    }

    private fun guardarHerramienta() {
        val nombre = etNombre.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()
        val especificaciones = etEspecificaciones.text.toString().trim()

        if (!Validators.isRequired(nombre) || !Validators.isRequired(descripcion) || !Validators.isRequired(especificaciones)) {
            UiUtils.showAlert(this, "Campos obligatorios", "Debe completar todos los campos obligatorios.")
            return
        }

        if (nombre.length < 3) {
            etNombre.error = "Mínimo 3 caracteres"
            return
        }

        if (!Validators.isValidName(nombre)) {
            etNombre.error = "Solo letras, números y espacios"
            return
        }

        val resultado = repo.insertHerramienta(nombre, descripcion, especificaciones, fotoUri)

        if (resultado != -1L) {
            UiUtils.showToast(this, "Herramienta guardada correctamente")
            limpiarCampos()
        } else {
            UiUtils.showToast(this, "Error al guardar")
        }
    }

    private fun limpiarCampos() {
        etNombre.text.clear()
        etDescripcion.text.clear()
        etEspecificaciones.text.clear()
        imgFoto.setImageDrawable(null)
        fotoUri = null
    }
}

