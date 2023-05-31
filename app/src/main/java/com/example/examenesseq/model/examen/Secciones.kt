package com.example.examenesseq.model.examen

data class Secciones(
    val IdSeccion: Int,
    val TituloSeccion: String,
    val DescripcionSeccion: String,
    val Activo: Int,
    val IdExamen: Int,
    val Orden: Int
)
