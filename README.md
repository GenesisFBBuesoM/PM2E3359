# PM2ExamenHerramientas

Aplicación Android para la gestión de herramientas de mantenimiento y sus asignaciones a técnicos. Desarrollado como Examen de Primer Parcial.

## Integrantes del Grupo
- **Genesis Fabiana Bueso Maldonado** - Cuenta: 202130030033
- **Henry Luis Monge Martinez** - Cuenta: 201610010459

## Características principales
- **Alta de Herramientas**: Registro con nombre, descripción, especificaciones y fotografía.
- **Gestión de Asignaciones**: Control de qué técnico tiene cada herramienta y en qué plazos.
- **Buscador Inteligente**: Filtro por nombre, técnico o especificaciones técnicas.
- **Sistema de Colores (Rúbrica)**:
    - **Rojo**: Asignada y con fecha de entrega vencida.
    - **Ámbar**: Asignada y próxima a vencer (menos de 48 horas).
    - **Verde**: Herramienta devuelta exitosamente.
    - **Gris**: Herramienta disponible para nueva asignación.
- **Persistencia**: Uso de SQLite para almacenamiento local de datos.

## Detalles Técnicos
- **SDK Mínimo**: 24 (Android 7.0 Nougat)
- **SDK Objetivo**: 36
- **Lenguaje**: Kotlin
- **Arquitectura**: Repositorio con SQLiteOpenHelper.

## Instrucciones de Construcción y Pruebas
1. Clonar el repositorio.
2. Abrir en **Android Studio Ladybug** (o superior).
3. Sincronizar Gradle.
4. Ejecutar en un emulador o dispositivo físico.

---
**Desarrollado para la clase de Programación Móvil I - I Periodo 2026**
