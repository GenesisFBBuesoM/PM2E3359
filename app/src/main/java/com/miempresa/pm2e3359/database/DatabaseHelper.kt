package com.miempresa.pm2e3359.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "herramientas.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {

        // TABLA HERRAMIENTAS
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Herramientas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                descripcion TEXT NOT NULL,
                especificaciones TEXT NOT NULL,
                foto_uri TEXT,
                estado TEXT NOT NULL DEFAULT 'DISPONIBLE'
            );
        """.trimIndent())

        // tabla tecnicos
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Tecnicos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                telefono TEXT,
                especialidad TEXT
            );
        """.trimIndent())

        // TABLA ASIGNACIONES
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS Asignaciones (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                herramienta_id INTEGER NOT NULL,
                tecnico_id INTEGER NOT NULL,
                fecha_inicio TEXT NOT NULL,
                fecha_fin TEXT NOT NULL,
                fecha_devolucion TEXT,
                notas_entrega TEXT,
                foto_entrega_uri TEXT,
                foto_devolucion_uri TEXT,
                FOREIGN KEY(herramienta_id) REFERENCES Herramientas(id),
                FOREIGN KEY(tecnico_id) REFERENCES Tecnicos(id)
            );
        """.trimIndent())

        // Seed Technicians
        db.execSQL("INSERT INTO Tecnicos (nombre, telefono, especialidad) VALUES ('Genesis Bueso', '9988-7766', 'Ingeniería')")
        db.execSQL("INSERT INTO Tecnicos (nombre, telefono, especialidad) VALUES ('Henry Martinez', '8877-6655', 'Mantenimiento')")
        db.execSQL("INSERT INTO Tecnicos (nombre, telefono, especialidad) VALUES ('Ana Gonzales', '7766-5544', 'Supervisora')")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Asignaciones")
        db.execSQL("DROP TABLE IF EXISTS Herramientas")
        db.execSQL("DROP TABLE IF EXISTS Tecnicos")
        onCreate(db)
    }
}
