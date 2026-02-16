package com.miempresa.pm2e3359.utils

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

object UiUtils {
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showAlert(context: Context, title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
