package com.miempresa.pm2e3359.models

data class Tecnico(
    val id: Int,
    val nombre: String,
    val telefono: String?,
    val especialidad: String?
) {
    override fun toString(): String {
        return nombre
    }
}
