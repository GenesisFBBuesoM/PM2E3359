package com.miempresa.pm2e3359.utils

object Validators {
    fun isValidName(name: String): Boolean {
        return name.length >= 3 && name.matches(Regex("^[a-zA-Z0-9\\s]+$"))
    }

    fun isRequired(text: String): Boolean {
        return text.isNotBlank()
    }
}
