package com.example.examenesseq.model.examen

data class SeccionesExamen (
    val idSeccion: Int,
    val seccion: String,
    val Orden: Int,
    val ActivoExamen: Int,
    val IdExamen: Int,
    val Preguntas :List<PreguntasExamen>
)