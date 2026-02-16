package com.miempresa.pm2e3359.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun getCurrentDate(): String = sdf.format(Date())

    fun parseDate(dateStr: String?): Date? {
        return try {
            if (dateStr == null) null else sdf.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }

    fun formatDate(date: Date): String = sdf.format(date)
    
    fun getHoursDifference(date1: Date, date2: Date): Long {
        val diff = date1.time - date2.time
        return diff / (1000 * 60 * 60)
    }
}
