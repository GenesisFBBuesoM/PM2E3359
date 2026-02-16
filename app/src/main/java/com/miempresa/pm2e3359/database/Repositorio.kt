package com.miempresa.pm2e3359.database

import android.content.ContentValues
import android.content.Context
import com.miempresa.pm2e3359.models.Herramienta
import com.miempresa.pm2e3359.models.Tecnico

class Repositorio(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertHerramienta(nombre: String, descripcion: String, especificaciones: String, fotoUri: String?): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("especificaciones", especificaciones)
            put("foto_uri", fotoUri)
            put("estado", "DISPONIBLE")
        }
        val id = db.insert("Herramientas", null, values)
        db.close()
        return id
    }

    fun getAllHerramientas(): List<Herramienta> {
        val tools = mutableListOf<Herramienta>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT h.*, t.nombre as tecnico_nombre, a.fecha_fin, a.fecha_devolucion 
            FROM Herramientas h
            LEFT JOIN Asignaciones a ON h.id = a.herramienta_id 
              AND a.id = (SELECT id FROM Asignaciones WHERE herramienta_id = h.id ORDER BY id DESC LIMIT 1)
            LEFT JOIN Tecnicos t ON a.tecnico_id = t.id
            ORDER BY 
              CASE WHEN h.estado = 'ASIGNADA' AND a.fecha_devolucion IS NULL THEN 0 ELSE 1 END,
              a.fecha_fin IS NULL ASC,
              a.fecha_fin ASC
        """.trimIndent()

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                tools.add(Herramienta(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    especificaciones = cursor.getString(cursor.getColumnIndexOrThrow("especificaciones")),
                    foto_uri = cursor.getString(cursor.getColumnIndexOrThrow("foto_uri")),
                    estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                    tecnicoAsignado = cursor.getString(cursor.getColumnIndexOrThrow("tecnico_nombre")),
                    fechaFin = cursor.getString(cursor.getColumnIndexOrThrow("fecha_fin")),
                    fechaDevolucion = cursor.getString(cursor.getColumnIndexOrThrow("fecha_devolucion"))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return tools
    }

    fun getTecnicos(): List<Tecnico> {
        val list = mutableListOf<Tecnico>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Tecnicos", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(Tecnico(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                    cursor.getString(cursor.getColumnIndexOrThrow("especialidad"))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        
        // Si no hay tecnos, insertamos unos por defecto (en caso de que el seed fallara)
        if (list.isEmpty()) {
            val dbWrite = dbHelper.writableDatabase
            dbWrite.execSQL("INSERT INTO Tecnicos (nombre, telefono, especialidad) VALUES ('Genesis Bueso', '9988-7766', 'Ingeniería')")
            dbWrite.execSQL("INSERT INTO Tecnicos (nombre, telefono, especialidad) VALUES ('Henry Martinez', '8877-6655', 'Mantenimiento')")
            dbWrite.execSQL("INSERT INTO Tecnicos (nombre, telefono, especialidad) VALUES ('Ana Gonzales', '7766-5544', 'Supervisora')")
            dbWrite.close()
            return getTecnicos() // Llamada recursiva para obtener los nuevos datos
        }
        
        db.close()
        return list
    }

    fun asignarHerramienta(toolId: Int, tecnicoId: Int, inicio: String, fin: String): Boolean {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        return try {
            val values = ContentValues().apply {
                put("herramienta_id", toolId)
                put("tecnico_id", tecnicoId)
                put("fecha_inicio", inicio)
                put("fecha_fin", fin)
            }
            db.insert("Asignaciones", null, values)

            val updateValues = ContentValues().apply {
                put("estado", "ASIGNADA")
            }
            db.update("Herramientas", updateValues, "id = ?", arrayOf(toolId.toString()))

            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            false
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun devolverHerramienta(toolId: Int, fechaDevolucion: String): Boolean {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        return try {
            val query = "SELECT id FROM Asignaciones WHERE herramienta_id = ? AND fecha_devolucion IS NULL ORDER BY id DESC LIMIT 1"
            val cursor = db.rawQuery(query, arrayOf(toolId.toString()))
            if (cursor.moveToFirst()) {
                val asignacionId = cursor.getInt(0)
                val values = ContentValues().apply {
                    put("fecha_devolucion", fechaDevolucion)
                }
                db.update("Asignaciones", values, "id = ?", arrayOf(asignacionId.toString()))

                val toolValues = ContentValues().apply {
                    put("estado", "DISPONIBLE")
                }
                db.update("Herramientas", toolValues, "id = ?", arrayOf(toolId.toString()))
                db.setTransactionSuccessful()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun deleteHerramienta(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("Herramientas", "id = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    fun updateHerramienta(id: Int, nombre: String, descripcion: String, especificaciones: String, fotoUri: String?): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("especificaciones", especificaciones)
            put("foto_uri", fotoUri)
        }
        val result = db.update("Herramientas", values, "id = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
}
