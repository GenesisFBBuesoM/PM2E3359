package com.miempresa.pm2e3359.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.miempresa.pm2e3359.R
import com.miempresa.pm2e3359.database.Repositorio
import com.miempresa.pm2e3359.models.Tecnico
import com.miempresa.pm2e3359.utils.DateUtils
import com.miempresa.pm2e3359.utils.UiUtils
import java.util.*

class AsignarHerramientaActivity : AppCompatActivity() {

    private lateinit var spinnerTecnicos: Spinner
    private lateinit var btnFechaInicio: Button
    private lateinit var btnFechaFin: Button
    private lateinit var btnAsignar: Button
    private lateinit var txtToolTitle: TextView
    private lateinit var txtToolNameDisplay: TextView
    private lateinit var btnVolver: LinearLayout
    private lateinit var btnCancelar: Button

    private var toolId: Int = -1
    private var toolNombre: String = ""
    private var tecnicos = mutableListOf<Tecnico>()
    
    private var fechaInicioStr: String = ""
    private var fechaFinStr: String = ""

    private val calendar = Calendar.getInstance()
    private lateinit var repo: Repositorio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asignar_herramienta)

        repo = Repositorio(this)
        toolId = intent.getIntExtra("TOOL_ID", -1)
        toolNombre = intent.getStringExtra("TOOL_NOMBRE") ?: "Herramienta"

        txtToolTitle = findViewById(R.id.txtToolTitle)
        txtToolNameDisplay = findViewById(R.id.txtToolNameDisplay)
        txtToolNameDisplay.text = toolNombre

        spinnerTecnicos = findViewById(R.id.spinnerTecnicos)
        btnFechaInicio = findViewById(R.id.btnFechaInicio)
        btnFechaFin = findViewById(R.id.btnFechaFin)
        btnAsignar = findViewById(R.id.btnAsignar)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnVolver = findViewById(R.id.btnVolver)
        
        btnVolver.setOnClickListener { finish() }
        btnCancelar.setOnClickListener { finish() }

        // Inicializar con la fecha actual
        val hoy = com.miempresa.pm2e3359.utils.DateUtils.getCurrentDate()
        fechaInicioStr = hoy
        fechaFinStr = hoy
        btnFechaInicio.text = hoy
        btnFechaFin.text = hoy

        cargarTecnicos()

        btnFechaInicio.setOnClickListener { showDateTimePicker { itSelected ->
            fechaInicioStr = itSelected
            btnFechaInicio.text = itSelected
        } }

        btnFechaFin.setOnClickListener { showDateTimePicker { itSelected ->
            fechaFinStr = itSelected
            btnFechaFin.text = itSelected
        } }

        btnAsignar.setOnClickListener { confirmarAsignacion() }
    }

    private fun cargarTecnicos() {
        tecnicos.clear()
        tecnicos.addAll(repo.getTecnicos())
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tecnicos)
        spinnerTecnicos.adapter = adapter
    }

    private fun showDateTimePicker(onSelected: (String) -> Unit) {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            
            TimePickerDialog(this, { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                onSelected(DateUtils.formatDate(calendar.time))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

        DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun confirmarAsignacion() {
        val tecnico = spinnerTecnicos.selectedItem as? Tecnico
        if (tecnico == null) {
            UiUtils.showToast(this, "Seleccione un técnico")
            return
        }

        if (fechaInicioStr.isEmpty() || fechaFinStr.isEmpty()) {
            UiUtils.showToast(this, "Seleccione las fechas")
            return
        }

        val dateInicio = DateUtils.parseDate(fechaInicioStr)
        val dateFin = DateUtils.parseDate(fechaFinStr)
        if (dateFin != null && dateInicio != null && dateFin.before(dateInicio)) {
            UiUtils.showToast(this, "La fecha de fin no puede ser anterior a la de inicio")
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Confirmar Asignación")
            .setMessage("¿Confirmar asignación de $toolNombre a ${tecnico.nombre} del $fechaInicioStr al $fechaFinStr?")
            .setPositiveButton("Confirmar") { _, _ ->
                val success = repo.asignarHerramienta(toolId, tecnico.id, fechaInicioStr, fechaFinStr)
                if (success) {
                    UiUtils.showToast(this, "Asignación exitosa")
                    finish()
                } else {
                    UiUtils.showToast(this, "Error en la asignación")
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
