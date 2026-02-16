package com.miempresa.pm2e3359.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miempresa.pm2e3359.R
import com.miempresa.pm2e3359.adapters.HerramientaAdapter
import com.miempresa.pm2e3359.database.Repositorio
import com.miempresa.pm2e3359.models.Herramienta
import com.miempresa.pm2e3359.utils.DateUtils
import com.miempresa.pm2e3359.utils.UiUtils

class ListaHerramientasActivity : AppCompatActivity() {

    private lateinit var rvHerramientas: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var adapter: HerramientaAdapter
    private lateinit var btnVolver: android.widget.LinearLayout
    private var toolList = mutableListOf<Herramienta>()
    private lateinit var repo: Repositorio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_herramientas)

        repo = Repositorio(this)
        rvHerramientas = findViewById(R.id.recyclerHerramientas)
        searchView = findViewById(R.id.searchView)
        btnVolver = findViewById(R.id.btnVolver)
        
        btnVolver.setOnClickListener { finish() }

        rvHerramientas.layoutManager = LinearLayoutManager(this)
        adapter = HerramientaAdapter(toolList) { tool ->
            mostrarOpciones(tool)
        }
        rvHerramientas.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filtrar(newText)
                return true
            }
        })

        cargarDatos()
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    private fun cargarDatos() {
        toolList.clear()
        toolList.addAll(repo.getAllHerramientas())
        adapter.updateList(toolList)
    }

    private fun filtrar(text: String?) {
        if (text.isNullOrEmpty()) {
            adapter.updateList(toolList)
            return
        }
        val query = text.lowercase()
        val filtered = toolList.filter {
            it.nombre.lowercase().contains(query) ||
            (it.tecnicoAsignado?.lowercase()?.contains(query) == true) ||
            it.especificaciones.lowercase().contains(query)
        }
        adapter.updateList(filtered)
    }

    private fun mostrarOpciones(tool: Herramienta) {
        val optionsList = mutableListOf<String>()
        
        if (tool.estado == "DISPONIBLE") {
            optionsList.add("Asignar a Técnico")
        } else {
            optionsList.add("Marcar Devolución")
            optionsList.add("Ver Resumen Asignación")
        }
        
        optionsList.add("Editar Herramienta")
        optionsList.add("Eliminar Herramienta")
        optionsList.add("Compartir Ficha")

        val options = optionsList.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle(tool.nombre)
            .setItems(options) { _, which ->
                when (options[which]) {
                    "Asignar a Técnico" -> abrirAsignacion(tool)
                    "Marcar Devolución" -> devolverHerramienta(tool)
                    "Ver Resumen Asignación" -> compartirResumen(tool)
                    "Editar Herramienta" -> abrirEdicion(tool)
                    "Eliminar Herramienta" -> confirmarEliminacion(tool)
                    "Compartir Ficha" -> compartirFicha(tool)
                }
            }
            .show()
    }

    private fun abrirEdicion(tool: Herramienta) {
        val intent = Intent(this, EditarHerramientaActivity::class.java).apply {
            putExtra("TOOL_ID", tool.id)
        }
        startActivity(intent)
    }

    private fun confirmarEliminacion(tool: Herramienta) {
        AlertDialog.Builder(this)
            .setTitle("¿Eliminar herramienta?")
            .setMessage("Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                if (repo.deleteHerramienta(tool.id)) {
                    UiUtils.showToast(this, "Herramienta eliminada")
                    cargarDatos()
                } else {
                    UiUtils.showToast(this, "Error al eliminar")
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun abrirAsignacion(tool: Herramienta) {
        val intent = Intent(this, AsignarHerramientaActivity::class.java).apply {
            putExtra("TOOL_ID", tool.id)
            putExtra("TOOL_NOMBRE", tool.nombre)
        }
        startActivity(intent)
    }

    private fun devolverHerramienta(tool: Herramienta) {
        val success = repo.devolverHerramienta(tool.id, DateUtils.getCurrentDate())
        if (success) {
            UiUtils.showToast(this, "Herramienta devuelta")
            cargarDatos()
        } else {
            UiUtils.showToast(this, "Error al devolver")
        }
    }

    private fun compartirFicha(tool: Herramienta) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Ficha Técnica: ${tool.nombre}")
            putExtra(Intent.EXTRA_TEXT, "Herramienta: ${tool.nombre}\nDescripción: ${tool.descripcion}\nEspecificaciones: ${tool.especificaciones}\nEstado: ${tool.estado}")
        }
        startActivity(Intent.createChooser(intent, "Compartir via"))
    }

    private fun compartirResumen(tool: Herramienta) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Resumen Asignación: ${tool.nombre}")
            putExtra(Intent.EXTRA_TEXT, "Herramienta: ${tool.nombre}\nTécnico: ${tool.tecnicoAsignado}\nFecha Entrega: ${tool.fechaFin}")
        }
        startActivity(Intent.createChooser(intent, "Compartir via"))
    }
}