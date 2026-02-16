package com.miempresa.pm2e3359.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.miempresa.pm2e3359.R
import com.miempresa.pm2e3359.database.DatabaseHelper
import com.miempresa.pm2e3359.database.Repositorio

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar DB
        val repo = Repositorio(this)
        repo.getTecnicos() // Esto gatilla el auto-seed si la lista esta vacia

        val btnIngresar: android.widget.Button = findViewById(R.id.btnIngresar)
        btnIngresar.setOnClickListener {
            // Navegar al Alta de Herramienta
            startActivity(Intent(this, AltaHerramientaActivity::class.java))
        }
    }
}
