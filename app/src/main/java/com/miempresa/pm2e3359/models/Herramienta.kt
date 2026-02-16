package com.miempresa.pm2e3359.models

data class Herramienta(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val especificaciones: String,
    val foto_uri: String?,
    var estado: String,
    var tecnicoAsignado: String? = null,
    var fechaFin: String? = null,
    var fechaDevolucion: String? = null
)
