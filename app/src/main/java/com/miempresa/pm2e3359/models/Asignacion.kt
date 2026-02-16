package com.miempresa.pm2e3359.models

data class Asignacion(
    val id: Int,
    val herramientaId: Int,
    val tecnicoId: Int,
    val fechaInicio: String,
    val fechaFin: String,
    val fechaDevolucion: String? = null,
    val notasEntrega: String? = null,
    val fotoEntregaUri: String? = null,
    val fotoDevolucionUri: String? = null
)
