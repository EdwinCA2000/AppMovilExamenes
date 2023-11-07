package com.example.examenesseq.fragments.adminExamen

data class AdminExamenesData (
    val IdExamen: Int,
    val TituloExamen: String,
    val DescripcionExamen: String,
    val Activo: Int,
    val FechaCreacion: String,
    val FechaInicio: String,
    val FechaFinal: String,
    val TiempoExamen: Int,
    val TextoBienvenida: String
)